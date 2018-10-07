package cj.lns.chip.sos.supersws.tools.user.cmd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.chip.sos.supersws.tools.CmdLine;
import cj.lns.chip.sos.supersws.tools.Command;
import cj.lns.chip.sos.supersws.tools.user.UserConsole;
import cj.lns.common.sos.service.model.SosUser;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "viewUserCommand")
public class ViewCommand extends Command {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "userConsole")
	UserConsole sosconsole;

	@SuppressWarnings("unchecked")
	public void doCommand(CmdLine cl) throws IOException {
		ISos sos = (ISos) cl.prop("sos");
		String indent = cl.propString("indent");
		CommandLine line = cl.line();
		List<String> args = line.getArgList();
		if (args.isEmpty()) {
			System.out.println(indent + "需要指定超级视窗号");
			return;
		}
		String name = args.get(0).trim();
		Object o = sos.viewUserByCode(name);
		if (o == null) {
			System.out.println(String.format("%s不存在", indent));
			return;
		}
		Map<String, Object> d = (Map<String, Object>) o;
		Map<String, Object> u = (Map<String, Object>) d.get("user");
		List<Map<String, Object>> roles = (List<Map<String, Object>>) d.get("roles");
		Map<String, Object> disk = (Map<String, Object>) d.get("disk");
		List<Map<String, Object>> s = (List<Map<String, Object>>) d
				.get("swsList");
		System.out.println(String.format("%sid:%s", indent,
				(long) (double) u.get("id")));
		System.out
				.println(String.format("%scode:%s", indent, u.get("userCode")));
		System.out
				.println(String.format("%snick:%s", indent, u.get("nickName")));
		String roleStr="";
		for(Map<String, Object> role:roles){
			roleStr=String.format("%s,%s", roleStr,role.get("roleCode"));
		}
		if(roleStr.startsWith(",")){
			roleStr=roleStr.substring(1, roleStr.length());
		}
		System.out
		.println(String.format("%ssosRoles:%s", indent,roleStr));
		if(disk!=null){
			System.out.println(String.format("%s%scapacity:%s", indent, indent,
					(long) (double) (disk.get("capacity"))));
			System.out.println(String.format("%s%scubeCount:%s", indent, indent,
					(long) (double)disk.get("cubeCount")));
			System.out.println(String.format("%s%suseSpace:%s", indent, indent,
					(long) (double)disk.get("useSpace")));
			System.out.println(String.format("%s%sdataSize:%s", indent, indent,
					(long) (double)disk.get("dataSize")));
		}
		System.out.println(
				String.format("%s---------------------", indent));
		for (Map<String, Object> sws : s) {
			System.out.println(String.format("%s%sswsid:%s", indent, indent,
					(long) (double) (sws.get("id"))));
			System.out.println(String.format("%s%sname:%s", indent, indent,
					sws.get("name")));
			System.out.println(String.format("%s%sportol:%s", indent, indent,
					sws.get("usePortal")));
			System.out.println(String.format("%s%sinherit:%s", indent, indent,
					(long) (double) sws.get("inheritId")));
			System.out.println(String.format("%s%slevel:%s", indent, indent,
					sws.get("level")));
			System.out.println(
					String.format("%s%s---------------------", indent, indent));
		}
	}

	@Override
	public String cmdDesc() {
		return "查看用户详细信息，用法：view userCode";
	}

	@Override
	public String cmd() {
		return "view";
	}

	@Override
	public Options options() {
		Options options = new Options();
		// Option mode = new Option("u", "user", true,
		// "查看指定用户，如果未指定则查看所有用户，默认分页大小10");
		// options.addOption(mode);
		// Option skip = new Option("skip", "skip", true, "开始");
		// options.addOption(skip);
		// Option limit = new Option("limit", "limit", true, "一页大小");
		// options.addOption(limit);
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
