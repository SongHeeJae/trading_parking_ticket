package com.kuke.parkingticket.model.dto.region;

import com.kuke.parkingticket.model.dto.town.TownDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto {
    private Long id;
    private String name;
    private List<TownDto> towns;
}
