package com.csucjm.mypetstore_api_dev.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO {

    private Long orderNo;
    private Integer userId;

    private BigDecimal paymentPrice;
    private Integer paymentType;
    private Integer postage;

    private Integer status;

    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;

    private String createTime;
    private String updateTime;

    private AddressVO addressVO;

    private List<OrderItemVO> orderItemVOList;

    private String imageServer;
}
