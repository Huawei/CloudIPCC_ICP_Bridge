
package com.huawei.bridge.service;

import java.util.Map;
import com.huawei.bridge.bean.CalloutParam;
import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;

/**
 * 
 * <p>Title: 呼叫的操作（Call Operation） </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class CallService
{
    
    private String workNo;
    
    private String monitordAgent;
    
    public CallService(String monitordAgent)
    {
        this.workNo = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_WORKNO");
        this.monitordAgent = monitordAgent;
    }
    
    /**
     * 应答呼叫(Answer Call)
     * @return
     */
    public Map<String, Object> callAnswer()
    {
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("thirdcontrol/");
        urlSb.append(workNo);
        urlSb.append("/callanswer/");
        urlSb.append(monitordAgent);
        return Request.put(workNo, urlSb.toString(), null);
    }
    
    /**
     * 呼出(Do Call out)
     * @param calloutParam
     * @return
     */
    public Map<String, Object> callOut(CalloutParam calloutParam)
    {
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("thirdcontrol/");
        urlSb.append(workNo);
        urlSb.append("/callout/");
        urlSb.append(monitordAgent);
        return Request.put(workNo, urlSb.toString(), calloutParam);
    }
}
