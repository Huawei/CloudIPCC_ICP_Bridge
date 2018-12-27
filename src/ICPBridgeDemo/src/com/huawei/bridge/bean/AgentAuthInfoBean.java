
package com.huawei.bridge.bean;

public class AgentAuthInfoBean
{
    private String guid;
    
    private String cookie;
    
    public AgentAuthInfoBean(String guid, String cookie)
    {
        this.guid = guid;
        this.cookie = cookie;
    }

    public String getGuid()
    {
        return guid;
    }

    
    public String getCookie()
    {
        return cookie;
    }

}
