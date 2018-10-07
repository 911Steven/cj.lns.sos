package cj.lns.common.sos.service.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sos_portlet database table.
 * 
 */
@Entity
@Table(name="sos_portlet")
@NamedQuery(name="SosPortlet.findAll", query="SELECT s FROM SosPortlet s")
public class SosPortlet implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String hostId;

	private String name;

	private String position;

	private BigInteger sort;

	public SosPortlet() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getHostId() {
		return this.hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public BigInteger getSort() {
		return this.sort;
	}

	public void setSort(BigInteger sort) {
		this.sort = sort;
	}

}