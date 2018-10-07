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
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');

function printAbstract(product,doc,m,cube){
	var tuple=product.tuple();
	var cnt=tuple.get('abstract');
	if(cnt==null){
		return;
	}
	cnt=cnt.get('$binary');
	var b=cube.decodeBinary(cnt);
	cnt=new String(b);
	doc.select('pre').html(cnt);
}
function printDocs(product,doc,sws,cube){
	var cjql="select {'tuple':'*'} from tuple product.docs java.util.HashMap where {'tuple.pid':'?(pid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	var list=q.getResultList();
	var ul=doc.select('.p-list > .p-docs').first();
	var docli=ul.select('>.doc').first().clone();
	ul.empty();
	var format=new SimpleDateFormat('yyyy年MM月dd日 hh:mm');
	for(var i=0;i<list.size();i++){
		var li=docli.clone();
		var tuple=list.get(i);
		var art=tuple.tuple();
		li.attr('artid',tuple.docid());
		li.select('>ul>li[title]').html(art.get('title'));
		var cnt=art.get('content');
		if(cnt.length<144){
			li.select('>ul>li[cnt]>span[extends]').remove();
		}else{
			cnt=cnt.substring(0,144);
		}
		if(sws.isOwner()){//列出删除按钮
			li.select('> span[remove]').attr('style','display:none;');
			li.select('> span[edit]').attr('style','display:none;');
		}else{
			li.select('> span[remove]').remove();
			li.select('> span[edit]').remove();
		}
		li.select('>ul>li[cnt]>pre').html(cnt);
		var time=format.format(new Date(art.get('ctime')));
		li.select('>ul>li[desc]>span[value]').html(time);
		ul.append(li);
	}
}
function printVersions(product,doc,sws,cube){
	var cjql="select {'tuple':'*'}.sort({'tuple.ctime':-1}) from tuple product.versions java.util.HashMap where {'tuple.pid':'?(pid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	var list=q.getResultList();
	
	var ul=doc.select('.v-panel > .releases').first();
	var docli=ul.select('>.release').first().clone();
	ul.empty();
	var format=new SimpleDateFormat('yyyy年MM月dd日 hh:mm');
	for(var i=0;i<list.size();i++){
		var li=docli.clone();
		var tuple=list.get(i);
		var release=tuple.tuple();
		li.attr('releaseid',tuple.docid());
		var src=String.format("./resource/ud/%s?path=%s://products/&u=%s",release.get('rfn'),sws.swsid(),sws.owner());
		li.attr('file',src);
		li.select('>.title>span[value]').html(release.get('fn'));
		var time=format.format(new Date(release.get('ctime')));
		li.select('> .memo > span[time]').html(time);
		var cnt=release.get('content');
		li.select('> .stress > li[issues] > div.panel>pre').html(cnt);
		li.select('> .stress > li[issues] > div.panel').attr('style','display:none;');
		var lines='0';
		if(cnt!=''&&cnt!=null){
			lines=cnt.split('\n').length;
		}
		li.select('> .stress > li[issues] > span[value]').html(lines);
		
		cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'myproduct','tuple.sourceid':'?(pid)','tuple.kind':'download','tuple.releaseid':'?(releaseid)'}";
		q=cube.createQuery(cjql);
		q.setParameter('pid',product.docid());
		q.setParameter('releaseid',tuple.docid());
		li.select('> .stress > li[dowloads]>span[value]').html(q.count());
		
		ul.append(li);
	}
}
function printLicense(product,doc,sws,cube){
	var cjql="select {'tuple':'*'}.sort({'tuple.ctime':-1}).limit(1) from tuple product.license java.util.HashMap where {'tuple.pid':'?(pid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	var tuple=q.getSingleResult();
	if(tuple==null){
		doc.html('无声明');
		return;
	}
	doc.select('pre').html(tuple.tuple().get('content'));
}
exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	var pid=frame.parameter('pid');
	var menu = frame.parameter('menu');
	var cjql="select {'tuple':'*'} from tuple product.entities java.util.HashMap where {'_id':ObjectId('?(pid)')}";
	var q=cube.createQuery(cjql);
	q.setParameter('pid',pid);
	var product=q.getSingleResult();
	if(typeof product=='undefined'||product==null){
		throw new CircuitException('404', '产品不存在');
	}
	switch (menu) {
	case 'abstract':
		var doc = m.context().html("/develop/getProductAbstract.html",
				m.site().contextPath(), "utf-8");
		printAbstract(product,doc,m,cube);
		circuit.content().writeBytes(doc.toString().getBytes());
		break;
	case 'docs':
		var doc = m.context().html("/develop/getProductDocs.html",
				m.site().contextPath(), "utf-8");
		printDocs(product,doc,sws,cube);
		circuit.content().writeBytes(doc.toString().getBytes());
		break;
	case 'issues':
		var doc = m.context().html("/develop/getProductIssues.html",
				m.site().contextPath(), "utf-8");
		circuit.content().writeBytes(doc.toString().getBytes());
		break;
	case 'versions':
		var doc = m.context().html("/develop/getProductVersions.html",
				m.site().contextPath(), "utf-8");
		printVersions(product,doc,sws,cube);
		circuit.content().writeBytes(doc.toString().getBytes());
		break;
	case 'license':
		var doc = m.context().html("/develop/getProductLicense.html",
				m.site().contextPath(), "utf-8");
		printLicense(product,doc,sws,cube);
		circuit.content().writeBytes(doc.toString().getBytes());
		break;
	}

}
