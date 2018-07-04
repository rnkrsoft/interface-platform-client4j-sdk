package com.rnkrsoft.platform.client.proxy;

import com.rnkrsoft.platform.client.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.invoker.AsyncInvoker;
import com.rnkrsoft.platform.client.invoker.SyncInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceProxy<T> implements InvocationHandler {
    public static ThreadPoolExecutor THREAD_POOL_EXECUTOR = null;
    /**
     * 服务配置对象
     */
    ServiceConfigure serviceConfigure;
    /**
     * 服务接口类
     */
    Class<T> serviceClass;

    public ServiceProxy(ServiceConfigure serviceConfigure, Class<T> serviceClass) {
        this.serviceConfigure = serviceConfigure;
        this.serviceClass = serviceClass;
        if (serviceConfigure.getAsyncExecuteThreadPoolSize() > 0){
            THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(serviceConfigure.getAsyncExecuteThreadPoolSize(),
                    serviceConfigure.getAsyncExecuteThreadPoolSize(),
                    200,
                    TimeUnit.MILLISECONDS,
                    new SynchronousQueue<Runnable>(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args.length == 1) {
            Object request = args[0];
            Class requestClass = method.getParameterTypes()[0];
            Class responseClass = method.getReturnType();
            return SyncInvoker.sync(serviceConfigure, serviceClass, method.getName(), requestClass, responseClass, request);
        } else if (args.length == 2) {
            if (THREAD_POOL_EXECUTOR == null){
                throw new IllegalArgumentException("不支持异步执行方式");
            }
            if (method.getParameterTypes()[1].isAssignableFrom(AsyncHandler.class)) {
                Object request = args[0];
                AsyncHandler asyncHandler = (AsyncHandler) args[1];
                Class requestClass = method.getParameterTypes()[0];
                ParameterizedType asyncHandlerClass = (ParameterizedType) method.getGenericParameterTypes()[1];
                Class responseClass = (Class) asyncHandlerClass.getActualTypeArguments()[0];
                THREAD_POOL_EXECUTOR.submit(new AsyncInvoker(serviceConfigure, serviceClass, method.getName(), requestClass, responseClass, request, asyncHandler));
                return null;
            } else {
                throw new IllegalArgumentException("无效的接口定义" + Arrays.toString(args));
            }
        } else {
            throw new IllegalArgumentException("无效的接口定义" + Arrays.toString(args));
        }
    }
}
