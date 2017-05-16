package com.yzc.comicreader.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yzc.comicreader.R;
import com.yzc.comicreader.adapter.FileItem;
import com.yzc.comicreader.adapter.FileItemAdapter;
import com.yzc.comicreader.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class InternalStorageListFragment extends AppCompatDialogFragment {

    private OnFragmentInteractionListener mListener;
    private File currentDir;//当前目录
    private ArrayList<FileItem> filesList = new ArrayList<>();//文件列表
    private ListView lvDirectory;
    private FileItemAdapter fileItemAdapter;
    private long sizeLimit = 1024 * 1024 * 1024;
    private String[] chhosefileType = {".zip", ".7z", ".rar", ".ZIP", ".RAR"};

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    public InternalStorageListFragment() {
        // Required empty public constructor
    }

    private View  getContentView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.fragment_internal_storage_list, container, false);
        lvDirectory = (ListView) rootView.findViewById(R.id.lvDirectory);

        File internalStorage;

        if (getArguments() != null) {
            //取出保存的值
            internalStorage = new File(getArguments().getString("dirPath"));
        } else {
            //内部存储根目录
            internalStorage = Environment.getExternalStorageDirectory();
        }
        if (listFiles(internalStorage)) {
            fileItemAdapter = new FileItemAdapter(getContext(), filesList);

            mFragmentManager = getFragmentManager();

            lvDirectory.setAdapter(fileItemAdapter);
            lvDirectory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position == 0 && getArguments() != null) {
                        //getFragmentManager().popBackStack();
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.remove(InternalStorageListFragment.this);
                        mFragmentTransaction.commit();
                        getFragmentManager().popBackStack();

                    } else {
                        FileItem item = filesList.get(position);
                        File file = item.file;

                        if (file.isDirectory()) {
                            //onFileItemSelect(file.getAbsolutePath());
                            InternalStorageListFragment fragmentNext = new InternalStorageListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("dirPath", file.getAbsolutePath());
                            fragmentNext.setArguments(bundle);

                            mFragmentTransaction = mFragmentManager.beginTransaction();
                            mFragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);

                            mFragmentTransaction.remove(InternalStorageListFragment.this);
                            mFragmentTransaction.addToBackStack(null);

                            //fragmentNext.show(mFragmentTransaction,file.getAbsolutePath());
                            mFragmentTransaction.add(fragmentNext,file.getAbsolutePath());
                            mFragmentTransaction.commit();
                        } else {
                            if (!file.canRead()) {
                                showErrorDialog(getString(R.string.access_error));
                                return;
                            }
                            if (sizeLimit != 0) {
                                if (file.length() > sizeLimit) {
                                    showErrorDialog(getString(R.string.over_file_size_limit));
                                    return;
                                }
                            }
                            if (file.length() == 0) {
                                return;
                            }
                            if (file.toString().contains(chhosefileType[0]) ||
                                    file.toString().contains(chhosefileType[1]) ||
                                    file.toString().contains(chhosefileType[2]) ||
                                    file.toString().contains(chhosefileType[3]) ||
                                    file.toString().contains(chhosefileType[4])) {
                                Log.d("ListFragment", "choose file:" + file.getAbsolutePath());
                                onFileItemSelect(file.getAbsolutePath());
                            } else {
                                showErrorDialog(getString(R.string.choose_correct_file));
                                return;
                            }

                        }
                    }
                }
            });
            fileItemAdapter.notifyDataSetChanged();
        }
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppThemeDialog);
        Dialog dialog = builder.setView(getContentView(LayoutInflater.from(getActivity()),null,savedInstanceState))
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.choose_a_comic_file)
                .setCancelable(true)
                //.setPositiveButton(R.string.alert_dialog_ok, 。。。)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.remove(InternalStorageListFragment.this);
                        mFragmentTransaction.commit();
                        getFragmentManager().popBackStack();

                    }
                })
                .create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.remove(InternalStorageListFragment.this);
                    mFragmentTransaction.commit();
                    getFragmentManager().popBackStack();

                }
                return false;
            }
        });
        return dialog;
    }


    public void onFileItemSelect(String dirPath) {
        if (mListener != null) {
            mListener.onFragmentInteraction(dirPath);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String dirPath);
    }

    private boolean listFiles(File dir) {
        if (!dir.canRead()) {
            if (dir.getAbsolutePath().startsWith(
                    Environment.getExternalStorageDirectory().toString())
                    || dir.getAbsolutePath().startsWith("/sdcard")
                    || dir.getAbsolutePath().startsWith("/mnt/sdcard")) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)
                        && !Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    currentDir = dir;
                    filesList.clear();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_SHARED.equals(state)) {
                        //emptyView.setText("UsbActive");
                    } else {
                        //emptyView.setText("NotMounted");
                    }
                    //clearDrawableAnimation(listView);
                    // scrolling = true;
                    //listAdapter.notifyDataSetChanged();
                    return true;
                }
            }
            //showErrorDialog("AccessError");
            return false;
        }
        //emptyView.setText("NoFiles");
        File[] files = null;
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            //showErrorDialog(e.getLocalizedMessage());
            return false;
        }
        if (files == null) {
            //showErrorDialog("UnknownError");
            return false;
        }
        currentDir = dir;
        filesList.clear();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() != rhs.isDirectory()) {
                    return lhs.isDirectory() ? -1 : 1;
                }
                return lhs.getName().compareToIgnoreCase(rhs.getName());
                /*
                 * long lm = lhs.lastModified(); long rm = lhs.lastModified();
				 * if (lm == rm) { return 0; } else if (lm > rm) { return -1; }
				 * else { return 1; }
				 */
            }
        });
        for (File file : files) {
            if (file.getName().startsWith(".")) {
                continue;
            }
            FileItem item = new FileItem();
            item.title = file.getName();
            item.file = file;
            if (file.isDirectory()) {
                item.icon = R.mipmap.ic_directory;
                item.subtitle = getString(R.string.folder);
            } else {
                String fname = file.getName();
                String[] sp = fname.split("\\.");
                item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                item.subtitle = Util.formatFileSize(file.length());
                fname = fname.toLowerCase();
                if (fname.endsWith(".jpg") || fname.endsWith(".png")
                        || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                    item.thumb = file.getAbsolutePath();
                }
            }
            filesList.add(item);
        }
        if (getArguments() != null) {//有参数，即非内置存储根目录，需要返回上一层
            FileItem item = new FileItem();
            item.title = "...";
            item.subtitle = getString(R.string.folder);
            item.icon = R.mipmap.ic_directory;
            item.file = null;
            filesList.add(0, item);
        }
        return true;
    }

    public void showErrorDialog(String error) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.app_name))
                .setMessage(error).setPositiveButton(R.string.ok, null).show();
    }
}
