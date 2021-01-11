package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.review.ReviewCreateRequestDto;
import com.kuke.parkingticket.model.dto.review.ReviewDto;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.history.HistoryService;
import com.kuke.parkingticket.service.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@Api(value = "History Controller", tags = {"History"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final ResponseService responseService;

    @ApiOperation(value = "해당 유저의 판매 내역 조회", notes = "해당 유저의 판매 내역을 조회한다.")
    @GetMapping(value = "/histories/sale/{userId}")
    public SingleResult<Slice<HistoryDto>> findSalesHistoriesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam("lastHistoryId") Long lastHistoryId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(historyService.findSalesHistoriesByUserId(userId, lastHistoryId, limit));
    }

    @ApiOperation(value = "해당 유저의 구매 내역 조회", notes = "해당 유저의 구매 내역을 조회한다.")
    @GetMapping(value = "/histories/purchase/{userId}")
    public SingleResult<Slice<HistoryDto>> findPurchaseHistoriesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "lastHistoryId") Long lastHistoryId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(historyService.findPurchaseHistoriesByUserId(userId, lastHistoryId, limit));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "주차권 매매 내역 생성", notes = "주차권 매매 내역을 생성한다.")
    @PostMapping(value = "/histories")
    public SingleResult<HistoryDto> createHistory(@RequestBody HistoryCreateRequestDto requestDto) {
        return responseService.handleSingleResult(historyService.createHistory(requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "매매 내역 삭제", notes = "매매 내역을 삭제한다.")
    @DeleteMapping(value = "/histories/{historyId}")
    public Result deleteHistory(@PathVariable("historyId") Long historyId) {
        historyService.deleteHistory(historyId);
        return responseService.handleSuccessResult();
    }
}
