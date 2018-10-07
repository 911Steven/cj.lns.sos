package cj.lns.chip.sos.service.sws.user;

import java.math.BigInteger;

public class ContactInfo {
	String memoName;
	BigInteger id;
	String userCode;
	String headPic;
	String personalSignature;
	BigInteger ownerGroupId;
	public BigInteger getOwnerGroupId() {
		return ownerGroupId;
	}
	public void setOwnerGroupId(BigInteger ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getMemoName() {
		return memoName;
	}
	public void setMemoName(String memoName) {
		this.memoName = memoName;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public String getPersonalSignature() {
		return personalSignature;
	}
	public void setPersonalSignature(String personalSignature) {
		this.personalSignature = personalSignature;
	}
	
}
