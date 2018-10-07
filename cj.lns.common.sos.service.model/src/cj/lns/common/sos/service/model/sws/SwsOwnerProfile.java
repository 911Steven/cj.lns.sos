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
 * The persistent class for the sws_ownerProfile database table.
 * 
 */
@Entity
@Table(name="sws_ownerProfile")
@NamedQuery(name="SwsOwnerProfile.findAll", query="SELECT s FROM SwsOwnerProfile s")
public class SwsOwnerProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger swsId;

	
	public SwsOwnerProfile() {
		
	}
	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}


}