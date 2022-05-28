package com.reggie.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.ShoppingCart;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:53
 */
public interface SetmealService extends IService<Setmeal> {
    R<String> saveSetmeal(SetmealDto setmealDto);

    R<Page> pageR(Integer page, Integer pageSize, String name);

    R<String> removeSetmeal(List<Long> ids);

    R<SetmealDto> getSetmealById(Long id);

    R<String> updateSetmeal(SetmealDto setmealDto);

    R<String> status(Integer status, List<Long> ids);

    R<List<Setmeal>> listSetmeal(Setmeal setmeal);

    R<List<Dish>> dishInSetmeal(Long setmealId);

}
