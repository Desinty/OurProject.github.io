package com.reggie.common;

/**
 * 自定义业务异常类
 * @author XuLongjie
 * @create 2022-05-13-22:08
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
