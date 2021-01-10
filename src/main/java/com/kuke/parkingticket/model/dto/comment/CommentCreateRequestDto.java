package com.kuke.parkingticket.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequestDto {
    private String content;
    private Long ticketId;
    private Long userId;
    private Long parentId;
}
