package com.csucjm.mypetstore_api_dev.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mystore_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("category_id")
    private Integer categoryId;

    private String name;
    private String subtitle;

    @TableField("main_image")
    private String mainImage;

    @TableField("sub_images")
    private String subImages;

    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
