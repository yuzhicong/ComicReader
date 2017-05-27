package com.yzc.comicreader.ui.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.yzc.comicreader.App;
import com.yzc.comicreader.R;
import com.yzc.comicreader.adapter.BookCollectionAdapter;
import com.yzc.comicreader.adapter.DecompressPasswordAdapter;
import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.database.ComicDbHelper;
import com.yzc.comicreader.ui.fragment.AddPasswordFragment;
import com.yzc.comicreader.ui.fragment.DecompressPasswordInputFragment;
import com.yzc.comicreader.ui.fragment.InternalStorageListFragment;
import com.yzc.comicreader.util.Util;
import com.yzc.comicreader.util.ZipCommandUtil;
import com.yzc.comicreader.util.ZipProcess;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookCollectionActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,InternalStorageListFragment.OnFragmentInteractionListener {

    private FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvBookCollection)
    RecyclerView rvBookCollection;
    public BookCollectionAdapter adapter;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.rvPassword)
    RecyclerView rvPassword;
    private DecompressPasswordAdapter passwordAdapter;

    boolean isSelectPassword=false;//是否选择了密码模块

    String orderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collection);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setNavBarBackground();
        if(((App)getApplication()).startTheme == null){
            ((App)getApplication()).startTheme = this.theme;
            Log.e("BookCollection","startTheme : " + this.theme);
        }

        if(savedInstanceState != null){
        isSelectPassword = savedInstanceState.getBoolean("isSelectPassword",false);}

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSelectPassword){
                    InternalStorageListFragment fragment = new InternalStorageListFragment();
                    fragment.show(getSupportFragmentManager(),"Choose a Comic file:");
                }else{
                    AddPasswordFragment addPasswordFragment = new AddPasswordFragment();
                    addPasswordFragment.setAdapter(passwordAdapter);
                    addPasswordFragment.show(getSupportFragmentManager(),"Add Decompress Password");

                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        MenuItem menuItem = (MenuItem) navigationView.getMenu().findItem(R.id.nav_book_collection);
        menuItem.setChecked(true);


        adapter = new BookCollectionAdapter(getApplicationContext(),ComicDbHelper.getComicDBHelper(this).queryComicBook(null));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rvBookCollection.setLayoutManager(gridLayoutManager);
        rvBookCollection.setAdapter(adapter);
        adapter.setBookItemClickListener(new BookCollectionAdapter.OnBookItemClickListener() {
            @Override
            public void onBookItemClick(ComicBook book) {
                Intent intent = new Intent(BookCollectionActivity.this,ReaderActivity.class);
                intent.putExtra("comicbook",book);
                startActivityForResult(intent,200);
            }
        });
        adapter.setBookItemLongClickListener(new BookCollectionAdapter.OnBookItemLongClickListener() {
            @Override
            public void onBookItemLongClick(final ComicBook book) {
                AlertDialog.Builder diaBuilder = new AlertDialog.Builder(BookCollectionActivity.this,R.style.Theme_AppCompat_Light_Dialog);
                diaBuilder.setTitle(R.string.remove_book_title);
                diaBuilder.setMessage(R.string.remove_book_message);
                diaBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bookname = book.getBookName();
                        boolean canDelete = ComicDbHelper.getComicDBHelper(getApplicationContext()).deleteComicBook(book);
                        if(canDelete){
                            adapter.refreshBookList(null);
                            showMessage(getString(R.string.remove_book_success));
                        }
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
        setFuntionView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!((App)getApplication()).startTheme.equals(this.theme)){
                changeAppIcon(this.theme);
            }
            super.onBackPressed();
        }
    }

    public void setNavBarBackground(){
        String theme = Util.getStringPreference(this,"app_theme");
        int backgroundId;
        switch (theme){
            case "0":{
                backgroundId = R.drawable.side_nav_bar;
                break;
            }
            case "1":{
                backgroundId = R.drawable.side_nav_bar_sasuke;
                break;
            }
            case "2":{
                backgroundId = R.drawable.side_nav_bar_sakura;
                break;
            }
            case "3":{
                backgroundId = R.drawable.side_nav_bar_rocklee;
                break;
            }
            default:{
                backgroundId = R.drawable.side_nav_bar;
                break;
            }
        }
        ((LinearLayout)navigationView.getHeaderView(0).findViewById(R.id.nav_bar)).setBackgroundResource(backgroundId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_book_collection_sort_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isSelectPassword) {
            menu.clear();
        }
        return true;
    }

    /**
     * toolbar上的菜单监听，主要用于排序漫画
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.sort_by_time_asc:{
                adapter.refreshBookList("bookid asc");
                break;
            }
            case R.id.sort_by_time_desc:{
                adapter.refreshBookList("bookid DESC");
                break;
            }
            case R.id.sort_by_title:{
                adapter.refreshBookList("bookname asc");
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 抽屉菜单项监听
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_book_collection:{
                isSelectPassword = false;
                setFuntionView();
                break;
            }
            case R.id.nav_decompress_password:{
                isSelectPassword = true;
                setFuntionView();
                break;
            }
            case R.id.nav_settings:{
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                break;
            }
            case R.id.nav_about:{
                startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFuntionView(){
        if(!isSelectPassword){
            rvBookCollection.setVisibility(View.VISIBLE);
            rvPassword.setVisibility(View.GONE);
            toolbar.setTitle(R.string.book_collection);
            isSelectPassword = false;
            invalidateOptionsMenu();
        }else{
            rvBookCollection.setVisibility(View.GONE);
            rvPassword.setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.password_library);
            isSelectPassword = true;
            invalidateOptionsMenu();
            if(passwordAdapter == null){
                passwordAdapter = new DecompressPasswordAdapter(this);
                LinearLayoutManager lLayoutManager = new LinearLayoutManager(getApplicationContext());
                lLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rvPassword.setLayoutManager(lLayoutManager);
                rvPassword.setAdapter(passwordAdapter);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSelectPassword",isSelectPassword);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //isSelectPassword = savedInstanceState.getBoolean("isSelectPassword");
        //setFuntionView();
    }

    @Override
    public void onFragmentInteraction(final String dirPath) {
        Log.e("activity","收到选择文件：" + dirPath);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        getSupportFragmentManager().popBackStackImmediate("Choose a Comic file:",1);
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        //判断漫画是否已经入库
        final ComicDbHelper helper = ComicDbHelper.getComicDBHelper(this);
        if(helper.hasThisBook(dirPath)){
            showMessage(getString(R.string.file_already_add));
        }else {
            final String outputPath = getExternalCacheDir().getAbsolutePath() + "/.Comic/" + ComicBook.parseBookname(dirPath);

            String decompressCommand = ZipCommandUtil.getDecompressCommand(dirPath,
                    outputPath,true,null,ZipCommandUtil.OverWriteMode.OVERWRITE_ALL_EXISTING_FILE,null);

            Log.e("Decompress:",decompressCommand);
            ZipProcess zipProcess = new ZipProcess(this,decompressCommand,dirPath);

            zipProcess.setZipProcessListener(new ZipProcess.ZipProcessListenner() {
                @Override
                public void onZipSuccess(final ProgressDialog dialog) {//需要运行在非ui线程否则会卡顿
                    dialog.setTitle(getString(R.string.analyse_title));
                    dialog.setMessage(getString(R.string.analyse_message));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean canInsert = helper.insertComicBook(new ComicBook(getApplicationContext(),dirPath,outputPath,""));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if(canInsert){
                                        adapter.refreshBookList(null);
                                        showMessage(getString(R.string.add_book_success));
                                    }else{
                                        showMessage(getString(R.string.add_book_failure));
                                    }
                                }
                            });

                        }
                    }).start();

                }

                @Override
                public void onZipFault(ProgressDialog dialog) {
                    dialog.dismiss();
                    DialogFragment DCPassword = new DecompressPasswordInputFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dirPath",dirPath);
                    DCPassword.setArguments(bundle);
                    DCPassword.show(getSupportFragmentManager(),"DCPassword Fragment");
                    //showMessage("解压失败，需要密码");
                }
            });

            zipProcess.start();//开始解压
        }

    }

    public void showMessage(String msg){
        Snackbar.make(fab, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        adapter.refreshBookList(orderBy);
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void changeAppIcon(String theme){

        PackageManager pm = getPackageManager();
        Log.e("BookCollection","startTheme : " + ((App)getApplication()).startTheme + " localTheme : " + theme);
        switch (((App)getApplication()).startTheme){
            case "0":{
                pm.setComponentEnabledSetting(new ComponentName(this,"com.yzc.comicreader.ui.activity.BookCollectionActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "1":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.SaSuKeActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "2":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.SaKuRaActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "3":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.RockLeeActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            default:{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.ui.activity.BookCollectionActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            }
        }
        switch (theme){
            case "0":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.ui.activity.BookCollectionActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "1":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.SaSuKeActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "2":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.SaKuRaActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            case "3":{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.RockLeeActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
            }
            default:{
                pm.setComponentEnabledSetting(new ComponentName(this, "com.yzc.comicreader.ui.activity.BookCollectionActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
            }
        }
        ((App)getApplication()).startTheme = this.theme;
    }

}
