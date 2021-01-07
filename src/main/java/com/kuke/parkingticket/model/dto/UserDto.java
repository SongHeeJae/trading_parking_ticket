package com.kuke.parkingticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String uid;
    private String nickname;
    private TownDto town;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
