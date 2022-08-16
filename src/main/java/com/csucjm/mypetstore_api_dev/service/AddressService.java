package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.Address;
import com.csucjm.mypetstore_api_dev.vo.AddressVO;

import java.util.List;

public interface AddressService {

    CommonResponse<AddressVO> addAddress(Integer userId, Address address);

    CommonResponse<Object> deleteAddress(Integer userId, Integer addressId);

    CommonResponse<AddressVO> updateAddress(Integer userId, Address address);

    CommonResponse<AddressVO> findAddressById(Integer userId, Integer addressId);

    CommonResponse<List<AddressVO>> findAllAddresses(Integer userId);

}
