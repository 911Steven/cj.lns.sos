package cj.lns.chip.sos.website.sws;

import java.io.Serializable;

import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.ISubject;

public class PublicSubject implements ISubject, Serializable {

	private static final long serialVersionUID = 1L;
	private String principal;
	private Face face;

	public PublicSubject(String principal,Face face) {
		this.principal = principal;
		this.face=face;
	}

	@Override
	public String principal() {
		return principal;
	}
	@Override
	public Face face() {
		// TODO Auto-generated method stub
		return face;
	}
	@Override
	public boolean containsRole(String role) {
		if ("guestUsers".equals(role))
			return true;
		if ("allUsers".equals(role))
			return true;
		return false;
	}

}
