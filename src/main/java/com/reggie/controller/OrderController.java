package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Orders;
import com.reggie.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XuLongjie
 * @create 2022-05-21-16:09
 */
@Api(tags = "订单控制器")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("提交订单")
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders) {
        return orderService.submitOrder(orders);
    }

    @ApiOperation("查看订单")
    @GetMapping("/userPage")
    public R<Page> orderPage(Integer page, Integer pageSize) {
        return orderService.orderPage(page, pageSize);
    }

    @ApiOperation("再来一单")
    @PostMapping("again")
    public R<String> againOrder(@RequestBody Orders orders) {
        return orderService.againOrder(orders);
    }

    @ApiOperation("后后订单明细")
    @GetMapping("/page")
    public R<Page> orderDetailPage(@ApiParam("当前页") Integer page,
                                   @ApiParam("当前页大小") Integer pageSize,
                                   @ApiParam("订单号") String number,
                                   @ApiParam("开始时间") String beginTime,
                                   @ApiParam("结束时间") String endTime
    ) {
        return orderService.orderDetailPage(page, pageSize, number, beginTime, endTime);
    }

    @ApiOperation("更改订单状态")
    @PutMapping
    public R<String> status(@RequestBody Orders orders) {
        return orderService.status(orders);
    }
}
