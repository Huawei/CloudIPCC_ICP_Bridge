
package com.huawei.bridge.service;

import java.util.HashMap;
import java.util.Map;

import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;

public class ThirdControlService
{ 
    
    private String monitordAgent;
    
    public ThirdControlService(String monitordAgent)
    {
        this.monitordAgent = monitordAgent;
    }
    
    public Map<String, Object> doStatusControl(String type)
    {
        String workNo = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_WORKNO");
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("thirdcontrol/");
        urlSb.append(workNo);
        urlSb.append("/setstatus/");
        urlSb.append(monitordAgent);
        Map<String, Object> reqObject = new HashMap<String, Object>();
        reqObject.put("agentStatus", "0".equals(type) ? "AgentState_Idle" : "AgentState_Busy");
        return Request.put(workNo, urlSb.toString(), reqObject);
    }
}
