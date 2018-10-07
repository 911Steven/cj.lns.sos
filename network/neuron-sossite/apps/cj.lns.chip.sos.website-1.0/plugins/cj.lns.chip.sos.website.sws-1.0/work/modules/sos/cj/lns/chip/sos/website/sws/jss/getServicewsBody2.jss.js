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
function writeServicews(swsid, circuit2) {

	var frame = new Frame("getServicewsBody /sws/instance sos/1.0");
	frame.parameter("swsid", swsid);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"获取视窗失败：%s", back.head('message')));
	}
	var json = new String(back.content().readFully());
	var body = new Gson().fromJson(json, ServicewsBody.class);
	if (StringUtil.isEmpty(body.getFaceImg())) {
		body.setFaceImg("../cjdk/module-icon.svg");
	}else{
		var src=String.format("../resource/ud/%s?path=%s://system/faces&u=%s", body.getFaceImg(),swsid,body.owner);
		body.setFaceImg(src);
	}
	if (body.getExtra().isEmpty()) {
		body.getExtra().put("intro", body.getSwsDesc());
	}
	circuit2.content().writeBytes(new Gson().toJson(body).getBytes());
}
exports.flow = function(frame, circuit, plug, ctx) {
	var swsid = frame.parameter("swsid");
	writeServicews(swsid, circuit);
}
