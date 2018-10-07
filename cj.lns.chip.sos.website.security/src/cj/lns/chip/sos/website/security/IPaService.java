package cj.lns.chip.sos.website.security;

import java.util.List;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.service.sws.security.AuthLoggerInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.studio.ecm.graph.CircuitException;

/**
 * 分配权限服务
 * 
 * <pre>
 *	~ 是安全中心的核心服务，用于为授权界面提供功能
 * </pre>
 * 
 * @author carocean
 *
 */
public interface IPaService {

	void allowRole(String resourceId, String valueId, String permCode,
			String permName, String roleCode, String swsId)
					throws CircuitException;

	void allowContactGroup(String groupId, String resourceId, String valueId,
			String permCode, String permName, String swsId)
					throws CircuitException;

	void denyContactGroup(String groupId, String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException;

	void allowContacts(String users, String resourceId, String valueId,
			String permCode, String permName, String swsId)
					throws CircuitException;

	void denyContacts(String users, String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException;

	List<ContactInfo> getAllowContacts(String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException;

	List<ContactInfo> getDenyContacts(String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException;

	List<AuthAskInfo> getQuestionList(String resourceId, String valueId,
			String permCode, String swsid) throws CircuitException;

	List<AuthLoggerInfo> getAuthLogger(String resourceId, String valueId,
			String permCode, String swsid);

	AuthAskInfo addQuestion(String a, String q, String resourceId, String valueId,
			String permCode, String permName, String swsId) throws CircuitException;

	void delQuestion(String aqid,String a,String q, String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException;

	void updateQuestion(String aqid, String a, String q, String resourceId,
			String valueId, String permCode, String swsId) throws CircuitException;


	

}
