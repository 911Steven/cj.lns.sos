package cj.lns.chip.sos.website.startmenu.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.util.StringUtil;

@CjService(name = "/")
public class StartMenuRenderComponent implements IComponent {
	@CjServiceRef(refByName = "toobarSoProvider")
	ISecurityObjectProvider toolbar;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 组装画面，向各模块发出渲染要求，在此组装，组装就是将插件的内容替换掉画布中场景插件对应的内容。

		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/startMenu.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext sws = IServicewsContext.context(frame);
		// 显示信息港
		Face face = sws.visitor().face();
		Elements img = doc.select(".right>.circle-box>img.circle-inner");
		String src = "#";
		String name="";
		if (!StringUtil.isEmpty(face.getHead())) {
			if (ISubject.subject(frame).containsRole("guestUsers")) {
				src = face.getHead();
				name="游客";
			} else {
				src = String.format(
						"./resource/ud/%s?path=home://system/img/faces&u=%s",
						face.getHead(), sws.visitor().principal());
				name=sws.visitor().principal();
			}
		}
		img.attr("src", src);
		doc.select(".right>.circle-box>span").html(name);
		
		if (sws.isOwner()) {
			doc.select(".first > a > div > label").html("我的视窗");
		} else {
			Face ownerFace=sws.face();
			if(ownerFace.isFeMale()){
				doc.select(".first > a > div > label").html("她的视窗");
			}else{
				doc.select(".first > a > div > label").html("他的视窗");
			}
			if (sws.visitor().containsRole("guestUsers")) {
				// 隐信息港
				doc.select(".third").attr("style", "display:none;");
			}
		}
		renderToolbar(doc, frame);

		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderToolbar(Document doc, Frame frame)
			throws CircuitException {
		List<?> list = toolbar.getChilds(ISurfacePosition.POSITION_TOOLBAR,
				ISurfacePosition.POSITION_TOOLBAR, ISubject.subject(frame),
				IServicewsContext.context(frame));
		Element tools = doc.select("#tools").first();
		Element button = tools.select("li").first().clone();
		tools.empty();
		for (Object o : list) {
			ToolSO so = (ToolSO) o;
			if (!StringUtil.isEmpty(so.getDescription()))
				button.attr("title", so.getDescription());
			if (!StringUtil.isEmpty(so.getId())) {
				button.attr("cjevent",
						String.format("openShortcutPanel(%s)", so.getId()));
				Element popup = button.select("div.popup-panel").first();
				popup.attr("id", so.getId());
				popup.attr("src", so.getCommand());
			}
			if (!StringUtil.isEmpty(so.getIcon()))
				button.select("img").attr("src", so.getIcon());
			button.select("a>div>label").html(so.getName());
			tools.appendChild(button);

			button = button.clone();

		}
	}

}
