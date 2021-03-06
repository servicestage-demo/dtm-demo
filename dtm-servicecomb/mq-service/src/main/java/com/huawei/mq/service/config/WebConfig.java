package com.huawei.mq.service.config;

import com.huawei.middleware.dtm.client.tcc.rocketmq.DtmRocketMqProducer;
import com.huawei.mq.service.model.RocketMqTemplate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public DtmRocketMqProducer dtmRocketMqProducer() throws Exception {
        DtmRocketMqProducer producer = new DtmRocketMqProducer("dtm-rocket-mq");
        producer.setNamesrvAddr("http://127.0.0.1:9876");
        return producer;
    }

    @Bean
    public RocketMqTemplate mqTemplate(@Qualifier("dtmRocketMqProducer") DtmRocketMqProducer producer) {
        return new RocketMqTemplate(producer);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
