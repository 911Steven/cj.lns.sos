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
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/microblog/pubArticleRight.html",
			m.site().contextPath(), "utf-8");
	var ul=doc.select('.portlet[category] > ul').first();
	var li=ul.select('>li').first().clone();
	li.removeAttr('class');
	ul.empty();
	var sws=IServicewsContext.context(frame);
	if(!sws.isOwner()){
		doc.select('.portlet[category] > .op').html('分类');
	}
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(sws.swsid());
	var cjql="select {'tuple':'*'} from tuple article.categories java.util.HashMap where {}";
	var q=cube.createQuery(cjql);
	var list=q.getResultList();
	for(var i=0;i<list.size();i++){
		var the=list.get(i);
		var tuple=the.tuple();
		li=li.clone();
		li.attr('category',the.docid());
		li.select('>span[label]').html(tuple.get('name'));
		if(!sws.isOwner()){
			li.select('>span[x]').remove();
		}
		ul.appendChild(li);
	}
	if(list.size()>0){
		ul.select('>li').first().attr('class','cat-selected');
	}
	circuit.content().writeBytes(doc.toString().getBytes());
}