package com.jxmfkj.mvvmlibrary.base;

import android.app.Application;
import android.content.Context;

import com.jxmfkj.mvvmlibrary.Config;

import androidx.annotation.NonNull;

public class BaseApplication extends Application {

    private static Application sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Config.fixGC();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(getBuilder(), this);
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(Config.Builder builder, @NonNull Application application) {
        sInstance = application;
        Config.init(builder == null ? new Config.Builder(application) : builder);
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }

    protected Config.Builder getBuilder() {
        return new Config.Builder(this);
    }
}
