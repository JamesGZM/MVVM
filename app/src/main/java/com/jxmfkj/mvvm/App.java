package com.jxmfkj.mvvm;

import com.jxmfkj.mvvmlibrary.Config;
import com.jxmfkj.mvvmlibrary.base.BaseApplication;

public class App extends BaseApplication {

    @Override
    protected Config.Builder getBuilder() {
        return new Config.Builder(this).setDebug(true).setSystemToast(false);
    }
}
