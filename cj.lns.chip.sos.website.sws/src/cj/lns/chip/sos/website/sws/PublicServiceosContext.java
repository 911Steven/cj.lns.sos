package cj.lns.chip.sos.website.sws;

import java.io.Serializable;

import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.frame.Frame;

public class PublicServiceosContext implements IServiceosContext, Serializable {

	private static final long serialVersionUID = 1L;


	@Override
	public IRemoteDevice device(Frame frame) {
		return IRemoteDevice.remoteDevice(frame);
	}

	@Override
	public ISubject subject(Frame frame) {
		return ISubject.subject(frame);
	}


}
