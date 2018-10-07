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
var Date = Java.type('java.util.Date');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	//根据开发者和他的服务名，可以定位到宿主位置及容器端口号，然后由服务名访问到具体的服务
	//因此：开发者和服务名确定一个key，写作：/developer/cscServiceName，先通过key找到配置，便有以下代码
	//开发者与访问者是两个不同的用户，前者是服务提供者，后者是使用者。
	var params=WebUtil.parserParam(new String(frame.content().readFully()));
	var developer=params.get('developer');
	if(StringUtil.isEmpty(developer)){
		throw new CircuitException('404','缺少参数：developer');
	}
	var chip=params.get('chip');
	if(StringUtil.isEmpty(chip)){
		throw new CircuitException('404','缺少参数：chip');
	}
	var csckey=params.get('csckey');
	var factory=m.site().getService('cscDeveloperFactory');
	var computer=factory.getComputer(developer);
	if(computer==null){
		throw new CircuitException('404','用户：'+developer+' 没有云计算机');
	}
	var url=String.format("get /%s/%s.service csc/1.0",chip,csckey.replace(/\./g, '/'));
	url=url.replace(/\/\//g, '/');
	var f = new Frame(url);
	f.head('csc-visitor',sws.visitor().principal());//总是有访问者
	f.head('dest-address',computer.getCscHost());
	//f.head('dest-address','rio-tcp://192.168.201.232:8081');//访问客户服务部署机
	//f.head('dest-address','rio-udt://192.168.201.232:8083');
	//f.head('dest-address','rio-http://192.168.201.232:8082');
	f.head('csc-customer',computer.getCscCustomer());//一般都与主机在同一机上，使用全地址形式是考虑将来也可跨主机（为了灵活性）
	var c = new Circuit("csc/1.0 200 ok");
	m.site().out().flow(f, c);
	var back=new Frame(c.content().readFully());
	var state=parseInt(back.head('status'));
	if(state>=300){
		throw new CircuitException(back.head('status'),'在远程上出现错误：'+back.head('message'));
	}
	circuit.content().writeBytes(back.content());
}