package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.region.RegionCreateRequestDto;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.service.region.RegionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TownControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired RegionService regionService;
    @Test
    public void findTownsByRegionTest() throws Exception {
        // given
        RegionDto regionDto = regionService.createRegion(new RegionCreateRequestDto("region"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/towns/{regionId}", regionDto.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datas").isArray())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}