package com.yzc.comicreader.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.yzc.comicreader.R;
import com.yzc.comicreader.adapter.DecompressPasswordAdapter;
import com.yzc.comicreader.database.PasswordDbHelper;
import com.yzc.comicreader.model.Password;
import com.yzc.comicreader.ui.activity.BookCollectionActivity;

import java.lang.reflect.Field;

/**
 * Created by YuZhicong on 2017/5/5.
 */

public class AddPasswordFragment extends AppCompatDialogFragment {

    private FragmentTransaction mFragmentTransaction;
    private DecompressPasswordAdapter adapter;
    private Dialog dialog;
    private Password passwordItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog);
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_password, null, false);

        final EditText etSrcName = (EditText) contentView.findViewById(R.id.etSrcName);
        final EditText etPasword = (EditText) contentView.findViewById(R.id.etPassword);

        final DialogInterface.OnClickListener listener;

        if(getArguments() != null) {
            passwordItem = (Password) getArguments().getSerializable("passwordItem");
            if (passwordItem != null) {
                etSrcName.setText(passwordItem.getSrcName());
                etPasword.setText(passwordItem.getPassword());
            }
        }

        dialog = builder.setView(contentView)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle((passwordItem == null?getString(R.string.add_password_title):getString(R.string.edit_password_title)))
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            Password psw = new Password();
                            String srcname = etSrcName.getText().toString();
                            String password = etPasword.getText().toString();
                            if (TextUtils.isEmpty(srcname)) {
                                etSrcName.setError(getString(R.string.source_empty_message));
                                preventDismissDialog();

                            } else if (TextUtils.isEmpty(password)) {
                                etPasword.setError(getString(R.string.password_empty_message));
                                preventDismissDialog();

                            } else if (!TextUtils.isEmpty(srcname) && !TextUtils.isEmpty(password)) {

                                psw.setSrcName(srcname);
                                psw.setPassword(password);
                                if (passwordItem != null) {
                                    psw.setId(passwordItem.getId());
                                    PasswordDbHelper.getPasswordDbHelper(getContext()).updaetPassword(psw);
                                    ((BookCollectionActivity) getActivity()).showMessage(getString(R.string.edit_password_data_success));
                                } else {
                                    PasswordDbHelper.getPasswordDbHelper(getContext()).addPassword(psw);
                                    ((BookCollectionActivity) getActivity()).showMessage(getString(R.string.add_password_success));
                                }
                                if (adapter != null) {
                                    adapter.refreshPasswordsList();
                                }
                                dismissDialog();
                                /*mFragmentTransaction = getFragmentManager().beginTransaction();
                                mFragmentTransaction.remove(AddPasswordFragment.this);
                                mFragmentTransaction.commit();
                                getFragmentManager().popBackStack();*/
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

                    mFragmentTransaction = getFragmentManager().beginTransaction();
                    mFragmentTransaction.remove(AddPasswordFragment.this);
                    mFragmentTransaction.commit();
                    getFragmentManager().popBackStack();

                }
                return false;
            }
        });


        return dialog;
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("XXXXXXXX");
        View contentView = inflater.inflate(R.layout.fragment_add_password,container,false);
        //super.onCreateView(inflater, container, savedInstanceState);
        return contentView;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    public void setAdapter(DecompressPasswordAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 关闭对话框
     */
    private void dismissDialog() {
        try {
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
        }
        dialog.dismiss();
    }

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog() {
        try {
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
