package com.kuke.parkingticket.repository.review;

import com.kuke.parkingticket.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {

    // 해당 유저가 작성한 리뷰
    @Query("select r from Review r join fetch r.seller join fetch r.buyer " +
            "where r.buyer.id = :userId and r.id < :lastReviewId order by r.createdAt desc")
    Slice<Review> findNextTypingReviewsByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastReviewId") Long lastReviewId, Pageable pageable);

    // 해당 유저에게 작성된 리뷰
    @Query("select r from Review r join fetch r.seller join fetch r.buyer " +
            "where r.seller.id = :userId and r.id < :lastReviewId order by r.createdAt desc")
    Slice<Review> findNextTypedReviewsByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastReviewId") Long lastReviewId, Pageable pageable);

    @Query("select r from Review r where r.ticket.id = :ticketId and r.buyer.id = :buyerId")
    Optional<Review> findReviewByTicketIdAndBuyerId(@Param("ticketId") Long ticketId, @Param("buyerId") Long buyerId);

}
