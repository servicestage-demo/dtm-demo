package com.huawei.dtm.kafka.server.config;

import com.huawei.dtm.kafka.server.model.KafkaTemplate;
import com.huawei.middleware.dtm.client.tcc.kafka.DtmKafkaProducer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class WebConfig {

    @Value("${spring.kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean
    public DtmKafkaProducer<String, String> dtmKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        // 必须设置的
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put("retries", 3);
        return new DtmKafkaProducer<>(props);
    }

    @Bean
    public KafkaTemplate kafkaTemplate(@Qualifier("dtmKafkaProducer") DtmKafkaProducer<String, String> producer) {
        return new KafkaTemplate(producer);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
