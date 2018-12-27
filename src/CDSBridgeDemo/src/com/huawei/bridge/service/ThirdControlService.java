
package com.huawei.bridge.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.bridge.bean.AttendeeInfo;
import com.huawei.bridge.bean.MixGroupUserInfo;
import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;

public class ThirdControlService
{ 
    
    private String monitoredAgent;
    
    private String userId;
    
    public ThirdControlService(String monitoredAgent)
    {
        this.monitoredAgent = monitoredAgent;
        userId = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_USERID");
    }
    
    /**
     * 指示调度席发起呼叫
     * @param calleeNumber
     * @param isVideo
     * @return
     */
    public Map<String, Object> call(String calleeNumber, boolean isVideo)
    {
 
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getCdsServerUrl());
        urlSb.append("/CDS/thirdControl/");
        urlSb.append(monitoredAgent);
        urlSb.append("/call");
        
        Map<String, Object> reqObject = new HashMap<String, Object>();
        reqObject.put("calleeNumber", calleeNumber);
        reqObject.put("isVideo", isVideo);
       
        return Request.put(GlobalObject.getAuthInfo(userId), urlSb.toString(), reqObject);
    }
    
    /**
     * 指示调度席发起会议
     * @param isVideo
     * @param isRecord
     * @param attendees
     * @return
     */
    public Map<String, Object> conference(boolean isVideo, boolean isRecord, List<AttendeeInfo> attendees)
    {
      
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getCdsServerUrl());
        urlSb.append("/CDS/thirdControl/");
        urlSb.append(monitoredAgent);
        urlSb.append("/conference");
        
        Map<String, Object> reqObject = new HashMap<String, Object>();
        reqObject.put("isVideo", isVideo);
        reqObject.put("isRecord", isRecord);
        reqObject.put("attendees", attendees);
 
        return Request.put(GlobalObject.getAuthInfo(userId), urlSb.toString(), reqObject);
    }

    /**
     * 指示调度席发起混合群组呼叫 
     * @param groupAlias
     * @param userList
     * @return
     */
    public Map<String, Object> mixGroupCall(String groupAlias, List<MixGroupUserInfo> userList)
    {
       
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getCdsServerUrl());
        urlSb.append("/CDS/thirdControl/");
        urlSb.append(monitoredAgent);
        urlSb.append("/mixgroupcall");
        Map<String, Object> reqObject = new HashMap<String, Object>();
        reqObject.put("groupAlias", groupAlias);
        reqObject.put("userList", userList);
        return Request.put(GlobalObject.getAuthInfo(userId), urlSb.toString(), reqObject);
    }
    
    /**
     * 指示调度席发送短信 Send SMS
     * @param userList
     * @param smsContent
     * @return
     */
    public Map<String, Object> sendSMS(List<String> userList, String smsContent)
    {
       
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getCdsServerUrl());
        urlSb.append("/CDS/thirdControl/");
        urlSb.append(monitoredAgent);
        urlSb.append("/sms");
        Map<String, Object> reqObject = new HashMap<String, Object>();
        reqObject.put("userList", userList);
        reqObject.put("smsContent", smsContent);
        return Request.put(GlobalObject.getAuthInfo(userId), urlSb.toString(), reqObject);
    }

    
    
    
}
