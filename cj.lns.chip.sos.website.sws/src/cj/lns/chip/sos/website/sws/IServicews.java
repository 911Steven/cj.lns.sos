package cj.lns.chip.sos.website.sws;

import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.graph.CircuitException;

public interface IServicews {
	/**
	 * 认证视窗，通过后返回视窗信息
	 * <pre>
	 * · 如果视窗的持有人是来访主体，则通过
	 * · 如果非持有人，拥有视窗访问许可，则按相应权限登入，如果没有访问许可或被拒绝，则按视窗资源的认证策略进行，认证策略有：
	 *   ·· 如果被拒绝，则直接显示被拒界面
	 *   ·· 如果是回答问题来访，则显示回答问题界面
	 *   ·· 如果是持有者在加联系人时联系人设置的密码，则显示登录界面
	 *   ·· 如果是消息申请，则显示消息申请对答界面
	 *   ·· 其它的认证策略
	 * </pre>
	 * @param subject 来访主体
	 * @param swsid 视窗
	 * @param circuit 与客户端的回路
	 * @return 
	 */
	int authServicews(ISubject subject, String swsid,Map<String,Object> fillSws,Circuit circuit) throws CircuitException;
	/**
	 * 获取当前视窗已挂载的应用
	 * <pre>
	 *
	 * </pre>
	 * @param swsid 
	 * @return
	 */
	List<AppSO> getMountedApp(String swsid);
	void mountApp(String swsid, byte[] appjson);
	void unmountApp(String swsid, String provider, String appId);
	List<PortletSO> getMountedPortlet(String swsid);
	void unmountPortlet(String swsid, String provider, String appId);
	void mountPortlet(String swsid, byte[] b);
	List<MenuSO> getMountedMenus(String swsid);
	void unmountMenu(String swsid, String provider, String menuId);
	void mountMenu(String swsid, byte[] b);
	SosUserInfo getOwner(String swsid);
	ServicewsSummary getServicewsSummary(String swsid,
			IServiceosWebsiteModule m);

}
