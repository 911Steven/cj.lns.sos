package cj.lns.chip.sos.website.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cj.lns.chip.sos.cube.framework.FileInfo;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IReader;
import cj.lns.chip.sos.cube.framework.OpenMode;
import cj.lns.chip.sos.cube.framework.TooLongException;
import cj.lns.chip.sos.cube.framework.lock.FileLockException;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.IWebsiteConstants;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.framework.csc.CscDeveloperFactory;
import cj.lns.chip.sos.website.framework.csc.CscComputer;
import cj.lns.chip.sos.website.util.SvgThemeTool;
import cj.lns.common.sos.website.customable.IComponentFilter;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFeedback;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
import cj.ultimate.util.StringUtil;

@CjService(name = "moduleResourceFilter")
public class ModuleResourceFilter extends SvgThemeTool implements IComponentFilter {
	public final static String KEY_CSC_RESOURCE="csc_resource_path";
	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// TODO Auto-generated method stub
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IModuleContext ctx = m.context();
		if(frame.containsParameter(KEY_CSC_RESOURCE)){
			processCscResource(frame,circuit,plug,m);
			return;
		}
		String path = frame.path();
		//只要来访问resouce模块的都是资源，因此注释掉
//		if (ctx.isWebResource(path, ".html|.htm|.service")) {
			/*
			 * ./resource/dd/guangjiewang.svg?path=home://chatGroups/570ba7e561c707980a8e54ae/head
			 */
			String type = frame.rootName();
			INetDisk disk = null;
			switch (type) {
			case "ud":// 在用户磁盘
				// IServicewsContext sws= IServicewsContext.context(frame);
				// String owner="";
				// if(sws==null){
				// ISubject subject=ISubject.subject(frame);
				// owner=subject.principal();
				// }else{
				// owner=sws.owner();
				// }
				String u = frame.parameter("u");
				if (StringUtil.isEmpty(u)) {
					throw new CircuitException("404",
							"resource not found. bacuse the parameter 'u' is not be exist");
				}
				disk = m.site().diskOwner(u);
				processResource(frame, circuit, disk);
				break;
			case "dd":// 在云数据磁盘
				disk = m.site().diskLnsData();
				processResource(frame, circuit, disk);
				break;
			case "ld":// 在云系统磁盘
				disk = m.site().diskLnsSystem();
				processResource(frame, circuit, disk);
				break;
			default:
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
				}
				byte[] b = ctx.resource(path);
				circuit.content().writeBytes(b);
			}
			return;
