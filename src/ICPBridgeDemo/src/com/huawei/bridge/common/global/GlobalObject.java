
package com.huawei.bridge.common.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.huawei.bridge.bean.AgentAuthInfoBean;
import com.huawei.bridge.bean.EventCount;

/**
 * 
 * <p>Title: 全局变量 (Global Variable) </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class GlobalObject
{
    /**
     * 第三方观察事件统计结果
     */
    private static  EventCount  eventCount = new EventCount();
       
    /**
     * 座席鉴权信息结合 (The map about the agent's auth info)
     * key : 座席工号 (work no)
     * value ： 鉴权信息  (auth info)
     */
    private static final Map<String, AgentAuthInfoBean> AGENT_AUTH_INFO_MAP = new HashMap<String, AgentAuthInfoBean>();
    
    /**
     * 事件集合(The map of the events)
     * key : 被监视的座席工号(the monitored work no)
     * value : 事件队列(The queue of the events)
     */
    private static final Map<String, Queue<Map<String, Object>>> EVENT_MAP = new ConcurrentHashMap<String, Queue<Map<String, Object>>>();
    

    /**
     * 当前来自界面监视
     * key: 被监视的座席工号(the monitored work no)
     * value: jessionid
     */
    private static final Map<String, String> CURRENT_MONITORED_AGENTS = new HashMap<String, String>();
    
    
    private static final byte[] LOCK_OBJECT = new byte[0];
    
    /**
     * 已经被监视的座席(The monitored agents have been monitored by the system)
     */
    private static List<String> monitoredAgentList = new ArrayList<String>();
    
    private static String agentServerUrl = "";
    
    
    private static boolean isLoginSucess;
    
    /**
     * 获取第三方观察事件统计结果
     * @return
     */
    public static EventCount getEventCount()
    {
        return eventCount;
    }
    
    /**
     * 设置第三方观察事件统计结果
     * @param eventCount
     */
    public static void setEventCount(EventCount eventCount)
    {
        GlobalObject.eventCount = eventCount;
    }

    
    /**
     * 新增座席鉴权信息 (Add the agent's auth info)
     * @param workNo 座席工号 (work no)
     * @param authInfo 鉴权信息  (auth info)
     */
    public static void addAgentAuthInfo(String workNo, AgentAuthInfoBean authInfo)
    {
        AGENT_AUTH_INFO_MAP.put(workNo, authInfo);
    }
    
    /**
     * 删除鉴权信息 (Delete the agent's auth info)
     * @param workNo 座席工号 (work no)
     */
    public static void delAgentAuthInfo(String workNo)
    {
        AGENT_AUTH_INFO_MAP.remove(workNo);
    }
    
    /**
     * 获取鉴权信息 (Get the agent's auth info)
     * @param workNo 座席工号 (work no)
     * @return
     */
    public static AgentAuthInfoBean getAgentAuthInfo(String workNo)
    {
        return AGENT_AUTH_INFO_MAP.get(workNo);
    }
    
    /**
     * 设置AgentServer的访问url (set the agentserver's url)
     * @param agentServerUrl
     */
    public static void setAgentServerUrl(String agentServerUrl)
    {
        GlobalObject.agentServerUrl = agentServerUrl;
    }
    
    /**
     * 获取AgentServer的访问url  (get the agentserver's url)
     * @return
     */
    public static String getAgentServerUrl()
    {
        return GlobalObject.agentServerUrl;
    }

    /**
     * 观察者登录结果(The monitor is login success)
     * @return
     */
    public static boolean isLoginSucess()
    {
        return isLoginSucess;
    }

    /**
     * 设置观察者登录结果(Set the result of login)
     * @param isLoginSucess
     */
    public static void setLoginSucess(boolean isLoginSucess)
    {
        GlobalObject.isLoginSucess = isLoginSucess;
    }
    
    
    public static void setMonitoredAgentList(List<String> agents)
    {
        GlobalObject.monitoredAgentList = agents;
    }
    
    
    /**
     * 判断传入的被监控座席是否存在 (Whether the monitored agent exists)
     * @param monitoredWorkNo
     * @return
     */
    public static boolean isValidMonitoredAgent(String monitoredWorkNo)
    {
        return GlobalObject.monitoredAgentList.contains(monitoredWorkNo);
    }
    
    
    public static void addMonitoredAgent(String monitoredWorkNo, String jessionId)
    {
        synchronized (LOCK_OBJECT)
        {
            CURRENT_MONITORED_AGENTS.put(monitoredWorkNo, jessionId);
            EVENT_MAP.put(monitoredWorkNo, new ConcurrentLinkedQueue<Map<String,Object>>());
        }
    }
    
    
    public static Queue<Map<String, Object>> getEventFromMonitoredAgent(String monitoredWorkNo)
    {
        synchronized (LOCK_OBJECT)
        {
            return EVENT_MAP.get(monitoredWorkNo);
        }
    }
    
    public static String getJSessionIdByMonitoredAgent(String monitoredWorkNo)
    {
        synchronized (LOCK_OBJECT)
        {
            return CURRENT_MONITORED_AGENTS.get(monitoredWorkNo);
        }
    }
    
    public static void removeMonitoredAgent(String monitoredWorkNo, String jessionId)
    {
        synchronized (LOCK_OBJECT)
        {
            String tempJessionId = CURRENT_MONITORED_AGENTS.get(monitoredWorkNo);
            if (tempJessionId != null && tempJessionId.equals(jessionId))
            {
                CURRENT_MONITORED_AGENTS.remove(jessionId);
                EVENT_MAP.remove(monitoredWorkNo);
            }
        }
    }
}
