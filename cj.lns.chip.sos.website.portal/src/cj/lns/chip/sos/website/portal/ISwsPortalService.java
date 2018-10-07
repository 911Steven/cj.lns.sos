package cj.lns.chip.sos.website.portal;

import java.util.Map;

import cj.studio.ecm.graph.CircuitException;

public interface ISwsPortalService {

	Map<String, String> getConfig(String swsid)throws CircuitException;

}
