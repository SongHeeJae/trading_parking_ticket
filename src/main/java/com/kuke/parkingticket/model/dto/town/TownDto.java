package com.kuke.parkingticket.model.dto.town;

import com.kuke.parkingticket.entity.Town;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TownDto implements Serializable {
    private Long id;
    private String name;

    public static TownDto convertTownToDto(Town t) {
        return new TownDto(t.getId(), t.getName());
    }
}
