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
	var disk=m.site().diskOwner(sws.owner());//将csc的有关自定制的东西都放到他的主空间中去
	var home=disk.home();
	var doc = m.context().html("/common/cscMyInstallChip.html",
			m.site().contextPath(), "utf-8");
	var menu=getMenu(home);
	var welcome=getWelcome(home);
	printMenu(welcome,menu,doc);
	var tools=getTools(home);
	printTools(tools,doc,sws);
	var installTable=getInstallTable(sws.owner(),m);
	var chipStatus=null;
	var computerStatus=getComputerStatus(sws.owner(),m);
	if(computerStatus){
		chipStatus=getChipStatus(sws.owner(),m)
	}
	printInstallTable(installTable,chipStatus,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function getWelcome(home){
	var cjql=String.format("select {'tuple':'*'} from tuple csc.app java.util.HashMap where {'tuple.welcome':'true'}");
	var q=home.createQuery(cjql);
	var tdoc=q.getSingleResult();
	if(tdoc==null){
		var map=new HashMap();
		map.put('ctime',System.currentTimeMillis());
		map.put('name','欢迎');
		map.put('sort',0);
		map.put('welcome','true');
		var tuple=new TupleDocument(map);
		var atid=home.saveDoc('csc.app',tuple);
		map.put('parent',atid);
		home.updateDocOne('csc.app',BsonDocument.parse(String.format("{_id:ObjectId('%s')}",atid)),BsonDocument.parse(String.format("{$set:{'tuple.parent':'%s'}}",atid)));
		map.put('atid',atid);
		return map;
	}else{
		var tuple=tdoc.tuple();
		tuple.atid=tdoc.docid();
		return tuple;
	}
}
function printInstallTable(installTable,chipStatus,doc){
	var ul=doc.select('.inst-panel > .install > .items').first();
	var ili=ul.select('>li.i').first().clone();
	ul.empty();
	var it=installTable.entrySet().iterator();
	var format=new SimpleDateFormat("yyyy-MM-dd hh:mm");
	while(it.hasNext()){
		var kv=it.next();
		var marketid=kv.getKey();
		var chip=kv.getValue();
		var li=ili.clone();
	//	print(chip);
		if(chip.guid=='B2441D22-9F0A-333F-8307-A22F7E0C8F3F'){
			li.select('>.ci>.op').remove();
		}
		li.attr('marketid',marketid);
		li.select('>.face>li[a]>span').html(chip.name);
		li.select('>.face>li[s]>span[inmarket]').html(chip.market);
		if(chipStatus!=null&&chipStatus.containsKey(chip.name)){
			var cstatus=chipStatus.get(chip.name);
			var inputServerTable=cstatus.inputServerTable;
			var isPlugin=false;
			var itpin=inputServerTable.entrySet().iterator();
			while(itpin.hasNext()){
				var plugin=itpin.next();
				var arr=plugin.getValue();
				if(!arr.isEmpty()){
					isPlugin=true;
					break;
				}
			}
			if(isPlugin){
				li.select('>.face>li[s]>span[status]').html('已运行');
			}else{
				li.select('>.face>li[s]>span[status]').html('已运行，但端子未插入net，因此不能处理服务请求。请点击‘运行按钮’');
			}
		}
		if(chip.icon==null||chip.icon=='#'){
			li.select('>img').remove();
		}else{
			var src=String.format("./resource/dd/%s?path=home://market-icons",chip.icon);
			li.select(">img").attr('src',src);
		}
		
		var view=li.select('> .ci>.view').first();
		view.select('>li[product]>span[value]').html(chip.product);
		view.select('>li[version]>span[value]').html(chip.version);
		view.select('>li[guid]>span[value]').html(chip.guid);
		view.select('>li[company]>span[value]').html(chip.company);
		view.select('>li[copyright]>span[value]').html(chip.copyright);
		view.select('>li[desc]>span[value]').html(chip.desc);
		
		var ctime=format.format(new Date(chip.ctime));
		view.select('>li[ctime]>span[value]').html(ctime);
		ul.append(li);
	}
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
function getTools(home){
	var pipeline=new ArrayList();
	pipeline.add(BsonDocument.parse("{$sort:{'tuple.ctime':1,'tuple.sort':1}}"));
	var res=home.aggregate('csc.tool',pipeline);
	var it=res.iterator();
	return it;
}
function printTools(tools,doc,sws){
	var ul=doc.select('.inst-panel > .service > ul.message').first();
	var tli=ul.select('>.t').first().clone();
	var opli=ul.select('>.op').first().clone();
	ul.empty();
	
	while(tools.hasNext()){
		var tool=tools.next();
		var li=tli.clone();
		li.attr('atid',tool._id);
		li.select('>span[m]').html(tool.tuple.name);
		var icon=tool.tuple.icon;
		if(icon==null||'#'==icon){
			li.select('>img').remove();
		}else{
			var src=String.format("./resource/ud/%s?path=home://system/csc/tool&u=%s",icon,sws.owner());
			li.select('>img').attr('src',src);
		}
		if(tool.tuple.binder!=null){
			li.select('>span[m]').attr('style','color: black;');
		}
		ul.append(li);
	}
	ul.append(opli);
}
function getMenu(home){
	var pipeline=new ArrayList();
	pipeline.add(BsonDocument.parse("{$match:{'tuple.welcome':{$ne:'true'}}}"));
	pipeline.add(BsonDocument.parse("{$sort:{'tuple.sort':1}}"));
	pipeline.add(BsonDocument.parse("{$group:{'_id':'$tuple.parent','atid':{$first:'$_id'},'name':{$first:'$tuple.name'},'binder':{$first:'$tuple.binder'},'ctime':{$first:'$tuple.ctime'},'sort':{$first:'$tuple.sort'},'childs':{$push:{'atid':'$_id','name':'$tuple.name','ctime':'$tuple.ctime','parent':'$tuple.parent','binder':'$tuple.binder'}}}}"));
	pipeline.add(BsonDocument.parse("{$sort:{'sort':1,'childs.sort':1,'ctime':1}}"));
	var res=home.aggregate('csc.app',pipeline);
	var it=res.iterator();
	return it;
}
function printMenu(welcome,menu,doc){
	
	var rootul=doc.select('.inst-panel > .service > ul.business').first();
	var op=rootul.select('>li.op').first().clone();
	var rootli=rootul.select('>li.s[atid!=0]').first().clone();
	rootul.select('>li.s[atid!=0]').remove();
	rootul.select('>li.op').remove();
	
	while(menu.hasNext()){
		var m=menu.next();
		var li=rootli.clone();
		li.attr('atid',m.atid);
		li.select('>span[m]').html(m.name);
		var childs=m.childs;
		var secondul=li.select('>ul').first();
		var sop=secondul.select('>li.op').first().clone();
		var secondli=secondul.select('>li.i').first().clone();
		li.select('>ul>li.i').remove();
		if(m.binder!=null){
			li.select('>span[m]').attr('style','color: black;');
		}
		secondul.select('>li.op').remove();
		for(var i=0;i<childs.length;i++){
			var c=childs[i];
			if(c.atid==c.parent)continue;
			var sli=secondli.clone();
			sli.attr('atid',c.atid);
			sli.select('>span[m]').html(c.name);
			if(c.binder!=null){
				sli.select('>span[m]').attr('style','color: black;');
			}
			secondul.prepend(sli);
		}
		secondul.prepend(sop);
		rootul.append(li);
	}
	rootul.append(op);
	var welcomeatid=welcome.atid;
	var wc=rootul.select('> li[welcome]');
	wc.attr('atid',welcomeatid);
	if(welcome.binder!=null){
		wc.select('>span[m]').attr('style','color: black;');
	}
}