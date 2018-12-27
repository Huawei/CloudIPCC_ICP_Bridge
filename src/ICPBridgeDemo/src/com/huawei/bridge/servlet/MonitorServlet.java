
package com.huawei.bridge.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.global.ServletErrorCode;
import com.huawei.bridge.common.util.LogUtils;
import com.huawei.bridge.common.util.StringUtils;

/**
 * 
 * <p>Title: Monitor the agent </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class MonitorServlet extends CommonServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 6201890070276306480L;
    
    private static final Logger LOG = LoggerFactory.getLogger(MonitorServlet.class);
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    {
      
        try
        {
            String message;
            String monitoredWorkNo = req.getParameter("monitoredWorkNo");
            if (StringUtils.isNullOrBlank(monitoredWorkNo))
            {
                message = makeResponseString(ServletErrorCode.PARAM_INVALID, "monitoredWorkNo is invalid", monitoredWorkNo);
                LOG.error("Monitor agent failed. the result is {}.", message);
                writeResponse(resp, message);
                return;
            }
            
            if (!GlobalObject.isValidMonitoredAgent(monitoredWorkNo))
            {
                message = makeResponseString(ServletErrorCode.MONITORED_AGENT_NOT_EXIST, "The monitored agent does not exist", monitoredWorkNo);
                LOG.error("Monitor agent failed. the result is {}",  message);
                writeResponse(resp, message);
                return;
            }
            req.getSession().invalidate();
            req.getSession().setAttribute("monitoredWorkNo", monitoredWorkNo);
            GlobalObject.addMonitoredAgent(monitoredWorkNo, req.getSession().getId());
            LOG.info("Monitor agent {} success.",  LogUtils.encodeForLog(monitoredWorkNo));
            message = makeResponseString(ServletErrorCode.SUCCESS, "", null);
            writeResponse(resp, message);
        }
        catch (Throwable e)
        {
            LOG.error("unkown exception {}", e.getMessage());
        }

    }
    
     
}
