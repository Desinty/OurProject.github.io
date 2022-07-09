package com.reggie.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.reggie.common.BaseContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.concurrent.TimeUnit;

import static com.reggie.util.RedisConstants.LOGIN_EMP_TTL;

/**
 * @author XuLongjie
 * @create 2022-07-09-22:49
 */

@Component
public class EmpLoginInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String tokenKey = request.getHeader("token");
        if (StrUtil.isBlank(tokenKey)) {
            response.setStatus(401);
            return false;
        }
        // 获取redis中的token值
        String tokenValue = stringRedisTemplate.opsForValue().get(tokenKey);
        if (tokenValue == null) {
            response.sendError(401);
            return false;
        }

        // 将信息保存在ThreadLocal中
        Long empId = Long.valueOf(tokenValue);
        BaseContext.setCurrentId(empId);
        stringRedisTemplate.expire(tokenKey, LOGIN_EMP_TTL, TimeUnit.DAYS);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
