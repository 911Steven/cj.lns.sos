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
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var Date = Java.type('java.util.Date');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/develop/myproduct.html",
			m.site().contextPath(), "utf-8");
	var sws = IServicewsContext.context(frame);
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	printList(cube, sws, doc);
	circuit.content().writeBytes(doc.toString().getBytes());
	var dynamicSession=chip.site().getService('$.cj.jss.sos.dynamicSession');
	if(dynamicSession!=null&&typeof dynamicSession!='undefined'){
		var json=dynamicSession.openDynamicSession('myproduct',sws);
		//print('----++++--'+json);
	}
}
function printList(cube, sws, doc) {
	var cjql = "select {'tuple':'*'}.sort({'tuple.ctime':-1}) from tuple product.entities java.util.HashMap where {}";
	var q = cube.createQuery(cjql);
	var list = q.getResultList();
	var ul = doc.select('.my-product > .list').first();
	var orgli = ul.select('>.product').first().clone();
	ul.empty();
	var format=new SimpleDateFormat('yyyy年MM月dd日 hh:mm');
	for (var i = 0; i < list.size(); i++) {
		li = orgli.clone();
		var productDoc = list.get(i);
		var product = productDoc.tuple();
		if(!sws.isOwner()){
			li.select('>span[close]').remove();
		}
		li.attr('pid',productDoc.docid());
		li.select('> .face>p[enname]').html(product.get('enname'));
		if (product.containsKey('cnname')) {
			li.select('> .face>p[cnname]').html(product.get('cnname'));
		} else {
			li.select('> .face>p[cnname]').html('&nbsp;');
		}
		if (product.containsKey('logo')) {
			var src = String.format(
					"./resource/ud/%s?path=%s://products/logos&u=%s", product
							.get('logo'), sws.swsid(), sws.owner());
			li.select('> .face>img').attr('src', src);
		} else {
			li.select('> .face>img').attr('src', 'img/xiangce.svg');
		}
		
		if (product.containsKey('abstract')) {
			var abstr=product.get('abstract');
			if(typeof abstr =='string'){
				if(abstr.length>144){
					abstr=abstr.substring(0,144);
					abstr=abstr+'<span extends>展开...</span>';
				}
				li.select('> .abstract>pre').html(abstr);
			}else{
				var b=abstr.get('$binary');
				abstr=cube.decodeBinary(b);
				abstr=new String(abstr);
				if(abstr.length>144){
					abstr=abstr.substring(0,144);
					abstr=abstr+'<span extends>展开...</span>';
				}
				li.select('> .abstract>pre').html(abstr);
			}
		} else {
			li.select('> .abstract>pre').html('&nbsp;');
		}
		var time=format.format(new Date(product.get('ctime')));
		li.select('> .abstract > .extra>span[value]').html(time);
		
		var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'myproduct','tuple.sourceid':'?(pid)','tuple.kind':'see'}";
		var q=cube.createQuery(cjql);
		q.setParameter('pid',productDoc.docid());
		var visitors=q.count();
		li.select('> .face > p[visitor]>span[value]').html(visitors);
		
		ul.append(li);
	}
}