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
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var HashMap = Java.type('java.util.HashMap');
var System = Java.type('java.lang.System');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=null;
	var entity='myspace';
	if('home'==frame.parameter('cube')){
		cube=disk.home();
		entity='homespace';
	}else{
		cube=disk.cube(sws.swsid());
	}
	var doc = m.context().html("/develop/myspace.html",
			m.site().contextPath(), "utf-8");
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var path=map.get('path');
	if(StringUtil.isEmpty(path)){
		throw new CircuitException('503','path参数为空');
	}
	var fs=cube.fileSystem();
	var file=fs.openFile(path);
	saveRelatives(entity,file,cube,sws);
}
function saveRelatives(entity,file,cube,sws){
	var map=new HashMap();
	map.put('swsid',sws.swsid());
	map.put('sourceid',file.fullName());
	map.put('entity',entity);
	map.put('kind','download');
	map.put('ctime',System.currentTimeMillis());
	map.put('reviewer',sws.visitor().principal());
	map.put('reviewerFace',sws.visitor().face());
	var doc=new TupleDocument(map);
	cube.saveDoc('entity.relatives',doc);
}

