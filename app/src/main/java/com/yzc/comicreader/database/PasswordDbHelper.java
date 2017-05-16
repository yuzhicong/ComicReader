package com.yzc.comicreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yzc.comicreader.model.Password;

import java.util.ArrayList;
import java.util.List;

/**
 * 解压缩密码数据管理类
 * Created by YuZhicong on 2017/5/4.
 */

public class PasswordDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "comic.db";
    private static final int DATABASE_VERSION = 1;
    private static PasswordDbHelper helper;

    private PasswordDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static PasswordDbHelper getPasswordDbHelper(Context context){
        if(helper == null){
            helper = new PasswordDbHelper(context.getApplicationContext());
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE passwords(id INTEGER PRIMARY KEY AUTOINCREMENT, src_name TEX, password TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPassword(Password password){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues psw = new ContentValues();
        psw.put("src_name",password.getSrcName());
        psw.put("password",password.getPassword());
        db.insert("passwords",null,psw);
        db.close();
    }

    public void delectePassword(Password password){
        SQLiteDatabase db = helper.getWritableDatabase();
        int i = db.delete("passwords","id=?",new String[]{Integer.toString(password.getId())});
        System.out.println(i);
        db.close();
    }

    public void updaetPassword(Password password){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues psw = new ContentValues();
        //psw.put("id",password.getId());
        psw.put("src_name",password.getSrcName());
        psw.put("password",password.getPassword());
        db.update("passwords",psw,"id=?",new String[]{Integer.toString(password.getId())});
        db.close();
    }

    public List<Password> queryPasswords(){
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Password> list = new ArrayList<>();
        Cursor cursor = db.query("passwords",new String[]{"id,src_name,password"},null,null,null,null,"id asc",null);

        while(cursor.moveToNext()){
            Password password = new Password();
            password.setId(cursor.getInt(0));
            password.setSrcName(cursor.getString(1));
            password.setPassword(cursor.getString(2));

            list.add(password);
        }

        cursor.close();
        db.close();

        return list;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!tabbleIsExist("passwords",db)){
        db.execSQL("CREATE TABLE passwords(id INTEGER PRIMARY KEY AUTOINCREMENT, src_name TEX, password TEXT);");
        }
    }

    /**
     * 判断某张表是否存在
     * @param tableName 表名
     * @return
     */
    public boolean tabbleIsExist(String tableName,SQLiteDatabase db){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
