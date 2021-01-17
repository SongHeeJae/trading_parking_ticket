package com.kuke.parkingticket.service.alarm;

import com.kuke.parkingticket.model.dto.message.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SimpMessageSendingOperations messagingTemplate;

    public void alarmByMessage(MessageDto messageDto) {
        messagingTemplate.convertAndSend("/sub/" + messageDto.getReceiverId(), messageDto);
    }

}
