
LANGUAGE_SUPPORT = {
	LANGUAGE_SUPPORT_CHINESE:"Chinese",
	LANGUAGE_SUPPORT_ENGLISH:"English"
}

//LanguagePageClass
function LanguagePageClass()
{
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
		I18N_MONITORED_NUMBER:"第三方控制的调度席账号:",
		I18N_CALLOUT_CONTROL:"外呼控制",
		I18N_CALLEENUMBER:"被叫号码:",
		I18N_ISVIDEO:"是否视频:",
		I18N_CALL_BUTTON:"发起呼叫",
		I18N_CONFERENCE_CONTROL:"会议控制",
		I18N_ISRECORD:"是否录制:",
		I18N_MEMBER_LIST:"成员列表",
		I18N_ADD_MEMBER:"新增成员",
		I18N_MEMBER_NUMBER:"用户号码",
		I18N_MEMBER_NAME:"用户名称",
		I18N_MEMBER_ISMUTE:"是否静音",
		I18N_CONFERENCE_BUTTON:"发起会议",
		I18N_MIXGROUP_CONTROL:"混合群组呼叫控制",
		I18N_MIXGROUPCALL_ALIAS:"混合群组名称:",
		I18N_MEMBER_USERTYPE:"用户类型:",
		I18N_MIXGROUP_BUTTON:"发起混合群组呼叫",
		I18N_SMS_CONTROL:"短信控制",
		I18N_SMS_USERLIST:"用户列表:",
		I18N_SMS_CONTENT:"短信内容:",
		I18N_SMS_BUTTON:"发送短信",
		I18N_LOG:"日志",
		I18N_LOG_CLEAR:"清理日志",
		I18N_MEMBER_OPERATE:"操作",
		I18N_MEMBER_DELETE:"删除",
		I18N_SMS_USERLIST_TIP:"多个用户之间以;进行分隔",
		I18N_USERTYPE_WIRELESS:"无线群组",
		I18N_USERTYPE_OTHER:"其他"
}

LanguageString.English = {
		I18N_MONITORED_NUMBER:"Third-party control of the dispatch agent:",
		I18N_CALLOUT_CONTROL:"Callout Control",
		I18N_CALLEENUMBER:"Called Number:",
		I18N_ISVIDEO:"IsVideo:",
		I18N_CALL_BUTTON:"Start Callout",
		I18N_CONFERENCE_CONTROL:"Conference Control",
		I18N_ISRECORD:"IsRecord:",
		I18N_MEMBER_LIST:"Member List",
		I18N_ADD_MEMBER:"AddMember",
		I18N_MEMBER_NUMBER:"Number",
		I18N_MEMBER_NAME:"Name",
		I18N_MEMBER_ISMUTE:"IsMute",
		I18N_CONFERENCE_BUTTON:"Start Conference",
		I18N_MIXGROUP_CONTROL:"MixGroup Call Control",
		I18N_MIXGROUPCALL_ALIAS:"The Alias Name of MixGroup:",
		I18N_MEMBER_USERTYPE:"UserType",
		I18N_MIXGROUP_BUTTON:"Start MixGroup",
		I18N_SMS_CONTROL:"SMS Control",
		I18N_SMS_USERLIST:"UserList:",
		I18N_SMS_CONTENT:"SmsContent:",
		I18N_SMS_BUTTON:"Send SMS",
		I18N_LOG:"Log",
		I18N_LOG_CLEAR:"Clear Log",
		I18N_MEMBER_OPERATE:"Operate",
		I18N_MEMBER_DELETE:"Delete",
		I18N_SMS_USERLIST_TIP:"When more than one user, use ';' to separate",
		I18N_USERTYPE_WIRELESS:"Wireless Group",
		I18N_USERTYPE_OTHER:"Other"
}