package com.kuke.parkingticket.service.region;

import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RegionServiceTest {
    @Autowired EntityManager em;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired RegionService regionService;

    @Test
    public void findAllRegionsWithTownsTest() {

        // given
        String[] regionNames = new String[]{"region1", "region2"};
        String[][] townNames = new String[2][];
        townNames[0] = new String[]{"town11", "town12"};
        townNames[1] = new String[]{"town21", "town22", "town23"};
        for(int i=0; i<regionNames.length; i++) {
            Region region = regionRepository.save(Region.createRegion(regionNames[i]));
            for(int j=0; j<townNames[i].length; j++) {
                townRepository.save(Town.createTown(townNames[i][j], region));
            }
        }
        em.flush();
        em.clear();

        // when
        List<RegionDto> result = regionService.findAllRegionsWithTowns();
        System.out.println("result = " + result);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTowns().size()).isEqualTo(2);
        assertThat(result.get(1).getTowns().size()).isEqualTo(3);
    }

}