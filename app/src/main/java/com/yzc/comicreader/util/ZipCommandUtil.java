package com.yzc.comicreader.util;

import android.support.annotation.NonNull;

/**
 * Created by YuZhicong on 2017/4/23.
 * 解压缩命令工具类
 */

public class ZipCommandUtil {

    public static class OverWriteMode{
        public static int OVERWRITE_ALL_EXISTING_FILE = 0;
        public static int SKIP_EXTRACTING_OF_EXISTING_FILES = 1;
        public static int AUTO_RENAME_EXTRACTING_FILE = 2;
        public static int AUTO_RENAME_EXISTING_FILE = 3;
    }

    /**
     * 获取解压命令语句
     * @param srcPath 原文件路径
     * @param outputPath 输出路径
     * @param isFilesOnly 是否仅输出文件
     * @param password 解压密码
     * @param overWriteMode 写入模式
     * @param wildCard 文件通配符
     * @return
     */
    public static String getDecompressCommand(@NonNull String srcPath, @NonNull String outputPath, boolean isFilesOnly, String password, int overWriteMode, String wildCard){
        StringBuilder sbCmd = new StringBuilder("7z ");
        sbCmd.append(isFilesOnly ? "e " : "x ");	//7z e || 7z x
        //input file path
        sbCmd.append("'" + srcPath + "' ");	//7z x 'aaa/bbb.zip'
        //output path
        sbCmd.append("'-o" + outputPath + "' ");	//7z x 'a.zip' '-o/out/'
        if(wildCard != null){
            sbCmd.append("'" + wildCard + "' ");	//7z x 'a.zip' '-o/out/' '*.txt'
        }
        if(password != null){
            sbCmd.append("'-p" + password + "' ");	//7z x 'a.zip' '-o/out/' '*.txt' -ppwd
        }
        //overwrite mode
        switch (overWriteMode) {
            case 0:
                sbCmd.append("-aoa ");	//-aoa Overwrite All existing files without prompt.
                break;
            case 1:
                sbCmd.append("-aos ");	//-aos Skip extracting of existing files.
                break;
            case 2:
                sbCmd.append("-aou ");	//-aou aUto rename extracting file (for example, name.txt will be renamed to name_1.txt).
                break;
            case 3:
                sbCmd.append("-aot ");	//-aot auto rename existing file (for example, name.txt will be renamed to name_1.txt).
                break;
            default:
                sbCmd.append("-y");	//-y
                break;
        }
        return sbCmd.toString();
    }
}
