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

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/common/cscMarket.html",
			m.site().contextPath(), "utf-8");
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	var market=fs.dir('/market');
	printMarketCategory(market,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printMarketCategory(market,doc){
	var ul=doc.select('.market > .category > ul').first();
	var cli=ul.select('>li').first().clone();
	ul.empty();
	cli.removeClass('cat-selected');
	var dirs=market.listDirs();
	for(var i=0;i<dirs.size();i++){
		var dir=dirs.get(i);
		//print(dir.name()+' '+dir.dirName()+' '+dir.path());
		var  li=cli.clone();
		li.attr('path',dir.path());
		li.select('>span[label]').html(dir.name());
		if(i==0){
			li.addClass('cat-selected');
		}
		ul.append(li);
	}
}