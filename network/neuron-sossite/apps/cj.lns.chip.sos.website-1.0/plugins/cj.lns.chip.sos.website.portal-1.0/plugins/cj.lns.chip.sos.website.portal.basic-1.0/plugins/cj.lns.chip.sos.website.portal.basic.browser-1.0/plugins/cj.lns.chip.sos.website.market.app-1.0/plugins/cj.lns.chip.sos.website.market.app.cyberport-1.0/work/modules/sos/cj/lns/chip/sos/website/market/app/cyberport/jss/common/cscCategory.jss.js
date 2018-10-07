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
	var doc = m.context().html("/common/cscCategory.html",
			m.site().contextPath(), "utf-8");
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	printMarketChips(home,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printMarketChips(home,doc){
	var cjql="select {'tuple':'*'}.sort({'tuple.ctime':-1}).limit(10) from tuple csc.market java.util.HashMap where {}";
	var q=home.createQuery(cjql);
	var list=q.getResultList();
	var ul=doc.select('.csc-category>.table').first();
	var sli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<list.size();i++){
		var tdoc=list.get(i);
		var tuple=tdoc.tuple();
		var li=sli.clone();
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
}