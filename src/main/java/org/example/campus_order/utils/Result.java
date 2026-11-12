package org.example.campus_order.utils;

import lombok.Data;

@Data
public class Result <T>{

    //定义状态码常量
    private static final Integer SUCCESS_CODE = 200;
    private static final Integer ERROR_CODE = 500;
    private static final String   SUCCESS_MSG = "操作成功";


    private Integer code;
    private String msg;
    private T data;

    //全参构造
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    //无参构造
    public Result(){}

    //成功(带数据)
    public static <T> Result<T> success(T data) {
        return new Result<T>(SUCCESS_CODE, SUCCESS_MSG, data);
    }
    //成功(无数据)
    public static <T> Result<T> success() {
        return new Result<T>(SUCCESS_CODE, SUCCESS_MSG, null);
    }
    //成功(带数据和自定义消息)
    public static <T> Result<T> success(T data, String msg) {
        return new Result<T>(SUCCESS_CODE, msg, data);
    }

    //失败
    public static <T> Result<T> error(String msg) {
        return new Result<T>(ERROR_CODE, msg, null);
    }
}