package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.Orders;

/**
 * @author XuLongjie
 * @create 2022-05-21-16:07
 */
public interface OrderService extends IService<Orders> {
    R<String> submitOrder(Orders orders);

    R<Page> orderPage(Integer page, Integer pageSize);

    R<String> againOrder(Orders orders);

    R<Page> orderDetailPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);

    R<String> status(Orders orders);
}
