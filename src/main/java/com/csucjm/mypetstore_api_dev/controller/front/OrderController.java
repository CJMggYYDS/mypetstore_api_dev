package com.csucjm.mypetstore_api_dev.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import com.csucjm.mypetstore_api_dev.entity.User;
import com.csucjm.mypetstore_api_dev.service.OrderService;
import com.csucjm.mypetstore_api_dev.vo.OrderCartItemVO;
import com.csucjm.mypetstore_api_dev.vo.OrderVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    public CommonResponse<OrderVO> create(
            @RequestParam @NotNull(message = "地址ID不能为空") Integer addressId,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return orderService.createOrder(loginUser.getId(), addressId);
    }

    @GetMapping("/cart_item_list")
    public CommonResponse<OrderCartItemVO> getCheckedCartItemList(HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return orderService.getCheckedCartItemList(loginUser.getId());
    }

    @GetMapping("/getDetail")
    public CommonResponse<OrderVO> getOrderDetail(
            @RequestParam @NotNull(message = "订单编号不能为空") Long orderNo,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return orderService.getOrderDetail(loginUser.getId(), orderNo);
    }

    @GetMapping("/orderList")
    public CommonResponse<Page<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return orderService.getOrderList(loginUser.getId(), pageNum, pageSize);
    }

    @PostMapping("/cancel")
    public CommonResponse<String> cancelOrder(
            @RequestParam @NotNull(message = "订单号不能为空") Long orderNo,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return orderService.cancelOrder(loginUser.getId(), orderNo);
    }
}
