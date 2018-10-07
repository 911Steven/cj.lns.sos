package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class ChangeSwsThemeParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="主题名")
	String themeName;
	@CjRemoteParameter(must=true,usage="视窗标识")
	long swsid;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public long getSwsid() {
		return swsid;
	}
	public void setSwsid(long swsid) {
		this.swsid = swsid;
	}
	
}
