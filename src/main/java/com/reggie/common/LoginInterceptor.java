package com.reggie.common;

import com.alibaba.fastjson.JSON;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断后台登陆系统是否被拦截
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null) {
            log.info("路径被放行：{}", request.getRequestURI());
            BaseContext.setCurrentId(empId);
            return true;
        }

        //判断移动端系统是否被拦截
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            log.info("路径被放行：{}", request.getRequestURI());
            BaseContext.setCurrentId(userId);
            return true;
        }

        log.info("路径被拦截：{}", request.getRequestURI());
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }
}