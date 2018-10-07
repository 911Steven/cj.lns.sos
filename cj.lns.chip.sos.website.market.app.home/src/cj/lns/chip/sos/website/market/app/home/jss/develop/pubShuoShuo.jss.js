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
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var UUID = Java.type('java.util.UUID');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var cnt=map.get('content');
	
	var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
	var source=new HashMap();
	source.put('id',UUID.randomUUID().toString());
	source.put('entity','shuoshuo');
	source.put('creator',sws.owner());
	source.put('createTime',System.currentTimeMillis());
	source.put('title','');
	source.put('creator',sws.owner());
	source.put('onsws',sws.swsid());
	var pictures=[];
	var picArr=map.get('pictures');
	if(picArr!=null){
		for(var i=0;i<picArr.size();i++){
			var pic=picArr.get(i);
			var src=String.format("./resource/ud/%s?path=%s://pictures/&u=%s",pic,sws.swsid(),sws.owner());
			pictures.push(src);
		}
		source.put('pictures',pictures);
	}
	
	var medias=[];
	var mediasArr=map.get('medias');
	if(mediasArr!=null){
		for(var i=0;i<mediasArr.size();i++){
			var media=mediasArr.get(i);
			var src=String.format("./resource/ud/%s?path=%s://medias/&u=%s",media,sws.swsid(),sws.owner());
			medias.push(src);
		}
		source.put('medias',medias);
	}
	pusher.push('shuoshuo',cnt,source,sws,m);
}

