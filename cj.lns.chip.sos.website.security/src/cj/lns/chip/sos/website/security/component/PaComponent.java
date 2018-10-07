package cj.lns.chip.sos.website.security.component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.service.sws.security.RoleInfo;
import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.security.IContactGroupService;
import cj.lns.chip.sos.website.security.IPaService;
import cj.lns.chip.sos.website.security.IRoleService;
import cj.lns.chip.sos.website.security.ISecurityCenter;
import cj.lns.chip.sos.website.security.ISecurityResource;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.util.StringUtil;

@CjService(name = "/securityobject-pa.html")
public class PaComponent implements IComponent {
	//如果 要支持全自授权，按说包括角色、用户、组在内一律都视为资源，全部从安全中心获取。
	@CjServiceRef(refByName = "securityCenter")
	ISecurityCenter center;
	@CjServiceRef(refByName = "roleService")
	IRoleService role;
	@CjServiceRef(refByName = "paService")
	IPaService pa;
	@CjServiceRef(refByName = "groupService")
	IContactGroupService group;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		String valueId = frame.parameter("valueId");
		String resourceId = frame.parameter("resourceId");
		String permCode = frame.parameter("permCode");
		String permName = frame.parameter("permName");
		String rootResourceId=frame.parameter("rootResourceId");;
		String rootValueId=frame.parameter("rootValueId");;
		try {
			permName = URLDecoder.decode(permName, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		IServicewsContext sws = IServicewsContext.context(frame);
		ISubject subject = ISubject.subject(frame);
		ISecurityResource sr = center.resource(rootResourceId);
		if (sr == null) {
			return;
		}

		Document doc = m.context().html("/securityobject-pa.html",
				m.site().contextPath(), "utf-8");
		doc.body().prepend(
				String.format("<nav imgsrc='%s' title='%s'/>", "#", permName));
		String hidden = String.format(
				"<input id='current' type='hidden' value='%s;%s;%s;%s'>",
				resourceId, valueId, permCode, permName);
		doc.body().append(hidden);

		Acl acl = renderRoles(rootResourceId,rootValueId,resourceId, valueId, permCode, doc, sr, subject,
				sws);
		renderGroups(doc, acl, subject, sws);
		renderContacts(rootResourceId,rootValueId,resourceId, valueId, permCode, doc, sr, subject, sws);
		renderQuestions(rootResourceId,rootValueId,resourceId, valueId, permCode, doc, sr, subject, sws);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderQuestions(String rootResourceId,String rootValueId,String resourceId, String valueId,
			String permCode, Document doc, ISecurityResource sr,
			ISubject subject, IServicewsContext sws) throws CircuitException {
		List<AuthAskInfo> list=pa.getQuestionList(resourceId,valueId,permCode,sws.swsid());
		Element pa=doc.select("ul.security-pa[subject-aq]").first();
		Element ul=pa.select("ul.aq[aq]").first();
		Element add=ul.select("li[add]").first().clone();
		Element aqli=ul.select("li[aq]").first().clone();
		ul.empty();
		ul.appendChild(add);
		if(list.isEmpty()){
			aqli=aqli.clone();
			aqli.attr("new","true");
			ul.appendChild(aqli.clone());
		}
		for(AuthAskInfo ai:list){
			aqli=aqli.clone();
			aqli.select("input[name=a]").val(ai.getAsk());
			aqli.select("input[name=q]").val(ai.getAnswer());
			aqli.attr("aqid",ai.getId().toString());
			ul.appendChild(aqli);
		}
		//答对答错问题的人这些之后再实现，不急
//		List<AuthLoggerInfo> logger=this.pa.getAuthLogger(resourceId,valueId,permCode,sws.swsid());
//		logger.get(0).get
	}

	private void renderContacts(String rootResourceId,String rootValueId,String resourceId, String valueId,
			String permCode, Document doc, ISecurityResource sr,
			ISubject subject, IServicewsContext sws) throws CircuitException {
		List<ContactInfo> allows = pa.getAllowContacts(resourceId, valueId,
				permCode, sws.swsid());
		List<ContactInfo> denies = pa.getDenyContacts(resourceId, valueId,
				permCode, sws.swsid());
		Element allowUl = doc.select(".user-list[subject-allow]").first();
		Element allowli = allowUl.children().first().clone();
		allowUl.empty();
		Element denyUl = doc.select(".user-list[subject-deny]").first();
		Element denyli = denyUl.children().first().clone();
		denyUl.empty();

		for (ContactInfo c : allows) {
			allowli = allowli.clone();
			if (!StringUtil.isEmpty(c.getHeadPic())) {
				allowli.select("img").attr("src", c.getHeadPic());
			}
			allowli.select("span").html(c.getMemoName());
			allowUl.appendChild(allowli);
		}
		for (ContactInfo c : denies) {
			denyli = denyli.clone();
			if (!StringUtil.isEmpty(c.getHeadPic())) {
				denyli.select("img").attr("src", c.getHeadPic());
			}
			denyli.select("span").html(c.getMemoName());
			denyUl.appendChild(denyli);
		}
	}

	private void renderGroups(Document doc, Acl acl, ISubject subject,
			IServicewsContext sws) throws CircuitException {
		List<ContactGroupInfo> groups = group.getContactGroups(sws.swsid());
		Element gRegion = doc.select(".security>.content-panel>.security-pa")
				.first();
		Element template = gRegion.select(".item").first();
		gRegion.empty();
		for (ContactGroupInfo g : groups) {
			Element item = template.clone();
			item.attr("gid", g.getId().toString());
			item.attr("gtype", g.getGroupType());
			item.select(".main-panel>div").html(g.getGroupName());
			if (acl != null && acl.containsGroup(g.getId().toString())) {
				item.select("input").attr("checked", "checked");
			}
			gRegion.appendChild(item);
		}

		// 哪些联系人组被选择，用ACL判断

	}

	private Acl renderRoles(String rootResourceId,String rootValueId,String resourceId, String valueId, String permCode,
			Document doc, ISecurityResource sr, ISubject subject,
			IServicewsContext sws) throws CircuitException {
		// IAclFinder finder = sr.finder();// 查找或设置资源的权限
		IAclSetting set = sr.resourceImpl(rootValueId).setting();
		List<RoleInfo> roles = role.getRoles(sws.swsid());
		Element roleRegion = doc
				.select(".security>.content-panel>.subject-sels").first();
		Element arole = roleRegion.select("a").first().clone();
		roleRegion.empty();
		renderRoles(roles, arole, roleRegion);

		Acl acl = set.getAcl(sws.swsid(), resourceId, valueId, permCode);
		if (acl == null) {// 如果资源还未被授权过，则许可不存在，也就没有ACL，因此这种情况选择默认权限分配布局,由于啥都没，界面就选定在自定义上，其内全是空。
			roleRegion.select("a").removeClass("selected");
			roleRegion.select("a[rid=-1]").addClass("selected");// -1表示自定义操作，即非角色：_custom
			return acl;
		}
		RoleInfo selectedRole = null;// 如果包含指定角色，则界面上选中，如果什么角色都没包含，则为自定义
		for (RoleInfo ri : roles) {
			if (acl.containsRole(ri.getRoleCode())) {
				selectedRole = ri;
				break;
			}
		}
		// 许可表中的充许表是否存在空这种情况，答案是可能存在。
		// 因为在用户注册视窗时会分配一个视窗角色，不同的视窗默认分配的角色不同，如私密性较高的视窗默认为仅自己，开放视窗默认为全部联系人，
		// 而对于子资源的许可，充许表可能为空
		// 但不管怎样，只要充许表中没有角色，则显示自定义面板

		if (selectedRole == null) {
			roleRegion.select("a").removeClass("selected");
			roleRegion.select("a[rid=-1]").addClass("selected");
		} else {// 显示选中的角色
			roleRegion.select("a").removeClass("selected");
			roleRegion.select(String.format("a[rid=%s]", selectedRole.getId()))
					.addClass("selected");
		}

		return acl;
	}

	private void renderRoles(List<RoleInfo> roles, Element template,
			Element roleRegion) {
		// 添加自定义在最前面
		Element a = template.clone();
		a.html("自定义");
		a.attr("rcode", "_custom");
		a.attr("onclick", "selectSubjectKind(this)");
		a.attr("rid", "-1");
		roleRegion.appendChild(a);
		for (RoleInfo ri : roles) {
			Element a2 = template.clone();
			a2.html(ri.getRoleName());
			a2.attr("onclick", "selectSubjectKind(this)");
			a2.attr("rcode", ri.getRoleCode());
			a2.attr("rid", ri.getId().toString());
			roleRegion.appendChild(a2);
		}

	}

}
