package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.vo.AliOSSCallbackResult;
import com.csucjm.mypetstore_api_dev.vo.AliYunOSSPolicy;

import javax.servlet.http.HttpServletRequest;

public interface OSSService {

    CommonResponse<AliYunOSSPolicy> generatePolicy();

    CommonResponse<AliOSSCallbackResult> callback(HttpServletRequest request);
}
