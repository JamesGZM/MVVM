package com.jxmfkj.mvvmlibrary.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jxmfkj.mvvmlibrary.bus.Messenger;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;


import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView {

    private Unbinder mUnBinder;
    protected VM viewModel;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(getLayoutId());
        mUnBinder = ButterKnife.bind(this);
        viewModel = initViewModel();
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
        //私有的ViewModel与View的契约事件回调逻辑
        BaseProxy.registerUIChangeLiveDataCallBack(this, this, viewModel);
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus
        viewModel.register();
        onViewCreated(savedInstanceState);
        initData();
    }

    public Context getContext() {
        return mContext;
    }

    public VM initViewModel() {
        return BaseProxy.initViewModel(this, getClass(), arguments());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, requestCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除Messenger注册
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.unregister();
        }
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    public Bundle arguments() {
        return getIntent().getExtras();
    }

    protected void initViewObservable() {

    }

}
