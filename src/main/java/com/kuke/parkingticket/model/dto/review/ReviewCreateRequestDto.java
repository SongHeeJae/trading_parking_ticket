package com.kuke.parkingticket.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequestDto {
    private String content;
    private double score;
    private Long buyerId;
    private Long sellerId;
    private Long ticketId;
}
