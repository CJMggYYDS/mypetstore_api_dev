package com.csucjm.mypetstore_api_dev.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mystore_address")
public class Address {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("address_name")
    private String addressName;

    @TableField("address_phone")
    private String addressPhone;

    @TableField("address_mobile")
    private String addressMobile;

    @TableField("address_province")
    private String addressProvince;

    @TableField("address_city")
    private String addressCity;

    @TableField("address_district")
    private String addressDistrict;

    @TableField("address_detail")
    private String addressDetail;

    @TableField("address_zip")
    private String addressZip;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
