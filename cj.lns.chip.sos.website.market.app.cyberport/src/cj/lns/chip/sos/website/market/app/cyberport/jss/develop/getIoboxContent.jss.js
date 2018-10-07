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
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var Mailbox = Java.type('cj.studio.ecm.sns.mailbox.Mailbox');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var ioboxid = frame.parameter('ioboxId');
	var disk = m.site().diskOwner(sws.owner());
	var cube = disk.cube(sws.swsid());
	var cjql = String.format("select {'tuple':'*'} from tuple iobox java.util.HashMap where {'_id':ObjectId('%s')}",ioboxid);
	var q = cube.createQuery(cjql);
	var doc=q.getSingleResult();
	var tuple=doc.tuple();
	var source=tuple.get('source');
	var appid=source.get('appId');
	var id=source.get('id');
	
	var ondisk = m.site().diskOwner(tuple.get('sender'));
	var oncube = ondisk.cube(tuple.get('senderOnSws'));
	var cnt=[];
	switch(appid){
	case 'myproduct':
		cjql = String.format("select {'tuple.abstract':1} from tuple product.entities java.util.HashMap where {'_id':ObjectId('%s')}",id);
		q = oncube.createQuery(cjql);
		doc=q.getSingleResult();
		if(doc.tuple().containsKey('abstract')){
			cnt=oncube.decodeBinary(doc.tuple().get('abstract').get('$binary'));
		}
		break;
	case 'myarticle':
		cjql = String.format("select {'tuple.content':1} from tuple article.entities java.util.HashMap where {'_id':ObjectId('%s')}",id);
		q = oncube.createQuery(cjql);
		doc=q.getSingleResult();
		if(doc.tuple().containsKey('content')){
			cnt=doc.tuple().get('content').getBytes();
		}
		break;
	case 'myspace':
		var file=oncube.fileSystem().openFile(id);
		var reader=file.reader(0);
		cnt=reader.readFully();
		reader.close();
		break;
	case 'homespace':
		var file=ondisk.home().fileSystem().openFile(id);
		var reader=file.reader(0);
		cnt=reader.readFully();
		reader.close();
		break;
	case 'mine':
		break;
	}
	
	circuit.content().writeBytes(cnt);
}