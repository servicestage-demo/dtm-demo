/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client.config;

import com.huawei.client.service.TransferService;
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

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.banka.url}")
    private String dbUrla;

    @Value("${spring.datasource.banka.username}")
    private String usernamea;

    @Value("${spring.datasource.banka.password}")
    private String passworda;

    @Value("${spring.datasource.banka.driver-class-name}")
    private String driverClassNamea;

    @Value("${spring.datasource.bankb.url}")
    private String dbUrlb;

    @Value("${spring.datasource.bankb.username}")
    private String usernameb;

    @Value("${spring.datasource.bankb.password}")
    private String passwordb;

    @Value("${spring.datasource.bankb.driver-class-name}")
    private String driverClassNameb;

    @Bean(name = "BankADataSource")
    @Qualifier("BankADataSource")
    @ConfigurationProperties(prefix = "spring.datasource.banka")
    public DataSource bankADataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrla);
        datasource.setUsername(usernamea);
        datasource.setPassword(passworda);
        datasource.setDriverClassName(driverClassNamea);
        datasource.setInitialSize(80);
        datasource.setMaxActive(120);
        return new DTMDataSource(datasource);
    }

    @Bean(name = "bankaJdbcTemplate")
    public JdbcTemplate bankaJdbcTemplate(@Qualifier("BankADataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "bankAService")
    public BankService bankAService(@Qualifier("bankaJdbcTemplate") JdbcTemplate bankaJdbcTemplate,
        @Qualifier("BankADataSource") DataSource dataSource) {
        return new BankService(bankaJdbcTemplate, dataSource);
    }



    @Bean(name = "BankBDataSource")
    @Qualifier("BankBDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bankb")
    public DataSource bankBDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlb);
        datasource.setUsername(usernameb);
        datasource.setPassword(passwordb);
        datasource.setDriverClassName(driverClassNameb);
        datasource.setInitialSize(80);
        datasource.setMaxActive(120);
        return new DTMDataSource(datasource);
    }

    @Bean(name = "bankbJdbcTemplate")
    public JdbcTemplate bankbJdbcTemplate(@Qualifier("BankBDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "bankBService")
    public BankService bankBService(@Qualifier("bankbJdbcTemplate") JdbcTemplate bankcJdbcTemplate,
        @Qualifier("BankBDataSource") DataSource dataSource) {
        return new BankService(bankcJdbcTemplate, dataSource);
    }

    @Bean(name = "transferService")
    public TransferService transferService(@Qualifier("bankAService") BankService bankAService,
        @Qualifier("bankBService") BankService bankBService) {
        return new TransferService(bankAService, bankBService);
    }
}
