
package com.huawei.bridge.bean;

public class CalloutParam
{
    private String caller;
    
    private String called;
    
    private int skillid;
    
    private String callappdata;
    
    private int mediaability;

    public String getCaller()
    {
        return caller;
    }

    public void setCaller(String caller)
    {
        this.caller = caller;
    }

    public String getCalled()
    {
        return called;
    }

    public void setCalled(String called)
    {
        this.called = called;
    }

    public int getSkillid()
    {
        return skillid;
    }

    public void setSkillid(int skillid)
    {
        this.skillid = skillid;
    }

    public String getCallappdata()
    {
        return callappdata;
    }

    public void setCallappdata(String callappdata)
    {
        this.callappdata = callappdata;
    }

    public int getMediaability()
    {
        return mediaability;
    }

    public void setMediaability(int mediaability)
    {
        this.mediaability = mediaability;
    }
    
}
