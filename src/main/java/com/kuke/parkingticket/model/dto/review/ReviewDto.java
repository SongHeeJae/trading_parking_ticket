package com.kuke.parkingticket.model.dto.review;

import com.kuke.parkingticket.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    public static ReviewDto convertReviewToDto(Review review) {
        return new ReviewDto(review.getId(), review.getContent(), review.getScore(), review.getBuyer().getId(), review.getBuyer().getNickname(),
                review.getSeller().getId(), review.getSeller().getNickname(), review.getTicket().getId(), review.getCreatedAt());
    }
}
