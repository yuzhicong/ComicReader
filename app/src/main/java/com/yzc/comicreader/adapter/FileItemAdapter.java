package com.yzc.comicreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzc.comicreader.R;
import com.yzc.comicreader.ui.MaterialFileItemView;

import java.util.ArrayList;

/**
 * Created by YuZhicong on 2017/4/6.
 */

public class FileItemAdapter extends BaseAdapter{

    private ArrayList<FileItem> fileList;
    private Context mContext;

    public FileItemAdapter(Context context,ArrayList<FileItem> list){
        mContext = context;
        fileList = list;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileView viewholder = null;
        if(view == null){
            view = LinearLayout.inflate(mContext, R.layout.item_file,null);
            viewholder = new FileView();
            viewholder.tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            viewholder.tvFileType = (TextView) view.findViewById(R.id.tvFileType);
            view.setTag(viewholder);
        }else{
            viewholder = (FileView) view.getTag();
        }
        FileItem item = fileList.get(i);
        viewholder.tvFileName.setText(item.title);
        viewholder.tvFileType.setText(item.subtitle);

        return view;
    }

    public static class FileView{
        TextView tvFileName,tvFileType;

    }
}
