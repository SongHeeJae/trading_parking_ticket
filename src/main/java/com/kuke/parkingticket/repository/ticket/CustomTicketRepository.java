package com.kuke.parkingticket.repository.ticket;

import com.kuke.parkingticket.model.dto.ticket.TicketSearchConditionDto;
import com.kuke.parkingticket.model.dto.ticket.TicketSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTicketRepository {
    Page<TicketSimpleDto> findAllTicketWithConditions(TicketSearchConditionDto conditionDto, Pageable pageable);
}
