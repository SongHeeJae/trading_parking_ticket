package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.CommentNotFoundException;
import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.ticket.TicketCreateRequestDto;
import com.kuke.parkingticket.model.dto.ticket.TicketDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.comment.CommentRepository;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.comment.CommentService;
import com.kuke.parkingticket.service.history.HistoryService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HistoryControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired TicketService ticketService;
    @Autowired HistoryService historyService;
    @Autowired HistoryRepository historyRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("buyer1", "1234", "buyer1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller1", "1234", "seller1", town.getId()));
    }

    @Test
    public void findSalesHistoriesByUserIdTest() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/histories/sale/{userId}", userDto.getId())
                    .queryParam("limit", "20")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findPurchaseHistoriesByUserIdTest() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/histories/purchase/{userId}", userDto.getId())
                    .queryParam("limit", "20")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createHistoryTest() throws Exception {
        // given
        UserLoginResponseDto buyer = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));
        UserLoginResponseDto seller = signService.loginUser(new UserLoginRequestDto("seller1", "1234"));
        String token = buyer.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", seller.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        HistoryCreateRequestDto info = new HistoryCreateRequestDto(ticketDto.getId(), buyer.getId(), seller.getId(), 3000,
                LocalDateTime.parse("201801010000", formatter),  LocalDateTime.parse("201802010000", formatter));
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/histories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteHistoryTest() throws Exception {
        // given
        UserLoginResponseDto buyer = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));
        UserLoginResponseDto seller = signService.loginUser(new UserLoginRequestDto("seller1", "1234"));
        String token = buyer.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", seller.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        HistoryDto historyDto = historyService.createHistory(new HistoryCreateRequestDto(ticketDto.getId(), buyer.getId(), seller.getId(), 3000,
                LocalDateTime.parse("201801010000", formatter), LocalDateTime.parse("201802010000", formatter)));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/histories/{historyId}", historyDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-AUTH-TOKEN", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertThatThrownBy(() -> historyRepository.findById(historyDto.getId()).orElseThrow(HistoryNotFoundException::new))
                .isInstanceOf(HistoryNotFoundException.class);
    }

}