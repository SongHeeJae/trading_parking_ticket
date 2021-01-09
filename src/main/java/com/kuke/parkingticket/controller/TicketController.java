package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.ticket.*;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.ticket.TicketService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(value = "Ticket Controller", tags = {"Ticket"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final ResponseService responseService;

    @ApiOperation(value = "주차권 글 목록 조회", notes = "주차권 글 목록을 조회한다.")
    @GetMapping(value = "/tickets")
    public SingleResult<Page<TicketSimpleDto>> findAllTickets(@NotNull Pageable pageable, TicketSearchConditionDto conditionDto){
        return responseService.handleSingleResult(ticketService.findAllTickets(conditionDto, pageable));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "주차권 글 작성", notes = "주차권 글을 작성한다.")
    @PostMapping(value = "/tickets", headers = "content-type=multipart/form-data")
    public SingleResult<TicketDto> createTicket(
            @RequestParam(value = "file", required = false) List<MultipartFile> files,
            @ModelAttribute TicketCreateRequestDto requestDto ) {
        return responseService.handleSingleResult(ticketService.createTicket(files, requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "주차권 글 수정", notes = "주차권 글을 수정한다.")
    @PutMapping(value = "/tickets/{ticketId}")
    public Result updateTicket(
            @PathVariable("ticketId") Long ticketId,
            @RequestParam(value = "file", required = false) List<MultipartFile> files,
            @ModelAttribute TicketUpdateRequestDto requestDto ) {
        ticketService.updateTicket(ticketId, files, requestDto);
        return responseService.handleSuccessResult();
    }

    @ApiOperation(value = "주차권 글 조회", notes = "주차권 글을 조회한다.")
    @GetMapping(value = "/tickets/{ticketId}")
    public SingleResult<TicketDto> readTicket(@PathVariable("ticketId") Long ticketId) {
        return responseService.handleSingleResult(ticketService.readTicket(ticketId));
    }

    @ApiOperation(value = "주차권 글 삭제", notes = "주차권 글을 삭제한다.")
    @DeleteMapping(value = "/tickets/{ticketId}")
    public Result deleteTicket(@PathVariable("ticketId") Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return responseService.handleSuccessResult();
    }
}
