package cj.lns.chip.sos.supersws.tools.cmd;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.Console;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "createSuperswsCommand")
public class CreateSuperswsCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "sosConsole")
	Console sosconsole;

	public void doCommand(CmdLine cl) throws IOException {
		ISos sos = (ISos) cl.prop("sos");
		String indent = cl.propString("indent");
		CommandLine line = cl.line();
		@SuppressWarnings("unchecked")
		List<String> args = line.getArgList();
		if (args.isEmpty()) {
			System.out.println(indent + "需要指定超级视窗号");
			return;
		}
		String name = args.get(0).trim();// portalId
		if(!line.hasOption("owner")){
			System.out.println(String.format("%s缺少参数：-o", indent));
			return;
		}
		if(!line.hasOption("scene")){
			System.out.println(String.format("%s缺少参数：-scene", indent));
			return;
		}
		if(!line.hasOption("canvas")){
			System.out.println(String.format("%s缺少参数：-canvas", indent));
			return;
		}
		if(!line.hasOption("theme")){
			System.out.println(String.format("%s缺少参数：-theme", indent));
			return;
		}
		String desc = line.getOptionValue("desc");
		String scene = line.getOptionValue("scene");
		String canvas = line.getOptionValue("canvas");
		String theme = line.getOptionValue("theme");
		String o = line.getOptionValue("o");
		String swsid=sos.createSupersws(name,o,scene,canvas,theme,desc);
		System.out.println(String.format("%sswsid:%s", indent,swsid));
	}

	@Override
	public String cmdDesc() {
		return "为指定的框架创建一个超级视窗，超级视窗的持有者的系统角色表中将添加sosUsers角色，用法：create portalId -o xxx";
	}

	@Override
	public String cmd() {
		return "create";
	}

	@Override
	public Options options() {
		Options options = new Options();
		Option owner = new Option("o", "owner", true, "超级视窗的持有者");
		options.addOption(owner);
		Option scene = new Option("scene", "scene", true, "超级视窗的场景");
		options.addOption(scene);
		Option canvas = new Option("canvas", "canvas", true, "超级视窗的画布");
		options.addOption(canvas);
		Option theme = new Option("theme", "theme", true, "超级视窗的主题");
		options.addOption(theme);
		Option mode = new Option("desc", "description", true, "说明");
		options.addOption(mode);
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
