package cj.lns.chip.sos.website.sws.component;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.service.sws.security.PermissionInfo;
import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.framework.RemoteDevice;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.chip.sos.website.sws.PublicServiceosContext;
import cj.lns.chip.sos.website.sws.PublicSubject;
import cj.lns.chip.sos.website.sws.ServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.HttpFrame;
import cj.ultimate.util.StringUtil;

/*
 * 在进入视窗之前验证：
 * · 有没有登录服务操作系统
 * · 视窗是不是开放视窗，即开放级别
 *   是开放给sos，还是开放给大众。如果是前者，则来访者必须先登录sos，否则不能使用。如果是开放给大众，不论是否登录sos都可访问视窗
 * · 是不是视窗的持有人来访
 *   如果是则全权访问
 * · 是联系人来访，调出联系人认证界面
 *   联系人角色、归属组等，采用基于角色的权限认证。如果联系人属于管理员角色，则和持有人一样拥有全权。
 *   
 *   -> sos   ->   sws
 *     统一认证    视窗认证
 *     语义为：谁来访问视窗，在同一会话中切换多视窗(任何视窗），在时间切片上最多只存在一个视窗
 */
@CjService(name = "/")
public class ServicewsComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String swsid = frame.parameter("swsid");
		if (!frame.containsParameter("swsid") || StringUtil.isEmpty(swsid)) {
			throw new CircuitException("404", "请求的视窗为空");
		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext ctx = IServicewsContext.context(frame);
		if (ctx != null && swsid.equals(ctx.swsid()) && ctx.serviceos(frame)
				.subject(frame).principal().equals(ctx.visitor())) {
			// 如果视窗已存在且它是请求的视窗，则说明当前来访者已在其会话空间中访问过该视窗了
			// 由于同一浏览器会话用户可能先登录一个视窗而后退出后又以另一账号登录了同一视窗，因此在同一会话中可能存在不同来访者访问同一视窗的可能。
			m.site().out().flow(frame, circuit);
			return;
		}
		// 登录的时间设备被设置，但如果是直接访问视窗则不存在
		if (IRemoteDevice.remoteDevice(frame) == null) {
			RemoteDevice rd = (RemoteDevice) RemoteDevice.parse(frame);
			((HttpFrame) frame).session()
					.attribute(IRemoteDevice.KEY_REMOTEDEVICE_IN_SESSION, rd);
		}
		// 1.检查是否存在主体，如果没有则建立公众主体及公众sos上下文
		// 2.访问视窗
		if (ISubject.subject(frame) == null) {// 说明没有来访主体，即没有登录sos,则以公众模拟登录,公众，在视窗的用户中是一个特殊的归属组，它具有有限权限
			// 初始化公众主题、公众sos上下文
			// 将来可以查检本地cookie中是否有用户存在，如果存在则可以该用户访问视窗，但要注意用户的安全性，如果安全则构建auto上下文而非公众上下文
			createPublicServiceos(frame, circuit, plug);
		}
		visitServicews(swsid, frame, circuit, plug);
	}

	private void createPublicServiceos(Frame frame, Circuit circuit,
			IPlug plug) {
		String address = (String) circuit.attribute("remote-address");
		String id = (String) circuit.attribute("select-id");
		String publicUserId = String.format("%s$%s", address, id);
		Face face=new Face("公众","./img/guest.svg","未知身份的用户",null,(byte)-1);//游客的face
		PublicSubject subject = new PublicSubject(publicUserId,face);
		HttpFrame f = (HttpFrame) frame;
		f.session().attribute(ISubject.KEY_SUBJECT_IN_SESSION, subject);
		PublicServiceosContext ctx = new PublicServiceosContext();
		f.session().attribute(IServiceosContext.KEY_SERVICEOSCONTEXT_IN_SESSION,
				ctx);
	}

	private void visitServicews(String swsid, Frame frame, Circuit circuit,
			IPlug plug) throws CircuitException {
		Map<String, Object> swsSimple = new HashMap<>();
		ISubject s = ISubject.subject(frame);
		int state = sws.authServicews(s, swsid, swsSimple,
				circuit/* 此处要认证该问者对视窗的访问权限*/);
		double d=(double)swsSimple.get("inheritId");
		String swstid = String.valueOf((long)d);
		
		if (state == 200) {
			@SuppressWarnings("unchecked")
			Map<String, Object> owner = (Map<String, Object>) swsSimple
					.get("owner");
			String ownerCode=(String)owner.get("userCode");
			Face face = new Face((String) owner.get("nickName"),
					(String) owner.get("head"),
					(String) owner.get("signatureText"),(String)owner.get("briefing"),(byte)(double)owner.get("sex"));
			Map<String,String> props=new HashMap<>();
			props.put("sws-name",(String) swsSimple.get("swsName"));
			props.put("sws-img",(String) swsSimple.get("faceImg"));
			props.put("sws-desc",(String) swsSimple.get("swsDesc"));
			props.put("portal.id",(String) swsSimple.get("portalId"));
			IServicewsContext ctx = new ServicewsContext(ownerCode,swsid,(byte)(double)swsSimple.get("level"), swstid,
					s, face,props);
			HttpFrame f = (HttpFrame) frame;
			f.session().attribute(IServicewsContext.KEY_SWS_CONTEXT_IN_SESSION,
					ctx);
			// 认证成功后则渲染portal
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(frame, circuit);
		} else {
			if (state == 201) {
				IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
				PermissionInfo pi = (PermissionInfo) swsSimple
						.get("permission");
				if (pi.getAuthMethod() == ServicewsAuthMethod.ask) {
					IComponent cmp = (IComponent) m.chip().site()
							.getService("/authAnswer.html");
					if (cmp != null) {
						frame.parameter("swsid", swsid);
						frame.parameter("action", "render");
						frame.parameter("permissionId",
								pi.getPermissionId().toString());
						cmp.flow(frame, circuit, plug);
						return;
					}
				} else if (pi.getAuthMethod() == ServicewsAuthMethod.password) {
					IComponent cmp = (IComponent) m.chip().site()
							.getService("/authPassword.html");
					if (cmp != null) {
						frame.parameter("swsid", swsid);
						frame.parameter("permissionId",
								pi.getPermissionId().toString());
						frame.parameter("action", "render");
						cmp.flow(frame, circuit, plug);
						return;
					}
				}
				// 将未处理的认证方式漏过去以拒绝访问视窗
			}
			HttpFrame f = (HttpFrame) frame;
			f.session().attribute(IServicewsContext.KEY_SWS_CONTEXT_IN_SESSION,
					null);
			throw new CircuitException("801", "拒绝访问视窗");
		}
	}

}
