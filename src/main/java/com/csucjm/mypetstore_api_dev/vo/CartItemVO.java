package com.csucjm.mypetstore_api_dev.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Integer checked;

    private String productName;
    private String productSubtitle;
    private BigDecimal productPrice;
    private Integer productStock;
    private String productMainImage;

    private BigDecimal cartItemTotalPrice;
    private Boolean checkStock;
}
