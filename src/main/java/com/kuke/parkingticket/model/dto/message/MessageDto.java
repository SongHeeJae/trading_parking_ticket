package com.kuke.parkingticket.model.dto.message;

import com.kuke.parkingticket.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto implements Serializable {
    private Long id;
    private Long senderId;
    private String senderNickname;
    private Long receiverId;
    private String receiverNickname;
    private String message;
    private ReadingStatus readingStatus;
    LocalDateTime createdAt;
}
