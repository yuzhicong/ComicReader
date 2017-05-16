package com.yzc.comicreader.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yzc.comicreader.R;
import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.database.ComicDbHelper;

import java.io.File;
import java.util.List;

/**
 * Created by YuZhicong on 2017/4/30.
 */

public class BookCollectionAdapter extends RecyclerView.Adapter<BookCollectionAdapter.BookItem> {

    private Context mContext;
    private List<ComicBook> mList;
    private OnBookItemClickListener bookItemClickListener;
    private OnBookItemLongClickListener bookItemLongClickListener;
    public BookCollectionAdapter(Context context,List<ComicBook> list){
        mContext = context;
        mList = list;
    }

    @Override
    public BookItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_comic_book,null);
        BookItem bookItem = new BookItem(itemView);
        return bookItem;
    }

    @Override
    public void onBindViewHolder(BookItem holder, int position) {
        final ComicBook comicBook = mList.get(position);
        holder.tvBookName.setText(comicBook.getBookName());
        holder.tvReadingProgerss.setText(comicBook.getLastPosition() + "/" + comicBook.getBookPage());
        Glide.with(mContext).load(new File(comicBook.getBookCover())).into(holder.ivBookCover);
        if (bookItemClickListener != null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookItemClickListener.onBookItemClick(comicBook);
                }
            });
        }
        if(bookItemLongClickListener != null){
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    bookItemLongClickListener.onBookItemLongClick(comicBook);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class BookItem extends RecyclerView.ViewHolder{

        public TextView tvBookName,tvReadingProgerss;
        public ImageView ivBookCover;
        public CardView cardView;
        public BookItem(View itemView) {
            super(itemView);
            ivBookCover = (ImageView) itemView.findViewById(R.id.ivBookCover);
            tvBookName = (TextView) itemView.findViewById(R.id.tvBookName);
            tvReadingProgerss = (TextView) itemView.findViewById(R.id.tvReadingProgress);
            cardView = (CardView) itemView.findViewById(R.id.cvBook);
        }
    }

    public interface OnBookItemClickListener{
        void onBookItemClick(ComicBook book);
    }

    public interface OnBookItemLongClickListener{
        void onBookItemLongClick(ComicBook book);
    }

    public void setBookItemClickListener(OnBookItemClickListener bookItemClickListener) {
        this.bookItemClickListener = bookItemClickListener;
    }

    public void setBookItemLongClickListener(OnBookItemLongClickListener bookItemLongClickListener) {
        this.bookItemLongClickListener = bookItemLongClickListener;
    }

    public void refreshBookList(String orderBy){
        mList = ComicDbHelper.getComicDBHelper(mContext).queryComicBook(orderBy);
        this.notifyDataSetChanged();
    }
}
