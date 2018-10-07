package cj.lns.chip.sos.website.sws.service;

import java.util.List;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.studio.ecm.graph.CircuitException;

public interface IAuthAnswerService {

	List<AuthAskInfo> getAskList(String swsid, String permId)throws CircuitException ;

	void auth(String askId, String answer)throws CircuitException;

}
