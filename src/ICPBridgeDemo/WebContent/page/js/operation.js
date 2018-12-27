
var global_event_timer = "";

String.prototype.trim = function() 
{ 
	var value = this.replace(/(^\s*)|(\s*$)/g, "");      
    return value.replace(/(^　*)|(　*$)/g, "");     
}; 


/**
 * 
 */
function Operate_StartMonitor()
{
	if ($("#monitoredWorkNo").val().trim() == "")
	{
		alert("Please input The Monitored Agent.");
		return; 
	}
	
	$.post("./MonitorServlet.do? ",
			{
				monitoredWorkNo : $("#monitoredWorkNo").val().trim(),
			},
			function(data){
				var ret = JSON.parse(data);
				switch(ret.retcode)
				{
					case global_resultCode_SUCCESSCODE:
						writeLog("Monitor Agent " + $("#monitoredWorkNo").val() + " Success");
						global_event_timer = setTimeout(Operate_getEvent(), 500);
						break;
					default:
						writeLog("Monitor Agent " + $("#monitoredWorkNo").val() + " failed. The result is " + data);
				}
			});
}


function Operate_getEvent()
{
	$.get("./EventServlet.do? ",
			function(data){
				var ret = JSON.parse(data);
				switch(ret.retcode)
				{
					case global_resultCode_SUCCESSCODE:
						
						if (ret["result"] != null )
						{
							writeLog("The Event is " + data);
							Operate_getEvent();
							return;
						}
						global_event_timer = setTimeout(Operate_getEvent(), 500);
						break;
					default:
						writeLog("Get Event failed. the result is " + data);
				}
			});
}



function Operate_getCountEvent()
{
	$.get("./EventCountServlet.do? ",
			function(data){
				var ret = JSON.parse(data);
				writeLog("EventCoutInfo " + data);
			});
}


function Operate_StatusControl(type)
{
	$.post("./StatusControlServlet.do? ",
			{
				operType :type
			},
			function(data){
				var ret = JSON.parse(data);
				switch(ret.retcode)
				{
					case global_resultCode_SUCCESSCODE:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "] for changing status to " + (type == 0 ? "AgentState_Idle" : "AgentState_Busy") + " Success.");
						break;
					case "10-000-004":
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Agent [" + monitoredWorkNo +"] has been monitored by other user." );
						break;
					default:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "]  for changing Status to " + (type == 0 ? "AgentState_Idle" : "AgentState_Busy") + " failed. the result is " + data);
				}
			});
}


function Operate_AnswerCall()
{
	$.post("./AnswerCallServlet.do? ", { },
			function(data){
				var ret = JSON.parse(data);
				switch(ret.retcode)
				{
					case global_resultCode_SUCCESSCODE:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "] for call answering success.");
						break;
					case "10-000-004":
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Agent [" + monitoredWorkNo +"] has been monitored by other user." );
						break;
					default:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "] for call answering failed. the result is " + data);
				}
			});
}

function Operate_Callout()
{
	$.post("./CallOutServlet.do? ", {
			"called" : $("#Callout_Called").val().trim(),
			"caller" : $("#Callout_Caller").val().trim(),
			"callappdata" : $("#Callout_CallAppData").val().trim(),
			"skillid" : $("#Callout_SkillId").val().trim(),
			"mediaability" : $("#Callout_MediaAbility").val().trim()
			},
			function(data){
				var ret = JSON.parse(data);
				switch(ret.retcode)
				{
					case global_resultCode_SUCCESSCODE:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "]'s for calling out success.");
						break;
					case "10-000-004":
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Agent [" + monitoredWorkNo +"] has been monitored by other user." );
						break;
					default:
						var monitoredWorkNo = ret["monitoredWorkNo"]; 
						writeLog("Send control to Agent [" + monitoredWorkNo + "]'s for calling out failed. the result is " + data);
				}
			});
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

