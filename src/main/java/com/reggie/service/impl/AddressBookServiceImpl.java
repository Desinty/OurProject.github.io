package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.mapper.AddressBookMapper;
import com.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-18-12:27
 */
@Service
@Transactional
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public R<String> saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        save(addressBook);
        return R.success("新增地址成功");
    }

    @Override
    public R<List<AddressBook>> listAddressBook(AddressBook addressBook) {
        // 1.获取用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        Long userId = addressBook.getUserId();
        // 2.根据用户id查询用户所有地址
        LambdaQueryWrapper<AddressBook> addressBookWrapper = new LambdaQueryWrapper<>();
        addressBookWrapper.eq(userId != null, AddressBook::getUserId, userId);
        addressBookWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = list(addressBookWrapper);
        // 3.返回
        return R.success(addressBookList);
    }

    @Override
    public R<String> defaultAddressBook(AddressBook addressBook) {
        // 1.修改该用户所有is_default为0：非默认地址
        LambdaUpdateWrapper<AddressBook> addressWrapper = new LambdaUpdateWrapper<>();
        addressWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressWrapper.set(AddressBook::getIsDefault, 0);
        update(addressWrapper);
        // 2.更新目标地址为默认地址
        addressBook.setIsDefault(1);
        updateById(addressBook);
        // 3.返回
        return R.success("地址成功设为默认地址");
    }

    @Override
    public R<String> updateAddress(AddressBook addressBook) {
        if (addressBook == null) {
            return R.error("没有用户地址信息");
        }
        updateById(addressBook);
        return R.success("更新成功");
    }

    @Override
    public R<AddressBook> getDefaultAddressBook() {
        // 1.获取用户Id
        Long userId = BaseContext.getCurrentId();
        // 2.根据user_id、is_default查询默认地址
        LambdaQueryWrapper<AddressBook> addressWrapper = new LambdaQueryWrapper<>();
        addressWrapper.eq(userId != null, AddressBook::getUserId, userId);
        addressWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook address = getOne(addressWrapper);
        // 3.判断是否存在默认地址
        if (address == null) {
            // 4.不存在，返回提示信息
            return R.error("请设置默认地址");
        }

        // 5.存在返回
        return R.success(address);
    }
}
