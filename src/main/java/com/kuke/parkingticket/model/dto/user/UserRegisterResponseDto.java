package com.kuke.parkingticket.model.dto.user;
import com.kuke.parkingticket.model.dto.town.TownDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponseDto {
    private String uid;
    private TownDto town;
}
