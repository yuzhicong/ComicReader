package com.yzc.comicreader;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.yzc.comicreader.util.Util;

/**
 * Created by YuZhicong on 2017/5/19.
 */

public class App extends Application {

    public String startTheme;
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
