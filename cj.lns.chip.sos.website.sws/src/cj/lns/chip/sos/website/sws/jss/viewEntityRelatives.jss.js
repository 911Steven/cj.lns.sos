/*
 * 说明：
 * 用途：查看实体的相关，如访问者列表、下载者列表等。
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
var BsonDocument = Java.type('org.bson.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var LinkedHashMap= Java.type('java.util.LinkedHashMap');
var Mailbox = Java.type('cj.studio.ecm.sns.mailbox.Mailbox');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');
var ServicewsBody=Java.type('cj.lns.chip.sos.service.sws.ServicewsBody');
var Face=Java.type('cj.lns.chip.sos.website.framework.Face');
var ByteArrayOutputStream=Java.type('java.io.ByteArrayOutputStream');
var URLDecoder =Java.type('java.net.URLDecoder');
var limit=10;

exports.flow = function(frame, circuit, plug, ctx) {
	var sws=IServicewsContext.context(frame);
	var m = ServiceosWebsiteModule.get();
	
	var user=frame.parameter('user');
	var swsid=frame.parameter('swsid');
	var entity=frame.parameter('entity');
	var sourceid=frame.parameter('sourceid');
	var kind=frame.parameter('kind');
	var skip=frame.parameter('skip');
	if(StringUtil.isEmpty(skip)){
		skip='0';
	}
	entity=URLDecoder.decode(entity);
	sourceid=URLDecoder.decode(sourceid);
	kind=URLDecoder.decode(kind);
	
	if(StringUtil.isEmpty(user)){
		user=sws.owner();
	}
	if(StringUtil.isEmpty(swsid)){
		swsid=sws.swsid();
	}
	var disk= m.site().diskOwner(user);
	var cube=null;
	if(entity=='homespace'){
		cube=disk.home();
	}else{
		cube=disk.cube(swsid);
	}
	var it=getEntityRelatives(entity,sourceid,kind,skip,cube,frame);
	var doc=m.context().html("/components/entityRelativeViewer.html", m.site().contextPath(), "utf-8");
	
	var count=printRelatives(it,doc);
	if(count==0){//去掉分页标签
		var hidden="<style name='.e-r-v' >.e-r-v>.more{display:none;}</style>"	;
		circuit.content().writeBytes(hidden.getBytes());
		return;
	}else{
		var pos=parseInt(skip)+count;
		doc.select('.e-r-v>.more').attr('skip',pos);
	}
	if(skip=='0'){
		if(count<limit){
			doc.select('.e-r-v>.more').remove();
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}else{
		circuit.content().writeBytes(doc.select('.e-r-v>.events').html().getBytes());
	}
}
function printRelatives(it,doc){
	var ul=doc.select('.e-r-v>.events').first();
	var eli=ul.select('>li').first().clone();
	ul.empty();
	var count=0;
	var format=new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
	while (it.hasNext()) {
		var t = it.next();
		var li=eli.clone();
		li.attr('user',t.get('_id'));
		li.select('>.face>li[name]>span[label]').html(t.get('_id'));
		var reviewerFace=t.get('reviewerFace');
		var nickname=reviewerFace.get('nick');
		if(nickname!=null){
			li.select('>.face').attr('title',nickname);
		}
		li.select('>.face>li[sign]>span[label]').html(reviewerFace.get('signText'));
		
		li.select('>.done>li[times]>span[value]').html(t.get('count'));
		var src=reviewerFace.get('head');
		if(src!=null&&src.indexOf('guest.svg')<0){
			src=String.format("./resource/ud/%s?path=home://system/img/faces/&u=%s",reviewerFace.get('head'),t.get('_id'));
		}
		li.select('>img').attr('src',src);
		var ctime =t.get('ctime');
		var time=format.format(new Date(ctime));
		li.select('>.done>li[ptime]>span[value]').html(time);
		ul.append(li);
		count++;
	}
	return count;
}
function getEntityRelatives(entity,sourceid,kind,skip,cube,frame){
	var pipelines = new ArrayList();
	var filter=String.format("{$match:{'tuple.entity':'%s','tuple.sourceid':'%s','tuple.kind':'%s'}}",entity,sourceid,kind);
	if(entity=='myproduct'&&kind=='download'){
		var releaseid=frame.parameter('releaseid');
		filter=String.format("{$match:{'tuple.releaseid':'%s','tuple.entity':'%s','tuple.sourceid':'%s','tuple.kind':'%s'}}",releaseid,entity,sourceid,kind);
	}else{
		filter=String.format("{$match:{'tuple.entity':'%s','tuple.sourceid':'%s','tuple.kind':'%s'}}",entity,sourceid,kind);
	}
	pipelines.add(BsonDocument.parse(filter));
	pipelines.add(BsonDocument.parse("{$sort:{'tuple.ctime':-1}}"));
	pipelines.add(BsonDocument.parse(
			"{$group:{_id:'$tuple.reviewer',reviewer:{$first:'$tuple.reviewer'},reviewerFace:{$first:'$tuple.reviewerFace'},ctime:{$first:'$tuple.ctime'},count:{'$sum':1}}}"));
	pipelines.add(BsonDocument.parse("{$skip:"+skip+"}"));
	pipelines.add(BsonDocument.parse("{$limit:"+limit+"}"));
	var result = cube
			.aggregate('entity.relatives', pipelines);
	var it = result.iterator();
	return it;
}
