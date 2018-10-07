package cj.lns.chip.sos.website.sws.component;

import java.text.DecimalFormat;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.sws.ServicewsBody;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjService(name = "/components/swsInfo.html")
public class SwsInfoComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/components/swsInfo.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext sws = IServicewsContext.context(frame);
		INetDisk disk = m.site().diskOwner(sws.owner());
		ICube cube = disk.cube(sws.swsid());
		ServicewsBody info = getServicews(sws.swsid(), circuit);
		printView(sws, info, cube, doc);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	public ServicewsBody getServicews(String swsid, Circuit circuit2)
			throws CircuitException {

		Frame frame = new Frame("getServicewsBody /sws/instance sos/1.0");
		frame.parameter("swsid", swsid);
		Circuit circuit = new Circuit("sos/1.0 200 ok");

		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		int state = Integer.valueOf(back.head("status"));
		if (state != 200) {
			throw new CircuitException(back.head("status"),
					String.format("获取视窗失败：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		ServicewsBody body = new Gson().fromJson(json, ServicewsBody.class);
		if (StringUtil.isEmpty(body.getFaceImg())) {
			body.setFaceImg("../cjdk/module-icon.svg");
		} else {
			String src = String.format(
					"../resource/ud/%s?path=%s://system/faces&u=%s",
					body.getFaceImg(), swsid, body.getOwner());
			body.setFaceImg(src);
		}
		if (body.getExtra().isEmpty()) {
			body.getExtra().put("intro", body.getSwsDesc());
		}
		return body;
	}

	private void printView(IServicewsContext sws, ServicewsBody si, ICube cube,
			Document doc) {
		//CubeConfig conf = cube.config();
		
		Element extraul=doc.select(".sws-cnt-i > ul > li.i-section > ul").first();
		Element extrali=extraul.select(">li").first().clone();
		extraul.empty();
		
		Map<String,Object> extra=si.getExtra();
		Element eli=null;
		for(String key:extra.keySet()){
			Object obj=extra.get(key);
			switch(key){
			case "intro":
				eli=extrali.clone();
				eli.select(">span[section]").html("简介");
				eli.select(">div").html(obj+"");
				extraul.appendChild(eli);
				break;
			case "hobby":
				eli=extrali.clone();
				eli.select(">span[section]").html("兴趣/爱好");
				eli.select(">div").html(obj+"");
				extraul.appendChild(eli);
				break;
			case "crop":
				eli=extrali.clone();
				eli.select(">span[section]").html("公司");
				eli.select(">div").html(obj+"");
				extraul.appendChild(eli);
				break;
			case "home":
				eli=extrali.clone();
				eli.select(">span[section]").html("主页");
				eli.select(">div").html(obj+"");
				extraul.appendChild(eli);
				break;
			case "address":
				eli=extrali.clone();
				eli.select(">span[section]").html("地址");
				eli.select(">div").html(obj+"");
				extraul.appendChild(eli);
				break;
			}
		}
		
		doc.select(".sws-cnt-i > ul > li.i-welcome > a")
				.html(sws.visitor().principal());
		String type = "";
		switch (si.getLevel()) {
		case 0:
			type = "超级视窗";
			break;
		case 1:
			type = "基础视窗";
			break;
		case 2:
			type = "公共视窗";
			break;
		default:
			type = "个人视窗";
			break;
		}
		doc.select(".sws-cnt-i > ul > li.i-level > p").html(type);

		Element ul = doc.select(".sws-cnt-i > ul > li.i-total > ul").first();
		Element li = ul.select(">li.i-top-total").first();
		Element ta = li.select(">a").first().clone();
		li.empty();

		double per = (si.getDataSize() / si.getCapacity()) * 100;
		Element a = ta.clone();
		a.select(">span[top]").html("空间已用");
		DecimalFormat df = new DecimalFormat(".##");
		a.select(">span[bottom]").html(df.format(per) + "%");
		li.appendChild(a);
		
		String cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {}";
		IQuery<Long> q=cube.createQuery(cjql);
		 a = ta.clone();
		a.select(">span[top]").html("总访问量");
		a.select(">span[bottom]").html(q.count() + "");
		li.appendChild(a);
		
		li = ul.select(">li.i-app-total").first();
		ta = li.select(">a").first().clone();
		li.empty();
		
		 cjql="select {'tuple':'*'}.count() from tuple article.entities java.lang.Long where {}";
		 q=cube.createQuery(cjql);
		a = ta.clone();
		a.select(">span[top]").html("博客");
		a.select(">span[bottom]").html(q.count() + "");
		li.appendChild(a);
		
		cjql="select {'tuple':'*'}.count() from tuple product.entities java.lang.Long where {}";
		 q=cube.createQuery(cjql);
		a = ta.clone();
		a.select(">span[top]").html("产品");
		a.select(">span[bottom]").html(q.count() + "");
		li.appendChild(a);
		
		cjql="select {'tuple':'*'}.count() from tuple iobox java.lang.Long where {'tuple.iobox':'inbox'}";
		 q=cube.createQuery(cjql);
		a = ta.clone();
		a.select(">span[top]").html("收件");
		a.select(">span[bottom]").html(q.count() + "");
		li.appendChild(a);
		
		cjql="select {'tuple':'*'}.count() from tuple iobox java.lang.Long where {'tuple.iobox':'outbox'}";
		 q=cube.createQuery(cjql);
		a = ta.clone();
		a.select(">span[top]").html("发件");
		a.select(">span[bottom]").html(q.count() + "");
		li.appendChild(a);
		
		a = ta.clone();
		a.select(">span[top]").html("文件数");
		a.select(">span[bottom]").html(cube.fileSystem().filesTotal() + "");
		li.appendChild(a);
	}

}
