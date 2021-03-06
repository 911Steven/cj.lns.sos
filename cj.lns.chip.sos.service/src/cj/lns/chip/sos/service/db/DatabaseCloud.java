package cj.lns.chip.sos.service.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.disk.DiskInfo;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.disk.NetDisk;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;
import cj.ultimate.util.StringUtil;

@CjService(name = "databaseCloud")
public class DatabaseCloud implements IDatabaseCloud {
	INetDisk lnsSysDisk;
	ICube lnsSysHome;
	Properties props;
	private MongoClient lnsSysclient;
	private MongoClient lnsDataclient;
	private MongoClient userClient;
	private INetDisk lnsDataDisk;
	private ICube lnsDataHome;
	public MongoClient userClient() {
		return userClient;
	}
	@Override
	public INetDisk getLnsSysDisk() {
		return lnsSysDisk;
	}
	@Override
	public void close() {
		if(lnsSysclient!=null){
			lnsSysclient.close();
		}
		if(lnsDataclient!=null){
			lnsDataclient.close();
		}
		if(userClient!=null){
			userClient.close();
		}
	}
	@Override
	public void init(Properties props) {
		this.props=props;
		this.lnsSysclient=client("lnsdisk.sys.address","lnsdisk.sys.credential",props);
		this.lnsDataclient=client("lnsdisk.data.address","lnsdisk.data.credential",props);
		 this.userClient=client("userdisk.address","userdisk.credential",props);
		
		
		String lnsDisk = props.getProperty("lnsdisk.sys.name");// "$lns.disk";
		String lnsdiskUserName = props.getProperty("lnsdisk.sys.userName");
		String lnsdiskPw = props.getProperty("lnsdisk.sys.password");
		lnsSysDisk = NetDisk.open(lnsSysclient, lnsDisk, lnsdiskUserName,
				lnsdiskPw);
		lnsSysHome=lnsSysDisk.home();
		
		String lnsDisk2 = props.getProperty("lnsdisk.data.name");// "$data.disk";
		String lnsdiskUserName2 = props.getProperty("lnsdisk.data.userName");
		String lnsdiskPw2 = props.getProperty("lnsdisk.data.password");
		lnsDataDisk = NetDisk.open(lnsDataclient, lnsDisk2, lnsdiskUserName2,
				lnsdiskPw2);
		lnsDataHome=lnsDataDisk.home();
	}
	@Override
	public void initCube(ICube cube){
		String dimsConfigFile= props.getProperty("dimsConfigFile");
		cube.importDims(dimsConfigFile);
	}
	@Override
	public INetDisk getLnsDataDisk() {
		return lnsDataDisk;
	}
	@Override
	public ICube getLnsDataHome() {
		return lnsDataHome;
	}
	@Override
	public ICube getLnsSysHome() {
		return lnsSysHome;
	}

	private static MongoClient client(String mongoAddress,String credential, Properties props) {
		String address = props.getProperty(mongoAddress);
		List<ServerAddress> seeds = new ArrayList<>();
		JsonElement je = new Gson().fromJson(address, JsonElement.class);
		for (JsonElement j : je.getAsJsonArray()) {
			JsonObject jo = j.getAsJsonObject();
			String host = ElementGet.getJsonProp(jo.get("host"));
			String portStr = ElementGet.getJsonProp(jo.get("port"));
			int port = 0;
			if (!StringUtil.isEmpty(portStr)) {
				port = Integer.valueOf(portStr);
			}
			if (port < 1) {
				seeds.add(new ServerAddress(host));
			} else {
				seeds.add(new ServerAddress(host, port));
			}
		}
		String credentialStr = props.getProperty(credential);
		je = new Gson().fromJson(credentialStr, JsonElement.class);
		List<MongoCredential> credentials = new ArrayList<>();
		for (JsonElement j : je.getAsJsonArray()) {
			JsonObject jo = j.getAsJsonObject();
			String user = ElementGet.getJsonProp(jo.get("user"));
			String pwd = ElementGet.getJsonProp(jo.get("pwd"));
			if (StringUtil.isEmpty(user)) {
				continue;
			}
			if (StringUtil.isEmpty(pwd)) {
				continue;
			}
			MongoCredential c=MongoCredential.createCredential(user, "test", pwd.toCharArray());
			credentials.add(c);
		}
		MongoClientOptions options = MongoClientOptions.builder().build();
		return new MongoClient(seeds, credentials, options);
	}
	@Override
	public INetDisk createUserDisk(String owner,String password, DiskInfo info) {
		INetDisk disk= NetDisk.create(userClient, owner, owner, password, info);
		initCube(disk.home());
		return disk;
	}
	@Override
	public boolean existsUserDisk(String user) {
		return NetDisk.existsDisk(userClient, user);
	}
	@Override
	public INetDisk getUserDisk(String user) {
		return NetDisk.trustOpen(userClient, user);
	}
}
