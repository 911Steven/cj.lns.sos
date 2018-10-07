package cj.lns.chip.sos.supersws.tools.user.cmd;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.user.UserConsole;
import cj.lns.common.sos.service.model.SosUser;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.ultimate.util.StringUtil;

@CjService(name = "lsUserCommand")
public class LsCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "userConsole")
	UserConsole sosconsole;

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
		String user=line.getOptionValue("u");
		List<SosUser> list = sos.findUserListByCode(user,skip,limit);
		for (SosUser u : list) {
			System.out.println(String.format("%sid:%s-----------", indent,
					u.getId()));
			System.out.println(String.format("%s%scode:%s", indent, indent,
					u.getUserCode()));
			System.out.println(String.format("%s%snick:%s", indent, indent,
					u.getNickName()));
			System.out.println(String.format("%s%sstate:%s", indent, indent,
					u.getStatus()));
			System.out.println(String.format("%s%screateTime:%s", indent, indent,
					u.getCreatetime()));
			System.out.println(String.format("%s%sdefaultSws:%s", indent, indent,
					u.getDefaultSws()));
			System.out.println(String.format("%s%sreal:%s", indent, indent,
					u.getRealName()));
			System.out.println(String.format("%s%ssign:%s", indent, indent,
					u.getSignatureText()));
			
		}
	}

	@Override
	public String cmdDesc() {
		return "查看用户列表";
	}

	@Override
	public String cmd() {
		return "ls";
	}

	@Override
	public Options options() {
		Options options = new Options();
		Option mode = new Option("u", "user", true,
				"查看指定用户，如果未指定则查看所有用户，默认分页大小10");
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
