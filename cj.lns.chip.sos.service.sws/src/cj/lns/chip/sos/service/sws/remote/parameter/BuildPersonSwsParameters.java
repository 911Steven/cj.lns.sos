package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class BuildPersonSwsParameters implements IRemoteParameters{
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
	@CjRemoteParameter(must=false,usage="视窗说明")
	String intro;
	@CjRemoteParameter(must=false,usage="兴趣爱好")
	String hobby;
//	@CjRemoteParameter(must=true,usage="视窗图标")
//	private String faceImg;
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
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getHobby() {
		return hobby;
	}
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
}
