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
var PortletSO=Java.type('cj.lns.chip.sos.website.PortletSO');
var TypeToken=Java.type('cj.ultimate.gson2.com.google.gson.reflect.TypeToken');
var List=Java.type('java.util.List');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var json = new String(frame.content().readFully());
	var let = new Gson().fromJson(json, PortletSO.class);
	var doc = m.context().html("/servicewsOwner.html", m.site().contextPath(),
			"utf-8");
	var src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s",sws.face().getHead(),sws.owner());
	doc.select('.portlet[owner] > .face > .img > img').attr('src',src);
	doc.select('.portlet[owner] > .face > .img > span[label]').html(sws.owner());
	//var	swsList = getMySwsList(sws);
	printSwsList(sws,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printSwsList(sws,doc){
		
		var src= String.format(
				"./resource/ud/%s?path=%s://system/faces&u=%s",
				sws.prop('sws-img'), sws.swsid(),
				sws.owner());
		var desc=sws.prop('sws-desc');
		if(desc==null){
			desc='&nbsp;';
		}
		var face=sws.face();
		if(face.getSignText()!=null){
			doc.select('.portlet[owner] > .face > .intro > p[sign]').html(face.getSignText());
		}
		if(face.getBriefing()!=null){
			doc.select('.portlet[owner] > .face > .intro > pre').html(face.getBriefing());
		}else{
			doc.select('.portlet[owner] > .face > .intro > pre').html("&nbsp;");
		}
//		doc.select('.portlet[owner] > .box').attr('title','视窗说明：'+desc);
//		doc.select('.portlet[owner] > .box >img').attr('src',src);
//		doc.select('.portlet[owner] > .box > ul>li[name]>span').html(sws.prop('sws-name'));
//		doc.select('.portlet[owner] > .box').attr('swsid',sws.swsid());
//		var type = "";
//		switch (sws.level()) {
//		case 0:
//			type = "超级视窗";
//			break;
//		case 1:
//			type = "基础视窗";
//			break;
//		case 2:
//			type = "公共视窗";
//			break;
//		case 3:
//			type = "个人视窗";
//			break;
//		}
//		doc.select('.portlet[owner] > .box > ul>li[type]>span').html("<span label>"+type+"</span>")
}
function getMySwsList(sws) {
	var swsid = sws.swsid();
	// 认证成功则查找默认视窗，如果有则
	var frame = new Frame("getServicewsFace /sws/instance sos/1.0");
	frame.parameter("userCode", sws.owner());
	var circuit = new Circuit("sos/1.0 200 ok");
	var out = ServiceosWebsiteModule.get().site().out();
	out.flow(frame, circuit);
	var ctx = circuit.content();
	if (ctx.readableBytes() > 0) {
	var back = new Frame(ctx.readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException("503",
				String.format("获取视窗失败：%s", swsid));
	}
	var json = new String(back.content().readFully());
	var list = eval(json);
	return list;
	
	}
	return [];
}

