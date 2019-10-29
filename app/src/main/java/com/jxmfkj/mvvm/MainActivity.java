package com.jxmfkj.mvvm;

import android.os.Bundle;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.jxmfkj.mvvmlibrary.base.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainViewModel> {

    @BindView(R.id.tv)
    TextView tv;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        tv.setTextColor(ColorUtils.getColor(R.color.colorAccent));

        viewModel.showMessage("测试一下");

        //在测试下获取权限

        viewModel.requestPermissions(this);

    }

}
