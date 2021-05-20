package com.huawei.dtm.invoke.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 增加数据源
 */
@Configuration
public class ClientConfig {

    @Value("${dtm.use-feign:false}")
    private boolean useFeign;

    @Bean(name = "use-feign")
    public boolean isUseFeign() {
        return useFeign;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
