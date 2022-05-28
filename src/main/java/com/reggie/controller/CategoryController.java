package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-19:11
 */
@Api(tags = "分类分类控制器")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("新增分类")
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("分类添加成功");
    }

    @ApiOperation("分类信息分页查询")
    @GetMapping("/page")
    public R<Page> page(
            @ApiParam("当前页") Integer page,
            @ApiParam("当前页大小") Integer pageSize
    ) {
        return categoryService.pageR(page, pageSize);
    }

    @ApiOperation("删除分类信息")
    @DeleteMapping
    public R<String> remove(@RequestParam("ids") Long id) {
        return categoryService.remove(id);
    }

    @ApiOperation("更新分类信息")
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("更新分类信息成功");
    }

    @ApiOperation("根据type查询分类信息")
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        return categoryService.listInfo(category);
    }

}
