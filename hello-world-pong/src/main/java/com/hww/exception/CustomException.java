package com.hww.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private Integer status; //状态码

    public CustomException(Integer status, String message) {
        super(message);
        this.status = status;
    }

}