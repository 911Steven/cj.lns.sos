package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_menu database table.
 * 
 */
@Entity
@Table(name="sws_menu")
@NamedQuery(name="SwsMenu.findAll", query="SELECT s FROM SwsMenu s")
public class SwsMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String command;
	private String description;
	private String provider;
	private String position;
	private String icon;

	private String name;

	private BigInteger swsId;
	private String target;
	private String code;
	private String category;
	private byte propagate;
	private int sort;
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public SwsMenu() {
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
	public BigInteger getSwsId() {
		return swsId;
	}
	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
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