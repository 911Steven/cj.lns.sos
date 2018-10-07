package cj.lns.chip.sos.website.security.component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.security.IPaService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/authAskSet.html")
public class AuthAskSetComponent implements IComponent {
	@CjServiceRef(refByName = "paService")
	IPaService pa;

	@Override
	public void flow(Frame frame, Circuit circuit,IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		String aqid = frame.parameter("aqid");
		String a = frame.parameter("a");
		String q = frame.parameter("q");
		String action = frame.parameter("action");
		String perm = frame.parameter("current");
		try {
			perm=URLDecoder.decode(perm,"utf-8");
		} catch (UnsupportedEncodingException e1) {
		}
		String[] permArr = perm.split(";");
		String resourceId = permArr[0];
		String valueId = permArr[1];
		String permCode = permArr[2];
		String permName = permArr[3];
		
		String swsId = IServicewsContext.context(frame).swsid();
		if ("new".equals(action)) {
			AuthAskInfo ai = pa.addQuestion(a, q, resourceId, valueId, permCode,
					permName, swsId);
			if (ai != null)
				circuit.content()
						.writeBytes(new Gson().toJson(ai).getBytes());
		} else if ("del".equals(action)) {
			pa.delQuestion(aqid, a, q, resourceId, valueId, permCode, swsId);
		} else if ("update".equals(action)) {
			pa.updateQuestion(aqid, a, q, resourceId, valueId, permCode, swsId);
		}
	}

}
