package com.jxmfkj.mvvmlibrary.base;

import android.os.Bundle;

public interface IBaseView {

    int getLayoutId();

    void onViewCreated(Bundle savedInstanceState);

    void initData();

    Bundle arguments();
}

