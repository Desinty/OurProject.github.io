package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;

import java.util.List;

/**
 * @author XuLongjie
 * @create 2022-05-18-12:27
 */
public interface AddressBookService extends IService<AddressBook> {

    R<String> saveAddress(AddressBook addressBook);

    R<List<AddressBook>> listAddressBook(AddressBook addressBook);

    R<String> defaultAddressBook(AddressBook addressBook);

    R<String> updateAddress(AddressBook addressBook);

    R<AddressBook> getDefaultAddressBook();

}
