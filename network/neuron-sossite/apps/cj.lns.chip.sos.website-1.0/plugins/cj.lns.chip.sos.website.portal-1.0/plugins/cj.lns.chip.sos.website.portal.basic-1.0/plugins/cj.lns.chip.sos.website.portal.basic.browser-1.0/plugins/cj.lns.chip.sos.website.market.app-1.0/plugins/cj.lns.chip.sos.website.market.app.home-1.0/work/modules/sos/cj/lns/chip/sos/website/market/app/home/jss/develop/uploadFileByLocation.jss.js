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
var UUID = Java.type('java.util.UUID');
var HashMap = Java.type('java.util.HashMap');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var type = frame.head('Content-Type');
	var boundary = type.substring(type.indexOf('boundary=') + 9, type.length);
	var data = frame.content().readFully();
	var fd = new FormData(data, '--' + boundary);// 必须在原分隔符上加上--号
	var p = {};
	for (var i = 0; i < fd.size(); i++) {
		var f = fd.get(i);
		if (f.isFile()) {
			p.file = f;
			continue;
		}
		p[f.getName()] = new String(f.data());
	}
	var sws=IServicewsContext.context(frame);
	saveDisk(p,sws,circuit,frame.parameter('cube'));
	
}

function saveDisk(p,sws,circuit,cubeName) {
	if(typeof p.location=='undefined'||p.location==null||p.location==''){
		throw new CircuitException('404','缺少参数location');
	}
	var f=p.file;
	var fn=f.filename();
	var full=p.location+'/'+fn;
	var m=ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(sws.owner());
	var cube=null;
	if('home'==cubeName){
		cube=disk.home();
	}else{
		cube=disk.cube(sws.swsid());
	}
	var fs=cube.fileSystem();
	if(fs.existsFile(full)){
		throw new CircuitException('505','文件已存在:'+full);
	}
	var dir=fs.dir(p.location);
	if(!dir.exists()){
		throw new CircuitException('404','路径不存在'+p.location);
	}
	var file=fs.openFile(full);
	var writer=file.writer(0);
	writer.write(f.data());
	writer.close();
	
	var src=String.format("./resource/ud/%s?path=%s:/%s/&u=%s",fn,sws.swsid(),p.location,sws.owner());
	var sourceType='myspace';
	var entity='myspace';
	if('home'==cubeName){
		sourceType='homespace';
		entity='homespace';
		src=String.format("./resource/ud/%s?path=home:/%s/&u=%s",fn,p.location,sws.owner());
	}
	circuit.content().writeBytes(String.format("{'src':'%s','old':'%s','fn':'%s'}",src,f.filename(),fn).getBytes());
	
	var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
	var cnt='上传了文件：'+fn;
	
	var files=[];
	files.push(src);
	
	var source=new HashMap();
	source.put('id',full);
	source.put('entity',entity);
	source.put('title',file.name());
	source.put('cnt-hidden',false);
	source.put('creator',sws.owner());
	source.put('onsws',sws.swsid());
	source.put('cube',sourceType);
	source.put('action','uploadFile');
	source.put('files',files);
	pusher.push(sourceType,cnt,source,sws,m);
}