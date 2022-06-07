package com.reggie.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author XuLongjie
 * @create 2022-05-10-17:15
 */
@Api(tags = "员工控制器")
@Slf4j
@RestController()
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ApiOperation("员工登录")
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.login(request, employee);
    }

    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @ApiOperation("新增员工")
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("员工信息：{}", employee);
        return employeeService.saveEmp(request, employee);
    }

    @ApiOperation("员工信息分页查询")
    @GetMapping("/page")
    public R<Page> page(
            @ApiParam("当前页") Integer page,
            @ApiParam("当前页大小") Integer pageSize,
            @ApiParam("模糊查询员工名字") String name
    ) {
        log.info("当前页：{}，页大小：{}，员工名字：{}", page, pageSize, name);
        return employeeService.pageR(page, pageSize, name);
    }

    @ApiOperation("更新员工信息")
    @PutMapping()
    public R<String> update(@RequestBody Employee employee) {
        log.info("员工状态：{}", employee.getStatus());
        return employeeService.updateEmp(employee);
    }

    @ApiOperation("根据id查询员工信息")
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据{}查询员工信息", id);
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return R.error("没有该员工信息");
        }
        return R.success(employee);
    }

}
