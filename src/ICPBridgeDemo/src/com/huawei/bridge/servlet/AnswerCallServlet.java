
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
import com.huawei.bridge.service.CallService;

/**
 * 
 * <p>Title: 应答呼叫（Answer Call） </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AnswerCallServlet extends CommonServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(AnswerCallServlet.class);

    /**
     * 
     */
    private static final long serialVersionUID = 8921559713898991504L;

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
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
                LOG.error("Do answercall failed. The result : {}", message);
                return;
            }
            
            String tempId = GlobalObject.getJSessionIdByMonitoredAgent(monitoredWorkNo);
            if (jsessionId.equals(tempId))
            {
                CallService service = new CallService(monitoredWorkNo);
                Map<String, Object> result = service.callAnswer();
                result.put("monitoredWorkNo", monitoredWorkNo);
                message = JsonUtils.beanToJson(result);
                writeResponse(resp, message);
                if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
                {
                    LOG.error("Do answer call on the agent [{}] failed. The result : {}", monitoredWorkNo, message);
                }
            }
            else
            {
                message = makeResponseString(ServletErrorCode.HAS_MONITORED_BY_OTHER, "Has monitored by other", monitoredWorkNo);
                writeResponse(resp, message);
                LOG.error("Do answer call on the agent [{}] failed.  The result : {}", monitoredWorkNo, message);
            }
        } 
        catch (Throwable t) 
        {
            LOG.error("Catch throwable at AnswerCallServlet. \r\n {}", t.getMessage());
        }
    }
}
