package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.message.MessageCreateRequestDto;
import com.kuke.parkingticket.model.dto.message.MessageDto;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.alarm.AlarmService;
import com.kuke.parkingticket.service.message.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@Api(value = "Message Controller", tags = {"Message"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final ResponseService responseService;
    private final MessageService messageService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "메시지 전송", notes = "메세지를 전송한다.")
    @PostMapping(value = "/messages")
    public SingleResult<MessageDto> sendMessage(@RequestBody MessageCreateRequestDto requestDto) {
        MessageDto messageDto = messageService.createMessage(requestDto);
        return responseService.handleSingleResult(messageDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "해당 유저의 메시지 전송 내역 조회", notes = "해당 유저의 메시지 전송 내역을 조회한다.")
    @GetMapping(value = "/messages/sent/{userId}")
    public SingleResult<Slice<MessageDto>> findSentMessagesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(messageService.findSentMessagesByUserId(userId, lastMessageId, limit));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "해당 유저의 메시지 수신 내역 조회", notes = "해당 유저의 메시지 수신 내역을 조회한다.")
    @GetMapping(value = "/messages/received/{userId}")
    public SingleResult<Slice<MessageDto>> findReceivedMessagesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @RequestParam(value = "limit", defaultValue = "15") int limit ) {
        return responseService.handleSingleResult(messageService.findReceivedMessagesByUserId(userId, lastMessageId, limit));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "메세지 삭제", notes = "메세지를 삭제한다.")
    @DeleteMapping(value = "/messages/{messageId}")
    public Result deleteMessage(@PathVariable("messageId") Long messageId) {
        messageService.deleteMessage(messageId);
        return responseService.handleSuccessResult();
    }
}
