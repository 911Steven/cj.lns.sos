package cj.lns.common.sos.service.model;

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
 * The persistent class for the sos_user database table.
 * 
 */
@Entity
@Table(name="sos_user")
@NamedQuery(name="SosUser.findAll", query="SELECT s FROM SosUser s")
public class SosUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	private byte sex;
	private BigInteger defaultSws;
	private String signatureText;
	private String head;

	private String nickName;

	private String realName;

	private byte status;

	private String userCode;
	private String briefing;
	
	public SosUser() {
	}
	public String getBriefing() {
		return briefing;
	}
	public void setBriefing(String briefing) {
		this.briefing = briefing;
	}
	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}
	public String getSignatureText() {
		return signatureText;
	}
	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}
	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public BigInteger getDefaultSws() {
		return this.defaultSws;
	}

	public void setDefaultSws(BigInteger defaultSws) {
		this.defaultSws = defaultSws;
	}

	public String getHead() {
		return this.head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getUserCode() {
		return this.userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}