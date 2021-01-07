package com.kuke.parkingticket.service;

import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.model.dto.RegionDto;
import com.kuke.parkingticket.model.dto.TownDto;
import com.kuke.parkingticket.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public List<RegionDto> findAllRegionsWithTowns() {
        return regionRepository.findAll().stream().map(r ->
            new RegionDto(r.getId(), r.getName(), r.getTowns().stream().map(t -> new TownDto(t.getId(), t.getName())).collect(Collectors.toList()))
        ).collect(Collectors.toList());
    }
}
