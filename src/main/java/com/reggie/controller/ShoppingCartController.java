package com.reggie.controller;

import com.reggie.common.R;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-19-0:02
 */
@Api(tags = "购物车控制器")
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public R<List<ShoppingCart>> listShopping() {
        return shoppingCartService.listShopping();
    }

    @ApiOperation("添加购物车")
    @PostMapping("add")
    public R<String> saveShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.saveShoppingCart(shoppingCart);
    }

    @ApiOperation("减少购物车")
    @PostMapping("/sub")
    public R<String> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        return shoppingCartService.subShoppingCart(shoppingCart);
    }
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public R<String> removeShoppingCart() {
        return shoppingCartService.removeShoppingCart();
    }
}
