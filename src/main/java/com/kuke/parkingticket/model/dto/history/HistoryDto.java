package com.kuke.parkingticket.model.dto.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto {
    private Long id;
    private int price;
    private Long ticketId;
    private String address;
    private Long buyerId;
    private String buyerNickname;
    private Long sellerId;
    private String sellerNickname;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
