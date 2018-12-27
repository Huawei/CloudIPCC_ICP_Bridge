
package com.huawei.bridge.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class StringUtils
{ 
  
    /**
     * 判断字符串是否为null或者为空字符串（不含空格）。
     * Determine whether the string is null or empty string (not including spaces).
     * @param str 字符串变量(String Input)
     * @return true/false
     */
    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否为null或者为空字符串（含空格）。
     * Determine whether the string is null or empty string (including spaces).
     * @param str 字符串变量(String Input)
     * @return true/false
     */
    public static boolean isNullOrBlank(String str)
    {
        return str == null || str.trim().isEmpty();
    }
    
    
    /**
     * 判断是否是安全目录
     * @return
     */
    public static boolean isInSecureDir(File file)
    {
        String canPath;
        try
        {
            canPath = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            return false;
        }
        String absPath = file.getAbsolutePath();
        if (canPath.equalsIgnoreCase(absPath))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断是否是安全目录
     * @return
     */
    public static boolean isRegularFile(Path filePath)
    {
        BasicFileAttributes attr;
        try
        {
            attr = Files.readAttributes(filePath, 
                    BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return attr.isRegularFile();
        }
        catch (IOException e)
        {
            return false;
        }
    }
}