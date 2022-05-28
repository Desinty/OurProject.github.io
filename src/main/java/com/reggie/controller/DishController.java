package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-21:55
 */
@Api(tags = "菜品控制器")
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto) {
        return dishService.saveDish(dishDto);
    }

    @ApiOperation("菜品信息分页查询")
    @GetMapping("/page")
    public R<Page> page(
            @ApiParam("当前页") Integer page,
            @ApiParam("当前页大小") Integer pageSize,
            @ApiParam("模糊查询菜品名称") String name
    ) {
        return dishService.pageR(page, pageSize, name);
    }

    @ApiOperation("回显菜品信息")
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") Long id) {
        return dishService.getDishById(id);
    }

    @ApiOperation("更新菜品")
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        return dishService.updateDish(dishDto);
    }

    @ApiOperation("菜品售卖状态")
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,
                          @RequestParam("ids") List<Long> ids
    ) {
        return dishService.status(status, ids);
    }

    @ApiOperation("删除菜品")
    @DeleteMapping
    public R<String> deleteDish(@RequestParam("ids") List<Long> ids) {
        return dishService.deleteDish(ids);
    }


/*    @ApiOperation("菜品列表")
    @GetMapping("/list")
    public R<List<Dish>> listDish(Dish dish) {
        return dishService.listDish(dish);
    }*/

    @ApiOperation("菜品列表")
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Dish dish) {
        return dishService.listDish(dish);
    }


}
