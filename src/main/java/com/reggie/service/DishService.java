package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:52
 */
public interface DishService extends IService<Dish> {

    R<String> saveDish(DishDto dishDto);

    R<Page> pageR(Integer page, Integer pageSize, String name);

    R<DishDto> getDishById(Long id);

    R<String> updateDish(DishDto dishDto);

    R<String> status(Integer status, List<Long> ids);

    R<String> deleteDish(List<Long> ids);

//    R<List<Dish>> listDish(Dish dish);

    R<List<DishDto>> listDish(Dish dish);
}
