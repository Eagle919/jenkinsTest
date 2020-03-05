package com.github.pig.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class OperationFileUtil {

    private OperationFileUtil(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationFileUtil.class);

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static void saveFile(InputStream inputStream, String fileName,String path) {

            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
        try (
                OutputStream os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
                ){
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            LOGGER.error("OperationFileUtil saveFile error",e);
        } finally {
            // 完毕，关闭所有链接
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("OperationFileUtil saveFile close error",e);
            }
        }
    }

    public static HttpServletResponse downLoad(String path, HttpServletResponse response) {

            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
        try (
                InputStream fis = new BufferedInputStream(new FileInputStream(path));
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                ){
            // 以流的形式下载文件。
            byte[] buffer = new byte[fis.available()];
            int count = 0;
            count = fis.read(buffer);
            LOGGER.info("文件大小 {}" , count);
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader(CONTENT_DISPOSITION, "attachment;filename=" + new String(filename.getBytes(), "ISO-8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
        } catch (IOException ex) {
            LOGGER.error("OperationFileUtil downLoad close error",ex);
        }
        return response;
    }

    /**
     * 获取文件扩展名
     * @return
     */
    public static String getExt(String filename) {
        int index = filename.lastIndexOf('.');

        if (index == -1) {
            return null;
        }
        return filename.substring(index + 1);
    }
}
