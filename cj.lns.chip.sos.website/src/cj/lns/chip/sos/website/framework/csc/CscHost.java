package cj.lns.chip.sos.website.framework.csc;

import java.util.List;

public class CscHost {
	transient String id;
	String name;
	String host;
	String assignedPort;
	String deployProtocol;
	String deployPort;
	List<Object> cscHosts;
	List<Object> dockers;//docker imageids
	public List<Object> getDockers() {
		return dockers;
	}
	public void setDockers(List<Object> dockers) {
		this.dockers = dockers;
	}
	public String getAssignedPort() {
		return assignedPort;
	}
	public void setAssignedPort(String assignedPort) {
		this.assignedPort = assignedPort;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDeployProtocol() {
		return deployProtocol;
	}
	public void setDeployProtocol(String deployProtocol) {
		this.deployProtocol = deployProtocol;
	}
	public String getDeployPort() {
		return deployPort;
	}
	public void setDeployPort(String deployPort) {
		this.deployPort = deployPort;
	}
	public List<Object> getCscHosts() {
		return cscHosts;
	}
	public void setCscHosts(List<Object> cscHosts) {
		this.cscHosts = cscHosts;
	}
	
}
