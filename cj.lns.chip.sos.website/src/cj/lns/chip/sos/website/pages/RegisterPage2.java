package cj.lns.chip.sos.website.pages;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.cube.framework.DirectoryInfo;
import cj.lns.chip.sos.cube.framework.FileSystem;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;

@CjService(name = "/public/register2.html")
public class RegisterPage2 extends Page {

	@Override
	protected void doPage(Frame frame, Circuit circuit,IPlug plug, PageContext ctx)
			throws CircuitException {
		
		Document doc = ctx.html(frame.relativePath());
		//打印照片
		Element box=doc.select(".register > .box > .right").first();
		IServiceosWebsiteModule m= ServiceosWebsiteModule.get();
		List<String> faces=getFaces(m);
		//随机选一张作为头照
		String defau=faces.get(frame.hashCode()%faces.size());
		String src=String.format("../resource/dd/%s?path=faces://", defau);
		box.select(">.face>img").attr("src",src);
		box.select(">.face>img").attr("fn",defau);
		Element ul=box.select(">.options").first();
		Element li=ul.select(">li").first().clone();
		ul.empty();
		
		printPics(ul,li,faces);
		circuit.content().writeBytes(doc.toString().getBytes());
	}
	/*
	 * ./resource/dd/guangjiewang.svg?path=faces://
	 */
	private void printPics(Element ul, Element li, List<String> faces) {
		for(String face:faces){
			li=li.clone();
			
			String src=String.format("../resource/dd/%s?path=faces://", face);
			li.select(">img").attr("src",src);
			li.select(">img").attr("fn",face);
			ul.appendChild(li);
		}
		
	}
	//获取图片地址
	private List<String> getFaces(IServiceosWebsiteModule m) {
		INetDisk disk=m.site().diskLnsData();
		ICube fc=null;
		if(disk.existsCube("faces")){
			fc=disk.cube("faces");
		}else{
			CubeConfig conf=new CubeConfig(-1);
			fc=disk.createCube("faces", conf);
		}
		FileSystem fs=fc.fileSystem();
		DirectoryInfo root=fs.dir("/");
		return root.listFileNames();
	}


}
