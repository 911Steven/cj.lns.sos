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
var String = Java.type('java.lang.String');
var CircuitException = Java.type('cj.studio.ecm.graph.CircuitException');
var ServiceosWebsiteModule = Java
		.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var ServicewsBody = Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var FileOutputStream = Java.type('java.io.FileOutputStream');

exports.flow = function(frame, circuit, plug, ctx) {
	var type=frame.head('Content-Type');
	var boundary=type.substring(type.indexOf('boundary=')+9,type.length);
	print(boundary);
	var data=frame.content().readFully();
	
	var fd=new FormData(data,'--'+boundary);//必须在原分隔符上加上--号
	for(var i=0;i<fd.size();i++){
		var f=fd.get(i);
		print(f.getName());
		if(f.isFile()){
			print(f.filename());
			var out=new FileOutputStream('/Users/carocean/Downloads/'+f.filename());
			out.write(f.data());
			out.close();
		}
	}
	circuit.content().writeBytes("{'msg':'200 ok'}".getBytes());
}