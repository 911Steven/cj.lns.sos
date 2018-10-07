package cj.lns.chip.sos.service.sws;

import java.math.BigInteger;


/**
 * 在超级用户登录后返回客户端的视窗信息
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
public class ServicewsInfo {
	//SwsInfo sws, SwsPortalConf portal, SosUser user
	String owner;
	private BigInteger swsId;
	private String sosId;
	private String description;
	private BigInteger inheritId;
	private byte level;
	private String name;
	private String usePortal;
	private String useCanvas;
	private String useSceneId;
	private String useTheme;
	private String faceImg;
	
	public ServicewsInfo() {
	}
	public String getFaceImg() {
		return faceImg;
	}
	public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	

	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public String getOwner() {
		return owner;
	}
	public String getSosId() {
		return sosId;
	}
	public void setSosId(String sosId) {
		this.sosId = sosId;
	}
	public String getUsePortal() {
		return usePortal;
	}
	public void setUsePortal(String usePortal) {
		this.usePortal = usePortal;
	}
	public String getUseCanvas() {
		return useCanvas;
	}
	public void setUseCanvas(String useCanvas) {
		this.useCanvas = useCanvas;
	}
	public String getUseSceneId() {
		return useSceneId;
	}
	public void setUseSceneId(String useSceneId) {
		this.useSceneId = useSceneId;
	}
	public String getUseTheme() {
		return useTheme;
	}
	public void setUseTheme(String useTheme) {
		this.useTheme = useTheme;
	}
	public BigInteger getSwsId() {
		return swsId;
	}
	public String getDescription() {
		return description;
	}
	public BigInteger getInheritId() {
		return inheritId;
	}
	public String getName() {
		return name;
	}
	public void setOwner(String user) {
		this.owner = user;
	}
	public void setSwsId(BigInteger id) {
		// TODO Auto-generated method stub
		this.swsId=id;
	}
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		this.description=description;
	}
	public void setInheritId(BigInteger inheritId) {
		// TODO Auto-generated method stub
		this.inheritId=inheritId;
	}
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name=name;
	}
	
}
