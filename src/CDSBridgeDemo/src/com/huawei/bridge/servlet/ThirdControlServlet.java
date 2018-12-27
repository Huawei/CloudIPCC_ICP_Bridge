package com.huawei.bridge.servlet;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.bean.AttendeeInfo;
import com.huawei.bridge.bean.MixGroupUserInfo;
import com.huawei.bridge.common.global.ServletErrorCode;
import com.huawei.bridge.common.util.JsonUtils;
import com.huawei.bridge.common.util.StringUtils;
import com.huawei.bridge.service.ThirdControlService;


public class ThirdControlServlet extends CommonServlet 
{
    
    private static final long serialVersionUID = -1899743541236765226L;
    
    private static final Logger LOG = LoggerFactory.getLogger(ThirdControlServlet.class); 
    
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("[0-9]{1,32}");
    
    private static final Pattern USERTYPE_PATTERN = Pattern.compile("[1|2]");
    
    private static final int STRING_MAX_LENGTH = 32;
    
    /**
     * 最大会议成员数
     * The max number of conference member
     */
    private static final int MAX_CONFERENCE_MEMBER = 23;
    
    /**
     * 最大混合群组成员数
     * The max number of mixgroup
     */
    private static final int MAX_MIXGROUP_MEMBER = 89;
    
    /**
     * 最大无线群组数
     * The max  number of wirelessgroup
     */
    private static final int MAX_WIRELESS_MEMBER = 10;
    
    
	public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
	{
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    String operType = request.getParameter("operType");
	    String monitoredAgent = request.getParameter("monitoredAgent");
	    if (StringUtils.isNullOrBlank(operType) || StringUtils.isNullOrBlank(monitoredAgent))
	    {
	        LOG.error("operType or monitoredAgent is empty");
	        String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "operType or monitoredAgent is empty");
	        writeResponse(response, message);
	        return;
	    }
	    
