package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;

import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
@CjBridge(aspects = "logging+transaction")
@CjService(name = "authPasswordService")
public class AuthPasswordService implements IAuthPasswordService{

	@Override
	public void setPassword(String who, String password, BigInteger swsid)
			throws CircuitException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePassword(String who, BigInteger swsid)
			throws CircuitException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean auth(BigInteger passwordId, String password)
			throws CircuitException {
		// TODO Auto-generated method stub
		return false;
	}

}
