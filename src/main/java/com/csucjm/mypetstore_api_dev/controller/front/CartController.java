package com.csucjm.mypetstore_api_dev.controller.front;

import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import com.csucjm.mypetstore_api_dev.entity.User;
import com.csucjm.mypetstore_api_dev.service.CartService;
import com.csucjm.mypetstore_api_dev.vo.CartVO;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/getCarts")
    public CommonResponse<CartVO> getCarts(HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return cartService.selectCarts(loginUser.getId());
    }

    @PostMapping("/addCart")
    public CommonResponse<Object> addCart(
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId,
            @RequestParam @Range(min = 1, message = "商品数量不能小于等于0") Integer quantity,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }

        return cartService.addCart(loginUser.getId(), productId, quantity);
    }

    @PutMapping("/updateCart")
    public CommonResponse<CartVO> updateCart(
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId,
            @RequestParam @Range(min = 1, message = "商品数量不能小于等于0") Integer quantity,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }

        return cartService.updateCart(loginUser.getId(), productId, quantity);
    }

    @DeleteMapping("/deleteCart")
    public CommonResponse<CartVO> deleteCart(
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
        }
        return cartService.deleteCart(loginUser.getId(), productId);
    }
}