//		}
//
//		plug.flow(frame, circuit);
	}

	private void processCscResource(Frame frame, Circuit circuit, IPlug plug, IServiceosWebsiteModule m)throws CircuitException {
		String cscpath=frame.parameter(KEY_CSC_RESOURCE);//格式：/developer/cscchipName
		if(cscpath.startsWith("/")){
			cscpath=cscpath.substring(1,cscpath.length());
		}
		int second=cscpath.indexOf("/");
		String developer=cscpath.substring(0,second);
		if(StringUtil.isEmpty(developer)){
			throw new CircuitException("404","缺少参数：developer");
		}
		String chip=cscpath.substring(second+1,cscpath.length());
		if(chip.endsWith("/")){
			chip=chip.substring(0, chip.length()-1);
		}
		if(StringUtil.isEmpty(chip)){
			throw new CircuitException("404","缺少参数：chip");
		}
		CscDeveloperFactory factory=(CscDeveloperFactory)m.site().getService("cscDeveloperFactory");
		CscComputer computer=factory.getComputer(developer);
		if(computer==null){
			throw new CircuitException("404","用户："+developer+" 不是开发者");
		}
		
		String visitor=ISubject.subject(frame).principal();
		//根据开发者得到所在的云服地址容器
		//根据开发者和他的服务名，可以定位到宿主位置及容器端口号，然后由服务名访问到具体的服务
		//因此：开发者和服务名确定一个key，写作：/developer/cscServiceName，先通过key找到配置，便有以下代码
		//开发者与访问者是两个不同的用户，前者是服务提供者，后者是使用者。
		Frame f = new Frame(String.format("get /%s%s csc/1.0",chip,frame.relativePath()));
		f.head("csc-visitor",visitor);//总是有访问者
		f.head("dest-address",computer.getCscHost());
		f.head("csc-customer",computer.getCscCustomer());//客户服务在客户机上的端口
		Circuit c = new Circuit("csc/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back=new Frame(c.content().readFully());
		int state=Integer.valueOf(back.head("status"));
		if(state>=300){
			throw new CircuitException(back.head("status"),"在远程上出现错误："+back.head("message"));
		}
		IServicewsContext sws=IServicewsContext.context(frame);
		if(sws!=null&&frame.path().endsWith(".svg")){
			try {
				renderSvgByTheme(back.content().readFully(), sws, circuit);
			} catch (IOException e) {
				throw new CircuitException("504",e);
			}
		}else{
			circuit.content().writeBytes(back.content());
		}
	}

	private void processResource(Frame frame, Circuit circuit, INetDisk disk)
			throws CircuitException {
		String download=frame.parameter("force-download");
		String fn = frame.name();
		try {
			if(StringUtil.isEmpty(fn)){
				fn=frame.relativePath();
			}
			fn = URLDecoder.decode(fn, "utf-8");
		} catch (UnsupportedEncodingException e1) {
		}
		if (StringUtil.isEmpty(fn)) {
			throw new CircuitException("404",
					String.format("缺少文件名:%s", frame.url()));
		}
		String pathInDisk = frame.parameter("path");
		try {
			pathInDisk = URLDecoder.decode(pathInDisk, "utf-8");
		} catch (UnsupportedEncodingException e1) {
		}
		if (StringUtil.isEmpty(pathInDisk)) {
			throw new CircuitException("404", "缺少参数：path，格式：cubeName://path");
		}
		String cubeName = pathInDisk.substring(0, pathInDisk.indexOf("://"));
		String dir = pathInDisk.substring(pathInDisk.indexOf("://") + 3,
				pathInDisk.length());
		String file = String.format("/%s/%s", dir, fn).replace("//", "/");
		ICube cube = disk.cube(cubeName);
		try {
			FileInfo fi = cube.fileSystem().openFile(file, OpenMode.onlyOpen);
			IReader reader = null;
			if (frame.containsHead("Neuron-Chunked-Position")) {// 说明是块请求
				String backurl="";
				if(!"true".equals(frame.head("catch-default-app"))){
					backurl=String.format("/resource%s", frame.url());
				}else{
					backurl=frame.url();
				}
				
				long pos = Long.valueOf(frame.head("Neuron-Chunked-Position"));
				int range = Integer.valueOf(frame.head("Neuron-Chunked-Range"));
				byte[] b = new byte[range];
				reader = fi.reader(pos);

				int readsize = reader.read(b, 0, b.length);
				IFeedback fb = circuit.feedback(IFeedback.KEY_OUTPUT_FEEDBACK);
				if (readsize > -1) {//当返回的内容为-1长度则表示文件读结束
					Frame f = new Frame(String.format("chunked %s chunked/1.0", backurl));
					f.content().writeBytes(b, 0, readsize);
					fb.doBack(f, circuit);
				}else{
					Frame f = new Frame(String.format("close %s chunked/1.0", backurl));
					fb.doBack(f, circuit);
				}
				return;
			}
			// 这种情况只适合website直接挂在httpServer上，对于挂到rio-tcp,rio-udt等net上时（网间转发链路）由于主动回发多次目前没有该功能在客端的httpServer上以接收
			// 因此，目前这种对chunked的请求，只能在专门的文件服务上使用，即客户端直连httpServer才可（或者跳出neuron架构而采用apache,ngnix)
			// 见：HttpServerSender方法的实现
			if (fi.spaceLength() > IWebsiteConstants.STATIC_RESOURCE_OVERFLOW_VALUE) {
				IFeedback fb = circuit.feedback(IFeedback.KEY_OUTPUT_FEEDBACK);
				boolean isHttp = "http"
						.equals(circuit.attribute("select-simple"));
				if (isHttp) {
					Frame f = new Frame("open / chunked/1.0");
					fb.doBack(f, circuit);
					byte[] b = new byte[8096];
					int readlen = 0;
					reader = fi.reader(0);
					while ((readlen = reader.read(b)) > -1) {
						f = new Frame("chunked / chunked/1.0");
						f.content().writeBytes(b, 0, readlen);
						fb.doBack(f, circuit);
					}
					f = new Frame("close / chunked/1.0");
					if("true".equals(download)){
						f.contentType("application/octet-stream");//为了使图片、视频等mime格式都能下载，所以在此强制为任意二进制类型，客户端得知此类型会以下载处理。
					}
					fb.doBack(f, circuit);
					return;
				}
				// 非http将告知客端对同一地址资源发起多次块请求
				String backurl="";
				if(!"true".equals(frame.head("catch-default-app"))){
					backurl=String.format("/resource%s", frame.url());
				}else{
					backurl=frame.url();
				}
				Frame f = new Frame(String.format("open %s chunked/1.0", backurl));
				f.head("Neuron-Chunked-Range", "65536");
				if("true".equals(download)){
					f.contentType("application/octet-stream");//为了使图片、视频等mime格式都能下载，所以在此强制为任意二进制类型，客户端得知此类型会以下载处理。
				}
				//open指令不写数
//				reader = fi.reader(0);
//				byte[] b = new byte[8192];
//				int readsize = reader.read(b, 0, b.length);
//				f.content().writeBytes(b, 0, readsize);
				fb.doBack(f, circuit);
				return;
			}
			if("true".equals(download)){
				circuit.contentType("application/octet-stream");//为了使图片、视频等mime格式都能下载，所以在此强制为任意二进制类型，客户端得知此类型会以下载处理。
			}
			byte[] b = new byte[8096];
			int readlen = 0;
			reader = fi.reader(0);
			if (file.endsWith(".svg")) {
				IServicewsContext sws = IServicewsContext.context(frame);
				try{
				renderSvgByTheme(reader.readFully(), sws, circuit);
				} catch (IOException e) {
					throw new CircuitException("404", e);
				} 
			}else{
				IFlowContent flow = circuit.content();
				while ((readlen = reader.read(b)) > -1) {
					flow.writeBytes(b, 0, readlen);
				}
			}
			reader.close();
		} catch (FileNotFoundException |TooLongException | FileLockException e) {
			throw new CircuitException("404", e.getMessage());
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
