package cj.lns.chip.sos.service.sws.market.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindAppPhyIdParameters extends AppParameters{
	@CjRemoteParameter(must = true, usage = "应用物理标识")
	BigInteger phyId;
	public BigInteger getPhyId() {
		return phyId;
	}
	public void setPhyId(BigInteger phyId) {
		this.phyId = phyId;
	}
}
