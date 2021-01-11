package com.kuke.parkingticket.service.review;

import com.kuke.parkingticket.advice.exception.ReviewAlreadyWrittenException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.review.ReviewCreateRequestDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.comment.CommentRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.comment.CommentService;
import com.kuke.parkingticket.service.sign.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {


    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired TicketRepository ticketRepository;
    @Autowired ReviewService reviewService;
    @Autowired UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("ReviewServiceTest");
        em.persist(region);
        Town town = Town.createTown("ReviewServiceTest", region);
        em.persist(town);
        signService.registerUser(new UserRegisterRequestDto("buyer1", "1234", "buyer1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("buyer2", "1234", "buyer2", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("buyer3", "1234", "buyer3", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("buyer4", "1234", "buyer4", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("buyer5", "1234", "buyer5", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller1", "1234", "seller1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller2", "1234", "seller2", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller3", "1234", "seller3", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller4", "1234", "seller4", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("seller5", "1234", "seller5", town.getId()));
    }

    @Test
    public void validateDuplicateReviewBySameUserTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        reviewService.createReview(new ReviewCreateRequestDto("review1", 5, buyer.getId(), seller.getId(), ticket.getId()));

        // when, then
        assertThrows(ReviewAlreadyWrittenException.class, () ->
                reviewService.createReview(new ReviewCreateRequestDto("review1", 5, buyer.getId(), seller.getId(), ticket.getId())));
    }

    @Test
    @Rollback(false)
    public void typedReviewInfiniteScrollTest() {
        // given
        User buyer1 = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User buyer2 = userRepository.findByUid("buyer2").orElseThrow(UserNotFoundException::new);
        User buyer3 = userRepository.findByUid("buyer3").orElseThrow(UserNotFoundException::new);
        User buyer4 = userRepository.findByUid("buyer4").orElseThrow(UserNotFoundException::new);
        User buyer5 = userRepository.findByUid("buyer5").orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, seller, seller.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        reviewService.createReview(new ReviewCreateRequestDto("review1", 5, buyer1.getId(), seller.getId(), ticket.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review2", 5, buyer2.getId(), seller.getId(), ticket.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review3", 5, buyer3.getId(), seller.getId(), ticket.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review4", 5, buyer4.getId(), seller.getId(), ticket.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review5", 5, buyer5.getId(), seller.getId(), ticket.getId()));

        // when
        Slice<ReviewDto> result1 = reviewService.findTypedReviewsByUserId(seller.getId(), null, 2);
        List<ReviewDto> content1 = result1.getContent();
        ReviewDto lastReview1 = content1.get(content1.size() - 1);
        Slice<ReviewDto> result2 = reviewService.findTypedReviewsByUserId(seller.getId(), lastReview1.getId(), 2);
        List<ReviewDto> content2 = result2.getContent();
        ReviewDto lastReview2 = content2.get(content2.size() - 1);
        Slice<ReviewDto> result3 = reviewService.findTypedReviewsByUserId(seller.getId(), lastReview2.getId(), 2);
        List<ReviewDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

    @Test
    public void typingReviewInfiniteScrollTest() {
        // given
        User buyer = userRepository.findByUid("buyer1").orElseThrow(UserNotFoundException::new);
        User seller1 = userRepository.findByUid("buyer2").orElseThrow(UserNotFoundException::new);
        User seller2 = userRepository.findByUid("buyer3").orElseThrow(UserNotFoundException::new);
        User seller3 = userRepository.findByUid("buyer4").orElseThrow(UserNotFoundException::new);
        User seller4 = userRepository.findByUid("buyer5").orElseThrow(UserNotFoundException::new);
        User seller5 = userRepository.findByUid("seller1").orElseThrow(UserNotFoundException::new);
        Ticket ticket1 = Ticket.createTicket("title", "content", "address", 0, seller1, seller1.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        Ticket ticket2 = Ticket.createTicket("title", "content", "address", 0, seller2, seller2.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        Ticket ticket3 = Ticket.createTicket("title", "content", "address", 0, seller3, seller3.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        Ticket ticket4 = Ticket.createTicket("title", "content", "address", 0, seller4, seller4.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        Ticket ticket5 = Ticket.createTicket("title", "content", "address", 0, seller5, seller5.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket1);
        em.persist(ticket2);
        em.persist(ticket3);
        em.persist(ticket4);
        em.persist(ticket5);
        reviewService.createReview(new ReviewCreateRequestDto("review1", 5, buyer.getId(), seller1.getId(), ticket1.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review2", 5, buyer.getId(), seller2.getId(), ticket2.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review3", 5, buyer.getId(), seller3.getId(), ticket3.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review4", 5, buyer.getId(), seller4.getId(), ticket4.getId()));
        reviewService.createReview(new ReviewCreateRequestDto("review5", 5, buyer.getId(), seller5.getId(), ticket5.getId()));

        // when
        Slice<ReviewDto> result1 = reviewService.findTypingReviewsByUserId(buyer.getId(), null, 2);
        List<ReviewDto> content1 = result1.getContent();
        ReviewDto lastReview1 = content1.get(content1.size() - 1);
        Slice<ReviewDto> result2 = reviewService.findTypingReviewsByUserId(buyer.getId(), lastReview1.getId(), 2);
        List<ReviewDto> content2 = result2.getContent();
        ReviewDto lastReview2 = content2.get(content2.size() - 1);
        Slice<ReviewDto> result3 = reviewService.findTypingReviewsByUserId(buyer.getId(), lastReview2.getId(), 2);
        List<ReviewDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

}