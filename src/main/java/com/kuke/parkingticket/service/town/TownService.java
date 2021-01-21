package com.kuke.parkingticket.service.town;

import com.kuke.parkingticket.advice.exception.*;
import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Review;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.town.TownCreateRequestDto;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.model.dto.user.UserDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TownService {
    private final TownRepository townRepository;
    private final RegionRepository regionRepository;
    private final CacheService cacheService;


    @Cacheable(value = CacheKey.TOWNS, key="#regionId", unless = "#result == null")
    public List<TownDto> findTownsByRegionId(Long regionId) {
        Region region = regionRepository.findById(regionId).orElseThrow(RegionNotFoundException::new);
        return townRepository.findByRegion(region).stream().map(t -> convertTownToDto(t)).collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.REGIONS),
            @CacheEvict(value = CacheKey.REGIONS_WITH_TOWNS),
            @CacheEvict(value = CacheKey.TOWNS, key = "#requestDto.regionId", allEntries = true)
    })
    public TownDto createTown(TownCreateRequestDto requestDto) {
        Town town = townRepository.save(
                Town.createTown(requestDto.getName(),
                        regionRepository.findById(requestDto.getRegionId()).orElseThrow(RegionNotFoundException::new)));
        return convertTownToDto(town);
    }

    @Transactional
    public void deleteTown(Long townId) {
        Town town = townRepository.findById(townId).orElseThrow(TownNotFoundException::new);
        cacheService.deleteTownsCache(town.getRegion().getId());
        townRepository.delete(town);
    }

    private TownDto convertTownToDto(Town t) {
        return new TownDto(t.getId(), t.getName());
    }
}
