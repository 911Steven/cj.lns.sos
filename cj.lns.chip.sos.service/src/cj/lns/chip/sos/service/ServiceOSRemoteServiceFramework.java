package cj.lns.chip.sos.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.RemoteServiceFramework;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
@CjService(name="remoteServiceFramework",isExoteric=true)
public class ServiceOSRemoteServiceFramework extends RemoteServiceFramework {
/*
 * 可做安全令牌认证拦截
 */
	public ServiceOSRemoteServiceFramework() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void invokeRemoteIndexMethod(String url, IRemoteService rs,
			Circuit c) {
		// TODO Auto-generated method stub
		super.invokeRemoteIndexMethod(url, rs, c);
	}
	@Override
	protected RemoteResult invokeRemoteIndexMethod(String url, IRemoteService rs,
			Method m, IRemoteParameters params) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		return super.invokeRemoteIndexMethod(url, rs, m, params);
	}
	@Override
	protected RemoteResult invokeRemoteMethod(String url, IRemoteService rs,
			Method m, IRemoteParameters params) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		return super.invokeRemoteMethod(url, rs, m, params);
	}
}
