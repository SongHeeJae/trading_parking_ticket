package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired EntityManager em;
    @Autowired ObjectMapper objectMapper;

    @Test
    public void registerTest() throws Exception {

        // given
        Region region = Region.createRegion("region");
        em.persist(region);
        Town town = Town.createTown("town", region);
        em.persist(town);

        UserRegisterRequestDto info = new UserRegisterRequestDto("uid", "1234", "nickname", town.getId());
        String content = objectMapper.writeValueAsString(info);
        System.out.println("content = " + content);

//        mockMvc.perform(post());
    }

    @Test
    public void loginTest() {
//        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
//        info.add("uid", "uid");
//        info.add("password", "1234");
//        info.add("nickname", "nickname");
//        info.add("townId", );

    }
}