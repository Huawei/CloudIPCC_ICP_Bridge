package com.huawei.bridge.service;




import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.util.LogUtils;



/**
 * 
 * <p>Title: The third monitor auto log into CDS</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AutoLoginThread extends Thread 
{
    /**
     * log
     */
	private static final Logger LOG = LoggerFactory.getLogger(AutoLoginThread.class);
	
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
	 *  最大断连次数
	 *  The max disconnection times
	 */
	private static final int NETWORK_ERROR_MAX_COUNT = 20;
	
	/**
	 * The interval of update token
	 */
	private static final long TOKEN_UPDATE_INTERVAL = 300000l;
	
	private static AutoLoginThread instance = null;
    
    private boolean isAlive = true;
    
    /**
     * 连续断连次数
     * The continuous disconnection times
     */
    private int netWorkErrorCount = 0;
    
    private CDSService cdsService = new CDSService();
    
   
    
    /**
     * 观察者是否已经登录
     * Whether the monitor is login
     */
    private boolean isLogin = false;
 
	private AutoLoginThread() 
	{
	}	
	

	
	@Override
    public void run()
    {  
	  
	    
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
                else
                {
                    updateToken();
                }
            }
            catch (Exception e)
            {
                LOG.error("Unkown exception. The error is \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            }
	    }
    }    
	

    /**
	 * 进行登录(do login)
	 */
	private void doLogin()
	{
	    if (cdsService.doLogin())
	    {
	        //login success
	        LOG.info("The monitor success.");
	        isLogin = true;
	        GlobalObject.setLoginSucess(isLogin);
	        netWorkErrorCount = 0;
	        pause(TOKEN_UPDATE_INTERVAL);
	    }
	    else
	    {
	        isLogin = false;
            GlobalObject.setLoginSucess(isLogin);
	        pause(LOGIN_FAILED_SLEEP);
	    }
	}
    
	/**
	 * 更新Token(Update token)
	 */
	private void updateToken()
	{
	    Map<String, Object> result = cdsService.updateToken();
	    if (CommonConstant.SUCCESS.equals(result.get("returnCode")))
	    {
	        pause(TOKEN_UPDATE_INTERVAL);
	    }
	    else if ("NETWORKERROR".equals(result.get("returnCode")))
	    {
	        //Network has error
	        netWorkErrorCount++;
	        if (netWorkErrorCount > NETWORK_ERROR_MAX_COUNT)
	        {
	            isLogin = false;
	            GlobalObject.setLoginSucess(isLogin);
	            return;
	        }
	        pause(CONNECTED_FAILED_SLEEP);
	    }
	    else
	    {
	        //Update Auth failed
	        isLogin = false;
            GlobalObject.setLoginSucess(isLogin);
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
            instance = new AutoLoginThread();
            instance.setName("AutoLoginThread");
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
    