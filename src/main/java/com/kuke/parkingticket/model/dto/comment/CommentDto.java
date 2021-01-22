package com.kuke.parkingticket.model.dto.comment;

import com.kuke.parkingticket.entity.Comment;
import com.kuke.parkingticket.entity.DeleteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {

    private Long id;
    private String content;
    private Long userId;
    private String nickname;
    private List<CommentDto> children = new ArrayList<>();

    public CommentDto(Long id, String content, Long userId, String nickname) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
    }

    public static CommentDto convertCommentToDto(Comment comment) {
        return comment.getIsDeleted() == DeleteStatus.Y ?
                new CommentDto(comment.getId(), "삭제된 댓글입니다.", null, null) :
                new CommentDto(comment.getId(), comment.getContent(), comment.getWriter().getId(), comment.getWriter().getNickname());
    }
}
