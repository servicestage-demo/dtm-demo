# DTM 使用 SpringCloud 框架的 DEMO

## 介绍
DTM 结合 SpringCloud框架的样例，其中invoke-service作为输入端，接受用户指令，通知bankCenter进行相应的操作，bankCenter再去调用bankA、bankB(初始化、转账、查询操作)。
包含 非侵入模式 和 TCC模式 下的使用

包含两种调用方式：
- feign调用：将bank-center和invoke-service中的application.yaml文件dtm:use-feign设置为true
- rest调用：将bank-center和invoke-service中的application.yaml文件dtm:use-feign设置为false
## 配置信息
- bank-center中application.yaml配置eureka服务的地址;dtm:use-feign若为true使用feign来调用，若为false使用rest来调用。 
- bankA-service中application.yaml配置eureka服务的地址;以及bankA数据库的信息 
- bankB-service中application.yaml配置eureka服务的地址;以及bankB数据库的信息 
- eureka-server中application.yaml配置eureka服务的功能
- invoke-service中application.yaml配置eureka服务的地址;dtm:use-feign若为true使用feign来调用，若为false使用rest来调用。
- dtm-config中dtmClientConfig.properties中配置dtm引擎信息

## 启动
### 代码启动
1. 完成配置信息修改
2. 分别启动EurekaServerApplication、BankCenterApplication、BankAMain、BankBMain、InvokeMain，在InvokeMain中输入相关命令

### jar包启动
1. 执行mvn clean install
2. 将5个jar包bankA-service-springcloud.jar、bankB-service-springcloud.jar、bank-center-springcloud.jar、eureka-server.jar、invoke-service-springcloud.jar分别放入不同的目录，同时目录中放置application.yaml以及dtm-config\dtmClientConfig.properties
3. 通过java -jar 分别启动eureka-server.jar、bank-center-springcloud.jar、bankA-service-springcloud.jar、bankB-service-springcloud.jar、invoke-service-springcloud.jar，在Invoke中输入相关命令
