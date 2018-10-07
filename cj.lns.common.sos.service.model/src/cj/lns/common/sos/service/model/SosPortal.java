package cj.lns.common.sos.service.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the sos_portal database table.
 * 
 */
@Entity
@Table(name="sos_portal")
@NamedQuery(name="SosPortal.findAll", query="SELECT s FROM SosPortal s")
public class SosPortal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String pluginGuid;
	private BigInteger swsTemplateId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
@Lob
	private String scenes;
@Lob
	private String themes;

	public SosPortal() {
	}
	public BigInteger getSwsTemplateId() {
		return swsTemplateId;
	}
	public void setSwsTemplateId(BigInteger swsTemplateId) {
		this.swsTemplateId = swsTemplateId;
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

	public String getScenes() {
		return this.scenes;
	}

	public void setScenes(String scenes) {
		this.scenes = scenes;
	}

	public String getThemes() {
		return this.themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}

}