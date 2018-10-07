package cj.lns.chip.sos.website.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.ISubject;

public class Subject implements ISubject,Serializable {
	List<String> roles;
	/**
	 * <pre>
	 *
	 * </pre>
	 */
	private static final long serialVersionUID = 1L;
	private String principal;
	private Face face;
	public Subject(String principal,String sosroles,Face face) {
		this.principal=principal;
		roles=new ArrayList<>();
		String arr[]=sosroles.split(",");
		for(String s:arr){
			roles.add(s);
		}
		this.face=face;
	}
	@Override
	public Face face() {
		// TODO Auto-generated method stub
		return face;
	}
	@Override
	public String principal() {
		return principal;
	}

	@Override
	public boolean containsRole(String role) {
		if("allUsers".equals(role)){
			return true;
		}
		return roles.contains(role);
	}

}
