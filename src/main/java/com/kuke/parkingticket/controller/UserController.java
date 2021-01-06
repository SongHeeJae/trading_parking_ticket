package com.kuke.parkingticket.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "User Controller", tags = {"User"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @ApiOperation(value = "모든 회원 조회", notes = "모든 회원을 조회한다.")
    @GetMapping(value = "/users")
    public String swaggerTest() {
        return "test";
    }

}
