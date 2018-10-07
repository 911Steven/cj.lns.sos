package cj.lns.chip.sos.website.sws.provider;

import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "switchWinToolSoProvider")
public class SwitchWinToolSoProvider implements ISecurityObjectProvider {


	private ToolSO root;
	private IAclFinder finder;

	@Override
	public String category() {
		return "position";
	}

	@Override
	public ToolSO root() {
		return root;
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if (!ctx.isOwner())
			return null;
		if (valueId.equals(root.getId())) {
			return root;
		}
		return null;
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		root = new ToolSO();
		root.setDescription("切换视窗工具");
		root.setIcon("./startMenu/img/iconfont-weibo.svg");
		root.setName("切换");
		root.setCommand("./servicews/components/switchPopup.html");
		root.setId(ISurfacePosition.POSITION_TOOLBAR_WIN_SWITCH);
		root.setSort(4);
		this.finder = finder;
		return null;
	}

}
