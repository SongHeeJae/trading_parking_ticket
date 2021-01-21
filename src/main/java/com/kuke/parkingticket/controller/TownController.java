package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.town.TownCreateRequestDto;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.model.dto.user.UserDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.town.TownService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value = "Town Controller", tags = {"Town"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;
    private final ResponseService responseService;

    @ApiOperation(value = "해당 지역의 모든 동네 조회", notes = "해당 지역의 모든 동네를 조회한다.")
    @GetMapping(value = "/towns/{regionId}")
    public MultipleResult<TownDto> findTownsByRegion(@PathVariable("regionId") Long regionId) {
        return responseService.handleListResult(townService.findTownsByRegionId(regionId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "0access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "동네 등록", notes = "동네를 등록한다.")
    @PostMapping(value = "/towns")
    public SingleResult<TownDto> createTown(@RequestBody TownCreateRequestDto requestDto) {
        return responseService.handleSingleResult(townService.createTown(requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "0access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "동네 삭제", notes = "동네를 삭제한다.")
    @DeleteMapping(value = "/towns/{townId}")
    public Result deleteTown(@PathVariable("townId") Long townId) {
        townService.deleteTown(townId);
        return responseService.handleSuccessResult();
    }
}
