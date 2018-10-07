package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the sws_contact database table.
 * 
 */
@Entity
@Table(name = "sws_contact")
@NamedQuery(name = "SwsContact.findAll", query = "SELECT s FROM SwsContact s")
public class SwsContact implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	private String userCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date joinTime;

	private String memoName;

	private BigInteger ownerGroupId;

	private BigInteger swsId;
	String headPic;
	String personalSignature;

	public SwsContact() {
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Date getJoinTime() {
		return this.joinTime;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public String getMemoName() {
		return this.memoName;
	}

	public void setMemoName(String memoName) {
		this.memoName = memoName;
	}

	public BigInteger getOwnerGroupId() {
		return this.ownerGroupId;
	}

	public void setOwnerGroupId(BigInteger ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public String getPersonalSignature() {
		return personalSignature;
	}

	public void setPersonalSignature(String personalSignature) {
		this.personalSignature = personalSignature;
	}
}