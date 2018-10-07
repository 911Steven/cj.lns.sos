package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.cube.framework.TupleDocument;
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

@CjService(name = "/joinChatGroup.service")
public class JoinChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		String gid=frame.parameter("gid");
		
		String cjql = "select {'tuple':'*'}.count() from tuple ?(colName) java.lang.Long where {'tuple.gid':'?(gid)','tuple.user':'?(user)'}";
		IQuery<Long> q = home.createQuery(cjql);
		q.setParameter("colName", ChatGroupBO.KEY_COL_USER_NAME);
		q.setParameter("user", sws.owner());
		q.setParameter("gid", gid);
		if(q.count()>0){//我已加入则返回
			circuit.content().writeBytes("你已在群中，不可重复加入".getBytes());
			return;
		}
		
		Map<String,Object> map=new HashMap<>();
		map.put("gid", gid);
		map.put("user", sws.owner());
		map.put("joinTime", System.currentTimeMillis());
		TupleDocument<Map<String,Object>> newdoc=new TupleDocument<Map<String,Object>>(map);
		home.saveDoc(ChatGroupBO.KEY_COL_USER_NAME,  newdoc);
	}

}
