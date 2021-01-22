package com.kuke.parkingticket.model.dto.user;

import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.town.TownDto;
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

    public static UserDto convertUserToDto(User user) {
        return new UserDto(user.getId(), user.getUid(), user.getNickname(), new TownDto(user.getTown().getId(), user.getTown().getName()), user.getCreatedAt(), user.getModifiedAt());
    }
}
