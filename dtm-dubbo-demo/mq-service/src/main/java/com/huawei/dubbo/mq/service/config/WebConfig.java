package com.huawei.dubbo.mq.service.config;

import com.huawei.dubbo.mq.service.model.RocketMqTemplate;
import com.huawei.middleware.dtm.client.tcc.rocketmq.DtmRocketMqProducer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
