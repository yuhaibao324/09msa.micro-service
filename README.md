![image](https://img.shields.io/badge/Spring%20Cloud-%E2%98%85%E2%98%85%E2%98%85-green.svg)
![image](https://img.shields.io/badge/Netflix-%E2%98%85%E2%98%85%E2%98%85-red.svg)

spring-cloud 微服务组件demo
===

![image](http://img.blog.csdn.net/20171018201759315?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcmlja2l5ZWF0/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

<table>
<tbody><tr>
<td>工程名</td>  <td>描述</td>  <td>端口</td>
</tr>
<tr>
<td>eureka-server</td>  <td>服务发现与注册中心</td>  <td>7070</td>
</tr>
<tr>
<td>ribbon</td>  <td>负载均衡器</td>  <td>7071</td>
</tr>
<tr>
<td>config-server</td>  <td>配置管理中心</td>  <td>7072</td>
</tr>
<tr>
<td>zuul</td>  <td>动态路由器</td>  <td>7073</td>
</tr>
<tr>
<td>service-A</td>  <td>A服务，用来测试服务间调用与路由</td>  <td>7074</td>
</tr>
<tr>
<td>service-B</td>  <td>B服务，整合Mybatis、PageHelper、Redis，整合接口限速方案，可选google Guava RateLimiter与自实现</td>  <td>7075</td>
</tr>
<tr>
<td>service-B2</td>  <td>B2服务，与B服务serviceId相同，用来测试负载均衡和容错</td>  <td>7076</td>
</tr>
<tr>
<td>hystrix-ribbon</td>  <td>负载均衡器的容错测试</td>  <td>7077</td>
</tr>
<tr>
<td>feign</td>  <td>声明式、模板化的HTTP客户端，可用来做负载均衡，较轻量</td>  <td>7078</td>
</tr>
<tr>
<td>hystrix-feign</td>  <td>feign的容错测试</td>  <td>7079</td>
</tr>
<tr>
<td>hystrix-dashboard</td>  <td>hystrix可视化监控台</td>  <td>7080</td>
</tr>
<tr>
<td>turbine</td>  <td>集群下hystrix可视化监控台</td>  <td>7081</td>
</tr>
<tr>
<td>sleuth</td>  <td>服务链路追踪</td>  <td>7082</td>
</tr>
<tr>
<td>service-admin</td>  <td>spring boot admin监控台</td>  <td>7088</td>
</tr>
</tbody></table>

环境：JDK1.8
组件依赖版本：Camden.SR5

```
 <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>1.8</java.version>
  </properties>
```

测试过程
===

1、最先启动的是eureka-server，并且你需要在整个测试过程中保持它的启动状态，因为它是注册中心，大多数服务必须依赖于它才能实现必要的功能。 <br>

	注册中心： eureka-server:  http://localhost:7070/

2、如果你想测试配置中心，可以先启动config-server，再启动service-A，按照规则来获取config-server的配置信息。 <br>

	配置中心-配置项目： config-server: http://localhost:7072/config-server/dev
	                  启动Service-A:  http://localhost:7074/from
	


3、如果你想测试负载均衡，则需启动ribbon、service-B、service-B2工程，在ribbon中配置自己需要的负载均衡策略，配置方法见：http://blog.csdn.net/rickiyeat/article/details/64918756 <br>


#### 创建redis
	micro-service\service-B\src\main\java\com\lovnx\web\RedisUtils.java

#### 创建数据库
	
	CREATE DATABASE IF NOT EXISTS sso  DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

	-- ----------------------------
	-- Table structure for interface_limit
	-- ----------------------------
	DROP TABLE IF EXISTS `interface_limit`;
	CREATE TABLE `interface_limit` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `interfaceId` int(11) DEFAULT NULL,
	  `unitTime` int(11) DEFAULT NULL,
	  `unitNum` int(11) DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
	
	-- ----------------------------
	-- Records of interface_limit
	-- ----------------------------
	INSERT INTO `interface_limit` VALUES ('1', '10', '60000', '10');


#### 测试
	   测试加法： service-A : http://localhost:7074/add?a=2&b=3

	测试加法： A服务调用B服务 : http://localhost:7074/testServiceB?a=2&b=3

             测试 robbin服务：  http://localhost:7071/add?a=3&b=6



4、如果你想测试路由，则需启动zuul工程，另外需保证service-B、service-B2、service-A其中一个或者多个工程处于启动状态，按照zuul工程的配置文件来进行相应的操作。 <br>

	
	# routes to serviceId
	zuul.routes.api-a.path=/api-a/**
	zuul.routes.api-a.serviceId=service-A

测     试：  [http://localhost:7073/api-a/add?a=3&b=4](http://localhost:7073/api-a/add?a=3&b=4) <br>
测试A调用B： [http://localhost:7073/api-a/testServiceB?a=3&b=4](http://localhost:7073/api-a/testServiceB?a=3&b=4)

	zuul.routes.api-b.path=/api-b/**
	zuul.routes.api-b.serviceId=ribbon

测     试：  [http://localhost:7073/api-b/add?a=3&b=4](http://localhost:7073/api-b/add?a=3&b=4)

	zuul.routes.api-cx.path=/service-b/**
	zuul.routes.api-cx.serviceId=service-b

测     试： [http://localhost:7073/service-b/add?a=3&b=4](http://localhost:7073/service-b/add?a=3&b=4)

	# routes to url
	zuul.routes.api-a-url.path=/api-a-url/**
	zuul.routes.api-a-url.url=http://localhost:7074/

测     试：  [http://localhost:7073/api-a-url/add?a=3&b=4](http://localhost:7073/api-a-url/add?a=3&b=4) <br>
测试A调用B：  [http://localhost:7073/api-a-url/testServiceB?a=3&b=4](http://localhost:7073/api-a-url/testServiceB?a=3&b=4)


5、如果你想查看spring boot admin监控台，则需启动service-admin、service-B工程，注意，spring boot admin工程需至少运行于JDK8环境。 <br>

service-admin监控地址: [http://localhost:7088/](http://localhost:7088/)


6、如果你想测试熔断功能，则需启动hystrix-ribbon与ribbon或者feign与hystrix-feign工程。 <br>


访问熔断地址： [http://localhost:7077/hystrix?a=3&b=4](http://localhost:7077/hystrix?a=3&b=4)
停止服务： service-b
访问熔断地址： [http://localhost:7077/hystrix?a=3&b=4](http://localhost:7077/hystrix?a=3&b=4)


7、如果你想查看断路器的监控台，请启动hystrix-dashboard（单机）和turbine（集群）工程，使用方法代码注释有写。 <br>

单机dashboard: [http://localhost:7080/hystrix/monitor](http://localhost:7080/hystrix/monitor)  <br>
监控地址：[http://localhost:7077/hystrix.stream](http://localhost:7077/hystrix.stream)

集群turbine： [进入hystrixdashboard页面，输入：localhost:7081/turbine.stream](localhost:7081/turbine.stream)


8、如果你想知道服务之间的调用情况，启动sleuth、service-B2、service-A。 <br>
	
ZipKin服务： [http://localhost:7082/](http://localhost:7082/)



