package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.advice.exception.LoginFailureException;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.UserRegisterResponseDto;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.repository.UserRepository;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Api(value = "Sign Controller", tags = {"Sign"})
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
    private final ResponseService responseService;
    private final SignService signService;

    @ApiOperation(value="로그인", notes = "아이디와 비밀번호로 로그인을 한다.")
    @PostMapping(value = "/login")
    public SingleResult<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        return responseService.handleSingleResult(signService.loginUser(requestDto));
    }

    @ApiOperation(value="회원가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/register")
    public SingleResult<UserRegisterResponseDto> register (@RequestBody UserRegisterRequestDto requestDto) {
        return responseService.handleSingleResult(signService.registerUser(requestDto));
    }
}

