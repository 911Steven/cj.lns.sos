package cj.lns.chip.sos.website.security.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.Permission;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.security.ISecurityCenter;
import cj.lns.chip.sos.website.security.ISecurityResource;
import cj.lns.chip.sos.website.security.ISecurityResourceImpl;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/securityobject-view.html")
public class ViewComponent implements IComponent {
	@CjServiceRef(refByName = "securityCenter")
	ISecurityCenter center;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String resourceId = frame.parameter("resourceId");
		String valueId = frame.parameter("valueId");
		String rootResourceId=frame.parameter("rootResourceId");
		String rootValueId=frame.parameter("rootValueId");
		ISecurityResource sr = center.resource(rootResourceId);
		if (sr == null) {
			return;
		}
		if(!sr.containsResourceImpl(rootValueId)){
			return;
		}
		ISecurityResourceImpl impl=sr.resourceImpl(rootValueId);
		ISecurityObject parent = impl.find(resourceId, valueId,
				ISubject.subject(frame), IServicewsContext.context(frame));
		if (parent == null)
			throw new CircuitException("404", String.format("安全资源在提供器：%s中不存在，请检查提供器的find方法。%s,%s",impl.soProviderClass().getName(),resourceId,valueId));

		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/securityobject-view.html",
				m.site().contextPath(), "utf-8");
		doc.body().prepend(String.format("<nav imgsrc='%s' title='%s'/>",
				parent.valueIcon(), parent.valueName()));

		List<ISecurityObject> list = impl.childs(resourceId, valueId,
				ISubject.subject(frame), IServicewsContext.context(frame));

		render(rootResourceId,rootValueId,parent, list, doc);

		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void render(String rootResourceId, String rootValueId, ISecurityObject parent, List<ISecurityObject> childs,
			Document doc) {
		Element universal = doc
				.select(".security-view>.content-panel>ul.universal").first();
		Element item = universal.select(".item").first().clone();
		universal.empty();
		List<Permission> perms = parent.permissions();
		for (Permission p : perms) {
			item=item.clone();
			String url = "./security/securityobject-pa.html";
			url = String.format("%s?rootResourceId=%s&rootValueId=%s&valueId=%s&resourceId=%s&permCode=%s&permName=%s",url,
					rootResourceId,rootValueId,parent.valueId(), parent.resourceId(), p.getCode(),p.getName());
			item.attr("openContent",url);
			item.select(".main-panel>div").html(p.getName());
			universal.appendChild(item);
		}
		
		Element objList = doc
				.select(".security-view>.content-panel>ul.objectlist").first();
		item = objList.select(".item").first().clone();
		objList.empty();
		
		for(ISecurityObject so:childs){
			item=item.clone();
			String url = "./security/securityobject-view.html";
			url = String.format("%s?rootResourceId=%s&rootValueId=%s&valueId=%s&resourceId=%s",url,rootResourceId,rootValueId,
					so.valueId(), so.resourceId());
			item.attr("openContent",url);
			item.select("img").attr("src",so.valueIcon());
			item.select(".main-panel>div").html(so.valueName());
//			Element permUl=item.select("ul.detail").first();
//			Element permItem=permUl.select("li").first().clone();
//			permUl.empty();
//			perms = so.permissions();
//			for (Permission p : perms) {
//				permItem=permItem.clone();
//				url = "./security/securityobject-pa.html";
//				url = String.format("%s?rootResourceId=%s&rootValueId=%s&valueId=%s&resourceId=%s&permCode=%s&permName=%s",url,
//						rootResourceId,rootValueId,parent.valueId(), parent.resourceId(), p.getCode(),p.getName());
//				permItem.attr("openContent",url);
//				permItem.select("span").html(p.getName());
//				permUl.appendChild(permItem);
//			}
			objList.appendChild(item);
		}
	}

}
