package cj.lns.chip.sos.service.sws.remote;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.remote.parameter.ContactGroupParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.DeleteContactGroupParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.FindAllGroupsAtSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetContactsByGroupParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetGroupByContactParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.MoveContactToGroupParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.RenameGroupParameters;
import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.service.sws.user.GroupContactCollectionInfo;
import cj.lns.chip.sos.sws.security.ISecuritySubject;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsContactGroup;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
/*
 * 视窗系统的联系人的关注关系：following表示关注了，follower表示被关注或粉丝
 * follow表示这种关注关系，1为关注；-1为粉丝；0表示双向关注，语句：
 * 定义：
 * 每一行表示视窗主人关注了该联系人，因此一个主人的联系人列表是它关注的集合并上它的粉丝集合
 * 设用户A，其有联系人B在其关注集合COL1内，则表示A关注了B，同样，如果A在B的关注集合COL2内，则表示B关注了A，如果二者互在则表示双向关注。因此：
 * 界面上A用户的联系人列表应是个follow全表，即包含COL1+COL2。
 * 下面是求zhaoxb的联系人列表算法（未考虑联系人分组），双向关注以分组求和即可。
 * (全字段）
 * SELECT 
    t.id,
    t.swsId,
    t.userCode,
    t.memoName,
    SUM(t.follow) 'follow',
    t.ownerGroupId,
    t.joinTime,
    t.headPic,
    t.personalSignature
FROM
    ((SELECT 
        sc.*, 1 'follow'
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId AND si.id = 1
            AND si.owner = 'zhaoxb') UNION (SELECT 
        sc.*, - 1 'follow'
    FROM
        sws_contact sc, (SELECT 
        si.owner
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId
            AND sc.userCode = 'zhaoxb') AS own
    WHERE
        own.owner = sc.userCode AND sc.swsId = 1)) AS t
GROUP BY t.userCode;

----下面是联系人列表的简单求法：只返回用户标识之间的关注关系。
SELECT 
    t.userCode,sum(t.follow) 'follow'
FROM
    ((SELECT 
        sc.userCode, 1 'follow'
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId AND si.owner = 'zhaoxb') UNION (SELECT 
        si.owner, - 1 'follow'
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId
            AND sc.userCode = 'zhaoxb')) AS t group by t.userCode;
            
            
            
            -------
    下面是求zhaoxb与zhaoh之间的关注关系，zhaoxb作为主条件，以判断与zhaoh的关系。(只返回用户标识之间的关注关系。)
SELECT 
    t.userCode, SUM(t.follow) 'follow'
FROM
    ((SELECT 
        sc.userCode, 1 'follow'
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId AND si.owner = 'zhaoxb' and si.id=1
            AND sc.userCode = 'zhaoh') UNION (SELECT 
        si.owner, - 1 'follow'
    FROM
        sws_contact sc, sws_info si
    WHERE
        si.id = sc.swsId AND si.owner = 'zhaoh'
            AND sc.userCode = 'zhaoxb')) AS t
GROUP BY t.userCode;
 */
