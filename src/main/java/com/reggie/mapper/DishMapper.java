package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:50
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
