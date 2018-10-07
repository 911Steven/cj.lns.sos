package cj.lns.chip.sos.website.framework.csc;

public class CscComputer {
	transient String id;
	String owner;
	String owneris;
	long ctime;
	String onHost;
	String deploy;
	String cscHost;
	String cscCustomer;
	String docker;
	String nrport;
	public String getNrport() {
		return nrport;
	}
	public void setNrport(String nrport) {
		this.nrport = nrport;
	}
	public String getDocker() {
		return docker;
	}
	public void setDocker(String docker) {
		this.docker = docker;
	}
	public String getOnHost() {
		return onHost;
	}
	public void setOnHost(String onHost) {
		this.onHost = onHost;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOwneris() {
		return owneris;
	}
	public void setOwneris(String owneris) {
		this.owneris = owneris;
	}
	public long getCtime() {
		return ctime;
	}
	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
	public String getDeploy() {
		return deploy;
	}
	public void setDeploy(String deploy) {
		this.deploy = deploy;
	}
	public String getCscHost() {
		return cscHost;
	}
	public void setCscHost(String cscHost) {
		this.cscHost = cscHost;
	}
	public String getCscCustomer() {
		return cscCustomer;
	}
	public void setCscCustomer(String cscCustomer) {
		this.cscCustomer = cscCustomer;
	}
	
}
