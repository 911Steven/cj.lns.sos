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

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var portal=sws.prop('portal.platform');
	switch(portal){
	case 'develop':
		//print('right');
		var call=m.site().getService('$.cj.jss.sos.develop.right');
		if(typeof call!=='undefined'&&call!=null){
			call.flow(frame,circuit,plug,ctx);
		}else{
			throw new CircuitException('404','找不到右列:right.jss.js');
		}
		break;
	case 'microblog':
		//print('right');
		var call=m.site().getService('$.cj.jss.sos.microblog.right');
		if(typeof call!=='undefined'&&call!=null){
			call.flow(frame,circuit,plug,ctx);
		}else{
			throw new CircuitException('404','找不到右列:right.jss.js');
		}
		break;
	default:
		//var doc = m.context().html("/error.html",
		//		m.site().contextPath(), "utf-8");
		//doc.select('.error').html('404 不支持的架构:');
		throw new CircuitException('404','不支持的架构:'+portal);
	}
	
}