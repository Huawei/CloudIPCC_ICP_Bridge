
package com.huawei.bridge.servlet;

import java.util.Map;
import java.util.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.global.ServletErrorCode;
import com.huawei.bridge.common.util.LogUtils;

/**
 * 
 * <p>Title:  Demo界面获取事件（Get the event by Demo page）</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class EventServlet extends CommonServlet
{
    private static final long serialVersionUID = 1876233489588811696L;
    
    private static final Logger LOG = LoggerFactory.getLogger(EventServlet.class);
    
    
    private void sleepWhenNoEvent()
    {
        try
        {
            Thread.sleep(100l);
        }
        catch (InterruptedException e)
        {
            LOG.error(LogUtils.encodeForLog(e.getMessage()));
        }
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) 
    {
        try 
        {
            HttpSession session = req.getSession();
            String jsessionId = session.getId();
            String monitoredWorkNo = (String)session.getAttribute("monitoredWorkNo");
            String message;
            if (null == monitoredWorkNo)
            {
                message = makeResponseString(ServletErrorCode.NO_MONITORED, "No agent is monitored", "");
                writeResponse(resp, message);
                return;
            }
            String tempId = GlobalObject.getJSessionIdByMonitoredAgent(monitoredWorkNo);
            if (jsessionId.equals(tempId))
            {
                /**
                 * 没有事件，则10秒后返回
                 * If there is no event, it will reponse after waiting 10s
                 */
                int times = 0;
                while (times < 100)
                {
                    Queue<Map<String, Object>> queue = GlobalObject.getEventFromMonitoredAgent(monitoredWorkNo);
                    if (queue == null)
                    {
                        message = makeResponseString(ServletErrorCode.NO_EVENT_QUEUE, "No event queue of the monitored agent", monitoredWorkNo);
                        writeResponse(resp, message);
                        return;
                    }
                    Map<String, Object> event = queue.poll();
                    if (event == null)
                    {
                        sleepWhenNoEvent();
                    }
                    else
                    {
                        message = makeEventResponseString(ServletErrorCode.SUCCESS, "", monitoredWorkNo, event);
                        writeResponse(resp, message);
                        LOG.info("Get the event success. The result is {}", message);
                        return;
                    }
                    times++;
                }
                
                message = makeResponseString(ServletErrorCode.SUCCESS, "", monitoredWorkNo);
                writeResponse(resp, message);
                
            }
            else
            {
                message = makeResponseString(ServletErrorCode.HAS_MONITORED_BY_OTHER, "Has monitored by other page", monitoredWorkNo);
                writeResponse(resp, message);
            }
        }
        catch (Throwable t) 
        {
            LOG.error("Catch throwable at EventServlet. \r\n {}", t.getMessage());
        }
    }

}
