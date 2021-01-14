package com.kuke.parkingticket.advice;

import com.kuke.parkingticket.advice.exception.*;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result ticketNotFoundException() {
        return responseService.handleFailResult(-1006, "해당 주차권을 찾을 수 없습니다.");
    }

    @ExceptionHandler(FileConvertException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result fileConvertException() {
        return responseService.handleFailResult(-1007, "파일 변환에 실패하였습니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result accessDeniedException() {
        return responseService.handleFailResult(-1008, "해당 권한이 없습니다.");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result authenticationEntryPointException() {
        return responseService.handleFailResult(-1009, "해당 권한이 없습니다.");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result commentNotFoundException() {
        return responseService.handleFailResult(-1010, "해당 댓글을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ReviewAlreadyWrittenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result reviewAlreadyWrittenException() {
        return responseService.handleFailResult(-1011, "리뷰가 이미 작성되었습니다.");
    }

    @ExceptionHandler(HistoryNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result historyNotFoundException() {
        return responseService.handleFailResult(-1012, "해당 내역을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result reviewNotFoundException() {
        return responseService.handleFailResult(-1013, "해당 리뷰를 찾을 수 없습니다.");
    }

    @ExceptionHandler(RegionNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result regionNotFoundException() {
        return responseService.handleFailResult(-1013, "해당 지역을 찾을 수 없습니다.");
    }

}
