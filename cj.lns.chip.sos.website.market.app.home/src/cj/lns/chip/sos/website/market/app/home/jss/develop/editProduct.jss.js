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
var BsonDoc=Java.type('org.bson.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var BasicDBObject = Java.type('com.mongodb.BasicDBObject');
var colName='product.entities';

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
//	if(!sws.isOwner()){
//		throw new CircuitException('503', '只有产品本人才有修改权限');
//	}
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	var map=WebUtil.parserParam(new String(frame.content().readFully()));
	var pid=map.get('pid');
	if(typeof pid=='undefined'||pid==''||pid==null||pid=='undefined'){
		//新建一个产品
		createProduct(map,sws,cube,m,circuit);
	}else{
		//更新产品
		updateProduct(map,sws,cube,m,circuit);
	}
}
function createProduct(map,sws,cube,m,circuit){
	var editor=map.get('editor');
	if('product.enname'!=editor){
		throw new CircuitException('503', '必须先设定产品的英文名');
	}
	var enname=map.get('value');
	var cjql="select {'tuple':'*'}.count() from tuple app.menus java.lang.Long where {'tuple.name':'?(enname)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('enname',enname);
	if(q.count()>0){
		throw new CircuitException('503', String.format('产品:%s 已存在',enname));
	}
	var store=new HashMap();
	store.put('enname',map.get('value'));
	store.put('ctime',System.currentTimeMillis());
	var doc=new TupleDocument(store);
	var id=cube.saveDoc(colName,doc);
	//{'id':'site.portal.develop.myproduct.dotnet','name':'dotnet','cmd':'./swssite/develop/myproduct.html?id=22','icon':'#','desc':'','appid':'site.portal.develop.myproduct'}
	var menumap=new HashMap();
	menumap.put('name',enname);
	menumap.put('id',String.format('site.portal.develop.myproduct.%s',enname));
	menumap.put('cmd',String.format('./swssite/develop/openProduct.html?id=%s',id));
	menumap.put('icon',String.format('%s','#'));
	menumap.put('desc','');
	menumap.put('appid','site.portal.develop.myproduct');
	var menu=new TupleDocument(menumap);
	cube.saveDoc('app.menus',menu);
	circuit.content().writeBytes(String.format("{\"id\":\"%s\"}",id).getBytes());
	
	
}
function updateProduct(map,sws,cube,m,circuit){
	var editor=map.get('editor');
	var value=map.get('value');
	var id=map.get('pid');
	switch(editor){
	case 'product.entitiy.delAllInfo':
		
		cube.deleteDocs('entity.relatives',String.format("{'tuple.entity':'myproduct','tuple.sourceid':'%s'}",id));
		cube.deleteDocs('product.license',String.format("{'tuple.pid':'%s'}",id));
		cube.deleteDocs('product.docs',String.format("{'tuple.pid':'%s'}",id));
		cube.deleteDocs('product.versions',String.format("{'tuple.pid':'%s'}",id));
		var cjql=String.format("select {'tuple':'*'} from tuple product.entities java.util.HashMap where {'_id':ObjectId('%s')}",id);
		var q=cube.createQuery(cjql);
		var result=q.getSingleResult();
		if(result!=null){
			cube.deleteDocs('app.menus',String.format("{'tuple.id':'site.portal.develop.myproduct.%s','tuple.appid':'site.portal.develop.myproduct'}",result.tuple().get('enname')));
		}
		cube.deleteDoc('product.entities',id);
		break;
	case 'product.enname':
		var filter=BsonDoc.parse(String.format("{_id:ObjectId('%s')}",id));
		var update=BsonDoc.parse(String.format("{$set:{'tuple.enname':'%s'}}",value));
		cube.updateDocOne(colName,filter,update);
		break;
	case 'product.cnname':
		var filter=BsonDoc.parse(String.format("{_id:ObjectId('%s')}",id));
		var update=BsonDoc.parse(String.format("{$set:{'tuple.cnname':'%s'}}",value));
		cube.updateDocOne(colName,filter,update);
		break;
	case 'product.abstract':
		var filter=BsonDoc.parse(String.format("{_id:ObjectId('%s')}",id));
		var update=BsonDoc.parse(String.format("{}"));
		var o=new BasicDBObject();
		o.put('tuple.abstract',value.getBytes());
		update.put('$set',o);
		cube.updateDocOne(colName,filter,update);
		
		var cjql="select {'tuple':'*'} from tuple product.entities java.util.HashMap where {'_id':ObjectId('?(pid)')}";
		var q=cube.createQuery(cjql);
		q.setParameter('pid',id);
		var product=q.getSingleResult();
		
		var pusher=chip.site().getService('$.cj.jss.sos.pushDynamicMsg');
		var cnt=value;
		var cntHidden=false;
		if(cnt.length>144){
			cnt=cnt.substring(0,144);
			cntHidden=true;
		}
		var source=new HashMap();
		source.put('id',id);
		source.put('entity','myproduct');
		source.put('title',product.tuple().get('enname'));
		source.put('cnt-hidden',cntHidden);
		source.put('creator',sws.owner());
		source.put('onsws',sws.swsid());
		pusher.push('myproduct',cnt,source,sws,m);
		
		break;
	case 'product.license':
		//license是法律文件，每次修改均保留日志，而显示时以最新一条为准
		var tuple=new HashMap();
		tuple.put('content',value);
		tuple.put('pid',id);
		tuple.put('ctime',System.currentTimeMillis());
		
		var doc=new TupleDocument(tuple);
		
		var docid=cube.saveDoc('product.license',doc);
		tuple.put('id',docid);
		circuit.content().writeBytes(new Gson().toJson(tuple).getBytes());
		return;
	case 'product.docs':
		var title=map.get('title');
		var tuple=new HashMap();
		tuple.put('title',title);
		tuple.put('content',value);
		tuple.put('pid',id);
		tuple.put('ctime',System.currentTimeMillis());
		
		var doc=new TupleDocument(tuple);
		
		var docid=cube.saveDoc('product.docs',doc);
		tuple.put('id',docid);
		circuit.content().writeBytes(new Gson().toJson(tuple).getBytes());
		return;//写回则终止后面的代码
	case 'product.docs.del':
		var artid=map.get('artid');
		cube.deleteDoc('product.docs',artid);
		break;
	case 'product.docs.getCnt':
		var artid=map.get('artid');
		var cjql="select {'tuple.content':1} from tuple product.docs java.util.HashMap where {'_id':ObjectId('?(artid)')}";
		var q=cube.createQuery(cjql);
		q.setParameter('artid',artid);
		var result=q.getSingleResult();
		if(result==null)return;
		
		circuit.content().writeBytes(result.tuple().get('content').getBytes());
		return;
	case 'product.docs.edit':
		var artid=map.get('artid');
		var title=map.get('title');
		var filter=BsonDoc.parse(String.format("{_id:ObjectId('%s')}",artid));
		var update=BsonDoc.parse(String.format("{}"));
		//var o=new BasicDBObject();
		var art=new HashMap();
		art.put('tuple.title',title);
		art.put('tuple.content',value);
		//o.put('tuple.title',title);
		//o.put('tuple.content',value.getBytes());
		
		update.put('$set',BsonDoc.parse(new Gson().toJson(art)));
		cube.updateDocOne('product.docs',filter,update);
		break;
	case 'product.versions':
		var rfn=map.get('rfn');
		var fn=map.get('fn');
		var tuple=new HashMap();
		tuple.put('rfn',rfn);
		tuple.put('fn',fn);
		tuple.put('content',value);
		tuple.put('pid',id);
		tuple.put('ctime',System.currentTimeMillis());
		
		var doc=new TupleDocument(tuple);
		
		var docid=cube.saveDoc('product.versions',doc);
		tuple.put('id',docid);
		circuit.content().writeBytes(new Gson().toJson(tuple).getBytes());
		return;
	case 'product.versions.downloads':
		var releaseid=map.get('releaseid');
		var tuple=new HashMap();
		tuple.put('releaseid',releaseid);
		tuple.put('sourceid',id);
		tuple.put('entity','myproduct');
		tuple.put('reviewer',sws.visitor().principal());
		tuple.put('reviewerFace',sws.visitor().face());
		tuple.put('ctime',System.currentTimeMillis());
		tuple.put('kind','download');
		var doc=new TupleDocument(tuple);
		
		var docid=cube.saveDoc('entity.relatives',doc);
		tuple.put('id',docid);
		circuit.content().writeBytes(new Gson().toJson(tuple).getBytes());
		break;
	case 'product.versions.del':
		var releaseid=map.get('releaseid');
		cube.deleteDoc('product.versions',releaseid);
		cube.deleteDocs('entity.relatives',String.format("{'tuple.entity':'myproduct','tuple.sourceid':'%s','tuple.releaseid':'%s','tuple.kind':'download'}",id,releaseid));
		break;
	case 'product.versions.releaseIssue':
		var releaseid=map.get('releaseid');
		var cjql="select {'tuple.content':1} from tuple product.versions java.util.HashMap where {'_id':ObjectId('?(releaseid)')}";
		var q=cube.createQuery(cjql);
		q.setParameter('releaseid',releaseid);
		var result=q.getSingleResult();
		var doc = m.context().html("/develop/getVersionIssue.html",
				m.site().contextPath(), "utf-8");
		doc.select('.issue>pre').html(result.tuple().get('content'));
		circuit.content().writeBytes(doc.toString().getBytes());
		return;
	}
	circuit.content().writeBytes(String.format("{\"id\":\"%s\"}",id).getBytes());
}
