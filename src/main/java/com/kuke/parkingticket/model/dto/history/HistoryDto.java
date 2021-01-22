package com.kuke.parkingticket.model.dto.history;

import com.kuke.parkingticket.entity.History;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto implements Serializable {
    private Long id;
    private int price;
    private Long ticketId;
    private String address;
    private Long buyerId;
    private String buyerNickname;
    private Long sellerId;
    private String sellerNickname;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime createdAt;

    public static HistoryDto convertHistoryToDto(History history) {
        return new HistoryDto(
                history.getId(),
                history.getPrice(),
                history.getTicket().getId(),
                history.getTicket().getAddress(),
                history.getBuyer().getId(),
                history.getBuyer().getNickname(),
                history.getSeller().getId(),
                history.getSeller().getNickname(),
                history.getStartDateTime(),
                history.getEndDateTime(),
                history.getCreatedAt()
        );
    }
}
