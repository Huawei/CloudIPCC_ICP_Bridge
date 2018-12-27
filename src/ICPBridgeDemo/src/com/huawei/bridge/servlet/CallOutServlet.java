
package com.huawei.bridge.servlet;


import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.bean.CalloutParam;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.global.ServletErrorCode;
import com.huawei.bridge.common.util.JsonUtils;
import com.huawei.bridge.common.util.StringUtils;
import com.huawei.bridge.service.CallService;

public class CallOutServlet extends CommonServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = -8784582302020863825L;

    private static final Logger LOG = LoggerFactory.getLogger(CallOutServlet.class);

    private static final Pattern CALLER_PATTERN = Pattern.compile("^\\d{0,24}$");
    
    private static final Pattern CALLED_PATTERN = Pattern.compile("^[0-9*#]{0,24}$");

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
                LOG.error("Do Callout failed. The result : {}", message);
                return;
            }
            
            String jsessionId = session.getId();
            String tempId = GlobalObject.getJSessionIdByMonitoredAgent(monitoredWorkNo);
            if (jsessionId.equals(tempId))
            {
                
                CalloutParam param = getCallOutParam(req);
                if (!validCalloutParam(monitoredWorkNo, param))
                {
                    message = makeResponseString(ServletErrorCode.PARAM_INVALID, "param is invalid", monitoredWorkNo);
                    writeResponse(resp, message);
                    return;
                }
                
                CallService service = new CallService(monitoredWorkNo);
                Map<String, Object> result = service.callOut(param);
                result.put("monitoredWorkNo", monitoredWorkNo);
                message = JsonUtils.beanToJson(result);
                writeResponse(resp, message);
                if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
                {
                    LOG.error("Do callout on the agent [{}] failed. The result : {}",monitoredWorkNo, message);
                }
            }
            else
            {
                message = makeResponseString(ServletErrorCode.HAS_MONITORED_BY_OTHER, "Has monitored by other", monitoredWorkNo);
                writeResponse(resp, message);
                LOG.error("Do callout on the agent [{}] failed. The result : {}", monitoredWorkNo, message);
            }
        }
        catch (Throwable t) 
        {
            LOG.error("Catch throwable at CallOutServlet. \r\n {}", t.getMessage());
        }
    }
    
    private CalloutParam getCallOutParam(HttpServletRequest req)
    {
        CalloutParam param =  new CalloutParam();
        param.setCalled(req.getParameter("called"));
        param.setCaller(req.getParameter("caller"));
        try
        {
            param.setSkillid(Integer.valueOf(req.getParameter("skillid")));
        }
        catch (NumberFormatException e)
        {
            param.setSkillid(0);
        }
        try
        {
            param.setMediaability(Integer.valueOf(req.getParameter("mediaability")));
        }
        catch (NumberFormatException e)
        {
            param.setMediaability(0);
        }
        param.setCallappdata(req.getParameter("callappdata"));
        return param;
    }
    
    
    private boolean validCalloutParam(String monitoredWorkNo, CalloutParam param)
    {
        if (param.getCallappdata() != null 
                && param.getCallappdata().length() > 512)
        {
            LOG.error("Do callout on the agent [{}] failed. callappdata is invalid", monitoredWorkNo);
            return false;
        }
        
        if (param.getCaller() != null
                && !CALLER_PATTERN.matcher(param.getCaller()).matches())
        {
            LOG.error("Do callout on the agent [{}] failed. caller is invalid", monitoredWorkNo);
            return false;
        }
        
        if (StringUtils.isNullOrBlank(param.getCalled())
                || !CALLED_PATTERN.matcher(param.getCalled()).matches())
        {
            LOG.error("Do callout on the agent [{}] failed. called is invalid", monitoredWorkNo);
            return false;
        }
        
        return true;
    }
}
