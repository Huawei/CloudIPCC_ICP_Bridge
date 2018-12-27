

String.prototype.trim = function() 
{ 
	var value = this.replace(/(^\s*)|(\s*$)/g, "");      
    return value.replace(/(^　*)|(　*$)/g, "");     
}; 



/**
 *change html tag to string
 * @param objVal
 * @returns
 */
function htmlEncode(objVal)
{
	var str = objVal+"";
	if(str == '')
	{
		return str;
	}
	str = str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(new RegExp("\"","g"),"&quot;").replace(new RegExp("\'","g"),"&#39;").replace(new RegExp("  ","g")," &nbsp;");
	return str;
}
//log print
function writeLog(content)
{
    var oldValue = $("#LogInfo").val();
    var time = getNowTime();
    $("#LogInfo").val("["+time+"]" + content + "\n\n" + oldValue);
}


//log clear
function clearLog()
{
  $("#LogInfo").val("");
}

function getNowTime()
{
	var date = new Date();
	return date.getHours()+':'+date.getMinutes()+':'+date.getSeconds();
}



function doCall()
{
	if ($("#monitoredAgent").val().trim() == "")
	{
		alert("Please input The Monitored Agent.");
		return; 
	}
	
	$.post("./ThirdControlServlet.do? ",
		{
			operType : OperType.DoCall,
			monitoredAgent : $("#monitoredAgent").val().trim(),
			calleeNumber : $("#Call_calleeNumber").val().trim(),
			isVideo : $("#Call_isVideo").val()
		},
		function(data){
			var ret = JSON.parse(data);
			switch(ret.returnCode)
			{
				case global_resultCode_SUCCESSCODE:
					writeLog("Do Call succes. The result is " + data);
					break;
				default:
					writeLog("Do Call failed. The result is " + data);
			}
		});
}


function addConfMember()
{
	window.open("./page/window/addConfMember.html", "AddAgentConfMeb", "left=" +  (window.screen.availWidth-400)/2 
			+ ",top=" + (window.screen.availHeight-250)/2
			+ ",width=400,height=250,scrollbars=no,resizable=no,toolbar=no,directories=no,status=no,menubar=no");
}

function addConfMemberRow(number, name, isMute)
{
	var html = new Array();
	html.push("<tr><td>");
	html.push(htmlEncode(number.trim()));
	html.push("</td><td>");
	html.push(htmlEncode(name.trim()));
	html.push("</td><td>");
	html.push(htmlEncode(isMute.trim()));
	html.push("</td><td>");
	html.push("<input type='button' value='" + global_language["I18N_MEMBER_DELETE"] + "' onclick='rowDelete(this)'>");
	html.push("</td></tr>");
	$("#ConfMemberTable tbody").append(html.join(""));
}

function rowDelete(obj)
{
	$(obj).parent().parent().empty(); 
}

function doConferenceCall()
{
	if ($("#monitoredAgent").val().trim() == "")
	{
		alert("Please input The Monitored Agent.");
		return; 
	}
	if ($("#ConfMemberTable tbody tr").size() == 0)
	{
		alert("Please add one member");
		return;
	}
	
	var numberArray = new Array();
	var nameArray = new Array();
	var isMuteArray = new Array();
	$("#ConfMemberTable tbody tr").each(function(i){
		$(this).find("td").each(function(j){
			if (0 == j)
			{
				numberArray.push($(this).text());	
			}
			else if (1 == j)
			{
				nameArray.push($(this).text());	
			}
			else if (2 == j)
			{
				isMuteArray.push($(this).text());
			}
		});
	});
	
	$.post("./ThirdControlServlet.do? ",
		{
			operType : OperType.DoConference,
			monitoredAgent : $("#monitoredAgent").val().trim(),
			isRecord : $("#Conference_isRecord").val(),
			isVideo : $("#Conference_isVideo").val(),
			number : numberArray.join(";"),
			name : nameArray.join(";"),
			isMute : isMuteArray.join(";")
		},
		function(data){
			var ret = JSON.parse(data);
			switch(ret.returnCode)
			{
				case global_resultCode_SUCCESSCODE:
					writeLog("Do Conference Call succes. The result is " + data);
					break;
				default:
					writeLog("Do Conference Call failed. The result is " + data);
			}
		});
}



function addMixGroupMember()
{
	window.open("./page/window/addMixGroupMember.html", "AddAgentConfMeb", "left=" +  (window.screen.availWidth-400)/2 
			+ ",top=" + (window.screen.availHeight-250)/2
			+ ",width=400,height=250,scrollbars=no,resizable=no,toolbar=no,directories=no,status=no,menubar=no");
}

function addMixGroupMemberRow(number, userType)
{
	var html = new Array();
	html.push("<tr><td>");
	html.push(htmlEncode(number.trim()));
	if (userType == 1)
	{
		html.push("</td><td value='1'>");
		html.push(global_language["I18N_USERTYPE_WIRELESS"]);
	}
	else
	{
		html.push("</td><td value='2'>");
		html.push(global_language["I18N_USERTYPE_OTHER"]);
	}
	html.push("</td><td>");
	html.push("<input type='button' value='" + global_language["I18N_MEMBER_DELETE"] + "' onclick='rowDelete(this)'>");
	html.push("</td></tr>");
	$("#MixGroupMemberTable tbody").append(html.join(""));
}


function doMixGroupCall()
{
	if ($("#monitoredAgent").val().trim() == "")
	{
		alert("Please input The Monitored Agent.");
		return; 
	}
	
	if ($("#MixGroupMemberTable tbody tr").length == 0)
	{
		alert("Please add one member");
		return;
	}
	
	var numberArray = new Array();
	var userTypeArray = new Array();
	$("#MixGroupMemberTable tbody tr").each(function(i){
		$(this).find("td").each(function(j){
			if (0 == j)
			{
				numberArray.push($(this).text());	
			}
			else if (1 == j)
			{
				userTypeArray.push($(this).attr("value"));	
			}
		});
	});
	
	$.post("./ThirdControlServlet.do? ",
		{
			operType : OperType.DoMixGroup,
			monitoredAgent : $("#monitoredAgent").val().trim(),
			groupAlias : $("#groupAlias").val().trim(),
			number : numberArray.join(";"),
			userType : userTypeArray.join(";")
		},
		function(data){
			var ret = JSON.parse(data);
			switch(ret.returnCode)
			{
				case global_resultCode_SUCCESSCODE:
					writeLog("Do MixGroup Call succes. The result is " + data);
					break;
				default:
					writeLog("Do MixGroup Call failed. The result is " + data);
			}
		});
}

function doSendSMS()
{
	if ($("#monitoredAgent").val().trim() == "")
	{
		alert("Please input The Monitored Agent.");
		return; 
	}
	

	$.post("./ThirdControlServlet.do? ",
		{
			operType : OperType.DoSendSms,
			monitoredAgent : $("#monitoredAgent").val().trim(),
			userList : $("#SMS_UserList").val().trim(),
			smsContent : $("#SMS_Content").val().trim()
		},
		function(data){
			var ret = JSON.parse(data);
			switch(ret.returnCode)
			{
				case global_resultCode_SUCCESSCODE:
					writeLog("Send SMS succes. The result is " + data);
					break;
				default:
					writeLog("Send SMS failed. The result is " + data);
			}
		});
}