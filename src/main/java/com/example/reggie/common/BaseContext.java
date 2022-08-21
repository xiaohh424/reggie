package com.example.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * 类似于session的使用
 */

//客户段发送的每一个请求，服务器都会为之分配线程处理它，
//先经过过滤器，再到update操作，再到自动填充update-fill操作，这些处于一个线程中运行
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}