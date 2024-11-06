// 用于在后台线程执行任务，与UI的主线程分开。例如数据库操作
package com.example.personalexpensetracker.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    // 使用单线程的 Executor 来执行后台任务
    private static final Executor diskIO = Executors.newSingleThreadExecutor();

    // 提供一个公共的静态方法来获取 Executor 实例
    public static Executor getDiskIO() {
        return diskIO;
    }
}
