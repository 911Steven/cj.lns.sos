package cj.lns.chip.sos.website.market.app.contact.component;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.cube.framework.FileInfo;
import cj.lns.chip.sos.cube.framework.FileSystem;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IReader;
import cj.lns.chip.sos.cube.framework.IWriter;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.cube.framework.lock.FileLockException;
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

@CjService(name = "/createChatGroup.service")
public class CreateChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		INetDisk userdisk = m.site().diskOwner(sws.owner());
		ICube userHome = userdisk.home();

		if ("create".equals(frame.parameter("action"))) {
			createGroup(home, userHome, sws.owner(), frame);
			return;
		}
		Document doc = m.context().html("/contact/createChatGroup.html",
				m.site().contextPath(), "utf-8");
		String num = genChatGroupNum(m);
		doc.select(".cg-panel>ul>li[label=num]>input").val(num);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private String genChatGroupNum(IServiceosWebsiteModule m)
			throws CircuitException {
		/// genNum framework/idgen/ sos/1.0
		Frame frame = new Frame(
				"genNum /framework/idgen/?subject=chatGroup sos/1.0");
		Circuit circuit = new Circuit(String.format("sos/1.0 200 ok"));
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					back.head("message"));
		}
		return back.head("num");
	}

	private void createGroup(ICube home, ICube userHome, String owner,
			Frame frame) throws CircuitException {

		FileSystem fs = home.fileSystem();

		byte[] b = frame.content().readFully();
		Map<String, Object> map = WebUtil.parserParam(new String(b));
		String name = (String) map.get("name");
		String picpath = (String) map.get("path");
		FileInfo from = null;
		try {
			/// resource/dd/guangjiewang.svg?path=home:///chatGroups/576655d143228da4e93af9fc/head"
			from = userHome.fileSystem().openFile(picpath);
		} catch (FileNotFoundException | FileLockException e) {
			throw new CircuitException("404", e.getMessage());
		}

		String introduce = (String) map.get("introduce");
		String num = (String) map.get("num");
		String creator = owner;// 群主用户代码
		long createTime = System.currentTimeMillis();
		ChatGroupBO bo = new ChatGroupBO();
		bo.setCreateTime(createTime);
		bo.setCreator(creator);
		bo.setIntroduce(introduce);
		bo.setNum(num);
		bo.setName(name);
		bo.setHeadFile(from.name());
		String id = home.saveDoc(ChatGroupBO.KEY_COL_NAME,
				new TupleDocument<ChatGroupBO>(bo));
		bo.setId(id);
		

		// 创建者肯定是成员
		Map<String, Object> users = new LinkedHashMap<>();
		users.put("user", owner);
		users.put("joinTime", System.currentTimeMillis());
		users.put("gid", id);
		home.saveDoc(ChatGroupBO.KEY_COL_USER_NAME, new TupleDocument<>(users));

		fs.dir(bo.getFileHome())
				.mkdir(String.format("群[%s]的文件目录", bo.getName()));
		fs.dir(bo.getPicHome())
				.mkdir(String.format("群[%s]的相册目录", bo.getName()));
		fs.dir(bo.getHeadHome())
				.mkdir(String.format("群[%s]的头像目录", bo.getName()));

		// 考群头像：自当前用户的主空间，考到数据空间
				String newFileName = String.format("/chatGroups/%s/head/%s", id,
						from.name());
				try {
					IReader r = from.reader(0);
					FileInfo to = fs.openFile(newFileName);
					IWriter w=to.writer(0);
					int readlen=0;
					byte[] buf=new  byte[8096];
					while((readlen=r.read(buf))>-1){
						w.write(buf,0,readlen);
					}
					w.close();
					r.close();
				} catch (FileNotFoundException | FileLockException e) {
					throw new CircuitException("404", e.getMessage());
				}
	}

}
