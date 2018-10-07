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
var URLDecoder = Java.type('java.net.URLDecoder');
var ArrayList = Java.type('java.util.ArrayList');
var BsonDocument =Java.type('org.bson.Document');
var HashMap=Java.type('java.util.HashMap');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/common/cscMarketChips.html",
			m.site().contextPath(), "utf-8");
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	var path=frame.parameter('path');
	path=URLDecoder.decode(path);
	var market=fs.dir(path);
	var totolInstallMap=getTotolInstallMap(home,market);
	var skip=frame.parameter('skip');
	if(StringUtil.isEmpty(skip)){
		skip=0;
	}else{
		skip=parseInt(skip);
	}
	printMarketChips(home,market,totolInstallMap,skip,doc);
	printMoreInstalledMarketChips(home,market,doc);
	if(skip==0){
		circuit.content().writeBytes(doc.toString().getBytes());
	}else{
		var items=doc.select('.table').html();
		circuit.content().writeBytes(items.getBytes());
	}
}
function getTotolInstallMap(home,market){
	var pipelines = new ArrayList();
	pipelines.add(BsonDocument.parse(String.format("{$match:{'tuple.market':'%s'}}",market.path())));
	pipelines.add(BsonDocument.parse(
	"{$group:{_id:'$tuple.marketid',count:{'$sum':1}}}"));
	var result = home.aggregate('csc.market.relatives', pipelines);
	var it = result.iterator();
	var map=new HashMap();
	while(it.hasNext()){
		var t=it.next();
		map.put(t.get('_id'),t.get('count'));
	}
	return map;
}
function printMoreInstalledMarketChips(home,market,doc){
	var cjql=String.format("select {'tuple.marketid':1}.distinct().limit(10).skip(0) from tuple csc.market.relatives java.lang.String where {'tuple.market':'%s'}",market.path());
	var q=home.createQuery(cjql);
	var list=q.getResultList();
	var ids=new ArrayList();
	for(var i=0;i<list.size();i++){
		var tdoc=list.get(i);
		var id=String.format("ObjectId('%s')",tdoc.tuple());
		ids.add(id);
	}
	var ul=doc.select('.zdxz > ul.morest').first();
	var sli=ul.select('>li').first().clone();
	ul.empty();
	cjql=String.format("select {'tuple':'*'}.sort({'tuple.ctime':-1}).limit(10).skip(0) from tuple csc.market java.util.HashMap where {'_id':{$in:%s}}",ids);
	q=home.createQuery(cjql);
	list=q.getResultList();
	for(var i=0;i<list.size();i++){
		var tdoc=list.get(i);
		var tuple=tdoc.tuple();
		var li=sli.clone();
		li.attr('marketid',tdoc.docid());
		li.select('>p[name]').html(tuple.name);
		li.select('>p[path]').html(tuple.market);
		var src=tuple.icon;
		if(src==null||src=='#'){
			li.select('>img').attr('style','display:none;');
		}else{
			src=String.format("./resource/dd/%s?path=home://market-icons",src);
			li.select('>img').attr('src',src);
		}
		ul.append(li);
	}
}
function printMarketChips(home,market,map,skip,doc){
	var limit=10;
	var path=market.path();
	var cjql=String.format("select {'tuple':'*'}.sort({'tuple.ctime':-1}).limit(%s).skip(%s) from tuple csc.market java.util.HashMap where {'tuple.market':'%s'}",limit+'',skip+'',path);
	var q=home.createQuery(cjql);
	var list=q.getResultList();
	var ul=doc.select('.table').first();
	var sli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<list.size();i++){
		var tdoc=list.get(i);
		var tuple=tdoc.tuple();
		var li=sli.clone();
		var marketid=tdoc.docid();
		if(map.containsKey(marketid)){
			li.select('>.op>li>span[value]').html(map.get(marketid));
		}else{
			li.select('>.op>li>span[value]').html('0');
		}
		li.attr('marketid',tdoc.docid());
		li.select('>.face>li[name]>span[label]').html(tuple.name);
		li.select('>.face>li[v]>span[label]').html(tuple.market);
		var src=tuple.icon;
		if(src==null||src=='#'){
			li.select('>img').attr('style','display:none;');
		}else{
			src=String.format("./resource/dd/%s?path=home://market-icons",src);
			li.select('>img').attr('src',src);
		}
		ul.append(li);
	}
	
	if(skip==0){
		cjql=String.format("select {'tuple':'*'}.count() from tuple csc.market java.lang.Long where {'tuple.market':'%s'}",path);
		q=home.createQuery(cjql);
		var count=q.count();
		if(count>limit){
			skip=skip+limit;
			var more=String.format("<li class='cimore' path='%s' skip='%s' style='display:block;'>more...</li>",path,skip+'');
			ul.append(more);
		}
	}else{
		if(!list.isEmpty()){
			skip=skip+limit;
			var more=String.format("<li class='cimore' path='%s' skip='%s' style='display:block;'>more...</li>",path,skip+'');
			ul.append(more);
		}
	}
	
}