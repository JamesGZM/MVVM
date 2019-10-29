package com.jxmfkj.mvvmlibrary.base;

import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class BaseProxy {

    public static <VM extends BaseViewModel> VM initViewModel(FragmentActivity activity, Class<?> clz, Bundle arguments) {
        return (VM) ViewModelProviders.of(activity, new ViewModelFactory(activity.getApplication(), arguments)).get(getModelClz(clz));
    }

    public static <VM extends BaseViewModel> VM initViewModel(Fragment fragment, Class<?> clz, Bundle arguments) {
        return (VM) ViewModelProviders.of(fragment, new ViewModelFactory(fragment.getActivity().getApplication(), arguments)).get(getModelClz(clz));
    }

    private static Class getModelClz(Class<?> clz) {
        Class modelClass;
        Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = BaseViewModel.class;
        }
        return modelClass;
    }

    static void registerUIChangeLiveDataCallBack(FragmentActivity activity, LifecycleOwner owner, BaseViewModel viewModel) {
        //跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(owner, parameter -> {
            if (parameter.isResult()) {
                startActivityForResult(activity, parameter.getClz(), parameter.getRequestCode(), parameter.getBundle());
            } else {
                startActivity(activity, parameter.getClz(), parameter.getBundle());
            }

        });
        //关闭界面
        viewModel.getUC().getFinishEvent().observe(owner, v -> activity.finish());
        //关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(owner, v -> activity.onBackPressed());
        //显示toast
        viewModel.getUC().getShowMessageEvent().observe(owner, message -> ToastProxy.show(activity, message));
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public static void startActivity(FragmentActivity activity, Class<?> clz) {
        startActivity(activity, clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public static void startActivity(FragmentActivity activity, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(activity, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public static void startActivityForResult(FragmentActivity activity, Class<?> clz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(activity, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

}
