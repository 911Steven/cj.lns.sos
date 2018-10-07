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
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var CscComputer = Java.type('cj.lns.chip.sos.website.framework.csc.CscComputer');
var System=Java.type('java.lang.System');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var factory = m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(sws.owner());
	checkComputerIsRun(computer,m);
	//安装芯片
	var f = new Frame(String.format("get /helloworld/stopChip.service csc/1.0",chip));
	f.head('dest-address',computer.getCscHost());
	f.head('csc-visitor',sws.owner());
	f.head('csc-customer',computer.cscCustomer);
	f.parameter('computer-owner',sws.owner());
	f.parameter('chip-marketid',frame.parameter('marketid'));
	if(frame.parameter('neuronFlush')=='true'){
		f.parameter('neuron-flush','true');
	}
	var site = m.site();
	var disk=m.site().diskLnsData();
	var home=disk.home();
	var chip=getChip(home,frame.parameter('marketid'));
	f.content().writeBytes(new Gson().toJson(chip.tuple()).getBytes());
	
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	circuit.content().writeBytes(back.content());
}
function checkComputerIsRun(computer,m){
	var f = new Frame("get /deploy/getComputerStatus.service csc/1.0");
	f.head('dest-address',computer.deploy);
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
	if(status!='running'){
		throw new CircuitException('805','云计算机未开机。');
	}
}
function getChip(home,chipmarketid){
	var cjql=String.format("select {'tuple':'*'} from tuple csc.market java.util.HashMap where {'_id':ObjectId('%s')}",chipmarketid)
	var q=home.createQuery(cjql);
	return q.getSingleResult();
}