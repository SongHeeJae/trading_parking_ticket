package com.kuke.parkingticket.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;

    public static Town createTown(String name, Region region) {
        Town town = new Town();
        town.name = name;
        town.region = region;
        return town;
    }
}
