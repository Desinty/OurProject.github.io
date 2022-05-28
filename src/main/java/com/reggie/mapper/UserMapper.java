package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuLongjie
 * @create 2022-05-17-16:03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
