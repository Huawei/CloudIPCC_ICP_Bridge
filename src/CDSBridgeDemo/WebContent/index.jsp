<!DOCTYPE html>
<%@page import="java.util.Locale"%>
<html>
<head>
<meta charset="UTF-8">
<title>CDSBridgeDemo</title>
<script type="text/javascript" src="page/js/i18n/i18n.js"></script>
<script type="text/javascript" src="page/js/i18n/language.js"></script>
<script type="text/javascript" src="page/openjs/jquery-1.6.2.js"></script>
<script type="text/javascript" src="page/openjs/json.js"></script>
<script type="text/javascript" src="page/js/globalVariable.js"></script>
<script type="text/javascript" src="page/js/operation.js"></script>
<link href="page/style/table.css" rel="stylesheet" type="text/css" />
<%
String language = request.getHeader("Accept-Language");
if (null == language)
{
    language = "en";
}
else
{
    language = language.toLowerCase(Locale.ENGLISH);
}
%>
<script type="text/javascript">  
      window.onload=function()
      { 
          var LanguagePage = new LanguagePageClass();
          var I18N = new I18NClass();
          var browerLanguage = "<%=language%>";
          if (browerLanguage.indexOf('zh') >= 0)
          {
        	  global_language = LanguagePage.GetLanguagePage(LANGUAGE_SUPPORT.LANGUAGE_SUPPORT_CHINESE);
              I18N.SwitchI18N(global_language);
          }
          else
          {
        	  global_language = LanguagePage.GetLanguagePage(LANGUAGE_SUPPORT.LANGUAGE_SUPPORT_ENGLISH);
              I18N.SwitchI18N(global_language);
          }
         
      }  
</script>
</head>
<body>
	<div style="float: left; width: 67%">
		<table width="100%" border="0" cellpadding="0" cellspacing="3">
			<tr>
				<td><span self="I18N_MONITORED_NUMBER">The Monitored Agent:</span>
					 <input type="text" id="monitoredAgent" maxlength="32" /></td>
			</tr>
		</table>

		<hr />
		<!-- 指示发起呼叫 -->
		<span self="I18N_CALLOUT_CONTROL">Callout Control</span>
		<table width="100%" border="0" cellpadding="0" cellspacing="3">
			<tr>
				<td><span self="I18N_CALLEENUMBER">CalledNumber:</span> 
					<input type="text" id="Call_calleeNumber" maxlength="24"></td>
			</tr>
			<tr>
				<td><span self="I18N_ISVIDEO">IsVideo:</span>
					 <select id="Call_isVideo">
						<option value="true" >true</option>
						<option value="false">false</option>
					</select>
				</td>
			</tr>
			<tr>
				<td><input type="button" value="Start Callout" id="startcall" self="I18N_CALL_BUTTON" onclick="doCall()">
				</td>
			</tr>
		</table>
		<hr />

		<!-- 指示发起会议 -->
		<span self="I18N_CONFERENCE_CONTROL">Conference Control</span>
		<br/>
		<span self="I18N_ISVIDEO">IsVideo:</span> 
		<select id="Conference_isVideo">
			<option value="false">false</option>
			<option value="true">true</option>
		</select> 
		<br/>
		<span self="I18N_ISRECORD">IsRecord:</span> 
		<select id="Conference_isRecord">
			<option value="false">false</option>
			<option value="true">true</option>
		</select>
		<br/>
		<span self="I18N_MEMBER_LIST">Member List</span> <input type="button" self="I18N_ADD_MEMBER" value="AddMember" onclick="addConfMember()"/>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="content_form_table" id="ConfMemberTable">
			<thead>
				<tr>
					<td><span self="I18N_MEMBER_NUMBER">Number</span></td>
					<td><span self="I18N_MEMBER_NAME">Name</span></td>
					<td><span self="I18N_MEMBER_ISMUTE">IsMute</span></td>
					<td><span self="I18N_MEMBER_OPERATE">Operate</span></td>
				<tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
		<input type="button" value="Start Conference" self="I18N_CONFERENCE_BUTTON" onclick="doConferenceCall()">
		
		<hr />

		<!-- 指示发起混合呼叫 -->
		<span self="I18N_MIXGROUP_CONTROL">MixGroupCall Control</span>
		<br/>
		<span self="I18N_MIXGROUPCALL_ALIAS">GroupAlias:</span><input id="groupAlias" type="text" value="" maxlength="32"/>
		<br/>
		<span self="I18N_MEMBER_LIST">Member List</span><input type="button" value="AddMember" self="I18N_ADD_MEMBER"  onclick="addMixGroupMember()"/>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="content_form_table" id="MixGroupMemberTable">
			<thead>
				<tr>
					<td><span self="I18N_MEMBER_NUMBER">Number</span></td>
					<td><span self="I18N_MEMBER_USERTYPE">UserType</span></td>
					<td><span self="I18N_MEMBER_OPERATE">Operate</span></td>
				<tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
		<input type="button" value="Start MixGroup" self="I18N_MIXGROUP_BUTTON"  onclick="doMixGroupCall()">
		
		<hr />
		<!-- 当前不支持指示调度座席发送短信功能  日期：2018/6/21   -->
		<!-- 
		<span self="I18N_SMS_CONTROL">SMS Control</span>
		<br/>
		<span self="I18N_SMS_USERLIST">UserList:</span><input type="text" maxlength="3300" id="SMS_UserList"><span self="I18N_SMS_USERLIST_TIP"></span>
		<br/>
		<span self="I18N_SMS_CONTENT">SmsContent:</span><input type="text" maxlength="1024" id="SMS_Content">
		<input type="button" value="Send SMS" self="I18N_SMS_BUTTON" onclick="doSendSMS()">
		<br/> 
		-->
	</div>
	
	<div style="float: left; width: 33%">
		<p self="I18N_LOG">LOG</p>
		<p>
			<input type="button" value="Clear Log" ID="clearLog"
				self="I18N_LOG_CLEAR" onclick="clearLog()" />
		</p>
		<textarea id="LogInfo" readonly="readonly" rows="50"
			style="width: 100%"></textarea>
	</div>
</body>
</html>