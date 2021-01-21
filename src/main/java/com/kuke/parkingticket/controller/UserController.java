package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.user.UserDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value = "User Controller", tags = {"User"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ResponseService responseService;

    @ApiOperation(value = "모든 회원 조회", notes = "모든 회원을 조회한다.")
    @GetMapping(value = "/users")
    public MultipleResult<UserDto> findAllUsers() {
        return responseService.handleListResult(userService.findAll());
    }

    @ApiOperation(value = "단일 회원 조회", notes = "단일 회원을 조회한다.")
    @GetMapping(value = "/users/{userId}")
    public SingleResult<UserDto> findUser(@PathVariable("userId") Long userId) {
        return responseService.handleSingleResult(userService.findUser(userId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PutMapping(value = "/users/{userId}")
    public SingleResult<UserDto> updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequestDto requestDto) {
        return responseService.handleSingleResult(userService.updateUser(userId, requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원을 삭제한다.")
    @DeleteMapping(value = "/users/{userId}")
    public Result deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return responseService.handleSuccessResult();
    }

}
