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
var Circuit = Java.type('cj.studio.ecm.frame.Circuit');
var String = Java.type('java.lang.String');
var CircuitException = Java.type('cj.studio.ecm.graph.CircuitException');
var ServiceosWebsiteModule = Java
		.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var ServicewsBody = Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');

function genSwsNum(assignedNum, client) {
	var frame = new Frame("genServicewsNum /framework/idgen/ sos/1.0");
	var circuit = new Circuit("sos/1.0 200 ok");
	frame.parameter("assignedNum", assignedNum);
	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var ctx = circuit.content();
	if (ctx.readableBytes() > 0) {
		var back = new Frame(ctx.readFully());
		var state = parseInt(back.head("status"));
		if (state != 200) {
			throw new CircuitException(back.head("status"), back
					.head("message"));
		}
		String
		json = String.format("{\"wholeNum\":\"%s\",\"reedomNum\":\"%s\"}", back
				.head("wholeNum"), back.head("reedomNum"));
		client.content().writeBytes(json.getBytes());
	}
}
exports.flow = function(frame, circuit, plug, ctx) {
	var assignedNum = frame.parameter("assignedNum");
	genSwsNum(assignedNum, circuit);
}
