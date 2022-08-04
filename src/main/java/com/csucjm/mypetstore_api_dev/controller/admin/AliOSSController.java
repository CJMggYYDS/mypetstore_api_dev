package com.csucjm.mypetstore_api_dev.controller.admin;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.service.OSSService;
import com.csucjm.mypetstore_api_dev.vo.AliOSSCallbackResult;
import com.csucjm.mypetstore_api_dev.vo.AliYunOSSPolicy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/oss")
public class AliOSSController {

    @Resource
    private OSSService ossService;

    @GetMapping("/getPolicy")
    public CommonResponse<AliYunOSSPolicy> getPolicy() {
        return ossService.generatePolicy();
    }

    @PostMapping("/callback")
    public CommonResponse<AliOSSCallbackResult> OSSCallback(HttpServletRequest request) {
        System.out.println("callback已被调用");
        return ossService.callback(request);
    }
}
