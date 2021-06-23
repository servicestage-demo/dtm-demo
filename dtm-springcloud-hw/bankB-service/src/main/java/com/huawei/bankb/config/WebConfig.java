package com.huawei.bankb.config;

import com.huawei.common.impl.BankBService;
import com.huawei.middleware.dtm.client.datasource.proxy.DTMDataSource;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * bankB 增加数据源
 */
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
        return new DTMDataSource(datasource);
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
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
