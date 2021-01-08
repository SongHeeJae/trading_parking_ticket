package com.kuke.parkingticket.model.dto;

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
public class TicketCreateRequestDto {
    private String title;
    private String content;
    private int price;
    private String address;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long userId;
    private Long townId;
    private TermType termType;
    private TicketStatus ticketStatus;
    private PlaceType placeType;
}
