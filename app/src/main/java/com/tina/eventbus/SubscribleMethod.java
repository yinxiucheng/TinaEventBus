package com.tina.eventbus;

import java.lang.reflect.Method;

/**
 * @author yxc
 * @date 2018/10/11
 */
public class SubscribleMethod {

    private Method method;
    //所处线程
    private ThreadMode threadMode;

    private Class<?> classType;

    public SubscribleMethod(Method method, ThreadMode threadMode, Class<?> classType) {
        this.method = method;
        this.threadMode = threadMode;
        this.classType = classType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }
}
