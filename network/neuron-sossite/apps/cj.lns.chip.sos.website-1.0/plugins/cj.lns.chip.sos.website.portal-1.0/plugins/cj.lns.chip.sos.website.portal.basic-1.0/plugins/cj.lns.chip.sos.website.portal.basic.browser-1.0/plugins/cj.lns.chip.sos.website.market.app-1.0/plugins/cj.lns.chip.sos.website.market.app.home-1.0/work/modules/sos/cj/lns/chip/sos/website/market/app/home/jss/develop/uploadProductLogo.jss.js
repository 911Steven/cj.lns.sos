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
var BsonDoc = Java.type('org.bson.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var BasicDBObject = Java.type('com.mongodb.BasicDBObject');
var UUID = Java.type('java.util.UUID');
var colName = 'product.entities';

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	if (!sws.isOwner()) {
		throw new CircuitException('503', '只有产品本人才有修改权限');
	}
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	uploadProduct(frame,sws, cube, m, circuit);
}

function uploadProduct(frame,sws, cube, m, circuit) {
	var m = ServiceosWebsiteModule.get();
	var type = frame.head('Content-Type');
	var boundary = type.substring(type.indexOf('boundary=') + 9, type.length);
	var data = frame.content().readFully();
	var fd = new FormData(data, '--' + boundary);// 必须在原分隔符上加上--号
	var p = {};
	for (var i = 0; i < fd.size(); i++) {
		var f = fd.get(i);
		if (f.isFile()) {
			p.file = f;
			continue;
		}
		p[f.getName()] = new String(f.data());
	}
	var sws = IServicewsContext.context(frame);
	saveDisk(p, sws, circuit);
}
function saveDisk(p, sws, circuit) {
	var f = p.file;
	var pid=p.pid;
	var fn = UUID.randomUUID().toString() + '_' + f.filename();
	var m = ServiceosWebsiteModule.get();
	var disk = m.site().diskOwner(sws.owner());
	var cube = disk.cube(sws.swsid());
	var fs = cube.fileSystem();
	var productDir=fs.dir('/products');
	if (!productDir.exists()) {
		productDir.mkdir('我的产品');
	}
	var dir = fs.dir('/products/logos');
	if (!dir.exists()) {
		dir.mkdir('产品图标');
	}
	var file = fs.openFile('/products/logos/' + fn);
	var writer = file.writer(0);
	writer.write(f.data());
	writer.close();
	
	//更新产品中的logo链接
	var filter=BsonDoc.parse(String.format("{_id:ObjectId('%s')}",pid));
	var update=BsonDoc.parse(String.format("{$set:{'tuple.logo':'%s'}}",fn));
	cube.updateDocOne(colName,filter,update);
	
	var src = String.format("./resource/ud/%s?path=%s://products/logos/&u=%s", fn,
			sws.swsid(), sws.owner());
	var map=new HashMap();
	map.put('src',src);
	map.put('fn',f.filename());
	map.put('rfn',fn);
	circuit.content().writeBytes(new Gson().toJson(map).getBytes());
}