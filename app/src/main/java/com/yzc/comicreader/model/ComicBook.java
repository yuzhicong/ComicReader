package com.yzc.comicreader.model;

import android.content.Context;

import com.yzc.comicreader.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by YuZhicong on 2017/4/2.
 * 漫画书实体类
 */

public class ComicBook implements Serializable{

    private int bookId;
    private String bookName;
    private String bookCover;
    private String filePath;
    private String srcPath;
    private int bookPage;
    private int lastPosition;
    private boolean needPassword;
    private String password;

    /**
     * 构造漫画书实体类时 只需一个文件路径即可，其它参数自动生成
     * @param context 上下文
     * @param filePath 文件路径*/
    public ComicBook(Context context,String filePath,String srcPath,String password){
        this.filePath = filePath;
        this.bookName = parseBookname(filePath);
        this.srcPath = srcPath;
        boolean successDecompress = true;
        if(password == null){
            this.needPassword = true;
            this.password = "";
            successDecompress = false;
        }else if(password.endsWith("")){
            this.needPassword = false;
            this.password = "";
        }else{
            this.needPassword = true;
            this.password = password;
        }

        if(successDecompress){
            File dir = new File(srcPath);
            if(dir.canRead()&&dir.isDirectory()){
                File files[] = dir.listFiles();
                int countingpage = 0;
                boolean isFindBookCover = false;
                for (File tempfile : files) {
                    String tempfilePath = tempfile.getAbsolutePath().toLowerCase();
                    if(tempfilePath.endsWith("jpg")||tempfilePath.endsWith("png")||tempfilePath.endsWith("jpeg")){
                        if(!isFindBookCover){
                            File copyFile = new File(tempfile.getAbsolutePath().replace(".Comic",".ComicbookCover"));
                            try {
                                copyFile.createNewFile();
                                Util.copyFileUsingFileChannels(tempfile,copyFile);
                                this.bookCover = copyFile.getAbsolutePath();
                            } catch (IOException e) {
                                e.printStackTrace();
                                this.bookCover = tempfile.getAbsolutePath();
                            }
                            isFindBookCover = true;//将遍历遇到的第一张图片作为漫画封面
                        }
                        countingpage++;//统计漫画页数
                    }
                }
                this.bookPage = countingpage;
                this.lastPosition = 1;
            }
        }
    }
    public ComicBook(Context context,String filePath){
        this(context,filePath,"",null);
    }

    public ComicBook(){

    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookCover() {
        return bookCover;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getBookPage() {
        return bookPage;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public boolean isNeedPassword() {
        return needPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setBookPage(int bookPage) {
        this.bookPage = bookPage;
    }

    public void setNeedPassword(boolean needPassword) {
        this.needPassword = needPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public static String parseBookname(String filePath){
        return filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
    }
}
