package com.kuke.parkingticket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate;

    // stomp 테스트 화면
    @GetMapping("/alarm/stomp")
    public String stompAlarm() {
        return "/stomp";
    }

    @MessageMapping("/{userId}")
    public void message(@DestinationVariable("userId") Long userId) {
        messagingTemplate.convertAndSend("/sub/" + userId, "alarm socket connection completed.");
    }
}