	    switch (operType)
        {
            case "1":
                doCall(monitoredAgent, request, response);
                break;
            case "2":
                doConference(monitoredAgent, request, response);
                break;
            case "3":
                doMixGroupCall(monitoredAgent, request, response);
                break;
            /*case "4":
                doSendSMS(monitoredAgent, request, response);
                break;*/
            default:
                LOG.error("operType is invalid");
                String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "operType is invalid");
                writeResponse(response, message);
                break;
        }
	    
	}
	
	/**
	 * 指示调度席发起呼叫
	 * Ask the monitored agent to make a call
	 * @param monitoredAgent
	 * @param request
	 * @param response
	 */
	private void doCall(String monitoredAgent, HttpServletRequest request, HttpServletResponse response)
	{
	    String calleeNumber = request.getParameter("calleeNumber");
	    if (StringUtils.isNullOrBlank(calleeNumber)
	            || !isPhoneNumber(calleeNumber))
	    {
	        LOG.error("calleeNumber is empty or not number");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "calleeNumber is empty or not number");
            writeResponse(response, message);
            return;
	    }
	    
	    String isVideoStr = request.getParameter("isVideo");
	    if (null == isVideoStr || !("true".equals(isVideoStr) || "false".equals(isVideoStr)))
	    {
	        LOG.error("isVideoStr is invalid");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "isVideo is invalid");
            writeResponse(response, message);
            return;
	    }
	    boolean isVideo = Boolean.valueOf(isVideoStr);
	    
	    ThirdControlService thirdControlService = new ThirdControlService(monitoredAgent);
	    Map<String, Object> result = thirdControlService.call(calleeNumber, isVideo);
	    writeResponse(response, JsonUtils.beanToJson(result));
	}
	
	/**
	 * 指示调度席发起会议
	 * Ask the monitored agent to do conference call
	 * @param monitoredAgent
	 * @param request
	 * @param response
	 */
	private void doConference(String monitoredAgent, HttpServletRequest request, HttpServletResponse response)
	{
	    String isVideoStr = request.getParameter("isVideo");
	    if (null == isVideoStr || !("true".equals(isVideoStr) || "false".equals(isVideoStr)))
        {
            LOG.error("isVideo is invalid");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "isVideo is invalid");
            writeResponse(response, message);
            return;
        }
        boolean isVideo = Boolean.valueOf(isVideoStr);
        
	    String isRecordStr = request.getParameter("isRecord");
	    if (null == isRecordStr || !("true".equals(isRecordStr) || "false".equals(isRecordStr)))
        {
            LOG.error("isRecordStr is invalid");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "isRecord is invalid");
            writeResponse(response, message);
            return;
        }
	    boolean isRecord = Boolean.valueOf(isRecordStr);
	    
	    String number = request.getParameter("number");
	    String name = request.getParameter("name");
	    String isMute = request.getParameter("isMute");

	    if (StringUtils.isNullOrBlank(number) || StringUtils.isNullOrBlank(name) || StringUtils.isNullOrBlank(isMute) )
        {
            LOG.error("number or name or isMute  is empty");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "number  is empty");
            writeResponse(response, message);
            return;
        }
	    String []numberArray = number.split(";");
	    String []nameArray = name.split(";");
	    String []isMuteArray = isMute.split(";");
	    
	    if (numberArray.length  != isMuteArray.length) 
	    {
	        LOG.error("number is empty");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "number is empty");
            writeResponse(response, message);
            return;
        }
	    
	    if (numberArray.length > MAX_CONFERENCE_MEMBER)
	    {
	        LOG.error("the number is over 23");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "the number is over 23");
            writeResponse(response, message);
            return;
	    }
	    
	    List<AttendeeInfo> list = new ArrayList<AttendeeInfo>();
	    AttendeeInfo attendeeInfo;
	    
	    for (int i = 0; i < nameArray.length; i++) 
	    {
	        if (nameArray[i].length() > STRING_MAX_LENGTH)
            {
                LOG.error("The length name is over 32");
                String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "The length name is over 32");
                writeResponse(response, message);
                return;
            }
        }
	    
	    for (int i = 0; i < numberArray.length; i++)
	    {
	        attendeeInfo = new AttendeeInfo();
	        if (!isPhoneNumber(numberArray[i]))
	        {
	            LOG.error("The number is not phone number");
	            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "The number is not phone number");
	            writeResponse(response, message);
	            return;
	        }
	        
	        attendeeInfo.setNumber(numberArray[i]);
	        
	        if (nameArray.length>0 && i <= (nameArray.length-1)) 
	        {
	            attendeeInfo.setName(nameArray[i]);
            }
	        attendeeInfo.setMute(Boolean.valueOf(isMuteArray[i]));
	        list.add(attendeeInfo);
	    }
	    
	    ThirdControlService thirdControlService = new ThirdControlService(monitoredAgent);
        Map<String, Object> result = thirdControlService.conference(isVideo, isRecord, list);
        writeResponse(response, JsonUtils.beanToJson(result));
	}
	
	private static boolean isPhoneNumber(String phoneNumber)
	{
	    if (PHONENUMBER_PATTERN.matcher(phoneNumber).matches())
	    {
	        return true;
	    }
	    return false;
	}
	
	private static boolean isUserType(String userType)
	{
	    if (USERTYPE_PATTERN.matcher(userType).matches())
	    {
	        return true;
	    }
	    return false;
	}
	
	/**
     * 指示调度席发起混合群组呼叫
     * Ask the monitored agent to do mixgroup call
     * @param monitoredAgent
     * @param request
     * @param response
     */
	private void doMixGroupCall(String monitoredAgent, HttpServletRequest request, HttpServletResponse response)
	{
	    String groupAlias = request.getParameter("groupAlias");
	    if (null == groupAlias)
	    {
	        groupAlias = "";
	    }
	    if (groupAlias.length() > STRING_MAX_LENGTH)
	    {
	        LOG.error("The length of groupAlias is over 32");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "groupAlias is over 32");
            writeResponse(response, message);
            return;
	    }
	    
        String number = request.getParameter("number");
        String userType = request.getParameter("userType");
        if (StringUtils.isNullOrBlank(number) || StringUtils.isNullOrBlank(number.replace(";","")) || StringUtils.isNullOrBlank(userType))
        {
            LOG.error("number or userType is empty");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "number or userType is empty");
            writeResponse(response, message);
            return;
        }
        String []numberArray = number.split(";");
        String []userTypeArray = userType.split(";");
        
        if (numberArray.length != userTypeArray.length)
        {
            LOG.error("number is empty");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "number or userType is empty");
            writeResponse(response, message);
            return;
        }
        
        if (numberArray.length != userTypeArray.length
                || numberArray.length > MAX_MIXGROUP_MEMBER)
        {
            LOG.error("member is over 89");
            String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "member is over 89");
            writeResponse(response, message);
            return;
        }
        List<MixGroupUserInfo> list = new ArrayList<MixGroupUserInfo>();
        MixGroupUserInfo mixGroupUserInfo;
        int countWirelessUser = 0;
        for (int i = 0; i < numberArray.length; i++)
        {
            mixGroupUserInfo = new MixGroupUserInfo();
            if (!isPhoneNumber(numberArray[i]) || !isUserType(userTypeArray[i]) )
            {
                LOG.error("number or userType is invalid");
                String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "number or userType is invalid");
                writeResponse(response, message);
                return;
            }
            mixGroupUserInfo.setNumber(numberArray[i]);
            mixGroupUserInfo.setUserType(Integer.valueOf(userTypeArray[i]));
            if (mixGroupUserInfo.getUserType() == 1)
            {
                countWirelessUser++;
                if (countWirelessUser > MAX_WIRELESS_MEMBER)
                {
                    LOG.error("It is over 10 wireless group");
                    String message = makeResponseString(ServletErrorCode.PARAM_INVALID, "It is over 10 wireless group");
                    writeResponse(response, message);
                    return;
                }
            }
            list.add(mixGroupUserInfo);
        }
        
        ThirdControlService thirdControlService = new ThirdControlService(monitoredAgent);
        Map<String, Object> result = thirdControlService.mixGroupCall(groupAlias, list);
        writeResponse(response, JsonUtils.beanToJson(result));
	}
	
	
	/**
	 * 指示调度席发送短信
	 * Ask the monitored to start send SMS
	 * @param monitoredAgent
	 * @param request
	 * @param response
	 *//*
	private void doSendSMS(String monitoredAgent, HttpServletRequest request, HttpServletResponse response)
	{
	    String userListStr = request.getParameter("userList");
	    String []userArray = userListStr.split(";");
        String smsContent = request.getParameter("smsContent");
        ThirdControlService thirdControlService = new ThirdControlService(monitoredAgent);
        Map<String, Object> result = thirdControlService.sendSMS(Arrays.asList(userArray), smsContent);
        writeResponse(response, JsonUtils.beanToJson(result));
	}*/


}
