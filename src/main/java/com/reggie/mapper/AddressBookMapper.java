package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuLongjie
 * @create 2022-05-18-12:26
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
