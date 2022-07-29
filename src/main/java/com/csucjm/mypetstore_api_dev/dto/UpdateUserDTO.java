package com.csucjm.mypetstore_api_dev.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserDTO {

    private Integer id;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "电话号码不能为空")
    private String phone;

    private String question;
    private String answer;
}
