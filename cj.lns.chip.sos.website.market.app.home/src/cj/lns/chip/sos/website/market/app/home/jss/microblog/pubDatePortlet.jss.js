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
var Date = Java.type('java.util.Date');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	
	var doc = m.context().html("/microblog/pubDatePortlet.html",
			m.site().contextPath(), "utf-8");
	printPubDates(doc,sws);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printPubDates(doc,sws){
	var m = ServiceosWebsiteModule.get();
	var disk=m.site().diskOwner(sws.owner());
	if(!disk.existsCube(sws.swsid())){
		throw new CircuitException('404','视窗空间不存在');
	}
	var cube=disk.cube(sws.swsid());
	
	var dates=doc.select('.portlet[mydate] > .dates').first();
	var yearli=dates.select('>li.year').first().clone();
	dates.empty();
	
	var members=cube.rootCoordinates('article.entities','createDate');
	for(var i=0;i<members.length;i++){
		yearli=yearli.clone();
		var coord=members.get(i);
		yearli.select('>div>span[label]').html(coord.value()+'&nbsp;年');
		var yearCount=cube.listTuplesByCoordinate('article.entities', "createDate",coord,true);
		yearli.select('>div>span[v]').html('('+yearCount+'&nbsp;)');
		var monthul=yearli.select('>ul').first();
		var monthli=monthul.select('li.month').first().clone();
		monthul.empty();
		var childs = cube.childCoordinates(
				"article.entities", "createDate", coord);
		for (var j=0;j<childs.size();j++) {
			monthli=monthli.clone();
			var c=childs.get(j);
			monthli.select('>div>span[label]').html(c.value()+'&nbsp;月');
			var monthCount=cube.listTuplesByCoordinate('article.entities', "createDate",c,true);
			monthli.select('>div>span[v]').html('('+monthCount+'&nbsp;)');
			var dayul=monthli.select('>ul').first();
			var dayli=dayul.select('li.day').first().clone();
			dayul.empty();
			var day = cube.childCoordinates(
					'article.entities', "createDate", c);
			for (var k=0;k<day.size();k++) {
				dayli=dayli.clone();
				var d=day.get(k);
				dayli.select('>div>span[label]').html(d.value()+'&nbsp;日');
				var dayCount=cube.listTuplesByCoordinate('article.entities', "createDate",d,true);
				dayli.select('>div>span[v]').html('('+dayCount+'&nbsp;)');
				dayli.attr('path',d.depCopyFromEnd().toPath());
				dayul.appendChild(dayli);
			}
			monthli.attr('path',c.depCopyFromEnd().toPath());
			monthul.appendChild(monthli);
		}
		yearli.attr('path',coord.depCopyFromEnd().toPath());
		dates.appendChild(yearli);
	}
}
