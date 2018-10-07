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

@CjService(name = "/setSelectedContact.html")
public class PaSetContactComponent implements IComponent {
	@CjServiceRef(refByName = "paService")
	IPaService pa;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		String users = frame.parameter("users");
		String action = frame.parameter("action");
		String perm = frame.parameter("current");
		String[] permArr = perm.split(";");
		String resourceId = permArr[0];
		String valueId = permArr[1];
		String permCode = permArr[2];
		String permName = "";
		try {
			permName = URLDecoder.decode(permArr[3], "uft-8");
		} catch (UnsupportedEncodingException e) {
		}
		String swsId = IServicewsContext.context(frame).swsid();
		if ("allow".equals(action)) {
			pa.allowContacts(users, resourceId, valueId, permCode,
					permName, swsId);
		} else if ("deny".equals(action)) {
			pa.denyContacts(users, resourceId, valueId, permCode, swsId);
		}
	}

}
