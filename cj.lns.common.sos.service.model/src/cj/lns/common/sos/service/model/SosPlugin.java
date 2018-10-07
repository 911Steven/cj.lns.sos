package cj.lns.common.sos.service.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the sos_plugin database table.
 * 
 */
@Entity
@Table(name="sos_plugin")
@NamedQuery(name="SosPlugin.findAll", query="SELECT s FROM SosPlugin s")
public class SosPlugin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String assemblyGuid;

	private String assemblyCompany;

	private String assemblyCopyright;

	private String assemblyDescription;

	private String assemblyDeveloperHome;

	private String assemblyIcon;

	private byte[] assemblyIconBytes;

	private String assemblyProduct;

	private String assemblyTitle;

	private String assemblyVersion;

	private String dependencyPlugin;

	public SosPlugin() {
	}

	public String getAssemblyGuid() {
		return this.assemblyGuid;
	}

	public void setAssemblyGuid(String assemblyGuid) {
		this.assemblyGuid = assemblyGuid;
	}

	public String getAssemblyCompany() {
		return this.assemblyCompany;
	}

	public void setAssemblyCompany(String assemblyCompany) {
		this.assemblyCompany = assemblyCompany;
	}

	public String getAssemblyCopyright() {
		return this.assemblyCopyright;
	}

	public void setAssemblyCopyright(String assemblyCopyright) {
		this.assemblyCopyright = assemblyCopyright;
	}

	public String getAssemblyDescription() {
		return this.assemblyDescription;
	}

	public void setAssemblyDescription(String assemblyDescription) {
		this.assemblyDescription = assemblyDescription;
	}

	public String getAssemblyDeveloperHome() {
		return this.assemblyDeveloperHome;
	}

	public void setAssemblyDeveloperHome(String assemblyDeveloperHome) {
		this.assemblyDeveloperHome = assemblyDeveloperHome;
	}

	public String getAssemblyIcon() {
		return this.assemblyIcon;
	}

	public void setAssemblyIcon(String assemblyIcon) {
		this.assemblyIcon = assemblyIcon;
	}

	public byte[] getAssemblyIconBytes() {
		return this.assemblyIconBytes;
	}

	public void setAssemblyIconBytes(byte[] assemblyIconBytes) {
		this.assemblyIconBytes = assemblyIconBytes;
	}

	public String getAssemblyProduct() {
		return this.assemblyProduct;
	}

	public void setAssemblyProduct(String assemblyProduct) {
		this.assemblyProduct = assemblyProduct;
	}

	public String getAssemblyTitle() {
		return this.assemblyTitle;
	}

	public void setAssemblyTitle(String assemblyTitle) {
		this.assemblyTitle = assemblyTitle;
	}

	public String getAssemblyVersion() {
		return this.assemblyVersion;
	}

	public void setAssemblyVersion(String assemblyVersion) {
		this.assemblyVersion = assemblyVersion;
	}

	public String getDependencyPlugin() {
		return this.dependencyPlugin;
	}

	public void setDependencyPlugin(String dependencyPlugin) {
		this.dependencyPlugin = dependencyPlugin;
	}

}