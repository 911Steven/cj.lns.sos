package cj.lns.common.sos.service.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;

/**
 * The persistent class for the sos_app database table.
 * 
 */
@Entity
@Table(name = "sos_linestate")
@NamedQuery(name = "SosLinestate.findAll", query = "SELECT s FROM SosLinestate s")
public class SosLinestate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	private String user;
	private String state;
	private String onTerminus;
	private String peerId;
	private String onDevice;
	private String  onServicews;
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOnTerminus() {
		return onTerminus;
	}
	public void setOnTerminus(String onTerminus) {
		this.onTerminus = onTerminus;
	}
	public String getPeerId() {
		return peerId;
	}
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	public String getOnDevice() {
		return onDevice;
	}
	public void setOnDevice(String onDevice) {
		this.onDevice = onDevice;
	}
	public String getOnServicews() {
		return onServicews;
	}
	public void setOnServicews(String onServicews) {
		this.onServicews = onServicews;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

}