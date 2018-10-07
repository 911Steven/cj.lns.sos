package cj.lns.chip.sos.website.framework;

import java.util.Map;
import java.util.Set;

import cj.studio.ecm.annotation.CjExotericalType;
@CjExotericalType
public interface IAuthForm {
	void put(String key, Object value);
	public Object get(String key);
	public boolean containsKey(String key);
	public Set<String> enumKey();
	String authType();
	void authType(String type);
	void load(Map<String, String> map);
}
