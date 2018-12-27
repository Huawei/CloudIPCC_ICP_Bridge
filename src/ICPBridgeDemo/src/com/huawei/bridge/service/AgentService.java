
package com.huawei.bridge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;
import com.huawei.bridge.common.util.JsonUtils;




/**
 * 
 * <p>Title:  登录和监视（Login And Monitor）</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AgentService
{
    
    private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);
    
    private static final int MAX_MONITOR_AGENTS = 500;
    private String workNo;
    
    public AgentService()
    {
        this.workNo = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_WORKNO");
    }
    
    /**
     * 进行登录 (do login)
     * @return
     */
    public boolean doLogin()
    {
       
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("onlineagent/");
        urlSb.append(workNo);
        urlSb.append("/forcelogin");
        
        Map<String, Object> loginMap = new HashMap<String, Object>();
        loginMap.put("password", ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_PASSWORD"));
        loginMap.put("phonenum", ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_PHONENUMBER"));
        
        Map<String, Object> result = Request.put(workNo, urlSb.toString(), loginMap);
        
        if (CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
        {
            //Login Success
            LOG.info("Login Success. the result is {}",  JsonUtils.beanToJson(result));
            return true;
        }
        LOG.error("Login failed. the error is {}", JsonUtils.beanToJson(result));
        return false;
    }
    
    /**
     * 开始监视座席(Start to monitor the agents)
     * @return
     */
    public boolean startMonitorAgents()
    {
        List<String> agents = getAllAgents();
        if (null == agents)
        {
            LOG.error("startMonitorAgents failed. because cannot get all agents");
            GlobalObject.setMonitoredAgentList(new ArrayList<String>());
            return false;
        }
        
        int size = agents.size();
        LOG.error("Begin to monitor [{}] agents. ", (size - 1));
        StringBuffer agentSb = new StringBuffer();
        int count = 0;
        for (int i = 0; i < size; i++)
        {
            if (workNo.equals(agents.get(i)))
            {
                continue;
            }
            if (count != 0)
            {
                agentSb.append(";");
            }
            agentSb.append(agents.get(i));
            count++;
            if (MAX_MONITOR_AGENTS == count
                    || i == (size - 1))
            {
                if (doMonitorAgents(agentSb.toString()))
                {
                    count = 0;
                    agentSb = new StringBuffer();

                }
                else
                {
                    GlobalObject.setMonitoredAgentList(new ArrayList<String>());
                    return false;
                }
            }
        }
        GlobalObject.setMonitoredAgentList(agents);
        return true;
    }
    
    /**
     * 监视座席(Monitor the agents)
     * @param agents
     * @return
     */
    private boolean doMonitorAgents(String agents)
    {
        /**
         * 在监视之前，先执行取消监视
         * Before begin to monitor, end monitor firstly.
         */

        doEndMonitor(agents);
        
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("qualitycontrol/");
        urlSb.append(workNo);
        urlSb.append("/beginmonitor");
        urlSb.append("?agents=");
        urlSb.append(agents);
        Map<String, Object> result = Request.post(workNo, urlSb.toString(), null);
        if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
        {
            LOG.error("Monitor failed. the result is \r\n {}.  \r\n  The agents is {}",
                    JsonUtils.beanToJson(result), agents);
            return false;
        }
        else
        {
            LOG.info("Monitor Success. The agents is \r\n {}", agents);
            return true;
        }
    }
    
    /**
     * end monitor firstly.
     * @param agents
     */
    private void doEndMonitor(String agents)
    {
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("qualitycontrol/");
        urlSb.append(workNo);
        urlSb.append("/endmonitor");
        urlSb.append("?agents=");
        urlSb.append(agents);
        Map<String, Object> result = Request.post(workNo, urlSb.toString(), null);
        if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
        {
            LOG.error("doEndMonitor failed. the result is \r\n {}.  \r\n  The agents is {}",
                    JsonUtils.beanToJson(result), agents);
            return;
        }
    }
 
    /**
     * 获取当前VDN的所有座席(Get the current agent)
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getAllAgents()
    {
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("agentgroup/");
        urlSb.append(workNo);
        urlSb.append("/allagentstatus");
        Map<String, Object> result = Request.get(workNo, urlSb.toString());
        if (!CommonConstant.SUCCESS.equals(String.valueOf(result.get("retcode"))))
        {
            return null;
        }
        
        List<String> monitoredList = new ArrayList<String>();
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("result");
        int length = resultList.size();
        for (int i = 0; i < length; i++)
        {
            monitoredList.add(String.valueOf(resultList.get(i).get("workno")));
        }
        return monitoredList;
    }
}
