package cj.lns.chip.sos.service.framework.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class CreateServicewsParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="视窗识别码（三位,高位从6-9用户是2-5），用于视窗编号识别")
	int kindCode;
	@CjRemoteParameter(must=true,usage="用户设置代码（四位），用于视窗编号")
	int userSetCode;
	@CjRemoteParameter(must=true,usage="框架插件标识")
	String portalId;
	@CjRemoteParameter(must=true,usage="视窗的创建者，或称之为owner")
	String creator;
	@CjRemoteParameter(must=true,usage="终端，如browner,mobile等，框架必须支持方可")
	String terminus;
	@CjRemoteParameter(must=true,usage="场景名")
	String sceneName;
	@CjRemoteParameter(must=true,usage="主题")
	String themeName;
	@CjRemoteParameter(must=false,usage="描述")
	String description;
	@CjRemoteParameter(must=false,usage="是否作为框架的视窗模板，默认为false，但当框架未有视窗模板时，将自动将第一个新建视窗作为模板")
	boolean asTemplate;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken=cjtoken;
	}

	public String getPortalId() {
		return portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAsTemplate() {
		return asTemplate;
	}

	public void setAsTemplate(boolean asTemplate) {
		this.asTemplate = asTemplate;
	}

	public String getTerminus() {
		return terminus;
	}

	public void setTerminus(String terminus) {
		this.terminus = terminus;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public int getKindCode() {
		return kindCode;
	}
	public void setKindCode(int kindCode) {
		this.kindCode = kindCode;
	}
	public int getUserSetCode() {
		return userSetCode;
	}
	public void setUserSetCode(int userSetCode) {
		this.userSetCode = userSetCode;
	}
}
