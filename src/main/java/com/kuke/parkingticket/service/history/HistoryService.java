package com.kuke.parkingticket.service.history;

import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.entity.History;
import com.kuke.parkingticket.entity.Review;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;

    /**
     * 사용자가 판매한 내역. 마지막 내역 아이디 다음 것부터 limit 개수 만큼 가져옴
     */
    @Cacheable(value = CacheKey.SALES_HISTORIES, key = "{#userId, #limit, #lastHistoryId}")
    public Slice<HistoryDto> findSalesHistoriesByUserId(Long userId, Long lastHistoryId, int limit) {
        return historyRepository.findNextSalesHistoriesByUserIdOrderByCreatedAt(userId, lastHistoryId != null ? lastHistoryId : Long.MAX_VALUE, PageRequest.of(0, limit))
                .map(h -> convertHistoryToDto(h));
    }

    /**
     * 사용자에게 구매한 내역. 마지막 내역 아이디 다음 것부터 limit 개수 만큼 가져옴
     */
    @Cacheable(value = CacheKey.PURCHASE_HISTORIES, key = "{#userId, #limit, #lastHistoryId}")
    public Slice<HistoryDto> findPurchaseHistoriesByUserId(Long userId, Long lastHistoryId, int limit) {
        return historyRepository.findNextPurchaseHistoriesByUserIdOrderByCreatedAt(userId, lastHistoryId != null ? lastHistoryId : Long.MAX_VALUE, PageRequest.of(0, limit))
                .map(h -> convertHistoryToDto(h));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.PURCHASE_HISTORIES, key = "#requestDto.buyerId", allEntries = true),
            @CacheEvict(value = CacheKey.SALES_HISTORIES, key = "#requestDto.sellerId", allEntries = true)
    })
    public HistoryDto createHistory(HistoryCreateRequestDto requestDto) {
        History history = historyRepository.save(
                History.createHistory(
                requestDto.getPrice(),
                ticketRepository.findById(requestDto.getTicketId()).orElseThrow(TicketNotFoundException::new),
                userRepository.findById(requestDto.getBuyerId()).orElseThrow(UserNotFoundException::new),
                userRepository.findById(requestDto.getSellerId()).orElseThrow(UserNotFoundException::new),
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime()
        ));
        return convertHistoryToDto(history);
    }

    @Transactional
    public void deleteHistory(Long historyId) {
        History history = historyRepository.findById(historyId).orElseThrow(HistoryNotFoundException::new);
        cacheService.deleteHistoriesCache(history.getBuyer().getId(), history.getSeller().getId());
        historyRepository.deleteById(historyId);
    }

    private HistoryDto convertHistoryToDto(History history) {
        return new HistoryDto(
              history.getId(),
              history.getPrice(),
              history.getTicket().getId(),
              history.getTicket().getAddress(),
              history.getBuyer().getId(),
                history.getBuyer().getNickname(),
                history.getSeller().getId(),
                history.getSeller().getNickname(),
                history.getStartDateTime(),
                history.getEndDateTime(),
                history.getCreatedAt()
        );
    }
}
