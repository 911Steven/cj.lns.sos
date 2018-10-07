package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.market.app.contact.bo.ChatGroupBO;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/chatGroup.html")
public class ChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/contact/chatGroup.html",
				m.site().contextPath(), "utf-8");
		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();

		IServicewsContext sws = IServicewsContext.context(frame);
		List<ChatGroupBO> groups = getMyGroups(sws.owner(), home, m);
		printMyGroups(groups, doc);
		List<ChatGroupBO> joinedgroups = getJoinGroups(sws.owner(), home, m);
		printJoinGroups(joinedgroups, doc);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void printJoinGroups(List<ChatGroupBO> groups, Document doc) {
		Element gul = doc.select(".chat-g>.chat-g-jo>.chat-g-list").first();
		Element gli = gul.select(">li.g").first().clone();
		Element addG = gul.select(">li.g[op]").first().clone();
		gul.empty();
		for (ChatGroupBO bo : groups) {
			gli = gli.clone();
			gli.attr("gid", bo.getId());
			String numText = String.format("群号：%s",
					bo.getNum() == null ? "无" : bo.getNum());
			gli.attr("title", numText);
			String src = String.format("./resource/dd/%s?path=home://%s",
					bo.getHeadFile(), bo.getHeadHome());
			gli.select("img").attr("src", src);
			gli.select("p").html(bo.getName());
			gul.appendChild(gli);
		}
		gul.appendChild(addG);
	}

	private List<ChatGroupBO> getJoinGroups(String owner, ICube home,
			IServiceosWebsiteModule m) {
		IQuery<String> qu = home.createQuery(
				"select {'tuple.gid':1}.distinct() from tuple ?(colName) java.lang.String where {'tuple.user':'?(user)'}");
		qu.setParameter("colName", ChatGroupBO.KEY_COL_USER_NAME);
		qu.setParameter("user", owner);
		List<IDocument<String>> ret = qu.getResultList();
		List<String> gids=new ArrayList<>();
		for(IDocument<String>t:ret){
			gids.add(String.format("ObjectId('%s')", t.tuple()));
		}
		List<ChatGroupBO> list = new ArrayList<>();
		IQuery<ChatGroupBO> q = home.createQuery(
				"select {'tuple':'*'} from tuple ?(colName) ?(clazz) where {'_id':{$in:?(gids)},'tuple.creator':{$ne:'?(owner)'}}");
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("gids", gids);
		q.setParameter("owner", owner);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		List<IDocument<ChatGroupBO>> result = q.getResultList();
		for (IDocument<ChatGroupBO> doc : result) {
			ChatGroupBO bo = doc.tuple();
			bo.setId(doc.docid());
			list.add(bo);
		}
		return list;
	}

	private void printMyGroups(List<ChatGroupBO> groups, Document doc) {
		Element gul = doc.select(".chat-g>.chat-g-my>.chat-g-list").first();
		Element gli = gul.select(">li.g").first().clone();
		Element addG = gul.select(">li.g[op]").first().clone();
		gul.empty();
		for (ChatGroupBO bo : groups) {
			gli = gli.clone();
			gli.attr("gid", bo.getId());
			String numText = String.format("群号：%s",
					bo.getNum() == null ? "无" : bo.getNum());
			gli.attr("title", numText);
			String src = String.format("./resource/dd/%s?path=home://%s",
					bo.getHeadFile(), bo.getHeadHome());
			gli.select("img").attr("src", src);
			gli.select("p").html(bo.getName());
			gul.appendChild(gli);
		}
		gul.appendChild(addG);
	}

	private List<ChatGroupBO> getMyGroups(String owner, ICube home,
			IServiceosWebsiteModule m) {
		List<ChatGroupBO> list = new ArrayList<>();
		IQuery<ChatGroupBO> q = home.createQuery(
				"select {'tuple':'*'} from tuple ?(colName) ?(clazz) where {'tuple.creator':'?(owner)'}");
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("owner", owner);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		List<IDocument<ChatGroupBO>> result = q.getResultList();
		for (IDocument<ChatGroupBO> doc : result) {
			ChatGroupBO bo = doc.tuple();
			bo.setId(doc.docid());
			list.add(bo);
		}
		return list;
	}

}
