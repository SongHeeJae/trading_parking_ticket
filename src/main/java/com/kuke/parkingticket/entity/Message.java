package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.entity.date.CreatedDateEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message extends CreatedDateEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;

    public static Message createMessage(User sender, User receiver, String message) {
        return Message.builder().sender(sender)
                .receiver(receiver)
                .message(message)
                .readingStatus(ReadingStatus.N)
                .build();
    }

    public void changeReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

}
