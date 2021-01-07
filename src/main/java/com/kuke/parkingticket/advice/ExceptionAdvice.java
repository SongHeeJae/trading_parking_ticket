package com.kuke.parkingticket.advice;

import com.kuke.parkingticket.advice.exception.*;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {
    private final ResponseService responseService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result defaultException(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        return responseService.handleFailResult(-1000, "오류가 발생하였습니다.");
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result loginFailureException() {
        return responseService.handleFailResult(-1001, "로그인에 실패하였습니다.");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result userNotFoundException() {
        return responseService.handleFailResult(-1002, "유저를 찾을 수 없습니다.");
    }

    @ExceptionHandler(TownNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result townNotFoundException() {
        return responseService.handleFailResult(-1003, "동네를 찾을 수 없습니다.");
    }

    @ExceptionHandler(UserIdAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result userIdAlreadyExistsException() {
        return responseService.handleFailResult(-1004, "이미 등록된 유저 아이디입니다.");
    }

    @ExceptionHandler(UserNicknameAlreadyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result userNicknameAlreadyException() {
        return responseService.handleFailResult(-1005, "이미 등록된 유저 닉네임입니다.");
    }


}