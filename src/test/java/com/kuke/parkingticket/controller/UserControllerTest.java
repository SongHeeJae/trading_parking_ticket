package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.RegionNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired UserRepository userRepository;
    @Autowired SignService signService;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("test", "1234", "test", town.getId()));
    }

    @Test
    public void findAllUsersTest() throws Exception{

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datas").isArray())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findUserTest() throws Exception {
        // given
        User user = userRepository.findByUid("test").orElseThrow(UserNotFoundException::new);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}", user.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.uid").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateUserTest() throws Exception {
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String userId = String.valueOf(responseDto.getId());
        String token = responseDto.getToken();

        Region region = regionRepository.findByName("region").orElseThrow(RegionNotFoundException::new);
        Town updateTown = townRepository.save(Town.createTown("town", region));
        String updateNickname = "test2";

        UserUpdateRequestDto info = new UserUpdateRequestDto(updateNickname, updateTown.getId());
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{userId}", userId)
                .header("X-AUTH-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value(updateNickname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.town.id").value(updateTown.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteUserTest() throws Exception{
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String userId = String.valueOf(responseDto.getId());
        String token = responseDto.getToken();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{userId}", userId)
                .header("X-AUTH-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThatThrownBy(() -> userRepository.findByUid("test").orElseThrow(UserNotFoundException::new))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void invalidTokenRequestTest() throws Exception {
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String userId = String.valueOf(responseDto.getId());
        String invalidToken = responseDto.getToken() + "a";

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{userId}", userId)
                .header("X-AUTH-TOKEN", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}