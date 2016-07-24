package com.intentpumin.lsy.intentpumin;

import android.app.Application;
import com.intentpumin.lsy.intentpumin.http.HttpUtil;
import com.intentpumin.lsy.intentpumin.network.CrashHandler;
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpUtil.init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

}
