package cj.lns.chip.sos.service.sws;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ServicewsBody {
	BigInteger swsid;
	BigInteger inheritId;
	byte level;
	String owner;
	String swsName;
	String faceImg;
	String portalId;
	String swsDesc;
	Map<String,Object> extra;
	double capacity;
	double useSpace;
	double dataSize;
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getFaceImg() {
		return faceImg;
	}
	public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	public Map<String, Object> getExtra() {
		if(extra==null){
			extra=new HashMap<>();
		}
		return extra;
	}
	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	public double getUseSpace() {
		return useSpace;
	}
	public void setUseSpace(double useSpace) {
		this.useSpace = useSpace;
	}
	public double getDataSize() {
		return dataSize;
	}
	public void setDataSize(double dataSize) {
		this.dataSize = dataSize;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
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
	
	
}
