package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the sws_info database table.
 * 
 */
@Entity
@Table(name="sws_info")
@NamedQuery(name="SwsInfo.findAll", query="SELECT s FROM SwsInfo s")
public class SwsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private BigInteger id;

	private String description;

	private BigInteger inheritId;
	private BigInteger swsbid;
	private byte level;
	private String name;

	private String owner;
	String faceImg;
	private String usePortal;

	public SwsInfo() {
	}
	public BigInteger getSwsbid() {
		return swsbid;
	}
	public void setSwsbid(BigInteger swsbid) {
		this.swsbid = swsbid;
	}
	public String getFaceImg() {
		return faceImg;
	}
	public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getInheritId() {
		return this.inheritId;
	}

	public void setInheritId(BigInteger inheritId) {
		this.inheritId = inheritId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getUsePortal() {
		return this.usePortal;
	}

	public void setUsePortal(String usePortal) {
		this.usePortal = usePortal;
	}

}