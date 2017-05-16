package com.yzc.comicreader.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.yzc.comicreader.R;
import com.yzc.comicreader.database.ComicDbHelper;
import com.yzc.comicreader.database.PasswordDbHelper;
import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.model.Password;
import com.yzc.comicreader.ui.activity.BookCollectionActivity;
import com.yzc.comicreader.util.ZipCommandUtil;
import com.yzc.comicreader.util.ZipProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YuZhicong on 2017/5/7.
 */

public class DecompressPasswordInputFragment extends DialogFragment {

    private Dialog dialog;
    private EditText etDCpassword;
    private CheckBox cbUsePasswordLibrary;
    private String dirPath;
    private BookCollectionActivity mActivity;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppThemeDialog);
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_input_compresspassword, null, false);

        etDCpassword = (EditText) contentView.findViewById(R.id.etDCpassword);
        cbUsePasswordLibrary = (CheckBox) contentView.findViewById(R.id.cbUsePasswordLibrary);

        dirPath = (String) getArguments().getCharSequence("dirPath");

        mActivity = (BookCollectionActivity) getActivity();

        Log.e("DecompressPassword",mActivity.toString());

        dialog = builder.setView(contentView)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.this_comic_file_need_password)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            if(!cbUsePasswordLibrary.isChecked()){
                                if(TextUtils.isEmpty(etDCpassword.getText().toString())){
                                    etDCpassword.setError(getString(R.string.password_empty_message));
                                }else{
                                    //隐藏解压密码框
                                    FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                                    mFragmentTransaction.remove(DecompressPasswordInputFragment.this);
                                    mFragmentTransaction.addToBackStack(null);
                                    mFragmentTransaction.commit();
                                    List<Password> lists = new ArrayList<Password>();
                                    Password password = new Password();
                                    password.setPassword(etDCpassword.getText().toString());
                                    lists.add(password);
                                    DecompressComicfile(lists);
                                }
                            }else{
                                //使用密码库自动匹配
                                DecompressComicfile(PasswordDbHelper.getPasswordDbHelper(getActivity()).queryPasswords());
                            }
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dismiss();
                }
                return false;
            }
        });


        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    private boolean isSuccess = false;

    void DecompressComicfile(final List<Password> lists){
        //使用用户输入的密码
        final ComicDbHelper helper = ComicDbHelper.getComicDBHelper(getActivity());

        final String outputPath = getActivity().getExternalCacheDir().getAbsolutePath() + "/.Comic/" + ComicBook.parseBookname(dirPath);

        for(Password password : lists){
            String decompressCommand = ZipCommandUtil.getDecompressCommand(dirPath,
                    outputPath,true,password.getPassword(),ZipCommandUtil.OverWriteMode.OVERWRITE_ALL_EXISTING_FILE,null);

            Log.e("Decompress:",decompressCommand);
            ZipProcess zipProcess = new ZipProcess(getActivity(),decompressCommand,dirPath);

            zipProcess.setZipProcessListener(new ZipProcess.ZipProcessListenner() {
                @Override
                public void onZipSuccess(final ProgressDialog dialog) {//需要运行在非ui线程否则会卡顿
                    dialog.setTitle(mActivity.getResources().getString(R.string.analyse_title));
                    dialog.setMessage(mActivity.getResources().getString(R.string.analyse_message));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean canInsert = helper.insertComicBook(new ComicBook(getActivity(),dirPath,outputPath,""));
                            dialog.dismiss();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(canInsert){
                                        mActivity.adapter.refreshBookList(null);
                                        mActivity.showMessage(mActivity.getResources().getString(R.string.add_book_success));
                                    }else{
                                        mActivity.showMessage(mActivity.getResources().getString(R.string.add_book_failure));
                                    }
                                    isSuccess = true;
                                }
                            });
                        }
                    }).start();

                }

                @Override
                public void onZipFault(ProgressDialog dialog) {
                    dialog.dismiss();
                    if(lists.size() == 1) {
                        getFragmentManager().popBackStack();
                        mActivity.showMessage(getString(R.string.add_book_failure));
                    }
                }
            });

            zipProcess.start();//开始解压
        }
    }
}
