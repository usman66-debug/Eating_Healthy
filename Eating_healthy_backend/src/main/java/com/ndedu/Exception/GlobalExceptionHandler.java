package com.ndedu.Exception;

import com.ndedu.common.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //定义异常类型为MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        //从异常中获取所有的字段错误信息
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        //构建错误信息
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(","));

        return Result.error(400,message,null);
    }

    //处理业务异常
    @ExceptionHandler(BussinessException.class)
    public Result<?> handleBussinessException(BussinessException e){
        return Result.error(e.getCode(),e.getMessage(),null);
    }
}
