
package com.huawei.bridge.servlet;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.global.ServletErrorCode;
import com.huawei.bridge.common.util.JsonUtils;
import com.huawei.bridge.service.ThirdControlService;


public class StatusControlServlet extends CommonServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(StatusControlServlet.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 6619680075848317185L;
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    {
        try 
        {
            HttpSession session = req.getSession();
            String monitoredWorkNo = (String)session.getAttribute("monitoredWorkNo");
            String message;
            if (null == monitoredWorkNo)
            {
                message = makeResponseString(ServletErrorCode.NO_MONITORED, "No agent is monitored", "");
                writeResponse(resp, message);
                LOG.error("Do status control failed. The result : {}", message);
                return;
            }
            String type = req.getParameter("operType");
            if (!"1".equals(type) && !"0".equals(type))
            {
                message = makeResponseString(ServletErrorCode.PARAM_INVALID, "operType is not right", monitoredWorkNo);
                writeResponse(resp, message);
                LOG.error("Do status control on the agent [{}] failed. The result : {}", monitoredWorkNo, message);
                return;
            }
            
            String jsessionId = session.getId();
            String tempId = GlobalObject.getJSessionIdByMonitoredAgent(monitoredWorkNo);
            if (jsessionId.equals(tempId))
            {
                ThirdControlService service = new ThirdControlService(monitoredWorkNo);
                Map<String, Object> result  = service.doStatusControl(type);
                result.put("monitoredWorkNo", monitoredWorkNo);
                message = JsonUtils.beanToJson(result);
                writeResponse(resp, message);
                if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
                {
                    LOG.error("Do status control on the agent [{}] failed. The result : {}", monitoredWorkNo, message);
                }
                
            }
            else
            {
                
                message = makeResponseString(ServletErrorCode.HAS_MONITORED_BY_OTHER, "Has monitored by other", monitoredWorkNo);
                writeResponse(resp, message);
                LOG.error("Do status control on the agent [{}] failed. The result : {}", monitoredWorkNo, message);
            }
        }
        catch (Throwable t) 
        {
            LOG.error("Catch throwable at StatusControlServlet. \r\n {}", t.getMessage());
        }
    }

}
