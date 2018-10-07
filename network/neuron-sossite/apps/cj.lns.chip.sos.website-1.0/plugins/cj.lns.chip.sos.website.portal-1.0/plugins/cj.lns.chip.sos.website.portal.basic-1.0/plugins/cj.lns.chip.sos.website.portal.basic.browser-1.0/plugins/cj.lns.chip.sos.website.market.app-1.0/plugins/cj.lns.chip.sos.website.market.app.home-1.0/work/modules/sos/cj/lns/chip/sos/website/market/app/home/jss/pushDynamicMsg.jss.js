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
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var MessageStub = Java.type('cj.studio.ecm.sns.mailbox.viewer.MessageStub');
var System = Java.type('java.lang.System');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var MySession = Java.type('cj.studio.ecm.sns.mailbox.MySession');

function createSession(swsid, app, owner,appId,cube){
	
	var session = new MySession();
	session.setAppId(appId);
	session.setAppCode(app.get("code"));
	session.setCreateTime(System.currentTimeMillis());
	switch(appId){
	case "myarticle":
		session.setIcon("./swssite/develop/img/article.svg");
		session.setTitle("博客");
		break;
	case "myproduct":
		session.setIcon("./swssite/develop/img/store.svg");
		session.setTitle("产品");
		break;
	case "myspace":
		session.setIcon("./swssite/develop/img/file.svg");
		session.setTitle("视窗空间");
		break;
	case "homespace":
		session.setIcon("./swssite/develop/img/file.svg");
		session.setTitle("主空间");
		break;
	case "shuoshuo"://与我相关
		session.setIcon("./swssite/develop/img/chat.svg");
		session.setTitle("说说");
		break;
	case "mine"://与我相关
		session.setIcon("./swssite/develop/img/mine.svg");
		session.setTitle("@与我相关");
		break;
	default:
		throw new CircuitException("503","未定定义会话应用："+appId);
		
	}
	var doc=new TupleDocument(session);
	var sid = cube.saveDoc('sns.session', doc);
	session.setSid(sid);
	return session;
}
exports.push= function(appid,cnt,source,sws,m,selectUser){
	var disk=m.site().diskOwner(sws.owner());
	var cube=disk.cube(sws.swsid());
	
	var userdisk = m.site().diskOwner(sws.owner());
	var userhome = userdisk.cube(sws.swsid());
	
	var cjql = "select {'tuple':'*'} from tuple sns.session java.util.HashMap where {'tuple.appCode':'dynamic','tuple.appId':'?(appid)'}";
	var q = userhome.createQuery(cjql);
	q.setParameter("appid", appid);
	var s = q.getSingleResult();
	var sid='';
	if (s == null) {//如果会话为空得为应用创建会话，注意：在接收端(cj.lns.chip.sns.server.device.dao.DynamicDao.createSession)也会创建应用的会话
		var cjql = "select {'tuple':'*'} from tuple sns.app java.util.HashMap where {'tuple.code':'dynamic'}";
		var q = m.site().diskLnsData().home().createQuery(cjql);
		var doc=q.getSingleResult();
		var app=doc.tuple();
		var session=createSession(sws.swsid(), app, sws.owner(),appid,cube);
		sid=session.getSid();
	}else{
		sid=s.docid();
	}
	
	var msg = new MessageStub();
	msg.setSender(sws.owner());
	msg.setSenderOnSws(sws.swsid());
	msg.setSendTime(System.currentTimeMillis());
	msg.setSid(sid);
	msg.setSwstid(sws.swstid());
	msg.setBody(cnt.getBytes());
	msg.getSource().putAll(source);
	msg.getSource().put('appId',appid);
	msg.getSource().put('senderFace',sws.face());
	
	
	
	//发送箱
	msg.setIobox('outbox');//发送的消息
	cube.saveDoc('iobox', new TupleDocument(msg));
	
	var f = new Frame("pushMsg /im/session.service im/1.0");
	f.parameter("app-id", appid);
	f.parameter("app-code", 'dynamic');
	if(typeof selectUser!='undefined'&&selectUser!=null){
		f.parameter("select-user", selectUser);
	}
	f.content().writeBytes(new Gson().toJson(msg).getBytes());
	var c = new Circuit("im/1.0 200 ok");
	m.site().out().flow(f, c);
	
}
