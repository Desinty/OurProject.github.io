package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.dto.LoginFormDTO;
import com.reggie.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @author XuLongjie
 * @create 2022-05-17-16:04
 */
public interface UserService extends IService<User> {
    R<String> sendMsg(User user, HttpSession session);

    R<User> login(LoginFormDTO loginForm, HttpSession session);


    R<String> enroll(User user);

    R<User> userLogin(LoginFormDTO loginForm, HttpSession session);
}
