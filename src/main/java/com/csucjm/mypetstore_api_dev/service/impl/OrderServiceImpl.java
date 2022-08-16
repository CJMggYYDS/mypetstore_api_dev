package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.*;
import com.csucjm.mypetstore_api_dev.persistence.*;
import com.csucjm.mypetstore_api_dev.service.AddressService;
import com.csucjm.mypetstore_api_dev.service.OrderService;
import com.csucjm.mypetstore_api_dev.utils.BigDecimalUtil;
import com.csucjm.mypetstore_api_dev.utils.DateTimeFormatterUtil;
import com.csucjm.mypetstore_api_dev.config.ImageServerConfig;
import com.csucjm.mypetstore_api_dev.vo.OrderCartItemVO;
import com.csucjm.mypetstore_api_dev.vo.OrderItemVO;
import com.csucjm.mypetstore_api_dev.vo.OrderVO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private AddressService addressService;

    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<OrderVO> createOrder(Integer userId, Integer addressId) {

        // 1. 从购物车中获取用户已选中的商品
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper
                .eq("user_id", userId)
                .eq("checked", CONSTANT.CART_CHECK.CHECKED);
        List<Cart> cartList = cartMapper.selectList(cartQueryWrapper);

        if(cartList.isEmpty()) {
            return CommonResponse.createForError("购物车为空,无法创建新订单");
        }

        // 2. 将cart中的数据信息转换为orderItems
        CommonResponse<List<OrderItem>> orderItemListResult = this.cartItemToOrderItem(cartList);
        if(!orderItemListResult.isSuccess()) {
            return CommonResponse.createForError("");
        }

        List<OrderItem> orderItemList = orderItemListResult.getData();
        // 3. 计算订单总价
        BigDecimal paymentPrice = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList) {
            paymentPrice = BigDecimalUtil.add(paymentPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }

        // 4. 生成订单Order并插入到Order表中
        Order order = new Order();
        Long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setPaymentPrice(paymentPrice);
        order.setPaymentType(CONSTANT.PayType.ALIPAY.getCode());
        order.setPostage(0);
        order.setStatus(CONSTANT.OrderStatus.PAID.getCode());
        // 订单中与购买业务的时间相关信息暂时不考虑

        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        int result = orderMapper.insert(order);
        if(result != 1) {
            return CommonResponse.createForError("服务端错误,生成新订单失败");
        }

        // 5. 将订单明细插入OrderItem表中
        for(OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(orderNo);
        }
        int orderItemResult = orderItemMapper.insertBatchSomeColumn(orderItemList);
        if(orderItemResult < 1) {
            return CommonResponse.createForError("服务端错误,生成新订单明细失败");
        }

        // 6. 减少商品库存
        UpdateWrapper<Product> productUpdateWrapper = new UpdateWrapper<>();
        for(OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectById(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productUpdateWrapper.eq("id", product.getId());
            productUpdateWrapper.set("stock", product.getStock());
            productMapper.update(product, productUpdateWrapper);
            productUpdateWrapper.clear();
        }

        // 7. 清空购物车
        for(Cart cartItem : cartList) {
            cartMapper.deleteById(cartItem.getId());
        }

        OrderVO orderVO = generateOrderVO(order, orderItemList);
        return CommonResponse.createForSuccess(orderVO);
    }

    @Override
    public CommonResponse<OrderCartItemVO> getCheckedCartItemList(Integer userId) {

        OrderCartItemVO orderCartItemVO = new OrderCartItemVO();

        //1. 从购物车中获取已经选中的商品
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("user_id", userId);
        cartQueryWrapper.eq("checked", CONSTANT.CART_CHECK.CHECKED);
        List<Cart> cartItemList = cartMapper.selectList(cartQueryWrapper);

        if(cartItemList.isEmpty()){
            return CommonResponse.createForError("购物车为空");
        }

        //2. 将购物车中的cartItem填入到orderItem中
        CommonResponse<List<OrderItem>> cartItemToOrderItemResult = this.cartItemToOrderItem(cartItemList);
        if(!cartItemToOrderItemResult.isSuccess()){
            return CommonResponse.createForError("");
        }

        List<OrderItem> orderItemList = cartItemToOrderItemResult.getData();

        //3. 计算整个订单总价和将OrderItem->OrderItemVO
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        BigDecimal paymentPrice = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            paymentPrice = BigDecimalUtil.add(paymentPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(orderItemToOrderItemVO(orderItem));
        }

        orderCartItemVO.setOrderItemVOList(orderItemVOList);
        orderCartItemVO.setPaymentPrice(paymentPrice);
        orderCartItemVO.setImageServer(imageServerConfig.getUrl());

        return CommonResponse.createForSuccess(orderCartItemVO);
    }

    @Override
    public CommonResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        Order order = orderMapper.selectOne(queryWrapper);

        if(order == null) {
            return CommonResponse.createForError("订单不存在");
        }
        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper
                .eq("user_id", userId)
                .eq("order_no", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectList(orderItemQueryWrapper);

        OrderVO orderVO = generateOrderVO(order, orderItemList);

        return CommonResponse.createForSuccess(orderVO);
    }

    @Override
    public CommonResponse<Page<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize) {
        Page<Order> result = new Page<>();
        result.setCurrent(pageNum);
        result.setSize(pageSize);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        result = orderMapper.selectPage(result, queryWrapper);

        List<Order> orderList = result.getRecords();
        List<OrderVO> orderVOList = new ArrayList<>();

        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        for(Order order : orderList) {
            orderItemQueryWrapper
                    .eq("user_id", userId)
                    .eq("order_no", order.getOrderNo());
            List<OrderItem> orderItemList = orderItemMapper.selectList(orderItemQueryWrapper);
            OrderVO orderVO = generateOrderVO(order, orderItemList);
            orderVOList.add(orderVO);
            orderItemQueryWrapper.clear();
        }
        Page<OrderVO> orderVOPage = new Page<>();
        orderVOPage.setCurrent(result.getCurrent());
        orderVOPage.setSize(result.getSize());
        orderVOPage.setTotal(result.getTotal());
        orderVOPage.setRecords(orderVOList);

        return CommonResponse.createForSuccess(orderVOPage);
    }

    @Override
    public CommonResponse<String> cancelOrder(Integer userId, Long orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("order_no", orderNo);
        Order order = orderMapper.selectOne(queryWrapper);

        if(order == null) {
            return CommonResponse.createForError("订单不存在");
        }
        if(order.getStatus() != CONSTANT.OrderStatus.UNPAID.getCode()) {
            return CommonResponse.createForError("订单不是未支付状态,不能取消");
        }
        order.setStatus(CONSTANT.OrderStatus.CANCEL.getCode());
        int result = orderMapper.updateById(order);
        if(result != 1) {
            return CommonResponse.createForError("服务端错误,取消订单失败");
        }
        return CommonResponse.createForSuccess();
    }

    private CommonResponse<List<OrderItem>> cartItemToOrderItem(List<Cart> cartList) {
        List<OrderItem> orderItemList = new ArrayList<>();

        for(Cart cartItem : cartList) {
            // 这一步核查购物车中的商品是否可以被购买
            Product product = productMapper.selectById(cartItem.getProductId());
            if(product.getStatus() != CONSTANT.ProductStatus.ON_SALE.getCode()) {
                return CommonResponse.createForError("商品: "+ product.getName() + "不是在售状态,生成订单失败");
            }
            if(product.getStock() < cartItem.getQuantity()) {
                return CommonResponse.createForError("商品: "+product.getName() + "库存不足,生成订单失败");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(cartItem.getUserId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cartItem.getQuantity().doubleValue()));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());

            orderItemList.add(orderItem);
        }
        return CommonResponse.createForSuccess(orderItemList);
    }

    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private OrderVO generateOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();

        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setUserId(order.getUserId());
        orderVO.setPaymentPrice(order.getPaymentPrice());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());

        orderVO.setPaymentTime(DateTimeFormatterUtil.format(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeFormatterUtil.format(order.getSendTime()));
        orderVO.setEndTime(DateTimeFormatterUtil.format(order.getEndTime()));
        orderVO.setCloseTime(DateTimeFormatterUtil.format(order.getCloseTime()));
        orderVO.setCreateTime(DateTimeFormatterUtil.format(order.getCreateTime()));
        orderVO.setUpdateTime(DateTimeFormatterUtil.format(order.getUpdateTime()));

        orderVO.setAddressVO(addressService.findAddressById(order.getUserId(), order.getAddressId()).getData());

        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for(OrderItem orderItem : orderItemList) {
            orderItemVOList.add(orderItemToOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setImageServer(imageServerConfig.getUrl());

        return orderVO;
    }

    private OrderItemVO orderItemToOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setId(orderItem.getId());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentPrice(orderItem.getCurrentPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVO;
    }
}