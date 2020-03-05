package com.caih.cloud.iscs.charge.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class JarFileUtil {
    public static InputStream getFile(Class<?> cls, String fileName) throws Exception
    {
        InputStream in;
        if (File.separator.equals("/"))
            in = cls.getClassLoader().getResourceAsStream("config/" + fileName);
        else
            in = new FileInputStream(ResourceUtils.getFile("classpath:config/" + fileName));
        return in;
    }
}
