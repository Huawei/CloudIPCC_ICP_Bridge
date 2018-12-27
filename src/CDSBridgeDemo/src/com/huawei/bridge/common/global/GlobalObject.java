
package com.huawei.bridge.common.global;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * <p>Title: 全局变量 (Global Variable) </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class GlobalObject
{
    /**
     * key : userId
     * value : token
     */
    private static final ConcurrentHashMap<String, String> AUTH_INFO_MAP = new ConcurrentHashMap<String ,String>();
    
    private static String cdsServerUrl = "";
    
    private static boolean isLoginSucess;
    
    
    
    
    /**
     * 观察者登录结果(The monitor is login success)
     * @return
     */
    public static boolean isLoginSucess()
    {
        return isLoginSucess;
    }

    
    /**
     * 获取CDSServer的访问url  (get the cdsServer's url)
     * @return
     */
    public static String getCdsServerUrl() 
    {
        return cdsServerUrl;
    }

    /**
     * 设置CDSServer的访问url (set the cdsServer's url)
     * @param cdsServerUrl
     */
    public static void setCdsServerUrl(String cdsServerUrl)
    {
        GlobalObject.cdsServerUrl = cdsServerUrl;
    }

    /**
     * 设置观察者登录结果(Set the result of login)
     * @param isLoginSucess
     */
    public static void setLoginSucess(boolean isLoginSucess)
    {
        GlobalObject.isLoginSucess = isLoginSucess;
    }
    
    
    public static void setAuthInfo(String userId, String token)
    {
        AUTH_INFO_MAP.put(userId, token);
    }
    
    public static String getAuthInfo(String userId)
    {
        return AUTH_INFO_MAP.get(userId);
    }
}
