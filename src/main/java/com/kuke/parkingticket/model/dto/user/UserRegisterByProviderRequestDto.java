package com.kuke.parkingticket.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterByProviderRequestDto {
    private String nickname;
    private Long townId;
}
