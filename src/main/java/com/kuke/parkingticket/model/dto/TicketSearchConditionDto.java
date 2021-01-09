package com.kuke.parkingticket.model.dto;

import com.kuke.parkingticket.entity.PlaceType;
import com.kuke.parkingticket.entity.TermType;
import com.kuke.parkingticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketSearchConditionDto {
    List<TermType> termTypes = new ArrayList<>();
    List<PlaceType> placeTypes = new ArrayList<>();
    List<TicketStatus> ticketStatuses = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyyMMddHHmm")
    LocalDateTime dateTime;

}
