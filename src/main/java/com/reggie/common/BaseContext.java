package com.reggie.common;

/**
 * 基于ThreadLocal 封装工具类，用户保存和获取当前登录用户id
 * @author XuLongjie
 * @create 2022-05-13-16:34
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
