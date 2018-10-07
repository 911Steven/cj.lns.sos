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

exports.flow = function(frame, circuit, plug, ctx) {
	var sws=IServicewsContext.context(frame);
	var m = ServiceosWebsiteModule.get();
	var doc=m.context().html("/components/servicewsSetting.html", m.site().contextPath(), "utf-8");
	printView(doc,sws,m);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printView(doc,sws,m){
	var f=new Frame("get /portal/getPortalInfoComponent.service http/1.1");
	f.parameter('portal.id',sws.prop('portal.id'));
	var c=new Circuit("http/1.1 200 ok");
	m.site().out().flow(f,c);
	var chip=c.attribute("return-portal-chip");
	var scanner=c.attribute("return-portal-scanner");
	var info=chip.info();
	var portals =scanner.getPortals();
	
	doc.select('.sws-setting > .content-panel >.scenelabel>span[usedscene]>span').html(sws.prop('portal.scene'));

	var portalPanle=doc.select('.sws-setting > .content-panel > .portal > .item > .basic').first();
	portalPanle.select('>li[num]>span[value]').html(info.getId());
	portalPanle.select('>li[name]>span[value]').html(info.getName());
	portalPanle.select('>li[desc]>span[value]').html(info.getDescription());
	portalPanle.select('>li[org]>span[value]').html(info.getCompany());
	portalPanle.select('>li[version]>span[value]').html(info.getVersion());
	
	var portal=portals.get(info.getId());
	var scenes=portal.getScenes();
	var scenemap=new HashMap();//key是场景简式名，value是SceneInfo
	var SceneInfo={};//
	for(var i=0;i<scenes.size();i++){
		var scene=scenes.get(i);
		var name=scene.getSceneName();
		var pos=name.indexOf('.');
		if(pos>-1){
			name=name.substring(0,pos);
		}
		var device=scene.getSceneName();
		device=device.substring(name.length+1,device.length);
		pos=device.indexOf('.');
		if(pos>-1){
			device=device.substring(0,pos);
		}else{
			device='browser';
		}
		var si=scenemap.get(name);
		if(si==null){
			si=[];
			si.push({
				device:device,
				title:scene.getSceneTitle(),
				fullname:scene.getSceneName(),
				desc:scene.getSceneDesc()
			});
			scenemap.put(name,si);
		}else{
			si.push({
				device:device,
				title:scene.getSceneTitle(),
				fullname:scene.getSceneName(),
				desc:scene.getSceneDesc()
			});
		}
		
	}
	var ul=doc.select('.sws-setting>.content-panel>.scene').first();
	var sli=ul.select('>li').first().clone();
	ul.empty();
	var it=scenemap.entrySet().iterator();
	
	while(it.hasNext()){
		var scene=it.next();
		var sceneName=scene.getKey();
		var files=scene.getValue();
		var title=files[0].title;
		var desc=files[0].desc;
		var devicestr='';
		for(var i=0;i<files.length;i++){
			devicestr+=files[i].device+",";
		}
		devicestr=devicestr.substring(0,devicestr.length-1);
		
		var li=sli.clone();
		var sceneE=li.select('>.main-panel>div');
		if(desc!=null){
			sceneE.attr('title',desc);
		}
		
		sceneE.select('>p[table]>span[name]').html(sceneName);
		sceneE.select('>p[col]>span[title]').html(title);
		sceneE.select('>p[col]>span[device]').html(devicestr);
		var url='./servicews/components/sceneSetting.html?scene='+sceneName;
		li.attr('opencontent',url);
		ul.append(li);
	}
}