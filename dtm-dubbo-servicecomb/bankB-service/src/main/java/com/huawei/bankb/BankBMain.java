package com.huawei.bankb;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class BankBMain {
  public static void main(String[] args) {
    new SpringApplicationBuilder(BankBMain.class).web(WebApplicationType.NONE).run(args);
  }
}
