package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.chip.sos.service.sws.security.remote.AllowGroupParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowRoleParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyGroupParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetObjectAclsParameters;
import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsPa;
import cj.lns.common.sos.service.model.sws.SwsPd;
import cj.lns.common.sos.service.model.sws.SwsPermission;
import cj.studio.ecm.graph.CircuitException;

/**
 * 许可
 * 
 * <pre>
 * 本方案采用面向许可的授权
 * 
 * －资源对象的某个许可什么主体授予充许权限
 * －资源对象的某个许可拒绝哪个用户对该许可的权限
 * 
 * 目的：
 * 当联系人访问某个资源时，计算出其对该资源拥有哪些许可
 * </pre>
 * 
 * @author carocean
 *
 */
public interface IPermissionService {
	/**
	 * 当联系人访问某个资源时，计算出其对该资源拥有哪些许可，这是权限控制的目的
	 * 
	 * <pre>
	 * 关联充许表、权限表、拒绝表三者联表查询
	 * </pre>
	 * 
	 * @param swsid
	 * @param userCode
	 * @return
	 */
	List<SwsPermission> getUserPermissions(BigInteger swsid, String userCode,
			String objectName, String objectInst) throws CircuitException;

	/**
	 * 当联系人访问某个资源时，计算出其对该资源拥有哪个许可是否有权限，如果为空则对该对象的这个许可没有权限
	 * 
	 * <pre>
	 * 关联充许表、权限表、拒绝表三者联表查询
	 * </pre>
	 * 
	 * @param swsid
	 * @param userCode
	 * @return
	 */
	SwsPermission getUserPermissions(BigInteger swsid, String userCode,
			String objectName, String objectInst, String permissionCode)
			throws CircuitException;

	void addPermission(SwsPermission permission) throws CircuitException;

	void removePermission(BigInteger permissionId) throws CircuitException;

	void clearPermission(BigInteger permissionId) throws CircuitException;

	SwsPermission find(BigInteger swsId, String objectName, String objectInst,
			String permissionCode) throws CircuitException;

	void allow(BigInteger permissionId, String subjectName, String subjectInst)
			throws CircuitException;

	boolean isAllow(BigInteger permissionId, String subjectName,
			String subjectInst) throws CircuitException;

	int removeAllow(BigInteger permissionId, String subjectName,
			String subjectInst) throws CircuitException;

	int clearAllows(BigInteger permissionId) throws CircuitException;

	void deny(BigInteger permissionId, String userCode) throws CircuitException;

	boolean isDeny(BigInteger permissionId, String userCode)
			throws CircuitException;

	void clearDenies(BigInteger permissionId) throws CircuitException;

	void removeDeny(BigInteger permissionId, String userCode)
			throws CircuitException;

	List<SwsPa> getAllows(BigInteger permissionId) throws CircuitException;

	List<SwsPd> getDenies(BigInteger permissionId) throws CircuitException;

	void setAuthMethod(BigInteger permissionId, ServicewsAuthMethod method)
			throws CircuitException;

	Acl getObjectAcl(BigInteger swsid, String objectName,
			String objectInst,String permissionCode);

	void allowRole(AllowRoleParameters parameters);

	void allowGroup(AllowGroupParameters parameters);

	void denyGroup(DenyGroupParameters p);

	void allowContacts(AllowContactsParameters parameters);

	void denyContacts(DenyContactsParameters parameters);


	List<SwsContact> getAllowContacts(GetContactsParameters parameters);

	List<SwsContact> getDenyContacts(GetContactsParameters parameters);

	List<Acl> getObjectAcls(GetObjectAclsParameters p) throws CircuitException;
	
}
