
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
import com.huawei.bridge.service.GetEventThread;



public class SystemInitListener implements ServletContextListener
{

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitListener.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        GetEventThread.end();
        
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
        
        StringBuffer agentServerUrl = new StringBuffer();
        if ("1".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.BASIC, "AGENT_SERVER_ISENABLESSL")))
        {
            //SSL
            agentServerUrl.append("https://");
        }
        else
        {
            agentServerUrl.append("http://");
        }
        agentServerUrl.append(ConfigProperties.getKey(ConfigList.BASIC, "AGENT_SERVER_IP")).append(":");
        agentServerUrl.append(ConfigProperties.getKey(ConfigList.BASIC, "AGENT_SERVER_PORT"));
        agentServerUrl.append("/agentgateway/resource/");
        GlobalObject.setAgentServerUrl(agentServerUrl.toString());
        
        Request.init();
        GetEventThread.begin();
        
    }

}
