package com.kuke.parkingticket.repository.history;

import com.kuke.parkingticket.entity.History;
import com.kuke.parkingticket.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {

    // 유저의 구매 내역
    @Query("select h from History h join fetch h.seller join fetch h.buyer join fetch h.ticket " +
            "where h.buyer.id = :userId and h.id < :lastHistoryId order by h.createdAt desc")
    Slice<History> findNextPurchaseHistoriesByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastHistoryId") Long lastHistoryId, Pageable pageable);

    // 유저의 판매내역
    @Query("select h from History h join fetch h.seller join fetch h.buyer join fetch h.ticket " +
            "where h.seller.id = :userId and h.id < :lastHistoryId order by h.createdAt desc")
    Slice<History> findNextSalesHistoriesByUserIdOrderByCreatedAt(@Param("userId") Long userId, @Param("lastHistoryId") Long lastHistoryId, Pageable pageable);

}
