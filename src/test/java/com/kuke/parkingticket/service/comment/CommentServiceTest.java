package com.kuke.parkingticket.service.comment;

import com.kuke.parkingticket.advice.exception.CommentNotFoundException;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.comment.CommentDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.comment.CommentRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import com.kuke.parkingticket.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired CommentService commentService;
    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired TicketRepository ticketRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("CommentServiceTest");
        em.persist(region);
        Town town = Town.createTown("CommentServiceTest", region);
        em.persist(town);
        signService.registerUser(new UserRegisterRequestDto("CommentServiceTest", "1234", "cst", town.getId()));
    }

    @Test
    public void nestedStructureTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        Comment comment1 = Comment.createComment("content1", ticket, user, null);
        em.persist(comment1);
        Comment comment2 = Comment.createComment("content2", ticket, user, comment1);
        em.persist(comment2);
        Comment comment3 = Comment.createComment("content3", ticket, user, comment1);
        em.persist(comment3);
        Comment comment4 = Comment.createComment("content4", ticket, user, comment2);
        em.persist(comment4);
        Comment comment5 = Comment.createComment("content5", ticket, user, comment2);
        em.persist(comment5);
        Comment comment6 = Comment.createComment("content6", ticket, user, comment4);
        em.persist(comment6);
        Comment comment7 = Comment.createComment("content7", ticket, user, comment3);
        em.persist(comment7);
        Comment comment8 = Comment.createComment("content8", ticket, user, null);
        em.persist(comment8);
        /**
         * 1
         *  2
         *   4
         *    6
         *   5
         *  3
         *   7
         * 8 의 댓글 구조
         */
        em.flush();
        em.clear();

        // when
        Ticket findTicket = ticketRepository.findById(ticket.getId()).orElseThrow(TicketNotFoundException::new);
        List<CommentDto> result = commentService.findCommentsByTicketId(findTicket.getId());

        // then
        assertThat(result.size()).isEqualTo(2); // 최상위 댓글
        assertThat(result.get(0).getChildren().size()).isEqualTo(2); // 1의 children
        assertThat(result.get(0).getChildren().get(0).getChildren().size()).isEqualTo(2); // 2의 children
        assertThat(result.get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1); // 4의 children
        assertThat(result.get(0).getChildren().get(0).getChildren().get(0)
                .getChildren().get(0).getChildren().size()).isEqualTo(0); // 6의 children
        assertThat(result.get(0).getChildren().get(0).getChildren().get(1).getChildren().size()).isEqualTo(0); // 5의 children
        assertThat(result.get(0).getChildren().get(1).getChildren().size()).isEqualTo(1); // 1의 children
        assertThat(result.get(0).getChildren().get(1).getChildren().get(0).getChildren().size()).isEqualTo(0); // 7의 children
        assertThat(result.get(1).getChildren().size()).isEqualTo(0); // 8의 children
    }

    @Test
    public void deleteStatusTest() {
        /**
         * 1
         *  2
         * 의 구조에서 1을 삭제하면, 자식 댓글이 있으므로 isDeleted == 'Y'로 변함
         */

        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        CommentDto comment1 = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        CommentDto comment2 = commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), comment1.getId()));
        em.flush();
        em.clear();

        // when
        commentService.deleteComment(comment1.getId());
        em.flush();
        em.clear();
        Comment findComment1 = commentRepository.findCommentByIdWithParent(comment1.getId()).orElseThrow(CommentNotFoundException::new);
        Comment findComment2 = commentRepository.findCommentByIdWithParent(comment2.getId()).orElseThrow(CommentNotFoundException::new);

        // then
        assertThat(findComment1.getIsDeleted()).isEqualTo(DeleteStatus.Y);
        assertThat(findComment1.getChildren().size()).isEqualTo(1);
        assertThat(findComment2.getParent().getId()).isEqualTo(findComment1.getId());
    }

    @Test
    public void deleteParentCascadeTest() {
        /**
         * 1
         *  2
         *   3
         *    4
         * 의 구조에서 2, 3을 삭제하고, 4를 삭제하면 1만 남음
         */
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        CommentDto comment1 = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        CommentDto comment2 = commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), comment1.getId()));
        CommentDto comment3 = commentService.createComment(new CommentCreateRequestDto("content3", ticket.getId(), user.getId(), comment2.getId()));
        CommentDto comment4 = commentService.createComment(new CommentCreateRequestDto("content4", ticket.getId(), user.getId(), comment3.getId()));
        em.flush();
        em.clear();
        
        // when
        commentService.deleteComment(comment2.getId());
        commentService.deleteComment(comment3.getId());
        commentService.deleteComment(comment4.getId());
        em.flush();
        em.clear();
        List<Comment> comments = em.createQuery("select c from Comment c where c.ticket.id = :ticketId", Comment.class)
                .setParameter("ticketId", ticket.getId()).getResultList();

        // then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getContent()).isEqualTo("content1");
    }

    @Test
    public void deletedCommentContentTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = Ticket.createTicket("", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        em.persist(ticket);
        CommentDto comment1 = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), comment1.getId()));
        em.flush();
        em.clear();
        // when
        commentService.deleteComment(comment1.getId());
        em.flush();
        em.clear();
        List<CommentDto> result = commentService.findCommentsByTicketId(ticket.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getContent()).isEqualTo("삭제된 댓글입니다.");
    }

}