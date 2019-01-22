package com.rnkrsoft.platform.client.async;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rnkrsoft.com on 2019/1/20.
 * 异步执行
 */
public abstract class AsyncTask<Params, Progress, Result> {
    /**
     * 当前异步任务句柄
     */
    private FutureTask<Result> future;
    /**
     * 当前异步任务执行的线程任务
     */
    private final WorkerRunnable<Params, Result> worker;

    private volatile Status status = Status.PENDING;

    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final AtomicBoolean taskInvoked = new AtomicBoolean();


    public AsyncTask() {
        worker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                Result result = doInBackground(mParams);
                return postResult(result);
            }
        };

        future = new FutureTask<Result>(worker);
    }

    /**
     * 使用默认线程池执行异步任务
     *
     * @param params 参数
     * @return 异步任务对象
     */
    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    /**
     * 在线程池中执行当前异步任务
     *
     * @param exec   线程池
     * @param params 参数
     * @return 异步任务
     */
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (status != Status.PENDING) {
            switch (status) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed(a task can be executed only once)");
            }
        }
        status = Status.RUNNING;
        onPreExecute();
        worker.mParams = params;
        exec.execute(future);
        return this;
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = taskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
        }
    }


    /**
     * 线程执行前操作
     */
    protected void onPreExecute() {
    }

    /**
     * 线程执行后操作
     *
     * @param result 执行结果
     */
    protected void onPostExecute(Result result) {
    }

    /**
     * 返回结果后操作
     *
     * @param result
     * @return
     */
    private Result postResult(Result result) {
        finish(result);
        return result;
    }

    /**
     * 后台执行任务体
     *
     * @param params 参数
     * @return 结果
     */
    protected abstract Result doInBackground(Params... params);

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        status = Status.FINISHED;
    }


    public final boolean isCancelled() {
        return cancelled.get();
    }

    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        cancelled.set(true);
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * 阻塞获取执行结果
     *
     * @return 结果
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   线程池异常
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    /**
     * 阻塞获取执行结果，超时中断
     *
     * @param timeout 超时时间
     * @param unit    单位
     * @return 结果
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   线程池异常
     * @throws TimeoutException     获取结果超时
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public final Status getStatus() {
        return status;
    }

    public FutureTask<Result> getFuture() {
        return future;
    }

    /**
     * 工作线程体
     *
     * @param <Params> 参数
     * @param <Result> 结果
     */
    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    public enum Status {
        /**
         * 尚未执行
         */
        PENDING,
        /**
         * 执行中
         */
        RUNNING,
        /**
         * 执行完成
         */
        FINISHED,
    }

    //----------------------------------------------以下为静态成员-------------------------------------------------------------------
    /**
     * CPU核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /**
     * 最大线程数
     */
    private static int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 检查超时时间
     */
    private static final int KEEP_ALIVE_SECONDS = 30;


    public static void setAsyncExecuteThreadPoolSize(int maxNumPoolSize) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                maxNumPoolSize,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                poolWorkQueue,
                threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Java AsyncTask #" + count.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

    public static Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                poolWorkQueue,
                threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }
}
