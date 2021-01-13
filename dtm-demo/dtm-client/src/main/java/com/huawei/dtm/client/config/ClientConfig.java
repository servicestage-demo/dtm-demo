package com.huawei.dtm.client.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.huawei.common.impl.BankAService;
import com.huawei.common.impl.BankBService;
import com.huawei.middleware.dtm.client.datasource.proxy.DTMDataSource;
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
public class ClientConfig {
    @Value("${spring.datasource.banka.url}")
    private String dbUrlA;

    @Value("${spring.datasource.banka.username}")
    private String usernameA;

    @Value("${spring.datasource.banka.password}")
    private String passwordA;

    @Value("${spring.datasource.banka.driver-class-name}")
    private String driverClassNameA;


    @Value("${spring.datasource.bankb.url}")
    private String dbUrlB;

    @Value("${spring.datasource.bankb.username}")
    private String usernameB;

    @Value("${spring.datasource.bankb.password}")
    private String passwordB;

    @Value("${spring.datasource.bankb.driver-class-name}")
    private String driverClassNameB;

    @Bean(name = "bankADataSource")
    @ConfigurationProperties(prefix = "spring.datasource.banka")
    public DataSource bankADataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlA);
        datasource.setUsername(usernameA);
        datasource.setPassword(passwordA);
        datasource.setDriverClassName(driverClassNameA);
        datasource.setInitialSize(10);
        datasource.setMaxActive(100);
        return new DTMDataSource(datasource);
    }

    @Bean(name = "bankAJdbcTemplate")
    public JdbcTemplate bankAJdbcTemplate(@Qualifier("bankADataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BankAService bankAService(@Qualifier("bankAJdbcTemplate") JdbcTemplate bankJdbcTemplate,
                                     @Qualifier("bankADataSource") DataSource dataSource) {
        return new BankAService(bankJdbcTemplate, dataSource);
    }


    @Bean(name = "bankBDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bankb")
    public DataSource bankDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlB);
        datasource.setUsername(usernameB);
        datasource.setPassword(passwordB);
        datasource.setDriverClassName(driverClassNameB);
        datasource.setInitialSize(10);
        datasource.setMaxActive(100);
        return new DTMDataSource(datasource);
    }

    @Bean(name = "bankBJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("bankBDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BankBService bankService(@Qualifier("bankBJdbcTemplate") JdbcTemplate bankJdbcTemplate,
                                    @Qualifier("bankBDataSource") DataSource dataSource) {
        return new BankBService(bankJdbcTemplate, dataSource);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
