package cj.lns.chip.sos.website.market.app.contact.bo;

public class ChatGroupBO {
	public static final String KEY_COL_NAME="sns.chatGroup";
	public static final String KEY_COL_USER_NAME="sns.chatGroup.users";
	transient String id;
	String name;
	String num;
	String introduce;
	byte security;//默认为1,表示需验证，0为公开
	String creator;//群主用户代码
	long createTime;
	String headFile;
	public ChatGroupBO() {
		security=1;
	}
	public byte getSecurity() {
		return security;
	}
	public void setSecurity(byte security) {
		this.security = security;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getHeadFile() {
		return headFile;
	}
	public void setHeadFile(String headFile) {
		this.headFile = headFile;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	/**
	 * 群在群主的磁盘上的信息集合
	 * <pre>
	 *
	 * </pre>
	 * @return
	 */
	public String getMsgColName(){
		return String.format("sns.chatGroup.%s", num);
	}
	public String getHeadHome(){
		return String.format("/chatGroups/%s/head", id);
	}
	/**
	 * 群相册路径
	 * <pre>
	 * 在创建者的主存中
	 * </pre>
	 * @return
	 */
	public String getPicHome() {
		String picHome=String.format("/chatGroups/%s/pictures", id);//群相册路径
		return picHome;
	}
	/**
	 * 群文件路径
	 * <pre>
	 *在创建者的主存中
	 * </pre>
	 * @return
	 */
	public String getFileHome() {
		String fileHome=String.format("/chatGroups/%s/files", id);//群文件路径
		return fileHome;
	}
	
}
