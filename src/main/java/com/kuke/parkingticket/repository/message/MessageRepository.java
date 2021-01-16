package com.kuke.parkingticket.repository.message;

import com.kuke.parkingticket.entity.History;
import com.kuke.parkingticket.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 유저의 발신 내역
    @Query("select m from Message m join fetch m.sender join fetch m.receiver " +
            "where m.sender.id = :userId and m.id < :lastMessageId order by m.createdAt desc")
    Slice<Message> findNextSentMessagesByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastMessageId") Long lastMessageId, Pageable pageable);

    // 유저의 수신 내역
    @Query("select m from Message m join fetch m.sender join fetch m.receiver " +
            "where m.receiver.id = :userId and m.id < :lastMessageId order by m.createdAt desc")
    Slice<Message> findNextReceivedMessagesByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastMessageId") Long lastMessageId, Pageable pageable);

}
