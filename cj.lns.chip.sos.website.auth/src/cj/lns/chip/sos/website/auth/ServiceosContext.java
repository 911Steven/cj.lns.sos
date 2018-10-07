package cj.lns.chip.sos.website.auth;

import java.io.Serializable;

import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.frame.Frame;

public class ServiceosContext implements IServiceosContext,Serializable{

	private static final long serialVersionUID = 1L;
	public ISubject subject(Frame frame){
		return ISubject.subject(frame);
	}
	@Override
	public IRemoteDevice device(Frame frame) {
		return IRemoteDevice.remoteDevice(frame);
	}
}
