package com.kuke.parkingticket.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultipleResult<T> extends Result {
    private List<T> datas;
}
