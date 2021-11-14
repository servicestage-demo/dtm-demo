package com.huawei.dubbo.invoke;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class FailedEventListener implements ApplicationListener<ApplicationFailedEvent> {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FailedEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        LOGGER.error(event.getException().getMessage());
        System.exit(0);
    }
}
