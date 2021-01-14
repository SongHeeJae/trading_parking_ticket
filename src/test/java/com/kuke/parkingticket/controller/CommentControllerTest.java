package com.kuke.parkingticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.advice.exception.CommentNotFoundException;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.comment.CommentDto;
import com.kuke.parkingticket.model.dto.ticket.TicketCreateRequestDto;
import com.kuke.parkingticket.model.dto.ticket.TicketDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.comment.CommentRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.comment.CommentService;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired TicketService ticketService;
    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("region"));
        Town town = townRepository.save(Town.createTown("town", region));
        signService.registerUser(new UserRegisterRequestDto("test", "1234", "test", town.getId()));
    }

    @Test
    public void createCommentTest() throws Exception {
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = userDto.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", userDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        CommentCreateRequestDto info = new CommentCreateRequestDto("content", ticketDto.getId(), userDto.getId(), null);
        String content = objectMapper.writeValueAsString(info);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-AUTH-TOKEN", token)
                    .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteCommentTest() throws Exception {
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        String token = userDto.getToken();
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", userDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));
        CommentDto commentDto = commentService.createComment(new CommentCreateRequestDto("content", ticketDto.getId(), userDto.getId(), null));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/{commentId}", commentDto.getId())
                .header("X-AUTH-TOKEN", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertThatThrownBy(() -> commentRepository.findById(commentDto.getId()).orElseThrow(CommentNotFoundException::new))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    public void findAllCommentsByTicketIdTest() throws Exception {
        // given
        UserLoginResponseDto userDto = signService.loginUser(new UserLoginRequestDto("test", "1234"));
        Town town = townRepository.findByName("town").orElseThrow(TownNotFoundException::new);
        TicketDto ticketDto = ticketService.createTicket(new TicketCreateRequestDto(Collections.emptyList(), "title", "content", 3000, "address", userDto.getId(), town.getId(), TermType.DAY, TicketStatus.ON,
                PlaceType.APARTMENT, null, null));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/{ticketId}", ticketDto.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}