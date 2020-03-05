package com.caih.cloud.iscs.charge.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpWrapper {

    private static final int CONNECTION_TIMEOUT = 2000;
    private static final int READ_TIMEOUT = 2000;
    private static final String ENCODE_CHARSET = "utf-8";

    public static String getRequest(String requestUrl)
    {
        StringBuilder resultData = new StringBuilder();
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection urlConn = null;
        InputStreamReader in = null;
        BufferedReader buffer = null;
        try {
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConn.setReadTimeout(READ_TIMEOUT);
            in = new InputStreamReader(urlConn.getInputStream(), ENCODE_CHARSET);
            buffer = new BufferedReader(in);
            if (urlConn.getResponseCode() == 200) {
                String inputLine;
                while ((inputLine = buffer.readLine()) != null)
                    resultData.append(inputLine);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
                if (in != null)
                    in.close();
                if (urlConn != null)
                    urlConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultData.toString();
    }


    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            //2.中文有乱码的需要将PrintWriter改为如下
            //out=new OutputStreamWriter(conn.getOutputStream(),"UTF-8")
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
