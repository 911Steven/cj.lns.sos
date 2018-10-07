package cj.lns.chip.sos.website.framework;

import cj.lns.chip.sos.website.framework.sink.DirectSink;
import cj.lns.chip.sos.website.framework.sink.PartSink;
import cj.lns.chip.sos.website.framework.sink.RenderSink;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.IModuleContainer;

@CjService(name = "hub")
public class Hub implements IHub {
	@CjServiceRef(refByName = "serviceosModuleContainer")
	IModuleContainer container;

	@Override
	public void refresh() {
		IModule sos = container.module("serviceos");
		
		sos.in().unPlug("renderSink");
		IPlug renderPlug = sos.in().plugLast("renderSink", createRenderSink());//renderSink仅调度到portal，sws
		IPin portalDown=sos.downriver("portal");
		renderPlug.plugBranch(portalDown.name(), portalDown);
		IPin swsDown=sos.downriver("servicews");//渲染是先flow到视窗，在视窗内为session设置视窗上下文，如果成功则回来flow portal
		renderPlug.plugBranch(swsDown.name(), swsDown);
		
		sos.site().in().unPlug("partSink");//partsink专门处理外部来的http协议，因此独立一个sink类型
		IPlug partPlug = sos.site().in().plugLast("partSink", createPartSink());//除sos模块端子之外的所有其它模块的上下游输入端子
		
		//检出一级模块及其下模块的要插入directsink的输出端子，设为集合a
		//检出一级模块及其下模块的内核输入端子，设为集合b
		//将b集合插入到a的任一元素，但排除b集合中与a中该元素同名的端子，且，如果是partPlug退出，如果非则对a的元素再插入remotePin和fetchPin
		
		String[] names = container.enumModuleName();
		for (String name : names) {
			if ("serviceos".equals(name)){
				String[] chipids=sos.enumSubordinateChipId();
				for(String upOut:chipids){//将一级模块的芯片的上下游输出端子接下directsink
					IModule m=container.module(upOut);
					m.out().unPlug("directSink");
					IPlug outPlug=m.out().plugLast("directSink", createDirectSink());
					plugDirectSink(outPlug,sos,container);
				}
				continue;
			}
			//以下插入内核输入端子
			IModule m = container.module(name);
			partPlug.plugBranch(name, m.site().in());//插入部件分支
			//其后将各个模块的内核输出端子插入directSink
			m.site().out().unPlug("directSink");
			IPlug outPlug=m.site().out().plugLast("directSink", createDirectSink());
			plugDirectSink(outPlug,sos,container);
		}
	}

	private void plugDirectSink(IPlug outPlug, IModule sos, IModuleContainer container) {
		//插入各模块中所有的内核输入端子，而除去模块自身的内核输入端子
		//最后插入远程端子和抓取端子
		String[] names = container.enumModuleName();
		for (String name : names) {
			if ("serviceos".equals(name)){
				continue;
			}
			if(name.equals(outPlug.owner())){//忽略自身内核输出再接入自身的内核输入端子
				continue;
			}
			IModule m = container.module(name);
			outPlug.plugBranch(name, m.site().in());
		}
		//将两个外部输出端子插入ß
		outPlug.plugBranch("$.remoteOut", sos.out());
		outPlug.plugBranch("$.snsOut", sos.site().out());
		outPlug.plugBranch("$.cscOut", sos.site().out());
	}

	protected ISink createPartSink() {
		return new PartSink();
	}

	protected ISink createDirectSink() {
		return new DirectSink();
	}

	protected ISink createRenderSink() {
		return new RenderSink();
	}

}
