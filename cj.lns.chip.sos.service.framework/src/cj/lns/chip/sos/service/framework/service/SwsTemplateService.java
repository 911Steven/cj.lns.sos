package cj.lns.chip.sos.service.framework.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.disk.DiskInfo;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.remote.IIdGeneratorRemote;
import cj.lns.chip.sos.service.framework.remote.parameter.CreateSuperswsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GenServicewsNumParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GetAllSwsTemplateParameters;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.common.sos.service.model.SosPlugin;
import cj.lns.common.sos.service.model.SosPortal;
import cj.lns.common.sos.service.model.SosRoleUa;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsApp;
import cj.lns.common.sos.service.model.sws.SwsContactGroup;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.model.sws.SwsOwnerProfile;
import cj.lns.common.sos.service.model.sws.SwsPortalConf;
import cj.lns.common.sos.service.model.sws.SwsRole;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "swsTemplateService")
public class SwsTemplateService implements IEntityManagerable, ISwsTemplateService {
	@CjServiceRef(refByName = "/framework/idgen/")
	IIdGeneratorRemote id;
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.ISwsTemplateService#getAllSwsTemplate(cj.lns.chip.sos.service.framework.remote.parameter.GetAllSwsTemplateParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public List<ServicewsInfo> getAllSwsTemplate(
			GetAllSwsTemplateParameters parameters) {
		String jpql = "select si from SwsInfo si where si.inheritId=-1";
		Query q = em.createQuery(jpql);
		List<ServicewsInfo> list = new ArrayList<>();
		List<?> ret = q.getResultList();
		for (Object o : ret) {
			SwsInfo si = (SwsInfo) o;
			ServicewsInfo s = new ServicewsInfo();
			s.setDescription(si.getDescription());
			s.setInheritId(si.getInheritId());
			s.setName(si.getName());
			SosUserInfo user = new SosUserInfo();
			user.setUserCode(si.getOwner());
			s.setOwner(si.getOwner());
			s.setSwsId(si.getId());
			s.setUsePortal(si.getUsePortal());
			list.add(s);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.ISwsTemplateService#getAllSupersws()
	 */
	@Override
	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	public List<SwsInfo> getAllSupersws() {
		String jpql = "select si from SwsInfo si where si.inheritId=-1 and si.level=0";
		Query q = em.createQuery(jpql);
		return q.getResultList();
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.ISwsTemplateService#createSupersws(cj.lns.chip.sos.service.framework.remote.parameter.CreateSuperswsParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public BigInteger createSupersws(CreateSuperswsParameters p)
			throws CircuitException {
		String jpql = "select si from SwsInfo si where si.usePortal=:portalId and si.level=0 and si.inheritId=-1";
		Query q = em.createQuery(jpql);
		q.setParameter("portalId", p.getPortalId());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
			throw new CircuitException("503", String.format("在框架下：%s已存在视窗：%s",
					p.getPortalId(), si.getId()));
		} catch (NoResultException e) {

		}

		jpql = "select si from SosPlugin si where si.assemblyGuid=:assemblyGuid";
		q = em.createQuery(jpql);
		q.setParameter("assemblyGuid", p.getPortalId());
		SosPlugin plugin = null;
		try {
			plugin = (SosPlugin) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404",
					String.format("框架：%s不存在", p.getPortalId()));
		}

		jpql = "select si from SosPortal si where si.pluginGuid=:portalId";
		q = em.createQuery(jpql);
		q.setParameter("portalId", p.getPortalId());
		SosPortal portal = null;
		try {
			portal = (SosPortal) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404",
					String.format("框架缺少场景定义：%s不存在", p.getPortalId()));
		}

		jpql = "select si from SosUser si where si.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", p.getOwner());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404",
					String.format("用户：%s不存在", p.getOwner()));
		}

		SwsInfo newsi = new SwsInfo();
		SwsPortalConf spc = new SwsPortalConf();
		SwsOwnerProfile sop = new SwsOwnerProfile();
		SwsRole allContacts = new SwsRole();
		SwsRole onlySelf = new SwsRole();
		SwsRole mobileContacts = new SwsRole();
		SwsRole publicToMember = new SwsRole();
		SwsRole publicToNet = new SwsRole();

		SwsContactGroup my = new SwsContactGroup();
		SwsContactGroup imports = new SwsContactGroup();
		SwsContactGroup blacklist = new SwsContactGroup();
		SwsContactGroup open = new SwsContactGroup();
		SwsContactGroup swsusers = new SwsContactGroup();

		SwsApp appFair = new SwsApp();// 至少得安装一个应用市场

		newsi.setDescription(p.getDesc());
		newsi.setInheritId(new BigInteger("-1"));
		newsi.setSwsbid(new BigInteger("-1"));
		newsi.setLevel((byte) 0);
		newsi.setName(String.format("%s 超级视窗", plugin.getAssemblyTitle()));
		newsi.setOwner(p.getOwner());
		newsi.setUsePortal(p.getPortalId());
		GenServicewsNumParameters parameters = new GenServicewsNumParameters();
		parameters.setAssignedNum(1000000);
		RemoteResult rr = this.id.genServicewsNum(parameters);
		BigInteger swsid = new BigInteger(rr.head("wholeNum"));
		newsi.setId(swsid);
		em.persist(newsi);
		user.setDefaultSws(swsid);
		em.merge(user);

		spc.setPortalId(p.getPortalId());
		spc.setSwsId(swsid);
		spc.setUseCanvas(p.getCanvas());
		spc.setUseSceneId(p.getScene());
		spc.setUseTheme(p.getTheme());
