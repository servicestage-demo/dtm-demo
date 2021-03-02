/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.bankb.config;

import com.huawei.common.BankService;
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

    @Bean(name = "BankBDataSource")
    @Qualifier("BankBDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bankb")
    public DataSource bankBDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlB);
        datasource.setUsername(usernameB);
        datasource.setPassword(passwordB);
        datasource.setDriverClassName(driverClassNameB);
        datasource.setInitialSize(10);
        datasource.setMaxActive(20);
        return new DTMDataSource(datasource);
    }

    @Bean(name = "bankbJdbcTemplate")
    public JdbcTemplate bankbJdbcTemplate(@Qualifier("BankBDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BankService bankBService(@Qualifier("bankbJdbcTemplate") JdbcTemplate bankbJdbcTemplate,
        @Qualifier("BankBDataSource") DataSource dataSource) {
        return new BankService(bankbJdbcTemplate, dataSource);
    }
}
