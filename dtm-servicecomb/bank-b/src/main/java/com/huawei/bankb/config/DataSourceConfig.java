/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.bankb.config;

import com.huawei.common.impl.BankBService;
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
 * bankB 增加数据源
 */
@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.bankb.url}")
    private String dbUrlB;

    @Value("${spring.datasource.bankb.username}")
    private String usernameB;

    @Value("${spring.datasource.bankb.password}")
    private String passwordB;

    @Value("${spring.datasource.bankb.driver-class-name}")
    private String driverClassNameB;

    @Bean(name = "bankDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bankb")
    public DataSource bankDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlB);
        datasource.setUsername(usernameB);
        datasource.setPassword(passwordB);
        datasource.setDriverClassName(driverClassNameB);
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
    public BankBService bankService(@Qualifier("bankJdbcTemplate") JdbcTemplate bankJdbcTemplate,
        @Qualifier("bankDataSource") DataSource dataSource) {
        return new BankBService(bankJdbcTemplate, dataSource);
    }
}
