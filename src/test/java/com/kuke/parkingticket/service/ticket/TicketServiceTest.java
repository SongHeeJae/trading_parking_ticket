package com.kuke.parkingticket.service.ticket;

import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.ticket.*;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.review.ReviewRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.review.ReviewService;
import com.kuke.parkingticket.service.sign.SignService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TicketServiceTest {

    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired TicketRepository ticketRepository;
    @Autowired TicketService ticketService;
    @Autowired UserRepository userRepository;
    @Autowired TownRepository townRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("TicketServiceTest");
        em.persist(region);
        Town town = Town.createTown("TicketServiceTest", region);
        em.persist(town);
        signService.registerUser(new UserRegisterRequestDto("TicketServiceTest", "1234", "TicketServiceTest", town.getId()));
    }

    @Test
    public void findAllTicketsTest() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        User user = userRepository.findByUid("TicketServiceTest").orElseThrow(UserNotFoundException::new);
        TicketDto ticketDto1 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title1", "content1", 3000, "address1", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        TicketDto ticketDto2 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title2", "content2", 3000, "address2", user.getId(), user.getTown().getId(), TermType.INPUT, TicketStatus.ON,
                PlaceType.HOUSE, LocalDateTime.parse("201801010000", formatter), LocalDateTime.parse("201802010000", formatter)));
        TicketDto ticketDto3 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title3", "content3", 3000, "address3", user.getId(), user.getTown().getId(), TermType.INPUT, TicketStatus.ON,
                PlaceType.OFFICE_HOTEL, LocalDateTime.parse("201901010000", formatter), LocalDateTime.parse("202001010000", formatter)));
        TicketDto ticketDto4 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title4", "content4", 3000, "address4", user.getId(), user.getTown().getId(), TermType.MONTH, TicketStatus.COMP,
                PlaceType.OFFICE_HOTEL, null, null));
        TicketDto ticketDto5 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title5", "content5", 3000, "address5", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        TicketDto ticketDto6 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title6", "content6", 3000, "address6", user.getId(), user.getTown().getId(), TermType.MONTH, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        TicketDto ticketDto7 = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title7", "content7", 3000, "address7", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.COMP,
                PlaceType.OFFICE_HOTEL, null, null));

        List<TermType> termTypes = new ArrayList<>(); // 1, 2, 5, 7
        termTypes.add(TermType.DAY);
        termTypes.add(TermType.INPUT);
        LocalDateTime dateTime = LocalDateTime.parse("201801150000", formatter);

        List<TicketStatus> ticketStatuses = new ArrayList<>(); // 1, 2, 3, 5, 6
        ticketStatuses.add(TicketStatus.ON);

        List<PlaceType> placeTypes = new ArrayList<>(); // 1, 2, 3, 4, 5, 6, 7
        placeTypes.add(PlaceType.APARTMENT);
        placeTypes.add(PlaceType.OFFICE_HOTEL);
        placeTypes.add(PlaceType.HOUSE);

        TicketSearchConditionDto conditionDto = new TicketSearchConditionDto(user.getTown().getId(), termTypes, placeTypes, ticketStatuses, dateTime);
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by("createdAt").ascending());

        // when
        Page<TicketSimpleDto> result = ticketService.findAllTickets(conditionDto, pageRequest);

        // then
        assertThat(result.getContent().size()).isEqualTo(4);
        assertThat(result.getContent().get(0).getId()).isEqualTo(ticketDto1.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(ticketDto2.getId());
        assertThat(result.getContent().get(2).getId()).isEqualTo(ticketDto3.getId());
        assertThat(result.getContent().get(3).getId()).isEqualTo(ticketDto5.getId());


    }

    @Test
    public void createTicketTest() {
        // given
        User user = userRepository.findByUid("TicketServiceTest").orElseThrow(UserNotFoundException::new);
        TicketCreateRequestDto requestDto = new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null);

        // when
        TicketDto ticketDto = ticketService.createTicket(requestDto);

        // then
        assertThat(ticketDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(ticketDto.getContent()).isEqualTo(requestDto.getContent());
        assertThat(ticketDto.getPrice()).isEqualTo(requestDto.getPrice());
    }

    @Test
    public void updateTicketTest() {
        // given
        User user = userRepository.findByUid("TicketServiceTest").orElseThrow(UserNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        // when
        ticketService.updateTicket(ticketDto.getId(), new TicketUpdateRequestDto(Collections.emptyList(), updateTitle, updateContent,
                3000, "address", null, null, TermType.DAY, TicketStatus.ON, PlaceType.APARTMENT));
        em.flush();
        em.clear();

        // then
        Ticket ticket = ticketRepository.findById(ticketDto.getId()).orElseThrow(TicketNotFoundException::new);
        assertThat(ticket.getTitle()).isEqualTo(updateTitle);
        assertThat(ticket.getContent()).isEqualTo(updateContent);
    }

    @Test
    public void deleteTicketTest() {
        // given
        User user = userRepository.findByUid("TicketServiceTest").orElseThrow(UserNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", user.getId(), user.getTown().getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        // when
        ticketService.deleteTicket(ticketDto.getId());

        // then
        assertThrows(TicketNotFoundException.class,
                () -> ticketRepository.findById(ticketDto.getId()).orElseThrow(TicketNotFoundException::new));
    }
}