package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.Category;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-13-19:09
 */
public interface CategoryService extends IService<Category> {

    R<Page> pageR(Integer page, Integer pageSize);

    R<String> remove(Long id);

    R<List<Category>> listInfo(Category category);
}
