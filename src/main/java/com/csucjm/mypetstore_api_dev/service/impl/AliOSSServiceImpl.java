package com.csucjm.mypetstore_api_dev.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.service.OSSService;
import com.csucjm.mypetstore_api_dev.vo.AliOSSCallbackResult;
import com.csucjm.mypetstore_api_dev.vo.AliYunOSSPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service("aliOSSService")
@Slf4j
public class AliOSSServiceImpl implements OSSService {

    @Resource
    private OSSClient ossClient;

    @Value("${aliyun.oss.accessId}")
    private String ACCESS_ID;

    @Value("${aliyun.oss.bucketHost}")
    private String BUCKET_HOST;

    @Value(("${aliyun.oss.dir}"))
    private String DIR;

    @Value("${aliyun.oss.policy.expire}")
    private long EXPIRE;

    @Value("${aliyun.oss.maxSize}")
    private long MAX_SIZE;

    @Value("${aliyun.oss.callbackUrl}")
    private String CALLBACK_URL;

    @Override
    public CommonResponse<AliYunOSSPolicy> generatePolicy() {
        AliYunOSSPolicy aliYunOSSPolicy = new AliYunOSSPolicy();

        String host = "http://" + BUCKET_HOST;

        try {
            long expireTime = EXPIRE;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1024L*1024*MAX_SIZE);
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, DIR);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);


            JSONObject jsonCallback = new JSONObject();
            jsonCallback.put("callbackUrl", CALLBACK_URL);
            jsonCallback.put("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jsonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            System.out.println(jsonCallback);

            String base64CallbackBody = BinaryUtil.toBase64String(jsonCallback.toString().getBytes());

            aliYunOSSPolicy.setAccessId(ACCESS_ID);
            aliYunOSSPolicy.setPolicy(encodedPolicy);
            aliYunOSSPolicy.setSignature(postSignature);
            aliYunOSSPolicy.setHost(host);
            aliYunOSSPolicy.setDir(DIR);
            aliYunOSSPolicy.setCallback(base64CallbackBody);

        }catch (Exception e) {
            log.info("服务器生成aliyun oss policy失败: ", e);
        }

        return CommonResponse.createForSuccess(aliYunOSSPolicy);
    }

    @Override
    public CommonResponse<AliOSSCallbackResult> callback(HttpServletRequest request) {
        AliOSSCallbackResult result = new AliOSSCallbackResult();
        result.setFilename(request.getParameter("filename"));
        result.setSize(request.getParameter("size"));
        result.setMimeType(request.getParameter("mimeType"));
        result.setHeight(request.getParameter("height"));
        result.setWidth(request.getParameter("width"));

        return CommonResponse.createForSuccess(result);
    }
}
