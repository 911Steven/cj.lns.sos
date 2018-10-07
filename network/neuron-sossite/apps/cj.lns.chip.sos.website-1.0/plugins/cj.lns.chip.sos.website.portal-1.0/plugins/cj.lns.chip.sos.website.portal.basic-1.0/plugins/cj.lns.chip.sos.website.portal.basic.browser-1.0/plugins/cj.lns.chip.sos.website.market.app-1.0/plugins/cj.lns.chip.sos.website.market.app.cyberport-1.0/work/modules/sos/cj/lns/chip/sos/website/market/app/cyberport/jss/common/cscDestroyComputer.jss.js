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
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var CscComputer = Java.type('cj.lns.chip.sos.website.framework.csc.CscComputer');
var System=Java.type('java.lang.System');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var factory = m.site().getService('cscDeveloperFactory');
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var computer=factory.getComputer(sws.owner());
	var deploy=computer.getDeploy();
	//部署运计算机
	var f = new Frame(String.format("get /deploy/undeployComputer.service csc/1.0",chip));
	f.head('dest-address',deploy);
	f.head('csc-visitor',sws.owner());
	f.parameter('computer-owner',sws.owner());
	
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	var factory = m.site().getService('cscDeveloperFactory');
	factory.removeComputer(sws.owner());
	circuit.content().writeBytes(back.content());
}
