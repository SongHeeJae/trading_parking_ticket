package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.service.TicketService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Ticket Controller", tags = {"Ticket"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
}
