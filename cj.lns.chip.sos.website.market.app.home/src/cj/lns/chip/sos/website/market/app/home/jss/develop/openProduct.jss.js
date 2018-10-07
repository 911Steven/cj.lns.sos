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
var Binary = Java.type('org.bson.types.Binary');
var HashMap = Java.type('java.util.HashMap');
var System = Java.type('java.lang.System');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var doc = m.context().html("/develop/openProduct.html",
			m.site().contextPath(), "utf-8");
	var sws = IServicewsContext.context(frame);
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	var pid=frame.parameter('id');
	if(pid!=null){
		var cjql="select {'tuple':'*'} from tuple product.entities java.util.HashMap where {'_id':ObjectId('?(pid)')}";
		var q=cube.createQuery(cjql);
		q.setParameter('pid',pid);
		var product=q.getSingleResult();
		if(product==null){
			throw new CircuitException('404', '产品不存在');
		}
		
		visit(product,doc,cube,sws,m);
		printLogo(product,doc,sws,m);
		printAbstract(product,doc,cube,sws,m);
		printLastRelease(product,doc,cube,sws,m);
		if(!sws.isOwner()){
			doc.select('.o-product > .main > .op').remove();
		}
		
	}else{
		doc.select('.o-product > .title > .show').remove();
	}
	circuit.content().writeBytes(doc.toString().getBytes());
}
function visit(product,doc,cube,sws,m){
	var tuple=new HashMap();
	tuple.put('sourceid',product.docid());
	tuple.put('entity','myproduct');
	tuple.put('reviewer',sws.visitor().principal());
	tuple.put('reviewerFace',sws.visitor().face());
	tuple.put('ctime',System.currentTimeMillis());
	tuple.put('kind','see');
	var relativedoc=new TupleDocument(tuple);
	cube.saveDoc('entity.relatives',relativedoc);
}
function printLastRelease(product,doc,cube,sws,m){
	var cjql="select {'tuple':'*'}.sort({'tuple.ctime':-1}).limit(1) from tuple product.versions java.util.HashMap where {'tuple.pid':'?(pid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	var releaseDoc=q.getSingleResult();
	if(releaseDoc==null){
		doc.select('.o-product > .title > .show').remove();
		return;
	}
	var release=releaseDoc.tuple();
	doc.select('.o-product > .title > .show > .release > span[value]').html(release.get('fn'));
	var cnt='';
	if(release.containsKey('content')){
		cnt=release.get('content');
	}
	doc.select('.o-product > .title > .show ').attr('releaseid',releaseDoc.docid());
	var src=String.format("./resource/ud/%s?path=%s://products/&u=%s",release.get('rfn'),sws.swsid(),sws.owner());
	doc.select('.o-product > .title > .show ').attr('file',src);
	doc.select('.o-product > .title > .show > .box > li[issue]>span[value]').html(cnt.split('\n').length);
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'myproduct','tuple.sourceid':'?(pid)','tuple.kind':'download','tuple.releaseid':'?(releaseid)'}";
	q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	q.setParameter('releaseid',releaseDoc.docid());
	doc.select('.o-product > .title > .show > .box > li[download]>span[value]').html(q.count()+'');
	
	
	
	cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'myproduct','tuple.sourceid':'?(pid)','tuple.kind':'see'}";
	q=cube.createQuery(cjql);
	q.setParameter('pid',product.docid());
	var visitors=q.count();
	doc.select('.o-product > .title > .show > .box > li[visitor]>span[value]').html(visitors+'');
}
function printAbstract(product,doc,cube,sws,m){
	var tuple=product.tuple();
	var cnt=tuple.get('abstract');
	if(cnt==null){
		return;
	}
	cnt=cnt.get('$binary');
	var b=cube.decodeBinary(cnt);
	cnt=new String(b);
	doc.select('.o-product > .main > .content').html(String.format('<pre>%s</pre>',cnt));
}
function printLogo(product,doc,sws,m){
	var tuple=product.tuple();
	doc.select('.o-product').attr('pid',product.docid());
	var logo=doc.select('.o-product > .title > .logo');
	logo.select("[editable=product.enname]").attr('has','true');
	if(typeof tuple.logo!='undefined'&&tuple.logo!=null){
		var src = String.format("./resource/ud/%s?path=%s://products/logos/&u=%s", tuple.logo,
				sws.swsid(), sws.owner());
		logo.select('img[editable=product.logo]').attr('src',src);
	}
	logo.select("[editable=product.enname]").html(tuple.get('enname'));
	if(tuple.containsKey('cnname')){
		logo.select("[editable=product.cnname]").html(tuple.get('cnname'));
	}else{
		if(!sws.isOwner()){
			logo.select("[editable=product.cnname]").remove();
		}
	}
	if(!sws.isOwner()){
		logo.select('[editable]').removeAttr('editable');
	}
}
