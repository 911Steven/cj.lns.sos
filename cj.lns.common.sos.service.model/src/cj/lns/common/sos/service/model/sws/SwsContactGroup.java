package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the sws_contactGroup database table.
 * 
 */
@Entity
@Table(name="sws_contactGroup")
@NamedQuery(name="SwsContactGroup.findAll", query="SELECT s FROM SwsContactGroup s")
public class SwsContactGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String groupName;

	private BigInteger swsId;
	private String groupType;
	private int sort;
	private byte propagate;
	public byte getPropagate() {
		return propagate;
	}
	public void setPropagate(byte propagate) {
		this.propagate = propagate;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public SwsContactGroup() {
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

}