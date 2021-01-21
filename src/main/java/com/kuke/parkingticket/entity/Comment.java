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

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public static Comment createComment(String content, Ticket ticket, User writer, Comment parent) {
        Comment comment = new Comment();
        comment.content = content;
        comment.ticket = ticket;
        comment.writer = writer;
        comment.parent = parent;
        comment.isDeleted = DeleteStatus.N;
        return comment;
    }

    public void changeDeletedStatus(DeleteStatus deleteStatus) {
        this.isDeleted = deleteStatus;
    }

}
