package cj.lns.chip.sos.supersws.tools;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import cj.lns.chip.sos.supersws.ISos;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "superswsEntryPoint", isExoteric = true)
public class SosEntryPoint {
	Logger logger = Logger.getLogger(SosEntryPoint.class);
	@CjServiceRef(refByName = "sosConsole")
	SosConsole console;
	@CjServiceRef(refByName="sos")
	ISos sos;
	public void main(CommandLine line)
			throws IOException, InterruptedException {
//		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		sos.load(line);
		console.monitor(sos);
		System.out.println("正在退出...");
		
		try {// 如果3秒后还没退出，则强制
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		} finally {
			System.exit(0);
		}
	}
	public void setMain(IAssembly main){
		sos.setMain(main);
	}
	public void setHomeDir(String homeDir){
		sos.setHomeDir(homeDir);
	}
}
