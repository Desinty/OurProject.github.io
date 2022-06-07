package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.service.EmployeeService;
import com.reggie.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author XuLongjie
 * @create 2022-05-10-17:12
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        // 1.密码md5加密
        String password = employee.getPassword();
        password = MD5.encrypt(password);
        // 2.根据用户名查询数据库
        String username = employee.getUsername();
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Employee emp = getOne(queryWrapper);
        //用户不存在
        if (emp == null) {
            return R.error("用户不存在");
        }
        // 2.1.密码比对不一致返回错误信息
        if (!emp.getPassword().equals(password)) {
            return R.error("密码不正确");
        }
        // 3.一致，查看员工状态
        if (emp.getStatus() == 0) {
            // 3.1.员工状态被禁用返回错误信息
            return R.error("该用户禁止登录");
        }
        // 3.2.将id存入session
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());
        // 4.返回
        return R.success(emp);
    }

    @Override
    public R<String> saveEmp(HttpServletRequest request, Employee employee) {
        //将密码转成密文 默认密码：123456
        employee.setPassword(MD5.encrypt("123456"));
        save(employee);
        return R.success("新增员工成功");
    }

    @Override
    public R<Page> pageR(Integer page, Integer pageSize, String name) {
        //设置分页信息
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        //设置查询条件
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderByAsc("create_time");

        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<String> updateEmp(Employee employee) {
        // 1.获取员工id
        Long empId = employee.getId();
        // 2.与管理员id比较 -- 1
        if (empId == 1L) {
            Employee admin = getById(empId);
            if (!admin.getStatus().equals(employee.getStatus())) {
                return R.error("管理员登录权限不能修改");
            }
            if (!admin.getUsername().equals(employee.getUsername())) {
                return R.error("管理员账号不能修改");
            }
            if (!admin.getName().equals(employee.getName())) {
                return R.error("管理员名字不能修改");
            }
        }
        // 3.更新员工信息
        updateById(employee);
        return R.success("员工信息修改成功");
    }

}
