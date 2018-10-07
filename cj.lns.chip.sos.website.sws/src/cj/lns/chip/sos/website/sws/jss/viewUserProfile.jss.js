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

function getMySwsList(user) {
	// 认证成功则查找默认视窗，如果有则
	var frame = new Frame("getServicewsFace /sws/instance sos/1.0");
	frame.parameter("userCode", user);
	var circuit = new Circuit("sos/1.0 200 ok");
	var out = ServiceosWebsiteModule.get().site().out();
	out.flow(frame, circuit);
	var ctx = circuit.content();
	if (ctx.readableBytes() > 0) {
	var back = new Frame(ctx.readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException("503",
				String.format("获取用户的视窗失败：%s", back.head('message')));
	}
	var json = new String(back.content().readFully());
	var list = eval(json);
	return list;
	
	}
	return [];
}
function getUserFace(user){
	var frame = new Frame("getUserFace /public/user/ sos/1.0");
	frame.parameter("userCode", user);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
	var json = new String(back.content().readFully());
	return new Gson().fromJson(json, Face.class);
}
function updateSignText(user,signText){
	var frame = new Frame("updateSignText /public/user/ sos/1.0");
	frame.parameter("userCode", user);
	frame.parameter("signatureText", signText);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
function updateBriefing(user,briefing){
	var frame = new Frame("updateBriefing /public/user/ sos/1.0");
	frame.parameter("userCode", user);
	frame.parameter("briefing", briefing);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
function updateNickName(user,nickName){
	var frame = new Frame("updateNickName /public/user/ sos/1.0");
	frame.parameter("userCode", user);
	frame.parameter("nickName", nickName);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
function updateHeadPic(user,fn){
	var frame = new Frame("updateHeadPic /public/user/ sos/1.0");
	frame.parameter("userCode", user);
	frame.parameter("head", fn);
	var circuit = new Circuit("sos/1.0 200 ok");

	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var back = new Frame(circuit.content().readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), String.format(
				"远程失败：%s", back.head('message')));
	}
}
exports.flow = function(frame, circuit, plug, ctx) {
	var sws=IServicewsContext.context(frame);
	var m = ServiceosWebsiteModule.get();
	var user=frame.parameter('user');
	var face=null;
	if(user==null){
		user=sws.owner();
		//face=sws.face();//为了在修改face后不必登录即生效因此注释掉
		face=getUserFace(user);
	}else{
		face=getUserFace(user);
	}
	var swslist=getMySwsList(user);
	var disk= m.site().diskOwner(user);
	
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var action=map.get('action');
	if('edit-sign'==action){
		if(!sws.isOwner()){
			throw new CircuitException('801','没有修改权限。');
		}
		var val=map.get('val');
		updateSignText(user,val);
		return;
	}
	if('edit-briefing'==action){
		if(!sws.isOwner()){
			throw new CircuitException('801','没有修改权限。');
		}
		var val=map.get('val');
		updateBriefing(user,val);
		return;
	}
	if('edit-nick'==action){
		if(!sws.isOwner()){
			throw new CircuitException('801','没有修改权限。');
		}
		var val=map.get('val');
		updateNickName(user,val);
		return;
	}
	if('edit-pic'==action){
		if(!sws.isOwner()){
			throw new CircuitException('801','没有修改权限。');
		}
		var fn=map.get('fn');
		updateHeadPic(user,fn);
		return;
	}
	var doc=m.context().html("/components/swsOwner.html", m.site().contextPath(), "utf-8");
	
	printview(user,face,swslist,disk,doc);
	if(sws.isOwner()&&sws.owner()==user){
		doc.select('.sws-cnt-o > .o-pic > .o-sin').attr('e','yes');
		doc.select('.sws-cnt-o > .o-owner > .o-card > ul > li[nick]').attr('e','yes');
		doc.select('.sws-cnt-o > .o-owner > .o-briefing').attr('e','yes');
	}else{
		doc.select('.sws-cnt-o > .o-pic > span[set]').remove();
	}
	circuit.content().writeBytes(doc.toString().getBytes());
}

function getBody(body){
	var out=new ByteArrayOutputStream();
	for(var j=0;j<body.size();j++){
		out.write(Integer.valueOf(body.get(j)));
	}
	out.close();
	return new String(out.toByteArray());
}
function printview(user,face,swslist,disk,doc){
	var home=disk.home();
	var facedir=home.fileSystem().dir('/system/img/faces/');
	var filenames=facedir.listFileNames();
	var opul=doc.select('.sws-cnt-o > .o-pic > .o-pic-sels').first();
	var opli=opul.select('>li').first().clone();
	opul.empty();
	doc.select('.sws-cnt-o > .o-pic').attr('title','如需上传头像，请到[主空间]将图片上传到目录：/system/img/faces/');
	for(var i=0;i<filenames.size();i++){
		var fn=filenames.get(i);
		var li=opli.clone();
		li.attr('fn',fn);
		var src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s",fn,user);
		li.select('>img').attr('src',src);
		opul.append(li);
	}
	if(filenames.size()<1){
		doc.select('.sws-cnt-o > .o-pic').attr('style','display:none;');
	}else{
		var fn=face.getHead();
		var src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s",fn,user);
		doc.select('.sws-cnt-o > .o-pic>img').attr('src',src);
		doc.select('.sws-cnt-o > .o-pic').attr('fn',fn);
	}
	
	doc.select('.sws-cnt-o > .o-owner > .o-card > ul > li[username] > span[text]').html(user);
	doc.select('.sws-cnt-o > .o-owner > .o-card > ul > li[nick] > span[text]').html(face.getMemoName());
	doc.select('.sws-cnt-o > .o-owner > .o-card > ul > li[sex] > span[text]').html(face.isMale()?'男':'女');
	var sign=face.getSignText();
	if(sign==null){
		sign='请输入签名...';
	}
	doc.select('.sws-cnt-o > .o-pic > .o-sin>p').html(sign);
	
	var briefing=face.getBriefing();
	if(briefing==null){
		briefing='请输入个人简介...';
	}
	doc.select('.sws-cnt-o > .o-owner > .o-briefing > pre').html(briefing);
	
	var swsbox=doc.select('.sws-cnt-o > .o-owner > .o-sections > ul').first();
	var swsli=swsbox.select('>li[sws]').first().clone();
	swsbox.empty();
	
	for(var k=0;k<swslist.length;k++){
		var sws=swslist[k];
		var sli=swsli.clone();
		var a=sli.select('>a');
		var src=String.format("./resource/ud/%s?path=%s://system/faces&u=%s",sws.faceImg,sws.swsId+'',sws.owner);
		a.select('>img').attr('src',src);
		a.select('>span[bottom]').html(sws.name);
		
		var type='';
		switch(sws.level){
		case 0:
			type='超级视窗';
			break;
		case 1:
			type='基础视窗';
			break;
		case 2:
			type='公共视窗';
			break;
		default:
			type='个人视窗';
			break;
		}
		a.attr('swsid',sws.swsId);
		a.select('>span[type]').html(type);
	
		//取最新的几条发件并打印
		var dul=sli.select('>ul');
		var cube=disk.cube(sws.swsId);
		
		var cjql=String.format("select {'tuple':'*'}.limit(4).sort({'tuple.sendTime':-1}) from tuple iobox java.util.HashMap where {'tuple.iobox':'outbox','tuple.source.appId':'myproduct'}");
		var q=cube.createQuery(cjql);
		var resultlist=q.getResultList();
		var ul=dul.select('>li[product]>ul').first();
		var ili=ul.select('>li').first().clone();
		ul.empty();
		for(var i=0;i<resultlist.size();i++){
			var item=resultlist.get(i);
			if(item.tuple().get('source')!=null&&item.tuple().get('source').get('title')!=null){
				var li=ili.clone();
				li.select('>span[item]').html((i+1)+'. '+item.tuple().get('source').get('title'));
				ul.append(li);
			}
		}
		if(resultlist.size()<1){
			var li=ili.clone();
			li.select('>span[item]').html('-');
			ul.append(li);
		}
		
		 cjql=String.format("select {'tuple':'*'}.limit(4).sort({'tuple.sendTime':-1}) from tuple iobox java.util.HashMap where {'tuple.iobox':'outbox','tuple.source.appId':'myarticle'}");
		 q=cube.createQuery(cjql);
		 resultlist=q.getResultList();
		 ul=dul.select('>li[article]>ul').first();
		 ili=ul.select('>li').first().clone();
		ul.empty();
		for(var i=0;i<resultlist.size();i++){
			var item=resultlist.get(i);
			if(item.tuple().get('source')!=null&&item.tuple().get('source').get('title')!=null){
				var li=ili.clone();
				li.select('>span[item]').html((i+1)+'. '+item.tuple().get('source').get('title'));
				ul.append(li);
			}
		}
		if(resultlist.size()<1){
			var li=ili.clone();
			li.select('>span[item]').html('-');
			ul.append(li);
		}
		
		 cjql=String.format("select {'tuple':'*'}.limit(4).sort({'tuple.sendTime':-1}) from tuple iobox java.util.HashMap where {'tuple.iobox':'outbox','tuple.source.appId':'shuoshuo'}");
		 q=cube.createQuery(cjql);
		 resultlist=q.getResultList();
		 ul=dul.select('>li[shuoshuo]>ul').first();
		 ili=ul.select('>li').first().clone();
		ul.empty();
		for(var i=0;i<resultlist.size();i++){
			var item=resultlist.get(i);
			var li=ili.clone();
			li.select('>span[item]').html((i+1)+'. '+getBody(item.tuple().get('body')));
			ul.append(li);
		}
		if(resultlist.size()<1){
			var li=ili.clone();
			li.select('>span[item]').html('-');
			ul.append(li);
		}
		
		 cjql=String.format("select {'tuple':'*'}.limit(4).sort({'tuple.sendTime':-1}) from tuple iobox java.util.HashMap where {'tuple.iobox':'outbox','tuple.source.appId':'myspace'}");
		 q=cube.createQuery(cjql);
		 resultlist=q.getResultList();
		 ul=dul.select('>li[myspace]>ul').first();
		 ili=ul.select('>li').first().clone();
		ul.empty();
		for(var i=0;i<resultlist.size();i++){
			var item=resultlist.get(i);
			var li=ili.clone();
			li.select('>span[item]').html((i+1)+'. '+getBody(item.tuple().get('body')));
			ul.append(li);
		}
		if(resultlist.size()<1){
			var li=ili.clone();
			li.select('>span[item]').html('-');
			ul.append(li);
		}
		
		 cjql=String.format("select {'tuple':'*'}.limit(4).sort({'tuple.sendTime':-1}) from tuple iobox java.util.HashMap where {'tuple.iobox':'outbox','tuple.source.appId':'homespace'}");
		 q=cube.createQuery(cjql);
		 resultlist=q.getResultList();
		 ul=dul.select('>li[homespace]>ul').first();
		 ili=ul.select('>li').first().clone();
		ul.empty();
		for(var i=0;i<resultlist.size();i++){
			var item=resultlist.get(i);
			var li=ili.clone();
			li.select('>span[item]').html((i+1)+'. '+getBody(item.tuple().get('body')));
			ul.append(li);
		}
		if(resultlist.size()<1){
			var li=ili.clone();
			li.select('>span[item]').html('-');
			ul.append(li);
		}
	
		swsbox.append(sli);
	}
}