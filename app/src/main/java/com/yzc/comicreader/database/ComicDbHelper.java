package com.yzc.comicreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yzc.comicreader.model.ComicBook;
import com.yzc.comicreader.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YuZhicong on 2017/4/20.
 * 漫画数据库工具类
 */

public class ComicDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "comic.db";
    private static final int DATABASE_VERSION = 1;
    private static ComicDbHelper comicDBHelper;

    private ComicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 使用单例模式 获取 ComicDbHelper实例对象
     * @param context 上下文对象
     * @return
     */
    public static ComicDbHelper getComicDBHelper(Context context) {
        if (comicDBHelper == null)
            comicDBHelper = new ComicDbHelper(context.getApplicationContext());

        return comicDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE comicbooks(bookid INT AUTO_INCREMENT, bookname TEXT, bookcover TEXT, filepath TEXT PRIMARY KEY, srcpath TEXT, bookpage INT, lastposition INT, needpassword TINYINT,password TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertComicBook(ComicBook book){

        if(!hasThisBook(book.getFilePath())) {

            SQLiteDatabase db = comicDBHelper.getWritableDatabase();
            db.insert("comicbooks", null, getComicbookValues(book));
            db.close();

            return true;
        }else{
            return false;}
    }

    public boolean updateComicBook(ComicBook book){

        if(hasThisBook(book.getFilePath())) {
            SQLiteDatabase db = comicDBHelper.getWritableDatabase();
            db.update("comicbooks", getComicbookValues(book), "filepath=?", new String[]{book.getFilePath()});
            db.close();

            return true;
        }else{
            return false;}
    }

    public boolean deleteComicBook(ComicBook book){

        if(hasThisBook(book.getFilePath())) {
            SQLiteDatabase db = comicDBHelper.getWritableDatabase();
            db.delete("comicbooks", "filepath=?", new String[]{book.getFilePath()});
            db.close();

            //删除漫画封面文件
            Util.deleteAllFilesOfDir(new File(book.getBookCover()));
            //删除本地解压缓存文件
            Util.deleteAllFilesOfDir(new File(book.getSrcPath()));

            return true;
        }else{
            return false;}
    }

    /**
     * 将漫画书实例对象转换为键值对对象
     * @param book
     * @return
     */
    public ContentValues getComicbookValues(ComicBook book){

        ContentValues values = new ContentValues();
        values.put("bookname", book.getBookName());
        values.put("bookcover", book.getBookCover());
        values.put("filepath", book.getFilePath());
        values.put("srcpath", book.getSrcPath());
        values.put("bookpage", book.getBookPage());
        values.put("lastposition", book.getLastPosition());
        values.put("needpassword", book.isNeedPassword() ? 1:0);
        values.put("password", book.getPassword());

        return values;
    }

    /**
     * 获取漫画书籍实体类集合
     * @param orderBy 排序条件
     * @return
     */
    public List<ComicBook> queryComicBook(String orderBy){

        if(orderBy == null) {
            orderBy = "bookid asc";//默认排序方法 根据书籍id升序
        }
        List<ComicBook> bookList = new ArrayList<>();

        SQLiteDatabase db = comicDBHelper.getWritableDatabase();
        Cursor cursor = db.query("comicbooks", new String[]{"bookid,bookname,bookcover,filepath,srcpath,bookpage,lastposition,needpassword,password"}, null, null, null, null, orderBy, null);

        while (cursor.moveToNext()) {
            ComicBook tempBook = new ComicBook();
            tempBook.setBookId(cursor.getInt(0));
            tempBook.setBookName(cursor.getString(1));
            tempBook.setBookCover(cursor.getString(2));
            tempBook.setFilePath(cursor.getString(3));
            tempBook.setSrcPath(cursor.getString(4));
            tempBook.setBookPage(cursor.getInt(5));
            tempBook.setLastPosition(cursor.getInt(6));
            tempBook.setNeedPassword(cursor.getInt(7) == 1);
            tempBook.setPassword(cursor.getString(8));

            bookList.add(tempBook);

        }

        cursor.close();
        db.close();

        return bookList;
    }

    /**
     * 根据书籍文件路径判断是否已经存在于表中
     * @param filePath
     * @return
     */
    public boolean hasThisBook(String filePath){
        List<ComicBook> bookList = queryComicBook(null);
        for (ComicBook temp : bookList) {
            if(temp.getFilePath().endsWith(filePath)){
                return true;
            }
        }
        return false;
    }

}
