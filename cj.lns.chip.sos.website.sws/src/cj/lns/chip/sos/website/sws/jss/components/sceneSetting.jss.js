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
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var Mailbox = Java.type('cj.studio.ecm.sns.mailbox.Mailbox');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');
var ServicewsBody=Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var Face=Java.type('cj.lns.chip.sos.website.framework.Face');
var ByteArrayOutputStream=Java.type('java.io.ByteArrayOutputStream');
var IServiceosContext=Java.type('cj.lns.chip.sos.website.framework.IServiceosContext');

exports.flow = function(frame, circuit, plug, ctx) {
	var sws=IServicewsContext.context(frame);
	var device=IServiceosContext.context(frame).device(frame);
	var m = ServiceosWebsiteModule.get();
	var selectScene=frame.parameter('scene');
//	var disk= m.site().diskOwner(user);
	
//	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var doc=m.context().html("/components/scene-setting.html", m.site().contextPath(), "utf-8");
	
	printView(selectScene,doc,sws,device,m);

	doc.body().prepend(String.format(
	"<nav imgsrc='img/servicePlatform.svg' title='%s' desc='场景'/>",selectScene));
	
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printView(selectScene,doc,sws,device,m){
	var f=new Frame("get /portal/getPortalInfoComponent.service http/1.1");
	f.parameter('portal.id',sws.prop('portal.id'));
	var c=new Circuit("http/1.1 200 ok");
	m.site().out().flow(f,c);
	var chip=c.attribute("return-portal-chip");
	var scanner=c.attribute("return-portal-scanner");
	var info=chip.info();
	var portals =scanner.getPortals();
	
	doc.select('.sceneset').attr('scene',selectScene);
	var currentTheme=sws.prop('portal.theme');
	
	var portal=portals.get(sws.prop('portal.id'));
	var themes=portal.getThemes();
	var deviceType='browser';
	if(device.isMobile()){
		deviceType='mobile';
	}
	
	var arr=themes.get(deviceType);
	var ul=doc.select('.sceneset > .content-panel > .themes').first();
	var tli=ul.select(' > .item').first().clone();
	ul.empty();
	
	for(var i=0;i<arr.length;i++){
		var li=tli.clone();
		li.select('>.main-panel>div').html(arr[i]);
		if(arr[i]==currentTheme){
			li.select('>.opp>input').attr('checked','checked');
		}
		li.attr('theme',arr[i]);
		ul.append(li);
	}
	//打印墙纸
	var disk=m.site().diskLnsData();
	var fs=disk.home().fileSystem();
	var dir=fs.dir("/wallpaper/");
	if(!dir.exists()){
		dir.mkdir('公共墙纸选');
	}
	var background=sws.prop('portal.background');
	if(background==null){
		background='img/background/background-default.jpg';
		doc.select('.sceneset > .content-panel > .wall > .shower > div[box]>img').attr('src',background);
	}else{
		background=String.format("./resource/dd/%s?path=home://wallpaper/",background);
		doc.select('.sceneset > .content-panel > .wall > .shower > div[box]>img').attr('src',background);
	}
	var files=dir.listFileNames();
	var ul=doc.select('.sceneset > .content-panel > .wall > .pics>ul').first();
	var pli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<files.size();i++){
		var file=files.get(i);
		var li=pli.clone();
		li.attr('file',file);
		var src=String.format("./resource/dd/%s?path=home://wallpaper/",file);
		li.select(">img").attr('src',src);
		ul.append(li);
	}
}