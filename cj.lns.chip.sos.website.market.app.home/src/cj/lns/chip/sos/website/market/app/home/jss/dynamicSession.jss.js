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
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');

exports.openDynamicSession= function(appid,sws){
	var m = ServiceosWebsiteModule.get();
	var f = new Frame("open /device/session/dynamicSession peer/1.0");
	f.parameter("owner", sws.owner());
	f.parameter("app-code", "dynamic");
	f.parameter("app-id",appid);//目前共4种应用：产品、博客、空间（两种）、与我相关（赞、评等）
	f.parameter("swsid", sws.swsid());
	var c = new Circuit("peer/1.0 200 OK");
	// 等待15秒
	try {
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			var back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						String.format("在远程服务器上出现错误。原因：%s",
								back.head("message")));
			}
			var json = new String(back.content().readFully());
			return json;
		}
	} catch (e1) {
		throw e1;
	}
}

