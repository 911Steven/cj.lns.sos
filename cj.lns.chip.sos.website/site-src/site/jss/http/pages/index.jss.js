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
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');
var File=Java.type('java.io.File');
var FileFilter=Java.type('java.io.FileFilter');
var ModuleContext=Java.type('cj.studio.ecm.plugins.moduleable.ModuleContext');
var Arrays=Java.type('java.util.Arrays');
var Comparator=Java.type('java.util.Comparator');

exports.doPage=function(frame,circuit,plug,ctx){
	var doc=ctx.html(frame.relativePath());
	var swslist=getSwsList("1", "0", "100");
	var subject=ISubject.subject(frame);
	printSwsList(subject,swslist,doc);
	if(subject!=null){
		doc.select('.content > .topbar > ul.right>.mine').remove();
		var cname=subject.class.getSimpleName();
		if(cname!='PublicSubject'){
			doc.select('.content > .topbar > ul.right>.login').remove();
			doc.select('.content > .topbar > ul.right').append("<li class='me'><span label>欢迎 "+subject.principal()+"</span></li>");
		}
		var sws=IServicewsContext.context(frame);
		if(sws!=null){
			var me=doc.select('.content > .topbar > ul.right>.me');
			me.attr('swsid',sws.swsid());
		}
	}
	var httproot=circuit.attribute('http.root');
	loadPortlets(frame,circuit,httproot,doc);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function loadPortlets(frame,circuit,httproot,doc){
	var m = ServiceosWebsiteModule.get();
	var site=m.site();
	var lethome=httproot+File.separator+'portlets';
	var left=lethome+File.separator+'left';
	var right=lethome+File.separator+'right';
	var center=lethome+File.separator+'center';
	
	var centerfile=new File(center);
	var centerlist=centerfile.listFiles(new FileFilter{accept:function(f){
		return f.isDirectory();
	}});
	Arrays.sort(centerlist, new Comparator{
	    compare:function(o1, o2) {
	        return o1.getName().compareTo(o2.getName());
	    }
	});
	var ul=doc.select('.content > .h-zw > .h-center>.c-center>.lets').first();
	var cli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<centerlist.length;i++){
		var file=centerlist[i];
		var li=cli.clone();
		printCenterPortlets(file,li,site,httproot);
		ul.append(li);
	}
	var leftfile=new File(left);
	var leftlist=leftfile.listFiles(new FileFilter{accept:function(f){
		return f.isDirectory();
	}});
	Arrays.sort(leftlist, new Comparator{
	    compare:function(o1, o2) {
	        return o1.getName().compareTo(o2.getName());
	    }
	});
	var ul=doc.select('.content > .h-zw > .h-center>.c-left>.lets').first();
	var cli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<leftlist.length;i++){
		var file=leftlist[i];
		var li=cli.clone();
		printLeftPortlets(file,li,site,httproot);
		ul.append(li);
	}
	var rightfile=new File(right);
	var rightlist=rightfile.listFiles(new FileFilter{accept:function(f){
		return f.isDirectory();
	}});
	Arrays.sort(rightlist, new Comparator{
	    compare:function(o1, o2) {
	        return o1.getName().compareTo(o2.getName());
	    }
	});
	var ul=doc.select('.content > .h-zw > .h-center>.c-right>.lets').first();
	var cli=ul.select('>li').first().clone();
	ul.empty();
	for(var i=0;i<rightlist.length;i++){
		var file=rightlist[i];
		var li=cli.clone();
		printRightPortlets(file,li,site,httproot);
		ul.append(li);
	}
}
function printRightPortlets(file,li,site,httproot){
	var jssdir=file.getAbsolutePath()+File.separator+'jss';
	var jssfile=new File(jssdir);
	if(!jssfile.exists()){
		jssfile.mkdir();
	}
	var jsslist=jssfile.listFiles(new FileFilter{accept:function(f){
		return f.getName().endsWith(".jss.js");
	}});
	for(var j=0;j<jsslist.length;j++){
		var jss=jsslist[j];
		var jssname=jss.getName();
		jssname=jssname.substring(0,jssname.lastIndexOf('.jss.js'));
		var location='/portlets/right/'+file.getName();
		var homepath=file.getAbsolutePath();
		var jsspath='$.cj.jss.portlets.right.'+file.getName()+'.jss.'+jssname;
		var portlet=site.getService(jsspath);
		if(portlet!=null){
			if(portlet.render==null){
				print('jss 定义的栏目缺少render函数：'+jsspath);
				continue;
			}
			var portletcxt=new ModuleContext(httproot,location);
			try{
				var panel=portlet.render(portletcxt);
				if(panel==null){
					print('jss render 无返回');
					continue;
				}
				li.html(panel);
			}catch(e){
				print(String.format('jss 定义的栏目运行时报错：%s 原因：%s',jsspath,e));
			}
		}
	}
}
function printLeftPortlets(file,li,site,httproot){
	var jssdir=file.getAbsolutePath()+File.separator+'jss';
	var jssfile=new File(jssdir);
	if(!jssfile.exists()){
		jssfile.mkdir();
	}
	var jsslist=jssfile.listFiles(new FileFilter{accept:function(f){
		return f.getName().endsWith(".jss.js");
	}});
	for(var j=0;j<jsslist.length;j++){
		var jss=jsslist[j];
		var jssname=jss.getName();
		jssname=jssname.substring(0,jssname.lastIndexOf('.jss.js'));
		var location='/portlets/left/'+file.getName();
		var homepath=file.getAbsolutePath();
		var jsspath='$.cj.jss.portlets.left.'+file.getName()+'.jss.'+jssname;
		var portlet=site.getService(jsspath);
		if(portlet!=null){
			if(portlet.render==null){
				print('jss 定义的栏目缺少render函数：'+jsspath);
				continue;
			}
			var portletcxt=new ModuleContext(httproot,location);
			try{
				var panel=portlet.render(portletcxt);
				if(panel==null){
					print('jss render 无返回');
					continue;
				}
				li.html(panel);
			}catch(e){
				print(String.format('jss 定义的栏目运行时报错：%s 原因：%s',jsspath,e));
			}
		}
	}
}
function printCenterPortlets(file,li,site,httproot){
	var jssdir=file.getAbsolutePath()+File.separator+'jss';
	var jssfile=new File(jssdir);
	if(!jssfile.exists()){
		jssfile.mkdir();
	}
	var jsslist=jssfile.listFiles(new FileFilter{accept:function(f){
		return f.getName().endsWith(".jss.js");
	}});
	for(var j=0;j<jsslist.length;j++){
		var jss=jsslist[j];
		var jssname=jss.getName();
		jssname=jssname.substring(0,jssname.lastIndexOf('.jss.js'));
		var location='/portlets/center/'+file.getName();
		var homepath=file.getAbsolutePath();
		var jsspath='$.cj.jss.portlets.center.'+file.getName()+'.jss.'+jssname;
		var portlet=site.getService(jsspath);
		if(portlet!=null){
			if(portlet.render==null){
				print('jss 定义的栏目缺少render函数：'+jsspath);
				continue;
			}
			var portletcxt=new ModuleContext(httproot,location);
			
			try{
				var panel=portlet.render(portletcxt);
				if(panel==null){
					print('jss render 无返回');
					continue;
				}
				li.html(panel);
			}catch(e){
				print(String.format('jss 定义的栏目运行时报错：%s 原因：%s',jsspath,e));
			}
		}
	}
}
function printSwsList(subject,swslist,doc){
	var ul=doc.select('.content > .banner > .b-center > .systems > .menu > .mlist').first();
	var mli=ul.select('>li').first().clone();
	ul.empty();
	mli.removeClass('tab-selected');
	var tabs=doc.select('.content > .abstract > .a-tabs').first();
	var tabli=tabs.select('>li.tab').first().clone();
	tabli.removeClass('tab-show');
	tabs.empty();
	for(var i=0;i<swslist.size();i++){
		var li=mli.clone();
		if(i==0){
			li.addClass('tab-selected');
		}
		var sws=swslist.get(i);
		var swsid=sws.id+'';
		li.attr('swsid',swsid);
		var src=String.format("./resource/ud/%s?path=%s://system/faces/&u=%s",sws.faceImg,swsid,sws.owner);
		li.select('>img[face]').attr('src',src);
		li.select('>p').html(sws.name);
		ul.append(li);
		
		var tli=tabli.clone();
		if(i==0){
			tli.addClass('tab-show');
		}
		tli.attr('swsid',swsid);
		tli.select('>p').html(sws.description);
		tabs.append(tli);
	}
}
function getSwsList(level, skip, limit) {
	// 查询指定类型的所有视窗，并分页
	var frame = new Frame("findServicews /sws/instance sos/1.0");
	frame.parameter("level", level);
	frame.parameter("skip", skip);
	frame.parameter("limit", limit);
	//frame.parameter("swsid", swsid);
	//frame.parameter("swsName", swsName);
	var circuit = new Circuit("sos/1.0 200 ok");
	var out = ServiceosWebsiteModule.get().out();
	out.flow(frame, circuit);
	var ctx = circuit.content();
	if (ctx.readableBytes() > 0) {
		var back = new Frame(ctx.readFully());
		var state = parseInt(back.head("status"));
		if (state == 404) {
			return new ArrayList();
		}
		if (state != 200) {
			throw new CircuitException("503",
					String.format("查找视窗失败:", back.head("message")));
		}
		var json = new String(back.content().readFully());
		return new Gson().fromJson(json,ArrayList.class);

	}
	return new ArrayList();
}