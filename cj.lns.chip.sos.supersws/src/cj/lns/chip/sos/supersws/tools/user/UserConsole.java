package cj.lns.chip.sos.supersws.tools.user;

import java.io.IOException;
import java.util.Map;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.Console;
import cj.lns.chip.sos.supersws.tools.SosConsole;
import cj.studio.ecm.annotation.CjService;
@CjService(name="userConsole")
public class UserConsole extends Console {
	ISos sos;
	@Override
	protected String prefix(ISos sos, Object... target) {
		String indent=(String)target[1];
		return SosConsole.COLOR_CMDPREV + indent+"um >"
				+ SosConsole.COLOR_CMDLINE;
	}
	@Override
	public void monitor(ISos sos, Object... target)
			throws IOException {
		this.sos=(ISos)target[0];
		super.monitor(sos, target);
	}
	@Override
	protected void beforDoCommand(Command cmd, CmdLine cl, Object[] target) {
		cl.prop("sos",sos);
		String indent=String.format("%s%s",cl.prop("indent"),cl.prop("indent"));
		cl.prop("indent",indent);
	}
	@Override
	protected void printMan(ISos sos, Object[] target,Map<String, Command> cmds) {
		System.out.println(prefix(sos, target)+"服务系统指令集");
		super.printMan(sos, target, cmds);
	}
	@Override
	protected boolean exit(ISos sos, String cmd) {
		if("close".equals(cmd)||"bye".equals(cmd)){
//			sos.close();
			return true;
		}
		return false;
	}

}
