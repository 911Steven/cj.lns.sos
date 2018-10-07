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

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=null;
	if('home'==frame.parameter('cube')){
		cube=disk.home();
	}else{
		cube=disk.cube(sws.swsid());
	}
	var doc = m.context().html("/develop/myspace.html",
			m.site().contextPath(), "utf-8");
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var path=map.get('path');
	if(StringUtil.isEmpty(path)){
		path="/";
	}
	var folder=doc.select('.cube > .show > .finder > .folder').first();
	printView(path,sws,cube,folder);
	circuit.content().writeBytes(folder.html().getBytes());
}
function printView(dir,sws,cube,folder){
	var d=cube.fileSystem().dir(dir);
	if(!d.exists()){
		throw new CircuitException('404','目录不存在:'+dir);
	}
	var ul=folder.select('>.items').first();
	var oli=ul.select('>.file').first().clone();
	if(!sws.isOwner()){
		oli.select('span.op').remove();
		folder.select(' > .toolbar > div[op]').empty();
	}
	var pli=ul.select('>.parentdir').first().clone();
	ul.empty();
	var foldername=d.name();
	if(foldername==null){
		d.refresh();
		foldername=d.name();
	}
	var parent=d.parent();
	if(parent!=null){
		var parentdir=parent.path();
		if(!StringUtil.isEmpty(parentdir)){
			pli.attr('path',parentdir);
			pli.attr('fstype','dir');
			ul.append(pli);
		}
	}
	folder.select('> .toolbar').attr('currentdir',dir);
	folder.select('> .toolbar').attr('currentfolder',foldername);
	
	var format=new SimpleDateFormat('yyyy年MM月dd日 hh:mm');
	var dirs=d.listDirs();
	folder.select('> .toolbar > div[folder]>span[value]').html(dirs.size());
	
	for(var i=0;i<dirs.size();i++){
		var the=dirs.get(i);
		var li=oli.clone();
		li.attr('path',the.path());
		li.attr('fstype','dir');
		var explain=li.select('> ul > li[b]>ul').first();
		li.select('>img').attr('src','./swssite/develop/img/folder.svg');
		li.select('>ul>li[t]>span[label]').html(the.dirName());
		explain.select('>li[space]').remove();
		var filenames=the.listFileNames();
		explain.select('>li[files]>span[value]').html(filenames.size());
		explain.select('>li[cdate]').remove();
		ul.append(li);
	}
	var util= chip.site().getService('$.cj.jss.sos.util');
	var files=d.listFiles();
	folder.select('>.toolbar > div[folder]>span[folder]>span[value]').html(files.size());
	for(var i=0;i<files.size();i++){
		var the=files.get(i);
		var li=oli.clone();
		li.attr('path',the.fullName());
		li.attr('fstype','file');
		var name=the.name();
		var src=util.getFileImgSrc(name);
		li.select('>img').attr('src',src);
		li.select('>ul>li[t]>span[label]').html(name);
		var explain=li.select('> ul > li[b]>ul').first();
		explain.select('>li[space]>span[value]').html(the.spaceLength());
		explain.select('>li[space]>span[unit]').html('字节');
		explain.select('>li[files]').remove();
		var createDate=the.createDate();
		var cdate=format.format(createDate);
		explain.select('>li[cdate]>span[value]').html(cdate);
		ul.append(li);
	}
}
