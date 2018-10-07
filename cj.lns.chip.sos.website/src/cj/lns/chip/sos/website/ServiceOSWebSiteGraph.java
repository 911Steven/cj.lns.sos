package cj.lns.chip.sos.website;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.Access;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.GraphCreator;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.net.layer.ISession;
import cj.studio.ecm.net.layer.ISessionEvent;
import cj.studio.ecm.net.layer.ISessionManager;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.net.web.HttpCircuit;
import cj.studio.ecm.net.web.HttpFrame;
import cj.studio.ecm.net.web.WebSiteGraph;
import cj.studio.ecm.net.web.WebSiteGraphCreator;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.script.IJssModule;

@CjService(name = "sosWebsiteGraph", isExoteric = true)
public class ServiceOSWebSiteGraph extends WebSiteGraph {

	
	protected class ConnectCscSink implements ISink {

		@Override
		public void flow(Frame frame, Circuit circuit, IPlug plug)
				throws CircuitException {
			if ("CSC/1.0".equals(frame.protocol())) {
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC, "true");
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT,
						"120000");
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_ASYNC_TIMEOUT,
						"240000");
				plug.branch("$.cscOut").flow(frame, circuit);
				return;
			}
			plug.flow(frame, circuit);
		}

	}
	protected class ConnectSnsSink implements ISink {

		@Override
		public void flow(Frame frame, Circuit circuit, IPlug plug)
				throws CircuitException {
			if ("PEER/1.0".equals(frame.protocol())) {
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC, "true");
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT,
						"120000");
				frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_ASYNC_TIMEOUT,
						"240000");
				plug.branch("$.snsOut").flow(frame, circuit);
			}else if ("IM/1.0".equals(frame.protocol())) {
				plug.branch("$.snsOut").flow(frame, circuit);
			}else{
				plug.flow(frame, circuit);
			}
		}

	}

	protected class ConnectRemoteSink implements ISink {

		@Override
		public void flow(Frame frame, Circuit circuit, IPlug plug)
				throws CircuitException {
			frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC, "true");
			frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT,
					"120000");
			frame.head(NetConstans.FRAME_HEADKEY_CIRCUIT_ASYNC_TIMEOUT,
					"240000");
			if (!frame.containsParameter("cjtoken")) {
				String cjtoken = site().getProperty("sos.remote.cjtoken");
				frame.parameter("cjtoken", cjtoken);
			}
			String remoteSosProcessId = site()
					.getProperty("sos.remote.processId");
			String url = frame.url();
			if (!url.startsWith(String.format("/%s", remoteSosProcessId))) {
				frame.url(String.format("/%s%s", remoteSosProcessId,
						frame.url()));
			}
			plug.branch("$.remoteOut").flow(frame, circuit);
		}

	}

	@Override
	protected GraphCreator newCreator() {
		return new MySiteGraphCreator();
	}
	private void scansHttpRootDirs(String httpRootPath,
			List<String> httpPublicDirs) {
		File f = new File(httpRootPath);
		File[] arr = f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File d : arr) {
			httpPublicDirs.add(String.format("/%s", d.getName()));
		}
	}
	@Override
	protected void buildIt(GraphCreator creator) {
		super.buildIt(creator);
		IChip chip=(IChip)site().getService(IChip.class.getName()	);
		String http_root = String
				.format("%s/%s/%s", chip.info().getProperty("home.dir"),
						IJssModule.RUNTIME_SITE_DIR,
						chip.info().getResourceProp("http.root")).replace("///", "/")
				.replace("//", "/").replace("/", File.separator);
		
		List<String> httpPublicDirs = new ArrayList<String>();
		scansHttpRootDirs(http_root, httpPublicDirs);
		
		IModule m = ServiceosWebsiteModule.get();
		try {
			m.context().cache("http.public.dirs", httpPublicDirs);
		} catch (CircuitException e) {
		}
		
		IPin storeOut = creator().newWirePin("remoteOutputPin", Access.output);// 访问远程服务
		IPin snsOut = creator().newWirePin("snsOutputPin", Access.output);// 通过lns云计算获取公网网站、芯片进程等。
		IPin cscOut = creator().newWirePin("cscOutputPin", Access.output);// 处理csc/1.0,deploy/1.0协仪。
		in("input").plugFirst("translate", new Translate());
		
		IPlug remotePlug = m.out().plugLast("connectRemoteSink",
				creator.newSink("connectRemoteSink"));
		remotePlug.plugBranch("$.remoteOut", storeOut);

		IPlug snsPlug = m.site().out().plugLast("connectSnsSink",
				creator.newSink("connectSnsSink"));
		snsPlug.plugBranch("$.snsOut", snsOut);
		
		IPlug cscPlug = m.site().out().plugLast("connectCscSink",
				creator.newSink("connectCscSink"));
		cscPlug.plugBranch("$.cscOut", cscOut);
	}

	public class MySiteGraphCreator extends WebSiteGraphCreator {
		@Override
		public ISink createYourSink(String name) {
			ISink sink = null;
			switch (name) {
			case "connectRemoteSink":
				sink = new ConnectRemoteSink();
				break;
			case "connectSnsSink":
				sink = new ConnectSnsSink();
				break;
			case "connectCscSink":
				sink = new ConnectCscSink();
				break;
			}
			
			if (sink != null)
				return sink;
			sink = super.createYourSink(name);
			return sink;
		}

		@Override
		protected ISessionManager createSessionManager() {
			// TODO Auto-generated method stub
			ISessionManager sm = super.createSessionManager();
			sm.addEvent(new SessionManagerEvent());
			return sm;
		}
	}

	public class SessionManagerEvent implements ISessionEvent {

		@Override
		public void doEvent(String eventType, Object... args) {
			System.out.println("sosWebsiteGraph :" + eventType + " "
					+ ((ISession) args[0]).id());
			if ("attributeAdd".equals(eventType)) {
				if (ISubject.KEY_SUBJECT_IN_SESSION.equals(args[1])) {
					ISession s = (ISession) args[0];
					ISubject subject = (ISubject) s
							.attribute(ISubject.KEY_SUBJECT_IN_SESSION);
					System.out.println("当前登录用户是：" + subject.principal());
				}
			}

		}

	}
	//为了接收非http侦
	public class Translate implements ISink {

		@Override
		public void flow(Frame frame, Circuit circuit, IPlug plug)
				throws CircuitException {
			if (frame instanceof HttpFrame) {
				plug.flow(frame, circuit);
			} else {
				HttpFrame f = new HttpFrame(frame.toString());
				f.copyFrom(frame, true);
				HttpCircuit c = new HttpCircuit(circuit.toString());
				c.coverFrom(circuit);
				plug.flow(f, c);
				circuit.coverFrom(c);
			}
		}

	}
}
