package com.jxmfkj.mvvmlibrary.base;

import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.jxmfkj.mvvmlibrary.Config;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

public class ToastProxy {

    public static class ToastMessage {
        private String text;
        private @StringRes
        int textId;
        private int length;

        public ToastMessage(String text) {
            this(text, Toast.LENGTH_SHORT);
        }

        public ToastMessage(@StringRes int textId) {
            this(textId, Toast.LENGTH_SHORT);
        }

        public ToastMessage(String text, int length) {
            this.text = text;
            this.length = length;
        }

        public ToastMessage(@StringRes int textId, int length) {
            this.textId = textId;
            this.length = length;
        }
    }

    public static void show(FragmentActivity activity, ToastMessage message) {
        if (message != null) {
            if (!TextUtils.isEmpty(message.text)) {
                if (!Config.isSystemToast()) {
                    com.hjq.toast.ToastUtils.show(message.text);
                    return;
                }
                if (message.length == Toast.LENGTH_SHORT) {
                    ToastUtils.showShort(message.text);
                } else {
                    ToastUtils.showLong(message.text);
                }
            } else {
                //需要适配多语言传递activity对象
                if (!Config.isSystemToast()) {
                    if (activity != null)
                        com.hjq.toast.ToastUtils.show(activity.getString(message.textId));
                    else
                        com.hjq.toast.ToastUtils.show(message.textId);
                    return;
                }
                if (message.length == Toast.LENGTH_SHORT) {
                    if (activity != null)
                        ToastUtils.showShort(activity.getString(message.textId));
                    else
                        ToastUtils.showShort(message.textId);
                } else {
                    if (activity != null)
                        ToastUtils.showLong(activity.getString(message.textId));
                    else
                        ToastUtils.showLong(message.textId);
                }
            }
        }
    }


    public static void showShort(String text) {
        show(null, new ToastMessage(text));
    }

    /**
     * @param textId Deprecated is 国际化传递Activity对象用于切换Stringid
     */
    @Deprecated
    public static void showShort(@StringRes int textId) {
        show(null, new ToastMessage(textId));
    }

    public static void showLong(String text) {
        show(null, new ToastMessage(text, Toast.LENGTH_LONG));
    }

    /**
     * @param textId Deprecated is 国际化传递Activity对象用于切换Stringid
     */
    @Deprecated
    public static void showLong(@StringRes int textId) {
        show(null, new ToastMessage(textId, Toast.LENGTH_LONG));
    }

    public static void showShort(FragmentActivity activity, String text) {
        show(activity, new ToastMessage(text));
    }

    public static void showShort(FragmentActivity activity, @StringRes int textId) {
        show(activity, new ToastMessage(textId));
    }

    public static void showLong(FragmentActivity activity, String text) {
        show(activity, new ToastMessage(text, Toast.LENGTH_LONG));
    }

    public static void showLong(FragmentActivity activity, @StringRes int textId) {
        show(activity, new ToastMessage(textId, Toast.LENGTH_LONG));
    }
}
