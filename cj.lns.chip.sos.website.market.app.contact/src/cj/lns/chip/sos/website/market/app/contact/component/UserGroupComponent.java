package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
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

@CjService(name = "/userGroup.html")
public class UserGroupComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String url = "/security/getAllGroup.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		List<ContactGroupInfo> groups = new Gson().fromJson(json,
				new TypeToken<List<ContactGroupInfo>>() {
				}.getType());
		Document doc = m.context().html("/contact/userGroup.html",
				m.site().contextPath(), "utf-8");
		render(groups, doc);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void render(List<ContactGroupInfo> groups, Document doc) {
		Element ul = doc.select(".ug-panel>ul.ug-g-list").first();
		Element li = ul.select("li").first().clone();
		ul.empty();
		for (ContactGroupInfo g : groups) {
			li.select(".ug-g-pad>span.ug-g-name").html(g.getGroupName());
			li.select(".ug-g-pad>span.ug-g-on>label[count]")
					.html(String.valueOf(g.getUserCount()));
			li.select(".ug-g-pad>span.ug-g-on>label[on]").html("0");
			li.attr("gid", g.getId().toString());
			li.select("ul.ug-g-u-list").remove();
			ul.appendChild(li);
			li = li.clone();
		}
	}

}
