package com.kuke.parkingticket.model.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    private String uid;
    private String password;
}
