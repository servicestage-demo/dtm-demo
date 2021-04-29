package com.huawei.client;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication(scanBasePackages = "com.huawei", exclude = {DataSourceAutoConfiguration.class})
public class InvokeMain {

    private static NoninvasiveStarter noninvasiveStarter;

    public InvokeMain(NoninvasiveStarter bankService) {
        this.noninvasiveStarter = bankService;
    }

    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
        noninvasiveStarter.start();
    }
}