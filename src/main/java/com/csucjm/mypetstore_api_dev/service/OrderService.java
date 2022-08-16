package com.csucjm.mypetstore_api_dev.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.vo.OrderCartItemVO;
import com.csucjm.mypetstore_api_dev.vo.OrderVO;

public interface OrderService {

    CommonResponse<OrderVO> createOrder(Integer userId, Integer addressId);

    CommonResponse<OrderCartItemVO> getCheckedCartItemList(Integer userId);

    CommonResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo);

    CommonResponse<Page<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize);

    CommonResponse<String> cancelOrder(Integer userId, Long orderNo);
}
