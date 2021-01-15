package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.ticket.TicketCreateRequestDto;
import com.kuke.parkingticket.model.dto.ticket.TicketDto;
import com.kuke.parkingticket.model.dto.ticket.TicketUpdateRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired UserRepository userRepository;
    @Autowired SignService signService;
    @Autowired TicketService ticketService;
    @Autowired TicketRepository ticketRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("test", "1234", "test", town.getId()));
    }

    @Test
    public void createTicketTest() throws Exception {
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = responseDto.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        String title = "title";
        String content = "content";
        String price = "3000";
        String address = "address";
        String userId = String.valueOf(responseDto.getId());
        String townId = String.valueOf(town.getId());
        String termType = "INPUT";
        String ticketStatus = "ON";
        String placeType = "APARTMENT";
        String start = "201801010000";
        String end = "201802010000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/tickets")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("X-AUTH-TOKEN", token)
                    .param("title", title)
                    .param("content", content)
                    .param("price", price)
                    .param("address", address)
                    .param("userId", userId)
                    .param("townId", townId)
                    .param("TermType", termType)
                    .param("TicketStatus", ticketStatus)
                    .param("PlaceType", placeType)
                    .param("startDateTime", start)
                    .param("endDateTime", end))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").value(price))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value(address))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.town.id").value(townId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.termType").value(termType))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ticketStatus").value(ticketStatus))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.placeType").value(placeType))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.startDateTime").value(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.endDateTime").value(endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteTicketTest() throws Exception{
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = responseDto.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);

        TicketDto ticket = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", responseDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tickets/{ticketId}", ticket.getId())
                .header("X-AUTH-TOKEN", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThatThrownBy(() -> ticketRepository.findById(ticket.getId()).orElseThrow(TicketNotFoundException::new))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    public void readTicketTest() throws Exception {
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticket = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", responseDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tickets/{ticketId}", ticket.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(String.valueOf(ticket.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value("content"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateTicketTest() throws Exception {

        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = responseDto.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);

        TicketDto ticket = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", responseDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/tickets/{ticketId}", ticket.getId())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("X-AUTH-TOKEN", token)
                    .param("title", updateTitle)
                    .param("content", updateContent))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findAllTicketsTest() throws Exception {
        // given
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", responseDto.getId(), town.getId(), TermType.INPUT, TicketStatus.ON,
                PlaceType.APARTMENT, LocalDateTime.parse("201801010000", formatter), LocalDateTime.parse("201802010000", formatter)));

        ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", responseDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tickets")
                .queryParam("townId", String.valueOf(town.getId()))
                .queryParam("size", "30")
                .queryParam("page", "0")
                .queryParam("termTypes", "DAY")
                .queryParam("termTypes", "INPUT")
                .queryParam("placeTypes", "APARTMENT")
                .queryParam("ticketStatus", "ON")
                .queryParam("dateTime", "201801010000")
                .queryParam("sort", "createdAt,desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}