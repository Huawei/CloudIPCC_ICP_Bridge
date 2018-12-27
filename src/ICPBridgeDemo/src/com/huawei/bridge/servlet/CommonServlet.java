
package com.huawei.bridge.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.common.util.JsonUtils;
public class CommonServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = -3585925553124042145L;
    
    private static final Logger LOG = LoggerFactory.getLogger(CommonServlet.class);
    
    protected void writeResponse(HttpServletResponse resp, String content)
    {
        resp.setStatus(200);
        PrintWriter writer = null;
        try
        {
            resp.addHeader("Cache-Control", "no-cache");
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html");
            writer = resp.getWriter();
            writer.print(content);
        }
        catch (IOException e)
        {
            LOG.error("writeResponse Failed. \r\n {}", e.getMessage());
        }
        finally
        {
            if (null != writer)
            {
                writer.close();
            }
        }
    }
    
    protected String makeResponseString(String retCode, String message, String monitoredWorkNo)
    {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("retcode", retCode);
        responseMap.put("message", message);
        responseMap.put("monitoredWorkNo", monitoredWorkNo);
        return JsonUtils.beanToJson(responseMap);
    }
    
    protected String makeEventResponseString(String retCode, String message, String monitoredWorkNo, Object result)
    {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("retcode", retCode);
        responseMap.put("message", message);
        responseMap.put("monitoredWorkNo", monitoredWorkNo);
        responseMap.put("result", result);
        return JsonUtils.beanToJson(responseMap);
    }

}
