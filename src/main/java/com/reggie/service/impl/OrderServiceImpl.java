package com.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.dto.OrderDto;
import com.reggie.entity.*;
import com.reggie.mapper.OrderMapper;
import com.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author XuLongjie
 * @create 2022-05-21-16:08
 */
@Slf4j
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public R<String> submitOrder(Orders orders) {
        // 1.获取用户id
        Long userId = BaseContext.getCurrentId();
        // 2.根据用户id查询购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingList = shoppingCartService.list(shoppingCartWrapper);
        if (shoppingList == null) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 查询用户信息
        User user = userService.getById(userId);
        // 根据地址id查询地址信息
        Long addressId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);
        //封装信息到OrderDetail
        List<OrderDetail> orderDetails = shoppingList.stream().map(new Function<ShoppingCart, OrderDetail>() {
            @Override
            public OrderDetail apply(ShoppingCart shoppingCart) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderId);
                orderDetail.setNumber(shoppingCart.getNumber());
                orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
                orderDetail.setDishId(shoppingCart.getDishId());
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
                orderDetail.setName(shoppingCart.getName());
                orderDetail.setImage(shoppingCart.getImage());
                orderDetail.setAmount(shoppingCart.getAmount());
                amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
                return orderDetail;
            }
        }).collect(Collectors.toList());
        //封装信息到orders
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        // 3.保存信息到订单表
        save(orders);
        // 4.保存信息到订单明细表
        orderDetailService.saveBatch(orderDetails);
        // 5.清空购物车
        shoppingCartService.remove(shoppingCartWrapper);
        // 6.返回
        return R.success("用户下单成功");
    }

    @Override
    public R<Page> orderPage(Integer page, Integer pageSize) {
        // 1.初始化分页信息
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        // 2.查询订单
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.eq(userId != null, Orders::getUserId, userId);
        ordersWrapper.orderByDesc(Orders::getOrderTime);
        page(pageInfo, ordersWrapper);
        // 3.拷贝分页信息
        Page<OrderDto> dtoPageInfo = new Page<>();
        BeanUtil.copyProperties(pageInfo, dtoPageInfo, "records");
        // 4.处理records数据
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map(new Function<Orders, OrderDto>() {
            @Override
            public OrderDto apply(Orders orders) {
                //拷贝对象
                OrderDto orderDto = new OrderDto();
                BeanUtil.copyProperties(orders, orderDto);
                //处理订单明细
                Long orderId = orders.getId();
                LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
                orderDetailWrapper.eq(orderId != null, OrderDetail::getOrderId, orderId);
                List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailWrapper);
                orderDto.setOrderDetails(orderDetailList);
                return orderDto;
            }
        }).collect(Collectors.toList());
        dtoPageInfo.setRecords(orderDtoList);
        // 5.返回
        return R.success(dtoPageInfo);
    }

    @Override
    public R<String> againOrder(Orders orders) {
        // 1.获取订单id
        Long ordersId = orders.getId();
        // 2.通过订单id查询订单明细
        LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
        orderDetailWrapper.eq(ordersId != null, OrderDetail::getOrderId, ordersId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailWrapper);
        // 3.清空购物车
        shoppingCartService.removeShoppingCart();
        // 4.将查询到的订单信息封装到购物车中
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(new Function<OrderDetail, ShoppingCart>() {
            @Override
            public ShoppingCart apply(OrderDetail orderDetail) {
                //构造购物车
                ShoppingCart shoppingCart = new ShoppingCart();
                //封装购物车信息
                shoppingCart.setName(orderDetail.getName());
                shoppingCart.setImage(orderDetail.getImage());
                shoppingCart.setUserId(BaseContext.getCurrentId());
                Long dishId = orderDetail.getDishId();
                Long setmealId = orderDetail.getSetmealId();
                if (dishId != null) {
                    shoppingCart.setDishId(dishId);
                } else {
                    shoppingCart.setSetmealId(setmealId);
                }
                shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                shoppingCart.setNumber(orderDetail.getNumber());
                shoppingCart.setAmount(orderDetail.getAmount());
                return shoppingCart;
            }
        }).collect(Collectors.toList());
        // 5.保存购物信息
        shoppingCartService.saveBatch(shoppingCartList);
        // 6.返回
        return R.success("再次下单成功");
    }

    @Override
    public R<Page> orderDetailPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        // 1.初始化分页信息
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        // 2.设置查询条件
        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.like(StrUtil.isNotBlank(number), Orders::getNumber, number);
        ordersWrapper.gt(StrUtil.isNotBlank(beginTime), Orders::getOrderTime, beginTime);
        ordersWrapper.lt(StrUtil.isNotBlank(endTime), Orders::getOrderTime, endTime);
        // 3.查询分页信息
        page(ordersPage, ordersWrapper);
        // 4.返回
        return R.success(ordersPage);
    }

    @Override
    public R<String> status(Orders orders) {
        // 1.获取JSON中的订单信息
        Long ordersId = orders.getId();
        // 2.查询数据库中订单信息
        Orders order = getById(ordersId);
        if (order == null) {
            // 3.订单信息不存在，返回错误
            throw new CustomException("没有此订单信息");
        }

        // 4.存在，更新订单状态
        order.setStatus(orders.getStatus());
        updateById(order);
        return R.success("订单状态更新成功");
    }

}
