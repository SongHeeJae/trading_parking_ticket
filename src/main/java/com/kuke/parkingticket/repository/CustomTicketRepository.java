package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.model.dto.TicketSearchConditionDto;
import com.kuke.parkingticket.model.dto.TicketSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTicketRepository {
    Page<TicketSimpleDto> findAllTicketWithConditions(TicketSearchConditionDto conditionDto, Pageable pageable);
}
