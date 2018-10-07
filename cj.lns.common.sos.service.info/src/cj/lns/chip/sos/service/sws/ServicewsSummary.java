package cj.lns.chip.sos.service.sws;

import java.math.BigInteger;

import cj.lns.chip.sos.service.SosUserInfo;

public class ServicewsSummary {
	BigInteger swsid;
	BigInteger inheritId;
	String swsName;
	String portalId;
	String swsDesc;
	SosUserInfo owner;
	byte level;
	String nickName;
	String headPic;
	String faceImg;
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public String getFaceImg() {
		return faceImg;
	}
	public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	public BigInteger getInheritId() {
		return inheritId;
	}
	public void setInheritId(BigInteger inheritId) {
		this.inheritId = inheritId;
	}
	public BigInteger getSwsid() {
		return swsid;
	}
	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}
	public String getSwsName() {
		return swsName;
	}
	public void setSwsName(String swsName) {
		this.swsName = swsName;
	}
	public String getPortalId() {
		return portalId;
	}
	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}
	public String getSwsDesc() {
		return swsDesc;
	}
	public void setSwsDesc(String swsDesc) {
		this.swsDesc = swsDesc;
	}
	public SosUserInfo getOwner() {
		return owner;
	}
	public void setOwner(SosUserInfo owner) {
		this.owner = owner;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	
	
}
