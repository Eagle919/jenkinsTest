package com.caih.cloud.iscs.charge.aasecretkey;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qq")
public class MyQQConfig {

    private String appid;

}
