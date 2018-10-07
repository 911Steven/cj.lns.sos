package cj.lns.chip.sos.service.framework.service;

import java.util.List;

import cj.lns.chip.sos.service.framework.remote.parameter.CreateServicewsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.ExistsSwsTemplateInPortalParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FindPortalInfoParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FrameworkReporterParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.SetUserRegisterDefaultTemplateParameters;
import cj.lns.chip.sos.service.portal.PortalInfo;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.studio.ecm.graph.CircuitException;

public interface IFrameworkService {

	void createServicews(CreateServicewsParameters parameters)
			throws CircuitException;

	PortalInfo findPortalInfo(FindPortalInfoParameters parameters)
			throws CircuitException;

	boolean existsSwsTemplateInPortal(
			ExistsSwsTemplateInPortalParameters parameters)
			throws CircuitException;

	List<?> getSosVersion();

	List<PortalInfo> getPortals() throws CircuitException;

	/**
	 * 用户注册时会列出框架，其中默认模板关联的框架为默认框架，注册后便以此视窗模板实例化
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	void setUserRegisterDefaultTemplate(
			SetUserRegisterDefaultTemplateParameters parameters)
			throws CircuitException;

	void report(FrameworkReporterParameters parameters) throws CircuitException;

}