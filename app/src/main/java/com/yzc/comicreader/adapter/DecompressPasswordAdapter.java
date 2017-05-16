package com.yzc.comicreader.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yzc.comicreader.R;
import com.yzc.comicreader.database.ComicDbHelper;
import com.yzc.comicreader.database.PasswordDbHelper;
import com.yzc.comicreader.model.Password;
import com.yzc.comicreader.ui.activity.BookCollectionActivity;
import com.yzc.comicreader.ui.fragment.AddPasswordFragment;

import java.util.List;

/**
 * Created by YuZhicong on 2017/5/4.
 */

public class DecompressPasswordAdapter extends RecyclerView.Adapter<DecompressPasswordAdapter.PasswordItem> {

    private List<Password> list;
    private Context mContext;

    public DecompressPasswordAdapter(Context context){

        mContext = context;
        list = PasswordDbHelper.getPasswordDbHelper(context).queryPasswords();

    }

    @Override
    public PasswordItem onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_password,null);
        return new PasswordItem(itemView);
    }

    @Override
    public void onBindViewHolder(PasswordItem passwordItem, int i) {
        final Password psw = list.get(i);
        passwordItem.tvSrcName.setText(psw.getSrcName());
        passwordItem.tvPassword.setText(psw.getPassword());
        passwordItem.tvIcon.setText(psw.getSrcName().substring(0,1));
        passwordItem.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder diaBuilder = new AlertDialog.Builder(mContext,R.style.Theme_AppCompat_Light_Dialog);
                diaBuilder.setTitle(R.string.remove_password_title);
                diaBuilder.setMessage(R.string.remove_password_message);
                diaBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PasswordDbHelper.getPasswordDbHelper(mContext).delectePassword(psw);
                        DecompressPasswordAdapter.this.refreshPasswordsList();
                        //最好显示提示删除结果
                    }
                });
                diaBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                diaBuilder.create().show();
            }
        });
        passwordItem.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPasswordFragment editPasswordFragment = new AddPasswordFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("passwordItem",psw);
                editPasswordFragment.setArguments(bundle);
                editPasswordFragment.show(((BookCollectionActivity)mContext).getSupportFragmentManager(),"Edit Decompress Password");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PasswordItem extends RecyclerView.ViewHolder{
        public TextView tvSrcName,tvPassword,tvIcon;
        public ImageView ivEdit,ivDelete;

        public PasswordItem(View itemView) {
            super(itemView);
            tvSrcName = (TextView) itemView.findViewById(R.id.tvSrcName);
            tvPassword = (TextView) itemView.findViewById(R.id.tvPassword);
            tvIcon = (TextView) itemView.findViewById(R.id.tvIcon);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
        }
    }

    public void refreshPasswordsList(){
        list = PasswordDbHelper.getPasswordDbHelper(mContext).queryPasswords();
        this.notifyDataSetChanged();
    }
}
