/*
 * 说明：
 * 作者：extends可以实现一种类型，此类型将可在java中通过调用服务提供器的.getServices(type)获取到。
 * <![jss:{
		scope:'runtime'
 	}
 ]>
 <![desc:{
	ttt:'2323',
	obj:{
		name:'09skdkdk'
		}
 * }]>
 */
//var imports = new JavaImporter(java.io, java.lang)导入类型的范围，单个用Java.type
var Frame = Java.type('cj.studio.ecm.frame.Frame');
var FormData = Java.type('cj.studio.ecm.frame.FormData');
var FieldData = Java.type('cj.studio.ecm.frame.FieldData');
var Circuit = Java.type('cj.studio.ecm.frame.Circuit');
var ISubject = Java.type('cj.lns.chip.sos.website.framework.ISubject');
var String = Java.type('java.lang.String');
var CircuitException = Java.type('cj.studio.ecm.graph.CircuitException');
var ServiceosWebsiteModule = Java
		.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var Document = Java.type('org.jsoup.nodes.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var Mailbox = Java.type('cj.studio.ecm.sns.mailbox.Mailbox');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');
var ServicewsBody=Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var Face=Java.type('cj.lns.chip.sos.website.framework.Face');
var ByteArrayOutputStream=Java.type('java.io.ByteArrayOutputStream');
var IServiceosContext=Java.type('cj.lns.chip.sos.website.framework.IServiceosContext');

function setWall(swsId,background){
	var frame = new Frame("changeSwsBackground /sws/instance sos/1.0");
	frame.parameter("swsid", swsId);
	frame.parameter("background", background);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
function setTheme(swsId,theme){
	var frame = new Frame("changeSwsTheme /sws/instance sos/1.0");
	frame.parameter("swsid", swsId);
	frame.parameter("themeName", theme);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
function setScene(swsId,scene){
	var frame = new Frame("changeSwsScene /sws/instance sos/1.0");
	frame.parameter("swsid", swsId);
	frame.parameter("sceneName", scene);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
exports.flow = function(frame, circuit, plug, ctx) {
	var sws=IServicewsContext.context(frame);
	var device=IServiceosContext.context(frame).device(frame);
	var m = ServiceosWebsiteModule.get();
	var action=frame.parameter('action');
	switch(action){
	case 'setWall':
		var scene=frame.parameter('scene');
		if(scene!=null&&scene!=sws.prop('portal.scene')){
			setScene(sws.swsid(),scene);
			sws.prop('portal.scene',scene);
		}
		var wall=frame.parameter('wall');
		setWall(sws.swsid(),wall);
		sws.prop('portal.background',wall);
		break;
	case 'setTheme':
		var scene=frame.parameter('scene');
		if(scene!=null&&scene!=sws.prop('portal.scene')){
			setScene(sws.swsid(),scene);
			sws.prop('portal.scene',scene);
		}
		var theme=frame.parameter('theme');
		setTheme(sws.swsid(),theme);
		sws.prop('portal.theme',theme);
		break;
	default:
		throw new CircuitException('503', '不支持的请求事件：'+action);
	}
//	var disk= m.site().diskOwner(user);
//	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	
	
}
