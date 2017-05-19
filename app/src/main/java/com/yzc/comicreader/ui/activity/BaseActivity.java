package com.yzc.comicreader.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.yzc.comicreader.R;
import com.yzc.comicreader.util.Util;

import java.util.Locale;

/**
 * Created by YuZhicong on 2017/5/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private String language;
    protected String theme;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        initTheme();
    }

    public void changeAppLanguage() {

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        // 本地语言设置
        String sta = Util.getStringPreference(this,"app_language");
        language = sta;
        if(!sta.equals("")){
            if(sta.equals("default")){
                conf.locale = Locale.getDefault();
            }else{
                conf.locale = new Locale(sta);}
        }

        res.updateConfiguration(conf, dm);
    }

    public void initTheme(){
        theme = Util.getStringPreference(this,"app_theme");
        switch (theme){
            case "0":{
                setTheme(R.style.AppTheme_NoActionBar);
                break;
            }
            case "1":{
                setTheme(R.style.AppThemeSasuke);
                break;
            }
            case "2":{
                setTheme(R.style.AppThemeSakura);
                break;
            }
            case "3":{
                setTheme(R.style.AppThemeRockLee);
                break;
            }
            default:{
                setTheme(R.style.AppTheme_NoActionBar);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!language.equals(Util.getStringPreference(this,"app_language"))||!theme.equals(Util.getStringPreference(this,"app_theme"))){
            recreate();
        }
    }
}
