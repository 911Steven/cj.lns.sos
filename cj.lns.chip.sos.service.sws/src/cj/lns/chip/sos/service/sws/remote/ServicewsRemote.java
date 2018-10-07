package cj.lns.chip.sos.service.sws.remote;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.PluginInfo;
import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.portal.PortalInfo;
import cj.lns.chip.sos.service.portal.SceneInfo;
import cj.lns.chip.sos.service.sws.ServicewsBody;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.service.sws.remote.parameter.AssignNewSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.ChangeSwsBackgroundParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.ChangeSwsSceneParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.ChangeSwsThemeParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.FindServicewsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetServicePortalConfigParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetServicewsFaceParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetServicewsSummaryParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetSwsIdParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.IsOwnerParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.SetDefaultAppParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.SwsPortalInfoParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.UserFindInPortalParameters;
import cj.lns.common.sos.service.model.SosPlugin;
import cj.lns.common.sos.service.model.SosPortal;
import cj.lns.common.sos.service.model.SosProperty;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsApp;
import cj.lns.common.sos.service.model.sws.SwsContactGroup;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.model.sws.SwsMenu;
import cj.lns.common.sos.service.model.sws.SwsPortalConf;
import cj.lns.common.sos.service.model.sws.SwsPortlet;
import cj.lns.common.sos.service.model.sws.SwsRole;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

