package com.huawei.bridge.service;


import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.bean.EventCount;
import com.huawei.bridge.common.config.ConfigList;
import com.huawei.bridge.common.config.ConfigProperties;
import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.http.Request;
import com.huawei.bridge.common.util.JsonUtils;
import com.huawei.bridge.common.util.LogUtils;



/**
 * 
 * <p>Title: GET event</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class GetEventThread extends Thread 
{
    /**
     * log
     */
	private static final Logger LOG = LoggerFactory.getLogger(GetEventThread.class);
	
	/**
	 * 发生断连后，500ms再发起请求
	 * after the disconnection, make a request again after 500ms.
	 */
	private static final long CONNECTED_FAILED_SLEEP = 500l;
	
	/**
	 * 登录失败，则间隔1分钟后，重新登录
	 * When login failed, after 1 minuters later, do login again.
	 */
	private static final long LOGIN_FAILED_SLEEP = 60000l;
	
	
	/**
     * 无事件暂停500ms
     * if there is no event, sleep 500ms
     */
    private static final long NO_EVENT_SLEEP = 500l;
	
	/**
	 *  最大断连次数
	 *  The max disconnection times
	 */
	private static final int NETWORK_ERROR_MAX_COUNT = 20;
	
	
	private static GetEventThread instance = null;
    
    private boolean isAlive = true;
    
    private String workNo;
    
    private String eventUrl;
    
    /**
     * 连续断连次数
     * The continuous disconnection times
     */
    private int netWorkErrorCount = 0;
    
    private AgentService agentService = new AgentService();
    
    /**
     * 观察者是否已经登录
     * Whether the monitor is login
     */
    private boolean isLogin = false;
 
	private GetEventThread() 
	{
	}	
	

	private void init()
	{  
        this.workNo = ConfigProperties.getKey(ConfigList.BASIC, "MONITOR_WORKNO");
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(GlobalObject.getAgentServerUrl());
        urlSb.append("agentevent/");
        urlSb.append(this.workNo);
        this.eventUrl = urlSb.toString();
	}
	
	@Override
    public void run()
    {  
	    init();
	    
	    while (isAlive)
	    {
	        try
            {
                if (!isLogin)
                {
                    /**
                     * 未登录(Not login)
                     */
                    doLogin();
                    continue;
                }
                
                doGetEvent();
            }
            catch (Exception e)
            {
                LOG.error("Unkown exception. The error is \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            }
	    }
    }    
	
	/**
	 * 获取事件
	 * Get the event
	 */
	@SuppressWarnings("unchecked")
    private void doGetEvent()
	{
        Map<String, Object> result = Request.get(workNo, eventUrl);
        String retcode = String.valueOf(result.get("retcode"));
        switch(retcode)
        {
            case CommonConstant.SUCCESS:
                netWorkErrorCount = 0;
                
                //统计第三方观察事件
                countEvent((Map<String, Object>)result.get("event"));
                doEventHandle((Map<String, Object>)result.get("event"));
                break;
                
            case CommonConstant.NOT_AUTH:
            case CommonConstant.NOT_LOGIN:
                /**
                 * 没有权限或者未登录
                 * No right or no login.
                 */
                netWorkErrorCount = 0;
                isLogin = false;
                GlobalObject.setLoginSucess(isLogin);
                break;
                
            case CommonConstant.NETWORKERROR:
                /**
                 * 发送请求失败
                 * Send Request Failed
                 */
                netWorkErrorCount++;
                if (netWorkErrorCount <= NETWORK_ERROR_MAX_COUNT)
                {
                    LOG.error("Do get event failed. because the network error");
                    pause(CONNECTED_FAILED_SLEEP);
                }
                else
                {
                    /**
                     * 超过最大连续失败次数，则重新登录
                     * More than the maximum number about connection failed, do log in again
                     */
                    isLogin = false;
                    GlobalObject.setLoginSucess(isLogin);
                }
                break;
               
            default:
                netWorkErrorCount = 0;
                isLogin = false;
                GlobalObject.setLoginSucess(isLogin);
                LOG.error("Do get event failed. the error is {}", 
                        JsonUtils.beanToJson(result));
                break;
        }
	}
	
	/**
	 * 统计第三方观察事件
	 * @param map
	 */
	@SuppressWarnings("unchecked")
    private void countEvent(Map<String, Object> event) {
	    if (null == event)
	    {
	        return;
	    }
	    String eventType = String.valueOf(event.get("eventType"));
	    if (CommonConstant.MONITOR_EVENT_TYPE.equals(eventType))
	    {
	        Map<String, Object> contentMap = (Map<String, Object>) event.get("content");
	        if (null == contentMap)
	        {
	            return;
	        }
	        String subtype = (String) contentMap.get("subtype");
	        EventCount eventCount = GlobalObject.getEventCount();
	        switch (subtype) {
            case "QC_AgentState_Busy":
                Integer qc_AgentState_Busy = eventCount.getQC_AgentState_Busy();
                eventCount.setQC_AgentState_Busy(qc_AgentState_Busy + 1);
                break;
            case "QC_AgentState_Mute":
                Integer qc_AgentState_Mute = eventCount.getQC_AgentState_Mute();
                eventCount.setQC_AgentState_Mute(qc_AgentState_Mute + 1);
                break;
            case "QC_AgentState_Monitor":
                Integer qc_AgentState_Monitor = eventCount.getQC_AgentState_Monitor();
                eventCount.setQC_AgentState_Monitor(qc_AgentState_Monitor + 1);
                break;
            case "QC_AgentState_RecordBegin":
                Integer QC_AgentState_RecordBegin = eventCount.getQC_AgentState_RecordBegin();
                eventCount.setQC_AgentState_RecordBegin(QC_AgentState_RecordBegin + 1);
                break;
            case "QC_AgentState_RecordStop":
                Integer QC_AgentState_RecordStop = GlobalObject.getEventCount().getQC_AgentState_RecordStop();
                eventCount.setQC_AgentState_RecordStop(QC_AgentState_RecordStop + 1);
                break;
            case "QC_AgentState_LiveVideoBegin":
                Integer QC_AgentState_LiveVideoBegin = eventCount.getQC_AgentState_LiveVideoBegin();
                eventCount.setQC_AgentState_LiveVideoBegin(QC_AgentState_LiveVideoBegin + 1);
                break;
            case "QC_AgentState_LiveVideoStop":
                Integer QC_AgentState_LiveVideoStop = eventCount.getQC_AgentState_LiveVideoStop();
                eventCount.setQC_AgentState_LiveVideoStop(QC_AgentState_LiveVideoStop + 1);
                break;
            case "QC_AgentState_SuperviseInsert":
                Integer QC_AgentState_SuperviseInsert = eventCount.getQC_AgentState_SuperviseInsert();
                eventCount.setQC_AgentState_SuperviseInsert(QC_AgentState_SuperviseInsert + 1);
                break;
            case "QC_AgentState_StopSuperviseInsert":
                Integer QC_AgentState_StopSuperviseInsert = eventCount.getQC_AgentState_StopSuperviseInsert();
                eventCount.setQC_AgentState_StopSuperviseInsert(QC_AgentState_StopSuperviseInsert + 1);
                break;
            case "QC_AgentState_StopMonitor":
                Integer QC_AgentState_StopMonitor = eventCount.getQC_AgentState_StopMonitor();
                eventCount.setQC_AgentState_StopMonitor(QC_AgentState_StopMonitor + 1);
                break;
            case "QC_AgentState_MuteVideo":
                Integer QC_AgentState_MuteVideo = eventCount.getQC_AgentState_MuteVideo();
                eventCount.setQC_AgentState_MuteVideo(QC_AgentState_MuteVideo + 1);
                break;
            case "QC_AgentState_MuteVideoAudio":
                Integer QC_AgentState_MuteVideoAudio = eventCount.getQC_AgentState_MuteVideoAudio();
                eventCount.setQC_AgentState_MuteVideoAudio(QC_AgentState_MuteVideoAudio + 1);
                break;
            case "QC_AgentState_PlayVoice":
                Integer QC_AgentState_PlayVoice = eventCount.getQC_AgentState_PlayVoice();
                eventCount.setQC_AgentState_PlayVoice(QC_AgentState_PlayVoice + 1);
                break;
            case "QC_AgentState_Active":
                Integer QC_AgentState_Active = eventCount.getQC_AgentState_Active();
                eventCount.setQC_AgentState_Active(QC_AgentState_Active + 1);
                break;
            case "QC_AgentState_WaitAnswer":
                Integer QC_AgentState_WaitAnswer = eventCount.getQC_AgentState_WaitAnswer();
                eventCount.setQC_AgentState_WaitAnswer(QC_AgentState_WaitAnswer + 1);
                break;
            case "QC_AgentState_WaitAlerting":
                Integer QC_AgentState_WaitAlerting = eventCount.getQC_AgentState_WaitAlerting();
                eventCount.setQC_AgentState_WaitAlerting(QC_AgentState_WaitAlerting + 1);
                break;
            case "QC_AgentState_ThreeParty":
                Integer QC_AgentState_ThreeParty = eventCount.getQC_AgentState_ThreeParty();
                eventCount.setQC_AgentState_ThreeParty(QC_AgentState_ThreeParty + 1);
                break;
            case "QC_AgentState_HungUp":
                Integer QC_AgentState_HungUp = eventCount.getQC_AgentState_HungUp();
                eventCount.setQC_AgentState_HungUp(QC_AgentState_HungUp+1);
                break;
            case "QC_AgentState_MonitorSupervisorOther":
                Integer QC_AgentState_MonitorSupervisorOther = eventCount.getQC_AgentState_MonitorSupervisorOther();
                eventCount.setQC_AgentState_MonitorSupervisorOther(QC_AgentState_MonitorSupervisorOther + 1);
                break;
            case "QC_AgentState_MonitorInsertCall":
                Integer QC_AgentState_MonitorInsertCall = eventCount.getQC_AgentState_MonitorInsertCall();
                eventCount.setQC_AgentState_MonitorInsertCall(QC_AgentState_MonitorInsertCall + 1);
                break;
            case "QC_AgentState_Idle":
                Integer QC_AgentState_Idle = eventCount.getQC_AgentState_Idle();
                eventCount.setQC_AgentState_Idle(QC_AgentState_Idle + 1);
                break;
            case "QC_AgentState_Work":
                Integer QC_AgentState_Work = eventCount.getQC_AgentState_Work();
                eventCount.setQC_AgentState_Work(QC_AgentState_Work + 1);
                break;
            case "QC_AgentState_Rest":
                Integer QC_AgentState_Rest = eventCount.getQC_AgentState_Rest();
                eventCount.setQC_AgentState_Rest(QC_AgentState_Rest + 1);
                break;
            case "QC_AgentState_Login":
                Integer QC_AgentState_Login = eventCount.getQC_AgentState_Login();
                eventCount.setQC_AgentState_Login(QC_AgentState_Login + 1);
                break;
            case "QC_AgentState_Logout":
                Integer QC_AgentState_Logout = eventCount.getQC_AgentState_Logout();
                eventCount.setQC_AgentState_Logout(QC_AgentState_Logout + 1);
                break;
            default:
                break;
            }
	        
	    }
        
    }


    /**
	 * 进行登录(do login)
	 */
	private void doLogin()
	{
	    if (agentService.doLogin() 
	            && agentService.startMonitorAgents())
	    {
	        //login success
	        isLogin = true;
	        GlobalObject.setLoginSucess(isLogin);
	        LOG.info("The monitor success.");
	    }
	    else
	    {
	        isLogin = false;
            GlobalObject.setLoginSucess(isLogin);
	        pause(LOGIN_FAILED_SLEEP);
	    }
	}
    

	

/**
 * put event object to event queue
 * @param event
 */
@SuppressWarnings("unchecked")
private void doEventHandle(Map<String, Object> event)
{
    if (null == event)
    {
        pause(NO_EVENT_SLEEP);
        return;
    }
    String eventType = String.valueOf(event.get("eventType"));
    if (CommonConstant.MONITOR_EVENT_TYPE.equals(eventType))
    {
        Map<String, Object> contentMap = (Map<String, Object>) event.get("content");
        if (null == contentMap)
        {
            return;
        }
        String monitoredAgent = (String) contentMap.get("monitoredagent");
        if (monitoredAgent == null)
        {
            return;
        }
        
        if (null == GlobalObject.getJSessionIdByMonitoredAgent(monitoredAgent))
        {
            //没有被监视(The agent is not monitored)
            return;
            
        }
        
        Queue<Map<String, Object>> queue = GlobalObject.getEventFromMonitoredAgent(monitoredAgent);
        
        if (null != queue)
        {
            queue.add(contentMap);
        }
        
    }
}
    
    /**
     * pause get event thread
     */
    private void pause(long sleep)
    {
    	try
        {
            Thread.sleep(sleep);
        }
        catch (InterruptedException e)
        {
            LOG.error(LogUtils.encodeForLog(e.getMessage()));
        }
    }
    
    /**
     * start get event thread
     */
    public static void begin()
    {  
        if (null == instance)
        {
            instance = new GetEventThread();
            instance.setName("GetEventThread");
            instance.start();
        }

    }
    
    /**
     * stop get event thread
     */
    public static void end()
    {
        if (null != instance)
        {
            instance.isAlive = false;
        }
    }
}
    