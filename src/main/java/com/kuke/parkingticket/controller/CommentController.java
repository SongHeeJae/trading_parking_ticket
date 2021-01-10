package com.kuke.parkingticket.controller;

import com.kuke.parkingticket.model.dto.comment.CommentCreateRequestDto;
import com.kuke.parkingticket.model.dto.comment.CommentDto;
import com.kuke.parkingticket.model.response.MultipleResult;
import com.kuke.parkingticket.model.response.Result;
import com.kuke.parkingticket.model.response.SingleResult;
import com.kuke.parkingticket.service.ResponseService;
import com.kuke.parkingticket.service.comment.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value = "Comment Controller", tags = {"Comment"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;

    @ApiOperation(value = "주차권의 댓글 목록 조회", notes = "해당 주차권의 댓글 목록을 조회한다.")
    @GetMapping(value = "/comments/{ticketId}")
    public MultipleResult<CommentDto> findAllCommentsByTicketId(@PathVariable("ticketId") Long ticketId) {
        return responseService.handleListResult(commentService.findCommentsByTicketId(ticketId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성한다.")
    @PostMapping(value = "/comments")
    public SingleResult<CommentDto> createComment(@RequestBody CommentCreateRequestDto requestDto) {
        return responseService.handleSingleResult(commentService.createComment(requestDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access-token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.")
    @DeleteMapping(value = "/comments/{commentId}")
    public Result deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return responseService.handleSuccessResult();
    }




}
