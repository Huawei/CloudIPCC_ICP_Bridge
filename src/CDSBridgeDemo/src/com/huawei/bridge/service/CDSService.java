
package com.huawei.bridge.service;


import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;
import com.huawei.bridge.common.util.JsonUtils;




/**
 * 
 * <p>Title:  登录（Login）</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class CDSService
{
    
    private static final Logger LOG = LoggerFactory.getLogger(CDSService.class);
 
    private String userId;
    
    public CDSService()
    {
        userId = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_USERID");
    }
    
    /**
     * Login to CDS
     * @return
     */
    public boolean doLogin()
    {
        String url = GlobalObject.getCdsServerUrl() + "/login/sc";
        String password = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_PASSWORD");
        Map<String, Object> result = Request.loginPost(userId, password, url);
        if (CommonConstant.SUCCESS.equals(String.valueOf(result.get("returnCode"))))
        {
            LOG.info("Login Success.");
            return true;
        }
        LOG.error("Login failed. the error is {}", JsonUtils.beanToJson(result));
        return false;
    }  
    
    /**
     * update token request
     * @return
     */
    public Map<String, Object> updateToken()
    {
        String url =  GlobalObject.getCdsServerUrl() + "/sc/token";
        return Request.tokenPost(GlobalObject.getAuthInfo(userId), userId, url);
    }
}
