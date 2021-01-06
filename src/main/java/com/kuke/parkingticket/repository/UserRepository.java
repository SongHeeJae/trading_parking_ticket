package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
    Optional<User> findByUidOrNickname(String uid, String nickname);
    Optional<User> findByNickname(String nickname);
}
