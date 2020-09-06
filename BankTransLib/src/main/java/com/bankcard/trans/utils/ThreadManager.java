package com.bankcard.trans.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理工具类，复用线程
 */
public class ThreadManager {

    private static ThreadManager instance = new ThreadManager();
    private ThreadPoolProxy longPool;

    public static ThreadManager newInstance() {
        return instance;
    }

    private ThreadManager() {
    }

    /**
     * 创建一个的线程池
     *
     * @return
     */
    public synchronized ThreadPoolProxy createThreadPool() {
        if (longPool == null) {
            longPool = new ThreadPoolProxy(3, 3, 0L);
        }
        return longPool;
    }

    public static class ThreadPoolProxy {
        private ThreadPoolExecutor pool;
        private int corePoolSize;
        private int maximumPoolSize;
        private long time;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.time = time;
        }

        /**
         * 执行任务
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (pool == null) {
                // 创建线程池
                /*
                 * 1. 线程池中核心线程的数量
                 * 2. 线程池中最大线程数量
                 * 3. 非核心线程的时长
                 * 4. 时间的单位，秒
                 * 5. 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到LinkedBlockingQueue对象中 排队
                 */
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                        time, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(10));
            }
            // 调用线程池 执行异步任务
            pool.execute(runnable);
        }

        /**
         * 取消任务
         *
         * @param runnable
         */
        public void cancel(Runnable runnable) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(runnable); // 取消异步任务
            }
        }
    }
}
