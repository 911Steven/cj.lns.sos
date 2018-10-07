package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;

/**
 * The persistent class for the sws_portlet database table.
 * 
 */
@Entity
@Table(name = "sws_portlet")
@NamedQuery(name = "SwsPortlet.findAll", query = "SELECT s FROM SwsPortlet s")
public class SwsPortlet implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	private String contentUrl;
	private BigInteger swsId;
	private String provider;
	private String name;
	private String useTemplate;
	private String description;
	private String position;
	private String icon;
	private int sort;
	private String code;
	private String category;
	private byte propagate;
	
	public String getUseTemplate() {
		return useTemplate;
	}
	public void setUseTemplate(String useTemplate) {
		this.useTemplate = useTemplate;
	}
	public byte getPropagate() {
		return propagate;
	}
	public void setPropagate(byte propagate) {
		this.propagate = propagate;
	}
	public String getContentUrl() {
		return contentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
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

	public SwsPortlet() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getSwsId() {
		return swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getSort() {
		return this.sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

}