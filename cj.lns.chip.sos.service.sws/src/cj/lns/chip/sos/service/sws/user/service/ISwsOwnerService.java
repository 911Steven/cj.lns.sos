package cj.lns.chip.sos.service.sws.user.service;

import java.util.List;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.remote.parameter.GetOwnerParameters;
import cj.studio.ecm.graph.CircuitException;

/**
 * sws系统的持有人信息
 * 
 * <pre>
 * 注：持有者可能是用户、机构、社区（群）、版块、论坛等，它总被一个用户创建，即超级用户,SWS是超级用户的私产。
 *    超级用户是否委托给管理员角色来管理另当别论，但此处的superUserCode即超级用户
 *    
 *    因此：在持有者表里，如果是用户，则不必使用swsId而，superUserCode即可定位到某个swsId上
 * </pre>
 * 
 * @author carocean
 *
 */
public interface ISwsOwnerService {
	/**
	 * 获取sws持有者视窗信息
	 * 
	 * <pre>
	 * 
	 * </pre>
	 * 
	 * @param superUserCode
	 * @return
	 */
	SosUserInfo getServicews(String superUserCode)throws CircuitException;


	SosUserInfo getAllSws(String userCode)throws CircuitException;

	void setDefaultSws(String userCode, String swsid)throws CircuitException;


	SosUserInfo getOwnerInfo(GetOwnerParameters parameters) throws CircuitException;


	List<String> getAllSwsid(String userCode);

}
