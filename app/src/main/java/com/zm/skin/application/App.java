package com.zm.skin.application;

import android.app.Application;

import com.zm.skin.skin.SkinHandler;

/**
 * @author zhangming
 * @Date 2019/3/21 10:00
 * @Description: 自定义application类
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinHandler.getInstance().init(this);
    }
}
