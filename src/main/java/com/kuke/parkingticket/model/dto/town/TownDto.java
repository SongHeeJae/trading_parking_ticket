package com.kuke.parkingticket.model.dto.town;

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
}
