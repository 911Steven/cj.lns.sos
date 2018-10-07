package cj.lns.chip.sos.service.sws.security;

/**
 * 视窗的认证方式
 * 
 * <pre>
 * 在视窗拒绝某联系人针对的许可后，如果认证方式不是－1则执行相应的认证策略从而取得许可。
 * －回答问题为0
 * －密码访问为1
 * －不能申请为-1
 * </pre>
 * 
 * @author carocean
 *
 */
public enum ServicewsAuthMethod {
	none(-1), ask(0), password(1);
	private byte v;
	private ServicewsAuthMethod(int v) {
		this.v=(byte)v;
	}
	public byte getValue() {
		return v;
	}
	public static ServicewsAuthMethod valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
        case -1:
        	return none;
        case 0:
            return ask;
        case 1:
            return password;
        default:
            return null;
        }
    }
}
