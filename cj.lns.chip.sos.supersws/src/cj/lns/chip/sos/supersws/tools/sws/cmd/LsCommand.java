package cj.lns.chip.sos.supersws.tools.sws.cmd;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.sws.SwsConsole;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.ultimate.util.StringUtil;

@CjService(name = "lsSwsCommand")
public class LsCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "swsConsole")
	SwsConsole sosconsole;

	public void doCommand(CmdLine cl) throws IOException {
		ISos sos = (ISos) cl.prop("sos");
		String indent = cl.propString("indent");
		CommandLine line = cl.line();
		String skip=line.getOptionValue("skip");
		if (StringUtil.isEmpty(skip)) {
			skip="0";
		}
		String limit=line.getOptionValue("limit");
		if (StringUtil.isEmpty(limit)) {
			limit="10";
		}
		int level = 3;
		if (line.hasOption("l")) {
			level = Integer.valueOf(line.getOptionValue("l"));
		}
		List<SwsInfo> list = sos.findSwsListByLevel(level,skip,limit);
		for (SwsInfo info : list) {
			System.out.println(String.format("%sswsid:%s-----------", indent,
					info.getId()));
			System.out.println(String.format("%s%sname:%s", indent, indent,
					info.getName()));
			System.out.println(String.format("%s%sowner:%s", indent, indent,
					info.getOwner()));
			System.out.println(String.format("%s%sportol:%s", indent, indent,
					info.getUsePortal()));
			System.out.println(String.format("%s%sdesc:%s", indent, indent,
					info.getDescription()));
			System.out.println(String.format("%s%sinherit:%s", indent, indent,
					info.getInheritId()));
			System.out.println(String.format("%s%slevel:%s", indent, indent,
					info.getLevel()));
		}
	}

	@Override
	public String cmdDesc() {
		return "查看视窗列表";
	}

	@Override
	public String cmd() {
		return "ls";
	}

	@Override
	public Options options() {
		Options options = new Options();
		Option mode = new Option("l", "level", true,
				"视窗级别,0是超级视窗，1是基础视窗；2是公共视窗；3是个人视窗。默认是3");
		options.addOption(mode);
		Option skip = new Option("skip", "skip", true, "开始");
		options.addOption(skip);
		Option limit = new Option("limit", "limit", true, "一页大小");
		options.addOption(limit);
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
