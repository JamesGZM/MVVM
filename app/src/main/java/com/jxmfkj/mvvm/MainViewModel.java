package com.jxmfkj.mvvm;

import android.Manifest;
import android.app.Application;
import android.os.Bundle;

import com.jxmfkj.mvvmlibrary.base.BaseViewModel;
import com.jxmfkj.mvvmlibrary.permissions.RxPermissions;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class MainViewModel extends BaseViewModel {

    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public MainViewModel(@NonNull Application application, @NonNull Bundle arguments) {
        super(application, arguments);
    }

    public void requestPermissions(FragmentActivity activity) {
        RxPermissions permissions = new RxPermissions(activity);
        addSubscribe(permissions.request("获取权限", BASIC_PERMISSIONS).subscribe(aBoolean -> {
            if (!aBoolean) showMessage("权限被拒绝，可能会造成应用程序无法运行。");
        }));
    }
}
