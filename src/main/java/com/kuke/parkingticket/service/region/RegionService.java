package com.kuke.parkingticket.service.region;

import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.model.dto.region.RegionCreateRequestDto;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.model.dto.region.RegionWithTownDto;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    @Cacheable(value = CacheKey.REGIONS_WITH_TOWNS, unless = "#result == null")
    public List<RegionWithTownDto> findAllRegionsWithTowns() {
        return regionRepository.findAll().stream().map(r ->
            new RegionWithTownDto(r.getId(), r.getName(), r.getTowns().stream().map(t -> new TownDto(t.getId(), t.getName())).collect(Collectors.toList()))
        ).collect(Collectors.toList());
    }

    @Cacheable(value = CacheKey.REGIONS, unless = "#result == null")
    public List<RegionDto> findAllRegions() {
        return regionRepository.findAll().stream().map(r ->
                new RegionDto(r.getId(), r.getName())).collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.REGIONS),
            @CacheEvict(value = CacheKey.REGIONS_WITH_TOWNS)
    })
    public RegionDto createRegion(RegionCreateRequestDto requestDto) {
        Region region = regionRepository.save(
                Region.createRegion(requestDto.getName()));
        return new RegionDto(region.getId(), region.getName());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.REGIONS),
            @CacheEvict(value = CacheKey.REGIONS_WITH_TOWNS),
            @CacheEvict(value = CacheKey.TOWNS, key = "#regionId", allEntries = true)
    })
    public void deleteRegion(Long regionId) {
        regionRepository.deleteById(regionId);
    }
}
