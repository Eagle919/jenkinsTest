package com.github.pig.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @className FileUtil
 * @Author chenkang
 * @Date 2018/11/7 16:11
 * @Version 1.0
 */
@Slf4j
public class FileCommonUtils {
    private FileCommonUtils(){}

    /**
     * 获取指定路径下的所有文件 -
     * @param path
     * @return
     */
    public static  File[] getFiles(String path){
        File file = new File(path);
        return file.listFiles();
    }

    /**
     * 获取文件后缀名
     * @param fileName
     * @return
     */
    public static String suffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * 判断路径是否存在，如果不存在则创建路径
     * @param path
     */
    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 上传附件
     * @param file
     * @param filePath
     * @param fileName
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) {
        try (FileOutputStream out = new FileOutputStream(filePath + fileName)) {
            out.write(file);
            out.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
