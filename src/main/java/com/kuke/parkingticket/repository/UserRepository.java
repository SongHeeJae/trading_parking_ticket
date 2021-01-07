package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
    Optional<User> findByNickname(String nickname);

    @Query("select u from User u join fetch Town t")
    List<User> findAllWithTown();

    @Query("select u from User u join fetch u.town where u.id = :id")
    Optional<User> findUser(@Param("id") Long id);

}
