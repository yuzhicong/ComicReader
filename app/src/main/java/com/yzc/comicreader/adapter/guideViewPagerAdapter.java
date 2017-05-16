package com.yzc.comicreader.adapter;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yzc.comicreader.R;

/**
 * Created by YuZhicong on 2017/4/2.
 */

public class guideViewPagerAdapter extends PagerAdapter {
    private Context ctx;

    public guideViewPagerAdapter(Context ctx){
        this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View v = LayoutInflater.from(ctx).inflate(R.layout.item_guide,null);
        ImageView ivGuideItem = (ImageView) v.findViewById(R.id.ivGuideItem);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
