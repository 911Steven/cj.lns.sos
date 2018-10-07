package cj.lns.chip.sos.website.framework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.util.SvgThemeTool;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.IHttpFilter;
import cj.studio.ecm.plugins.moduleable.ModuleContext;
import cj.ultimate.util.StringUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 框架入口过滤器
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
public class FrameworkEntrypointFilter extends SvgThemeTool
		implements IHttpFilter {
	List<String> httpPublicDirs;// 在http.root下的所有目录均为开放目录，即在framework下的目录。
	@CjServiceRef(refByName = "serviceosWebsiteModule")
	IServiceosWebsiteModule sos;

	@Override
	public int sort() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		Circuit c = circuit;
		if (httpPublicDirs == null) {
			this.httpPublicDirs = (List<String>) sos.context()
					.cache("http.public.dirs");
		}
		String relurl = frame.relativePath();
		String startPath = "";
		if (!relurl.equals("/")) {
			int pos = relurl.indexOf("/", 1);
			if (pos > 0) {
				startPath = relurl.substring(0, pos);
			}
		}
		// 如果有查询串则说明不是简单的开放资源，也就是根模块不处理带查询串的资源，这交由其下的模块处理。目前仅用于csc资源的调度，在resource模块中处理
		if (frame.containsParameter("csc_resource_path")) {
			IPin csc = sos.downriver("resource");
			csc.flow(frame, circuit);
			return;
		}
		if (httpPublicDirs.contains(startPath)) {// 只要是在根站点下（被定义在framework下）的目录下都是开放资源
			if (relurl.endsWith(".svg")) {// 处理根的svg
				String file = String.format("%s%s",
						circuit.attribute("http.root"), relurl);
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);
					IServicewsContext sws = IServicewsContext.context(frame);
					if (sws == null) {
						renderSvgByTheme(in, "", circuit);
					} else {
						renderSvgByTheme(in, sws, circuit);
					}
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

			if ("/portlets".equals(startPath)) {// 调度portlets
				String url=frame.url();
				int endpos = url.lastIndexOf(".html");
				if (endpos > 0) {
					int statpos=url.indexOf("/portlets");
					String path = url.substring(statpos+startPath.length() + 1,
							endpos);
					path=path.replace("/", ".").replace("..", ".");
					String jsspath = String.format("$.cj.jss.portlets.%s",path);
					ScriptObjectMirror jss = (ScriptObjectMirror) plug.site()
							.getService(jsspath);
					if(jss==null){
						plug.flow(frame, c);//将html当成普通资源查找
						return;
					}
					if(!jss.hasMember("render")){
						throw new CircuitException("503", "栏目信息看板的jss服务定义错误，缺少render方法");
					}
					String arr[]=path.split("\\.");
					
					String httproot=(String)circuit.attribute("http.root");
					String porletlocation=String.format("/portlets/%s/%s",arr[0],arr[1]);
					ModuleContext ctx=new ModuleContext(httproot, porletlocation);
					Object docobj=jss.callMember("render", ctx);
					if(docobj==null){
						throw new CircuitException("404", "请求的jss服务返回为空:"+url);
					}
					Document doc=(Document)docobj;
					circuit.content().writeBytes(doc.toString().getBytes());
					return;
				}
			}
			plug.flow(frame, c);
			return;
		}
		if (StringUtil.isEmpty(startPath)
				&& !frame.containsParameter("swsid")) {// 不含视窗参数则进入website处理程序，即net的通用web架构
			plug.flow(frame, circuit);
			return;
		}
		String rootName = frame.rootName();
		frame.head("root-name", rootName);
		frame.url(frame.relativeUrl());
		if (StringUtil.isEmpty(startPath)) {// 如果是框架渲染请求
			sos.in().flow(frame, c);
		} else {
			sos.site().in().flow(frame, c);
		}
	}

}
