package cj.lns.chip.sos.website.security;

import java.util.List;

import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.studio.ecm.graph.CircuitException;

public interface IContactGroupService {

	List<ContactGroupInfo> getContactGroups(String swsid)throws CircuitException;


	List<ContactInfo> getContactsByGroup(String gid, String swsid)
			throws CircuitException;

}
