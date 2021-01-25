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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired CommentService commentService;
    @Autowired SignService signService;
    @Autowired TicketRepository ticketRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired UserRepository userRepository;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired EntityManager em;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("CommentServiceTest"));
        Town town = townRepository.save(Town.createTown("CommentServiceTest", region));
        signService.registerUser(new UserRegisterRequestDto("CommentServiceTest", "1234", "cst", town.getId()));
    }

    @Test
    public void createCommentTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));

        // when
        CommentDto dto = commentService.createComment(new CommentCreateRequestDto("content", ticket.getId(), user.getId(), null));

        // then
        Comment result = commentRepository.findById(dto.getId()).orElseThrow(CommentNotFoundException::new);
        assertThat(result.getId()).isEqualTo(dto.getId());
        assertThat(result.getContent()).isEqualTo(dto.getContent());
        assertThat(result.getParent()).isNull();
        assertThat(result.getChildren().size()).isEqualTo(0);
        assertThat(result.getIsDeleted()).isEqualTo(DeleteStatus.N);
        assertThat(result.getTicket().getId()).isEqualTo(ticket.getId());
        assertThat(result.getWriter().getId()).isEqualTo(user.getId());
    }

    @Test
    public void createReplyCommentTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));
        CommentDto parentDto = commentService.createComment(new CommentCreateRequestDto("content", ticket.getId(), user.getId(), null));

        // when
        CommentDto childDto = commentService.createComment(new CommentCreateRequestDto("content", ticket.getId(), user.getId(), parentDto.getId()));
        em.flush();
        em.clear();

        // then
        Comment parent = commentRepository.findById(parentDto.getId()).orElseThrow(CommentNotFoundException::new);
        Comment child = commentRepository.findById(childDto.getId()).orElseThrow(CommentNotFoundException::new);
        assertThat(parent.getChildren().size()).isEqualTo(1);
        assertThat(child.getParent().getId()).isEqualTo(parent.getId());

    }

    @Test
    public void deleteCommentTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));
        CommentDto dto = commentService.createComment(new CommentCreateRequestDto("content", ticket.getId(), user.getId(), null));

        // when
        commentRepository.deleteById(dto.getId());

        // then
        assertThatThrownBy(() -> commentRepository.findById(dto.getId()).orElseThrow(CommentNotFoundException::new))
                .isInstanceOf(CommentNotFoundException.class);
    }


    @Test
    public void nestedStructureTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));
        CommentDto comment1 = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        CommentDto comment2 = commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), comment1.getId()));
        CommentDto comment3 = commentService.createComment(new CommentCreateRequestDto("content3", ticket.getId(), user.getId(), comment1.getId()));
        CommentDto comment4 = commentService.createComment(new CommentCreateRequestDto("content4", ticket.getId(), user.getId(), comment2.getId()));
        CommentDto comment5 = commentService.createComment(new CommentCreateRequestDto("content5", ticket.getId(), user.getId(), comment2.getId()));
        CommentDto comment6 = commentService.createComment(new CommentCreateRequestDto("content6", ticket.getId(), user.getId(), comment4.getId()));
        CommentDto comment7 = commentService.createComment(new CommentCreateRequestDto("content7", ticket.getId(), user.getId(), comment3.getId()));
        CommentDto comment8 = commentService.createComment(new CommentCreateRequestDto("content8", ticket.getId(), user.getId(), null));

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
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));

        CommentDto comment1 = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        CommentDto comment2 = commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), comment1.getId()));
        em.flush();
        em.clear();

        // when
        commentService.deleteComment(comment1.getId());
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
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));
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

        List<Comment> comments = commentRepository.findCommentByTicketId(ticket.getId());

        // then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getContent()).isEqualTo("content1");
    }

    @Test
    public void deletedCommentContentTest() {
        // given
        User user = userRepository.findByUid("CommentServiceTest").orElseThrow(UserNotFoundException::new);
        Ticket ticket = ticketRepository.save(Ticket.createTicket("test", "content", "address", 0, user, user.getTown(),
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null));
        CommentDto parent = commentService.createComment(new CommentCreateRequestDto("content1", ticket.getId(), user.getId(), null));
        commentService.createComment(new CommentCreateRequestDto("content2", ticket.getId(), user.getId(), parent.getId()));
        em.flush();
        em.clear();

        // when
        commentService.deleteComment(parent.getId());
        List<CommentDto> result = commentService.findCommentsByTicketId(ticket.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getContent()).isEqualTo("삭제된 댓글입니다.");
    }

}