package com.phz.dropdownmenudemo.util;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

/**
 * @author haizhuo
 * @description activity堆栈式管理
 */
public class AppManager {

    private final String TAG="AppManager";
    private Stack<Activity> activityStack;
    private static AppManager mInstance;

    private AppManager() {
        activityStack= new Stack<>();
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppManager();
        }
        return mInstance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 从堆栈中删除Activity
     */
    public void removeActivity(Activity activity){
        if (activity == null || activityStack.isEmpty()) {
            return;
        }
        activityStack.remove(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty()) {
            activity = activityStack.lastElement();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity == null || activityStack.isEmpty()) {
            return;
        }
        if (!activity.isFinishing()) {
            activity.finish();
        }
        activityStack.remove(activity);
    }



    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            while (true) {
                Activity activity = currentActivity();
                if (activity == null) {
                    break;
                }
                finishActivity(activity);
            }
        } catch (Exception e) {
            Log.e(TAG,"关闭所有Activity错误"+e.getMessage());
        }finally {
            activityStack.clear();
        }
    }

    /**
     * 获取指定的Activity
     *
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null){
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
            /*android.os.Process.killProcess(android.os.Process.myPid());*/
            //终止当前JVM虚拟机，效果和上面的杀死进程差不多
            /*System.exit(0);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}