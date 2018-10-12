package com.tina.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yxc
 * @date 2018/10/11
 */
public class EventBus {
    private static EventBus instance;

    private Map<Object, List<SubscribleMethod>> cacheMap;
    private ExecutorService executorService;

    private Handler handler;

    private EventBus() {
        cacheMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        handler = new Handler(Looper.getMainLooper());
    }

    public static EventBus getDefault() {
        if (instance == null){
            instance = new EventBus();
        }
        return instance;
    }

    public void register(Object activity) {
        Class<?> clazz = activity.getClass();
        List<SubscribleMethod> list = cacheMap.get(activity);

        if (list == null) {
            list = getSubscribleMethods(activity);
            cacheMap.put(activity, list);
        }
    }

    private List<SubscribleMethod> getSubscribleMethods(Object activity) {

        List<SubscribleMethod> list = new ArrayList<>();

        Class clazz = activity.getClass();


        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }

            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);

                if (subscribe == null) {
                    continue;
                }

                Class[] paratems = method.getParameterTypes();

                if (paratems.length != 1) {
                    throw new RuntimeException("eventBus 只能接受一个参数");
                }

                ThreadMode threadMode = subscribe.threadMode();
                SubscribleMethod subscribleMethod = new SubscribleMethod(method, threadMode, paratems[0]);

                list.add(subscribleMethod);
            }

            clazz = clazz.getSuperclass();
        }
        return list;
    }


    //通知
    public void post(final Object friend) {
        Set<Object> set = cacheMap.keySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            final Object activity = iterator.next();
            List<SubscribleMethod> list = cacheMap.get(activity);
            for (final SubscribleMethod subscribleMethod : list) {
                if (subscribleMethod.getClassType().isAssignableFrom(friend.getClass())) {
                    switch (subscribleMethod.getThreadMode()) {
                        case Async:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, activity, friend);
                                    }
                                });
                            } else {
                                invoke(subscribleMethod, activity, friend);
                            }
                            break;
                        case MainThread:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscribleMethod, activity, friend);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, activity, friend);
                                    }
                                });
                            }
                            break;
                        case PostThread:
                            break;
                    }
                }
            }
        }

    }

    private void invoke(SubscribleMethod subscribleMethod, Object activity, Object friend) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(activity, friend);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    public void unRegister() {

    }


}
