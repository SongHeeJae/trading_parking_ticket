package com.kuke.parkingticket.repository.comment;

import com.kuke.parkingticket.entity.Comment;

import java.util.List;

public interface CustomCommentRepository {
    //findCommentsByTicketIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc
    List<Comment> findCommentByTicketId(Long ticketId);
}
