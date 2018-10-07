package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.common.sos.service.model.sws.SwsAuthLogger;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
@CjBridge(aspects = "logging+transaction")
@CjService(name = "authLogger")
public class AuthLogger implements IAuthLogger{

	@Override
	public void write(SwsAuthLogger logger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(BigInteger id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SwsAuthLogger> getOwnerLoggers(ServicewsAuthMethod method,
			BigInteger swsid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SwsAuthLogger> getOwnerLoggers(BigInteger swsid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SwsAuthLogger> getSenderLoggers(ServicewsAuthMethod method,
			String sender, BigInteger permId, BigInteger swsid) {
		// TODO Auto-generated method stub
		return null;
	}

}
