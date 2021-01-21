package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.region.RegionCreateRequestDto;
import com.kuke.parkingticket.model.dto.region.RegionDto;
import com.kuke.parkingticket.model.dto.region.RegionWithTownDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.region.RegionService;
import com.kuke.parkingticket.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value = "Region Controller", tags = {"Region"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final ResponseService responseService;

    @ApiOperation(value="모든 지역과 동네 조회", notes = "모든 지역과 동네를 조회한다.")
    @GetMapping(value = "/regions/with-towns")
    public MultipleResult<RegionWithTownDto> findAllRegionsWithTowns() {
        return responseService.handleListResult(regionService.findAllRegionsWithTowns());
    }

    @ApiOperation(value="모든 지역 조회", notes = "모든 지역을 조회한다.")
    @GetMapping(value = "/regions")
    public MultipleResult<RegionDto> findAllRegions() {
        return responseService.handleListResult(regionService.findAllRegions());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "지역 등록", notes = "지역을 등록한다.")
    @PostMapping(value = "/regions")
    public SingleResult<RegionDto> createRegion(@RequestBody RegionCreateRequestDto requestDto) {
        return responseService.handleSingleResult(regionService.createRegion(requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "지역 삭제", notes = "지역을 삭제한다.")
    @DeleteMapping(value = "/regions/{regionId}")
    public Result deleteRegion(@PathVariable("regionId") Long regionId) {
        regionService.deleteRegion(regionId);
        return responseService.handleSuccessResult();
    }
}
