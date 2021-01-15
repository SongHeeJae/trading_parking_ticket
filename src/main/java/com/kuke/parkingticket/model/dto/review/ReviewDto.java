package com.kuke.parkingticket.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto implements Serializable {
    private Long id;
    private String content;
    private double score;
    private Long buyerId;
    private String buyerNickname;
    private Long sellerId;
    private String sellerNickname;
    private Long ticketId;
}
