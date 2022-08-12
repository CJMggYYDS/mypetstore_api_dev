package com.csucjm.mypetstore_api_dev.controller.front;

import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import com.csucjm.mypetstore_api_dev.entity.Address;
import com.csucjm.mypetstore_api_dev.entity.User;
import com.csucjm.mypetstore_api_dev.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    @PostMapping("/add")
    public CommonResponse<Address> add(@RequestBody @Valid Address address, HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return addressService.addAddress(loginUser.getId(), address);
    }

    @DeleteMapping("/delete")
    public CommonResponse<Object> delete(@RequestParam @NotNull(message = "地址ID不能为空") Integer addressId,
                                         HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return addressService.deleteAddress(loginUser.getId(), addressId);
    }

    @PutMapping("/update")
    public CommonResponse<Address> update(@RequestBody @Valid Address address, HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return addressService.updateAddress(loginUser.getId(), address);
    }

    @GetMapping("/find")
    public CommonResponse<Address> findById(@RequestParam @NotNull(message = "地址ID不能为空") Integer addressId,
                                            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return addressService.findAddressById(loginUser.getId(), addressId);
    }

    @GetMapping("/findList")
    public CommonResponse<List<Address>> findAll(HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return addressService.findAllAddresses(loginUser.getId());
    }
}
