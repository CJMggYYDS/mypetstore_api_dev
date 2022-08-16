package com.csucjm.mypetstore_api_dev.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mystore_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("order_no")
    private Long orderNo;

    @TableField("user_id")
    private Integer userId;

    @TableField("address_id")
    private Integer addressId;

    @TableField("payment_price")
    private BigDecimal paymentPrice;

    @TableField("payment_type")
    private Integer paymentType;

    private Integer postage;

    private Integer status;

    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("close_time")
    private LocalDateTime closeTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
