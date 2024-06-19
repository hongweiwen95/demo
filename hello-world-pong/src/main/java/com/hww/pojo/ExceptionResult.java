package com.hww.pojo;

import com.hww.exception.CustomException;
import lombok.Getter;


@Getter
public class ExceptionResult {
    private Integer status;
    private String message;

    public ExceptionResult(CustomException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
    }

    public ExceptionResult(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
