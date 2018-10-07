package cj.lns.chip.sos.website.sws.component.popup;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.chip.sos.website.sws.so.PublishSO;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
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
@CjService(name = "/components/publishPopup.html")
public class PublishPopupComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;
	@CjServiceRef(refByName = "PublishWinToolSoProvider")
	ISecurityObjectProvider publish;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc=m.context().html("/components/publish-popup.html", m.site().contextPath(), "utf-8");
		List<?> list=publish.getChilds("tool",
				ISurfacePosition.POSITION_TOOLBAR_WIN_PUBLISH,
				ISubject.subject(frame), IServicewsContext.context(frame));
		render(list,doc,frame);
		circuit.content().writeBytes(doc.toString().getBytes());
	}
	private void render(List<?> list, Document doc, Frame frame) {
		Element ul=doc.select(".pub-panel>.pub-region>.a-t-box").first();
		Element li=ul.select("li").first().clone();
		ul.empty();
		for(Object o:list){
			PublishSO so=(PublishSO)o;
			li.attr("publishId", so.getId());
			li.attr("resourceId", "publish");
			ToolSO root = (ToolSO) publish.root();
			li.attr("tool", root.getId());
			li.attr("cjopen", so.getPhyId().toString());
			li.select("span").html(so.getName());
			if(!StringUtil.isEmpty(so.getIcon())){
				li.select("img").attr("src",so.getIcon());
			}
			ul.appendChild(li);
			
			li=li.clone();
		}
	}


}
