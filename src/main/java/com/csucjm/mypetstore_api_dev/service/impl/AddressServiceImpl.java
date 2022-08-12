package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.Address;
import com.csucjm.mypetstore_api_dev.persistence.AddressMapper;
import com.csucjm.mypetstore_api_dev.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public CommonResponse<Address> addAddress(Integer userId, Address address) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户地址信息");
        }
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        int result = addressMapper.insert(address);
        if(result!=1) {
            return CommonResponse.createForError("服务端异常,新增地址失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<Object> deleteAddress(Integer userId, Integer addressId) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户地址信息");
        }
        if(addressId == null) {
            return CommonResponse.createForError("缺少addressID,删除失败");
        }
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("id", addressId);
        int result = addressMapper.delete(queryWrapper);
        if(result != 1) {
            return CommonResponse.createForError("服务端异常,删除地址失败");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Address> updateAddress(Integer userId, Address address) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户地址信息");
        }
        UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("user_id", userId)
                .eq("id", address.getId());
        int result = addressMapper.update(address, updateWrapper);
        if(result!=1) {
            return CommonResponse.createForError("服务端异常,更新失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<Address> findAddressById(Integer userId, Integer addressId) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户地址信息");
        }
        if(addressId == null) {
            return CommonResponse.createForError("缺少addressID,删除失败");
        }
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("id", addressId);
        Address address = addressMapper.selectOne(queryWrapper);
        if(address == null) {
            return CommonResponse.createForError("服务端异常,获取地址信息失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<List<Address>> findAllAddresses(Integer userId) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户地址信息");
        }
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Address> addressList = addressMapper.selectList(queryWrapper);
        return CommonResponse.createForSuccess(addressList);
    }
}
