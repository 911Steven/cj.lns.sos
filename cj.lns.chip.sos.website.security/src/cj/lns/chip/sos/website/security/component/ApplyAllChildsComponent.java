package cj.lns.chip.sos.website.security.component;

import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
/**
 * 应用到全部子资源，直到叶子节点
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
@CjService(name="/applyAllChilds.service")
public class ApplyAllChildsComponent implements IComponent{

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		/*
		 * 该功能必须在做出父子资源的时候再实现，如菜单资源时。
		 * ~ 从安全中心读取资源，并根据AclFinder获取子节点，许可表结构不必变。
		 * ~ 为了优化性能，先取到许可物理标识，而后每次将子资源集合发给服务器端存储。
		 */
	}

}
