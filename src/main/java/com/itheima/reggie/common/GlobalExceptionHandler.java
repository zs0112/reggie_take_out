package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<String> exp(Exception exception){
        log.error(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")){
            String[] s = exception.getMessage().split(" ");
            String string = s[9]+"已存在";
            return R.error(string);
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> exp1(CustomException exception){
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
