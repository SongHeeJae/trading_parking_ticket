package com.kuke.parkingticket.service.ticket;

import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.entity.Image;
import com.kuke.parkingticket.entity.Ticket;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.image.ImageDto;
import com.kuke.parkingticket.model.dto.ticket.*;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final FileService fileService;

    public Page<TicketSimpleDto> findAllTickets(TicketSearchConditionDto conditionDto, Pageable pageable) {
        return ticketRepository.findAllTicketWithConditions(conditionDto, pageable);
    }

    @Transactional
    public TicketDto createTicket(TicketCreateRequestDto requestDto) {
        User user = userRepository.findUser(requestDto.getUserId()).orElseThrow(UserNotFoundException::new);
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        Ticket ticket = Ticket.createTicket(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getAddress(),
                requestDto.getPrice(),
                user,
                town,
                requestDto.getPlaceType(),
                requestDto.getTermType(),
                requestDto.getTicketStatus(),
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime()
        );
        for (int i=0; i<requestDto.getFiles().size(); i++) {
            ticket.addImage(Image.createImage(
                    fileService.upload(requestDto.getFiles().get(i), generateImageName(requestDto.getFiles().get(i), i, user.getId())),
                    fileService.getBaseUrl(),
                    ticket));
        }
        ticketRepository.save(ticket);
        return TicketDto.convertTicketToDto(ticket);
    }

    private String generateImageName(MultipartFile file, int idx, Long id) {
        String originalFileName = file.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        return id + "_" + LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() + "_" + idx + ext;
    }

    @Transactional
    public void updateTicket(Long ticketId, TicketUpdateRequestDto requestDto) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
        List<String> savedImageNames = ticket.getImages().stream().map(i -> i.getPath()).collect(Collectors.toList()); // 기존에 저장된 이미지명
        List<String> updateImageNames = requestDto.getFiles().stream().map(f -> f.getOriginalFilename()).collect(Collectors.toList()); // 가져온 이미지명

        List<MultipartFile> newImages = requestDto.getFiles().stream().filter(f -> !savedImageNames.contains(f.getOriginalFilename())).collect(Collectors.toList());
        List<Image> deleteImages = ticket.getImages().stream().filter(i -> !updateImageNames.contains(i.getPath())).collect(Collectors.toList()); // 삭제될 이미지

        for (int i=0; i<newImages.size(); i++) {
            ticket.addImage(Image.createImage(
                    fileService.upload(newImages.get(i), generateImageName(newImages.get(i), i, ticket.getWriter().getId())),
                    fileService.getBaseUrl(),
                    ticket));
        }

        deleteImages.stream().forEach(i -> {
            fileService.delete(i.getPath());
            ticket.getImages().remove(i);
        });

        ticket.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getPrice(), requestDto.getAddress()
                ,requestDto.getStartDateTime(), requestDto.getEndDateTime(), requestDto.getTermType(), requestDto.getTicketStatus(), requestDto.getPlaceType());
    }

    @Transactional
    public TicketDto readTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findTicketByIdWithWriterAndTown(ticketId).orElseThrow(TicketNotFoundException::new);
        ticket.addView();
        return TicketDto.convertTicketToDto(ticket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
        ticket.getImages().stream().forEach(t -> fileService.delete(t.getPath()));
        ticketRepository.delete(ticket);
    }

}
