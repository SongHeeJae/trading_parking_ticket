package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.entity.date.CommonDateEntity;
import com.kuke.parkingticket.entity.date.CreatedDateEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment extends CreatedDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Enumerated(value = EnumType.STRING)
    private DeleteStatus isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent",
            orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public static Comment createComment(String content, Ticket ticket, User writer, Comment parent) {
        return Comment.builder()
                .content(content)
                .ticket(ticket)
                .writer(writer)
                .parent(parent)
                .isDeleted(DeleteStatus.N).build();
    }

    public void changeDeletedStatus(DeleteStatus deleteStatus) {
        this.isDeleted = deleteStatus;
    }

}
