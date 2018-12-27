package com.huawei.bridge.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.bean.EventCount;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.util.JsonUtils;



public class EventCountServlet extends CommonServlet {
    
	private static final long serialVersionUID = 1L;
       
    private static final Logger LOG = LoggerFactory.getLogger(EventCountServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	{
	    try 
	    {
	        request.setCharacterEncoding(CommonConstant.UTF_8);
	        response.setCharacterEncoding(CommonConstant.UTF_8);
	        
	        EventCount result = GlobalObject.getEventCount();
	        String responseResult = JsonUtils.beanToJson(result);
	        writeResponse(response, responseResult);
        }
	    catch (Throwable t) 
	    {
	        LOG.error("Catch throwable at AnswerCallServlet. \r\n {}", t.getMessage());
        }
	}



}
