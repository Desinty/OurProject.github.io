package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-19:10
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public R<Page> pageR(Integer page, Integer pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<String> remove(Long id) {
        //查询是否有与菜品关联的分类信息
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishWrapper);
        //如果关联，返回业务异常
        if (dishCount > 0) {
            throw new CustomException("当前分类已关联菜品，不能删除");
        }

        //查询是否有与套餐关联的分类信息
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealWrapper);
        //如果关联，返回业务异常
        if (setmealCount > 0 ) {
            throw new CustomException("当前分类已关联套餐，不能删除");
        }

        //没有任何关联，删除分类信息，并返回信息
        removeById(id);
        return R.success("分类信息删除成功");
    }

    @Override
    public R<List<Category>> listInfo(Category category) {
        Integer type = category.getType();
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        //设置查询条件
        wrapper.eq(type != null, Category::getType, type);

        //设置排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categoryList = list(wrapper);
        return R.success(categoryList);
    }
}
