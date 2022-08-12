package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.vo.CartVO;

public interface CartService {

    CommonResponse<CartVO> selectCarts(Integer userId);

    CommonResponse<Object> addCart(Integer userId, Integer productId, Integer quantity);

    CommonResponse<CartVO> updateCart(Integer userId, Integer productId, Integer quantity);

    CommonResponse<CartVO> deleteCart(Integer userId, String productIds);

    CommonResponse<Integer> getCartCount(Integer userId);

    CommonResponse<CartVO> updateAllCheckStatus(Integer userId, Integer checkStatus);

    CommonResponse<CartVO> updateCheckStatus(Integer userId, Integer productId, Integer checkStatus);
}
