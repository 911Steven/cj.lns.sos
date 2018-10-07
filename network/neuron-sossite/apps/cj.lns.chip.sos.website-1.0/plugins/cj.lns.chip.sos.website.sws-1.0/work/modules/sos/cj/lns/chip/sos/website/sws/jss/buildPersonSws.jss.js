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
var ServicewsBody = Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var FileOutputStream = Java.type('java.io.FileOutputStream');

function saveDisk(p,owner) {
	var f=p.file;
	var m=ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(owner);
	var cube=disk.cube(p.swsid);
	var file=cube.fileSystem().openFile('/system/faces/'+f.filename());
	var writer=file.writer(0);
	writer.write(f.data());
	writer.close();
}
function inheritServicews(p, f,cir) {
	var out = ServiceosWebsiteModule.get().out();
	var subject=ISubject.subject(f);
	var frame = new Frame("buildPersonSws /sws/build sos/1.0");
	var circuit = new Circuit("sos/1.0 200 ok");
	frame.parameter("owner", subject.principal());
	frame.parameter("assingedSwsid", p.swsid);
	frame.parameter("inheritId", p.inheritId);
	frame.parameter("level", p.level);
	frame.parameter("name", p.swsName);
	frame.parameter("capacity", p.capacity);
	//frame.parameter("faceImg", p.file.filename());
	frame.parameter("intro", p.intro);
	frame.parameter("hobby", p.hobby);
	out.flow(frame, circuit);
	var ctx = circuit.content();
	var back = new Frame(ctx.readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), back.head("message"));
	}
	//saveDisk(p,subject.principal());
	cir.content().writeBytes(back.content());
}
function deleteSws(p){
	var out = ServiceosWebsiteModule.get().out();
	var subject=ISubject.subject(f);
	var frame = new Frame("deleteSws /sws/build sos/1.0");
	var circuit = new Circuit("sos/1.0 200 ok");
	frame.parameter("swsid", p.swsid);
	out.flow(frame, circuit);
	var ctx = circuit.content();
	var back = new Frame(ctx.readFully());
	var state = parseInt(back.head("status"));
	if (state != 200) {
		throw new CircuitException(back.head("status"), back.head("message"));
	}
}
exports.flow = function(frame, circuit, plug, ctx) {
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
	try{
		inheritServicews(p,frame,circuit);
	}catch(e){
		try{
		deleteSws(p);
		}catch(e1){
			
		}
		throw new CircuitException('503',e);
	}
}