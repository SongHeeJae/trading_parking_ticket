package com.kuke.parkingticket.model.dto.ticket;

import com.kuke.parkingticket.entity.PlaceType;
import com.kuke.parkingticket.entity.TermType;
import com.kuke.parkingticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdateRequestDto {
    private String title;
    private String content;
    private int price;
    private String address;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TermType termType;
    private TicketStatus ticketStatus;
    private PlaceType placeType;
}
