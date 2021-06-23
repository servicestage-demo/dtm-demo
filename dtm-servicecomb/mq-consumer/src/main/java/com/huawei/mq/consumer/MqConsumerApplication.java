package com.huawei.mq.consumer;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqConsumerApplication {
    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
    }
}
