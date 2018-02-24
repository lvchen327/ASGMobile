package com.handict.superapp_mobile;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by LC on 2018/2/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 将MultiDex注入到项目中
        MultiDex.install(this);
        UMShareAPI.get(this);
//        PlatformConfig.setWeixin("wx52d31ccb73e0222f", "91382503c3cdf25e15efd549b9221634");
        Config.DEBUG = true;
    }
    {
//        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
//        PlatformConfig.setWeixin("wx52d31ccb73e0222f", "f9a7bb5dbd4ded41c5f4502dc2fe2d67");
        PlatformConfig.setWeixin("wx52d31ccb73e0222f", "91382503c3cdf25e15efd549b9221634");

    }
}