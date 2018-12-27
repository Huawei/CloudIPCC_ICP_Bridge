// language string definition file
// Copyright Huawei Technologies Co., Ltd. 2016. All rights reserved.
/*
 * this file defines the language strings of diference language , 
 * the LangugePageClass has a public interface which returns the language string define page of specified language for self attributed element. 
 *
 * Notice:
 * To Add a new language you need to as following
 * 1. In the LANGUGE_SUPPORT object , add a new member like LANGUAGE_SUPPORT_XXX:"XXX", NOTE That the javascript object members sperated by ","
 * 2. Copy the Language.English object definition, changes to Languge.XXX . Modify the member value to the string of specified string for language XXX
 * 3. In the GetLanguagePage method of LanguagePageClass, add an else if branch which enables the function to return the languagepage of specified language xxx.
 * 4. Done.
 */
//language supported  
LANGUAGE_SUPPORT = {
	LANGUAGE_SUPPORT_CHINESE:"Chinese",
	LANGUAGE_SUPPORT_ENGLISH:"English"
}

//LanguagePageClass
function LanguagePageClass()
{
	// function
	// desc: the function reture the language string define page for specified language
	// params：
	// [IN] language : string of language
	// return： void
	this.GetLanguagePage = function(language){
		if (language === LANGUAGE_SUPPORT.LANGUAGE_SUPPORT_CHINESE){
			return LanguageString.Chinese;
		}
		else if (language === LANGUAGE_SUPPORT.LANGUAGE_SUPPORT_ENGLISH){
			return LanguageString.English;
		}
		else{
			return LanguageString.English;
		}
	}
}

var LanguageString = {};
LanguageString.Chinese = {
		I18N_MONITORED_WORKNO:"被监视座席工号:",
		I18N_START_MONIOTR:"开始监视",
		I18N_SAYBUSY:"示忙",
		I18N_SAYIDLE:"置闲",
		I18N_AnswerCall:"应答呼叫",
		I18N_AGENTAPPDEMO_LOG:"日志",
		I18N_LOG_CLEAR:"清理日志",
		I18N_STATUS_CONTROL:"状态控制",
		I18N_CALL_CONTROL:"外呼控制",
		I18N_ANSWER_CALL_CONTROL:"呼叫控制",
		I18N_CALL_CONTROL_CALLED:"被叫号码:",
		I18N_CALL_CONTROL_CALLER:"主叫号码:",
		I18N_CALL_CONTROL_CALLAPPDATA:"随路数据信息:",
		I18N_CALL_CONTROL_SKILLID:"技能队列:",
		I18N_MEDIAABILITY:"媒体能力:",
		I18N_MEDIAABILITY_VOICE:"音频",
		I18N_MEDIAABILITY_VIDEO:"视频",
		I18N_MEDIAABILITY_DEFAULT:"默认",
		I18N_Callout:"外呼",
		I18N_MEDIAABILITY_EVENT_STATICS:"第三方观察事件统计"
}

LanguageString.English = {
		I18N_MONITORED_WORKNO:"The Monitored Agent:",
		I18N_START_MONIOTR:"Start Monitor",
		I18N_SAYBUSY:"SayBusy",
		I18N_SAYIDLE:"SayIdle",
		I18N_AnswerCall:"AnswerCall",
		I18N_Callout:"Callout",
		I18N_AGENTAPPDEMO_LOG:"LOG",
		I18N_LOG_CLEAR:"clearLog",
		I18N_STATUS_CONTROL:"Status Control",
		I18N_CALL_CONTROL:"Callout Control",
		I18N_ANSWER_CALL_CONTROL:"Answer Call Control",
		I18N_CALL_CONTROL_CALLED:"Called:",
		I18N_CALL_CONTROL_CALLER:"Caller:",
		I18N_CALL_CONTROL_CALLAPPDATA:"CallAppData:",
		I18N_CALL_CONTROL_SKILLID:"SkillId:",
		I18N_MEDIAABILITY:"MediaAbility:",
		I18N_MEDIAABILITY_VOICE:"Voice",
		I18N_MEDIAABILITY_VIDEO:"Video",
		I18N_MEDIAABILITY_DEFAULT:"Default",
		I18N_Callout:"Callout",
		I18N_MEDIAABILITY_EVENT_STATICS:"Third-party observation event statistics"
}