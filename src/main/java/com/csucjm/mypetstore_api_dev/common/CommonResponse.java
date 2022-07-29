package com.csucjm.mypetstore_api_dev.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private Integer code;
    private String msg;
    private T data;

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == ResponseCode.SUCCESS.getCode();
    }

    //成功，无返回数据data
    public static <T> CommonResponse<T> createForSuccess() {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    //成功，并有返回数据data
    public static <T> CommonResponse<T> createForSuccess(T data) {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(),data);
    }

    //错误，默认错误信息
    public static <T> CommonResponse<T> createForError() {
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg(), null);
    }

    //错误，指定错误信息
    public static <T> CommonResponse<T> createForError(String message) {
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), message, null);
    }

    //错误，指定错误码和错误信息
    public static <T> CommonResponse<T> createForError(Integer code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
