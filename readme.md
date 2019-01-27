## 接口平台客户端 for Java
采用原生Java编写，内置Gson,实现零依赖。Gson版权归Google所有，其余代码归rnkrsoft.com所有。

[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.rnkrsoft.platform/interface-platform-client4j-sdk/badge.svg)](http://search.maven.org/#search|ga|1|g%3A%22com.rnkrsoft.platform%22%20AND%20a%3A%22interface-platform-client4j-sdk%22)

```xml
<dependency>
    <groupId>com.rnkrsoft.platform</groupId>
    <artifactId>interface-platform-client4j-sdk</artifactId>
    <version>最新版本号</version>
</dependency>
```

## 原理

该客户端作为接口平台的一部分，简化接口调用，提供统一的接口界面和安全算法保障。
调用接口时向接口平台发送的报文包含通信信息和业务信息。
接口通信层请求

```json
{
	"channel": "public",//通道号
	"txNo": "000", //交易码
	"version": "1",//版本号
	"sessionId": "", //会话号
	"uid": null,   //用户号
	"uic": null,   //用户设备识别码
	"deviceManufacturer":"xiaomi",
	"deviceModel":"S2",
	"deviceType":"Android",
	"osVersion":"6.1.2",
	"appVersion":"1.0.0",
	"timestamp": "20180724155700743", //发送请求的时间戳
	"token": "", //TOKEN令牌
	"sign": "", //data对应的签字信息
	"lng": 0.0, //经度
	"lat": 0.0 //纬度
	"data": "{}", //业务对象序列化的JSON字符串  例如:LoginRequest
}
```

接口通信层应答

```json
{
	"data": "{}", //业务对象序列化的JSON字符串  例如:LoginResponse
	"sign":  "",   //data对应的签字信息
	"updateMetadataTimestamp": 0, //更新接口元信息的毫秒数
	"updateConfigureTimestamp": 0, //更新网关配置的毫秒数
	"code": "000", //通信层应答代码
	"desc": "通信成功" //通信层应答描述
}
```



原生Java用法

用户服务接口定义

```java
@ApidocService("用户服务")
public interface UserService{
    @ApidocInterface(value = "登录", name = "001", version = "1")
    LoginResponse login(LoginRequest request);
}
```

登录请求对象
```java
public class LoginRequest implements Serializable {
    @ApidocElement(value = "手机号")
    String mobileNo;
     @ApidocElement(value = "密码")
    String password;

    //--------------------------
    //getter or setter
}
```

登录应答对象
```java
public class LoginResponse extends AbstractResponse implements TokenAble{
    @ApidocElement(value = "令牌")
    String token;

    //--------------------------
    //getter or setter
}
```

客户端进行同步方式调用远程接口
```java
    ServiceFactory serviceFactory = new ServiceFactory();
    serviceFactory.settingConfigure(false, "gateway-configure.xxx.com", 80, "/configure");
//设置接口连接器实现

    serviceFactory.getServiceConfigure().setInterfaceConnectorClass(HttpInterfaceConnector.class);
    //设置远程配置获取失败后的退回接口服务器信息
    serviceFactory.settingFallback("test-channel", false, "localhost", 80, "/api");
    serviceFactory.settingFallback("public", false, "localhost", 80, "/api");
    //如果不使用TOKEN作为密码时的固定密码
    serviceFactory.setPassword("1234567890123456");
    //加密时或者解密时的秘钥向量 默认配置，在configure无法成功获取时使用
    serviceFactory.setKeyVector("1234567890654321");
	//用户版本号
    serviceFactory.setAppVersion("4.0.0");
	//注册定位信息提供者
    serviceFactory.registerLocationProvider(new LocationProvider() {
        @Override
        public void locate(LocationStore locationStore) {
            locationStore.refreshLocation(new Location(1, 2));
        }
    });
    //是否自动获取定位信息
    serviceFactory.getServiceConfigure().setAutoLocate(true);
    serviceFactory.getServiceConfigure().setMacAddress("44-45-53-54-00-00");
    //用户标识
    serviceFactory.getServiceConfigure().setUid("sssss");
    serviceFactory.getServiceConfigure().setUic("2542563b-a153-48af-84d4-d40542c8bc3b");
	//向客户端中注册用户服务类, 安卓环境下只能使用该方法注册服务
    serviceFactory.addServiceClasses(UserService.class);
    //初始化
    //serviceFactory.init();
    //如果需要在安卓平台上处理初始化错误则需要调用另一个init
    serviceFactory.init(true, new AsyncHandler() {
        @Override
        public void fail(String code, String desc, String detail) {
            System.out.println(desc);
        }

        @Override
        public void success(Object response) {
            System.out.println(response);
        }
    });

    //获取业务的门面类
    UserService userService = ServiceFactory.get(UserService.class);

    //构建一个业务对象
    LoginRequest request = new LoginRequest();
    request.setMobileNo("123456789"):
    request.setPassword("this is password");
	//如果通信错误将抛出异常
    LoginResponse response = userService.login(request);
    System.out.println(response);
    if(response.getCode().equals("0000")){//业务执行成功
        System.out.println(Thread.currentThread() + ":" + response.getToken());
    }else{//业务执行失败
        System.out.println(Thread.currentThread() + ": 登录失败 ");
        System.out.println(response.getCode() + ":" + response.getDesc());
    }
```

客户端进行异步方式调用远程接口



```java
 ServiceFactory serviceFactory = new ServiceFactory();
    serviceFactory.settingConfigure(false, "gateway-configure.xxx.com", 80, "/configure");
//设置接口连接器实现
serviceFactory.getServiceConfigure().setInterfaceConnectorClass(HttpInterfaceConnector.class);
    //设置远程配置获取失败后的退回接口服务器信息
serviceFactory.settingFallback("test-channel", false, "localhost", 80, "/api");
    serviceFactory.settingFallback("public", false, "localhost", 80, "/api");
    //如果不使用TOKEN作为密码时的固定密码
    serviceFactory.setPassword("1234567890123456");
    //加密时或者解密时的秘钥向量 默认配置，在configure无法成功获取时使用
    serviceFactory.setKeyVector("1234567890654321");
	//用户版本号
    serviceFactory.setAppVersion("4.0.0");
	//注册定位信息提供者
    serviceFactory.registerLocationProvider(new LocationProvider() {
        @Override
        public void locate(LocationStore locationStore) {
            locationStore.refreshLocation(new Location(1, 2));
        }
    });
    //是否自动获取定位信息
    serviceFactory.getServiceConfigure().setAutoLocate(true);
    serviceFactory.getServiceConfigure().setMacAddress("44-45-53-54-00-00");
    //用户标识
    serviceFactory.getServiceConfigure().setUid("sssss");
    serviceFactory.getServiceConfigure().setUic("2542563b-a153-48af-84d4-d40542c8bc3b");
	//向客户端中注册用户服务类, 安卓环境下只能使用该方法注册服务
    serviceFactory.addServiceClasses(UserService.class);
    //初始化
    serviceFactory.init();
    //如果需要在安卓平台上处理初始化错误则需要调用另一个init
    serviceFactory.init(true, new AsyncHandler() {
        @Override
        public void fail(String code, String desc, String detail) {
            System.out.println(desc);
        }

        @Override
        public void success(Object response) {
            System.out.println(response);
        }
    });
    //获取业务的门面类
    UserService userService = ServiceFactory.get(UserService.class);

    //构建一个业务对象
    LoginRequest request = new LoginRequest();
    request.setMobileNo("123456789"):
    request.setPassword("this is password");
    /**
             * 通过构建一个异步处理器对象
             * 在调用成功或者通信发生错误的情况下会调用处理器的success和fail对应的方法。
             * 异步处理器中的代码将在线程池中执行，并不在调用线程中执行
             */
    userService.login(request, new AsyncHandler<LoginResponse>{
        //调用远程接口通信失败时执行
        public void fail(String code, String desc, String detail) {
            System.out.println(Thread.currentThread() + ":--------------------->" +  code + ":" + desc + detail);
        }
        //调用远程接口成功时执行
        public void success(LoginResponse response) {
            //打印调试日志
            System.out.println(ServiceFactory.getServiceConfigure().getLogs());
            System.out.println(Thread.currentThread() + ":" + response);
            if(response.getCode().equals("0000")){//业务执行成功
                System.out.println(Thread.currentThread() + ":" + response.getToken());
            }else{//业务执行失败
                System.out.println(Thread.currentThread() + ": 登录失败 ");
                System.out.println(response.getCode() + ":" + response.getDesc());
            }
        }
    });
```

详细参见 https://github.com/rnkrsoft/interface-platform-client4j-demo 项目中的演示例子



## Spring集成用法

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!-- 配置接口平台配置对象 -->
    <bean class="com.rnkrsoft.platform.client.spring.InterfacePlatformClientConfigure">
        <!--配置远程网关配置-->
        <property name="configure">
            <bean class="com.rnkrsoft.platform.client.spring.ConfigureAddress">
                <property name="ssl" value="false"/>
                <property name="host" value="localhost"/>
                <property name="port" value="80"/>
                <property name="contextPath" value="configure"/>
            </bean>
        </property>
        <!--失败回退网关地址-->
        <property name="fallbackGateways">
            <array>
                <bean class="com.rnkrsoft.platform.client.spring.GatewayAddress">
                    <property name="channel" value="public"/>
                    <property name="ssl" value="false"/>
                    <property name="host" value="localhost"/>
                    <property name="port" value="80"/>
                    <property name="contextPath" value="api"/>
                </bean>
                <bean class="com.rnkrsoft.platform.client.spring.GatewayAddress">
                    <property name="channel" value="test-channel"/>
                    <property name="ssl" value="false"/>
                    <property name="host" value="localhost"/>
                    <property name="port" value="80"/>
                    <property name="contextPath" value="api"/>
                </bean>
            </array>
        </property>
        <!--密钥向量-->
        <property name="keyVector" value="1234567890654321"/>
        <!--固定密码-->
        <property name="password" value="1234567890"/>
        <!--拉取远程网关配置间隔 大于0周期性拉取-->
        <property name="fetchConfigureIntervalSecond" value="10"/>
        <!--拉取远程元信息间隔 大于0周期性拉取-->
        <property name="fetchMetadataIntervalSecond" value="20"/>
        <!--扫描服务包路径-->
        <property name="basePackages">
            <array>
                <value>com.rnkrsoft.platform.client.demo</value>
            </array>
        </property>
    </bean>
</beans>
```

用ClassPathApplicationContext或者注解方式，可以直接进行注入

```java
package com.rnkrsoft.platform.client.demo.service;

import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Created by rnkrsoft.com on 2019/1/24.
 */

@ContextConfiguration("classpath*:applicationContext-platform.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class HelloServiceTest {

    @Autowired
    HelloService helloService;
    @Test
    public void testHelloSync() throws Exception {
        HelloRequest request = new HelloRequest();
        request.setName("test sync");
        HelloResponse response = helloService.hello(request);
        System.out.println(response);
        Thread.sleep(60 * 1000);
    }

    @Test
    public void testHelloAsync() throws Exception {
        HelloRequest request = new HelloRequest();
        request.setName("test async");
        AsyncTask asyncTask = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println("--------------->" + desc);
            }

            @Override
            public void success(HelloResponse response) {
                System.out.println("--------------->" + response);
            }
        });
        asyncTask.get();
    }
}
```



## 实现APP登录

假设登录服务为UserService重的login方法

```java
@ApidocService("用户服务")
public interface UserService{
    @ApidocInterface(value = "登录", name = "001", version = "1")
    LoginResponse login(LoginRequest request);
}
```



```java
public class LoginRequest implements Serializable {
    @ApidocElement(value = "手机号")
    String mobileNo;//以手机号作为用户标识
     //@ApidocElement(value = "密码")
    //String password; 可以使用短信验证码或者密码方式

