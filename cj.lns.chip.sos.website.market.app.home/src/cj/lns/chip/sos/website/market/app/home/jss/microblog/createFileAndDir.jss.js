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
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var location=map.get('location');
	var op=map.get('op');
	if(StringUtil.isEmpty(location)){
		location="/";
	}
	var fs=cube.fileSystem();
	switch(op){
	case 'createFolder':
		var dirName=map.get('dirName');
		var folderName=map.get('folderName');
		var path=String.format("%s/%s",location,dirName);
		var dir=fs.dir(path);
		if(dir.exists()){
			throw new CircuitException('505','目录已存在:'+dirName);
		}
		dir.mkdir(folderName);
		break;
	case 'createFile':
		var fileName=map.get('fileName');
		var content=map.get('content');
		var path=String.format("%s/%s",location,fileName);
		var file=fs.openFile(path);
		var writer=file.writer(0);
		writer.write(content.getBytes());
		writer.close();
		
		var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
		var cnt=content;
		var cntHidden=false;
		if(cnt.length>144){
			cnt=cnt.substring(0,144);
			cntHidden=true;
		}
		var src=String.format("./resource/ud/%s?path=%s:/%s&u=%s",fileName,sws.swsid(),location,sws.owner());
		var sourceType='myspace';
		var entity='myspace';
		if('home'==frame.parameter('cube')){
			sourceType='homespace';
			myspace='homespace';
			src=String.format("./resource/ud/%s?path=home:/%s&u=%s",fileName,location,sws.owner());
		}
		var files=[];
		files.push(src);
		
		var source=new HashMap();
		source.put('id',path);
		source.put('entity',entity);
		source.put('title',file.name());
		source.put('cnt-hidden',cntHidden);
		source.put('creator',sws.owner());
		source.put('onsws',sws.swsid());
		source.put('cube',sourceType);
		source.put('action','createFile');
		source.put('files',files);
		pusher.push(sourceType,cnt,source,sws,m);
		
		break;
	}
}
function printView(dir,sws,cube,folder){
	
}
