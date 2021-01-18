package com.kuke.parkingticket.model.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDto {
    private Long id;
    private String token;
    private String refreshToken;
}
