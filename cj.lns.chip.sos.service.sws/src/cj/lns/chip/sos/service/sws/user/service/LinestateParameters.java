package cj.lns.chip.sos.service.sws.user.service;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class LinestateParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "领牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "用户代码")
	String user;
	@CjRemoteParameter(must = true, usage = "命令：上线(online)、下线(offline)，状态修改(changestate)")
	String command;
	@CjRemoteParameter(must = true, usage = "状态")
	String lineState;
	@CjRemoteParameter(must = false, usage = "对等点所在终点站")
	String onTerminus;
	@CjRemoteParameter(must = true, usage = "对等点标识")
	String peerId;
	@CjRemoteParameter(must = false, usage = "所在设备")
	String onDevice;
	@CjRemoteParameter(must = false, usage = "所在视窗")
	String onServicews;

	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken = cjtoken;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getLineState() {
		return lineState;
	}

	public void setLineState(String lineState) {
		this.lineState = lineState;
	}

	public String getOnTerminus() {
		return onTerminus;
	}

	public void setOnTerminus(String onTerminus) {
		this.onTerminus = onTerminus;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getOnDevice() {
		return onDevice;
	}

	public void setOnDevice(String onDevice) {
		this.onDevice = onDevice;
	}

	public String getOnServicews() {
		return onServicews;
	}

	public void setOnServicews(String onServicews) {
		this.onServicews = onServicews;
	}
	
}
