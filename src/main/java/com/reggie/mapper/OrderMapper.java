package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuLongjie
 * @create 2022-05-21-16:06
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
