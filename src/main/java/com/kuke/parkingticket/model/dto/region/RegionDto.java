package com.kuke.parkingticket.model.dto.region;

import com.kuke.parkingticket.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto implements Serializable {
    private Long Id;
    private String name;

    public static RegionDto convertRegionToDto(Region region) {
        return new RegionDto(region.getId(), region.getName());
    }
}
