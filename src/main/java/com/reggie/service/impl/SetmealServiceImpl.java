package com.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.*;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:54
 */
@Transactional
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Override
    public R<String> saveSetmeal(SetmealDto setmealDto) {
        //保存信息到setmeal表
        save(setmealDto);

        //保存信息到setmeal_dish表
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList = setmealDishList.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishList);
        return R.success("套餐信息添加成功");
    }

    @Override
    public R<Page> pageR(Integer page, Integer pageSize, String name) {

        //初始化分页信息
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询套餐名称
        queryWrapper.like(StrUtil.isNotBlank(name), Setmeal::getName, name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询
        page(setmealPageInfo, queryWrapper);

        //对setmealDto初始化分页信息
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //拷贝信息给dto
        BeanUtil.copyProperties(setmealPageInfo, setmealDtoPage, "records");
        List<Setmeal> records = setmealPageInfo.getRecords();

        List<SetmealDto> setmealDtoList = records.stream().map(setmeal -> {

            SetmealDto setmealDto = new SetmealDto();

            BeanUtil.copyProperties(setmeal, setmealDto);
            //分类id
            Long categoryId = setmeal.getCategoryId();
            //根据分类id查询套餐对应的分类
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    @Override
    public R<String> removeSetmeal(List<Long> ids) {
        // 1.查询套餐状态（1：起售 0：停售）
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = count(queryWrapper);
        // 2.判断套餐是否可以删除
        if (count > 0) {
            // 3.不能删除返回错误
            throw new CustomException("套餐正在售卖，不能删除");
        }
        // 4.删除setmeal表中套餐信息
        removeByIds(ids);
        // 5.删除setmeal_dish表中关系信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);

        return R.success("套餐删除成功");
    }

    @Override
    public R<SetmealDto> getSetmealById(Long id) {
        // 1.根据id查询套餐信息
        Setmeal setmeal = getById(id);
        // 2.将套餐信息拷贝给dto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtil.copyProperties(setmeal, setmealDto);
        // 3.根据套餐id查询菜品 setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        // 4.封装菜品信息
        setmealDto.setSetmealDishes(setmealDishList);
        // 5.返回
        return R.success(setmealDto);
    }

    @Override
    public R<String> updateSetmeal(SetmealDto setmealDto) {

        // 1.更新setmeal表中的信息
        updateById(setmealDto);
        // 2.根据setmeal_id删除setmeal_dish表中的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 3.获取setmealDto中的套餐菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(setmealDish -> {

            Long dishId = setmealDish.getDishId();
            //判断菜品是否存在
            Dish dish = dishService.getById(dishId);
            //不存在，返回信息
            if (dish == null) {
                throw new CustomException(setmealDish.getName() + "已经停售");
            }
            //判断菜品是否停售
            LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
            dishQueryWrapper.eq(Dish::getId, dishId).eq(Dish::getStatus, 0);
            int count = dishService.count(dishQueryWrapper);

            if (count == 1) {
                //停售，返回信息
                throw new CustomException(setmealDish.getName() + "已经停售");
            }
            //**************************************
            //手动添加套餐id
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        // 4.添加到setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
        // 5.返回结果
        return R.success("套餐修改成功");
    }

    @Override
    public R<String> status(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 1.根据id查询套餐信息
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, status);
        // 2.计数套餐的状态
        int count = count(queryWrapper);
        // 3.判断是否要修改状态
        if (count > 0) {
            // 4.不需要修改返回信息
            throw new CustomException("修改状态与初始一致");
        }
        // 5.修改套餐状态
        Setmeal setmeal = new Setmeal();
        for (Long id : ids) {
            setmeal.setId(id);
            setmeal.setStatus(status);
            updateById(setmeal);
        }
        return R.success("套餐状态修改成功");
    }

    @Override
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, 1)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = list(setmealWrapper);
        return R.success(setmealList);
    }


    @Override
    public R<List<DishDto>> dishInSetmeal(Long setmealId) {
        // 1.在setmeal_dish表中查询套餐关联的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.eq(setmealId != null, SetmealDish::getSetmealId, setmealId)
                .orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishWrapper);
        // 2.封装套餐信息到dto
        List<DishDto> dishDtoList = setmealDishList.stream().map(setmealDish -> {
            DishDto dishDto = new DishDto();
            BeanUtil.copyProperties(setmealDish, dishDto);
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtil.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        // 3.返回
        return R.success(dishDtoList);
    }

}
