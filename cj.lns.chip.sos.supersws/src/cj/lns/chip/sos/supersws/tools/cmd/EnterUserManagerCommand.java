package cj.lns.chip.sos.supersws.tools.cmd;

import java.io.IOException;

import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.Console;
import cj.lns.chip.sos.supersws.tools.user.UserConsole;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "enterUserManagerCommand")
public class EnterUserManagerCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "sosConsole")
	Console sosconsole;
	@CjServiceRef
	UserConsole userConsole;
	public void doCommand(CmdLine cl) throws IOException {
		ISos sos = (ISos) cl.prop("sos");
		String indent = cl.propString("indent");
//		CommandLine line=cl.line();
		userConsole.monitor(sos,sos, indent);
	}

	@Override
	public String cmdDesc() {
		return "进入用户管理窗口";
	}

	@Override
	public String cmd() {
		return "um";
	}

	@Override
	public Options options() {
		Options options = new Options();
//		 Option mode = new Option("p", "portal", true,
//		 "要查看");
//		 options.addOption(mode);
		// Option invite = new Option("i", "invite", true,
		// "邀请对方连接,属于open指令，格式：,号隔开目标对，目标对：号前是用户名，冒号后是peer标识，如果是*空无冒号均表示用户的所有peer。");
		// options.addOption(invite);
		// Option join = new Option("j", "join", true,
		// "直接将用户加入当前linker，属于open指令。格式：,号隔开目标对，目标对：号前是用户名，冒号后是peer标识，如果是*空无冒号均表示用户的所有peer。");
		// options.addOption(join);
		// Option u = new Option("p", "peer", true,
		// "指定的对方用户的设备标识，该参数只对invite指令有效");
		// options.addOption(u);
		// Option p = new Option("e", "editor",false,
		// "聊天内容:文本、表情、小音视频等，具有一定的格式。");
		// options.addOption(p);
		return options;
	}
}
