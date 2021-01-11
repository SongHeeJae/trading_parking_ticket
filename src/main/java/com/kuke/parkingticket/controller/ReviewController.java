package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.comment.CommentDto;
import com.kuke.parkingticket.model.dto.review.ReviewCreateRequestDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.comment.CommentService;
import com.kuke.parkingticket.service.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@Api(value = "Review Controller", tags = {"Review"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ResponseService responseService;


    @ApiOperation(value = "해당 유저가 작성한 리뷰 목록 조회", notes = "해당 유저가 작성한 리뷰 목록을 조회한다.")
    @GetMapping(value = "/reviews/typing/{userId}")
    public SingleResult<Slice<ReviewDto>> findTypingReviewByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam("lastReviewId") Long lastReviewId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(reviewService.findTypingReviewsByUserId(userId, lastReviewId, limit));
    }

    @ApiOperation(value = "해당 유저에게 작성된 리뷰 목록 조회", notes = "해당 유저에게 작성된 리뷰 목록을 조회한다.")
    @GetMapping(value = "/reviews/typed/{userId}")
    public SingleResult<Slice<ReviewDto>> findTypedReviewByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "lastReviewId") Long lastReviewId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(reviewService.findTypedReviewsByUserId(userId, lastReviewId, limit));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "리뷰 작성", notes = "리뷰를 작성한다.")
    @PostMapping(value = "/reviews")
    public SingleResult<ReviewDto> createReview(@RequestBody ReviewCreateRequestDto requestDto) {
        return responseService.handleSingleResult(reviewService.createReview(requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "리뷰 삭제", notes = "리뷰를 삭제한다.")
    @DeleteMapping(value = "/reviews/{reviewId}")
    public Result deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return responseService.handleSuccessResult();
    }
}
