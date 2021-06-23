/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.banka.config;

import com.huawei.common.impl.BankAService;
import com.huawei.middleware.dtm.client.datasource.proxy.DTMDataSource;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
/**
 * bankA 增加数据源
 */
@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.banka.url}")
    private String dbUrlA;

    @Value("${spring.datasource.banka.username}")
    private String usernameA;

    @Value("${spring.datasource.banka.password}")
    private String passwordA;

    @Value("${spring.datasource.banka.driver-class-name}")
    private String driverClassNameA;

    @Bean(name = "bankDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.banka")
    public DataSource bankDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlA);
        datasource.setUsername(usernameA);
        datasource.setPassword(passwordA);
        datasource.setDriverClassName(driverClassNameA);
        datasource.setInitialSize(10);
        datasource.setMaxActive(20);
        return new DTMDataSource(datasource);
        // return datasource;
    }

    @Bean(name = "bankJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("bankDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BankAService bankService(@Qualifier("bankJdbcTemplate") JdbcTemplate bankJdbcTemplate,
        @Qualifier("bankDataSource") DataSource dataSource) {
        return new BankAService(bankJdbcTemplate, dataSource);
    }
}
