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
var Assembly=Java.type('cj.studio.ecm.Assembly');
var HashMap = Java.type('java.util.HashMap');
var System = Java.type('java.lang.System');
var File = Java.type('java.io.File');
var FileOutputStream = Java.type('java.io.FileOutputStream');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var IServicewsContext=Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var FileHelper = Java.type('cj.ultimate.util.FileHelper');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	var market=fs.dir('/market');
	var docid=frame.parameter('docid');
	var cjql=String.format("select {'tuple':'*'} from tuple csc.market java.util.HashMap where {'_id':ObjectId('%s')}",docid);
	var q=home.createQuery(cjql);
	var doc=q.getSingleResult();
	var tuple=doc.tuple();
	var file=String.format("%s%s",tuple.market,tuple.assembly);
	var file=fs.openFile(file);
	file.delete();
	var fn=String.format("/market-icons/%s",tuple.icon);
	if(fs.existsFile(fn)){
		file=fs.openFile(fn);
		file.delete();
	}
	home.deleteDoc('csc.market',docid);
}
