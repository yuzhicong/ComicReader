package com.yzc.comicreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yzc.comicreader.R;
import com.yzc.comicreader.adapter.BookPageAdapter;
import com.yzc.comicreader.database.ComicDbHelper;
import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.util.PagingScrollHelper;
import com.yzc.comicreader.util.RecyclerViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReaderActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rvBookPage)
    public RecyclerView rvBookPage;

    private PagingScrollHelper scrollHelper = new PagingScrollHelper();

    @BindView(R.id.sbChangePage)
    public SeekBar sbChangePage;

    @BindView(R.id.tvProcess)
    public TextView tvProgerss;
    ComicBook book;
    private  PagingScrollHelper.onPageChangeListener SHonPageChangeListener;

    @BindView(R.id.btnPrew)
    public Button btnPrew;

    @BindView(R.id.btnNext)
    public Button btnNext;

    @BindView(R.id.btnMode)
    public Button btnMode;

    @BindView(R.id.btnStyle)
    public Button btnStyle;

    @BindView(R.id.nightView)
    public View nightView;

    @BindView(R.id.toolbarReader)
    public Toolbar toolbar;

    @BindView(R.id.readingToolbar)
    public LinearLayout readingToolbar;

    public static int READ_MODE_SWIPE = 0;
    public static int READ_MODE_SCROLL = 1;

    private int readmode;

    private BookPageAdapter adapter;
    private LinearLayoutManager hLinearLayoutManager;
    private LinearLayoutManager vLinearLayoutManager;
    private boolean isShowReadingToolbar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        ButterKnife.bind(this);

        book = (ComicBook) getIntent().getSerializableExtra("comicbook");
        if(book != null){
            Log.e("ReaderActivity",book.toString());
        }

        toolbar.setTitle(book.getBookName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new BookPageAdapter(getApplicationContext(),book);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        hLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        vLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        readmode = READ_MODE_SWIPE;

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);

        adapter.setReadmode(readmode);
        rvBookPage.setLayoutManager(hLinearLayoutManager);
        rvBookPage.setAdapter(adapter);
        //rvBookPage.addItemDecoration(itemDecoration);

        rvBookPage.addOnItemTouchListener(new RecyclerViewClickListener(this, rvBookPage, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("rvBookPage",view.getId() + " onItemClick()");
                if(isShowReadingToolbar == true){
                    toolbar.setVisibility(View.INVISIBLE);
                    readingToolbar.setVisibility(View.INVISIBLE);
                    isShowReadingToolbar = false;
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                    readingToolbar.setVisibility(View.VISIBLE);
                    isShowReadingToolbar = true;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));



        SHonPageChangeListener = new PagingScrollHelper.onPageChangeListener() {
            @Override
            public void onPageChange(int index) {
                sbChangePage.setProgress(index);
                tvProgerss.setText((index + 1) + "/" + book.getBookPage());
            }
        };

        scrollHelper.setUpRecycleView(rvBookPage);
        scrollHelper.setOnPageChangeListener(SHonPageChangeListener);

        sbChangePage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MoveToPosition(hLinearLayoutManager,rvBookPage,i);
                tvProgerss.setText((i + 1) + "/" + book.getBookPage());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                scrollHelper.setStartingPage(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                scrollHelper.resetOffsetData(seekBar.getProgress());
            }
        });

        sbChangePage.setMax(book.getBookPage()-1);
        sbChangePage.setProgress(book.getLastPosition()-1);
        tvProgerss.setText(book.getLastPosition() + "/" + book.getBookPage());

        rvBookPage.post(new Runnable() {
            @Override
            public void run() {
                scrollHelper.resetOffsetData(sbChangePage.getProgress());
            }
        });

        Log.e("ReaderActivity",adapter.getItemCount()+ "");
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager  设置RecyclerView对应的manager
     * @param n  要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager   设置RecyclerView对应的manager
     * @param mRecyclerView  当前的RecyclerView
     * @param n  要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {


        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }

    @OnClick({R.id.btnPrew,R.id.btnNext,R.id.btnMode,R.id.btnStyle})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.btnPrew:{
                if(sbChangePage.getProgress()>0){
                    scrollHelper.setStartingPage(sbChangePage.getProgress());

                    sbChangePage.setProgress(sbChangePage.getProgress()-1);

                    scrollHelper.resetOffsetData(sbChangePage.getProgress());
                }
                break;
            }
            case R.id.btnNext:{
                if(sbChangePage.getProgress()<book.getBookPage()-1) {
                    scrollHelper.setStartingPage(sbChangePage.getProgress());

                    sbChangePage.setProgress(sbChangePage.getProgress() + 1);

                    scrollHelper.resetOffsetData(sbChangePage.getProgress());
                }
                break;
            }
            case R.id.btnStyle:{
                if(nightView.getVisibility() == View.INVISIBLE){
                    nightView.setVisibility(View.VISIBLE);
                    btnStyle.setText(getString(R.string.read_style_dark));
                    btnStyle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_read_style_dark),null,null);
                    //btnStyle.setCompoundDrawables(null, getResources().getDrawable(R.mipmap.ic_read_style_dark),null,null);
                }else if(nightView.getVisibility() == View.VISIBLE){
                    nightView.setVisibility(View.INVISIBLE);
                    btnStyle.setText(getString(R.string.read_style_light));
                    btnStyle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_read_style_light),null,null);
                    //btnStyle.setCompoundDrawables(null, getResources().getDrawable(R.mipmap.ic_read_style_light),null,null);
                }
                break;
            }
            case R.id.btnMode:{
                if(readmode == READ_MODE_SWIPE){
                    scrollHelper.unSetRecycleView(rvBookPage);
                    btnMode.setText(getString(R.string.read_mode_scroll));
                    btnMode.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_read_mode_scroll),null,null);
                    rvBookPage.setLayoutManager(vLinearLayoutManager);
                    readmode = READ_MODE_SCROLL;
                    adapter.setReadmode(readmode);
                }else if(readmode == READ_MODE_SCROLL){
                    btnMode.setText(getString(R.string.read_mode_swipe));
                    btnMode.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_read_mode_swipe),null,null);
                    rvBookPage.setLayoutManager(hLinearLayoutManager);
                    scrollHelper.setUpRecycleView(rvBookPage);
                    readmode = READ_MODE_SWIPE;
                    adapter.setReadmode(readmode);
                }
                break;
            }
        }
    }


    @Override
    protected void onPause() {
        //保存阅读记录
        book.setLastPosition(sbChangePage.getProgress() + 1);
        ComicDbHelper.getComicDBHelper(this).updateComicBook(book);
        setResult(RESULT_OK,null);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            ReaderActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
