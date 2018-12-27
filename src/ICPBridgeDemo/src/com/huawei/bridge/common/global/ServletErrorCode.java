
package com.huawei.bridge.common.global;

public interface ServletErrorCode
{
    String SUCCESS = "0";
    /**
     * 参数非法
     */
    String PARAM_INVALID = "100-000-001";
    
    /**
     * 被监视的座席不存在
     */
    String MONITORED_AGENT_NOT_EXIST = "100-000-002";
    
    /**
     * 没有座席被监视(No agent is monitored)
     */
    String NO_MONITORED = "10-000-003";
    
    
    /**
     * 已经被其他人监视(Has monitored by other user)
     */
    String HAS_MONITORED_BY_OTHER = "10-000-004";
    
    String NO_EVENT_QUEUE = "10-000-005";
}
