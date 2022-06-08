package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:56
 */
@Slf4j
@Api(tags = "套餐控制器")
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("新增套餐")
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        return setmealService.saveSetmeal(setmealDto);
    }

    @ApiOperation("套餐信息分页查询")
    @GetMapping("/page")
    public R<Page> page(
            @ApiParam("当前页") Integer page,
            @ApiParam("当前页大小") Integer pageSize,
            @ApiParam("模糊查询套餐名称") String name
    ) {
        return setmealService.pageR(page, pageSize, name);
    }

    @ApiOperation("删除套餐")
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        return setmealService.removeSetmeal(ids);
    }

    @ApiOperation("回显套餐")
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable("id") Long id) {
        return setmealService.getSetmealById(id);
    }

    @ApiOperation("更新套餐")
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        return setmealService.updateSetmeal(setmealDto);
    }

    @ApiOperation("套餐出售状态")
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status,
                            @RequestParam("ids") List<Long> ids
    ) {
        return setmealService.status(status, ids);
    }

    @ApiOperation("套餐列表")
    @GetMapping("/list")
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal) {
        log.info("套餐信息: {}", setmeal);
        return setmealService.listSetmeal(setmeal);
    }

    @ApiOperation("显示菜品")
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dishInSetmeal(@PathVariable("id") Long setmealId) {
        return setmealService.dishInSetmeal(setmealId);
    }
}
