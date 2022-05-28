package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.ShoppingCart;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-19-0:00
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    R<String> saveShoppingCart(ShoppingCart shoppingCart);

    R<List<ShoppingCart>> listShopping();

    R<String> removeShoppingCart();

    R<String> subShoppingCart(ShoppingCart shoppingCart);
}
