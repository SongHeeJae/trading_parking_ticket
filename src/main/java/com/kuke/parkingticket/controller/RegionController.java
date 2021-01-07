package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.RegionDto;
import com.kuke.parkingticket.model.dto.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.UserLoginResponseDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.RegionService;
import com.kuke.parkingticket.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Region Controller", tags = {"Region"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final ResponseService responseService;

    @ApiOperation(value="모든 지역과 동 조회", notes = "모든 지역과 동을 조회 한다.")
    @GetMapping(value = "/regions")
    public MultipleResult<RegionDto> findAllRegionsWithTowns() {
        return responseService.handleListResult(regionService.findAllRegionsWithTowns());
    }
}
