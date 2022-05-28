package com.reggie.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-18-12:28
 */
@Api(tags = "地址簿控制器")
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {


    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation("新增地址")
    @PostMapping
    public R<String> saveAddress(@RequestBody AddressBook addressBook) {
        log.info("地址信息：{}", addressBook);
        return addressBookService.saveAddress(addressBook);
    }

    @ApiOperation("展示用户所有地址")
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        return addressBookService.listAddressBook(addressBook);
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public R<String> defaultAddressBook(@RequestBody AddressBook addressBook) {
        return addressBookService.defaultAddressBook(addressBook);
    }

    @ApiOperation("回显地址")
    @GetMapping("/{id}")
    public R<AddressBook> getAddress(@PathVariable("id") Long id) {
        AddressBook address = addressBookService.getById(id);
        return address == null ? R.error("没有该地址信息") : R.success(address);
    }

    @ApiOperation("修改地址")
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.updateAddress(addressBook);
    }

    @ApiOperation("删除地址")
    @DeleteMapping
    public R<String> removeAddress(@RequestParam("ids") Long id) {
        boolean isRemove = addressBookService.removeById(id);
        return BooleanUtil.isTrue(isRemove) ? R.success("地址删除成功") : R.error("地址删除失败");
    }

    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddressBook() {
        return addressBookService.getDefaultAddressBook();
    }
}
