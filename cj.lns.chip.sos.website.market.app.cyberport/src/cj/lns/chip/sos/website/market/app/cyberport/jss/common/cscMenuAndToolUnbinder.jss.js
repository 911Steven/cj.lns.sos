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
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var Document = Java.type('org.jsoup.nodes.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var File = Java.type('java.io.File');
var FileOutputStream = Java.type('java.io.FileOutputStream');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var JavaUtil = Java.type('cj.ultimate.util.JavaUtil');
var FileWriter = Java.type('java.io.FileWriter');
var BufferedWriter = Java.type('java.io.BufferedWriter');
var FileHelper = Java.type('cj.ultimate.util.FileHelper');
var IServicewsContext = Java
.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var ServiceosWebsiteModule = Java
.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var  BsonDocument = Java.type('org.bson.Document');
var  URLDecoder = Java.type('java.net.URLDecoder');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var doc = m.context().html("/common/cscMenuAndToolsInfo.html",
			m.site().contextPath(), "utf-8");
	
	var atid=frame.parameter('atid');
	var tab=frame.parameter('tab');
	var disk=m.site().diskOwner(sws.owner());//将csc的有关自定制的东西都放到他的主空间中去
	var home=disk.home();
	var table='csc.app';
	if(tab=='tool'){
		table='csc.tool';
	}
	var filter=BsonDocument.parse(String.format("{'_id':ObjectId('%s')}",atid));
	var update=BsonDocument.parse("{$unset:{'tuple.binder':1}}");
	home.updateDocOne(table,filter,update);
}

