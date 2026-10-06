package com.ndedu.Exception;

public class BussinessException extends RuntimeException {
    private Integer code;

    public BussinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BussinessException(String message,Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
