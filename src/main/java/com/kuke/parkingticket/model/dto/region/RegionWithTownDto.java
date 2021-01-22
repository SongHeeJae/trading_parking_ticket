package com.kuke.parkingticket.model.dto.region;

import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.model.dto.town.TownDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionWithTownDto implements Serializable {
    private Long id;
    private String name;
    private List<TownDto> towns;

    public static RegionWithTownDto convertRegionToDto(Region region) {
        return new RegionWithTownDto(region.getId(), region.getName(), region.getTowns().stream().map(t -> TownDto.convertTownToDto(t)).collect(Collectors.toList()));
    }

}
