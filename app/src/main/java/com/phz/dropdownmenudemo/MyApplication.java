package com.phz.dropdownmenudemo;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication myApplication;

    /**
     * 获取实例
     *
     * @return myApplication
     */
    public static MyApplication getInstance() {
        return myApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public String getAppName() {
        try {
            return myApplication.getPackageManager().getPackageInfo(myApplication.getPackageName(), 0).applicationInfo.loadLabel(myApplication.getPackageManager()).toString();
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return "";
        }
    }

}
