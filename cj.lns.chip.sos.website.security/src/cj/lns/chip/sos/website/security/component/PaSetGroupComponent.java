package cj.lns.chip.sos.website.security.component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.security.IPaService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/setSelectedGroup.html")
public class PaSetGroupComponent implements IComponent {
	@CjServiceRef(refByName="paService")
	IPaService pa;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String action=frame.parameter("action");
		String groupId=frame.parameter("groupId");
		String perm=frame.parameter("current");
		try {
			perm=URLDecoder.decode(perm,"utf-8");
		} catch (UnsupportedEncodingException e1) {
		}
		String[] permArr=perm.split(";");
		String resourceId=permArr[0];
		String valueId=permArr[1];
		String permCode=permArr[2];
		String permName=permArr[3];
		
		String swsId=IServicewsContext.context(frame).swsid();
		if("allow".equals(action)){
		pa.allowContactGroup(groupId,resourceId,valueId,permCode,permName,swsId);
		}else if("deny".equals(action)){
			pa.denyContactGroup(groupId,resourceId,valueId,permCode,swsId);
		}
	}

}
