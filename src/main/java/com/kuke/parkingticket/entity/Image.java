package com.kuke.parkingticket.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    public static Image createImage(String path, Ticket ticket) {
        Image image = new Image();
        image.path = path;
        image.ticket = ticket;
        return image;
    }


}
