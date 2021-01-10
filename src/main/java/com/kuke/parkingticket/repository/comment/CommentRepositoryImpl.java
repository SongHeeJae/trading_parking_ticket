package com.kuke.parkingticket.repository.comment;

import com.kuke.parkingticket.entity.Comment;
import com.kuke.parkingticket.entity.QComment;
import com.kuke.parkingticket.entity.Ticket;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kuke.parkingticket.entity.QComment.*;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CustomCommentRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findCommentByTicketId(Long ticketId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .where(comment.ticket.id.eq(ticketId))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc()
                ).fetch();
    }
}
