package cj.lns.chip.sos.website.framework;

import cj.studio.ecm.graph.CircuitException;

public class AuthenticationException extends CircuitException {

	public AuthenticationException(String status, boolean isSystemException,
			String e) {
		super(status, isSystemException, e);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String status, boolean isSystemException,
			Throwable e) {
		super(status, isSystemException, e);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String status, String e) {
		super(status, e);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String status, Throwable e) {
		super(status, e);
		// TODO Auto-generated constructor stub
	}

	

}
