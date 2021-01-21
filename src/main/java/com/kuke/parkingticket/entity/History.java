package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.entity.date.CommonDateEntity;
import com.kuke.parkingticket.entity.date.CreatedDateEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class History extends CreatedDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    public static History createHistory(int price, Ticket ticket, User buyer, User seller, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        History history = new History();
        history.price = price;
        history.ticket = ticket;
        history.buyer = buyer;
        history.seller = seller;
        history.startDateTime = startDateTime;
        history.endDateTime = endDateTime;
        return history;
    }
}
