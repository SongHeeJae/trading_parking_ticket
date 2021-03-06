package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.region.RegionService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.town.TownService;
import org.junit.jupiter.api.BeforeEach;
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
class RegionControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired TownService townService;
    @Autowired RegionService regionService;
    @Autowired SignService signService;
    @Test
    public void findAllRegionsWithTownsTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/regions/with-towns")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datas").isArray())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findAllRegionsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/regions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datas").isArray())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createRegionTest() throws Exception {

        // given
    }
}