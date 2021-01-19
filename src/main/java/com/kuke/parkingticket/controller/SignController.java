package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.advice.exception.InvalidateProviderException;
import com.kuke.parkingticket.model.dto.user.*;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.social.KakaoService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.web.bind.annotation.*;

@Api(value = "Sign Controller", tags = {"Sign"})
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
    private final ResponseService responseService;
    private final SignService signService;
    private final KakaoService kakaoService;

    @ApiOperation(value="로그인", notes = "아이디와 비밀번호로 로그인을 한다.")
    @PostMapping(value = "/login")
    public SingleResult<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        return responseService.handleSingleResult(signService.loginUser(requestDto));
    }

    @ApiOperation(value="회원가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/register")
    public SingleResult<UserRegisterResponseDto> register(@RequestBody UserRegisterRequestDto requestDto) {
        return responseService.handleSingleResult(signService.registerUser(requestDto));
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃을 한다")
    @PostMapping(value = "/logout")
    public Result logout(@RequestHeader(value="X-AUTH-TOKEN") String token) {
        signService.logoutUser(token);
        return responseService.handleSuccessResult();
    }

    @ApiOperation(value = "토큰 재발급", notes = "토큰을 재발급한다")
    @PostMapping(value = "/refresh")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "REFRESH-TOKEN", value = "refresh-token", required = true, dataType = "String", paramType = "header")
    })
    public SingleResult<UserLoginResponseDto> refreshToken(
            @RequestHeader(value="X-AUTH-TOKEN") String token,
            @RequestHeader(value="REFRESH-TOKEN") String refreshToken ) {
        return responseService.handleSingleResult(signService.refreshToken(token, refreshToken));
    }

    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
    @PostMapping(value = "/social/login/{provider}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "SOCIAL-ACCESS-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    public SingleResult<UserLoginResponseDto> loginByProvider(
            @RequestHeader("SOCIAL-ACCESS-TOKEN") String accessToken,
            @PathVariable("provider") String provider) {
        return responseService.handleSingleResult(signService.loginUserByProvider(accessToken, provider));
    }

    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
    @PostMapping(value = "/social/register/{provider}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "SOCIAL-ACCESS-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    public SingleResult<UserRegisterResponseDto> registerByProvider(
            @RequestHeader("SOCIAL-ACCESS-TOKEN") String accessToken,
            @PathVariable("provider") String provider,
            @RequestBody UserRegisterByProviderRequestDto requestDto) {
        return responseService.handleSingleResult(signService.registerUserByProvider(requestDto, accessToken, provider));
    }
}