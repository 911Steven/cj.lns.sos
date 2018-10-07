package cj.lns.chip.sos.website.sws;

import java.util.Map;

import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.frame.Frame;

public class ServicewsContext implements IServicewsContext {

	private static final long serialVersionUID = 1L;
	private String swsid;
	private String swstid;
	private byte level;
	private ISubject visitor;
	private Map<String, String> props;
	String owner;
	
	Face face;
	public ServicewsContext(String owner,String swsid,byte level,String swstid,ISubject visitor,Face face,Map<String,String> props) {
		super();
		this.owner=owner;
		this.props=props;
		this.swsid = swsid;
		this.level=level;
		this.visitor=visitor;
		this.face=face;
		this.swstid=swstid;
	}
	@Override
	public byte level() {
		return level;
	}
	@Override
	public String swstid() {
		return swstid;
	}
	public String owner() {
		return owner;
	}
	@Override
	public Face face() {
		return face;
	}
	@Override
	public boolean isOwner() {
		return owner.equals(visitor.principal());
	}
	@Override
	public ISubject visitor() {
		return visitor;
	}
	@Override
	public IRemoteDevice device(Frame frame) {
		return IRemoteDevice.remoteDevice(frame);
	}

	@Override
	public IServiceosContext serviceos(Frame frame) {
		return IServiceosContext.context(frame);
	}

	@Override
	public String swsid() {
		return swsid;
	}
	@Override
	public String prop(String name) {
		// TODO Auto-generated method stub
		return props.get(name);
	}
	@Override
	public String prop(String name, String value) {
		return props.put(name, value);
	}
	@Override
	public String[] enumProp() {
		// TODO Auto-generated method stub
		return props.keySet().toArray(new String[0]);
	}



}