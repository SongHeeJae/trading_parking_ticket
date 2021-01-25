package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(value = TestConfig.class)
class TicketTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("TicketTest");
        em.persist(region);
        Town town = Town.createTown("TicketTest", region);
        em.persist(town);
        User user = User.createUser("TicketTest", "1234", "TicketTest", town, null);
        em.persist(user);
    }

    @Test
    public void ticketCascadePersistTest() {
        // given
        User findUser = em.createQuery("select u from User u where u.uid = :uid", User.class)
                .setParameter("uid", "TicketTest").getSingleResult();
        Town findTown = em.createQuery("select t from Town t where t.name = :name", Town.class)
                .setParameter("name", "TicketTest").getSingleResult();
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, findUser, findTown,
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON, null, null);
        Comment comment1 = Comment.createComment("content1", ticket, findUser, null);
        Comment comment2 = Comment.createComment("content2", ticket, findUser, null);

        // when
        ticket.addComment(comment1);
        ticket.addComment(comment2);
        em.persist(ticket);
        em.flush();
        em.clear();

        // then
        Ticket findTicket = em.find(Ticket.class, ticket.getId());
        assertThat(findTicket.getComments().size()).isEqualTo(2);
    }

}