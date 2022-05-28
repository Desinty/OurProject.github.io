package com.reggie.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.R;
import com.reggie.dto.LoginFormDTO;
import com.reggie.entity.User;
import com.reggie.mapper.UserMapper;
import com.reggie.service.UserService;
import com.reggie.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @author XuLongjie
 * @create 2022-05-17-16:04
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public R<String> sendMsg(User user, HttpSession session) {
        // 1.验证手机号
        if (RegexUtils.isPhoneInvalid(user.getPhone())) {
            // 2.格式错误，返回错误
            return R.error("手机号码格式错误");
        }
        // 3.生成验证码
        String code = RandomUtil.randomNumbers(4);
        // 4.存入session
        session.setAttribute("code", code);
        // 5.发送验证码（模拟）
        log.info("验证码：{}",code);
        // 6.返回
        return R.success("手机验证码发送成功");
    }

    @Override
    public R<User> login(LoginFormDTO loginForm, HttpSession session) {
        // 1.验证手机号码
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 1.1.格式错误返回错误
            return R.error("手机号格式错误");
        }
        // 2.验证验证码
        String loginFormCode = loginForm.getCode();
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode == null || !sessionCode.equals(loginFormCode)) {
            // 2.1.验证码错误返回错误
            return R.error("验证码错误");
        }
        // 3.根据手机号查询用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getPhone, phone);
        User user = getOne(userWrapper);
        // 4.判断用户是否存在
        if (user == null) {
            // 4.1.不存在则注册用户并保存
            user = new User();
            user.setPhone(phone);
            save(user);
        }
        // 5.存入session
        session.setAttribute("user", user.getId());
        // 6.返回结果
        return R.success(user);
    }
}
