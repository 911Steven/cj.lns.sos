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
	var appid='';
	if('home'==frame.parameter('cube')){
		cube=disk.home();
		appid='homespace';
	}else{
		cube=disk.cube(sws.swsid());
		appid='myspace';
	}
	
	var doc = m.context().html("/microblog/myspace.html",
			m.site().contextPath(), "utf-8");
	doc.select('.cube').append("<input id='cube' type='hidden' value='"+frame.parameter('cube')+"'>");
	var path=frame.parameter('path');
	if(StringUtil.isEmpty(path)){
		path="/";
	}
	printFstypeDims(sws,cube,doc);
	printCDateDims(sws,cube,doc);
	printView(path,sws,cube,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
	var dynamicSession=chip.site().getService('$.cj.jss.sos.dynamicSession');
	if(dynamicSession!=null&&typeof dynamicSession!='undefined'){
		var json=dynamicSession.openDynamicSession(appid,sws);
		//print('----++++--'+json);
	}
}
function printCDateDims(sws,cube,doc){
	var fs=cube.fileSystem();
	var dims=fs.coordinateRoot("createDate");
	var yearul=doc.select('.cube > .dims > ul > li.cdate > .right>ul.year').first();
	var yearli=yearul.select('>li').first().clone();
	yearul.empty();
	
	for(var i=0;i<dims.size();i++){
		var coord=dims.get(i);
		var li=yearli.clone();
		li.select('>span').html(coord.value());
		li.attr('path',coord.toPath());
		
		var monthul=li.select('>ul.month').first();
		var monthli=monthul.select('>li').first().clone();
		monthul.empty();
		var months = fs.coordinateChilds("createDate",coord);
		for(var j=0;j<months.size();j++){
			var mcoord=months.get(j);
			var mli=monthli.clone();
			mli.select('>span').html(mcoord.value());
			mli.attr('path',mcoord.depCopyFromEnd().toPath());
			
			var dayul=mli.select('>ul.day').first();
			var dayli=dayul.select('>li').first().clone();
			dayul.empty();
			var days = fs.coordinateChilds("createDate",mcoord);
			for(var k=0;k<days.size();k++){
				var dcoord=days.get(k);
				var dli=dayli.clone();
				dli.select('>span').html(dcoord.value());
				dli.attr('path',dcoord.depCopyFromEnd().toPath());
				dayul.append(dli);
			}
			
			monthul.append(mli);
		}
		
		yearul.append(li);
	}
}
function printFstypeDims(sws,cube,doc){
	var fs=cube.fileSystem();
	var dimtype=fs.coordinateRoot("system_fs_files");
	var fstypeul=doc.select('.cube > .dims > ul > li.type > .right').first();
	var fstypeli=fstypeul.select('>span[value]').first().clone();
	fstypeul.empty();
	if(dimtype.size()<1){
		var li=fstypeli.clone();
		li.html('&nbsp;');
		fstypeul.html(li);
	}
	for(var i=0;i<dimtype.size();i++){
		var li=fstypeli.clone();
		var fstypecoord=dimtype.get(i);
		if(fstypecoord.value()=='$dir'){
			continue;
		}
		li.attr('path',fstypecoord.toPath());
		li.html(fstypecoord.value());
		fstypeul.append(li);
	}
}
function printView(dir,sws,cube,doc){
	var fs=cube.fileSystem();
	var d=fs.dir(dir);
	var folderName=d.name();
	if(folderName==null){
		d.refresh();
		folderName=d.name()
	}
	doc.select('.cube > .show > .title>span[label]').html(folderName);
	var site=String.format("{\"dir\":\"%s\"}",dir);
	doc.select('.cube > .show > .title>span[value]').html(site);
	
	resetfinder(d,sws,cube,doc);
	var ul=doc.select('.cube > .show > .finder > .folder > .items').first();
	var oli=ul.select('>.file').first().clone();
	if(!sws.isOwner()){
		oli.select('span.op').remove();
		doc.select('.cube > .show > .finder > .folder > .toolbar > div[op]').empty();
	}
	var pli=ul.select('>.parentdir').first().clone();
	ul.empty();
	
	var parent=d.parent();
	if(parent!=null){
		var parentdir=parent.path();
		if(!StringUtil.isEmpty(parentdir)){
			pli.attr('path',parentdir);
			pli.attr('fstype','dir');
			ul.append(pli);
		}
	}
	doc.select('.cube > .show > .finder > .folder > .toolbar').attr('currentdir',dir);
	doc.select('.cube > .show > .finder > .folder > .toolbar').attr('currentfolder',folderName);
	doc.select('.cube > .dims > ul > li.path > input').val(dir);
	
	var format=new SimpleDateFormat('yyyy年MM月dd日 hh:mm');
	var dirs=d.listDirs();
	doc.select('.cube > .show > .finder > .folder > .toolbar > div[folder]>span[value]').html(dirs.size());
	for(var i=0;i<dirs.size();i++){
		var the=dirs.get(i);
		var li=oli.clone();
		li.attr('path',the.path());
		li.attr('fstype','dir');
		var explain=li.select('> ul > li[b]>ul').first();
		li.select('>img').attr('src','./swssite/microblog/img/folder.svg');
		li.select('>ul>li[t]>span[label]').html(the.dirName());
		explain.select('>li[space]').remove();
		var filenames=the.listFileNames();
		explain.select('>li[files]>span[value]').html(filenames.size());
		explain.select('>li[cdate]').remove();
		ul.append(li);
	}
	var util= chip.site().getService('$.cj.jss.sos.util');
	var files=d.listFiles();
	doc.select('.cube > .show > .finder > .folder > .toolbar > div[folder]>span[folder]>span[value]').html(files.size());
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
function resetfinder(d,sws,cube,doc){
	var detail=doc.select('.cube > .show > .finder > .box > .detail').first();
	detail.html('&nbsp;');
	detail.attr('style','display:none;');
	var folder=doc.select('.cube > .show > .finder > .folder').first();
	folder.attr('style','width:100%;margin-left:0');
}
