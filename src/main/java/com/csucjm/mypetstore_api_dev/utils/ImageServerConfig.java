package com.csucjm.mypetstore_api_dev.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("image.server")
@Component
public class ImageServerConfig {
    private String url;
    private String username;
    private String password;
}
