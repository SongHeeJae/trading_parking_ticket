package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.MessageNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.message.MessageCreateRequestDto;
import com.kuke.parkingticket.model.dto.message.MessageDto;
import com.kuke.parkingticket.model.dto.ticket.TicketCreateRequestDto;
import com.kuke.parkingticket.model.dto.ticket.TicketDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.message.MessageRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.history.HistoryService;
import com.kuke.parkingticket.service.message.MessageService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MessageControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired MessageService messageService;
    @Autowired MessageRepository messageRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("sender1", "1234", "sender1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("receiver1", "1234", "receiver1", town.getId()));
    }

    @Test
    public void findSentMessagesByUserIdTest() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("sender1", "1234"));
        String token = userDto.getToken();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/sent/{userId}", userDto.getId())
                    .header("X-AUTH-TOKEN", token)
                    .queryParam("limit", "20")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findReceivedMessagesByUserIdTest() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("receiver1", "1234"));
        String token = userDto.getToken();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/received/{userId}", userDto.getId())
                .header("X-AUTH-TOKEN", token)
                .queryParam("limit", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sendMessageTest() throws Exception {
        // given
        UserLoginResponseDto sender = signService.loginUser(new UserLoginRequestDto("sender1", "1234"));
        UserLoginResponseDto receiver = signService.loginUser(new UserLoginRequestDto("receiver1", "1234"));
        String token = sender.getToken();
        MessageCreateRequestDto info = new MessageCreateRequestDto(sender.getId(), receiver.getId(), "content");
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteMessageTest() throws Exception {

        // given
        UserLoginResponseDto sender = signService.loginUser(new UserLoginRequestDto("sender1", "1234"));
        UserLoginResponseDto receiver = signService.loginUser(new UserLoginRequestDto("receiver1", "1234"));
        String token = sender.getToken();
        MessageDto messageDto = messageService.createMessage(new MessageCreateRequestDto(sender.getId(), receiver.getId(), "content"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/messages/{messageId}", messageDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertThatThrownBy(() -> messageRepository.findById(messageDto.getId()).orElseThrow(MessageNotFoundException::new))
                .isInstanceOf(MessageNotFoundException.class);
    }
}