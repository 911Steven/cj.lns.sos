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
var String = Java.type('java.lang.String');
var CircuitException = Java.type('cj.studio.ecm.graph.CircuitException');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var Document = Java.type('org.jsoup.nodes.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var File = Java.type('java.io.File');
var FileOutputStream = Java.type('java.io.FileOutputStream');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var JavaUtil = Java.type('cj.ultimate.util.JavaUtil');
var FileWriter = Java.type('java.io.FileWriter');
var BufferedWriter = Java.type('java.io.BufferedWriter');
var FileHelper = Java.type('cj.ultimate.util.FileHelper');
var IServicewsContext = Java
.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var ServiceosWebsiteModule = Java
.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var Date = Java.type('java.util.Date');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var  BsonDocument = Java.type('org.bson.Document');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var doc = m.context().html("/common/cscMenuAndToolsInfo.html",
			m.site().contextPath(), "utf-8");
	var installTable=getInstallTable(sws.owner(),m);
	var chipStatus=null;
	var computerStatus=getComputerStatus(sws.owner(),m);
	if(computerStatus){
		chipStatus=getChipStatus(sws.owner(),m)
	}
	var tab=frame.parameter('tab');
	var atid=frame.parameter('atid');
	var at =getAt(tab,atid,sws,m);
	printInstallTable(installTable,chipStatus,tab,at,sws,doc);
	
	circuit.content().writeBytes(doc.toString().getBytes());
}
function getAt(tab,atid,sws,m){
	var disk=m.site().diskOwner(sws.owner());//将csc的有关自定制的东西都放到他的主空间中去
	var home=disk.home();
	
	var cjql=String.format("select {'tuple':'*'} from tuple %s java.util.HashMap where {'_id':ObjectId('%s')}",tab=='tool'?'csc.tool':'csc.app',atid);
	var q=home.createQuery(cjql);
	var tdoc=q.getSingleResult();
	var tuple=tdoc.tuple();
	tuple.atid=tdoc.docid();
	return tuple;
}
function getInstallTable(owner,m){
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(owner);
	if(computer==null){
		throw new CircuitException('404','用户：'+developer+' 没有云计算机');
	}
	var deploy=computer.getDeploy();
	var f = new Frame("get /deploy/getChipInstallTable.service csc/1.0");
	f.head('csc-visitor',owner);
	f.head('dest-address',deploy);
	f.parameter('computer-owner',computer.owner);
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	var json=new String(back.content().readFully());
	return new Gson().fromJson(json,HashMap.class);
}

function printInstallTable(installTable,chipStatus,tab,at,sws,doc){
	var cscinfo=doc.select('.csc-s-info');
	
	cscinfo.attr('atid',at.atid);
	doc.select('.csc-s-info > .header > .pad > li[name]').html(at.name);
	var src='#';
	if(tab=='tool'){
		src=String.format("./resource/ud/%s?path=home://system/csc/tool&u=%s",at.icon,sws.owner());
		doc.select('.csc-s-info > .header > img').attr('src',src);
	}else{
		doc.select('.csc-s-info > .header > img').remove();
	}
	var binder=at.binder;
	if(binder!=null){
		doc.select('.csc-s-info > .header > .pad > li[info] > span[chip]').html(binder.chipname);
		doc.select('.csc-s-info > .header > .pad > li[info] > span[service]').html(binder.csctitle);
		var csctype='';
		switch(binder.csctype){
		case 'tool':
			csctype='工具';
			break;
		case 'bflow':
			csctype='业务流';
			break;
		case 'site':
			csctype='web3.0网站';
			break;
		}
		doc.select('.csc-s-info > .header > .pad > li[info] > span[csctype]').html('('+csctype+')');
		cscinfo.attr('bind-marketid',binder.marketid);
		cscinfo.attr('bind-csckey',binder.csckey);
		cscinfo.attr('bind-csctype',binder.csctype);
		cscinfo.attr('tab',tab);
	}else{
		doc.select('.csc-s-info > .header > p[unbind]').attr('style','display:none;');
		doc.select('.csc-s-info > .header > .pad > li[info] > span[chip]').html('-');
		doc.select('.csc-s-info > .header > .pad > li[info] > span[csctype]').html('');
		doc.select('.csc-s-info > .header > .pad > li[info] > span[service]').html('-');
	}
	
	var select=doc.select('.csc-s-info > .body > ul > li > select').first();
	var option=select.select('>option').first().clone();
	select.empty();
	select.append("<option value='-----'>&nbsp;</option>");
	var it=installTable.entrySet().iterator();
	while(it.hasNext()){
		var kv=it.next();
		var marketid=kv.getKey();
		var chip=kv.getValue();
		if(chipStatus!=null&&chipStatus.containsKey(chip.name)){
			var op=option.clone();
			op.attr('value',marketid);
			op.attr('listtype',tab);
			op.html(chip.name);
			select.append(op);
		}
	}
}
function getComputerStatus(owner,m){
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(owner);
	if(computer==null){
		throw new CircuitException('404','用户：'+developer+' 没有云计算机');
	}
	var deploy=computer.getDeploy();
	var f = new Frame("get /deploy/getComputerStatus.service csc/1.0");
	f.head('dest-address',deploy);
	f.head('csc-visitor',computer.owner);
	f.parameter('computer-owner',computer.owner);
	
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	var status=back.head('csc-computer-status');
	return status=='running';
}
function getChipStatus(owner,m){
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(owner);
	if(computer==null){
		throw new CircuitException('404','用户：'+developer+' 没有云计算机');
	}
	var f = new Frame("get /helloworld/getChipStatus.service csc/1.0");
	f.head('csc-visitor',owner);
	f.head('dest-address',computer.cscHost);
	f.head('csc-customer',computer.cscCustomer);
	f.parameter('computer-owner',computer.owner);
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	var json=new String(back.content().readFully());
	return new Gson().fromJson(json,HashMap.class);
}