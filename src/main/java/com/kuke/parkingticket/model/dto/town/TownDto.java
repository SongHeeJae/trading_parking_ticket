package com.kuke.parkingticket.model.dto.town;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TownDto {
    private Long id;
    private String name;
}