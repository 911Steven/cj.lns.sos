package cj.lns.chip.sos.service.db.model;

import java.io.Serializable;

import javax.persistence.*;

import cj.studio.ecm.annotation.CjExotericalType;

import java.util.Date;


/**
 * The persistent class for the sos_user database table.
 * 
 */
@CjExotericalType(typeName="model")
@Entity
@Table(name="sos_user")
@NamedQuery(name="SosUser.findAll", query="SELECT s FROM SosUser s")
public class SosUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;

	private String head;

	private String nickName;

	private String realName;

	private byte status;

	private String userCode;

	public SosUser() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
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