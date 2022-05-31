package com.reggie.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.LoginFormDTO;
import com.reggie.entity.User;
import com.reggie.mapper.UserMapper;
import com.reggie.service.UserService;
import com.reggie.util.MD5;
import com.reggie.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

/**
 * @author XuLongjie
 * @create 2022-05-17-16:04
 */
@Slf4j
@Service
@Transactional
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
//             4.1.不存在则注册用户并保存
            user = new User();
            user.setPhone(phone);
            save(user);
            session.setAttribute("user", user.getId());
            return R.userRegister(user);
        }
        // 5.存在，直接存入session
        session.setAttribute("user", user.getId());
        // 6.返回结果
        return R.success(user);
    }

    @Override
    public R<String> enroll(User user) {
        // 1.获取登录信息
        Long userId = BaseContext.getCurrentId();
        // 2.查询数据库，是否有该用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(userId != null, User::getId, userId);
        userWrapper.eq(User::getStatus, 1);
        User u = getOne(userWrapper);
        if (u == null) {
            // 3.不存在，返回错误
            return R.error("请重新确认个人信息");
        }
        // 4.存在，更新用户信息
        String password = user.getPassword();
        password = MD5.encrypt(password);
        u.setName(user.getName());
        u.setPassword(password);
        u.setUsername(user.getUsername());
        u.setIdNumber(user.getIdNumber());
        updateById(u);
        // 5.返回
        return R.success("个人信息更新成功");
    }

    @Override
    public R<User> userLogin(LoginFormDTO loginForm, HttpSession session) {
        // 1.密码加密
        String password = loginForm.getPassword();
        password = MD5.encrypt(password);
        // 2.根据用户名查询数据库
        String username = loginForm.getUsername();
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(StrUtil.isNotBlank(username), User::getUsername, username);
        User user = getOne(userWrapper);
        // 3.判断用户是否存在
        if (user == null) {
            // 4.不存在，返回错误
            return R.error("用户不存在");
        }
        // 5.判断密码是否正确
        if (!user.getPassword().equals(password)) {
            // 6.不正确，返回错误
            return R.error("密码错误");
        }
        // 7.判断用户账号是否被禁用
        if (user.getStatus() == 0) {
            // 8.是，返回错误
            return R.error("用户账号异常");
        }
        // 9.将用户id存入session
        session.setAttribute("user", user.getId());
        // 10.返回
        return R.success(user);
    }

}
