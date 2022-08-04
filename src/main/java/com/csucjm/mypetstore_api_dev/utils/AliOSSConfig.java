package com.csucjm.mypetstore_api_dev.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AliOSSConfig {

    @Value("${aliyun.oss.endpoint}")
    private String ENDPOINT;

    @Value("${aliyun.oss.accessId}")
    private String ACCESS_ID;

    @Value("${aliyun.oss.accessKey}")
    private String ACCESS_KEY;

    @Bean
    public OSSClient ossClient() {
        return new OSSClient(ENDPOINT, ACCESS_ID, ACCESS_KEY);
    }
}
