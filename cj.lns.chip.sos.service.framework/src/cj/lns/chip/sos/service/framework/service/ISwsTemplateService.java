package cj.lns.chip.sos.service.framework.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.framework.remote.parameter.CreateSuperswsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GetAllSwsTemplateParameters;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.studio.ecm.graph.CircuitException;

public interface ISwsTemplateService {

	List<ServicewsInfo> getAllSwsTemplate(
			GetAllSwsTemplateParameters parameters);

	List<SwsInfo> getAllSupersws();

	BigInteger createSupersws(CreateSuperswsParameters p)
			throws CircuitException;

	void delSupersws(String portalId);

}