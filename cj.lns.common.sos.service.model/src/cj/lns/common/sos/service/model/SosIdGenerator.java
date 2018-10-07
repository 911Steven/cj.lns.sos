package cj.lns.common.sos.service.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sos_IdGenerator database table.
 * 
 */
@Entity
@Table(name="sos_IdGenerator")
@NamedQuery(name="SosIdGenerator.findAll", query="SELECT s FROM SosIdGenerator s")
public class SosIdGenerator implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private BigInteger id;

	private BigInteger currentNum;

	private String subject;

	public SosIdGenerator() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getCurrentNum() {
		return currentNum;
	}

	public void setCurrentNum(BigInteger currentNum) {
		this.currentNum = currentNum;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	
}