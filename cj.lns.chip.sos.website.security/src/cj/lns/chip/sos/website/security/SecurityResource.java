package cj.lns.chip.sos.website.security;

import java.util.HashMap;
import java.util.Map;

class SecurityResource implements ISecurityResource {
	Map<String, ISecurityResourceImpl> fixed;

	public SecurityResource() {
		fixed = new HashMap<>();
	}
	@Override
	public void add(String valueId, ISecurityResourceImpl impl) {
		// TODO Auto-generated method stub
		fixed.put(valueId, impl);
	}
	@Override
	public String[] enumResourceImpl() {
		return fixed.keySet().toArray(new String[0]);
	}

	@Override
	public ISecurityResourceImpl resourceImpl(String valueId) {
		return fixed.get(valueId);
	}

	@Override
	public boolean containsResourceImpl(String valueId) {
		return fixed.containsKey(valueId);
	}

}
