package com.kuke.parkingticket.service.region;

import com.kuke.parkingticket.advice.exception.RegionNotFoundException;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.region.RegionCreateRequestDto;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.model.dto.region.RegionWithTownDto;
import com.kuke.parkingticket.model.dto.town.TownCreateRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.town.TownService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class RegionServiceTest {
    @Autowired TownService townService;
    @Autowired RegionService regionService;
    @Autowired EntityManager em;
    @Autowired RegionRepository regionRepository;

    @Test
    public void findAllRegionsWithTownsTest() {

        // given
        int townLengthPerRegion[] = {2, 3};
        for(int i=0; i<townLengthPerRegion.length; i++) {
            RegionDto region = regionService.createRegion(new RegionCreateRequestDto("region" + i));
            for(int j=0; j<townLengthPerRegion[i]; j++) {
                townService.createTown(new TownCreateRequestDto("town" + i + j, region.getId()));
            }
        }

        em.flush();
        em.clear();

        // when
        List<RegionWithTownDto> result = regionService.findAllRegionsWithTowns();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTowns().size()).isEqualTo(2);
        assertThat(result.get(1).getTowns().size()).isEqualTo(3);
    }

    @Test
    public void findAllRegionsTest() {
        // given
        for(int i=0; i<2; i++) {
            regionService.createRegion(new RegionCreateRequestDto("region" + i));
        }

        // when
        List<RegionDto> result = regionService.findAllRegions();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void createRegionTest() {

        // given
        RegionCreateRequestDto requestDto = new RegionCreateRequestDto("region1");

        // when
        RegionDto region = regionService.createRegion(requestDto);

        // then
        assertThat(region.getName()).isEqualTo(requestDto.getName());
    }

    @Test
    public void deleteRegionTest() {

        // given
        RegionCreateRequestDto requestDto = new RegionCreateRequestDto("region1");
        RegionDto region = regionService.createRegion(requestDto);

        // when
        regionService.deleteRegion(region.getId());

        // then
        assertThatThrownBy(() -> regionRepository.findById(region.getId()).orElseThrow(RegionNotFoundException::new)).isInstanceOf(RegionNotFoundException.class);
    }

}