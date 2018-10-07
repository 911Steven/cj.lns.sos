package cj.lns.chip.sos.website.auth.component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.auth.IAuthFactory;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

/**
 * 认证组件
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "/applyServicews.html")
public class ApplyServicews implements IComponent {
	@CjServiceRef(refByName = "authFactory")
	IAuthFactory factory;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/applyServicews.html",
				m.site().contextPath(), "utf-8");
		String kind = frame.parameter("kind");
		String b = "3";
		String type = "";
		switch (kind) {
		case "ssws":
			b = "0";
			type = "超级视窗";
			break;
		case "bsws":
			b = "1";
			type = "基础视窗";
			break;
		case "csws":
			b = "2";
			type = "公共视窗";
			break;
		case "psws":
			b = "3";
			type = "个人视窗";
			break;
		default:
			throw new CircuitException("503",
					String.format("不认识的视窗类型：%s", kind));
		}
		String skip = frame.parameter("skip");
		if (StringUtil.isEmpty(skip)) {
			skip = "0";
		}
		String limit = frame.parameter("limit");
		if (StringUtil.isEmpty(limit)) {
			limit = "10";
		}
		String swsid = frame.parameter("swsid");
		String swsName = frame.parameter("swsName");
		if (!StringUtil.isEmpty(swsName)) {
			try {
				swsName = URLDecoder.decode(swsName, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		List<Object> swsList = getSwsList(b, skip, limit, swsid, swsName);
		printList(swsList, doc, type, kind);
		if ("0".equals(skip) && StringUtil.isEmpty(swsName)
				&& StringUtil.isEmpty(swsid)&&!"true".equals(frame.parameter("search"))) {
			circuit.content().writeBytes(doc.toString().getBytes());
		} else {
			Element ul = doc.select(".new-sws > .left > .sws-table").first();
			circuit.content().writeBytes(ul.html().getBytes());
		}
	}

	private void printList(List<Object> swsList, Document doc, String type,
			String kind) {
		doc.select(".new-sws > .left > .top > .label").html(type);
		doc.select(".new-sws > .left > .top > .label").attr("kind", kind);
		Element ul = doc.select(".new-sws > .left > .sws-table").first();
		Element li = ul.select(">li").first().clone();
		ul.empty();// {owner=wuj, level=0.0, name=开发平台, description=开发平台门户模板,
					// inheritId=1.0, id=11.0, usePortal=cjdk}
		for (Object o : swsList) {
			Map<String, Object> map = (Map<String, Object>) o;
			if("computer".equals(map.get("platform"))&&"csws".equals(kind)){
				continue;
			}
				
			li = li.clone();
			li.attr("swsid", String.format("%.0f", map.get("id")));
			String src = (String)map.get("faceImg");
			if(StringUtil.isEmpty(src)){
			src=String.format("../%s/module-icon.svg",map.get("usePortal"));
			}else{
				src=String.format("../resource/ud/%s?path=%s://system/faces&u=%s", (String)map.get("faceImg"),String.format("%.0f", map.get("id")),map.get("owner"));
			}
			li.select(">img").attr("src", src);
			li.select(">.face>li[name]").html((String) map.get("name"));
			if (map.get("description") != null) {
				li.select(">.face>li[sign]")
						.html((String) map.get("description"));
			}
			li.select("> .desc > li>span[text]")
					.html((String) map.get("owner"));
			ul.appendChild(li);
		}
	}

	protected List<Object> getSwsList(String level, String skip, String limit,
			String swsid, String swsName) throws CircuitException {
		// 查询指定类型的所有视窗，并分页
		Frame frame = new Frame("findServicews /sws/instance sos/1.0");
		frame.parameter("level", level);
		frame.parameter("skip", skip);
		frame.parameter("limit", limit);
		frame.parameter("swsid", swsid);
		frame.parameter("swsName", swsName);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state == 404) {
				return new ArrayList<>();
			}
			if (state != 200) {
				throw new CircuitException("503",
						String.format("查找视窗失败:", back.head("message")));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json,
					new TypeToken<List<HashMap<String, Object>>>() {
					}.getType());

		}
		return new ArrayList<>();
	}

}
