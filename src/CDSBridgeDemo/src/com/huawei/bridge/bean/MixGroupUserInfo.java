package com.huawei.bridge.bean;

/**
 * 混合群组成员信息结构
 * 
 */
public class MixGroupUserInfo 
{
    
    /**
     * 用户号码，取数字，长度为1~32。
     * 
     */
    private String number;
    
    /**
     * 用户类型，取值类型：1 无线群组2 调度或其他用户
     */
    private int userType;

    public String getNumber() 
    {
        return number;
    }

    public void setNumber(String number) 
    {
        this.number = number;
    }

    public int getUserType()
    {
        return userType;
    }

    public void setUserType(int userType) 
    {
        this.userType = userType;
    }

    
    
}
