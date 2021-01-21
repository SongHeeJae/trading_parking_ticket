package com.kuke.parkingticket.service.town;

import com.kuke.parkingticket.advice.exception.RegionNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.region.RegionCreateRequestDto;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.model.dto.region.RegionWithTownDto;
import com.kuke.parkingticket.model.dto.town.TownCreateRequestDto;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.region.RegionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TownServiceTest {
    @Autowired EntityManager em;
    @Autowired TownService townService;
    @Autowired TownRepository townRepository;
    @Autowired RegionService regionService;
    @Autowired RegionRepository regionRepository;

    @Test
    public void findTownsByRegionId() {
        // given
        RegionDto region = regionService.createRegion(new RegionCreateRequestDto("region"));
        List<String> townNames = Arrays.asList("town1", "town2");
        for (String townName : townNames) {
            townService.createTown(new TownCreateRequestDto(townName, region.getId()));
        }
        em.flush();
        em.clear();

        // when
        List<TownDto> result = townService.findTownsByRegionId(region.getId());

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo(townNames.get(0));
        assertThat(result.get(1).getName()).isEqualTo(townNames.get(1));
    }

    @Test
    public void createTownTest() {
        // given
        RegionDto region = regionService.createRegion(new RegionCreateRequestDto("region"));
        TownCreateRequestDto requestDto = new TownCreateRequestDto("town", region.getId());

        // when
        TownDto result = townService.createTown(requestDto);

        // then
        assertThat(result.getName()).isEqualTo(requestDto.getName());
    }

    @Test
    public void deleteRegionTest() {

        // given
        RegionDto region = regionService.createRegion(new RegionCreateRequestDto("region"));
        TownDto townDto = townService.createTown(new TownCreateRequestDto("town", region.getId()));

        // when
        townService.deleteTown(townDto.getId());

        // then
        assertThatThrownBy(() -> {
            townRepository.findById(townDto.getId()).orElseThrow(TownNotFoundException::new);
        }).isInstanceOf(TownNotFoundException.class);
    }
}