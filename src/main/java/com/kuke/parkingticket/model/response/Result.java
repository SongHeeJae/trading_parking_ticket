package com.kuke.parkingticket.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private boolean success; // 응답 성공 여부
    private int code; // 응답 코드
    private String msg; // 응답 메시지
}
