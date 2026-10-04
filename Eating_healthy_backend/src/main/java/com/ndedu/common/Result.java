package com.ndedu.common;


import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    public static <T> Result<T> ok(T data){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(ResultCode.ERROR.getMessage());
        return result;
    }

    public static <T> Result<T> error(Integer code,String message,T data){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
