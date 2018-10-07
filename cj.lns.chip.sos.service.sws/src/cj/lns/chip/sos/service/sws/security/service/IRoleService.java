package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsRole;

public interface IRoleService {
	boolean containsUser(BigInteger swsId,String role, String userCode);
	BigInteger addRole(BigInteger swsId,String roleCode,String roleName,String desc);
	void deleteRole(BigInteger swsId,String roleCode);
	List<SwsRole> getRoles(BigInteger swsId);
	
	void addUsers(BigInteger swsId,String roleCode,List<String> userCodes);
	void removeUsers(BigInteger swsId,String roleCode,List<String> userCodes);
	List<SwsContact> getContacts(BigInteger swsId,String roleCode);
	boolean isServicewsContacts(String userCode, BigInteger swsId);
	boolean isServicewsOwner(String userCode, BigInteger swsId);
	boolean isServiceosUser(String userCode);
}
