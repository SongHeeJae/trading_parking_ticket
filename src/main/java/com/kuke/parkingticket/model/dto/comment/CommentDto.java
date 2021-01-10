package com.kuke.parkingticket.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

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
}
