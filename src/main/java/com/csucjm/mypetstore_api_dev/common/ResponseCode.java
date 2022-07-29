package com.csucjm.mypetstore_api_dev.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(200, "SUCCESS"),
    ERROR(1, "ERROR"),
    ARGUMENT_ILLEGAL(10, "ARGUMENT_ILLEGAL");

    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
