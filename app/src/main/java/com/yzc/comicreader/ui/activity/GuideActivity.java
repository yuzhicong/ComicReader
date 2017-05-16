package com.yzc.comicreader.ui.activity;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.yzc.comicreader.R;
import com.yzc.comicreader.adapter.guideViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.vpGuide)
    ViewPager vpGuide;

    @BindView(R.id.btnStart) Button mBtnStart;

    private ArgbEvaluator mArgbEvaluator;

    private static int colors[] = {R.color.colorPrimary,R.color.colorPrimaryDark,R.color.colorAccent,R.color.black_overlay,R.color.black_overlay};

    private int vpBgColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        mArgbEvaluator = new ArgbEvaluator();
        vpGuide.setAdapter(new guideViewPagerAdapter(this));
        vpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                vpBgColor = (int) mArgbEvaluator.evaluate(positionOffset,getResources().getColor(colors[position]),getResources().getColor(colors[position+1]));
                vpGuide.setBackgroundColor(vpBgColor);

                if(position == 2){
                    if(positionOffset > 0.5){
                    mBtnStart.setAlpha( (positionOffset - 0.5f) * 2);}
                    else{
                        mBtnStart.setAlpha(0.0f);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mBtnStart.setVisibility(position >=2 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
