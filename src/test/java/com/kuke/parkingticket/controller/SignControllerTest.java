package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.config.security.JwtTokenProvider;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.sign.SignService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        jwtTokenProvider.setTokenValidMillisecond(1000L * 60 * 30);
        jwtTokenProvider.setRefreshTokenValidMillisecond(1000L * 60 * 60 * 24 * 7);
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("test", "1234", "test", town.getId()));
    }

    @Test
    public void registerTest() throws Exception{

        // given
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        UserRegisterRequestDto info = new UserRegisterRequestDto("uid", "1234", "nickname", town.getId());
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sign/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.uid").value("uid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("nickname"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void loginTest() throws Exception{
        // given
        String uid = "test";
        String password = "1234";
        UserLoginRequestDto info = new UserLoginRequestDto(uid, password);
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sign/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        User user = userRepository.findByUid("test").orElseThrow(UserNotFoundException::new);
        assertThat(jwtTokenProvider.validateToken(user.getRefreshToken())).isTrue();
    }

    @Test
    public void logoutTest() throws Exception {

        // given
        UserLoginResponseDto loginDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = loginDto.getToken();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sign/logout")
                .header("X-AUTH-TOKEN", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print());

        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    public void refreshTokenTest() throws Exception {

        // given
        jwtTokenProvider.setTokenValidMillisecond(-1L);
        UserLoginResponseDto loginDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = loginDto.getToken();
        String refreshToken = loginDto.getRefreshToken();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sign/refresh")
                .header("X-AUTH-TOKEN", token)
                .header("REFRESH-TOKEN", refreshToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void refreshEmptyTokenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sign/refresh"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andDo(MockMvcResultHandlers.print());
    }
}