package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/GetContactsByGroupId.service")
public class GetContactsByGroupIdComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String gid = frame.parameter("gid");
		if (StringUtil.isEmpty(gid)) {
			throw new CircuitException("503", "参数组标识未指定");
		}
		String url = "/security/getContactsByGroup.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		List<ContactInfo> contacts = new Gson().fromJson(json,
				new TypeToken<List<ContactInfo>>() {
				}.getType());
		Document doc = m.context().html("/contact/userGroup.html",
				m.site().contextPath(), "utf-8");
		render(contacts, doc,circuit);
		
	}

	private void render(List<ContactInfo> contacts, Document doc, Circuit circuit) {
		Element ul = doc.select(".ug-panel>ul.ug-g-list>.ug-g-box>.ug-g-u-list")
				.first();
		Element li = ul.select("li").first().clone();
		ul.empty();
		if (contacts.isEmpty()) {
			return;
		}
		for (ContactInfo g : contacts) {
			
			li.select("ul.ug-g-u-d>li:first-child").html(g.getUserCode());
			if (!StringUtil.isEmpty(g.getHeadPic())) {
				String src =String.format("./resource/ud/%s?path=home://system/img/faces&u=%s",g.getHeadPic(),g.getUserCode());
				li.select("img").attr("src", src);
			}
			if (StringUtil.isEmpty(g.getPersonalSignature())) {
				g.setPersonalSignature("&nbsp;");
			}
			li.select("ul.ug-g-u-d>li:last-child")
					.html(g.getPersonalSignature());
			li.attr("cid", g.getId().toString());
			li.attr("uid", g.getUserCode());
			ul.appendChild(li);
			li = li.clone();
		}
		circuit.content().writeBytes(ul.outerHtml().getBytes());
	}

}
