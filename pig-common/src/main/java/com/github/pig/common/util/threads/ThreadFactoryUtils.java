package com.github.pig.common.util.threads;

import com.github.pig.common.util.Query;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @className ThreadFactoryUtils 线程池自定义工厂
 * @Author chenkang
 * @Date 2019/3/11 10:42
 * @Version 1.0
 */
public class ThreadFactoryUtils {


    public ThreadFactoryUtils() {

    }



    /**
     * 内部类
     */
    public static class InnerThreadFactory implements ThreadFactory{

        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;


        public InnerThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = "cust - pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static ThreadFactory defaultThreadFactory(){
        return Executors.defaultThreadFactory();
    }

    /**
     * 获取自定义内置线程工厂
     * @param threadGroup
     * @return
     */
    public static InnerThreadFactory getThreadFactory(String threadGroup){
        return new InnerThreadFactory("");
    }




}


