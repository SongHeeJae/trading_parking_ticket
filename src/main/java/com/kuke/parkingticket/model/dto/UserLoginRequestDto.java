package com.kuke.parkingticket.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    private String uid;
    private String password;
}
