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
var IServicewsContext=Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var Document = Java.type('org.jsoup.nodes.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var URLDecoder = Java.type('java.net.URLDecoder');
var ArrayList = Java.type('java.util.ArrayList');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');
var BsonDocument =Java.type('org.bson.Document');
var HashMap=Java.type('java.util.HashMap');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var doc = m.context().html("/common/cscChipInfo.html",
			m.site().contextPath(), "utf-8");
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var marketid=frame.parameter('marketid');
	var installTable=getInstallTable(sws.owner(),m);
	printMarketChips(home,marketid,installTable,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function getInstallCount(home,marketid){
	var cjql=String.format("select {'tuple':'*'}.count() from tuple csc.market.relatives java.lang.Long where {'tuple.marketid':'%s'}",marketid);
	var q=home.createQuery(cjql);
	return q.count();
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
function printMarketChips(home,marketid,installTable,doc){
	var times=getInstallCount(home,marketid);
	
	var pipelines = new ArrayList();
	pipelines.add(BsonDocument.parse(String.format("{$match:{'_id':ObjectId('%s')}}",marketid)));
	pipelines.add(BsonDocument.parse(
	"{$group:{_id:'$tuple.product',orgid:{$first:'$_id'},tuple:{$first:'$tuple'},count:{'$sum':1}}}"));
	var result = home.aggregate('csc.market', pipelines);
	var it = result.iterator();
	if(!it.hasNext()){
		throw new CircuitException('404','未发现芯片:'+marketid);
	}
	var tdoc=it.next();
	var tuple=tdoc.tuple;
	
	var format=new SimpleDateFormat("yyyy-MM-dd hh:mm");
	var pdate=format.format(new Date(tuple.ctime));
	doc.select('.cinfo > .face > span[name]').html(tuple.name);
	doc.select('.cinfo > .face>p > span[path]').html(tuple.market);
	doc.select('.cinfo > .face>p > span[count]').html(times);
	
	var button=doc.select('.cinfo > .op > li[status=install]');
	button.attr('marketid',marketid);
	if(installTable.containsKey(marketid)){
		button.html('已安装');
		button.attr('installed','yes');
	}
	
	var view=doc.select('.cinfo > ul.view').first();
	view.select('>li[product]>span[value]').html(tuple.product);
	view.select('>li[version]>span[value]').html(tuple.version);
	view.select('>li[guid]>span[value]').html(tuple.guid);
	view.select('>li[company]>span[value]').html(tuple.company);
	
	var file=home.fileSystem().openFile(String.format("%s%s",tuple.market,tuple.assembly));
	var size=file.dataLength();
	view.select('>li[size]>span[value]').html(size);
	view.select('>li[history]>span[value]').html(tdoc.count);
	view.select('>li[ctime]>span[value]').html(pdate);
	view.select('>li[copyright]>span[value]').html(tuple.copyright);
	view.select('>li[desc]>span[value]').html(tuple.desc);
}