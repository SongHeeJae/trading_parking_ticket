package com.kuke.parkingticket.service.cache;

import com.kuke.parkingticket.common.cache.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    @CacheEvict(value = CacheKey.COMMENTS, key = "#ticketId")
    public void deleteCommentsCache(Long ticketId) {
        log.debug("CacheService.deleteCommentsCache - ticketId {}", ticketId);
    }
}
