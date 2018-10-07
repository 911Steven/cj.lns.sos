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
 * The persistent class for the sws_portalConf database table.
 * 
 */
@Entity
@Table(name="sws_portalConf")
@NamedQuery(name="SwsPortalConf.findAll", query="SELECT s FROM SwsPortalConf s")
public class SwsPortalConf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger swsId;

	private String portalId;

	private String useCanvas;

	private String useSceneId;

	private String useTheme;
	private String defaultAppId;
	private String background;
	private String platform;
	public SwsPortalConf() {
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getBackground() {
		return background;
	}
	public void setBackground(String background) {
		this.background = background;
	}
	public String getDefaultAppId() {
		return defaultAppId;
	}
	public void setDefaultAppId(String defaultAppId) {
		this.defaultAppId = defaultAppId;
	}
	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public String getPortalId() {
		return this.portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public String getUseCanvas() {
		return this.useCanvas;
	}

	public void setUseCanvas(String useCanvas) {
		this.useCanvas = useCanvas;
	}

	public String getUseSceneId() {
		return this.useSceneId;
	}

	public void setUseSceneId(String useSceneId) {
		this.useSceneId = useSceneId;
	}

	public String getUseTheme() {
		return this.useTheme;
	}

	public void setUseTheme(String useTheme) {
		this.useTheme = useTheme;
	}

}