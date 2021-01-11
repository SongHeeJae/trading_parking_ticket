package com.kuke.parkingticket.model.dto.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCreateRequestDto {
    private Long ticketId;
    private Long buyerId;
    private Long sellerId;
    private int price;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