//视窗模板在FrameworkRemote中定义
@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗实例服务。")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/instance", isExoteric = true)
public class ServicewsRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为视窗设置默认应用", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult setDefaultApp(SetDefaultAppParameters parameters)
			throws CircuitException {
		String jpql = "update SwsPortalConf as g set g.defaultAppId=:appId where g.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("appId", parameters.getAppId());
		int i = q.executeUpdate();
		RemoteResult result = null;
		if (i == 1) {
			result = new RemoteResult(200, "成功设置");
		} else {
			result = new RemoteResult(500, "失败,可能视窗不存在");
		}
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗框架配置", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getServicewsSummary(
			GetServicewsSummaryParameters parameters) throws CircuitException {
		String jpql = "select si,u  from SwsInfo si,SosUser u where si.owner=u.userCode and si.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();
			ServicewsSummary sum = new ServicewsSummary();
			Object[] arr = (Object[]) o;
			SwsInfo si = (SwsInfo) arr[0];
			em.refresh(si);
			SosUser u = (SosUser) arr[1];
			em.refresh(u);
			sum.setHeadPic(u.getHead());
			sum.setInheritId(si.getInheritId());
			sum.setNickName(sum.getNickName());
			sum.setFaceImg(si.getFaceImg());
			sum.setPortalId(si.getUsePortal());
			sum.setSwsDesc(si.getDescription());
			sum.setSwsid(si.getId());
			sum.setSwsName(si.getName());
			sum.setLevel(si.getLevel());
			SosUserInfo ui = new SosUserInfo();
			ui.setCreatetime(u.getCreatetime());
			ui.setHead(u.getHead());
			ui.setId(u.getId());
			ui.setNickName(u.getNickName());
			ui.setRealName(u.getRealName());
			ui.setSex(u.getSex());
			ui.setSignatureText(u.getSignatureText());
			ui.setBriefing(u.getBriefing());
			ui.setStatus(u.getStatus());
			ui.setUserCode(u.getUserCode());
			sum.setOwner(ui);
			RemoteResult rr = new RemoteResult(200, "ok");
			rr.content().writeBytes(new Gson().toJson(sum).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗没有配置信息%s", parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗的基础视窗，如果本身就是基础视窗和上级视窗，则返回空", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getbasicSws(GetServicewsSummaryParameters parameters)
			throws CircuitException {
		String jpql = String.format(
				"select a from SwsInfo a, (select si.swsbid swsbid  from SwsInfo si where si.id=%s) b where a.id = b.swsbid",
				parameters.getSwsid());
		Query q = em.createQuery(jpql);
		try {
			Object o = q.getSingleResult();
			RemoteResult rr = new RemoteResult(200, "ok");
			rr.content().writeBytes(new Gson().toJson(o).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗不存在%s", parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗简介及视窗的存储空间信息", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getServicewsBody(
			GetServicewsSummaryParameters parameters) throws CircuitException {
		String jpql = "select si  from SwsInfo si where si.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();
			ServicewsBody body = new ServicewsBody();
			SwsInfo si = (SwsInfo) o;
			body.setInheritId(si.getInheritId());
			body.setPortalId(si.getUsePortal());
			body.setSwsDesc(si.getDescription());
			body.setSwsid(si.getId());
			body.setSwsName(si.getName());
			body.setLevel(si.getLevel());
			body.setFaceImg(si.getFaceImg());
			body.setOwner(si.getOwner());

			// 网盘信息
			IServiceosServiceModule m = ServiceosServiceModule.get();
			IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
			INetDisk disk = db.getUserDisk(si.getOwner());
			if (disk != null && disk.existsCube(si.getId().toString())) {
				ICube cube = disk.cube(si.getId().toString());
				double capacity = cube.config().getCapacity();
				double useSpace = cube.usedSpace();
				double dataSize = cube.dataSize();
				body.setCapacity(capacity);
				body.setUseSpace(useSpace);
				body.setDataSize(dataSize);
			}
			ICube datahome = db.getLnsDataHome();
			String cjql = "select {'tuple':'*'} from tuple sos.servicews java.util.HashMap where {'tuple.swsid':'?(swsid)'}";
			IQuery<Map<String, Object>> qh = datahome.createQuery(cjql);
			qh.setParameter("swsid", si.getId().toString());
			IDocument<Map<String, Object>> doc = qh.getSingleResult();
			if (doc != null) {
				body.setExtra(doc.tuple());
			} else {
				body.getExtra().put("intro", si.getDescription());
			}

			jpql = "select si  from SwsPortalConf si where si.swsId=:swsid";
			q = em.createQuery(jpql);
			q.setParameter("swsid", si.getId());
			SwsPortalConf pc = (SwsPortalConf) q.getSingleResult();
			q.setParameter("swsid", parameters.getSwsid());
			body.getExtra().put("platform", pc.getPlatform());

			RemoteResult rr = new RemoteResult(200, "ok");
			rr.content().writeBytes(new Gson().toJson(body).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗没有配置信息%s", parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取用户的视窗face信息", returnContentType = "text/json", returnUsage = "200成功,json list")
	public RemoteResult getServicewsFace(GetServicewsFaceParameters parameters)
			throws CircuitException {
		String jpql = "select si  from SwsInfo si where si.owner=:owner";
		Query q = em.createQuery(jpql);
		q.setParameter("owner", parameters.getUserCode());
		List<?> list = q.getResultList();
		List<ServicewsInfo> ret = new ArrayList<>();
		for (Object o : list) {
			ServicewsInfo info = new ServicewsInfo();
			SwsInfo si = (SwsInfo) o;
			info.setInheritId(si.getInheritId());
			info.setUsePortal(si.getUsePortal());
			info.setDescription(si.getDescription());
			info.setSwsId(si.getId());
			info.setName(si.getName());
			info.setLevel(si.getLevel());
			info.setFaceImg(si.getFaceImg());
			info.setOwner(si.getOwner());
			ret.add(info);
		}
		RemoteResult rr = new RemoteResult(200, "ok");
		rr.content().writeBytes(new Gson().toJson(ret).getBytes());
		return rr;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "按条件查询视窗列表", returnContentType = "text/json", returnUsage = "200成功")
	public RemoteResult findServicews(FindServicewsParameters parameters)
			throws CircuitException {
		String jpql = "select si  from SwsInfoP si where";
		if (parameters.getLevel() > -1) {
			jpql = String.format("%s si.level=%s and", jpql,
					parameters.getLevel());
		}
		if (parameters.getSwsid() != null) {
			jpql = String.format("%s si.id=%s and", jpql,
					parameters.getSwsid());
		}
		if (parameters.getSwsName() != null) {
			jpql = String.format("%s si.name like '%%%s%%'", jpql,
					parameters.getSwsName());
		}
		if (jpql.endsWith("where")) {
			jpql = jpql.substring(0, jpql.length() - 5);
		}
		if (jpql.endsWith("and")) {
			jpql = jpql.substring(0, jpql.length() - 3);
		}
		Query q = em.createQuery(jpql);
		q.setFirstResult((int) parameters.getSkip());
		q.setMaxResults((int) parameters.getLimit());
		List<?> retList = q.getResultList();

		RemoteResult rr = new RemoteResult(200, "ok");
		rr.content().writeBytes(new Gson().toJson(retList).getBytes());
		return rr;

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗的模板标识", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getServicewstid(
			GetServicewsSummaryParameters parameters) throws CircuitException {
		String jpql = "select si from SwsInfo si where si.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();

			String tid = String.valueOf(((SwsInfo) o).getInheritId());
			RemoteResult result = new RemoteResult(200, "ok");
			result.head("swstid", tid);
			return result;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗不存在:%s", parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗的模板标识", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getServicewsOwner(
			GetServicewsSummaryParameters parameters) throws CircuitException {
		String jpql = "select si from SwsInfo si where si.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();
			RemoteResult result = new RemoteResult(200, "ok");
			result.head("owner", ((SwsInfo) o).getOwner());
			return result;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗不存在:%s", parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取指定视窗相同基础视窗下的指定目标用户的子视窗列表", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getAnotherSwsids(GetSwsIdParameters parameters)
			throws CircuitException {
		// select a from SwsInfo a, (select si.swsbid swsbid from SwsInfo si
		// where si.id=%s) b where a.id = b.swsbid
		// String jpql = "select si from SwsInfo si where
		// si.inheritId=:inheritId and si.owner=:owner";
		String jpql = "select t.id from SwsInfo t, (select a.id,a.owner from SwsInfo a, (select si.swsbid  from SwsInfo si where si.id=:swsid) b where a.id = b.swsbid) basic where t.swsbid=basic.id and t.owner=:owner";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSourceSwsid());
		q.setParameter("owner", parameters.getToUser());
		List<?> list = q.getResultList();
		List<String> ids=new ArrayList<>();
		for(Object o:list){
			BigInteger b=(BigInteger)o;
			ids.add(b.toString());
		}
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(new Gson().toJson(ids).getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗框架配置", returnContentType = "text/json", returnUsage = "200成功,404不存在")
	public RemoteResult getServicePortalConfig(
			GetServicePortalConfigParameters parameters)
			throws CircuitException {
		String jpql = "select pc from SwsPortalConf pc where pc.swsId=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();
			em.refresh(o);
			RemoteResult rr = new RemoteResult(200, "ok");
			rr.content().writeBytes(new Gson().toJson(o).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗没有配置信息%s", parameters.getSwsid()));
			return result;
		}
	}

	public SosUser getUserFace(String user) throws CircuitException {
		String jpql = "select u from SosUser u  where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", user);
		SosUser u = null;
		try {
			u = (SosUser) q.getSingleResult();
			return u;
		} catch (NoResultException e) {
			return null;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "判断用户是否是指定视窗的持有人", returnContentType = "text/json", returnUsage = "404表示否,200存在，内容：视窗信息")
	public RemoteResult isOwner(IsOwnerParameters parameters)
			throws CircuitException {
		String jpql = "select si from SwsInfo si where si.owner=:userCode and si.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getUserCode());
		q.setParameter("swsid", parameters.getSwsid());
		try {
			Object o = q.getSingleResult();
			RemoteResult rr = new RemoteResult(200, "ok");
			rr.content().writeBytes(new Gson().toJson(o).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("用户%s不是视窗%s的持有者", parameters.getUserCode(),
							parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查找用户在指定portal下的视窗", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult findSwsAtPortal(UserFindInPortalParameters parameters)
			throws CircuitException {
		String jpql = "select user from SosUser user where user.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getUserCode());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("用户%s不存在", parameters.getUserCode()));
			return result;
		}
		q = em.createQuery(
				"select p from SosProperty p where p.propName='sos.id'");
		String sosid = "";
		try {
			SosProperty p = (SosProperty) q.getSingleResult();
			sosid = p.getPropValue();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					"当前系统属性缺少服务操作系统标识：sos.id");
			return result;
		}

		jpql = "select si from SwsInfo si where si.owner=:userCode and si.usePortal=:portalId";
		q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getUserCode());
		q.setParameter("portalId", parameters.getPortalId());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("用户%s在框架%s下未有视窗", parameters.getUserCode(),
							parameters.getPortalId()));
			return result;
		}

		jpql = "select pc from SwsPortalConf pc where pc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		SwsPortalConf pc = null;
		try {
			pc = (SwsPortalConf) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗%s配置不存在", si.getId()));
			return result;
		}
		ServicewsInfo info = new ServicewsInfo();
		info.setDescription(si.getDescription());
		info.setInheritId(si.getInheritId());
		info.setName(si.getName());

		info.setSosId(sosid);
		info.setSwsId(si.getId());
		info.setUseCanvas(pc.getUseCanvas());
		info.setUsePortal(pc.getPortalId());
		info.setUseSceneId(pc.getUseSceneId());
		info.setUseTheme(pc.getUseTheme());
		SosUserInfo owner = new SosUserInfo();
		owner.setCreatetime(user.getCreatetime());
		owner.setHead(user.getHead());
		owner.setId(user.getId());
		owner.setNickName(user.getNickName());
		owner.setRealName(user.getRealName());
		owner.setStatus(user.getStatus());
		owner.setUserCode(user.getUserCode());
		info.setOwner(si.getOwner());
		RemoteResult result = new RemoteResult(200, "成功获取");
		result.content().writeBytes(new Gson().toJson(owner).getBytes());
		return result;
	}

	/*
	 * 含视窗模板
	 * <pre>
	 *
	 * </pre>
	 */
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗的框架信息(不包含菜单、栏目、应用、弹出菜单）", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult getSwsPortalInfo(SwsPortalInfoParameters parameters)
			throws CircuitException {
		String jpql = "select si from SwsInfo si where si.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗%s不存在", parameters.getSwsid()));
			return result;
		}
		jpql = "select plug from SosPlugin plug where plug.assemblyGuid=:portalId";
		q = em.createQuery(jpql);
		q.setParameter("portalId", si.getUsePortal());
		SosPlugin plugin = null;
		try {
			plugin = (SosPlugin) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("框架%s 在插件表中不存在", si.getUsePortal()));
			return result;
		}

		jpql = "select p from SosPortal p where p.pluginGuid=:portalId";
		q = em.createQuery(jpql);
		q.setParameter("portalId", si.getUsePortal());
		SosPortal portal = null;
		try {
			portal = (SosPortal) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("框架%s 在框架信息表中不存在", si.getUsePortal()));
			return result;
		}

		PortalInfo pi = new PortalInfo();
		PluginInfo pl = new PluginInfo();
		pl.setAssemblyCompany(plugin.getAssemblyCompany());
		pl.setAssemblyCopyright(plugin.getAssemblyCopyright());
		pl.setAssemblyDescription(plugin.getAssemblyDescription());
		pl.setAssemblyDeveloperHome(plugin.getAssemblyDeveloperHome());
		pl.setAssemblyGuid(plugin.getAssemblyGuid());
		pl.setAssemblyIcon(plugin.getAssemblyIcon());
		pl.setAssemblyIconBytes(plugin.getAssemblyIconBytes());
		pl.setAssemblyProduct(plugin.getAssemblyProduct());
		pl.setAssemblyTitle(plugin.getAssemblyTitle());
		pl.setAssemblyVersion(plugin.getAssemblyVersion());
		pl.setDependencyPlugin(plugin.getDependencyPlugin());
		pi.setInfo(pl);
		pi.setSwsTemplateId(portal.getSwsTemplateId());
		List<SceneInfo> scenes = new Gson().fromJson(portal.getScenes(),
				new TypeToken<List<SceneInfo>>() {
				}.getType());
		pi.getScenes().addAll(scenes);
		Map<String, List<String>> themes = new Gson().fromJson(
				portal.getThemes(), new TypeToken<Map<String, List<String>>>() {
				}.getType());
		pi.getThemes().putAll(themes);

		// 排除以下市场配置
		pi.setApps(null);
		pi.setLets(null);
		pi.setPopups(null);
		pi.setMenus(null);

		RemoteResult result = new RemoteResult(200, "成功读取持有者户默认的的视窗");
		String json = new Gson().toJson(pi);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	// 默认模板在属性表user.register.sws.template
	@CjRemoteMethod(usage = "获取服务操作系统默认模板号,在注册用户时以此作为默认选项的", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult getServiceosDefaultTemplate(
			UserFindInPortalParameters parameters) throws CircuitException {
		RemoteResult result = new RemoteResult(200, "成功读取持有者户默认的的视窗");
		return result;
	}

	/**
	 * 用户可以拥有多个框架下的视窗，但在每个框架下仅有一个视窗
	 * 
	 * <pre>
	 * -因此视窗是一个框架下的窗口，在视窗内不能再改变视窗的归属portal，而只能换场、换画布、换主题等
	 *  －此方法将依据指定框架的视窗模板创建视窗，因此，一个框架会有一个视窗模板
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为用户分配新视窗，即从指定视窗拷贝", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult assignNewSws(AssignNewSwsParameters parameters)
			throws CircuitException {
		String jpql = "select si from SwsInfo si where si.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getInheritId());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(404,
					String.format("视窗模块%s不存在", parameters.getInheritId()));
			return result;
		}
		SwsInfo newsws = new SwsInfo();
		newsws.setId(parameters.getSwsId());
		newsws.setDescription(si.getDescription());
		newsws.setInheritId(si.getId());
		newsws.setName(si.getName());
		newsws.setOwner(parameters.getUserCode());
		newsws.setUsePortal(si.getUsePortal());
		em.persist(newsws);

		BigInteger newSwsId = newsws.getId();

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
		newpc.setBackground(pc.getBackground());
		newpc.setDefaultAppId(pc.getDefaultAppId());
		em.persist(newpc);

		jpql = "select g from SwsContactGroup g where g.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> groups = null;
		groups = q.getResultList();
		for (Object o : groups) {
			SwsContactGroup g = (SwsContactGroup) o;
			SwsContactGroup newg = new SwsContactGroup();
			newg.setGroupName(g.getGroupName());
			newg.setGroupType(g.getGroupType());
			newg.setSwsId(newSwsId);
			em.persist(newg);
		}

		jpql = "select g from SwsRole g where g.swsId=:swsId";
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
			em.persist(newsr);
		}

		jpql = "select g from SwsMenu g where g.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> menus = null;
		menus = q.getResultList();
		for (Object o : menus) {
			SwsMenu sm = (SwsMenu) o;
			SwsMenu newsm = new SwsMenu();
			newsm.setSwsId(newSwsId);
			newsm.setCode(sm.getCode());
			newsm.setCommand(sm.getCommand());
			newsm.setIcon(sm.getIcon());
			newsm.setId(sm.getId());
			newsm.setName(sm.getName());
			newsm.setProvider(sm.getProvider());
			newsm.setTarget(sm.getTarget());
			em.persist(newsm);
		}

		jpql = "select g from SwsApp g where g.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> SwsApp = null;
		SwsApp = q.getResultList();
		for (Object o : SwsApp) {
			SwsApp sm = (SwsApp) o;
			SwsApp newsm = new SwsApp();
			newsm.setSwsId(newSwsId);
			newsm.setCode(sm.getCode());
			newsm.setCommand(sm.getCommand());
			newsm.setIcon(sm.getIcon());
			newsm.setId(sm.getId());
			newsm.setName(sm.getName());
			newsm.setProvider(sm.getProvider());
			newsm.setTarget(sm.getTarget());
			em.persist(newsm);
		}

		jpql = "select g from SwsPortlet g where g.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		List<?> SwsPortlet = null;
		SwsPortlet = q.getResultList();
		for (Object o : SwsPortlet) {
			SwsPortlet sm = (SwsPortlet) o;
			SwsPortlet newsm = new SwsPortlet();
			newsm.setSwsId(newSwsId);
			newsm.setId(sm.getId());
			newsm.setName(sm.getName());
			newsm.setPosition(sm.getPosition());
			newsm.setProvider(sm.getProvider());
			em.persist(newsm);
		}

		RemoteResult result = new RemoteResult(200,
				"成功分配视窗，视窗id在head中：servicewsid=xxx");
		result.head("servicewsid", String.valueOf(newSwsId));
		result.content().writeBytes(new Gson().toJson(newsws).getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为视窗换场景", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult changeSwsScene(ChangeSwsSceneParameters parameters)
			throws CircuitException {
		String jpql = "update  SwsPortalConf g set g.useSceneId=:sceneName,g.useCanvas=canvas where g.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("sceneName", parameters.getSceneName());
		q.setParameter("canvas", parameters.getSceneName());
		int i = q.executeUpdate();
		RemoteResult result = null;
		if (i == 1) {
			result = new RemoteResult(200, "成功设置");
		} else {
			result = new RemoteResult(500, "失败,可能视窗不存在");
		}
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为视窗换主题", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult changeSwsTheme(ChangeSwsThemeParameters parameters)
			throws CircuitException {
		String jpql = "update  SwsPortalConf g set g.useTheme=:theme where g.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("theme", parameters.getThemeName());
		int i = q.executeUpdate();
		RemoteResult result = null;
		if (i == 1) {
			result = new RemoteResult(200, "成功设置");
		} else {
			result = new RemoteResult(500, "失败,可能视窗不存在");
		}
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "为视窗换背景图，或背景色", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult changeSwsBackground(
			ChangeSwsBackgroundParameters parameters) throws CircuitException {
		String jpql = "update SwsPortalConf g set g.background=:background  where g.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("background", parameters.getBackground());
		RemoteResult result = null;
		if (q.executeUpdate() > 0) {
			result = new RemoteResult(200, "成功设置");
		} else {
			result = new RemoteResult(500, "失败,可能视窗不存在");
		}
		return result;
	}

}
