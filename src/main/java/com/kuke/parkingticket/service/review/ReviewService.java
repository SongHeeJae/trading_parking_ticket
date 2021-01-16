package com.kuke.parkingticket.service.review;

import com.kuke.parkingticket.advice.exception.ReviewAlreadyWrittenException;
import com.kuke.parkingticket.advice.exception.TicketNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.entity.Review;
import com.kuke.parkingticket.model.dto.review.ReviewCreateRequestDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.repository.review.ReviewRepository;
import com.kuke.parkingticket.repository.ticket.TicketRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private Review save;

    /**
     * 사용자가 작성한 리뷰. 마지막 리뷰 아이디 다음 것부터 limit 개수 만큼 가져옴
     */
    @Cacheable(value = CacheKey.TYPING_REVIEWS, key = "{#userId, #limit, #lastReviewId}")
    public Slice<ReviewDto> findTypingReviewsByUserId(Long userId, Long lastReviewId, int limit) {
        return reviewRepository.findNextTypingReviewsByUserIdOrderByCreatedAt(userId, lastReviewId != null ? lastReviewId : Long.MAX_VALUE, PageRequest.of(0, limit))
                .map(r -> convertReviewToDto(r));
    }

    /**
     * 사용자에게 작성된 리뷰. 마지막 리뷰 아이디 다음 것부터 limit 개수 만큼 가져옴
     */
    @Cacheable(value = CacheKey.TYPED_REVIEWS, key = "{#userId, #limit, #lastReviewId}")
    public Slice<ReviewDto> findTypedReviewsByUserId(Long userId, Long lastReviewId, int limit) {
        return reviewRepository.findNextTypedReviewsByUserIdOrderByCreatedAt(userId, lastReviewId != null ? lastReviewId : Long.MAX_VALUE, PageRequest.of(0, limit))
                .map(r -> convertReviewToDto(r));
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheKey.TYPED_REVIEWS, allEntries = true),
            @CacheEvict(cacheNames = CacheKey.TYPING_REVIEWS, allEntries = true)
    })
    public ReviewDto createReview(ReviewCreateRequestDto requestDto) {
        validateDuplicateReviewBySameUser(requestDto.getTicketId(), requestDto.getBuyerId());
        save = reviewRepository.save(
                Review.createReview(requestDto.getContent(),
                        requestDto.getScore(),
                        userRepository.findById(requestDto.getBuyerId()).orElseThrow(UserNotFoundException::new),
                        userRepository.findById(requestDto.getSellerId()).orElseThrow(UserNotFoundException::new),
                        ticketRepository.findById(requestDto.getTicketId()).orElseThrow(TicketNotFoundException::new)));
        Review review = save;
        return convertReviewToDto(review);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheKey.TYPED_REVIEWS, allEntries = true),
            @CacheEvict(cacheNames = CacheKey.TYPING_REVIEWS, allEntries = true)
    })
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }


    private ReviewDto convertReviewToDto(Review review) {
        return new ReviewDto(review.getId(), review.getContent(), review.getScore(), review.getBuyer().getId(), review.getBuyer().getNickname(),
                review.getSeller().getId(), review.getSeller().getNickname(), review.getTicket().getId());
    }

    private void validateDuplicateReviewBySameUser(Long ticketId, Long buyerId) {
        Optional<Review> review = reviewRepository.findReviewByTicketIdAndBuyerId(ticketId, buyerId);
        if(review.isPresent()) throw new ReviewAlreadyWrittenException();
    }
}
