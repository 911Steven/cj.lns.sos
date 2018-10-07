/*
 * 说明：文章门户列表
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
var Date = Java.type('java.util.Date');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var HashMap=Java.type('java.util.HashMap');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/microblog/myarticle.html",
			m.site().contextPath(), "utf-8");
	var categoryId=frame.parameter('categoryId');
	var skip=frame.parameter('skip');
	var limit=frame.parameter('limit');
	if(typeof limit=='undefined'||limit==''||limit==null){
		limit=10;
	}
	if(typeof skip=='undefined'||skip==''||skip==null){
		skip=0;
	}
	
	var cdPath=frame.parameter('cdPath');
	var sws=IServicewsContext.context(frame);
	if(typeof cdPath!='undefeind'&&cdPath!=null&&cdPath!=''){
		var articles=getArticlesByCdPath(cdPath,sws);
		printArticles(doc,articles,sws);
		circuit.content().writeBytes(doc.toString().getBytes());
	}else{
		var articles=getArticles(categoryId,skip,limit,sws);
		printArticles(doc,articles,sws);
		circuit.content().writeBytes(doc.toString().getBytes());
	}
	var dynamicSession=chip.site().getService('$.cj.jss.sos.dynamicSession');
	if(dynamicSession!=null&&typeof dynamicSession!='undefined'){
		var json=dynamicSession.openDynamicSession('myarticle',sws);
		//print('----++++--'+json);
	}
}
function getArticlesByCdPath(cdPath,sws){
	var m = ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(sws.swsid());
	var cds="{'createDate':'"+cdPath+"'}";
	var coordmap = cube.parseCoordinate(cds);
	var docs=cube.listTuplesByCoordinate(
			'article.entities', HashMap.class,coordmap,"{'tuple.createTime':-1}",0,-1, true);
	return docs;
}
function getArticles(categoryId,skip,limit,sws){
	var m = ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(sws.swsid());
	var cjql=String.format("select {'tuple':'*'}.limit(%s).skip(%s).sort({'tuple.createTime':-1}) from tuple article.entities java.util.HashMap where {'tuple.categoryId':?(categoryId)}",limit,skip);
	var q=cube.createQuery(cjql);
	if(typeof categoryId!='undefined'&&categoryId!=null&&categoryId!=''){
		q.setParameter('categoryId',"'"+categoryId+"'");
	}else{
		q.setParameter('categoryId',"{$exists:true}");
	}
	var docs=q.getResultList();
	return docs;
}
function printArticleToolbar(articleDoc,art,artli,sws){
	var m = ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(sws.swsid());
	
	var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.kind':'comment','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('artid',articleDoc.docid());
	var count=q.count();
	artli.select('.body > .op > ul > li[action=forum] > span[value]').html(String.format('(%s)',count));
	
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.kind':'great','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	q=cube.createQuery(cjql);
	q.setParameter('artid',articleDoc.docid());
	count=q.count();
	artli.select('.body > .op > ul > li[action=great] > span[value]').html(String.format('(%s)',count));
	
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.kind':'follow','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	q=cube.createQuery(cjql);
	q.setParameter('artid',articleDoc.docid());
	count=q.count();
	artli.select('.body > .op > ul > li[action=follow] > span[value]').html(String.format('(%s)',count));
	
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.kind':'see','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	q=cube.createQuery(cjql);
	q.setParameter('artid',articleDoc.docid());
	count=q.count();
	artli.select('.body > .second>span[visits]>span[label]').html(String.format('浏览 (%s)',count));
}
function printArticles(doc,articles,sws){
	if(articles.size()<10){
		doc.select('.art-panel > .more').remove();
	}
	var ul=doc.select('.art-panel > .art-ul').first();
	var li=ul.select('>.article').first().clone();
	ul.empty();
	for(var i=0;i<articles.length;i++){
		li=li.clone();
		var articleDoc=articles.get(i);
		var art=articleDoc.tuple();
		printArticleToolbar(articleDoc,art,li,sws);
		li.attr('id',articleDoc.docid());
		li.select('>.body>.title>span[label]').html(art.get('title'));
		var createTime=art.get('createTime');
		var date=new Date(createTime);
		var format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
		li.select('>.body>.second>span[time]').html(format.format(date));
		
		var content=art.get('content');
		if(typeof content=='undefined'||content==null||content==''){
			content="&nbsp;"
		}
		if(content.length>140){
			content=content.substring(0,140)+'&nbsp;&nbsp;...';
		}
		content.replace('\r\n','<br>');
		content="<p>"+content+"</p>";
		li.select('>.body>.content').html(content);
		var pics=0;
		if(art.containsKey('pictures')){
			pics= art.get('pictures').length;
		}
		li.select('>.body>.atten>span[pics][v]').html(pics+'&nbsp;个');
		var medias=0;
		if(art.containsKey('medias')){
			medias= art.get('medias').length;
		}
		li.select('>.body>.atten>span[medias][v]').html(medias+'&nbsp;个');
		var files=0;
		if(art.containsKey('files')){
			files= art.get('files').length;
		}
		li.select('>.body>.atten>span[files][v]').html(files+'&nbsp;个');
		ul.appendChild(li);
		
	}
}