    //--------------------------
    //getter or setter
}
```

应答对象是重点，重点，重点



```java
@ToString
public class HelloResponse extends AbstractResponse {
    @ApidocElement("TOKEN")
    String token;
    
    //--------------------------
    //getter or setter
}
```

如果只是这样，是不能实现安全机制的，需要如下编写，则可实现TOKEN的自动注入

```java
@ToString
public class HelloResponse extends AbstractResponse implements TokenAble{
    @ApidocElement("TOKEN")
    String token;
    
    //--------------------------
    //getter or setter
}
```

区别在于实现了TokenAble接口，他可以实现在返回应答中，自动提取Token方法ServiceFactory中，在需要登录验证的接口中Request对象自动填入TOKEN值。如果TOKEN错误则会收到错误InterfaceRspCode.TOKEN_ILLEGAL("020","TOKEN无效").



## 错误码

| 枚举名称 | 代码 | 描述 | 原因 |
| ---- | ---- | ---- | ---- |
|    SUCCESS| 000| 成功 | 正常情况 |
|   PARAM_IS_NULL| 001| 参数不能为空 | 有参数为null时返回 |
|    TIMESTAMP_ILLEGAL| 002| 系统时间与北京时间不一致，请校准后再重试 | 调用端时间与服务器端时间不一致导致 |
|   REQUEST_SIGN_ILLEGAL| 003| 请求签字信息无效 | 请求时，服务器无法校验签字信息 |
|    ACCOUNT_HAS_LOGIN_ON_OTHER_DEVICE| 004| 您的帐号已在其它设备登录 | 存在其他客户端登录相同的账号引发 |
|   PARAM_TYPE_NOT_MATCH| 005| 参数类型不匹配 |  |
|    USER_IS_NOT_LOGIN| 006| 用户未登录 | 调用需要校验的业务接口前，为调用登录接口导致服务器没有TOKEN值 |
|   INTERFACE_IS_ILLEGAL| 007| 接口无效 |  |
|    INTERFACE_IS_DEV| 008| 接口正在开发 |  |
|   INTERFACE_HAPPEN_UNKNOWN_ERROR| 009| 接口发生未知错误 | 服务器在执行业务代码时发生了错误 |
|    INTERFACE_EXECUTE_HAPPENS_ERROR| 010| 接口执行发生错误 | 服务器在执行业务代码时发生了错误 |
|   INTERFACE_NOT_DEFINED| 011| 接口未定义 | 服务器中未配置正在调用的交易码映射关系 |
|    INTERFACE_SERVICE_CLASS_NOT_FOUND| 012| 接口对应的服务类不存在 | 服务器中的业务代码不存在交易码映射的服务类，可能为开发或者部署导致 |
|   INTERFACE_SERVICE_METHOD_NOT_EXIST| 013| 接口对应的服务方法不存在 | 服务器中的业务代码存在服务类，但是不存在交易码映射的方法，可能为开发或者部署导致 |
|    INTERFACE_EXISTS_OTHER_VERSION| 014| 存在其他版本的接口，但不存在当前版本号的接口 |  |
|   CHANNEL_NOT_EXISTS| 015| 接口通道不存在 | 调用服务器上的接口时，服务器未配置channel中指定的通道 |
|    DECRYPT_HAPPENS_FAIL| 016| 解密发生错误 | 服务器在进行请求数据解密时发生错误 |
|   VERIFY_HAPPENS_FAIL| 017| 验签发生错误 | 服务器在进行请求数据验证签字时发生错误 |
|    SIGN_HAPPENS_FAIL| 018| 签字发生错误 | 服务器在进行应答数据签字时发生错误 |
|   ENCRYPT_HAPPENS_FAIL| 019| 加密发生错误 | 服务器在进行应答数据加密时发生错误 |
|    TOKEN_ILLEGAL| 020| TOKEN无效 | 调用时发送的TOKEN值在服务器端不存在，有可能被攻击也有可能调用了不同的环境导致 |
|   UPDATE_REQUEST_HAPPENS_ERROR| 021| 更新请求信息发生错误 |  |
|    UPDATE_RESPONSE_HAPPENS_ERROR| 022| 更新应答信息发生错误 |  |
|   REQUEST_DATA_IS_NULL| 023| 请求数据为空 | 调用请求时未传data数据域中的内容导致 |
|    TX_NO_IS_NULL| 024| 交易码为空 | 调用请求时未传txNo数据域中的交易码导致 |
|   TOKEN_SERVICE_NOT_EXISTS| 025| TOKEN服务不存在 | 服务器端未配置TokenService实现导致 |
|    DATA_CONVERT_HAPPENS_ERROR| 026| 数据转换发生错误 |  |
|   DATA_CONVERT_SERVICE_EXISTS_ERROR| 027| 数据转换服务存在错误 |  |
|    DEVICE_CAN_NOT_ACCESS_INTERNET| 028  |设备不能访问互联网||
|   NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM| 029 |不支持的加密解密算法||
|    NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM| 030 |不支持的签字验签算法||
|   INVALID_COMMUNICATION_MESSAGE| 031 |无效的通信报文||
|    RESPONSE_DATA_IS_NULL| 032| 应答数据为空 |  |
|   SOCKET_PERMISSION_DENIED| 033| 无网络权限 | 客户端在调用时无网络权限，一般发生在安卓平台上，需要配置网络权限 |
|    INTERFACE_PLATFORM_GATEWAY_NOT_FOUND| 034| 接口平台网关未发现 |  |
|   CONFIGURE_GATEWAY_NOT_FOUND| 035| 配置网关未发现 |  |
|    USE_TOKEN_AS_PASSWORD_BUT_TOKEN_IS_NULL| 036 |使用TOKEN作为密码，但是TOKEN为空||
|   ENCRYPT_DATA_IS_NULL| 037| 加密数据为空 |  |
|    DECRYPT_DATA_IS_NULL| 038| 解密数据为空 |  |
|   SYNC_SEND_SERVICE_HAPPENS_ERROR| 039| 同步发送服务发生错误 |  |
|    UNSUPPORTED_DIRECTION| 040| 不支持的方向 |  |
|   GATEWAY_CONFIGURE_IS_NOT_CONFIG| 041| 网关配置信息未配置 |  |
|    SIGN_ILLEGAL| 042 |签字信息无效||
|   VERSION_ILLEGAL| 043| 版本号无效 |  |
|    INTERFACE_CONNECTOR_IS_NOT_CONFIG| 044| 接口连接器未配置 |  |
|   OVER_FLOW_LIMITING| 045| 超过流量限制 |  |
|    CONVERT_REQUEST_DATA_FAIL| 046| 转换请求数据失败 |  |
|   CONVERT_RESPONSE_DATA_FAIL| 047| 转换应答数据失败 |  |
|    REQUEST_DATA_ILLEGAL| 048| 请求数据无效，请重新输入！ |  |
|   ENVIRONMENT_IS_ALREADY_DISABLED| 049| 环境已经禁用 |  |
|    LOCATION_PROVIDER_IS_NOT_CONFIG| 050| 定位提供者未配置！ |  |
|   CONFIGURE_PROVIDER_IS_NOT_CONFIG| 051| 配置提供者未配置！ |  |
|    CLIENT_IS_NOT_INITIALIZED| 052| 客服端未初始化！ |  |
|   INTERFACE_FALLBACK_GATEWAY_IS_NOT_CONFIG| 053| 接口失败回退网关未配置！ |  |
|    FETCH_INTERFACE_METADATA_IS_FAILURE| 054| 获取接口元信息失败！ |  |
|   INTERFACE_HAPPENS_SERVER_ERROR| 055| 执行接口发生服务器错误！ |  |
|    SYSTEM_MAINTENANCE| 998| 系统正在维护，请稍后重试！ |  |
|   FAIL| 999| 错误 |  |