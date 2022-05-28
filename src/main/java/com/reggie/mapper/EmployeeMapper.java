package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuLongjie
 * @create 2022-05-10-17:08
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
