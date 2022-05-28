package com.reggie.controller;

import com.reggie.common.R;
import com.reggie.dto.LoginFormDTO;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author XuLongjie
 * @create 2022-05-17-16:05
 */
@Api(tags = "用户控制器")
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("发送手机验证码")
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        return userService.sendMsg(user, session);
    }

    @ApiOperation("登录功能")
    @PostMapping("/login")
    public R<User> login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        log.info("loginForm=={}", loginForm);
        return userService.login(loginForm, session);
    }

    @ApiOperation("退出功能")
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("code");
        return R.success("用户成功退出");
    }
}
