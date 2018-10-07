package cj.lns.chip.sos.website.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.MongoClient;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.disk.DiskInfo;
import cj.lns.chip.sos.disk.NetDisk;
import cj.lns.chip.sos.website.framework.IDatabaseCloud;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.WebUtil;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;
import cj.ultimate.security.RSAUtils;
@CjService(name="/pages/registerDiskSet.service")
public class RegisterDiskSet  extends Page{
	@CjServiceRef(refByName="databaseCloud")
	IDatabaseCloud db;
	@Override
	public void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		String b = new String(frame.content().readFully());
		try {
			b = URLDecoder.decode(b, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new CircuitException("503", e.getMessage());
		}
		Map<String, Object> map = WebUtil.parserParam(b);
		MongoClient client = this.db.userClient();
		CubeConfig home=new CubeConfig();
		
		double d=Double.valueOf((String)map.get("disk[homeSize]"));
		long homeSize=(long)(d*1024*1024*1024);
		home.setCapacity(homeSize);
		
		DiskInfo info=new DiskInfo((String)map.get("user[nickName]"), home);
		double d2=Double.valueOf((String)map.get("disk[diskSize]"));
		long diskSize=(long)(d2*1024*1024*1024);
		info.attr("capacity",diskSize);
		
		NetDisk.create(client,(String) map.get("user[userCode]"), (String)map.get("user[userCode]"),(String) map.get("user[password]"), info);
		
		int userState=Integer.valueOf((String)map.get("userState"));
		updateUserState((String)map.get("user[userCode]"),userState);
		
		genToolsKey((String)map.get("user[userCode]"));
	}
	private void genToolsKey(String userCode) throws CircuitException {
		String publicKey="";
		String privateKey="";
		try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();
            publicKey = RSAUtils.getPublicKey(keyMap);
            privateKey = RSAUtils.getPrivateKey(keyMap);
        } catch (Exception e) {
           throw new CircuitException("304", "生成key失败:"+e);
        }
		HashMap<String,String> maptools=new HashMap<>();
		maptools.put("user", userCode);
		maptools.put("privateKey", privateKey);
		maptools.put("publicKey", publicKey);
		TupleDocument<HashMap<String, String>> tuple=new TupleDocument<HashMap<String,String>>(maptools);
		db.getLnsSysHome().saveDoc("userKeyTools",tuple);
	}
	private void updateUserState(String userCode, int userState) throws CircuitException {
		Frame frame = new Frame("updateState /public/user/ sos/1.0");
		frame.parameter("state",String.valueOf(userState));
		frame.parameter("userCode",userCode);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		Frame back = new Frame(ctx.readFully());
		int state = Integer.valueOf(back.head("status"));
		if (state != 200) {
			throw new CircuitException(back.head("status"),
					back.head("message"));
		}
	}
}
