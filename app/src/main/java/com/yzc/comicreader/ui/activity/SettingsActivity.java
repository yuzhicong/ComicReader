package com.yzc.comicreader.ui.activity;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.yzc.comicreader.R;
import com.yzc.comicreader.config.GlideConfiguration;
import com.yzc.comicreader.util.Util;

import java.util.List;
import java.util.Locale;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private ListPreference lpAppLanguage;
    private ListPreference lpTheme;
    private String language;
    private String theme;

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                if(preference.getKey().equals("app_language")){
                    String chooseLanguage = stringValue;
                    boolean needRecreat = !language.equals(chooseLanguage);
                    language = ((ListPreference) preference).getValue();
                    if(needRecreat)
                        recreate();
                }
                if(preference.getKey().equals("app_theme")){
                    if(!theme.equals(stringValue)){
                        recreate();
                    }
                }
            } else if (preference instanceof SwitchPreference) {
                if(preference.getKey().equals("high_quality_picture_mode")){
                    //
                    GlideBuilder glideBuilder = new GlideBuilder(SettingsActivity.this);
                    glideBuilder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
                    GlideConfiguration config = new GlideConfiguration();
                    config.applyOptions(SettingsActivity.this,glideBuilder);
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        initTheme();
        //setupActionBar();
        //getSupportActionBar().setTitle(R.string.settings);
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_settings_toolbar, root, false);
        toolbar.setTitle(R.string.settings);
        root.addView(toolbar,0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref_general);

        lpAppLanguage = (ListPreference) findPreference("app_language");
        lpAppLanguage.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        int index = lpAppLanguage.findIndexOfValue(Util.getStringPreference(this,"app_language"));
        lpAppLanguage.setSummary(index > 0 ? lpAppLanguage.getEntries()[index] : null);

        lpTheme = (ListPreference) findPreference("app_theme");
        lpTheme.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        lpTheme.setSummary(lpTheme.getEntry());

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                this.finish();
                //NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
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
        int statusBarColor;
        switch (theme){
            case "0":{
                setTheme(R.style.AppTheme_NoActionBar);
                statusBarColor = getResources().getColor(R.color.colorPrimaryDark);
                break;
            }
            case "1":{
                setTheme(R.style.AppThemeSasuke);
                statusBarColor = getResources().getColor(R.color.sasuke_colorPrimaryDark);
                break;
            }
            case "2":{
                setTheme(R.style.AppThemeSakura);
                statusBarColor = getResources().getColor(R.color.sakura_colorPrimaryDark);
                break;
            }
            case "3":{
                setTheme(R.style.AppThemeRockLee);
                statusBarColor = getResources().getColor(R.color.rocklee_colorPrimaryDark);
                break;
            }
            default:{
                setTheme(R.style.AppTheme_NoActionBar);
                statusBarColor = getResources().getColor(R.color.colorPrimaryDark);
                break;
            }
        }
        if(Build.VERSION.SDK_INT > 20) {
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getWindow().setStatusBarColor(statusBarColor);
        }
    }
}
