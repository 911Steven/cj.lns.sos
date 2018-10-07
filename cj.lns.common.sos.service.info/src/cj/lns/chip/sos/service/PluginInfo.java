package cj.lns.chip.sos.service;

public class PluginInfo {
	private String assemblyTitle;
	private String assemblyVersion;
	private String assemblyGuid;
	String dependencyPlugin;
	private String assemblyCompany;
	private String assemblyDeveloperHome;
	private String assemblyCopyright;
	private String assemblyProduct;

	private String assemblyIcon;
	private byte[] assemblyIconBytes;
	private String assemblyDescription;
	public byte[] getAssemblyIconBytes() {
		if(assemblyIconBytes==null){
			assemblyIconBytes=new byte[0];
		}
		return assemblyIconBytes;
	}
	public void setAssemblyIconBytes(byte[] assemblyIconBytes) {
		this.assemblyIconBytes = assemblyIconBytes;
	}
	public String getDependencyPlugin() {
		return dependencyPlugin;
	}

	public void setDependencyPlugin(String dependencyPlugin) {
		this.dependencyPlugin = dependencyPlugin;
	}

	public String getAssemblyCompany() {
		return assemblyCompany;
	}

	public void setAssemblyCompany(String assemblyCompany) {
		this.assemblyCompany = assemblyCompany;
	}

	public String getAssemblyCopyright() {
		return assemblyCopyright;
	}

	public void setAssemblyCopyright(String assemblyCopyright) {
		this.assemblyCopyright = assemblyCopyright;
	}

	public String getAssemblyDescription() {
		return assemblyDescription;
	}

	public void setAssemblyDescription(String assemblyDescription) {
		this.assemblyDescription = assemblyDescription;
	}

	public String getAssemblyGuid() {
		return assemblyGuid;
	}

	public void setAssemblyGuid(String assemblyGuid) {
		this.assemblyGuid = assemblyGuid;
	}

	public String getAssemblyProduct() {
		return assemblyProduct;
	}

	public void setAssemblyProduct(String assemblyProduct) {
		this.assemblyProduct = assemblyProduct;
	}

	public String getAssemblyTitle() {
		return assemblyTitle;
	}

	public void setAssemblyTitle(String assemblyTitle) {
		this.assemblyTitle = assemblyTitle;
	}

	public String getAssemblyVersion() {
		return assemblyVersion;
	}

	public void setAssemblyVersion(String assemblyVersion) {
		this.assemblyVersion = assemblyVersion;
	}

	public String getAssemblyIcon() {
		return assemblyIcon;
	}

	public void setAssemblyIcon(String icon) {
		// TODO Auto-generated method stub
		this.assemblyIcon = icon;
	}

	public String getAssemblyDeveloperHome() {
		return assemblyDeveloperHome;
	}

	public void setAssemblyDeveloperHome(String developerHome) {
		this.assemblyDeveloperHome = developerHome;
	}

}
