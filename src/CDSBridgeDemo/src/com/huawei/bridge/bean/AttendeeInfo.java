
package com.huawei.bridge.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AttendeeInfo
{
    private String number;
    
    private String name;
    
    @JsonProperty
    private boolean isMute;

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @JsonIgnore
    public boolean isMute()
    {
        return isMute;
    }

    public void setMute(boolean isMute)
    {
        this.isMute = isMute;
    }
    
    
}
