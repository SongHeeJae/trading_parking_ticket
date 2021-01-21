package com.kuke.parkingticket.service.cache;

import com.kuke.parkingticket.common.cache.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    @CacheEvict(value = CacheKey.COMMENTS, key = "#ticketId")
    public void deleteCommentsCache(Long ticketId) {
        log.debug("CacheService.deleteCommentsCache - ticketId {}", ticketId);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheKey.SENT_MESSAGES, key = "#senderId", allEntries = true),
            @CacheEvict(value = CacheKey.RECEIVED_MESSAGES, key = "#receiverId", allEntries = true),
            @CacheEvict(value = CacheKey.MESSAGE, key = "#messageId")
    })
    public void deleteMessagesCache(Long messageId, Long senderId, Long receiverId) {
        log.debug("CacheService.deleteMessagesCache - messageId {}, senderId {}, receiverId {}", messageId, senderId, receiverId);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheKey.PURCHASE_HISTORIES, key = "#buyerId", allEntries = true),
            @CacheEvict(value = CacheKey.SALES_HISTORIES, key = "#sellerId", allEntries = true)
    })
    public void deleteHistoriesCache(Long buyerId, Long sellerId) {
        log.debug("CacheService.deleteHistoriesCache - buyerId {}, sellerId {}", buyerId, sellerId);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheKey.TYPING_REVIEWS, key = "#buyerId", allEntries = true),
            @CacheEvict(value = CacheKey.TYPED_REVIEWS, key = "#sellerId", allEntries = true)
    })
    public void deleteReviewsCache(Long buyerId, Long sellerId) {
        log.debug("CacheService.deleteReviewsCache - buyerId {}, sellerId {}", buyerId, sellerId);
    }


    @Caching(evict = {
            @CacheEvict(value = CacheKey.REGIONS),
            @CacheEvict(value = CacheKey.REGIONS_WITH_TOWNS),
            @CacheEvict(value = CacheKey.TOWNS, key = "#regionId", allEntries = true)
    })
    public void deleteTownsCache(Long regionId) {
        log.debug("CacheService.deleteTownsCache - regionId {}", regionId);
    }
}
