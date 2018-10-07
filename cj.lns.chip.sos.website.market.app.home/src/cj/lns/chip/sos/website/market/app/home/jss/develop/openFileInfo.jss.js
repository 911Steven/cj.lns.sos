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
	var doc = m.context().html("/develop/myspace.html",
			m.site().contextPath(), "utf-8");
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var path=map.get('path');
	if(StringUtil.isEmpty(path)){
		throw new CircuitException('503','path参数为空');
	}
	
	var detail=doc.select('.cube > .show > .finder > .box > .detail');
	printDetail(isHome,detail,path,cube,sws);
	circuit.content().writeBytes(detail.html().getBytes());
}
function saveRelatives(entity,file,cube,sws){
	var map=new HashMap();
	map.put('swsid',sws.swsid());
	map.put('sourceid',file.fullName());
	map.put('entity',entity);
	map.put('kind','see');
	map.put('ctime',System.currentTimeMillis());
	map.put('reviewer',sws.visitor().principal());
	map.put('reviewerFace',sws.visitor().face());
	var doc=new TupleDocument(map);
	cube.saveDoc('entity.relatives',doc);
}
function printDetail(isHome,detail,path,cube,sws){
	var fs=cube.fileSystem();
	if(!fs.existsFile(path)){
		throw new CircuitException('404','文件不存在:'+path);
	}
	var file=fs.openFile(path);
	var entity=(isHome?'homespace':'myspace');
	
	saveRelatives(entity,file,cube,sws);
	
	var util= chip.site().getService('$.cj.jss.sos.util');
	
	detail.select('>.preview > .area > .title').html(file.name());
	var fullpath='';
	if(isHome){
		fullpath=String.format("./resource/ud/%s?path=home:/%s&u=%s",file.name(),file.parent().path(),sws.owner());
	}else{
		fullpath=String.format("./resource/ud/%s?path=%s:/%s&u=%s",file.name(),sws.swsid(),file.parent().path(),sws.owner());
	}
	var media=detail.select('>.preview > .area >.media');
	media.attr('path',fullpath);
	media.attr('file',path);
	media.attr('entity',entity);
	util.fileMedia(file,fullpath,media);
	
	var sl=file.spaceLength();
	var title=String.format("约等于：%sk %sm %sg",(sl/1024).toFixed(2),(sl/1024/1024).toFixed(4),(sl/1024/1024/1024).toFixed(8));
	detail.select(' > .view > ul > li[space]').attr('title',title);
	detail.select(' > .view > ul > li[space] > span[value]').html(sl);
	
	sl=file.dataLength();
	title=String.format("约等于：%sk %sm %sg",(sl/1024).toFixed(2),(sl/1024/1024).toFixed(4),(sl/1024/1024/1024).toFixed(8));
	detail.select(' > .view > ul > li[size] ').attr('title',title);
	detail.select(' > .view > ul > li[size] > span[value]').html(sl);
	
	var per = (sl / file.spaceLength()) * 100;
	var df = new DecimalFormat("#.##");
	var pertext=df.format(per);
	detail.select(' > .view > ul > li[per] > span[value]').html(pertext);
	
	var cd=file.coordinate('createDate');
	detail.select(' > .view > ul > li[coord] > ul>li[createDate]>span[value]').html(cd.toPath());
	var ud=file.coordinate('updateDate');
	detail.select(' > .view > ul > li[coord] > ul>li[updateDate]>span[value]').html(ud.toPath());
	var ft=file.coordinate('fileType');
	detail.select(' > .view > ul > li[coord] > ul>li[fileType]>span[value]').html(ft.toPath());
	detail.select(' > .view > ul > li[coord] > ul>li[path]>span[value]').html(file.parent().path());
	
	var count=detail.select('>.preview >.count ');
	var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.swsid':'?(swsid)','tuple.entity':'?(entity)','tuple.kind':'download','tuple.sourceid':'?(sourceid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('sourceid',file.fullName());
	q.setParameter('entity',entity);
	q.setParameter('swsid',sws.swsid());
	count.select('>li[downloads]>span[value]').html(q.count());
	
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.swsid':'?(swsid)','tuple.entity':'?(entity)','tuple.kind':'see','tuple.sourceid':'?(sourceid)'}";
	q=cube.createQuery(cjql);
	q.setParameter('sourceid',file.fullName());
	q.setParameter('entity',entity);
	q.setParameter('swsid',sws.swsid());
	count.select('>li[visitors]>span[value]').html(q.count());
}
