package com.kuke.parkingticket.service.history;

import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.review.ReviewService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class HistoryServiceTest {

    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired TicketService ticketService;
    @Autowired HistoryService historyService;
    @Autowired UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("ReviewServiceTest");
        em.persist(region);
        Town town = Town.createTown("ReviewServiceTest", region);
        em.persist(town);
        signService.registerUser(new UserRegisterRequestDto("buyer", "1234", "buyer", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller", "1234", "seller", town.getId()));
    }

    @Test
    public void createHistoryTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
//        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
//                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
//        em.persist(ticket);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        // when
//        historyService.createHistory(new HistoryCreateRequestDto(ticket.getId(), buyer.getId(), seller.getId(), 3000,
//                ))
    }

}