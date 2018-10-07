package cj.lns.chip.sos.service.sws.remote;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.cube.framework.FileInfo;
import cj.lns.chip.sos.cube.framework.FileSystem;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IReader;
import cj.lns.chip.sos.cube.framework.IWriter;
import cj.lns.chip.sos.cube.framework.TooLongException;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.cube.framework.lock.FileLockException;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.remote.parameter.BuildBasicSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.BuildCommonSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.BuildPersonSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.DeleteSwsParameters;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsApp;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsContactGroup;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.model.sws.SwsOwnerProfile;
import cj.lns.common.sos.service.model.sws.SwsPortalConf;
import cj.lns.common.sos.service.model.sws.SwsRole;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗实例服务。")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/build", isExoteric = true)
public class BuildSwsRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "删除视窗", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult deleteSws(DeleteSwsParameters parameters)
			throws CircuitException {
		String jpql = "";
		Query q = null;
		jpql = "delete from SwsApp t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsMenu t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsPortlet t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsContactGroup t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsContact t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsRoleContact t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsRole t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsOwnerProfile t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "delete from SwsPortalConf t where t.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.executeUpdate();

		jpql = "select t from SwsInfo t where t.id=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		try {
			SwsInfo si = (SwsInfo) q.getSingleResult();
			em.remove(si);
			// 删除该视窗的网盘
			IServiceosServiceModule m = ServiceosServiceModule.get();
			IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
			if (db.existsUserDisk(si.getOwner())) {
				INetDisk disk = db.getUserDisk(si.getOwner());
				if (disk.existsCube(si.getId().toString())) {
					disk.deleteCube(si.getId().toString());
				}
			}
			db.getLnsDataHome().deleteDocOne("sos.servicews", String.format("{'tuple.swsid':'%s'}",si.getId().toString()));
		} catch (NoResultException e) {
			return new RemoteResult(404, "视窗不存在");
		}

		return new RemoteResult(200, "ok");

	}

	/**
	 * 用户可以拥有多个框架下的视窗，每个框架下用户也可以拥有多个视窗，但是只能从一个父视窗派生一个
	 * 
	 * <pre>
	 * -因此视窗是一个框架下的窗口，在视窗内不能再改变视窗的归属portal，而只能换场、换画布、换主题等
	 * 
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为用户分配基础视窗，即从超级视窗拷贝", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult buildBasicSws(BuildBasicSwsParameters parameters)
			throws CircuitException {
		if (parameters.getLevel() != 1) {
			throw new CircuitException("503", "申请的视窗类型必须是基础视窗");
		}
		String jpql = "select si from SwsInfo si where si.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getInheritId());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("超级视窗%s不存在", parameters.getInheritId()));
			return result;
		}
		SwsInfo newsws = new SwsInfo();
		newsws.setId(parameters.getAssingedSwsid());
		newsws.setDescription(parameters.getDesc());
		newsws.setInheritId(si.getId());
		newsws.setSwsbid(parameters.getAssingedSwsid());//基础视窗的基础视窗是其自身
		newsws.setName(parameters.getName());
		newsws.setOwner(parameters.getOwner());
		newsws.setUsePortal(si.getUsePortal());
		newsws.setFaceImg(parameters.getFaceImg());
		newsws.setLevel((byte) 1);
		em.persist(newsws);

		BigInteger newSwsId = newsws.getId();

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		if (db.existsUserDisk(newsws.getOwner())) {
			INetDisk disk = db.getUserDisk(newsws.getOwner());
			if (!disk.existsCube(newSwsId.toString())) {
				CubeConfig conf = new CubeConfig(parameters.getCapacity());
				conf.alias(newsws.getName());
				ICube cube = disk.createCube(newSwsId.toString(), conf);
				db.initCube(cube);
				cube.fileSystem().dir("/system/").mkdir("系统");
				cube.fileSystem().dir("/system/faces/").mkdir("视窗图标");
			}
		}
		ICube home = db.getLnsDataHome();
		Map<String, String> tuple = new HashMap<>();
		tuple.put("swsid", newSwsId.toString());
		tuple.put("intro", parameters.getDesc());
		tuple.put("level", "1");
		home.saveDoc("sos.servicews",
				new TupleDocument<Map<String, String>>(tuple));
		
		jpql = "select sp from SwsPortalConf sp where sp.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		SwsPortalConf pc = null;
		try {
			pc = (SwsPortalConf) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗框架无配制：%s", parameters.getInheritId()));
			return result;
		}
		SwsPortalConf newpc = new SwsPortalConf();
		newpc.setPortalId(pc.getPortalId());
		newpc.setSwsId(newSwsId);
		newpc.setUseCanvas(pc.getUseCanvas());
		newpc.setUseSceneId(pc.getUseSceneId());
		newpc.setUseTheme(pc.getUseTheme());
		newpc.setPlatform(parameters.getPlatform());
		newpc.setDefaultAppId(pc.getDefaultAppId());
		newpc.setBackground(pc.getBackground());
		em.persist(newpc);

		SwsOwnerProfile newsop = new SwsOwnerProfile();
		newsop.setSwsId(newSwsId);
		em.persist(newsop);

		jpql = "select g from SwsContactGroup g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> groups = null;
		groups = q.getResultList();
		boolean friends = false;
		boolean swsusers = false;
		boolean open = false;
		boolean blacklist = false;
		for (Object o : groups) {
			SwsContactGroup g = (SwsContactGroup) o;
			if ("friends".equals(g.getGroupType())) {
				friends = true;
			} else if ("swsusers".equals(g.getGroupType())) {
				swsusers = true;
			} else if ("open".equals(g.getGroupType())) {
				open = true;
			} else if ("blacklist".equals(g.getGroupType())) {
				blacklist = true;
			}
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName(g.getGroupName());
			newg.setSort(g.getSort());
			newg.setGroupType(g.getGroupType());
			newg.setSwsId(newSwsId);
			newg.setPropagate(g.getPropagate());
			em.persist(newg);
		}
		if (!friends) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("我的好友");
			newg.setSort(0);
			newg.setGroupType("friends");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!swsusers) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("视窗用户");
			newg.setSort(2000);
			newg.setGroupType("swsusers");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!open) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("公众");
			newg.setSort(2010);
			newg.setGroupType("open");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!blacklist) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("黑名单");
			newg.setSort(2020);
			newg.setGroupType("blacklist");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		// 向父视窗添加一个联系人(当前用户)
		jpql = "select g from SwsContact g where g.swsId=:swsId and g.userCode=:owner";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.setParameter("owner", parameters.getOwner());
		try {
			q.getSingleResult();
		} catch (NoResultException e2) {
			jpql = "select sop from SosUser sop where sop.userCode=:owner";
			q = em.createQuery(jpql);
			q.setParameter("owner", parameters.getOwner());
			SosUser user = (SosUser) q.getSingleResult();
			SwsContact ct = new SwsContact();
			ct.setHeadPic(user.getHead());
			ct.setJoinTime(new Date());
			ct.setUserCode(user.getUserCode());
			ct.setMemoName(user.getUserCode());
			ct.setPersonalSignature(user.getSignatureText());
			ct.setSwsId(si.getId());// 指定为父视窗
			jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
			q = em.createQuery(jpql);
			q.setParameter("swsId", si.getId());
			try {
				SwsContactGroup parentG = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(parentG.getId());// 在父视窗中的swsusers分组物理标识
			} catch (NoResultException e) {// 如果父视窗不存在视窗用户分组，则为之新建一个
				SwsContactGroup newg = new SwsContactGroup();
				newg.setGroupName("视窗用户");
				newg.setSort(2000);
				newg.setGroupType("swsusers");
				newg.setSwsId(si.getId());
				newg.setPropagate((byte) 1);
				em.persist(newg);
				jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
				q = em.createQuery(jpql);
				q.setParameter("swsId", si.getId());
				newg = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(newg.getId());// 在父视窗中的swsusers分组物理标识
			}
			em.persist(ct);
		}
		// 结束向父添加当前联系人

		jpql = "select g from SwsRole g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> roles = null;
		roles = q.getResultList();
		for (Object o : roles) {
			SwsRole sr = (SwsRole) o;
			SwsRole newsr = new SwsRole();
			newsr.setSwsId(newSwsId);
			newsr.setDescription(sr.getDescription());
			newsr.setRoleCode(sr.getRoleCode());
			newsr.setRoleName(sr.getRoleName());
			newsr.setPropagate(sr.getPropagate());
			em.persist(newsr);
		}

		// jpql = "select g from SwsMenu g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> menus = null;
		// menus = q.getResultList();
		// for (Object o : menus) {
		// SwsMenu sm = (SwsMenu) o;
		// SwsMenu newsm = new SwsMenu();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsApp g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsApp = null;
		// SwsApp = q.getResultList();
		// for (Object o : SwsApp) {
		// SwsApp sm = (SwsApp) o;
		// SwsApp newsm = new SwsApp();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setProvider(sm.getProvider());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsPortlet g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsPortlet = null;
		// SwsPortlet = q.getResultList();
		// for (Object o : SwsPortlet) {
		// SwsPortlet sm = (SwsPortlet) o;
		// SwsPortlet newsm = new SwsPortlet();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setPosition(sm.getPosition());
		// newsm.setProvider(sm.getProvider());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setContentUrl(sm.getContentUrl());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setSort(sm.getSort());
		// em.persist(newsm);
		// }
		// '60','appFair','controlPanelApp','应用市场','win.controlpanel','/servicews/market/appSettingPanel.html','./servicews/img/iconfont-security.svg','_self','6666666109','1','control.panel','启用或停用应用'
		SwsApp appFair = new SwsApp();// 至少得安装一个应用市场
		appFair.setCategory("control.panel");
		appFair.setCode("appFair");
		appFair.setCommand("/servicews/market/appSettingPanel.html");
		appFair.setDescription("启用或停用应用");
		appFair.setIcon("./img/kaifangpingtai.svg");
		appFair.setName("应用");
		appFair.setPosition("win.controlpanel");
		appFair.setProvider("controlPanelApp");
		appFair.setSwsId(newSwsId);
		appFair.setTarget("_self");
		em.persist(appFair);
		RemoteResult result = new RemoteResult(200,
				"成功分配视窗，视窗id在head中：servicewsid=xxx");
		result.head("servicewsid", String.valueOf(newSwsId));
		result.content().writeBytes(new Gson().toJson(newsws).getBytes());
		return result;
	}

	/**
	 * 用户可以拥有多个框架下的视窗，每个框架下用户也可以拥有多个视窗，但是只能从一个父视窗派生一个
	 * 
	 * <pre>
	 * -因此视窗是一个框架下的窗口，在视窗内不能再改变视窗的归属portal，而只能换场、换画布、换主题等
	 * 
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为用户分配公共视窗，即从基础视窗拷贝", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult buildCommonSws(BuildCommonSwsParameters parameters)
			throws CircuitException {
		if (parameters.getLevel() != 2) {
			throw new CircuitException("503", "申请的视窗类型必须是公共视窗");
		}

		String jpql = "select si from SwsInfo si where si.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getInheritId());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("基础视窗%s不存在", parameters.getInheritId()));
			return result;
		}
		jpql = "select si from SwsInfo si where si.owner=:owner and si.level=2 and si.inheritId=:inheritId";
		q = em.createQuery(jpql);
		q.setParameter("inheritId", parameters.getInheritId());
		q.setParameter("owner", parameters.getOwner());
		try {
			SwsInfo tmp = (SwsInfo) q.getSingleResult();
			RemoteResult result = new RemoteResult(503,
					String.format("用户:%s 已拥有基础视窗：%s 下的公共视窗:%s", tmp.getOwner(),
							parameters.getInheritId(), tmp.getId().toString()));
			return result;
		} catch (NoResultException e) {

		}

		SwsInfo newsws = new SwsInfo();
		newsws.setId(parameters.getAssingedSwsid());
		String desc = "";
		if (parameters.getIntro().length() > 20) {
			desc = parameters.getIntro().substring(0, 20);
		} else {
			desc = parameters.getIntro();
		}
		newsws.setDescription(desc);
		newsws.setInheritId(si.getId());
		newsws.setSwsbid(si.getId());
		newsws.setName(parameters.getName());
		newsws.setOwner(parameters.getOwner());
		newsws.setUsePortal(si.getUsePortal());
		newsws.setFaceImg(parameters.getFaceImg());
		newsws.setLevel((byte) 2);
		em.persist(newsws);

		BigInteger newSwsId = newsws.getId();

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		if (db.existsUserDisk(newsws.getOwner())) {
			INetDisk disk = db.getUserDisk(newsws.getOwner());
			if (!disk.existsCube(newSwsId.toString())) {
				CubeConfig conf = new CubeConfig(parameters.getCapacity());
				conf.alias(newsws.getName());
				ICube cube = disk.createCube(newSwsId.toString(), conf);
				db.initCube(cube);
				cube.fileSystem().dir("/system/").mkdir("系统");
				cube.fileSystem().dir("/system/faces/").mkdir("视窗图标");
			}
		}
		ICube home = db.getLnsDataHome();
		Map<String, String> tuple = new HashMap<>();
		tuple.put("swsid", newSwsId.toString());
		tuple.put("crop", parameters.getCrop());
		tuple.put("home", parameters.getHome());
		tuple.put("intro", parameters.getIntro());
		tuple.put("address", parameters.getAddress());
		tuple.put("level", "2");
		home.saveDoc("sos.servicews",
				new TupleDocument<Map<String, String>>(tuple));

		jpql = "select sp from SwsPortalConf sp where sp.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		SwsPortalConf pc = null;
		try {
			pc = (SwsPortalConf) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗框架无杨制：%s", parameters.getInheritId()));
			return result;
		}
		SwsPortalConf newpc = new SwsPortalConf();
		newpc.setPortalId(pc.getPortalId());
		newpc.setSwsId(newSwsId);
		newpc.setUseCanvas(pc.getUseCanvas());
		newpc.setUseSceneId(pc.getUseSceneId());
		newpc.setUseTheme(pc.getUseTheme());
		newpc.setPlatform(pc.getPlatform());
		newpc.setDefaultAppId(pc.getDefaultAppId());
		newpc.setBackground(pc.getBackground());
		em.persist(newpc);

		SwsOwnerProfile newsop = new SwsOwnerProfile();
		newsop.setSwsId(newSwsId);
		em.persist(newsop);

		jpql = "select g from SwsContactGroup g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> groups = null;
		groups = q.getResultList();
		boolean friends = false;
		boolean swsusers = false;
		boolean open = false;
		boolean blacklist = false;
		for (Object o : groups) {
			SwsContactGroup g = (SwsContactGroup) o;
			if ("friends".equals(g.getGroupType())) {
				friends = true;
			} else if ("swsusers".equals(g.getGroupType())) {
				swsusers = true;
			} else if ("open".equals(g.getGroupType())) {
				open = true;
			} else if ("blacklist".equals(g.getGroupType())) {
				blacklist = true;
			}
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName(g.getGroupName());
			newg.setSort(g.getSort());
			newg.setGroupType(g.getGroupType());
			newg.setSwsId(newSwsId);
			newg.setPropagate(g.getPropagate());
			em.persist(newg);
		}
		if (!friends) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("我的好友");
			newg.setSort(0);
			newg.setGroupType("friends");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!swsusers) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("视窗用户");
			newg.setSort(2000);
			newg.setGroupType("swsusers");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!open) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("公众");
			newg.setSort(2010);
			newg.setGroupType("open");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!blacklist) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("黑名单");
			newg.setSort(2020);
			newg.setGroupType("blacklist");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		// 向父视窗添加一个联系人(当前用户)
		jpql = "select g from SwsContact g where g.swsId=:swsId and g.userCode=:owner";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.setParameter("owner", parameters.getOwner());
		try {
			q.getSingleResult();
		} catch (NoResultException e2) {
			jpql = "select sop from SosUser sop where sop.userCode=:owner";
			q = em.createQuery(jpql);
			q.setParameter("owner", parameters.getOwner());
			SosUser user = (SosUser) q.getSingleResult();
			SwsContact ct = new SwsContact();
			ct.setHeadPic(user.getHead());
			ct.setJoinTime(new Date());
			ct.setUserCode(user.getUserCode());
			ct.setMemoName(user.getUserCode());
			ct.setPersonalSignature(user.getSignatureText());
			ct.setSwsId(si.getId());// 指定为父视窗
			jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
			q = em.createQuery(jpql);
			q.setParameter("swsId", si.getId());
			try {
				SwsContactGroup parentG = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(parentG.getId());// 在父视窗中的swsusers分组物理标识
			} catch (NoResultException e) {// 如果父视窗不存在视窗用户分组，则为之新建一个
				SwsContactGroup newg = new SwsContactGroup();
				newg.setGroupName("视窗用户");
				newg.setSort(2000);
				newg.setGroupType("swsusers");
				newg.setSwsId(si.getId());
				newg.setPropagate((byte) 1);
				em.persist(newg);
				jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
				q = em.createQuery(jpql);
				q.setParameter("swsId", si.getId());
				newg = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(newg.getId());// 在父视窗中的swsusers分组物理标识
			}
			em.persist(ct);
		}
		// 结束向父添加当前联系人

		jpql = "select g from SwsRole g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> roles = null;
		roles = q.getResultList();
		for (Object o : roles) {
			SwsRole sr = (SwsRole) o;
			SwsRole newsr = new SwsRole();
			newsr.setSwsId(newSwsId);
			newsr.setDescription(sr.getDescription());
			newsr.setRoleCode(sr.getRoleCode());
			newsr.setRoleName(sr.getRoleName());
			newsr.setPropagate(sr.getPropagate());
			em.persist(newsr);
		}

		// jpql = "select g from SwsMenu g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> menus = null;
		// menus = q.getResultList();
		// for (Object o : menus) {
		// SwsMenu sm = (SwsMenu) o;
		// SwsMenu newsm = new SwsMenu();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsApp g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsApp = null;
		// SwsApp = q.getResultList();
		// for (Object o : SwsApp) {
		// SwsApp sm = (SwsApp) o;
		// SwsApp newsm = new SwsApp();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setProvider(sm.getProvider());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsPortlet g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsPortlet = null;
		// SwsPortlet = q.getResultList();
		// for (Object o : SwsPortlet) {
		// SwsPortlet sm = (SwsPortlet) o;
		// SwsPortlet newsm = new SwsPortlet();
		// newsm.setSwsId(newSwsId);
		// newsm.setId(sm.getId());
		// newsm.setCode(sm.getCode());
		// newsm.setName(sm.getName());
		// newsm.setPosition(sm.getPosition());
		// newsm.setProvider(sm.getProvider());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setContentUrl(sm.getContentUrl());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setSort(sm.getSort());
		// em.persist(newsm);
		// }
		// '60','appFair','controlPanelApp','应用市场','win.controlpanel','/servicews/market/appSettingPanel.html','./servicews/img/iconfont-security.svg','_self','6666666109','1','control.panel','启用或停用应用'
		SwsApp appFair = new SwsApp();// 至少得安装一个应用市场
		appFair.setCategory("control.panel");
		appFair.setCode("appFair");
		appFair.setCommand("/servicews/market/appSettingPanel.html");
		appFair.setDescription("启用或停用应用");
		appFair.setIcon("./img/kaifangpingtai.svg");
		appFair.setName("应用");
		appFair.setPosition("win.controlpanel");
		appFair.setProvider("controlPanelApp");
		appFair.setSwsId(newSwsId);
		appFair.setTarget("_self");
		em.persist(appFair);
		RemoteResult result = new RemoteResult(200,
				"成功分配视窗，视窗id在head中：servicewsid=xxx");
		result.head("servicewsid", String.valueOf(newSwsId));
		result.content().writeBytes(new Gson().toJson(newsws).getBytes());
		return result;
	}

	/**
	 * 用户可以拥有多个框架下的视窗，每个框架下用户也可以拥有多个视窗，但是只能从一个父视窗派生一个
	 * 
	 * <pre>
	 * -因此视窗是一个框架下的窗口，在视窗内不能再改变视窗的归属portal，而只能换场、换画布、换主题等
	 * 
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为用户分配个人视窗，即从公共视窗拷贝", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult buildPersonSws(BuildPersonSwsParameters parameters)
			throws CircuitException {
		if (parameters.getLevel() != 3) {
			throw new CircuitException("503", "申请的视窗类型必须是个人视窗");
		}

		String jpql = "select si from SwsInfo si where si.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getInheritId());
		SwsInfo si = null;// 父视窗
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("公共视窗%s不存在", parameters.getInheritId()));
			return result;
		}
		jpql = "select si from SwsInfo si where si.owner=:owner and si.level=3 and si.inheritId=:inheritId";
		q = em.createQuery(jpql);
		q.setParameter("inheritId", parameters.getInheritId());
		q.setParameter("owner", parameters.getOwner());
		try {
			SwsInfo tmp = (SwsInfo) q.getSingleResult();
			RemoteResult result = new RemoteResult(503,
					String.format("用户:%s 已拥有公共视窗：%s 下的个人视窗:%s", tmp.getOwner(),
							parameters.getInheritId(), tmp.getId().toString()));
			return result;
		} catch (NoResultException e) {

		}

		SwsInfo newsws = new SwsInfo();
		newsws.setId(parameters.getAssingedSwsid());
		String desc = "";
		if (parameters.getIntro().length() > 20) {
			desc = parameters.getIntro().substring(0, 20);
		} else {
			desc = parameters.getIntro();
		}
		newsws.setDescription(desc);
		newsws.setInheritId(si.getId());
		newsws.setSwsbid(si.getSwsbid());
		newsws.setName(parameters.getName());
		newsws.setOwner(parameters.getOwner());
		newsws.setUsePortal(si.getUsePortal());
		// 从父视窗获取图标，并将之考被到新视窗中，但有人会问，如果公共视窗换了图标怎么办？方案是推送喽，并消息通知子视窗企业logo有更新
		newsws.setFaceImg(si.getFaceImg());
		newsws.setLevel((byte) 3);// 个人视窗
		em.persist(newsws);

		BigInteger newSwsId = newsws.getId();

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		if (db.existsUserDisk(newsws.getOwner())) {
			INetDisk disk = db.getUserDisk(newsws.getOwner());
			if (!disk.existsCube(newSwsId.toString())) {
				CubeConfig conf = new CubeConfig(parameters.getCapacity());
				conf.alias(newsws.getName());
				ICube cube = disk.createCube(newSwsId.toString(), conf);
				db.initCube(cube);
				FileSystem fs = cube.fileSystem();
				fs.dir("/system/").mkdir("系统");
				fs.dir("/system/faces/").mkdir("视窗图标");
				FileSystem parentfs = db.getUserDisk(si.getOwner())
						.cube(si.getId().toString()).fileSystem();
				String fn = String.format("/system/faces/%s",
						newsws.getFaceImg());
				try {
					FileInfo oldFile = parentfs.openFile(fn);
					FileInfo newFile = fs.openFile(fn);
					IReader r = oldFile.reader(0);
					byte[] b = r.readFully();
					IWriter w = newFile.writer(0);
					w.write(b);
					w.close();
					r.close();
				} catch (FileNotFoundException | FileLockException
						| TooLongException e) {
					throw new CircuitException("503", e);
				}
			}
		}
		ICube home = db.getLnsDataHome();
		Map<String, String> tuple = new HashMap<>();
		tuple.put("swsid", newSwsId.toString());
		tuple.put("level", "3");
		tuple.put("hobby", parameters.getHobby());
		tuple.put("intro", parameters.getIntro());
		home.saveDoc("sos.servicews",
				new TupleDocument<Map<String, String>>(tuple));

		jpql = "select sp from SwsPortalConf sp where sp.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		SwsPortalConf pc = null;
		try {
			pc = (SwsPortalConf) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗框架无杨制：%s", parameters.getInheritId()));
			return result;
		}
		SwsPortalConf newpc = new SwsPortalConf();
		newpc.setPortalId(pc.getPortalId());
		newpc.setSwsId(newSwsId);
		newpc.setUseCanvas(pc.getUseCanvas());
		newpc.setUseSceneId(pc.getUseSceneId());
		newpc.setPlatform(pc.getPlatform());
		newpc.setUseTheme(pc.getUseTheme());
		newpc.setDefaultAppId(pc.getDefaultAppId());
		newpc.setBackground(pc.getBackground());
		em.persist(newpc);

		// 从sosuser表将用户信息考过来

		SwsOwnerProfile newsop = new SwsOwnerProfile();
		newsop.setSwsId(newSwsId);
		em.persist(newsop);

		jpql = "select g from SwsContactGroup g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> groups = null;
		groups = q.getResultList();
		boolean friends = false;
		// boolean swsusers = false;
		boolean open = false;
		boolean blacklist = false;
		for (Object o : groups) {
			SwsContactGroup g = (SwsContactGroup) o;
			if ("swsusers".equals(g.getGroupType())) {// 个人用户没有视窗用户
				continue;
			}
			if ("friends".equals(g.getGroupType())) {
				friends = true;
			} else if ("open".equals(g.getGroupType())) {
				open = true;
			} else if ("blacklist".equals(g.getGroupType())) {
				blacklist = true;
			}
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName(g.getGroupName());
			newg.setSort(g.getSort());
			newg.setGroupType(g.getGroupType());
			newg.setSwsId(newSwsId);
			newg.setPropagate(g.getPropagate());
			em.persist(newg);
		}
		if (!friends) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("我的好友");
			newg.setSort(0);
			newg.setGroupType("friends");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		// if (!swsusers) {// 插入固定分组，个人视窗不需要视窗用户，因为它不会被派生
		// SwsContactGroup newg = new SwsContactGroup();
		// newg.setGroupName("视窗用户");
		// newg.setSort(2000);
		// newg.setGroupType("swsusers");
		// newg.setSwsId(newSwsId);
		// newg.setPropagate((byte)1);
		// em.persist(newg);
		// }
		if (!open) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("公众");
			newg.setSort(2010);
			newg.setGroupType("open");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		if (!blacklist) {// 插入固定分组
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName("黑名单");
			newg.setSort(2020);
			newg.setGroupType("blacklist");
			newg.setSwsId(newSwsId);
			newg.setPropagate((byte) 1);
			em.persist(newg);
		}
		// 向父视窗添加一个联系人(当前用户)
		jpql = "select g from SwsContact g where g.swsId=:swsId and g.userCode=:owner";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.setParameter("owner", parameters.getOwner());
		try {
			q.getSingleResult();
		} catch (NoResultException e2) {
			jpql = "select sop from SosUser sop where sop.userCode=:owner";
			q = em.createQuery(jpql);
			q.setParameter("owner", parameters.getOwner());
			SosUser user = (SosUser) q.getSingleResult();
			SwsContact ct = new SwsContact();
			ct.setHeadPic(user.getHead());
			ct.setJoinTime(new Date());
			ct.setUserCode(user.getUserCode());
			ct.setMemoName(user.getUserCode());
			ct.setPersonalSignature(user.getSignatureText());
			ct.setSwsId(si.getId());// 指定为父视窗
			jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
			q = em.createQuery(jpql);
			q.setParameter("swsId", si.getId());
			try {
				SwsContactGroup parentG = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(parentG.getId());// 在父视窗中的swsusers分组物理标识
			} catch (NoResultException e) {// 如果父视窗不存在视窗用户分组，则为之新建一个
				SwsContactGroup newg = new SwsContactGroup();
				newg.setGroupName("视窗用户");
				newg.setSort(2000);
				newg.setGroupType("swsusers");
				newg.setSwsId(si.getId());
				newg.setPropagate((byte) 1);
				em.persist(newg);
				jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='swsusers'";
				q = em.createQuery(jpql);
				q.setParameter("swsId", si.getId());
				newg = (SwsContactGroup) q.getSingleResult();
				ct.setOwnerGroupId(newg.getId());// 在父视窗中的swsusers分组物理标识
			}
			em.persist(ct);
		}
		// 结束向父添加当前联系人

		jpql = "select g from SwsRole g where g.swsId=:swsId ";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> roles = null;
		roles = q.getResultList();
		for (Object o : roles) {
			SwsRole sr = (SwsRole) o;
			SwsRole newsr = new SwsRole();
			newsr.setSwsId(newSwsId);
			newsr.setDescription(sr.getDescription());
			newsr.setRoleCode(sr.getRoleCode());
			newsr.setRoleName(sr.getRoleName());
			newsr.setPropagate(sr.getPropagate());
			em.persist(newsr);
		}

		// jpql = "select g from SwsMenu g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> menus = null;
		// menus = q.getResultList();
		// for (Object o : menus) {
		// SwsMenu sm = (SwsMenu) o;
		// SwsMenu newsm = new SwsMenu();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsApp g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsApp = null;
		// SwsApp = q.getResultList();
		// for (Object o : SwsApp) {
		// SwsApp sm = (SwsApp) o;
		// SwsApp newsm = new SwsApp();
		// newsm.setSwsId(newSwsId);
		// newsm.setCode(sm.getCode());
		// newsm.setCommand(sm.getCommand());
		// newsm.setIcon(sm.getIcon());
		// newsm.setId(sm.getId());
		// newsm.setName(sm.getName());
		// newsm.setProvider(sm.getProvider());
		// newsm.setTarget(sm.getTarget());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setPosition(sm.getPosition());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setProvider(sm.getProvider());
		// em.persist(newsm);
		// }
		//
		// jpql = "select g from SwsPortlet g where g.swsId=:swsId ";
		// q = em.createQuery(jpql);
		// q.setParameter("swsId", si.getId());
		// List<?> SwsPortlet = null;
		// SwsPortlet = q.getResultList();
		// for (Object o : SwsPortlet) {
		// SwsPortlet sm = (SwsPortlet) o;
		// SwsPortlet newsm = new SwsPortlet();
		// newsm.setSwsId(newSwsId);
		// newsm.setId(sm.getId());
		// newsm.setCode(sm.getCode());
		// newsm.setName(sm.getName());
		// newsm.setPosition(sm.getPosition());
		// newsm.setProvider(sm.getProvider());
		// newsm.setCategory(sm.getCategory());
		// newsm.setDescription(sm.getDescription());
		// newsm.setContentUrl(sm.getContentUrl());
		// newsm.setPropagate(sm.getPropagate());
		// newsm.setSort(sm.getSort());
		// em.persist(newsm);
		// }
		// '60','appFair','controlPanelApp','应用市场','win.controlpanel','/servicews/market/appSettingPanel.html','./servicews/img/iconfont-security.svg','_self','6666666109','1','control.panel','启用或停用应用'
		SwsApp appFair = new SwsApp();// 至少得安装一个应用市场
		appFair.setCategory("control.panel");
		appFair.setCode("appFair");
		appFair.setCommand("/servicews/market/appSettingPanel.html");
		appFair.setDescription("启用或停用应用");
		appFair.setIcon("./img/kaifangpingtai.svg");
		appFair.setName("应用");
		appFair.setPosition("win.controlpanel");
		appFair.setProvider("controlPanelApp");
		appFair.setSwsId(newSwsId);
		appFair.setTarget("_self");
		em.persist(appFair);
		RemoteResult result = new RemoteResult(200,
				"成功分配视窗，视窗id在head中：servicewsid=xxx");
		result.head("servicewsid", String.valueOf(newSwsId));
		result.content().writeBytes(new Gson().toJson(newsws).getBytes());
		return result;
	}
}
