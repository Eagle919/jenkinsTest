package com.github.pig.common.util.threads;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @className ThreadPoolHanderUtils
 * @Author chenkang
 * @Date 2019/3/12 11:39
 * @Version 1.0
 */
public class ThreadPoolHandlerFactory {


    private final static RejectedExecutionHandler ABORT_POLICY = new ThreadPoolExecutor.AbortPolicy();

    private final static RejectedExecutionHandler DISCARD_POLICY  = new ThreadPoolExecutor.DiscardPolicy();

    private final static RejectedExecutionHandler DISCARD_OLDEST_POLICY  = new ThreadPoolExecutor.DiscardOldestPolicy();

    private final static RejectedExecutionHandler CALLER_RUNS_POLICY  = new ThreadPoolExecutor.CallerRunsPolicy();

    public ThreadPoolHandlerFactory() {
    }

    public static RejectedExecutionHandler getAbortPolicy(){

        return ABORT_POLICY;
    }

    public static RejectedExecutionHandler getDiscardPolicy(){

        return DISCARD_POLICY;
    }

    public static RejectedExecutionHandler getDiscardOldestPolicy(){
        return DISCARD_OLDEST_POLICY;
    }

    public static RejectedExecutionHandler getCallerRunsPolicy(){
        return CALLER_RUNS_POLICY;
    }

    public static class customPolicy implements RejectedExecutionHandler{

        public customPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {

            System.out.println("自定义策略触发");

            /**
             * 线程池中没线程 主线程就等待 直到线程池中有空闲资源
             */
            if (!e.isShutdown()) {
                while (e.getQueue().remainingCapacity() == 0);
                e.execute(r);
            }
        }
    }

}
