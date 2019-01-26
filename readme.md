# 接口平台客户端 for Java
采用原生Java编写，内置Gson,实现零依赖。Gson版权归Google所有，其余代码归rnkrsoft.com所有。

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
	"fetchMetadata": false, //是否需要拉取元信息定义
	"fetchConfigure": false, //是否拉取新的配置信息
	"code": "000", //通信层应答代码
	"desc": "通信成功" //通信层应答描述
}
```



## 用法

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
public class LoginRequest{
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
public class LoginResponse{
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
    //是否自动获取定位信息
    serviceFactory.getServiceConfigure().setAutoLocate(true);
    serviceFactory.getServiceConfigure().setDeviceManufacturer("huawei");
    serviceFactory.getServiceConfigure().setDeviceModel("honer6");
    serviceFactory.getServiceConfigure().setMacAddress("44-45-53-54-00-00");
    //用户版本号
    serviceFactory.getServiceConfigure().setAppVersion("4.0.0");
    //如果不使用TOKEN作为密码时的固定密码
    serviceFactory.getServiceConfigure().setPassword("1234567890123456");
    //加密时或者解密时的秘钥向量 默认配置，在configure无法成功获取时使用
    serviceFactory.getServiceConfigure().setKeyVector("1234567890654321");
    //用户标识
    serviceFactory.getServiceConfigure().setUid("sssss");
    serviceFactory.getServiceConfigure().setUic("2542563b-a153-48af-84d4-d40542c8bc3b");
    //启用DEBUG模式，会进行日志的记录
    serviceFactory.getServiceConfigure().enableDebug();
	//启用啰嗦模式，只在DEBUG模式启动的情况下生效
    serviceFactory.getServiceConfigure().enableVerbosLog();
	//向客户端中注册用户服务类, 安卓环境下只能使用该方法注册服务
    serviceFactory.addServiceClasses(UserService.class);
	//扫描指定包路径下的标注有@ApidocService的接口类，与addServiceClasses可以同时使用
    serviceFactory.scan();
    //增加需要调用的服务
    //serviceFactory.addServiceClasses(HelloService.class);
    //初始化
    serviceFactory.init(0);
    //如果需要处理拉取远程的接口错误 与 ServiceFactory.init(0)不能并列使用
    serviceFactory.init();

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
	ServiceFactory.settingConfigure(false, "gateway-configure.xxx.com", 80, "/configure");
         //设置接口连接器实现
        ServiceFactory.getServiceConfigure().setInterfaceConnectorClass(HttpInterfaceConnector.class);
        //设置远程配置获取失败后的退回接口服务器信息
        ServiceFactory.settingFallback("test-channel", false, "localhost", 80, "/api");
        ServiceFactory.settingFallback("public", false, "localhost", 80, "/api");
        //是否自动获取定位信息
        ServiceFactory.getServiceConfigure().setAutoLocate(true);
        ServiceFactory.getServiceConfigure().setDeviceManufacturer("huawei");
        ServiceFactory.getServiceConfigure().setDeviceModel("honer6");
        ServiceFactory.getServiceConfigure().setMacAddress("44-45-53-54-00-00");
        //用户版本号
        ServiceFactory.getServiceConfigure().setAppVersion("4.0.0");
        //如果不使用TOKEN作为密码时的固定密码
        ServiceFactory.getServiceConfigure().setPassword("1234567890123456");
        //加密时或者解密时的秘钥向量 默认配置，在configure无法成功获取时使用
        ServiceFactory.getServiceConfigure().setKeyVector("1234567890654321");
        //用户标识
        ServiceFactory.getServiceConfigure().setUid("sssss");
        ServiceFactory.getServiceConfigure().setUic("2542563b-a153-48af-84d4-d40542c8bc3b");
        //启用DEBUG模式，会进行日志的记录
        ServiceFactory.getServiceConfigure().enableDebug();
    	//启用啰嗦模式，只在DEBUG模式启动的情况下生效
        ServiceFactory.getServiceConfigure().enableVerbosLog();
    	//向客户端中注册用户服务类, 安卓环境下只能使用该方法注册服务
        ServiceFactory.addServiceClasses(UserService.class);
    	//扫描指定包路径下的标注有@ApidocService的接口类，与addServiceClasses可以同时使用
        ServiceFactory.scan();
        //增加需要调用的服务
        //ServiceFactory.addServiceClasses(HelloService.class);
        //初始化
        ServiceFactory.init(0);
        //如果需要处理拉取远程的接口错误 与 ServiceFactory.init(0)不能并列使用
        ServiceFactory.init();
        ServiceFactory.fetchRemoteMetadata(new AsyncHandler<Boolean>() {
            @Override
            public void fail(String code, String desc, String detail) {
                //拉取远程的接口元信息定义失败的提示
                System.out.println("-------------" + desc);
            }

            @Override
            public void success(Boolean response) {
                //拉取远程的接口元信息定义成功后提示信息或者后续操作
                System.out.println("-------------" + "success");
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

