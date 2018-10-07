package cj.lns.common.sos.service.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the sos_menuHost database table.
 * 
 */
@Entity
@Table(name="sos_menuHost")
@NamedQuery(name="SosMenuHost.findAll", query="SELECT s FROM SosMenuHost s")
public class SosMenuHost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String pluginGuid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	private String owner;

	private String portalId;

	public SosMenuHost() {
	}

	public String getPluginGuid() {
		return this.pluginGuid;
	}

	public void setPluginGuid(String pluginGuid) {
		this.pluginGuid = pluginGuid;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPortalId() {
		return this.portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

}