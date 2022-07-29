package com.csucjm.mypetstore_api_dev.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mystore_user")
public class User {
    private Integer id;
    private String username;
    private String password;

    private String email;
    private String phone;
    private String question;
    private String answer;

    private Integer role;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
