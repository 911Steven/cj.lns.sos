package cj.lns.chip.sos.website.portal.basic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cj.lns.chip.sos.cube.framework.FileSystem;
import cj.lns.chip.sos.cube.framework.TooLongException;
import cj.lns.chip.sos.cube.framework.lock.FileLockException;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.util.SvgThemeTool;
import cj.lns.common.sos.website.customable.IComponentFilter;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
import cj.ultimate.util.StringUtil;
@CjService(name="moduleResourceFilter")
public class ModuleResourceFilter extends SvgThemeTool implements IComponentFilter{

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		IModuleContext ctx=m.context();
		String path=frame.path();
		if(ctx.isWebResource(path, ".html|.htm")){
			if("/module-icon.svg".equals(path)){//请求框架的图标
				writePortalIcon(circuit);
				return;
			}
			if(path.endsWith("background-default.jpg")&&path.startsWith("/themes/")){
				IServicewsContext sws=IServicewsContext.context(frame);
				//background-image: url("img/background-default.jpg");原理：查找到默认的img/background-default.jpg文件名并替换成相应的背景文件名
				String background=sws.prop("portal.background");//实际背景图片
				if(StringUtil.isEmpty(background)||"#".equals(background)){//使用css定义时的默认背景
					byte[] b=ctx.resource(path);
					circuit.content().writeBytes(b);
					return;
				}
				String fn=String.format("/wallpaper/%s", background);
				FileSystem fs=m.site().diskLnsData().home().fileSystem();
				if(fs.existsFile(fn)){
					byte[] b;
					try {
						b = fs.openFile(fn).reader(0).readFully();
						circuit.content().writeBytes(b);
						return;
					} catch (FileNotFoundException | TooLongException
							| FileLockException e) {
						throw new CircuitException("404", e);
					}
					
				}else{//使用css定义时的默认背景
					byte[] b=ctx.resource(path);
					circuit.content().writeBytes(b);
					return;
				}
			}
			
			if (path.endsWith(".svg")) {
				IServicewsContext sws = IServicewsContext.context(frame);
				InputStream in = ctx.resourceAsStream(path);
				try {
					renderSvgByTheme(in, sws, circuit);
				} catch (IOException e) {
					throw new CircuitException("404", e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				}
				return;
			}
			byte[] b=ctx.resource(path);
			circuit.content().writeBytes(b);
			return;
		}
		plug.flow(frame, circuit);
	}

	private void writePortalIcon(Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		InputStream in = m.chip().info().getIconStream();
		if(in==null){
			return ;
		}
		int blen = 2048;
		int timeRead = 0;
		byte[] b = new byte[blen];
		IFlowContent out=circuit.content();
		try {
			while ((timeRead = in.read(b, 0, blen)) > 0) {
				out.writeBytes(b, 0, timeRead);
			}
		} catch (IOException e) {
			throw new CircuitException("404",
					String.format("读取模块图标失败:%s", m.chip().info().getId()));
		}
	}

	@Override
	public int matchCircuit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
