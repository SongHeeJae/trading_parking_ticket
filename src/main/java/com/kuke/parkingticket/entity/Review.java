package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.entity.date.CommonDateEntity;
import com.kuke.parkingticket.entity.date.CreatedDateEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Review extends CreatedDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    public static Review createReview(String content, double score, User buyer, User seller, Ticket ticket) {
        Review review = new Review();
        review.content = content;
        review.score = score;
        review.buyer = buyer;
        review.seller = seller;
        review.ticket = ticket;
        return review;
    }



}
