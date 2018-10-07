package cj.lns.chip.sos.website.sws.service;

import java.util.Map;

import cj.studio.ecm.graph.CircuitException;

public interface ISecurityService {
	Map<String, Object>  getSws(String swsid) throws CircuitException ;
	boolean isOwner(String principal, String swsid) throws CircuitException ;
	/**
	 * 是否拥有视窗访问权限
	 * <pre>
	 *
	 * </pre>
	 * @param principal
	 * @param swsid
	 * @return 200许可可用，201许可被拒，202许可不存在，其它则异常
	 */
	int hasSwsVisitPermission(String principal, String swsid,Map<String, Object> fillSws)throws CircuitException;

}
