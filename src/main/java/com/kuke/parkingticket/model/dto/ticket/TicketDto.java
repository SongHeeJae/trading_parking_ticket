package com.kuke.parkingticket.model.dto.ticket;

import com.kuke.parkingticket.entity.PlaceType;
import com.kuke.parkingticket.entity.TermType;
import com.kuke.parkingticket.entity.Ticket;
import com.kuke.parkingticket.entity.TicketStatus;
import com.kuke.parkingticket.model.dto.image.ImageDto;
import com.kuke.parkingticket.model.dto.town.TownDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto implements Serializable {
    private Long id;
    private String title;
    private String content;
    private int price;
    private int view;
    private String address;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TermType termType;
    private TicketStatus ticketStatus;
    private PlaceType placeType;
    private Long userId;
    private String nickname;
    private TownDto town;
    private List<ImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static TicketDto convertTicketToDto(Ticket ticket) {
        return new TicketDto(ticket.getId(), ticket.getTitle(), ticket.getContent(), ticket.getPrice(),
                ticket.getView(), ticket.getAddress(), ticket.getStartDateTime(), ticket.getEndDateTime(),
                ticket.getTermType(), ticket.getTicketStatus(), ticket.getPlaceType(),
                ticket.getWriter().getId(),
                ticket.getWriter().getNickname(),
                TownDto.convertTownToDto(ticket.getTown()),
                ticket.getImages().stream().map(i -> ImageDto.convertImageToDto(i)).collect(Collectors.toList()),
                ticket.getCreatedAt(), ticket.getModifiedAt());
    }
}
