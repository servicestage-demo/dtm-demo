# DTM 使用 SpringCloud 框架的样例

## 介绍
SpringCloud框架下DTM使用的样例，使得Spring Cloud框架下的项目可以快速接入DTM。

### 代码结构
- bank-center 金融中心服务 接受用户的初始化、转账、查询等命令再分别调用bankA服务和bankB服务 
- bankA-service bankA 服务的实现
- bankB-service bankB 服务的实现
- common 公共服务
- eureka-server 实现SpringCloud微服务实例的注册和发现
- invoke-service 作为交互的输入端 接受用户指令并通知bankCenter进行相应的操作
- mq-consumer RocketMQ的消息消费者
- mq-service RocketMQ的消息服务

### 功能模式
包含非侵入模式、TCC模式以及MQ消息接入DTM事务的使用  

#### 非侵入模式接入DTM
基于应用层实现的一种对业务逻辑无侵入的事务模型

#### TCC模式接入DTM
针对每个操作都要注册一个与其对应的确认和补偿，分为以下3个操作：
1. Try阶段：主要是对业务系统做检测及资源预留。
2. Confirm阶段：确认执行业务操作。
3. Cancel阶段：取消执行业务操作。

#### MQ消息接入DTM事务
MQ消息中的事务接入DTM，保证使用消息中间件时的事务一致性

## 样例启动前准备

### 开发环境准备
- 安装本地jdk环境 JDK版本要求：1.8版本
- 安装Maven环境 Maven版本要求：3.3.0及以上
- 启动DTM服务
- 创建Mysql8.0数据库


### 业务数据库准备
- 创建业务数据库banka和bankb
```sql
CREATE DATABASE banka;    
CREATE DATABASE bankb;
```
- 业务库中创建事务表：推荐通过修改DTM配置信息，自动创建DTM事务表dtm_tran_info，用来记录事务信息
    - 方式一：自动创建，修改dtm-config中dtmClientConfig.properties文件中auto-create-table-dtm-tran-info参数为on，开启自动创建
        ```properties
        auto-create-table-dtm-tran-info=on
        ```
    - 方式二：手动创建，执行如下SQL，分别在banka和bankb业务数据库创建事务表dtm_tran_info
        ```sql
        CREATE TABLE dtm_tran_info ( 
          branch_id bigint(20) NOT NULL, 
          global_id bigint(20) NOT NULL, 
          tran_info longblob NOT NULL, 
          info_status int(11) NOT NULL, 
          info_created datetime(0) NOT NULL, 
          ext varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL, 
          PRIMARY KEY (branch_id) USING BTREE 
        ) ENGINE = InnoDB AUTO_INCREMENT = 3571 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic; 
        ```

### 样例配置信息修改
- 配置eureka服务的地址 在bank-center bankA-service bankB-service invoke-service mq-consumer mq-service模块的application.yaml中配置正确的eureka服务地址
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: ${eureka_address}
```

- 配置请求的调用方式 修改bank-center invoke-service mq-consumer mq-service模块的application.yaml中mode为rest或feign 实现rest调用和feign调用
```yaml
dtm:
  invoke:
    mode: ${mode}
```

- 配置bankA和bankB的数据库地址 参考下面信息在bankA-service的application.yaml中配置bankA的数据库信息;在bankB-service的application.yaml中配置bankB的数据库信息;在mq-consumer的application.yaml中配置bankB的数据库信息
```yaml
spring:
  datasource:
    bank:
      username: ${db_user_name}
      password: ${db_user_pwd}
      url: jdbc:mysql://${db_url}?verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      driver-class-name: com.mysql.cj.jdbc.Driver
```

- 配置DTM配置信息 参考下面信息在dtm-config中dtmClientConfig.properties中配置启动的dtm服务信息
```properties
auto-create-table-dtm-tran-info=off
dtm-app-name=${app-name}
sc-server-address=${ip:port}
rpc-ssl-switch=off
```


## 样例启动

### 代码启动
1. 完成配置信息修改

2. 执行mvn clean install

3. 分别启动EurekaServerApplication、BankCenterApplication、BankAMain、BankBMain、InvokeMain、MqServerApplication、MqConsumerApplication，在InvokeMain中输入相关命令
```
[0] 初始化数据库， 重置账号资金;
[1] 查询 Bank A 和 Bank B 余额;
[2] 非侵入用例 -> DTM 事务 微服务场景调用;
[3] TCC用例 -> DTM 事务 微服务场景调用;
[4] DTM对接消息用例 -> DTM 事务 微服务场景调用;
[5] EXIST;
```

- 输入命令 **0** 初始化bankA和bankB数据库
```
请输入命令执行操作：(当前远程调用/feign)
0
2021-06-16 10:24:54.847  INFO 16772 --- [main] com.huawei.dtm.invoke.impl.FeignOpImpl   : Use feign invoke mode init bankA and bankB success
2021-06-16 10:24:54.847  INFO 16772 --- [main] c.h.dtm.invoke.service.TransferService   : Init bankA bankB success
```

- 输入命令 **1** 查询A、B银行的帐号余额
```
请输入命令执行操作：(当前远程调用/feign)
1
|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|
|             0|            1000000|            1000000|      2000000|
|             1|            1000000|            1000000|      2000000|
|             2|            1000000|            1000000|      2000000|
|             3|            1000000|            1000000|      2000000|
|             4|            1000000|            1000000|      2000000|
......
|           496|            1000000|            1000000|      2000000|
|           497|            1000000|            1000000|      2000000|
|           498|            1000000|            1000000|      2000000|
|           499|            1000000|            1000000|      2000000|
2021-06-15 21:01:41.243  INFO 16772 --- [main] c.h.dtm.invoke.service.TransferService: Run finish. total a 500000000,total b 500000000,sum 1000000000
```

- 输入命令 **2** 非侵入模式调用bankA和bankB的转账业务，接着输入运行的线程数量、每个线程的事务数量以及发生异常的概率值，输入格式为 线程数量:单线程事务数量:异常概率。最后输入命令 **1** 可查询执行结果
```
请输入命令执行操作：(当前远程调用/feign)
2
请输入线程数量:单线程事务数量:异常概率
10:10:50
......
total cost: 1846 ms
```


- 输入命令 **3** TCC模式调用bankA和bankB的转账业务，接着输入异常情况 0代表无异常发生 1代表发生异常。最后输入命令 **1** 可查询执行结果
```
请输入命令执行操作：(当前远程调用/feign)
3
是否模拟发生异常情况: 输入 0 无异常  输入 1 发生异常
1
```

- 输入命令 **4** 对接消息中间件调用bankA和bankB的转账业务，接着输入运行的线程数量、每个线程的事务数量以及发生异常的概率值，输入格式为 线程数量:单线程事务数量:异常概率。最后输入命令 **1** 可查询执行结果
```
请输入命令执行操作：(当前远程调用/feign)
4
请输入线程数量:单线程事务数量:异常概率
10:10:50
......
total cost: 1624 ms
```

- 输入命令 **5** 退出
