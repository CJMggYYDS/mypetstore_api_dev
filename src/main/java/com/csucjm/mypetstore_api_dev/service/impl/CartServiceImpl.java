package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import com.csucjm.mypetstore_api_dev.entity.Cart;
import com.csucjm.mypetstore_api_dev.entity.Product;
import com.csucjm.mypetstore_api_dev.persistence.CartMapper;
import com.csucjm.mypetstore_api_dev.persistence.ProductMapper;
import com.csucjm.mypetstore_api_dev.service.CartService;
import com.csucjm.mypetstore_api_dev.utils.BigDecimalUtil;
import com.csucjm.mypetstore_api_dev.utils.ImageServerConfig;
import com.csucjm.mypetstore_api_dev.vo.CartItemVO;
import com.csucjm.mypetstore_api_dev.vo.CartVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<CartVO> selectCarts(Integer userId) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户购物车信息");
        }
        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> addCart(Integer userId, Integer productId, Integer quantity) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户信息");
        }
        if(productId == null) {
            return CommonResponse.createForError("缺少productId,添加失败");
        }
        if(quantity == null) {
            return CommonResponse.createForError("未指定添加商品数量,添加失败");
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("product_id", productId);

        Cart cartItem = cartMapper.selectOne(queryWrapper);
        int productStock = productMapper.selectById(productId).getStock();

        if(cartItem == null) {
            cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            if(productStock < quantity) {
                quantity = productStock;
            }
            cartItem.setQuantity(quantity);
            cartItem.setChecked(CONSTANT.CART_CHECK.CHECKED);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cartItem);
        }
        else {
            if(productStock < cartItem.getQuantity() + quantity) {
                quantity = productStock;
            }
            UpdateWrapper<Cart> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId());
            updateWrapper.set("quantity", quantity);
            updateWrapper.set("update_time", LocalDateTime.now());
            cartMapper.update(cartItem, updateWrapper);
        }
        return CommonResponse.createForSuccess();
    }

    public CommonResponse<CartVO> updateCart(Integer userId, Integer productId, Integer quantity) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户信息");
        }
        if(productId == null) {
            return CommonResponse.createForError("缺少productId,更新失败");
        }
        if(quantity == null) {
            return CommonResponse.createForError("未指定添加商品数量,更新失败");
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("product_id", productId);
        Cart cartItem = cartMapper.selectOne(queryWrapper);

        if(cartItem != null) {
            UpdateWrapper<Cart> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId());
            updateWrapper.set("quantity", quantity);
            updateWrapper.set("update_time", LocalDateTime.now());
            cartMapper.update(cartItem, updateWrapper);

            CartVO cartVO = this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        }
        else {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getMsg());
        }
    }

    @Override
    public CommonResponse<CartVO> deleteCart(Integer userId, Integer productId) {
        if(userId == null) {
            return CommonResponse.createForError("缺少userID,未获取到用户信息");
        }
        if(productId == null) {
            return CommonResponse.createForError("缺少productId,删除失败");
        }
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("product_id", productId);
        Cart cartItem = cartMapper.selectOne(queryWrapper);

        if(cartItem != null) {
           UpdateWrapper<Cart> updateWrapper = new UpdateWrapper<>();
           updateWrapper.eq("id", cartItem.getId());
           cartMapper.delete(updateWrapper);

           CartVO cartVO = this.getCartVOAndCheckStock(userId);
           return CommonResponse.createForSuccess(cartVO);
        }
        else {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getMsg());
        }
    }

    private CartVO getCartVOAndCheckStock(Integer userId) {
        CartVO cartVO = new CartVO();
        List<CartItemVO> cartItemVOList = new ArrayList<>();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        boolean allSelected = true;

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);

        if(!cartList.isEmpty()) {
            for(Cart cart : cartList) {
                CartItemVO cartItemVO = new CartItemVO();
                cartItemVO.setId(cart.getId());
                cartItemVO.setUserId(cart.getUserId());
                cartItemVO.setProductId(cart.getProductId());
                cartItemVO.setChecked(cart.getChecked());

                Product product = productMapper.selectById(cart.getProductId());
                if(product != null) {
                    cartItemVO.setProductName(product.getName());
                    cartItemVO.setProductSubtitle(product.getSubtitle());
                    cartItemVO.setProductMainImage(product.getMainImage());
                    cartItemVO.setProductPrice(product.getPrice());
                    cartItemVO.setProductStock(product.getStock());

                    // 校验库存
                    if(product.getStock() >= cart.getQuantity()) {
                        cartItemVO.setQuantity(cart.getQuantity());
                        cartItemVO.setCheckStock(true);
                    }
                    else {
                        cartItemVO.setQuantity(product.getStock());
                        Cart updateStockCart = new Cart();
                        UpdateWrapper<Cart> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", cart.getId());
                        updateWrapper.set("quantity", product.getStock());
                        updateWrapper.set("update_time", LocalDateTime.now());
                        cartMapper.update(updateStockCart, updateWrapper);
                        cartItemVO.setCheckStock(false);
                    }

                    cartItemVO.setCartItemTotalPrice(BigDecimalUtil.multiply(cartItemVO.getProductPrice().doubleValue(), cartItemVO.getQuantity().doubleValue()));
                }
                cartItemVOList.add(cartItemVO);
                if (cart.getChecked() == CONSTANT.CART_CHECK.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartItemVO.getCartItemTotalPrice().doubleValue());
                }
                else {
                    allSelected = false;
                }
            }
            cartVO.setCartItemVOList(cartItemVOList);
            cartVO.setCartTotalPrice(cartTotalPrice);
            cartVO.setAllSelected(allSelected);
            cartVO.setProductImageServer(imageServerConfig.getUrl());
        }

        return cartVO;
    }
}
