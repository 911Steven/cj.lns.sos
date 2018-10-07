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
var colName='entity.relatives';

function getFollows(cube,owner){
	var cjql=String.format("select {'tuple':'*'}.sort({'tuple.ctime':-1}) from tuple ?(colName) java.util.HashMap where {'tuple.kind':'follow','tuple.reviewer':'%s'}",owner);
	var q=cube.createQuery(cjql);
	q.setParameter('colName',colName);
	return q.getResultList();
}
function getEntity(entity,sourceid,reviewer){
	var m = ServiceosWebsiteModule.get();
	var disk = m.site().diskOwner(reviewer);
	//var cube = disk.cube(sws.swsid());
	print('------');
}
function printView(follows,doc){
	var ul=doc.select('.fllow-panel > .fllow-ul').first();
	var articleli=ul.select('>.article').first().clone();
	ul.empty();
	for(var i=0;i<follows.size();i++){
		var li=articleli.clone();
		var item=follows.get(i);
		var sourceid=item.tuple().get('sourceid');
		var entity=item.tuple().get('entity');
		var iobox=item.tuple().get('iobox');
		if(iobox==null)continue;
		var face=item.tuple().get('reviewerFace');
		var reviewer=item.tuple().get('reviewer');
		var iobox= getIobox(entity,sourceid,reviewer);
		
		
		li.select(' > .body > .second > span[user]').html(reviewer);
		
		ul.append(li);
	}
}
exports.flow = function(frame, circuit, plug, ctx) {
	var sws = IServicewsContext.context(frame);
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/develop/like.html",
			m.site().contextPath(), "utf-8");
	var disk = m.site().diskOwner(sws.owner());
	var cube = disk.cube(sws.swsid());
	var follows=getFollows(cube,sws.owner());
	printView(follows,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}