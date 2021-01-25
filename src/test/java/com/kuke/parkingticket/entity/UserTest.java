package com.kuke.parkingticket.entity;

import com.kuke.parkingticket.TestConfig;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
class UserTest {
    @Autowired EntityManager em;

    @Test
    public void addRoleTest() {

        // given
        em.persist(User.createUser("uid", "pwd", "nickname", null, null));
        em.flush();
        em.clear();

        // when
        User user = em.createQuery("select u from User u where u.uid = :uid", User.class)
                .setParameter("uid", "uid")
                .getSingleResult();
        user.addRole(Role.ROLE_ADMIN);
        em.flush();
        em.clear();

        // that
        User result = em.createQuery("select u from User u where u.id = :id", User.class)
                .setParameter("id", user.getId())
                .getSingleResult();
        assertThat(result.getRoles().size()).isEqualTo(2);
        assertThat(result.getRoles().get(0)).isEqualTo(Role.ROLE_NORMAL);
        assertThat(result.getRoles().get(1)).isEqualTo(Role.ROLE_ADMIN);
    }

}