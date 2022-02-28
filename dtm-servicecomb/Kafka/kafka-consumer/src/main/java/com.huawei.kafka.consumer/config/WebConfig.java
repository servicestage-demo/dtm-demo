package com.huawei.kafka.consumer.config;

import com.huawei.common.impl.BankBService;
import com.huawei.kafka.consumer.ConsumerTemplate;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Properties;

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
    
    @Value("${spring.kafka.bootstrapServers}")
    private String bootstrapServers;

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
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "test-group");
        props.put("enable.auto.commit", true);
        props.put("auto.commit.interval.ms", 1000);
        props.put("session.timeout.ms", 30000);
        props.put("max.poll.records", 1000);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        // 只消费 read_committed数据
        props.put("isolation.level", "read_committed");
        props.put("auto.offset.reset", "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("dtm-kafka"));
        return consumer;
    }

    @Bean
    public ConsumerTemplate consumerTemplate(@Qualifier("kafkaConsumer") KafkaConsumer<String, String> consumer,
        @Qualifier("bankService") BankBService bankBService) {
        return new ConsumerTemplate(consumer, bankBService);
    }
}
