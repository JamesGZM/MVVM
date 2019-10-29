package com.jxmfkj.mvvmlibrary.base;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.jxmfkj.mvvmlibrary.bus.event.SingleLiveEvent;
import com.trello.rxlifecycle3.LifecycleProvider;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel implements IBaseViewModel {

    private UIChangeLiveData uc;
    private Bundle arguments;
    //弱引用持有
    private WeakReference<LifecycleProvider> lifecycle;
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;

    public BaseViewModel(@NonNull Application application, @NonNull Bundle arguments) {
        super(application);
        this.arguments = arguments;
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = new WeakReference<>(lifecycle);
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle.get();
    }

    public Bundle getArguments() {
        return arguments;
    }

    public UIChangeLiveData getUC() {
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivityForResult(Class<?> clz, int requestCode, Bundle bundle) {
        Parameter parameter = new Parameter(clz, bundle);
        parameter.setResult(true);
        parameter.setRequestCode(requestCode);

        uc.startActivityEvent.postValue(parameter);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        uc.startActivityEvent.postValue(new Parameter(clz, bundle));
    }

    /**
     * 关闭界面
     */
    public void finish() {
        uc.finishEvent.call();
    }

    /**
     * 返回上一层
     */
    public void onBackPressed() {
        uc.onBackPressedEvent.call();
    }

    /**
     * 显示Toast
     *
     * @param text 内容
     */
    public void showMessage(String text) {
        uc.showMessageEvent.postValue(new ToastProxy.ToastMessage(text));
    }

    /**
     * 显示Toast
     *
     * @param textId 内容Id
     */
    public void showMessage(int textId) {
        uc.showMessageEvent.postValue(new ToastProxy.ToastMessage(textId));
    }

    public void onActivityResult(int requestCode, int requestCode1, Intent data) {
    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void register() {
    }

    @Override
    public void unregister() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //ViewModel销毁时会执行，同时取消所有异步任务
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


    final static class UIChangeLiveData extends SingleLiveEvent {
        private SingleLiveEvent<Parameter> startActivityEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;
        private SingleLiveEvent<ToastProxy.ToastMessage> showMessageEvent;

        public SingleLiveEvent<Parameter> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<ToastProxy.ToastMessage> getShowMessageEvent() {
            return showMessageEvent = createLiveData(showMessageEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        private SingleLiveEvent createLiveData(SingleLiveEvent liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent();
            }
            return liveData;
        }

        @Override
        public void observe(LifecycleOwner owner, Observer observer) {
            super.observe(owner, observer);
        }
    }

    public static final class Parameter {
        private final Class<?> clz;
        private boolean isResult;
        private final Bundle bundle;
        private int requestCode;

        public Parameter(Class<?> clz, Bundle bundle) {
            this.clz = clz;
            this.bundle = bundle;
        }

        public Class<?> getClz() {
            return clz;
        }

        public boolean isResult() {
            return isResult;
        }

        public void setResult(boolean result) {
            isResult = result;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }
    }
}
