package cj.lns.chip.sos.website.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cj.lns.chip.sos.website.framework.IAuthForm;

public  class AuthForm implements IAuthForm {
	protected final Map<String, Object> map;
	public AuthForm() {
		map=new HashMap<String, Object>();
	}
	@Override
	public void load(Map<String, String> map) {
		this.map.putAll(map);
	}
	@Override
	public void put(String key, Object value) {
		map.put(key,value);
	}
	public Object get(String key){
		return map.get(key);
	}
	public boolean containsKey(String key){
		return map.containsKey(key);
	}
	public Set<String> enumKey(){
		return map.keySet();
	}

	@Override
	public String authType() {
		return (String)map.get("authType");
	}

	@Override
	public void authType(String type) {
		map.put("authType", type);
	}


	
}
