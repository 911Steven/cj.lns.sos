package cj.lns.chip.sos.website.auth;

import cj.studio.ecm.graph.CircuitException;

public class UnknownAuthenticatorException extends CircuitException {

	public UnknownAuthenticatorException(String status, Throwable e) {
		super(status, e);
		// TODO Auto-generated constructor stub
	}

	public UnknownAuthenticatorException(String status,
			boolean isSystemException, Throwable e) {
		super(status, isSystemException, e);
		// TODO Auto-generated constructor stub
	}

	public UnknownAuthenticatorException(String status, String e) {
		super(status, e);
		// TODO Auto-generated constructor stub
	}

	public UnknownAuthenticatorException(String status,
			boolean isSystemException, String e) {
		super(status, isSystemException, e);
		// TODO Auto-generated constructor stub
	}

}
