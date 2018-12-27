
package com.huawei.bridge.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.util.StringUtils;

/**
 * 
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public class SessionListener implements HttpSessionListener
{
    private static final Logger LOG = LoggerFactory.getLogger(StatusControlServlet.class);
    
    @Override
    public void sessionCreated(HttpSessionEvent event)
    {
        
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        HttpSession session = event.getSession();
        String monitoredWorkNo = (String) session.getAttribute("monitoredWorkNo");
        if (StringUtils.isNullOrEmpty(monitoredWorkNo))
        {
            return;
        }
        LOG.warn("Remove agent's monitor {}", monitoredWorkNo);
        GlobalObject.removeMonitoredAgent(monitoredWorkNo, session.getId());
    }

}