//视窗模板在FrameworkRemote中定义
@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗联系人分组。固定分组：我的好友，公众，黑名单")
@CjService(name = "/sws/userGroup/", isExoteric = true)
@CjBridge(aspects = "logging+transaction")
public class ServicewsUserGroupRemote
		implements IRemoteService, IEntityManagerable, ISecuritySubject {
	EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "返回用户归属组", returnContentType = "text/json", returnUsage = "内容：json SwsContactGroup;代码：200 ok,404:如果未加入视窗")
	public RemoteResult getGroupByContact(
			GetGroupByContactParameters parameters) throws CircuitException {
		String jpql = "select g from SwsContactGroup g,SwsContact c,SosUser u where g.id=c.ownerGroupId "
				+ " and u.userCode=c.userCode "
				+ " and g.id=c.ownerGroupId "
				+ " and g.swsId=:swsId "
				+ " and u.id=:uid";
		Query q = em.createQuery(jpql);
		q.setParameter("uid", parameters.getUid());
		q.setParameter("swsId", parameters.getSwsid());
		try{
			SwsContactGroup g=(SwsContactGroup)q.getSingleResult();
			RemoteResult rr=new RemoteResult(200,"ok");
			rr.content().writeBytes(new Gson().toJson(g).getBytes());
			return rr;
		}catch(NoResultException e){
			return new RemoteResult(404,"用户还未加入视窗");
		}
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "返回视窗下所有用户的简单信息", returnContentType = "text/json", returnUsage = "内容：json list;代码：200 ok,other error")
	public RemoteResult getContactsByGroup(
			GetContactsByGroupParameters parameters) throws CircuitException {
		String jpql = "select c from SwsContactGroup g,SwsContact c where g.id=c.ownerGroupId "
				+ " and g.id=:groupId and g.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("groupId", parameters.getGroupId());
		q.setParameter("swsId", parameters.getSwsid());
		List<?> contacts = q.getResultList();
		List<ContactInfo> client = new ArrayList<>();
		for (Object o : contacts) {
			SwsContact c = (SwsContact) o;
			ContactInfo ci = new ContactInfo();
			ci.setId(c.getId());
			ci.setMemoName(c.getMemoName());
			ci.setOwnerGroupId(parameters.getGroupId());
			ci.setHeadPic(c.getHeadPic());
			ci.setPersonalSignature(c.getPersonalSignature());
			ci.setUserCode(c.getUserCode());
			client.add(ci);
		}
		String json = new Gson().toJson(client);
		RemoteResult result = new RemoteResult(200, "成功获取所有分组及联系人");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "返回视窗下所有用户，以联系人组为分类", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult getServicewsUserNames(
			FindAllGroupsAtSwsParameters parameters) throws CircuitException {
		String jpql = String.format(
				"select c.userCode from Sws_Contact c where  c.swsId=%s ",
				parameters.getSwsid());
		Query q = em.createNativeQuery(jpql);
		List<?> users = q.getResultList();
		String json = new Gson().toJson(users);
		RemoteResult result = new RemoteResult(200, "成功获取所有分组及联系人");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "返回视窗下所有用户，以联系人组为分类", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult getServicewsUsers(
			FindAllGroupsAtSwsParameters parameters) throws CircuitException {
		String jpql = String.format(
				"select g.id gid,g.groupName,g.groupType,c.id cid,c.memoName,g.swsId,c.headPic,c.personalSignature,c.userCode from Sws_ContactGroup g left join Sws_Contact c on g.id=c.ownerGroupId where  g.swsId=%s ",
				parameters.getSwsid());
		Query q = em.createNativeQuery(jpql);
		List<?> enGroups = q.getResultList();

		Map<BigInteger, GroupContactCollectionInfo> groups = new HashMap<BigInteger, GroupContactCollectionInfo>();
		for (Object o : enGroups) {
			Object[] group = (Object[]) o;
			BigInteger gid = BigInteger.valueOf((long) group[0]);
			BigInteger swsid = BigInteger.valueOf((long) group[5]);
			String gname = (String) group[1];
			String gtype = (String) group[2];

			GroupContactCollectionInfo gi = groups.get(gid);
			if (gi == null) {
				gi = new GroupContactCollectionInfo();
				gi.setGroupName(gname);
				gi.setGroupType(gtype);
				gi.setId(gid);
				gi.setSwsId(swsid);
				groups.put(gid, gi);
			}
			// 联系人可能为空，因为可能存在空组
			BigInteger cid = group[3] == null ? BigInteger.valueOf(-1)
					: BigInteger.valueOf((long) group[3]);
			if (cid.compareTo(BigInteger.valueOf(-1)) != 0) {
				String memoName = group[4] == null ? "" : (String) group[4];
				String headPic = group[6] == null ? "" : (String) group[6];
				String personalSign = group[7] == null ? "" : (String) group[7];
				String userCode = group[8] == null ? "" : (String) group[8];
				ContactInfo ci = new ContactInfo();
				ci.setId(cid);
				ci.setMemoName(memoName);
				ci.setOwnerGroupId(gid);
				ci.setHeadPic(headPic);
				ci.setPersonalSignature(personalSign);
				ci.setUserCode(userCode);
				gi.getContacts().add(ci);
			}
		}
		String json = new Gson().toJson(groups.values());
		RemoteResult result = new RemoteResult(200, "成功获取所有分组及联系人");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "返回视窗下的所有联系人分组", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult getContactGroups(
			FindAllGroupsAtSwsParameters parameters) throws CircuitException {
		String jpql = "select g,count(c.id) as cid from SwsContactGroup g left join SwsContact c on g.id=c.ownerGroupId where g.swsId=:swsId  group by g.id ORDER BY g.sort";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		List<?> list = q.getResultList();
		RemoteResult result = new RemoteResult(200, "成功获取所有联系人分组");
		List<ContactGroupInfo> groups = new ArrayList<ContactGroupInfo>();
		for (Object o : list) {
			Object[] arr = (Object[]) o;

			SwsContactGroup group = (SwsContactGroup) arr[0];
			Long contactCount = (Long) arr[1];
			ContactGroupInfo info = new ContactGroupInfo();
			info.setUserCount(contactCount);
			info.setGroupName(group.getGroupName());
			info.setGroupType(group.getGroupType());
			info.setId(group.getId());
			info.setSwsId(group.getSwsId());
			groups.add(info);
		}
		String json = new Gson().toJson(groups);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "添加联系人分组", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult addContactGroup(ContactGroupParameters parameters)
			throws CircuitException {
		String jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupName=:groupName";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", Long.valueOf(parameters.getSwsid()));
		q.setParameter("groupName", parameters.getGroupName());
		if (!q.getResultList().isEmpty()) {
			throw new CircuitException("504", String.format("视窗%s中已存在分组：%s",
					parameters.getSwsid(), parameters.getGroupName()));
		}
		SwsContactGroup group = new SwsContactGroup();
		group.setGroupName(parameters.getGroupName());
		group.setSwsId(new BigInteger(parameters.getSwsid()));
		group.setGroupType("custom");// 用户只能添加自定义组,其它系统组在新建视窗时创建
		em.persist(group);
		RemoteResult result = new RemoteResult(200, "成功添加联系人分组");
		return result;
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "重命名联系人分组", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult renameContactGroup(RenameGroupParameters parameters)
			throws CircuitException {
		String jpql = "select g from SwsContactGroup g where g.groupName=:groupName and g.swsId=:swsId and g.id=:id";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", Long.valueOf(parameters.getSwsid()));
		q.setParameter("id", Long.valueOf(parameters.getGid()));
		q.setParameter("groupName", parameters.getGroupName());
		try {
			q.getSingleResult();
			RemoteResult result = new RemoteResult(202,
					String.format("联系人组已存在。groupName:%s,gid:%s",
							parameters.getGroupName(), parameters.getGid()));
			return result;
		} catch (NoResultException e) {
			jpql = "update SwsContactGroup g set g.groupName=:groupName where g.swsId=:swsId and g.id=:id";
			q = em.createQuery(jpql);
			q.setParameter("swsId", Long.valueOf(parameters.getSwsid()));
			q.setParameter("id", Long.valueOf(parameters.getGid()));
			q.setParameter("groupName", parameters.getGroupName());
			int i = q.executeUpdate();
			RemoteResult result = new RemoteResult(200,
					String.format("成功重命名联系人分组，更新行数:%s", i));
			return result;
		}

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "转移联系人到指定分组", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult moveContactToGroup(
			MoveContactToGroupParameters parameters) throws CircuitException {
		String jpql = "select g from SwsContactGroup g where  g.swsId=:swsId and g.id=:id";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("id", parameters.getGroupId());
		try {
			q.getSingleResult();
			jpql = "update SwsContact g set g.ownerGroupId=:ownerGroupId where g.swsId=:swsId and g.userCode=:userCode";
			q = em.createQuery(jpql);
			q.setParameter("swsId", parameters.getSwsid());
			q.setParameter("userCode", parameters.getUserCode());
			q.setParameter("ownerGroupId", parameters.getGroupId());
			int i = q.executeUpdate();
			RemoteResult result = new RemoteResult(200,
					String.format("成功转移联系人到指定分组，更新行数:%s", i));
			return result;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(202,
					String.format("分组不存在。gid:%s", parameters.getGroupId(),
							parameters.getGroupId()));
			return result;
		}

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "删除联系人分组(仅能删除自定义分组)，其下所有联系人将移到默认分组，公众分组中", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult deleteContactGroup(DeleteContactGroupParameters p)
			throws CircuitException {
		String jpql = "select c from SwsContact c where c.ownerGroupId=:gid and c.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("gid", p.getGid());
		q.setParameter("swsId", p.getSwsid());
		List<?> contacts = q.getResultList();
		//查询公众分组标识
		jpql = "select g from  SwsContactGroup g where g.swsId=:swsId and g.groupType='open'";
		 q = em.createQuery(jpql);
		q.setParameter("swsId", p.getSwsid());
		SwsContactGroup open=null;
		try{
			open=(SwsContactGroup)q.getSingleResult();
		}catch(NoResultException e){
			return new RemoteResult(202,"未有公众分组");
		}
		jpql = "select g from SwsContactGroup g where g.id=:gid and g.swsId=:swsId";
		q = em.createQuery(jpql);
		q.setParameter("gid", p.getGid());
		q.setParameter("swsId", p.getSwsid());
		SwsContactGroup g=(SwsContactGroup)q.getSingleResult();
		if(!"custom".equals(g.getGroupType())){
			return new RemoteResult(503,"不能删除固定分组");
		}
		
		jpql = "delete from SwsContactGroup g where g.id=:gid and g.swsId=:swsId and g.groupType='custom'";
		q = em.createQuery(jpql);
		q.setParameter("gid", p.getGid());
		q.setParameter("swsId", p.getSwsid());
		if (q.executeUpdate() > 0 && !contacts.isEmpty()) {// 转移联系人
			for (Object o : contacts) {
				SwsContact c = (SwsContact) o;
				c.setOwnerGroupId(open.getId());
				em.merge(c);
			}
		}
		RemoteResult result = new RemoteResult(200, "成功读取持有者用户的视窗");
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "添加联系人到黑名单", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult addContactBlackList(
			FindAllGroupsAtSwsParameters parameters) throws CircuitException {
		RemoteResult result = new RemoteResult(200, "成功读取持有者用户的视窗");
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "从黑名单中移除指定联系人", returnContentType = "text/json", returnUsage = "返回所有联系人分组")
	public RemoteResult removeContactBlackList(
			FindAllGroupsAtSwsParameters parameters) throws CircuitException {
		RemoteResult result = new RemoteResult(200, "成功读取持有者用户的视窗");
		return result;
	}

	@Override
	public String subjectName() {
		return "contactGroup";
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean containsUser(BigInteger swsId, String groupId,
			String userCode) {
		String jpql = "select u from SwsContactGroup g,SosUser u where g.swsId=:swsId and g.id=:groupId and u.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("groupId", new BigInteger(groupId));
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
}
