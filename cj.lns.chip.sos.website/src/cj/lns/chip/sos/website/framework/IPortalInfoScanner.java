package cj.lns.chip.sos.website.framework;

import java.util.Map;

import cj.lns.chip.sos.website.framework.info.PortalInfo;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.ultimate.IDisposable;
@CjExotericalType
public interface IPortalInfoScanner extends IDisposable{
	Map<String, PortalInfo> getPortals();
}
