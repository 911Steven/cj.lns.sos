package cj.lns.chip.sos.supersws.tools;

import java.io.IOException;

import cj.lns.chip.sos.supersws.ISos;
import cj.studio.ecm.annotation.CjService;

@CjService(name="sosConsole")
public class SosConsole extends Console{
	public static final String COLOR_SURFACE = "\033[0;30m";
	public static final String COLOR_RESPONSE = "\033[0;34m";
	public static final String COLOR_CMDLINE = "\033[0;32m";
	public static final String COLOR_CMDPREV = "\033[0;31m";
	@Override
	protected String prefix(ISos sos, Object... target) {
		return SosConsole.COLOR_CMDPREV + " >"
				+ SosConsole.COLOR_CMDLINE;
	}
	@Override
	public void monitor(ISos sos, Object... target) throws IOException {
		System.out.println("——————————————使用说明——————————————");
		System.out.println("       如不记得命令，可用man命令查询");
		System.out.println("__________________________________");
		System.out.println();
		super.monitor(sos, target);
	}
	@Override
	protected boolean exit(ISos sos, String cmd) {
		if("exit".equals(cmd)||"bye".equals(cmd)||"close".equals(cmd)){
			sos.close();
			return true;
		}
		return false;
	}

}
