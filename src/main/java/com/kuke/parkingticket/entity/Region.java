package com.kuke.parkingticket.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "region")
    private List<Town> towns = new ArrayList<>();

    public static Region createRegion(String name) {
        return Region.builder()
                .name(name).build();
    }
}
