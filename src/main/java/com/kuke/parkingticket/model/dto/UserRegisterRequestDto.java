package com.kuke.parkingticket.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    private String uid;
    private String password;
    private String nickname;
    private Long townId;
}
