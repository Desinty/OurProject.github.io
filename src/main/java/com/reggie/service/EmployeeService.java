package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

/**
 * @author XuLongjie
 * @create 2022-05-10-17:11
 */
public interface EmployeeService extends IService<Employee> {
    R<Employee> login(HttpServletRequest request, Employee employee);

    R<String> saveEmp(HttpServletRequest request, Employee employee);

    R<Page> pageR(Integer page, Integer pageSize, String name);


    R<String> updateEmp(Employee employee);
}
