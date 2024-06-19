package com.hww.handler;

import com.hww.exception.CustomException;
import com.hww.pojo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseEntity<Object> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }


}