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

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var doc = m.context().html("/common/csc.html",
			m.site().contextPath(), "utf-8");
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(sws.owner());
	if(computer==null){
		doc = m.context().html("/common/cscTips.html",
				m.site().contextPath());
		circuit.content().writeBytes(doc.toString().getBytes());
		return;
	}
	var disk=m.site().diskOwner(sws.owner());//将csc的有关自定制的东西都放到他的主空间中去
	var home=disk.home();
	
	var tools=getTools(home);
	printTools(tools,sws,doc);
	
	var menu=getMenu(home);
	var welcome=getWelcome(home);
	printMenu(welcome,menu,sws,doc);
	
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printMenu(welcome,menu,sws,doc){
	var ful=doc.select('.csc-work > .header > .panel[item="service"] > ul').first();
	var fyli=ful.select('>li').first().clone();
	ful.empty();
	while(menu.hasNext()){
		var m=menu.next();
		var fli=fyli.clone();
		var fspan=fli.select('>span[m]');
		fspan.html(m.name);
		fspan.attr('atid',m.atid);
		fspan.attr('developer',sws.owner());
		var binder=m.binder;
		if(binder!=null){
			fspan.attr('chip',binder.chipname);
			fspan.attr('marketid',binder.marketid);
			fspan.attr('csckey',binder.csckey);
			fspan.attr('csctype',binder.csctype);
		}else{
			fspan.attr('style','color:gray;');
		}
		
		var secondul=fli.select('>ul').first();
		var secondli=secondul.select('>li').first().clone();
		secondul.empty();
		var childs=m.childs;
		for(var i=0;i<childs.length;i++){
			var c=childs[i];
			if(c.atid==c.parent)continue;
			var sli=secondli.clone();
			var sspan=sli.select('>span[m]');
			sspan.html(c.name);
			sspan.attr('atid',c.atid);
			sspan.attr('developer',sws.owner());
			var binder=c.binder;
			if(binder!=null){
				sspan.attr('chip',binder.chipname);
				sspan.attr('marketid',binder.marketid);
				sspan.attr('csckey',binder.csckey);
				sspan.attr('csctype',binder.csctype);
			}else{
				sspan.attr('style','color:gray;');
			}
			secondul.append(sli);
		}
		ful.append(fli);
	}
	var welcomeatid=welcome.atid;
	var wc=doc.select('.csc-work > .header > .panel[item=service]>span[welcome]');
	wc.attr('atid',welcomeatid);
	wc.attr('developer',sws.owner());
	var binder=welcome.binder;
	if(binder!=null){
		wc.attr('chip',binder.chipname);
		wc.attr('marketid',binder.marketid);
		wc.attr('csckey',binder.csckey);
		wc.attr('csctype',binder.csctype);
	}else{
		wc.attr('style','color: gray;');
	}
}
function printTools(tools,sws,doc){
	var ul=doc.select('.csc-work>.footer>.push-services>.list').first();
	var tli=ul.select('>li').first().clone();
	ul.empty();
	while(tools.hasNext()){
		var tool=tools.next();
		var li=tli.clone();
		li.select('>p').html(tool.tuple.name);
		var src=tool.tuple.icon;
		if(StringUtil.isEmpty(src)||src=='#'){
			li.select('>img').remove();
		}else{
			src=String.format("./resource/ud/%s?path=home://system/csc/tool&u=%s",src,sws.owner());
			li.select('>img').attr('src',src);
		}
		var binder=tool.tuple.binder;
		li.attr('chip',binder.chipname);
		li.attr('developer',sws.owner());
		li.attr('marketid',binder.marketid);
		li.attr('csckey',binder.csckey);
		li.attr('csctype',binder.csctype);
		ul.append(li);
	}
}
function getWelcome(home){
	var cjql=String.format("select {'tuple':'*'} from tuple csc.app java.util.HashMap where {'tuple.welcome':'true'}");
	var q=home.createQuery(cjql);
	var tdoc=q.getSingleResult();
	if(tdoc==null){
		var map=new HashMap();
		map.put('ctime',System.currentTimeMillis());
		map.put('name','欢迎');
		map.put('sort',0);
		map.put('welcome','true');
		var tuple=new TupleDocument(map);
		var atid=home.saveDoc('csc.app',tuple);
		map.put('parent',atid);
		home.updateDocOne('csc.app',BsonDocument.parse(String.format("{_id:ObjectId('%s')}",atid)),BsonDocument.parse(String.format("{$set:{'tuple.parent':'%s'}}",atid)));
		map.put('atid',atid);
		return map;
	}else{
		var tuple=tdoc.tuple();
		tuple.atid=tdoc.docid();
		return tuple;
	}
}
function getMenu(home){
	var pipeline=new ArrayList();
	pipeline.add(BsonDocument.parse("{$match:{'tuple.welcome':{$ne:'true'}}}"));
	//pipeline.add(BsonDocument.parse("{$sort:{'tuple.sort':1}}"));
	pipeline.add(BsonDocument.parse("{$group:{'_id':'$tuple.parent','atid':{$first:'$_id'},'name':{$first:'$tuple.name'},'binder':{$first:'$tuple.binder'},'ctime':{$first:'$tuple.ctime'},'sort':{$first:'$tuple.sort'},'childs':{$push:{'atid':'$_id','name':'$tuple.name','ctime':'$tuple.ctime','parent':'$tuple.parent','binder':'$tuple.binder'}}}}"));
	//pipeline.add(BsonDocument.parse("{$sort:{'sort':1,'childs.sort':1,'ctime':1}}"));
	var res=home.aggregate('csc.app',pipeline);
	var it=res.iterator();
	return it;
}
function getTools(home){
	var pipeline=new ArrayList();
	pipeline.add(BsonDocument.parse("{$match:{'tuple.binder':{$exists:true}}}"));
	pipeline.add(BsonDocument.parse("{$sort:{'tuple.ctime':1,'tuple.sort':1}}"));
	var res=home.aggregate('csc.tool',pipeline);
	var it=res.iterator();
	return it;
}