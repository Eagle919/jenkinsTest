package com.github.pig.common.util.threads;

import java.util.concurrent.*;


/**
 * @className ThreadPoolUtils
 * @Author chenkang
 * @Date 2019/3/7 10:02
 * @Version 1.0
 */
public class ThreadPoolUtils {


    public ThreadPoolUtils() {
    }

    /**
     * 参数工厂
     */
    private static class ParamFactory{

        private int corepoolSize;

        private int maximumPoolsize;

        private long keepAliveTime;

        private BlockingQueue queue;

        private RejectedExecutionHandler handler;


        public ParamFactory() {
        }

        public ParamFactory(int corepoolSize, int maximumPoolsize, long keepAliveTime, BlockingQueue queue, RejectedExecutionHandler handler) {
            this.corepoolSize = corepoolSize;
            this.maximumPoolsize = maximumPoolsize;
            this.keepAliveTime = keepAliveTime;
            this.queue = queue;
            this.handler = handler;
        }

        public ParamFactory setPoolSize(int poolSize) {
            this.corepoolSize = poolSize;
            return this;
        }

        public ParamFactory setMaximumPoolsize(int maximumPoolsize) {
            this.maximumPoolsize = maximumPoolsize;
            return this;
        }

        public ParamFactory setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }
        public ParamFactory setQueue(BlockingQueue queue) {
            this.queue = queue;
            return this;
        }

        public ParamFactory setCorepoolSize(int corepoolSize) {
            this.corepoolSize = corepoolSize;
            return this;
        }

        public ParamFactory setHandler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }
    }
    /**
     * 获取参数工厂
     * @param coreSize
     * @param maximumPoolsize
     * @param keepAliveTime
     * @param queue
     * @param handler
     * @return
     */
    public static ParamFactory getParamFactory(int coreSize , int maximumPoolsize, long keepAliveTime ,BlockingQueue queue,RejectedExecutionHandler handler){
        return new ParamFactory(coreSize,maximumPoolsize,keepAliveTime,queue,handler);
    }


    /**
     * fixed线程池
     * @param poolsize
     * @return
     */
    public static ExecutorService fixedThreadPool(int poolsize){

        return new ThreadPoolExecutor(poolsize, poolsize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }


    /**
     *
     * @return
     */
    public static ExecutorService singleThreadPool(){

        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }


    public static ExecutorService cacheThreadPool(){
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    /**
     * 自定义线程池
     * @param paramFactory
     * @return
     */
    public static ExecutorService customThreadPool(ParamFactory paramFactory){
        return new ThreadPoolExecutor(paramFactory.corepoolSize, paramFactory.maximumPoolsize,
                paramFactory.keepAliveTime, TimeUnit.SECONDS,
                paramFactory.queue);
    }

    public static ExecutorService customThreadPoolAll(ParamFactory paramFactory, ThreadFactoryUtils.InnerThreadFactory threadFactory){

        ThreadFactory t =  threadFactory;

        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        if(t == null){
            t =  Executors.defaultThreadFactory();
        }

        if(paramFactory.handler != null){
            handler = paramFactory.handler;
        }

        return new ThreadPoolExecutor(paramFactory.corepoolSize, paramFactory.maximumPoolsize,
                paramFactory.keepAliveTime, TimeUnit.SECONDS,
                paramFactory.queue,t,handler);
    }

}
