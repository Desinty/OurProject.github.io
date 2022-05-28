package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.ShoppingCart;
import com.reggie.mapper.ShoppingCartMapper;
import com.reggie.service.ShoppingCartService;
import javafx.scene.control.Label;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-19-0:01
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public R<String> saveShoppingCart(ShoppingCart shoppingCart) {
        // 1.设置userId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 2.判断添加的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        // 2.1.菜品、口味
        shoppingCartWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        // 2.2.套餐
        shoppingCartWrapper.eq(shoppingCart.getSetmealId() != null,
                ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        shoppingCartWrapper.eq(userId != null, ShoppingCart::getUserId, userId);

        ShoppingCart scInfo = getOne(shoppingCartWrapper);
        // 3.判断当前菜品或套餐是否存在
        if (scInfo != null) {
            // 4.存在，数量加1
            scInfo.setNumber(scInfo.getNumber() + 1);
            updateById(scInfo);
        } else {
            // 5.不存在则新增
            save(shoppingCart);
        }
        // 6.返回
        return R.success("添加购物车成功");
    }

    @Override
    public R<List<ShoppingCart>> listShopping() {
        // 1.根据用户id查询当前用户信息
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        shoppingCartWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = list(shoppingCartWrapper);
        // 2.返回
        return R.success(shoppingCartList);
    }

    @Override
    public R<String> removeShoppingCart() {
        // 1.根据用户id删除购物车信息
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        remove(shoppingCartWrapper);
        // 2.返回
        return R.success("成功清空购物车");
    }

    @Override
    public R<String> subShoppingCart(ShoppingCart shoppingCart) {
        // 1.获取userId
        Long userId = BaseContext.getCurrentId();
        // 2.根据userId、setmeal_id/dish_id查询购物信息
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        shoppingCartWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        shoppingCartWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);
        ShoppingCart scInfo = getOne(shoppingCartWrapper);
        // 3.判断数量是否等于1
        Integer number = scInfo.getNumber();
        if (number == 1) {
            // 4.是，删除购物信息
            remove(shoppingCartWrapper);
        } else {
            // 5.不是，number - 1
            scInfo.setNumber(number - 1);
            // 6.更新数据
            updateById(scInfo);
        }
        return R.success("购物车信息删除成功");
    }
}
