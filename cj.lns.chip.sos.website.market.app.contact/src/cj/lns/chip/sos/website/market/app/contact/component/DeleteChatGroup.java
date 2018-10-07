package cj.lns.chip.sos.website.market.app.contact.component;

import cj.lns.chip.sos.cube.framework.FileSystem;
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

@CjService(name = "/deleteChatGroup.service")
public class DeleteChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		INetDisk userdisk = m.site().diskOwner(sws.owner());
		ICube userHome = userdisk.home();
		String gid = frame.parameter("gid");

		IQuery<ChatGroupBO> q = home.createQuery(String.format(
				"select {'tuple':'*'} from tuple %s %s where {'_id':ObjectId('%s')}",
				ChatGroupBO.KEY_COL_NAME, ChatGroupBO.class.getName(), gid));
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		IDocument<ChatGroupBO> doc = q.getSingleResult();
		ChatGroupBO bo = doc.tuple();

		String whereBson = String.format("{'_id':ObjectId('%s')}", gid);
		home.deleteDocs(ChatGroupBO.KEY_COL_NAME, whereBson);
		whereBson = String.format("{'tuple.gid':'%s'}", gid);
		home.deleteDocs(ChatGroupBO.KEY_COL_USER_NAME, whereBson);

		FileSystem fs = userHome.fileSystem();
		fs.dir(bo.getFileHome()).delete();
		fs.dir(bo.getPicHome()).delete();
	}

}
