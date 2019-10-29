package com.jxmfkj.mvvmlibrary;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastQQStyle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Config {

    private static boolean isDebug = false, isSystemToast = true;

    public static void init(Builder builder) {
        isDebug = builder.isDebug;
        Utils.init(builder.app);
        initLog(builder.tag);
        isSystemToast = builder.isSystemToast;
        if (!builder.isSystemToast) {
            ToastUtils.init(builder.app);
            ToastUtils.initStyle(new ToastQQStyle(builder.app));
        }
    }

    private static void initLog(String tag) {
        LogUtils.Config config = LogUtils.getConfig()
                .setLogSwitch(isDebug())// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(isDebug())// 设置是否输出到控制台开关，默认开
                .setGlobalTag(tag)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
                .setFileExtension(".log")// 设置日志文件后缀
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFormatter(new LogUtils.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList arrayList) {
                        return "LogUtils Formatter ArrayList { " + arrayList.toString() + " }";
                    }
                })
                .setFileWriter(null);
        LogUtils.i(config.toString());
    }

    /**
     * 解决部分机型GC超时
     **/
    public static void fixGC() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);
            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            method.invoke(field.get(null));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isSystemToast() {
        return isSystemToast;
    }

    //构造者模式构造配置
    public static class Builder {
        //开启debug模式
        private boolean isDebug;
        //log 全局tag
        private String tag;
        //是否是系统Toast 默认是
        private boolean isSystemToast = true;
        private Application app;

        public Builder(Application app) {
            this.app = app;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setSystemToast(boolean systemToast) {
            isSystemToast = systemToast;
            return this;
        }

    }
}
