package com.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.DishMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:52
 */
@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @Override
    public R<String> saveDish(DishDto dishDto) {
        // 1.将菜品信息保存在dish表
        save(dishDto);
        // 2.将菜品口味保存到dish_flavor表
        // 2.1.获取口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 2.2.设置dishId
        flavors = flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
        return R.success("菜品添加成功");
    }

    @Override
    public R<Page> pageR(Integer page, Integer pageSize, String name) {
        // 1.初始化分页信息
        Page<Dish> dishPageInfo = new Page<>(page, pageSize);
        // 2.分页查询菜品信息
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        page(dishPageInfo, queryWrapper);

        // 3.查询分类名称
        Page<DishDto> dishDtoPageInfo = new Page<>();
        // 3.1.拷贝分页信息
        BeanUtil.copyProperties(dishPageInfo, dishDtoPageInfo, "records");

        List<Dish> records = dishPageInfo.getRecords();

        // 3.2.处理records数据
        List<DishDto> list = records.stream().map(dish -> {
            //将records封装成DishDto
            DishDto dishDto = new DishDto();
            //拷贝对象
            BeanUtil.copyProperties(dish, dishDto);
            //分类id
            Long categoryId = dish.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPageInfo.setRecords(list);

        return R.success(dishDtoPageInfo);
    }

    @Override
    public R<DishDto> getDishById(Long id) {
        DishDto dishDto = new DishDto();
        // 1.根据id查询dish表
        Dish dish = getById(id);
        // 2.将dish信息拷贝到dishDto
        BeanUtil.copyProperties(dish, dishDto);

        // 3.查询flavor表中的菜品口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavorList = dishFlavorService.list(wrapper);

        dishDto.setFlavors(flavorList);

        return R.success(dishDto);
    }

    @Override
    public R<String> updateDish(DishDto dishDto) {
        // 1.更新dish表中的信息
        updateById(dishDto);

        // 2.删除dish_flavor表中的信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 3.新增dish_flavor表中的信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 3.1.设置dishId
        flavors = flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

        return R.success("更新菜品信息成功");
    }

    @Override
    public R<String> status(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 1.根据id查询菜品信息
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, status);
        // 2.计数菜品的状态
        int count = count(queryWrapper);
        // 3.判断是否要修改状态
        if (count > 0) {
            // 4.不需要修改返回信息
            throw new CustomException("修改状态与初始一致");
        }
        // 5.修改菜品状态
        Dish dish = new Dish();
        for (Long id : ids) {
            dish.setId(id);
            dish.setStatus(status);
            updateById(dish);
        }
        return R.success("菜品状态更改成功");
    }

    @Override
    public R<String> deleteDish(List<Long> ids) {
        // 1.查询菜品状态
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(Dish::getId, ids);
        dishWrapper.eq(Dish::getStatus, 1);
        int count = count(dishWrapper);
        // 2.判断菜品是否正在出售
        if (count > 0) {
            // 3.正在出售，返回信息
            throw new CustomException("菜品正在出售，不能删除");
        }
        // 4.停售，删除dish表中菜品信息
        removeByIds(ids);
        // 5.删除dish_flavor表中菜品信息
        // 5.1.根据菜品id删除flavor表中菜品信息
        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(flavorWrapper);
        // 6.返回
        return R.success("菜品删除成功");
    }

    /*@Override
    public R<List<Dish>> listDish(Dish dish) {
        //根据分类id查询菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //菜品售卖状态 1：起售
        queryWrapper.eq(Dish::getStatus, 1);
        //设置排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> dishList = list(queryWrapper);

        return R.success(dishList);
    }*/

    @Override
    public R<List<DishDto>> listDish(Dish dish) {
        // 1.根据分类id查询菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //菜品售卖状态 1：起售
        dishWrapper.eq(dish.getStatus() != null, Dish::getStatus, 1);
        //设置排序条件
        dishWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> dishList = list(dishWrapper);

        // 2.菜品中封装口味信息
        List<DishDto> dishDtoList = dishList.stream().map(dish1 -> {
            DishDto dishDto = new DishDto();
            // 拷贝对象给dishDto
            BeanUtil.copyProperties(dish1, dishDto);
            //根据菜品id查询口味
            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.eq(dishId != null, DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorWrapper);
            //封装口味信息
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}


















