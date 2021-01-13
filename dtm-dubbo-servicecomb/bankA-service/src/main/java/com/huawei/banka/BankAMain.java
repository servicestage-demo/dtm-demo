package com.huawei.banka;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class BankAMain {
  public static void main(String[] args) {
    new SpringApplicationBuilder(BankAMain.class).web(WebApplicationType.NONE).run(args);
  }
}
