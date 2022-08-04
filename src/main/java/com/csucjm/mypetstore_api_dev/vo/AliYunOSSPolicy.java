package com.csucjm.mypetstore_api_dev.vo;

import lombok.Data;

@Data
public class AliYunOSSPolicy {

    private String accessId;

    private String host;

    private String policy;

    private String signature;

    private String dir;

    private String callback;
}
