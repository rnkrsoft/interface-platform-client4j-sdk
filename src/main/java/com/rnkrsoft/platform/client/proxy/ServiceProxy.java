package com.rnkrsoft.platform.client.proxy;

import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.exception.InitException;
import com.rnkrsoft.platform.client.invoker.AndroidAsyncInvoker;
import com.rnkrsoft.platform.client.invoker.JavaAsyncInvoker;
import com.rnkrsoft.platform.client.invoker.SyncInvoker;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.TokenAble;
import com.rnkrsoft.platform.protocol.TokenReadable;
import com.rnkrsoft.platform.protocol.TokenWritable;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.Future;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class ServiceProxy<T> implements InvocationHandler {
    static Logger log = LoggerFactory.getLogger(ServiceProxy.class);
    ServiceFactory serviceFactory;
    /**
     * 服务接口类
     */
    Class<T> serviceClass;

    public ServiceProxy(ServiceFactory serviceFactory, Class<T> serviceClass) {
        this.serviceFactory = serviceFactory;
        this.serviceClass = serviceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.debug("execute invoke method");
        if (args.length == 1) {
            if (serviceClass != PublishService.class && !serviceFactory.isInit()) {
                throw new InitException("client is not initialization, please execute init() or init(boolean, AsyncHandler)!");
            }
            Object request = args[0];
            Class requestClass = method.getParameterTypes()[0];
            Class responseClass = method.getReturnType();
            if (request instanceof TokenWritable || request instanceof TokenAble) {
                ((TokenWritable) request).setToken(serviceFactory.getServiceConfigure().getToken());
            }
            log.debug("synchronous execute remote service request '{}'", request);
            SyncInvoker invoker = new SyncInvoker();
            Object response = invoker.call(this.serviceFactory, serviceClass, method.getName(), requestClass, responseClass, request);
            log.debug("synchronous execute remote service response '{}'", response);
            return response;
        } else if (args.length == 2) {
            Object request = args[0];
            if (request instanceof TokenWritable || request instanceof TokenAble) {
                ((TokenWritable) request).setToken(serviceFactory.getServiceConfigure().getToken());
            }
            AsyncHandler asyncHandler = (AsyncHandler) args[1];
            Class requestClass = method.getParameterTypes()[0];
            ParameterizedType asyncHandlerClass = (ParameterizedType) method.getGenericParameterTypes()[1];
            Class responseClass = (Class) asyncHandlerClass.getActualTypeArguments()[0];
            if (serviceClass != PublishService.class && !serviceFactory.isInit()) {
                asyncHandler.fail(InterfaceRspCode.CLIENT_IS_NOT_INITIALIZED, "client is not initialization, please execute init() or init(boolean, AsyncHandler)!");
                return null;
            }
            log.debug("asynchronous execute remote service request '{}'", request);
            if (JavaEnvironmentDetector.isAndroid()) {
                AndroidAsyncInvoker<Object> asyncInvoker = new AndroidAsyncInvoker<Object>(this.serviceFactory, log.getSessionId(), serviceClass, method.getName(), requestClass, responseClass, asyncHandler);
                android.os.AsyncTask asyncTask = asyncInvoker.execute(request);
                if (method.getReturnType() == android.os.AsyncTask.class) {
                    return asyncTask;
                } else if (method.getReturnType() == Future.class) {
                    throw new RuntimeException("on Android Platform do not support 'java.util.concurrent.Future' as return value.");
                } else {
                    return null;
                }
            } else {
                JavaAsyncInvoker<Object> javaAsyncInvoker = new JavaAsyncInvoker<Object>(this.serviceFactory, log.getSessionId(), serviceClass, method.getName(), requestClass, responseClass, asyncHandler);
                AsyncTask asyncTask = javaAsyncInvoker.execute(request);
                if (method.getReturnType() == AsyncTask.class) {
                    return asyncTask;
                } else if (method.getReturnType() == Future.class) {
                    return asyncTask.getFuture();
                } else {
                    return null;
                }
            }
        } else {
            throw new RuntimeException("illegal interface method '" + method + "'!");
        }

    }
}
