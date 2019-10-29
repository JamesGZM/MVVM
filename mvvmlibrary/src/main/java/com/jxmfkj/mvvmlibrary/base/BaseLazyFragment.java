package com.jxmfkj.mvvmlibrary.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 用于ViewPager的懒加载方案
 *
 * @param <VM>
 */
public abstract class BaseLazyFragment<VM extends BaseViewModel> extends BaseFragment<VM> {

    private boolean isViewCreated = false;
    private boolean isViewVisible = false;
    private boolean isInitialized = false;

    private boolean isNoLazy = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isViewVisible = isVisibleToUser;
        prepareLoad();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //或者父级是Activity且不需要懒加载
        if (getParentFragment() != null && getParentFragment() instanceof NoLazyListener) {
            //父级是Fragment 且不需要懒加载
            isNoLazy = true;
        } else isNoLazy = getActivity() != null && getActivity() instanceof NoLazyListener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
    }


    private void prepareLoad() {
        if (isNoLazy) {
            //不懒加载
            onLazyLoad();
        } else {
            //懒加载
            if (isViewCreated && isViewVisible && !isInitialized) {
                onLazyLoad();
                isInitialized = true;
            }
        }
    }

    public abstract void onLazyLoad();

    @Override
    public void initData() {
        prepareLoad();
    }
}
