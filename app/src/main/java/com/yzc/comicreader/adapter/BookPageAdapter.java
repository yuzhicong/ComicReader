package com.yzc.comicreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.yzc.comicreader.R;
import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.ui.activity.ReaderActivity;
import com.yzc.comicreader.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YuZhicong on 2017/5/1.
 */

public class BookPageAdapter extends RecyclerView.Adapter<BookPageAdapter.PageView>{

    private Context mContext;
    private ComicBook comicBook;
    private List<String> mList;
    private int readmode;

    public BookPageAdapter(Context context,ComicBook book){
        mContext = context;
        comicBook = book;
        mList = getBookPageSrcList(book);

    }

    @Override
    public PageView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_page,parent,false);
        PageView pageView = new PageView(view);
        return pageView;
    }

    @Override
    public void onBindViewHolder(final PageView holder, int position) {
        Log.e("adapterOnBindView",mList.get(position));
        File pageFile = new File(mList.get(position));
        /*//这部分显示高度不同图片，
        String width = dataBean.getWidth();
        String height = dataBean.getHeight();
        ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
        lp.width = mWidth;
        lp.height = (mWidth * Integer.parseInt(height) / Integer.parseInt(width));
        holder.img.setLayoutParams(lp);*/
        //Glide.with(mContext).load(pageFile).placeholder(R.mipmap.ic_launcher).fitCenter().into(holder.ivPageContent);
        Glide.with(mContext).load(pageFile).asBitmap().placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                int imageWidth = resource.getWidth();
                int imageHeight = resource.getHeight();
                int height = (readmode == ReaderActivity.READ_MODE_SCROLL ? (Util.getScreenWidth(mContext) * imageHeight / imageWidth) :  Util.getScreenHeight(mContext) );
                ViewGroup.LayoutParams para = holder.ivPageContent.getLayoutParams();
                para.height = height;
                para.width = Util.getScreenWidth(mContext);
                holder.ivPageContent.setImageBitmap(resource);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class PageView extends RecyclerView.ViewHolder{

        private ImageView ivPageContent;
        public PageView(View itemView) {
            super(itemView);
            ivPageContent = (ImageView) itemView.findViewById(R.id.ivBookContent);
        }
    }

    private List<String> getBookPageSrcList(ComicBook book){
        File dir = new File(book.getSrcPath());
        File files[] = dir.listFiles();
        List<String> bookPageList = new ArrayList<>();
        for (File tempfile : files) {
            String tempfilePath = tempfile.getAbsolutePath().toLowerCase();
            if(tempfilePath.endsWith("jpg")||tempfilePath.endsWith("png")||tempfilePath.endsWith("jpeg")){
                bookPageList.add(tempfile.getAbsolutePath());
            }
        }
        return  bookPageList;
    }

    //阅读模式设置
    public void setReadmode(int readmode) {
        this.readmode = readmode;
    }
}
