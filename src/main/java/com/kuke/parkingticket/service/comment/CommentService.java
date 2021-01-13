package com.kuke.parkingticket.service.comment;

import com.kuke.parkingticket.advice.exception.CommentNotFoundException;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.Comment;
import com.kuke.parkingticket.entity.DeleteStatus;
import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.comment.CommentDto;
import com.kuke.parkingticket.repository.comment.CommentRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public List<CommentDto> findCommentsByTicketId(Long ticketId) {
        ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
        return convertNestedStructure(commentRepository.findCommentByTicketId(ticketId));
    }

    @Transactional
    public CommentDto createComment(CommentCreateRequestDto requestDto) {
        Comment comment = commentRepository.save(
                Comment.createComment(requestDto.getContent(),
                        ticketRepository.findById(requestDto.getTicketId()).orElseThrow(TicketNotFoundException::new),
                        userRepository.findById(requestDto.getUserId()).orElseThrow(UserNotFoundException::new),
                        requestDto.getParentId() != null ?
                                commentRepository.findById(requestDto.getParentId()).orElseThrow(CommentNotFoundException::new) : null)
        );
        return convertCommentToDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findCommentByIdWithParent(commentId).orElseThrow(CommentNotFoundException::new);
        if(comment.getChildren().size() != 0) {
            comment.changeDeletedStatus(DeleteStatus.Y);
        } else {
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
    }

    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent();
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted() == DeleteStatus.Y)
                return getDeletableAncestorComment(parent);
        return comment;
    }


    private CommentDto convertCommentToDto(Comment comment) {
        return comment.getIsDeleted() == DeleteStatus.Y ?
                new CommentDto(comment.getId(), "삭제된 댓글입니다.", null, null) :
                new CommentDto(comment.getId(), comment.getContent(), comment.getWriter().getId(), comment.getWriter().getNickname());
    }

    private List<CommentDto> convertNestedStructure(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();
        comments.stream().forEach(c -> {
            CommentDto dto = convertCommentToDto(c);
            map.put(dto.getId(), dto);
            if(c.getParent() != null) map.get(c.getParent().getId()).getChildren().add(dto);
            else result.add(dto);
        });
        return result;
    }


}
