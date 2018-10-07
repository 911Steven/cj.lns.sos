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

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var doc = m.context().html("/common/cscComputer.html",
			m.site().contextPath(), "utf-8");
	var factory = m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(sws.owner());
	if(computer==null){
		doc = m.context().html("/common/cscTips.html",
				m.site().contextPath());
		circuit.content().writeBytes(doc.toString().getBytes());
		return;
	}
	
	var host=factory.getHost(computer.onHost);
	printComputer(computer,host,doc,m,sws);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function printComputer(computer,host,doc,m,sws){
	if(!sws.isOwner()){
		var op=doc.select('.ser-panel > .runtimeEnv > .info > .op');
		op.attr('title','您不是计算机的拥有者，不能操作，只能看看。');
		op.select('li[start]').removeAttr('start');
		op.select('li[restart]').removeAttr('restart');
		op.select('li[shutdown]').removeAttr('shutdown');
		op.select('li[destroy]').removeAttr('title');
		op.select('li[destroy]').removeAttr('destroy');
	}
	if('developer'!=computer.owneris){
		doc.select('.ser-panel > .runtimeEnv > .info > .panel > li[mypub]').attr('style','display:none;');
	}
	doc.select('.ser-panel > .runtimeEnv > .face > p[name]>span[value]').html(computer.owner);
	doc.select('.ser-panel > .runtimeEnv > .info > .host > ul > li[name]>span[value]').html(host.name);
	var pt=computer.cscHost;
	pt=pt.substring(0,pt.indexOf('://'));
	doc.select('.ser-panel > .runtimeEnv > .info > .host > ul > li[pt]>span[value]').html(pt);
	var docker='';
	var dockers=host.dockers;
	for(var i=0;i<dockers.size();i++){
		var d=dockers.get(i);
		if(computer.docker==d.imageid){
			docker=d.title;
			break;
		}
	}
	doc.select('.ser-panel > .runtimeEnv > .info > .pc > li[source]>span[value]').html(docker);
	doc.select('.ser-panel > .runtimeEnv > .info > .pc > li[interface]>span[value]').html(computer.cscCustomer);
	doc.select('.ser-panel > .runtimeEnv > .info > .pc > li[nrport]>span[value]').html(computer.nrport);
	var format=new SimpleDateFormat("yyyy-MM-dd hh:mm");
	var pdate=format.format(new Date(computer.ctime));
	doc.select('.ser-panel > .runtimeEnv > .info > .pc > li[ctime]>span[value]').html(pdate);
	
	var deploy=computer.getDeploy();
	var f = new Frame(String.format("get /deploy/getComputerStatus.service csc/1.0",chip));
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
	if(status=='running'){
		status='已开机';
	}else{
		status='已关机';
	}
	doc.select('.ser-panel > .runtimeEnv > .face > p[status]>span[value]').html(status);
}