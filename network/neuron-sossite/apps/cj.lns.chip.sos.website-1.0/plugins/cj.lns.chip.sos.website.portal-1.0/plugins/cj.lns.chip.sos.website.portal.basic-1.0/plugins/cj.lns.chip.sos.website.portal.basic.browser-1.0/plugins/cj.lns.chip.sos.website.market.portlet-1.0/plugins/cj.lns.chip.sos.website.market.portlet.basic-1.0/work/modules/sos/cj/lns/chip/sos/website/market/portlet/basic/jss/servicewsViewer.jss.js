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
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var PortletSO=Java.type('cj.lns.chip.sos.website.PortletSO');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var disk = m.site().diskOwner(sws.owner());
	var cube = disk.cube(sws.swsid());
	
	var json = new String(frame.content().readFully());
	var let = new Gson().fromJson(json, PortletSO.class);
	var doc = m.context().html("/servicewsViewer.html", m.site().contextPath(),
			"utf-8");
	doc.select('.portlet[swsviewer]>.face>.name').html(sws.prop('sws-name'));
	var src=String.format("./resource/ud/%s?path=%s://system/faces/&u=%s",sws.prop('sws-img'),sws.swsid(),sws.owner());
	doc.select('.portlet[swsviewer]>.face>img').attr('src',src);
	
	var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {}";
	var q=cube.createQuery(cjql);
	doc.select('.portlet[swsviewer] > .visitor > li[browse] > p[value]').html(q.count());
	
	var type = "";
	switch (sws.level()) {
	case 0:
		type = "超级视窗";
		break;
	case 1:
		type = "基础视窗";
		break;
	case 2:
		type = "公共视窗";
		break;
	case 3:
		type = "个人视窗";
		break;
	}
	doc.select('.portlet[swsviewer]>.face>p[swstype]>span[value]').html(type);
	circuit.content().writeBytes(doc.toString().getBytes());
}
