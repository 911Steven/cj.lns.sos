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
var DecimalFormat=Java.type('java.text.DecimalFormat');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var isHome=false;
	var cube=null;
	if('home'==frame.parameter('cube')){
		cube=disk.home();
		isHome=true;
	}else{
		cube=disk.cube(sws.swsid());
	}
	var doc = m.context().html("/develop/cubeOutlookLeft.html",
			m.site().contextPath(), "utf-8");
	printView(isHome,cube,doc,sws);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printView(isHome,cube,doc,sws){
	if(isHome){
//		var src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s",sws.face().getHead(),sws.owner());
		var src="img/disk.svg";
		doc.select('.cube-face > .face > img').attr('src',src);
		var name=cube.config().alias();
		if(name==null){
			name="主存空间";
		}
		var title=cube.config().getDesc();
		if(title==null){
			title='主存空间是介于视窗之外的存储空间，在您的任何视窗中均可使用。';
		}
		doc.select('.cube-face > .face > img').attr('title',title);
		doc.select('.cube-face > .face > p[name]').html(name);
		doc.select('.cube-face > .face > p[num]').html(sws.owner());
	}else{
//		var src="img/disk.svg";
//		var src=String.format("./resource/ud/%s?path=%s://system/faces&u=%s",sws.prop('sws-img'),sws.swsid(),sws.owner());
//		doc.select('.cube-face > .face > img').attr('src',src);
		doc.select('.cube-face > .face > img').attr('title',sws.prop('sws-desc'));
		doc.select('.cube-face > .face > p[name]').html(sws.prop('sws-name'));
		doc.select('.cube-face > .face > p[num]').html(sws.swsid());
	}
	var ds=cube.dataSize();
	
	var per = (ds / cube.config().getCapacity()) * 100;
	var df = new DecimalFormat("#.##");
	var pertext=df.format(per);
	doc.select('.cube-tips > ul.space > li[per] > span[value]').html(pertext);
	
	var title=String.format("约等于:%sk %sm %sg",(ds/1024).toFixed(2),(ds/1024/1024).toFixed(4),(ds/1024/1024/1024).toFixed(8));
	doc.select('.cube-tips > ul.space > li[datasize] > span[value]').html(ds);
	doc.select('.cube-tips > ul.space > li[datasize]').attr('title',title);
	doc.select('.cube-tips > ul.space > li[datasize] > span[unit]').html('字节');
	ds=cube.usedSpace();
	title=String.format("约等于:%sk %sm %sg",(ds/1024).toFixed(2),(ds/1024/1024).toFixed(4),(ds/1024/1024/1024).toFixed(8));
	doc.select('.cube-tips > ul.space > li[spacesize] > span[value]').html(ds);
	doc.select('.cube-tips > ul.space > li[spacesize]').attr('title',title);
	doc.select('.cube-tips > ul.space > li[spacesize] > span[unit]').html('字节');
	ds=cube.config().getCapacity();
	title=String.format("约等于:%sk %sm %sg",(ds/1024).toFixed(2),(ds/1024/1024).toFixed(4),(ds/1024/1024/1024).toFixed(8));
	doc.select('.cube-tips > ul.space > li[capacity] > span[value]').html(ds);
	doc.select('.cube-tips > ul.space > li[capacity]').attr('title',title);
	doc.select('.cube-tips > ul.space > li[capacity] > span[unit]').html('字节');
	
	var set=cube.tupleCoordinate();
	doc.select('.portlet[cube] > .cube-tips > ul.statistics > li[col] > span[value]').html(set.size());
	
	var panel=doc.select('.portlet[cube] > .cube-tips > ul.statistics > li[col] > .panel').first();
	var tuple=panel.select('>.tuple').first().clone();
	panel.empty();
	for(var i=0;i<set.size();i++){
		var coord=set.get(i);
		var li=tuple.clone();
		li.select('>ul>li[name]>span[value]').html(coord.value());
		var t = cube.tupleStats(coord.value());
		li.select('>ul>li[rows]>span[value]').html(t.get('count'));
		li.select('>ul>li[size]>span[value]').html(t.get('size'));
		li.select('>ul>li[space]>span[value]').html(t.get('storageSize'));
		li.select('>ul>li[index]>span[value]').html(t.get('totalIndexSize'));
		panel.append(li);
	}
	var fs=cube.fileSystem();
	doc.select(' .cube-tips > ul.statistics > li[folder] > span[value]').html(fs.dirsTotal());
	doc.select(' .cube-tips > ul.statistics > li[file] > span[value]').html(fs.filesTotal());
	var chunks= fs.coordinateRoot("system_fs_chunks");
	doc.select('.cube-tips > ul.statistics > li[volume] > span[value]').html(chunks.size());
	
	var panel=doc.select('.portlet[cube] > .cube-tips > ul.statistics > li[volume] > .panel').first();
	var tuple=panel.select('>.tuple').first().clone();
	panel.empty();
	for(var i=0;i<chunks.size();i++){
		var coord=chunks.get(i);
		var li=tuple.clone();
		var fileCount=fs.fileCountByCoordinate(
				"system_fs_chunks", coord, true);
		
		var name=coord.value();
		li.select('>ul>li[name]>span[value]').html('卷_'+name.substring(17,name.length));
		var t = cube.tupleStats(coord.value());
		li.select('>ul>li[rows]>span[value]').html(fileCount);
		li.select('>ul>li[count]>span[value]').html(t.get('count'));
		li.select('>ul>li[size]>span[value]').html(t.get('size'));
		li.select('>ul>li[space]>span[value]').html(t.get('storageSize'));
		li.select('>ul>li[index]>span[value]').html(t.get('totalIndexSize'));
		panel.append(li);
	}
}