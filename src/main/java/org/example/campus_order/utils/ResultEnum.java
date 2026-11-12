package org.example.campus_order.utils;

public enum ResultEnum {
    LOGIN_ERR(501,"账号密码错误"),
    TOKEN_ERR(502,"token失效，请重新登录");
    private Integer code;
    private String msg;
    ResultEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
    //getter
    public Integer getCode(){return code;}
    public String getMsg(){return msg;}
}
