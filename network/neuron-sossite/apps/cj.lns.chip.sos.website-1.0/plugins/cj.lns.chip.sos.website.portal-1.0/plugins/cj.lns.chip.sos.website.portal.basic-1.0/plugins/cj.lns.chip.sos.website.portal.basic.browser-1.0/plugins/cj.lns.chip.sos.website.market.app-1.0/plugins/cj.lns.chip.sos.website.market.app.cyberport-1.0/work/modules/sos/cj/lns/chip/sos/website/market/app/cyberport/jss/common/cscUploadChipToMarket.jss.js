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
var Assembly=Java.type('cj.studio.ecm.Assembly');
var HashMap = Java.type('java.util.HashMap');
var System = Java.type('java.lang.System');
var File = Java.type('java.io.File');
var FileOutputStream = Java.type('java.io.FileOutputStream');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var IServicewsContext=Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var FileHelper = Java.type('cj.ultimate.util.FileHelper');
var Graph=Java.type('cj.studio.ecm.graph.Graph');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws=IServicewsContext.context(frame);
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var fs=home.fileSystem();
	var market=fs.dir('/market');
	
	var type=frame.contentType();
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
	saveFile(p,market,fs);
	checkAndSaveChipInfo(p,home,sws);
	circuit.content().writeBytes("{\"status\":\"200\"}".getBytes());
}

function saveFile(p,market,fs){
	var file=p.file;
	var rfile=String.format("%s%s",p.path,file.filename());
	if(fs.existsFile(rfile)){
		throw new CircuitException('503',String.format('在所选市场分类中存在重名芯片：%s，请重命名后再尝试发布。',file.filename()));
	}
	var rf=fs.openFile(rfile);
	var writer=rf.writer(0);
	writer.write(file.data());
	writer.close();
	
}
function checkAndSaveChipInfo(p,home,sws){
	var file=p.file;
	var path=p.path;
	var f = File.createTempFile("csc-market-", ".jar");
	f.deleteOnExit();
	var out=null;
	var ai=null;
	try{
		if(f.exists()){
			f.createNewFile();
		}
		out=new FileOutputStream(f);
		out.write(file.data());
		ai=Assembly.loadAssembly(f);
		
		var chipType=ai.getProperty('csc.chip.type');
		if(chipType==null||(chipType!='message'&&chipType!='business')){
			throw new CircuitException('503','未知的芯片类型，请在芯片的 assembly.properties 文件中指明，如：csc.chip.type=message');
		}
		if(StringUtil.isEmpty(ai.getProduct())){
			throw new CircuitException('503','芯片定义不全，应正指明产品. 在 assembly.json 中的assemblyProduct');
		}
		ai.start();
		var neuron=ai.workbin().part('cj.neuron.app');
		if(neuron==null){
			throw new CircuitException('404',String.format('芯片中未定义：cj.neuron.app 服务。'));
		}
		if(!Graph.class.isAssignableFrom(neuron.getClass())){
			throw new CircuitException('404',String.format('芯片中的：cj.neuron.app 服务不是Graph。'));
		}
		
		var map=new HashMap();
		map.put('name',ai.getName());
		map.put('market',path);
		map.put('creator',sws.owner());
		map.put('version',ai.getVersion());
		map.put('type',chipType);
		map.put('assembly',file.filename());
		map.put('guid',ai.getGuid());
		map.put('company',ai.getCompany());
		map.put('product',ai.getProduct());
		map.put('copyright',ai.getCopyright());
		map.put('desc',ai.getDescription());
		map.put('ctime',System.currentTimeMillis());
		if(ai.getIconStream()!=null){
			var iconraw=FileHelper.readFully(ai.getIconStream());
			var fn=ai.workbin().chipInfo().getIconFileName();
			var iconfile=String.format('%s-%s-%s',map.guid,map.ctime,fn);
			var fs=home.fileSystem();
			var marketsIconsDir=fs.dir('/market-icons/');
			if(!marketsIconsDir.exists()){
				marketsIconsDir.mkdir('市场中芯片的图标');
			}
			var infile=fs.openFile(String.format("/market-icons/%s",iconfile));
			var writer=infile.writer(0);
			writer.write(iconraw);
			writer.close();
			map.put('icon',iconfile);
		}
		var doc=new TupleDocument(map);
		return home.saveDoc('csc.market',doc);
	}catch(e){
		var rfile=String.format("%s%s",p.path,file.filename());
		var fs=home.fileSystem();
		if(fs.existsFile(rfile)){
			var rf=fs.openFile(rfile);
			rf.delete();
		}
		throw new CircuitException('503',e);
	}finally{
		if(out!=null){
			out.close();
		}
		f.delete();
		if(ai!=null){
			try{
			ai.unload();
			}catch(e){
				
			}
		}
	}
	
}