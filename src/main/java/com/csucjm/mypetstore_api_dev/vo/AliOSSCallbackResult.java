package com.csucjm.mypetstore_api_dev.vo;

import lombok.Data;

@Data
public class AliOSSCallbackResult {

    private String filename;

    private String size;

    private String mimeType;

    private String height;

    private String width;
}
