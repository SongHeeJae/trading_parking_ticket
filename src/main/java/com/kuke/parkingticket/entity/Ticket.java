package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.entity.date.CommonDateEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ticket extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int view; // 조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;

    @Enumerated(EnumType.STRING)
    private TermType termType;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public static Ticket createTicket(String title, String content, String address, int price, User writer,
                               Town town, PlaceType placeType, TermType termType, TicketStatus ticketStatus,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Ticket ticket = new Ticket();
        ticket.title = title;
        ticket.content = content;
        ticket.address = address;
        ticket.price = price;
        ticket.writer = writer;
        ticket.town = town;
        ticket.placeType = placeType;
        ticket.termType = termType;
        ticket.ticketStatus = ticketStatus;
        ticket.startDateTime = startDateTime;
        ticket.endDateTime = endDateTime;
        ticket.view = 0;
        return ticket;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addImage(Image image) {
        this.images.add(image);
    }

    public void update(String title, String content, int price, String address, LocalDateTime startDateTime,
                       LocalDateTime endDateTime, TermType termType, TicketStatus ticketStatus, PlaceType placeType) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.address = address;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.termType = termType;
        this.ticketStatus = ticketStatus;
        this.placeType = placeType;
    }

    public void addView() {
        this.view++;
    }
}
