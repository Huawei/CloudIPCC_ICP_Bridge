
package com.huawei.bridge.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.config.PasswdPropertyPlaceholder;
import com.huawei.bridge.common.config.RootKeyManager;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;
import com.huawei.bridge.service.AutoLoginThread;



public class SystemInitListener implements ServletContextListener
{

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitListener.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        AutoLoginThread.end();
    }

    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        String path = event.getServletContext().getRealPath("/");
        
        /**
         * 解析keys.properties
         */
        LOG.info("Start Parse keys.properties begin...");
        if (!RootKeyManager.parseKeysProperties(path))
        {
            LOG.error("Parse keys.properties failed"); 
            PasswdPropertyPlaceholder.clean();
            return;
        }
        LOG.info("Start Parse keys.properties end...");
        
        /**
         * 初始化工作秘钥
         */
        PasswdPropertyPlaceholder.init();
        
        /**
         * 配置信息初始化
         */
        LOG.info("Start load config files...");
        if (!ConfigProperties.loadConfig())
        {
            LOG.error("Load config file failed, we are shutdown now.");
            PasswdPropertyPlaceholder.clean();
            RootKeyManager.cleanKey();
            return;
        }
        
        RootKeyManager.cleanKey();
        LOG.info("Start load config files end...");
        
        StringBuffer cdsServerurl = new StringBuffer();
        if ("1".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.BASIC, "UPORTAL_SERVER_ISENABLESSL")))
        {
            //SSL
            cdsServerurl.append("https://");
        }
        else
        {
            cdsServerurl.append("http://");
        }
        cdsServerurl.append(ConfigProperties.getKey(ConfigList.BASIC, "UPROTAL_SERVER_IP")).append(":");
        cdsServerurl.append(ConfigProperties.getKey(ConfigList.BASIC, "UPORTAL_SERVER_PORT"));
        GlobalObject.setCdsServerUrl(cdsServerurl.toString());
        
        Request.init();
        AutoLoginThread.begin();
        
    }

}
