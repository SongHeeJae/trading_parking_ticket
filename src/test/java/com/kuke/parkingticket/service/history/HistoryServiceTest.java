package com.kuke.parkingticket.service.history;

import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.review.ReviewService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class HistoryServiceTest {

    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired TicketService ticketService;
    @Autowired HistoryService historyService;
    @Autowired UserRepository userRepository;
    @Autowired HistoryRepository historyRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("ReviewServiceTest");
        em.persist(region);
        Town town = Town.createTown("ReviewServiceTest", region);
        em.persist(town);
        signService.registerUser(new UserRegisterRequestDto("buyer1", "1234", "buyer", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller1", "1234", "seller", town.getId()));
    }

    @Test
    public void createHistoryTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null);
        em.persist(ticket);
        int price = 3000;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime startDateTime = LocalDateTime.parse("202001010000", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("202003060000", formatter);
        HistoryDto result = historyService.createHistory(new HistoryCreateRequestDto(ticket.getId(), buyer.getId(), seller.getId(), price, startDateTime,
                endDateTime));
        em.flush();
        em.clear();

        // when
        History history = historyRepository.findById(result.getId()).orElseThrow(HistoryNotFoundException::new);

        // then
        assertThat(history.getId()).isEqualTo(result.getId());
        assertThat(history.getPrice()).isEqualTo(price);
    }

    @Test
    public void deleteHistoryTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null);
        em.persist(ticket);
        int price = 3000;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime startDateTime = LocalDateTime.parse("202001010000", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("202003060000", formatter);
        HistoryDto result = historyService.createHistory(new HistoryCreateRequestDto(ticket.getId(), buyer.getId(), seller.getId(), price, startDateTime,
                endDateTime));
        em.flush();
        em.clear();

        // when
        historyService.deleteHistory(result.getId());

        // then
        assertThrows(HistoryNotFoundException.class, () ->
            historyRepository.findById(result.getId()).orElseThrow(HistoryNotFoundException::new)
        );
    }

    @Test
    public void findPurchaseHistoryInfiniteScrollTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null);
        em.persist(ticket);
        int price = 3000;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime startDateTime = LocalDateTime.parse("202001010000", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("202003060000", formatter);
        for(int i=0; i<5; i++) {
            historyService.createHistory(new HistoryCreateRequestDto(ticket.getId(), buyer.getId(), seller.getId(), price + i, startDateTime,
                    endDateTime));
        }
        em.flush();
        em.clear();

        // when
        Slice<HistoryDto> result1 = historyService.findPurchaseHistoriesByUserId(buyer.getId(), null, 2);
        List<HistoryDto> content1 = result1.getContent();
        HistoryDto lastHistory1 = content1.get(content1.size() - 1);
        Slice<HistoryDto> result2 = historyService.findPurchaseHistoriesByUserId(buyer.getId(), lastHistory1.getId(), 2);
        List<HistoryDto> content2 = result2.getContent();
        HistoryDto lastHistory2 = content2.get(content2.size() - 1);
        Slice<HistoryDto> result3 = historyService.findPurchaseHistoriesByUserId(buyer.getId(), lastHistory2.getId(), 2);
        List<HistoryDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

    @Test
    public void findSalesHistoryInfiniteScrollTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null);
        em.persist(ticket);
        int price = 3000;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime startDateTime = LocalDateTime.parse("202001010000", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("202003060000", formatter);
        for(int i=0; i<5; i++) {
            historyService.createHistory(new HistoryCreateRequestDto(ticket.getId(), buyer.getId(), seller.getId(), price + i, startDateTime,
                    endDateTime));
        }
        em.flush();
        em.clear();

        // when
        Slice<HistoryDto> result1 = historyService.findSalesHistoriesByUserId(seller.getId(), null, 2);
        List<HistoryDto> content1 = result1.getContent();
        HistoryDto lastHistory1 = content1.get(content1.size() - 1);
        Slice<HistoryDto> result2 = historyService.findSalesHistoriesByUserId(seller.getId(), lastHistory1.getId(), 2);
        List<HistoryDto> content2 = result2.getContent();
        HistoryDto lastHistory2 = content2.get(content2.size() - 1);
        Slice<HistoryDto> result3 = historyService.findSalesHistoriesByUserId(seller.getId(), lastHistory2.getId(), 2);
        List<HistoryDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

}