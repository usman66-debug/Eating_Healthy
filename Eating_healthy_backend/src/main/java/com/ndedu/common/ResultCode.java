package com.ndedu.common;

public enum ResultCode {
    SUCCESS(200,"操作成功"),
    ERROR(-1,"操作失败"),
    UNAUTHORIZED(401,"未授权"),
    SYSTEM_ERROR(500,"系统错误");

    private Integer code;
    private String message;

    ResultCode(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
