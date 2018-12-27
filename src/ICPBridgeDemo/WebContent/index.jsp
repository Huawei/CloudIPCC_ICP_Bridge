<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ICPBridgeDemo</title>
<script type="text/javascript" src="page/openjs/jquery-1.6.2.js"></script>
<script type="text/javascript" src="page/openjs/json.js"></script>
<script type="text/javascript" src="page/js/globalVariable.js"></script>
<script type="text/javascript" src="page/js/operation.js"></script>
<script type="text/javascript" src="page/js/i18n/demo.icpbridgedemo.lang.string.js"></script>
<script type="text/javascript" src="page/js/i18n/i18n.js"></script>
<script type="text/javascript">  
        window.onload=function()
        { 
            var LanguagePage = new LanguagePageClass();
            var I18N = new I18NClass();
            var browerLanguage =(navigator.language || navigator.browserLanguage).toLowerCase();
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
<%
request.getSession().invalidate();
%>
</head>
<body>
	<div style="float:left;width:67%">
		<table  width="100%" border="0" cellpadding="0" cellspacing="3">
			<tr>
                <td>
                    <span self="I18N_MONITORED_WORKNO">The Monitored Agent:</span>
                    <input type="text" id="monitoredWorkNo" maxlength="5"/>
                </td>
            </tr> 
            <tr>
            	<td>
            		<input type="button" value="Start Monitor" self="I18N_START_MONIOTR"  onclick="Operate_StartMonitor()">
            	</td>
            </tr>
		</table>
		
		<hr/>
		<span self="I18N_STATUS_CONTROL">Status Control</span>
		<table  width="100%" border="0" cellpadding="0" cellspacing="3">
            <tr>
            	<td>
            		<input type="button" value="SayBusy" self="I18N_SAYBUSY" onclick="Operate_StatusControl(1)">
            		<input type="button" value="SayIdle" self="I18N_SAYIDLE" onclick="Operate_StatusControl(0)">
            	</td>
            </tr>
		</table>
		
		<hr/>
		<span self="I18N_ANSWER_CALL_CONTROL">Answer Call Control</span>
		<table  width="100%" border="0" cellpadding="0" cellspacing="3">
            <tr>
            	<td>
            		<input type="button" value="" self="I18N_AnswerCall" onclick="Operate_AnswerCall()">
            	</td>
            </tr>
		</table>
		
		<hr/>
		<span self="I18N_CALL_CONTROL">Callout Control</span>
		<table  width="100%" border="0" cellpadding="0" cellspacing="3">
            <tr>
            	<td>
            	
            		<span self="I18N_CALL_CONTROL_CALLED">Called:</span><input type="text" id="Callout_Called" maxlength="24">
            		<span self="I18N_CALL_CONTROL_CALLER">Caller:</span><input type="text" id="Callout_Caller"   maxlength="24">
            		<span self="I18N_CALL_CONTROL_CALLAPPDATA">CallAppData:</span><input type="text" id="Callout_CallAppData"  maxlength="512">
            		<span self="I18N_CALL_CONTROL_SKILLID">SkillId:</span><input type="text" id="Callout_SkillId" maxlength="5">
            		<span self="I18N_MEDIAABILITY">MediaAbility:</span>
            		<select id="Callout_MediaAbility">
            			<option value="0" self="I18N_MEDIAABILITY_VOICE">Voice</option>
            			<option value="1" self="I18N_MEDIAABILITY_VIDEO">Video</option>
            			<option value="2" self="I18N_MEDIAABILITY_DEFAULT">Default</option>
            		</select>
            		<input type="button" value="Callout" self="I18N_Callout" onclick="Operate_Callout()">
            	</td>
            </tr>
		</table>
		<h3> <input type="button"  value="Event statistics" self="I18N_MEDIAABILITY_EVENT_STATICS" onclick="Operate_getCountEvent()"></h3>
		
	</div>
	<div style="float:left;width:33%">
        <p self="I18N_AGENTAPPDEMO_LOG">LOG</p>
        <p>
            <input type="button" value="Clear Log" ID="clearLog" self="I18N_LOG_CLEAR" onclick="clearLog()"/>
        </p>
        <textarea id="LogInfo" readonly="readonly" rows="50" style="width:100%" ></textarea>
    </div>
</body>
</html>