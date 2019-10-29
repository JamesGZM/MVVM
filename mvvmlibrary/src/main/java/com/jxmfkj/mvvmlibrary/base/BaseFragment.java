package com.jxmfkj.mvvmlibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jxmfkj.mvvmlibrary.bus.Messenger;
import com.trello.rxlifecycle3.components.support.RxFragment;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<VM extends BaseViewModel> extends RxFragment implements HandleBackInterface, IBaseView {

    private Unbinder mUnBinder;
    protected VM viewModel;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null, false);
        mUnBinder = ButterKnife.bind(this, mRootView);// 绑定到butterknife
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = initViewModel();
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
        //私有的ViewModel与View的契约事件回调逻辑
        BaseProxy.registerUIChangeLiveDataCallBack(getActivity(), this, viewModel);
        //页面事件监听的setApplication方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus
        viewModel.register();
        onViewCreated(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected VM initViewModel() {
        return BaseProxy.initViewModel(this, getClass(), arguments());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, requestCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除Messenger注册
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.unregister();
        }
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    //解决fragment的返回键监听
    @Override
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
    }

    public Bundle arguments() {
        return getArguments();
    }

    protected View getRootView() {
        return mRootView;
    }

    protected void initViewObservable() {
    }
}
