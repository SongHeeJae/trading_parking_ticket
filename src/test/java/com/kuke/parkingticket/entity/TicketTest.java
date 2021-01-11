package com.kuke.parkingticket.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class TicketTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void beforeEach() {
        Region region = Region.createRegion("서울");
        em.persist(region);
        Town town = Town.createTown("희재동", region);
        em.persist(town);
        User user = User.createUser("gmlwo308", "1234", "희재희재", town);
        em.persist(user);
    }

    @Test
    public void ticketCascadePersistTest() {
        // given
        User findUser = em.createQuery("select u from User u where u.uid = :uid", User.class)
                .setParameter("uid", "gmlwo308").getSingleResult();
        Town findTown = em.createQuery("select t from Town t where t.name = :name", Town.class)
                .setParameter("name", "희재동").getSingleResult();
        Ticket ticket = Ticket.createTicket("title", "content", "address", 0, findUser, findTown,
                PlaceType.APARTMENT, TermType.DAY, TicketStatus.ON);
        Comment comment1 = Comment.createComment("content1", ticket, findUser, null);
        Comment comment2 = Comment.createComment("content2", ticket, findUser, null);
        Image image1 = Image.createImage("/name1", ticket);
        Image image2 = Image.createImage("/name2", ticket);
        // when
        ticket.addComment(comment1);
        ticket.addComment(comment2);
        ticket.addImage(image1);
        ticket.addImage(image2);
        em.persist(ticket);
        em.flush();
        em.clear();

        Ticket findTicket = em.find(Ticket.class, ticket.getId());
        // then
        assertThat(findTicket.getComments().size()).isEqualTo(2);
        assertThat(findTicket.getImages().size()).isEqualTo(2);
    }

}