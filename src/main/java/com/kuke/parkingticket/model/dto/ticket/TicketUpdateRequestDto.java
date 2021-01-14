package com.kuke.parkingticket.model.dto.ticket;

import com.kuke.parkingticket.entity.PlaceType;
import com.kuke.parkingticket.entity.TermType;
import com.kuke.parkingticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdateRequestDto {
    List<MultipartFile> files = new ArrayList<>();
    private String title;
    private String content;
    private int price;
    private String address;
    @DateTimeFormat(pattern = "yyyyMMddHHmm")
    private LocalDateTime startDateTime;
    @DateTimeFormat(pattern = "yyyyMMddHHmm")
    private LocalDateTime endDateTime;

    private TermType termType;
    private TicketStatus ticketStatus;
    private PlaceType placeType;
}
