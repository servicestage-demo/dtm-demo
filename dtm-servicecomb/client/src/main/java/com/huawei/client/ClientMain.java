package com.huawei.client;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication(scanBasePackages = "com.huawei")
public class ClientMain {

    private static NoninvasiveStarter noninvasiveStarter;

    public ClientMain(NoninvasiveStarter bankAService) {
        this.noninvasiveStarter = bankAService;
    }

    public static void main(String[] args) throws Exception {
        //Log4jUtils.init();
        BeanUtils.init();
        noninvasiveStarter.start();
    }
}