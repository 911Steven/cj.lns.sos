package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class BuildBasicSwsParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="持有人")
	String owner;
	@CjRemoteParameter(must=true,usage="已分配的视窗号")
	BigInteger assingedSwsid;
	@CjRemoteParameter(must=true,usage="要继承的视窗id")
	BigInteger inheritId;
	@CjRemoteParameter(must=true,usage="视窗空间限额")
	long capacity;
	@CjRemoteParameter(must=true,usage="视窗级别，必须是1")
	int level;
	@CjRemoteParameter(must=true,usage="视窗名称")
	String name;
	@CjRemoteParameter(must=true,usage="绑定的平台号")
	String platform;
	@CjRemoteParameter(must=false,usage="视窗说明")
	String desc;
	@CjRemoteParameter(must=true,usage="视窗图标")
	private String faceImg;
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
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
	public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public BigInteger getAssingedSwsid() {
		return assingedSwsid;
	}
	public void setAssingedSwsid(BigInteger assingedSwsid) {
		this.assingedSwsid = assingedSwsid;
	}
	public BigInteger getInheritId() {
		return inheritId;
	}
	public void setInheritId(BigInteger inheritId) {
		this.inheritId = inheritId;
	}
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getFaceImg() {
		return faceImg;
	}
	
}
