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
public class TicketSimpleDto {
    private Long id;
    private String title;
    private int price;
    private int view;
    private String address;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TermType termType;
    private TicketStatus ticketStatus;
    private PlaceType placeType;
    private String nickname;
    private LocalDateTime createdAt;

}
