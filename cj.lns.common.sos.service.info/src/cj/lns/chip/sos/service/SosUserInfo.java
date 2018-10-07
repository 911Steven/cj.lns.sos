package cj.lns.chip.sos.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cj.lns.chip.sos.service.sws.ServicewsInfo;

public class SosUserInfo {

	private BigInteger id;

	private Date createtime;

	private byte sex;
	private String head;

	private String nickName;

	private String realName;

	private byte status;

	private String userCode;
	private String signatureText;
	private String briefing;
	private long capacity;//磁盘容量
	double useSpace;//磁盘已分配空间
	double dataSize;
	long cubeCount;
	double homeCapacity;
	private List<ServicewsInfo> swsList;
	public SosUserInfo() {
		
	}
	public String getBriefing() {
		return briefing;
	}
	public void setBriefing(String briefing) {
		this.briefing = briefing;
	}
	public double getHomeCapacity() {
		return homeCapacity;
	}
	public void setHomeCapacity(double homeCapacity) {
		this.homeCapacity = homeCapacity;
	}
	public List<ServicewsInfo> getSwsList() {
		if(swsList==null){
			swsList=new ArrayList<>();
		}
		return swsList;
	}
	public void setSwsList(List<ServicewsInfo> swsList) {
		this.swsList = swsList;
	}
	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
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

	public long getCubeCount() {
		return cubeCount;
	}

	public void setCubeCount(long cubeCount) {
		this.cubeCount = cubeCount;
	}
	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}
	public String getSignatureText() {
		return signatureText;
	}
	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}
	

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public String getHead() {
		return this.head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getUserCode() {
		return this.userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}