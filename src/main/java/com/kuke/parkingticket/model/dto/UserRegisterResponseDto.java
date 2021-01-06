package com.kuke.parkingticket.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponseDto {
    private String uid;
    private String townName;
}
