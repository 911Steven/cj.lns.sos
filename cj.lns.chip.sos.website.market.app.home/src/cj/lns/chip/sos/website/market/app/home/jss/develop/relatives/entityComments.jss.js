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
var System = Java.type('java.lang.System');
var colName='entity.relatives';
var HashMap = Java.type('java.util.HashMap');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');

function createComment(map,cube,sws,circuit,m){
	var sourceid=map.get('sourceid');
	var author=map.get('creator');//作者
	var ioboxid=map.get('ioboxid');
	var entity=map.get('entity');
	if(entity=='mine'){
		throw new CircuitException('503','评论的源实体不能是mine类型');
	}
	var to=map.get('to');
	var comment=map.get('comment');
	var thread=map.get('thread');
	var reviewerface=sws.visitor().face();
	var reviewer=sws.visitor().principal();//评论者
	var ctime=System.currentTimeMillis();//评论时间
	if('@'==to){//评论给文章作者
		to=author;
	}else{
		to=to.substring(1,to.length);
	}
	
	var tuple=new HashMap();
	tuple.put('ioboxid',ioboxid);
	tuple.put('sourceid',sourceid);
	tuple.put('entity',entity);
	tuple.put('thread',thread);//所属的主贴
	tuple.put('reviewer',reviewer);
	tuple.put('reviewerFace',reviewerface);
	tuple.put('to',to);
	tuple.put('prefix','@');
	tuple.put('kind','comment');//评论类型
	tuple.put('comment',comment);
	tuple.put('ctime',ctime);
	var newdoc=new TupleDocument(tuple);
	var commentid=cube.saveDoc(colName,newdoc);
	
	var retmap=new HashMap();
	retmap.put('commentid',commentid);
	retmap.put('reviewerId',reviewer);
	retmap.put('to',to);
	retmap.put('reviewerFace',reviewerface);
	var format=new SimpleDateFormat("hh:mm MM月dd日");
	var timeDisplay=format.format(new Date(ctime));
	retmap.put('ctime',timeDisplay);
	circuit.content().writeBytes(new Gson().toJson(retmap).getBytes());
	
	var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
	var cnt='评论了';
	var source=new HashMap();
	source.put('entity',entity);
	source.put('ioboxid',ioboxid);
	source.put('id',sourceid);
	var onsws=map.get('onsws');
	var creator=map.get('creator');
	source.put('onsws',onsws);
	source.put('creator',creator);
	
	fillIoboxItemToSource(ioboxid,source,m,sws);
	source.put('kind','comment');
	source.put('reviewer',reviewer);
	source.put('reviewerFace',reviewerface);
	pusher.push('mine',cnt,source,sws,m,creator);
}
function fillIoboxItemToSource(ioboxid,source,m,sws){
	var disk=m.site().diskOwner(sws.owner());
	var cube=disk.cube(sws.swsid());
	var cjql=String.format("select {'tuple':'*'} from tuple iobox java.util.HashMap where {'_id':ObjectId('%s')}",ioboxid);
	var q=cube.createQuery(cjql);
	var entity=q.getSingleResult();
	if(entity==null)return;
	var tuple =entity.tuple();

	var out=new ByteArrayOutputStream();
	var body=tuple.get('body');
	for(var j=0;j<body.size();j++){
		out.write(body.get(j));
	}
	out.close();
	var cnt=new String(out.toByteArray());
	source.put('sourceCnt',cnt);
	var src=tuple.get('source');
	if(src.get('title')!=null){
		source.put('sourceTitle',src.get('title'));
	}
	if(src!=null){
		if(src.get('pictures')!=null){
			source.put('pictures',src.get('pictures'));
		}
	}
}
function delComment(map,cube,sws,circuit){
	var commentid=map.get('commentid');
	cube.deleteDoc(colName,commentid);
}
function toggleGreat(map,cube,sws,circuit,m){
	var sourceid=map.get('sourceid');
	var ioboxid=map.get('ioboxid');
	var entity=map.get('entity');
	if(entity=='mine'){
		throw new CircuitException('503','评论的源实体不能是mine类型');
	}
	//返回评论号，评论者的face
	var reviewerface=sws.visitor().face();
	var reviewer=sws.visitor().principal();//评论者
	//看看是否点了赞，如果是已点则取消
	var cjql="select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {'tuple.entity':'?(entity)','tuple.kind':'great','tuple.sourceid':'?(sourceid)','tuple.reviewer':'?(reviewer)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('sourceid',sourceid);
	q.setParameter('entity',entity);
	q.setParameter('colName',colName);
	q.setParameter('reviewer',reviewer);
	var count=q.count();
	if(count>0){//取消
		cube.deleteDocOne(colName,String.format("{'tuple.entity':'%s','tuple.sourceid':'%s','tuple.reviewer':'%s','tuple.kind':'great'}",entity,sourceid,reviewer));
		var retmap=new HashMap();
		retmap.put('reviewerId',reviewer);
		retmap.put('cancel','true');//是点赞还是取消
		retmap.put('reviewerFace',reviewerface);
		circuit.content().writeBytes(new Gson().toJson(retmap).getBytes());
	}else{
		var ctime=System.currentTimeMillis();//评论时间
		
		var tuple=new HashMap();
		tuple.put('sourceid',sourceid);
		tuple.put('ioboxid',ioboxid);
		tuple.put('entity',entity);
		tuple.put('reviewer',reviewer);
		tuple.put('reviewerFace',reviewerface);
		tuple.put('kind','great');//评论类型
		tuple.put('ctime',ctime);
		var newdoc=new TupleDocument(tuple);
		var greatid=cube.saveDoc(colName,newdoc);
		var retmap=new HashMap();
		retmap.put('reviewerId',reviewer);
		retmap.put('cancel','false');//是点赞还是取消
		retmap.put('reviewerFace',reviewerface);
		var format=new SimpleDateFormat("hh:mm MM月dd日");
		var timeDisplay=format.format(new Date(ctime));
		retmap.put('ctime',timeDisplay);
		circuit.content().writeBytes(new Gson().toJson(retmap).getBytes());
		
		var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
		var cnt='赞了';
		var source=new HashMap();
		source.put('ioboxid',ioboxid);
		source.put('entity',entity);
		source.put('id',sourceid);
		var onsws=map.get('onsws');
		var creator=map.get('creator');
		source.put('onsws',onsws);
		source.put('creator',creator);
		fillIoboxItemToSource(ioboxid,source,m,sws);
		source.put('kind','great');
		source.put('reviewer',reviewer);
		source.put('reviewerFace',reviewerface);
		pusher.push('mine',cnt,source,sws,m,creator);
	}
}
function toggleFollow(map,cube,sws,circuit,m){
	var sourceid=map.get('sourceid');
	var ioboxid=map.get('ioboxid');
	var entity=map.get('entity');
	if(entity=='mine'){
		throw new CircuitException('503','评论的源实体不能是mine类型');
	}
	//返回评论号，评论者的face
	var reviewerface=sws.visitor().face();
	var reviewer=sws.visitor().principal();//评论者
	//看看是否关注了，如果是已点则取消
	var cjql="select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {'tuple.entity':'?(entity)','tuple.kind':'follow','tuple.sourceid':'?(sourceid)','tuple.reviewer':'?(reviewer)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('sourceid',sourceid);
	q.setParameter('entity',entity);
	q.setParameter('colName',colName);
	q.setParameter('reviewer',reviewer);
	var count=q.count();
	if(count>0){//取消
		cube.deleteDocOne(colName,String.format("{'tuple.entity':'%s','tuple.sourceid':'%s','tuple.reviewer':'%s','tuple.kind':'follow'}",entity,sourceid,reviewer));
		var retmap=new HashMap();
		retmap.put('cancel','true');//是关注还是取消
		circuit.content().writeBytes(new Gson().toJson(retmap).getBytes());
	}else{
		var ctime=System.currentTimeMillis();//评论时间
		var tuple=new HashMap();
		tuple.put('sourceid',sourceid);
		tuple.put('ioboxid',ioboxid);
		tuple.put('entity',entity);
		tuple.put('reviewer',reviewer);
		tuple.put('reviewerFace',reviewerface);
		tuple.put('kind','follow');//评论类型
		tuple.put('ctime',ctime);
		var newdoc=new TupleDocument(tuple);
		var greatid=cube.saveDoc(colName,newdoc);
		var retmap=new HashMap();
		retmap.put('cancel','false');//是点赞还是取消
		circuit.content().writeBytes(new Gson().toJson(retmap).getBytes());
		
		var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
		var cnt='赞了';
		var source=new HashMap();
		source.put('ioboxid',ioboxid);
		source.put('entity',entity);
		source.put('id',sourceid);
		var onsws=map.get('onsws');
		var creator=map.get('creator');
		source.put('onsws',onsws);
		source.put('creator',creator);
		fillIoboxItemToSource(ioboxid,source,m,sws);
		source.put('kind','follow');
		source.put('reviewer',reviewer);
		source.put('reviewerFace',reviewerface);
		pusher.push('mine',cnt,source,sws,m,creator);
	}
}
function doShare(map,cube,sws,circuit,m){
	
}
exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var sws=IServicewsContext.context(frame);
	var onsws=map.get('onsws');
	var creator=map.get('creator');
	if(creator==null){
		throw new CircuitException('404','缺参数：creator，是实体的创建者一般是发送人');
	}
	if(onsws==null){
		throw new CircuitException('404','缺参数：onsws，是实体的创建者的视窗');
	}
	var disk=m.site().diskOwner(creator);
	if(!disk.existsCube(onsws)){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(onsws);
	var action=map.get('action');
	switch(action){
	case 'comment':
		createComment(map,cube,sws,circuit,m);
		break;
	case 'great':
		toggleGreat(map,cube,sws,circuit,m);
		break;
	case 'follow':
		toggleFollow(map,cube,sws,circuit,m);
		break;
	case 'share':
		doShare(map,cube,sws,circuit,m);
		break;
	case 'delComment':
		delComment(map,cube,sws,circuit);
		break;
	}
	//circuit.content().writeBytes(String.format("{'id':'%s'}",phyId).getBytes())	;
}