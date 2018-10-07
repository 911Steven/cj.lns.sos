package cj.lns.chip.sos.supersws.tools.cmd;

import java.io.IOException;

import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.Console;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "VersionCommand")
public class VerionCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "sosConsole")
	Console sosconsole;
	

	public void doCommand(CmdLine cl) throws IOException {
		ISos sos = (ISos) cl.prop("sos");
		String indent = cl.propString("indent");
		String info=sos.getVersion();
		System.out.println(String.format("%s%s", indent,info));
	}

	@Override
	public String cmdDesc() {
		return "查看sos版本信息：表sos_properties";
	}

	@Override
	public String cmd() {
		return "version";
	}

	@Override
	public Options options() {
		Options options = new Options();
//		Option mode = new Option("m", "mode", true,
//				"打开方式，属于open指令.有：r|c|cr，默认是cr，r是只读，c是创建。");
//		options.addOption(mode);
		// Option invite = new Option("i", "invite", true,
		// "邀请对方连接,属于open指令，格式：,号隔开目标对，目标对：号前是用户名，冒号后是peer标识，如果是*空无冒号均表示用户的所有peer。");
		// options.addOption(invite);
		// Option join = new Option("j", "join", true,
		// "直接将用户加入当前linker，属于open指令。格式：,号隔开目标对，目标对：号前是用户名，冒号后是peer标识，如果是*空无冒号均表示用户的所有peer。");
		// options.addOption(join);
//		Option u = new Option("p", "peer", true,
//				"指定的对方用户的设备标识，该参数只对invite指令有效");
//		options.addOption(u);
		// Option p = new Option("e", "editor",false,
		// "聊天内容:文本、表情、小音视频等，具有一定的格式。");
		// options.addOption(p);
		return options;
	}
}
