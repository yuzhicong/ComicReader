package com.yzc.comicreader;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.yzc.comicreader.util.Util;

/**
 * Created by YuZhicong on 2017/5/19.
 */

public class App extends Application {

    public String startTheme;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