//		spc.setBackground(background);
		em.persist(spc);

//		sop.setHeadPicUrl(user.getHead());
//		sop.setSignText(user.getSignatureText());
		sop.setSwsId(swsid);
		em.persist(sop);

		allContacts.setRoleCode("allContacts");
		allContacts.setRoleName("全部联系人");
		allContacts.setSwsId(swsid);
		allContacts.setDescription("视窗内所有联系人");
		em.persist(allContacts);

		onlySelf.setRoleCode("onlySelf");
		onlySelf.setRoleName("仅自己");
		onlySelf.setSwsId(swsid);
		onlySelf.setDescription("仅自已拥有此权限");
		em.persist(onlySelf);

//		mobileContacts.setRoleCode("mobileContacts");
//		mobileContacts.setRoleName("手机通讯录联系人");
//		mobileContacts.setSwsId(swsid);
//		mobileContacts.setDescription("手机通讯录联系人");
//		em.persist(mobileContacts);

		publicToMember.setRoleCode("publicToMember");
		publicToMember.setRoleName("开放给平台会员");
		publicToMember.setSwsId(swsid);
		publicToMember.setDescription("平台的会话拥有该权限");
		em.persist(publicToMember);

		publicToNet.setRoleCode("publicToNet");
		publicToNet.setRoleName("完全开放");
		publicToNet.setSwsId(swsid);
		publicToNet.setDescription("开放给公网");
		em.persist(publicToNet);

		my.setGroupName("我的好友");
		my.setGroupType("friends");
		my.setSwsId(swsid);
		my.setSort(-1);
		em.persist(my);

//		imports.setGroupName("手机通讯录");
//		imports.setGroupType("imports");
//		imports.setSwsId(swsid);
//		imports.setSort(100000);
//		em.persist(imports);

		swsusers.setGroupName("视窗用户");
		swsusers.setGroupType("swsusers");
		swsusers.setSwsId(swsid);
		swsusers.setSort(100001);
		em.persist(swsusers);

		open.setGroupName("公众");
		open.setGroupType("open");
		open.setSwsId(swsid);
		open.setSort(100002);
		em.persist(open);

		blacklist.setGroupName("黑名单");
		blacklist.setGroupType("blacklist");
		blacklist.setSwsId(swsid);
		blacklist.setSort(100003);
		em.persist(blacklist);

		jpql = "select si from SosRoleUa si where si.userCode=:userCode and si.roleCode='sosUsers'";
		q = em.createQuery(jpql);
		q.setParameter("userCode", p.getOwner());
		SosRoleUa ua = null;
		try {
			ua = (SosRoleUa) q.getSingleResult();
		} catch (NoResultException e) {
			ua = new SosRoleUa();
			ua.setRoleCode("sosUsers");
			ua.setUserCode(p.getOwner());
			em.persist(ua);
		}

		// '60','appFair','controlPanelApp','应用市场','win.controlpanel','/servicews/market/appSettingPanel.html','./servicews/img/iconfont-security.svg','_self','6666666109','1','control.panel','启用或停用应用'
		appFair.setCategory("control.panel");
		appFair.setCode("appFair");
		appFair.setCommand("/servicews/market/appSettingPanel.html");
		appFair.setDescription("启用或停用应用");
		appFair.setIcon("./img/kaifangpingtai.svg");
		appFair.setName("应用");
		appFair.setPosition("win.controlpanel");
		appFair.setProvider("controlPanelApp");
		appFair.setSwsId(swsid);
		appFair.setTarget("_self");
		em.persist(appFair);
		
		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		INetDisk ud = null;
		if (!db.existsUserDisk(newsi.getOwner())) {
			CubeConfig shared = new CubeConfig(-1);
			String alias = String.format("%s 的主存空间", user.getNickName());
			DiskInfo info = new DiskInfo(alias, shared);
			info.attr("capacity",-1);
			ud = db.createUserDisk(newsi.getOwner(), p.cjtoken(), info);
		} else {
			ud = db.getUserDisk(newsi.getOwner());
		}
		if (!ud.existsCube(swsid.toString())) {
			CubeConfig conf = new CubeConfig(-1);
			conf.alias(newsi.getName());
			conf.setDesc(newsi.getDescription());
			ICube cube=ud.createCube(swsid.toString(), conf);
			db.initCube(cube);
		}

		return swsid;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.ISwsTemplateService#delSupersws(java.lang.String)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public void delSupersws(String portalId) {

		String jpql = "select si from SwsInfo si where si.usePortal=:portalId and si.level=0 and si.inheritId=-1";
		Query q = em.createQuery(jpql);
		q.setParameter("portalId", portalId);
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			return;
		}

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		INetDisk ud = db.getUserDisk(si.getOwner());
		if (ud.existsCube(si.getId().toString())) {
			ud.deleteCube(si.getId().toString());
		}

		jpql = "select si from SosUser si where si.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", si.getOwner());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
			user.setDefaultSws(null);
			em.merge(user);
		} catch (NoResultException e) {

		}

		jpql = "delete from SosRoleUa si where si.userCode=:userCode and si.roleCode='sosUsers'";
		q = em.createQuery(jpql);
		q.setParameter("userCode", si.getOwner());
		q.executeUpdate();

		jpql = "delete from SwsPortalConf sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsOwnerProfile sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsRoleContact sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsContact sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsContactGroup sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsRole sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsMenu sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsApp sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		jpql = "delete from SwsPortlet sc where sc.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("swsId", si.getId());
		q.executeUpdate();

		em.remove(si);
	}
}
