package cj.lns.chip.sos.website.security;

import java.util.List;

import cj.lns.chip.sos.service.sws.security.RoleInfo;
import cj.studio.ecm.graph.CircuitException;

public interface IRoleService {

	List<RoleInfo> getRoles(String swsid)throws CircuitException;



}
