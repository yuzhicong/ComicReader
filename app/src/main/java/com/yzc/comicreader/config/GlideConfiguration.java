package com.yzc.comicreader.config;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;
import com.yzc.comicreader.App;
import com.yzc.comicreader.util.Util;

/**
 * Created by YuZhicong on 2017/5/1.
 */

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        if(Util.getBooleanPreference(App.mContext,"high_quality_picture_mode")){
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        }else{
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);}
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
