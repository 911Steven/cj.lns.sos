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
var ServicewsBody = Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var FileOutputStream = Java.type('java.io.FileOutputStream');


exports.flow = function(frame, circuit, plug, ctx) {
	var swsid=frame.parameter('swsid');
	var out = ServiceosWebsiteModule.get().out();
	var subject=ISubject.subject(frame);
	var f = new Frame("getbasicSws /sws/instance sos/1.0");
	var c = new Circuit("sos/1.0 200 ok");
	f.parameter("swsid", swsid);
	out.flow(f, c);
	var ctx = c.content();
	var back = new Frame(ctx.readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), back.head("message"));
	}
	circuit.content().writeBytes(back.content());
}