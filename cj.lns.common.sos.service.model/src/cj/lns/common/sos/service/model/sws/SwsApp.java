package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;

/**
 * The persistent class for the sws_app database table.
 * 
 */
@Entity
@Table(name = "sws_app")
@NamedQuery(name = "SwsApp.findAll", query = "SELECT s FROM SwsApp s")
public class SwsApp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;

	private String command;

	private String provider;
	private String position;
	private String icon;

	private String name;
	private String description;
	private String target;
	private String code;
	private BigInteger swsId;
	private String category;
	private byte propagate;
	private String menus;
	private String portalLeft;
	private String portalRight;
	private int sort;
	private String hidden;
	String platform;
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getMenus() {
		return menus;
	}

	public String getPortalLeft() {
		return portalLeft;
	}

	public String getPortalRight() {
		return portalRight;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public void setPortalLeft(String portalLeft) {
		this.portalLeft = portalLeft;
	}

	public void setPortalRight(String portalRight) {
		this.portalRight = portalRight;
	}

	public byte getPropagate() {
		return propagate;
	}

	public void setPropagate(byte propagate) {
		this.propagate = propagate;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getSwsId() {
		return swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SwsApp() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}