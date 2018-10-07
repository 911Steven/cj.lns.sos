package cj.lns.chip.sos.service.user.remote;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.cube.framework.DirectoryInfo;
import cj.lns.chip.sos.cube.framework.FileInfo;
import cj.lns.chip.sos.cube.framework.FileSystem;
import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IReader;
import cj.lns.chip.sos.cube.framework.IWriter;
import cj.lns.chip.sos.cube.framework.TooLongException;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.cube.framework.lock.FileLockException;
import cj.lns.chip.sos.disk.DiskInfo;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.user.remote.parameter.FindUserParameters;
import cj.lns.chip.sos.service.user.remote.parameter.GetUserFaceParameters;
import cj.lns.chip.sos.service.user.remote.parameter.GetUserParameters;
import cj.lns.chip.sos.service.user.remote.parameter.GetUsersWhereInParameters;
import cj.lns.chip.sos.service.user.remote.parameter.LikeUserParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdateBriefingParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdateHeadPicParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdateNickNameParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdatePwdParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdateSignTextParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UpdateStateParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UserParameters;
import cj.lns.chip.sos.service.user.remote.parameter.UserProfileParameters;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.common.sos.service.model.SosAuth;
import cj.lns.common.sos.service.model.SosRole;
import cj.lns.common.sos.service.model.SosRoleUa;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.security.RSAUtils;
import cj.ultimate.util.StringUtil;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "用户注册、查询服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/public/user/", isExoteric = true)
public class UserRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户的face", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult getUserFaceAndRoles(GetUserFaceParameters p)
			throws CircuitException {
		String jpql = "select u from SosUser u  where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		SosUser u = null;
		try {
			u = (SosUser) q.getSingleResult();
			// 取SOS角色
			jpql = "select r from SosRole r,SosRoleUa u where r.code=u.roleCode and u.userCode=:userCode";
			q = em.createQuery(jpql);
			q.setParameter("userCode", p.getUserCode());
			@SuppressWarnings("unchecked")
			List<SosRole> roles = q.getResultList();
			Face face = new Face(u.getNickName(), u.getHead(),
					u.getSignatureText(), u.getBriefing(), u.getSex());
			Map<String,Object> map=new HashMap<>();
			map.put("face", face);
			map.put("roles", roles);
			RemoteResult r = new RemoteResult(200, "ok");
			r.content().writeBytes(new Gson().toJson(map).getBytes());
			return r;
		} catch (NoResultException e) {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户的face", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult getUserFace(GetUserFaceParameters p)
			throws CircuitException {
		String jpql = "select u from SosUser u  where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		SosUser u = null;
		try {
			u = (SosUser) q.getSingleResult();
			Face face = new Face(u.getNickName(), u.getHead(),
					u.getSignatureText(), u.getBriefing(), u.getSex());
			RemoteResult r = new RemoteResult(200, "ok");
			r.content().writeBytes(new Gson().toJson(face).getBytes());
			return r;
		} catch (NoResultException e) {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户的face", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updateState(UpdateStateParameters p)
			throws CircuitException {
		String jpql = "update SosUser as u set u.status=:state where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("state", p.getState());
		if (q.executeUpdate() > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新个人简介", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updateBriefing(UpdateBriefingParameters p)
			throws CircuitException {
		String jpql = "update SosUser as u set u.briefing=:briefing where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("briefing", p.getBriefing());
		if (q.executeUpdate() > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新签名", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updateSignText(UpdateSignTextParameters p)
			throws CircuitException {
		String jpql = "update SosUser as u set u.signatureText=:signatureText where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("signatureText", p.getSignatureText());
		if (q.executeUpdate() > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新签名", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updateNickName(UpdateNickNameParameters p)
			throws CircuitException {
		String jpql = "update SosUser as u set u.nickName=:nickName where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("nickName", p.getNickName());
		if (q.executeUpdate() > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新密码", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updatePwd(UpdatePwdParameters p)
			throws CircuitException {
		String jpql = "update SosAuth as u set u.data=:pwd where u.authModeCode='password' and u.account =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("pwd", p.getPwd());
		if (q.executeUpdate() > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新签名", returnContentType = "text/json", returnUsage = "404用户不存在")
	public RemoteResult updateHeadPic(UpdateHeadPicParameters p)
			throws CircuitException {
		String jpql = "update SosUser as u set u.head=:head where u.userCode =:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("head", p.getHead());
		int ucount = q.executeUpdate();

		jpql = "update SwsContact as u set u.headPic=:head where u.userCode =:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("head", p.getHead());
		q.executeUpdate();

		// jpql = "select u from SosUser as u where u.userCode =:userCode";
		// q = em.createQuery(jpql);
		// q.setParameter("userCode", p.getUserCode());
		//
		// em.refresh(q.getSingleResult());

		if (ucount > 0) {
			return new RemoteResult(200, "ok");
		} else {
			return new RemoteResult(404, "用户不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户，按in条件", returnContentType = "text/json", returnUsage = "内容：json list")
	public RemoteResult getUsersWhereIn(GetUsersWhereInParameters p)
			throws CircuitException {
		List<String> userCodes = new Gson().fromJson(p.getUserCodes(),
				new TypeToken<ArrayList<String>>() {
				}.getType());
		if (userCodes.isEmpty()) {
			RemoteResult r = new RemoteResult(200, "ok");
			r.content().writeBytes(
					new Gson().toJson(new ArrayList<>()).getBytes());
			return r;
		}
		String jpql = "select u from SosUser u  where u.userCode in :userCodes";
		Query q = em.createQuery(jpql);

		q.setParameter("userCodes", userCodes);
		@SuppressWarnings("unchecked")
		List<SosUser> list = q.getResultList();
		List<SosUserInfo> result = new ArrayList<>();
		for (SosUser u : list) {
			SosUserInfo ui = new SosUserInfo();
			ui.setCreatetime(u.getCreatetime());
			ui.setHead(u.getHead());
			ui.setId(u.getId());
			ui.setSex(u.getSex());
			ui.setNickName(u.getNickName());
			ui.setRealName(u.getRealName());
			ui.setSignatureText(u.getSignatureText());
			ui.setStatus(u.getStatus());
			ui.setUserCode(u.getUserCode());
			result.add(ui);
		}
		RemoteResult r = new RemoteResult(200, "ok");
		r.content().writeBytes(new Gson().toJson(result).getBytes());
		return r;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户", returnContentType = "text/json", returnUsage = "内容：json Object")
	public RemoteResult getUser(GetUserParameters p) throws CircuitException {
		String jpql = "select u from SosUser u  where u.id=:uid";
		Query q = em.createQuery(jpql);
		q.setParameter("uid", p.getUid());
		try {
			SosUser u = (SosUser) q.getSingleResult();
			SosUserInfo ui = new SosUserInfo();
			ui.setCreatetime(u.getCreatetime());
			ui.setHead(u.getHead());
			ui.setId(u.getId());
			ui.setSex(u.getSex());
			ui.setNickName(u.getNickName());
			ui.setRealName(u.getRealName());
			ui.setSignatureText(u.getSignatureText());
			ui.setStatus(u.getStatus());
			ui.setUserCode(u.getUserCode());
			RemoteResult r = new RemoteResult(200, "ok");
			r.content().writeBytes(new Gson().toJson(ui).getBytes());
			return r;
		} catch (NoResultException e) {
			return new RemoteResult(404, "用户不存在：" + p.getUid());
		}
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "按用户代码模糊查询用户", returnContentType = "text/json", returnUsage = "内容：json Array")
	public RemoteResult likeUserNames(LikeUserParameters p)
			throws CircuitException {
		String jpql = "select u.userCode,u.nickName,u.sex,u.head from SosUser u  where u.userCode like :userCodelike";
		Query q = em.createQuery(jpql);
		q.setFirstResult(p.getSkip());
		q.setMaxResults(p.getLimit());
		q.setParameter("userCodelike", p.getLikeUserCode());
		List<?> list=q.getResultList();
		RemoteResult r = new RemoteResult(200, "ok");
		String json = new Gson().toJson(list);
		r.content().writeBytes(json.getBytes());
		return r;
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "按条件查询用户", returnContentType = "text/json", returnUsage = "内容：json Array")
	public RemoteResult findUserList(FindUserParameters p)
			throws CircuitException {
		String jpql = "select u from SosUser u  ";
		boolean hasUsercode = false;
		if (!StringUtil.isEmpty(p.getUserCode())) {
			jpql = String.format("%s where u.userCode=:userCode ", jpql);
			hasUsercode = true;
		}
		// if(!StringUtil.isEmpty(p.getOnline())){
		// jpql=String.format(" %s where u.userCode=:userCode ", jpql);
		// }
		if (p.getSex() == 0 || p.getSex() == 1) {
			if (hasUsercode)
				jpql = String.format(" %s and u.sex=:sex ", jpql);
			else
				jpql = String.format(" %s where u.sex=:sex ", jpql);
		}
		Query q = em.createQuery(jpql);
		if (hasUsercode)
			q.setParameter("userCode", p.getUserCode());
		if (p.getSex() == 0 || p.getSex() == 1) {
			q.setParameter("sex", p.getSex());
		}
		if (!hasUsercode) {
			q.setMaxResults(p.getMax());
			q.setFirstResult(p.getStart());
		}
		List<?> list = null;
		list = q.getResultList();
		List<SosUserInfo> ret = new ArrayList<>();
		for (Object o : list) {
			SosUser u = (SosUser) o;
			SosUserInfo ui = new SosUserInfo();
			ui.setCreatetime(u.getCreatetime());
			ui.setHead(u.getHead());
			ui.setId(u.getId());
			ui.setSex(u.getSex());
			ui.setNickName(u.getNickName());
			ui.setRealName(u.getRealName());
			ui.setSignatureText(u.getSignatureText());
			ui.setStatus(u.getStatus());
			ui.setUserCode(u.getUserCode());
			ret.add(ui);
		}
		RemoteResult r = new RemoteResult(200, "ok");
		String json = new Gson().toJson(ret);
		r.content().writeBytes(json.getBytes());
		return r;
	}

	private void genToolsKey(String userCode) throws CircuitException {
		String publicKey = "";
		String privateKey = "";
		try {
			Map<String, Object> keyMap = RSAUtils.genKeyPair();
			publicKey = RSAUtils.getPublicKey(keyMap);
			privateKey = RSAUtils.getPrivateKey(keyMap);
		} catch (Exception e) {
			throw new CircuitException("304", "生成key失败:" + e);
		}
		HashMap<String, String> maptools = new HashMap<>();
		maptools.put("user", userCode);
		maptools.put("privateKey", privateKey);
		maptools.put("publicKey", publicKey);
		TupleDocument<HashMap<String, String>> tuple = new TupleDocument<HashMap<String, String>>(
				maptools);
		IDatabaseCloud db = (IDatabaseCloud) ServiceosServiceModule.get().site()
				.databaseCloud();
		db.getLnsSysHome().saveDoc("userKeyTools", tuple);
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "注册用户。快速简单注册。", returnContentType = "text/json", returnUsage = "返回状态:200成功,否则报错")
	public RemoteResult register(UserParameters parameters)
			throws CircuitException {
		// 保存密码、按默认视窗实例化视窗实例
		SosUser user = new SosUser();
		user.setCreatetime(new Date());
		user.setUserCode(parameters.getUserCode());
		user.setNickName(parameters.getNickName());
		user.setStatus((byte) 0);
		user.setHead(parameters.getFaceImg());
		switch (parameters.getSex()) {
		case "male":
			user.setSex((byte) 0);
			break;
		case "female":
			user.setSex((byte) 1);
			break;
		}
		em.persist(user);
		SosAuth auth = new SosAuth();
		auth.setAccount(user.getUserCode());
		auth.setAuthModeCode("password");
		auth.setData(parameters.getPassword());
		auth.setMask("0");
		em.persist(auth);
		SosRoleUa ua = new SosRoleUa();
		ua.setRoleCode("swsUsers");// 注册时默认为视窗用户组
		ua.setUserCode(user.getUserCode());
		em.persist(ua);

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		FileSystem fs = null;
		if (!db.existsUserDisk(parameters.getUserCode())) {
			CubeConfig shared = new CubeConfig(parameters.getHomeSize());
			String alias = String.format("%s 的主存空间",
					(StringUtil.isEmpty(user.getNickName()) ? user.getUserCode()
							: user.getNickName()));
			DiskInfo info = new DiskInfo(alias, shared);
			info.attr("capacity", parameters.getCapacity());
			INetDisk disk = db.createUserDisk(parameters.getUserCode(),
					parameters.getPassword(), info);
			fs = disk.home().fileSystem();
			DirectoryInfo systemDir = fs.dir("/system/");
			if (!systemDir.exists()) {
				systemDir.mkdir("系统");
			}
			fs.dir("/system/img/faces").mkdir("我的头像");
			fs.dir("/system/img/backgounds").mkdir("我的背景");
		} else {
			INetDisk disk = db.getUserDisk(parameters.getUserCode());
			fs = disk.home().fileSystem();
			DirectoryInfo systemDir = fs.dir("/system/");
			if (!systemDir.exists()) {
				systemDir.mkdir("系统");
			}
			if (!fs.dir("/system/img/faces").exists()) {
				fs.dir("/system/img/faces").mkdir("我的头像");
			}
			if (!fs.dir("/system/img/backgounds").exists()) {
				fs.dir("/system/img/backgounds").mkdir("我的背景");
			}
		}
		ICube faces = db.getLnsDataDisk().cube("faces");
		try {
			FileInfo fi = faces.fileSystem()
					.openFile(String.format("/%s", parameters.getFaceImg()));
			IReader reader = fi.reader(0);
			FileInfo my = fs.openFile(String.format("/system/img/faces/%s",
					parameters.getFaceImg()));
			IWriter writer = my.writer(0);
			byte[] b = reader.readFully();
			writer.write(b);
			writer.close();
			reader.close();
		} catch (FileNotFoundException | FileLockException
				| TooLongException e) {
			throw new CircuitException("503", e);
		}

		genToolsKey(user.getUserCode());
		RemoteResult result = new RemoteResult(200, "注册成功");
		return result;
	}

	protected BigInteger assignServicews(String cjtoken, String userCode,
			BigInteger inheritId) throws CircuitException {
		String req = "assignNewSws /sws/instance/ sos/1.0";// -Pcjtoken=sss
															// -PuserCode=wuj
															// -PinheritId=1";
		Frame frame = new Frame(req);
		frame.parameter("cjtoken", cjtoken);
		frame.parameter("userCode", userCode);
		frame.parameter("inheritId", String.valueOf(inheritId));

		Circuit circuit = new Circuit("sos/1.0 200 OK");
		ServiceosServiceModule.get().site().out().flow(frame, circuit);
		String newswsid = circuit.head("servicewsid");
		return StringUtil.isEmpty(newswsid) ? BigInteger.valueOf(-1L)
				: new BigInteger(newswsid);
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "登记用户明细资料（未实现）", returnContentType = "text/json", returnUsage = "返回状态:200成功,否则报错")
	public RemoteResult registerProfile(UserProfileParameters parameters)
			throws CircuitException {

		return null;
	}

}
