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
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var IServicewsContext=Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var ArrayList =Java.type('java.util.ArrayList');
var BsonDocument =Java.type('org.bson.Document');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');
var HashMap = Java.type('java.util.HashMap');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var doc = m.context().html("/common/cscMyPublishChip.html",
			m.site().contextPath(), "utf-8");
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	var market=fs.dir('/market');
	var mselect=doc.select('.mypub>.pub>.path>select').first();
	
	printMarketCategory(market,mselect);
	
	var installTable=getInstallTable(sws.owner(),m);
	printChips(market,installTable,home,doc,sws);
	
	circuit.content().writeBytes(doc.toString().getBytes());
}
function getInstallTable(owner,m){
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(owner);
	if(computer==null){
		throw new CircuitException('404','用户：'+developer+' 没有云计算机');
	}
	var deploy=computer.getDeploy();
	var f = new Frame("get /deploy/getChipInstallTable.service csc/1.0");
	f.head('csc-visitor',owner);
	f.head('dest-address',deploy);
	f.parameter('computer-owner',computer.owner);
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	var json=new String(back.content().readFully());
	return new Gson().fromJson(json,HashMap.class);
}
function printChips(market,installTable,home,doc,sws){
	/* 
	  {$match:{'tuple.creator':'user1'}}
	  {$sort:{'tuple.arriveTime':-1}}
	  {$group:{_id:'$tuple.sid',newestMsg:{$first:'$tuple'},countMsgs:{'$sum':1}}}
	*/
	var pipelines = new ArrayList();
	pipelines.add(BsonDocument.parse(String.format("{$match:{'tuple.creator':'%s'}}",sws.owner())));
	pipelines.add(BsonDocument.parse("{$sort:{'tuple.ctime':-1}}"));
	pipelines.add(BsonDocument.parse(
	"{$group:{_id:'$tuple.product',orgid:{$first:'$_id'},tuple:{$first:'$tuple'},count:{'$sum':1}}}"));
	var result = home
			.aggregate('csc.market', pipelines);
	var it = result.iterator();
	var ul=doc.select('.pb-panel > .mypub > .items').first();
	var cli=ul.select('>li').first().clone();
	ul.empty();
	while(it.hasNext()){
		var t=it.next();
		var tuple=t.tuple;
		var docid=t.orgid+'';
		var li=cli.clone();
		li.attr('docid',docid);
		
		if(installTable.containsKey(docid)){
			var button=li.select('> .ci > .op > li');
			button.html('已安装');
			button.attr('installed','yes');
		}
		li.select('>.face>li[a]>span[label]').html(tuple.name);
		li.select('>.face>li[s]>span[inmarket]').html(tuple.market);
		var src=String.format("./resource/dd/%s?path=home://market-icons",tuple.icon);
		li.select('>img').attr('src',src);
		
		var view=li.select('>.ci>.view').first();
		view.select('>li[guid]>span[value]').html(tuple.guid);
		view.select('>li[product]>span[value]').html(tuple.product);
		view.select('>li[version]>span[value]').html(tuple.version);
		view.select('>li[company]>span[value]').html(tuple.company);
		view.select('>li[history]>span[value]').html(t.count);
		var format=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		var pdate=format.format(new Date(tuple.ctime));
		view.select('>li[ctime]>span[value]').html(pdate);
		var fs=home.fileSystem();
		var file=fs.openFile(String.format("%s%s",tuple.market,tuple.assembly))
		var size=file.dataLength();
		view.select('>li[size]>span[value]').html(size);
		view.select('>li[copyright]>span[value]').html(tuple.copyright);
		view.select('>li[desc]>span[value]').html(tuple.desc);
		ul.append(li);
	}
}
function printMarketCategory(market,mselect){
	var cop=mselect.select('>option').first().clone();
	mselect.empty();
	var dirs=market.listDirs();
	for(var i=0;i<dirs.size();i++){
		var dir=dirs.get(i);
		var  op=cop.clone();
		op.attr('value',dir.path());
		op.html(dir.name());
		mselect.append(op);
	}
}