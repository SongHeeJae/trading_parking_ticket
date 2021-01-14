package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.ReviewNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.review.ReviewCreateRequestDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.model.dto.ticket.TicketCreateRequestDto;
import com.kuke.parkingticket.model.dto.ticket.TicketDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.review.ReviewRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.history.HistoryService;
import com.kuke.parkingticket.service.review.ReviewService;
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
class ReviewControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired TicketService ticketService;
    @Autowired ReviewService reviewService;
    @Autowired ReviewRepository reviewRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("buyer1", "1234", "buyer1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller1", "1234", "seller1", town.getId()));
    }

    @Test
    public void findTypingReviewByUserIdTest() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/typing/{userId}", userDto.getId())
                .queryParam("limit", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findTypedReviewByUserId() throws Exception{
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/typed/{userId}", userDto.getId())
                .queryParam("limit", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createReviewTest() throws Exception {
        // given
        UserLoginResponseDto buyer = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));
        UserLoginResponseDto seller = signService.loginUser(new UserLoginRequestDto("seller1", "1234"));
        String token = buyer.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", seller.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        ReviewCreateRequestDto info = new ReviewCreateRequestDto("content", 5, buyer.getId(), seller.getId(), ticketDto.getId());
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteReviewTest() throws Exception {
        // given
        UserLoginResponseDto buyer = signService.loginUser(new UserLoginRequestDto("buyer1", "1234"));
        UserLoginResponseDto seller = signService.loginUser(new UserLoginRequestDto("seller1", "1234"));
        String token = buyer.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", seller.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        ReviewDto reviewDto = reviewService.createReview(new ReviewCreateRequestDto("content", 5, buyer.getId(), seller.getId(), ticketDto.getId()));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{reviewId}", reviewDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertThatThrownBy(() -> reviewRepository.findById(reviewDto.getId()).orElseThrow(ReviewNotFoundException::new))
                .isInstanceOf(ReviewNotFoundException.class);
    }
}