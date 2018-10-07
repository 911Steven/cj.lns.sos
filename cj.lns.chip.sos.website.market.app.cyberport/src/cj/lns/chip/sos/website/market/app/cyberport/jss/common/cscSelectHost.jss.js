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
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var factory = m.site().getService('cscDeveloperFactory');
	doc = m.context().html("/common/cscRequestComputer.html",
			m.site().contextPath());
	var config=doc.select('.r-computer > .detail > .host > .config').first();
	printView(frame.parameter('hostid'),factory,sws,config);
	circuit.content().writeBytes(config.html().getBytes());
}
function printView(hostid,factory,sws,config){
	var firstHost=factory.getHost(hostid);
	//打印第一个主机的信息
	if(firstHost==null){
		return;
	}
	
	config.select('> ul > .computerCount>span[value]').html(factory.getComputerCountOnHost(hostid));
	
	var hptSel= config.select('> ul >.protocol>select').first();
	var hptOp=hptSel.select('>option').first().clone();
	hptSel.empty();
	var cscHosts=firstHost.getCscHosts();
	for(var i=0;i<cscHosts.size();i++){
		var h=cscHosts.get(i);
		var op=hptOp.clone();
		op.attr('value',h.cscProtocol);
		op.html(h.cscProtocol);
		hptSel.append(op);
	}
	var assignedPort=firstHost.getAssignedPort();
	if(StringUtil.isEmpty(assignedPort)){
		assignedPort="9000";
	}else{
		assignedPort++;
	}
	config.select('> ul > li.port > ul > li[pcport]>input').attr('value',assignedPort);
	
	var dockerImages= config.select('> ul > li.port > ul > li[images]>select').first();
	var dockerOption=dockerImages.select('>option').first().clone();
	dockerImages.empty();
	var dockers=firstHost.getDockers();
	for(var i=0;i<dockers.size();i++){
		var d=dockers.get(i);
		var op=dockerOption.clone();
		op.attr('value',d.imageid);
		op.html(d.title);
		dockerImages.append(op);
	}
}