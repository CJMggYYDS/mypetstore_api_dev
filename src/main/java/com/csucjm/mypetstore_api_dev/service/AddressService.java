package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.Address;

import java.util.List;

public interface AddressService {

    CommonResponse<Address> addAddress(Integer userId, Address address);

    CommonResponse<Object> deleteAddress(Integer userId, Integer addressId);

    CommonResponse<Address> updateAddress(Integer userId, Address address);

    CommonResponse<Address> findAddressById(Integer userId, Integer addressId);

    CommonResponse<List<Address>> findAllAddresses(Integer userId);
}
