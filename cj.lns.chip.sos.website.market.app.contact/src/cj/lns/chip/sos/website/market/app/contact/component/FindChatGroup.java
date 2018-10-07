package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.cube.framework.Coordinate;
import cj.lns.chip.sos.cube.framework.FileInfo;
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
import cj.studio.ecm.net.web.WebUtil;
import cj.ultimate.util.StringUtil;

@CjService(name = "/findChatGroup.service")
public class FindChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		Document doc = m.context().html("/contact/findChatGroup.html",
				m.site().contextPath(), "utf-8");
		String action = frame.parameter("action");
		if ("find".equals(action)) {// 执行查询
			byte[] b=frame.content().readFully();
			String str=new String(b);
			Map<String,Object> map=WebUtil.parserParam(str);
			String value=(String)map.get("value");
			ChatGroupBO bo=null;
			if("0".equals(map.get("by"))){//群号，1是群名
				 bo=findGroupByNum(value,home);
			}else if("1".equals(map.get("by"))){
				 bo=findGroupByName(value,home);
			}
			if(bo==null){
				return;
			}
			List<ChatGroupBO> groups = Arrays.asList(bo);
			printGroups(doc, groups);
			circuit.content().writeBytes(
					doc.select(".find-g>.result").html().getBytes());
			return;
		}
		if ("view".equals(action)) {// 显示群明细
			String gid = frame.parameter("gid");
			ChatGroupBO bo = findGroup(gid, home);
			if (bo == null)
				return;
			Element ul = doc.select(".find-g>.result").first();
			Element panel = ul.select(".gitem>.gpanel").first().clone();
			printGroupDetails(sws.owner(), gid, bo, panel, home);
			circuit.content().writeBytes(panel.html().getBytes());
			return;
		}
		
		List<ChatGroupBO> groups = getGroups(sws.owner(),
				frame.parameter("sikp"), doc, home);
		printGroups(doc, groups);
		if (frame.containsParameter("sikp")
				&& Long.valueOf(frame.parameter("skip")) > 0) {
			circuit.content().writeBytes(
					doc.select(".find-g>.result").html().getBytes());
		} else {
			circuit.content().writeBytes(doc.toString().getBytes());
		}
	}

	private ChatGroupBO findGroupByName(String name,ICube home) {
		String cjql = "select {'tuple':'*'} from tuple ?(colName) ?(clazz) where {'tuple.name':'?(name)'}";
		IQuery<ChatGroupBO> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("name", name);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		IDocument<ChatGroupBO> bo = q.getSingleResult();
		if (bo == null)
			return null;
		ChatGroupBO cgbo = bo.tuple();
		cgbo.setId(bo.docid());
		return cgbo;
	}

	private ChatGroupBO findGroupByNum(String num,ICube home) {
		String cjql = "select {'tuple':'*'} from tuple ?(colName) ?(clazz) where {'tuple.num':'?(num)'}";
		IQuery<ChatGroupBO> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("num", num);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		IDocument<ChatGroupBO> bo = q.getSingleResult();
		if (bo == null)
			return null;
		ChatGroupBO cgbo = bo.tuple();
		cgbo.setId(bo.docid());
		return cgbo;
	}

	private void printGroupDetails(String owner, String gid, ChatGroupBO bo,
			Element panel, ICube home) {
		panel.select(">li[creator]>a").html(bo.getCreator());
		panel.select(">li[num]>a").html(bo.getNum());
		String security = "";
		if (bo.getSecurity() == 1) {
			security = "需验证";
		} else if (bo.getSecurity() == 0) {
			security = "公开";
		} else {
			security = "-";
		}
		panel.select(">li[securiy]>span[jj]").html(security);
		panel.select(">li[intr]>p").html(bo.getIntroduce());
		
		String cjql = "select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {'tuple.gid':'?(gid)','tuple.user':'?(user)'}";
		IQuery<Long> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_USER_NAME);
		q.setParameter("user", owner);
		q.setParameter("gid", gid);
		if(q.count()>0){
			panel.select(">li[member]>label").attr("style","color:gray");
			panel.select(">li[member]>label").html("我已加入");
		}
		// 以下是统计
		cjql = "select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {'tuple.gid':'?(gid)'}";
		q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_USER_NAME);
		q.setParameter("gid", gid);
		panel.select(">li[member]>a").html(String.valueOf(q.count()));
		bo.getFileHome();

		// <span class='label'>资产：</span><a href='#'>照片|100</a><a
		// href='#'>文件|%s</a><a href='#'>信息|1万</a>
		String tp = String.format("{'dir':'%s'}", bo.getPicHome());
		Map<String, Coordinate> coords = home.parseCoordinate(tp);
		List<FileInfo> pics = home.fileSystem()
				.listFilesByCoordinate(coords, true);

		tp = String.format("{'dir':'%s'}", bo.getFileHome());
		coords = home.parseCoordinate(tp);
		List<FileInfo> files = home.fileSystem()
				.listFilesByCoordinate(coords, true);

		tp = String.format("{'dir':'%s'}", bo.getHeadHome());
		coords = home.parseCoordinate(tp);
		List<FileInfo> headPics = home.fileSystem()
				.listFilesByCoordinate(coords, true);
		
		cjql = "select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {}";
		q = home.createQuery(cjql);
		q.setParameter("colName", bo.getMsgColName());
		long msgTotal = q.count();

		
		String html = String.format(
				"<span class='label'>资产：</span><a	href='#'>消息|%s</a><a href='#'>群头像|%s</a><a href='#'>照片|%s</a><a	href='#'>文件|%s</a>",msgTotal,headPics.size(),
				pics.size(), files.size());
		panel.select(">li[prop]").html(html);

	}

	private ChatGroupBO findGroup(String gid, ICube home) {
		String cjql = "select {'tuple':'*'} from tuple ?(colName) ?(clazz) where {'_id':ObjectId('?(gid)')}";
		IQuery<ChatGroupBO> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("gid", gid);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		IDocument<ChatGroupBO> bo = q.getSingleResult();
		if (bo == null)
			return null;
		ChatGroupBO cgbo = bo.tuple();
		cgbo.setId(bo.docid());
		return cgbo;
	}

	private void printGroups(Document doc, List<ChatGroupBO> groups) {
		Element ul = doc.select(".find-g>.result").first();
		Element li = ul.select(".gitem").first().clone();
		ul.empty();
		for (ChatGroupBO bo : groups) {
			li = li.clone();
			li.attr("title", String.format("群号:%s", bo.getNum()));
			li.attr("gid", bo.getId());
			li.attr("security", String.valueOf(bo.getSecurity()));
			String src = String.format("./resource/dd/%s?path=home://%s",
					bo.getHeadFile(), bo.getHeadHome());
			li.select(">img.gface").attr("src", src);
			li.select(">.gtext>.gname").html(bo.getName());
			li.select(">.gtext>.gintr").html(bo.getIntroduce());
			ul.appendChild(li);
		}
	}

	private List<ChatGroupBO> getGroups(String owner, String skip, Document doc,
			ICube home) {

		if (StringUtil.isEmpty(skip)) {
			skip = "0";
		}
		List<ChatGroupBO> list = new ArrayList<>();
		String cjql = String.format(
				"select {'tuple':'*'}.sort({'tuple.createTime':-1}).skip(%s).limit(10) from tuple ?(colName) ?(clazz) where {'tuple.creator':{$ne:'?(creator)'}}",
				skip);
		IQuery<ChatGroupBO> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_NAME);
		q.setParameter("clazz", ChatGroupBO.class.getName());
		q.setParameter("creator", owner);
		Thread.currentThread()
				.setContextClassLoader(this.getClass().getClassLoader());
		List<IDocument<ChatGroupBO>> result = q.getResultList();
		for (IDocument<ChatGroupBO> tuple : result) {
			ChatGroupBO bo = tuple.tuple();
			bo.setId(tuple.docid());
			list.add(bo);
		}
		return list;
	}

}
