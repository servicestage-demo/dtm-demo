package com.huawei.mq.consumer.config;

import com.huawei.common.impl.BankBService;
import com.huawei.middleware.dtm.client.datasource.proxy.DTMDataSource;
import com.huawei.middleware.dtm.client.tcc.rocketmq.DtmRocketMqProducer;
import com.huawei.mq.consumer.ConsumerStore;
import com.huawei.mq.consumer.RocketMqTemplate;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
public class WebConfig {

    @Value("${spring.datasource.bank.url}")
    private String dbUrl;

    @Value("${spring.datasource.bank.username}")
    private String username;

    @Value("${spring.datasource.bank.password}")
    private String password;

    @Value("${spring.datasource.bank.driver-class-name}")
    private String driverClassName;

    @Bean(name = "bankDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bank")
    public DataSource bankDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(10);
        datasource.setMaxActive(100);
        // return new DTMDataSource(datasource);
        return datasource;
    }

    @Bean(name = "bankJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("bankDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BankBService bankService(@Qualifier("bankJdbcTemplate") JdbcTemplate bankJdbcTemplate,
        @Qualifier("bankDataSource") DataSource dataSource) {
        return new BankBService(bankJdbcTemplate, dataSource);
    }

    @Bean
    public DtmRocketMqProducer dtmRocketMqProducer() throws Exception {
        DtmRocketMqProducer producer = new DtmRocketMqProducer("dtm-rocket-mq");
        producer.setNamesrvAddr("http://127.0.0.1:9876");
        return producer;
    }

    @Bean
    public DefaultMQPushConsumer dtmConsumer(BankBService bankBService) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("dtm-rocket-mq");
        consumer.setNamesrvAddr("http://127.0.0.1:9876");
        consumer.subscribe("dtm-topic-mq", "*");
        /**
         * 消费的时候要保证幂等性, RocketMQ 为确保能正常消费, 会有概率性重复投递
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                String msg = new String(msgs.get(0).getBody());
                System.out.println("Receive msg : " + msg);
                String[] arr = msg.split("__");
                if (ConsumerStore.INST.alreadyConsume(arr[0])) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                /**
                 * 确定唯一标识, 已经消费不在重复消费, ROCKETMQ 会保证消息至少会被消费一次. 防止重复消费
                 */
                bankBService.transferOut(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]));
                ConsumerStore.INST.add(arr[0]);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        return consumer;
    }

    @Bean
    public RocketMqTemplate mqTemplate(@Qualifier("dtmRocketMqProducer") DtmRocketMqProducer producer,
        @Qualifier("dtmConsumer") DefaultMQPushConsumer consumer) {
        return new RocketMqTemplate(producer, consumer);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
