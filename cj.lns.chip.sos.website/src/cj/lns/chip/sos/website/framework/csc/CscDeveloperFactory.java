package cj.lns.chip.sos.website.framework.csc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.UpdateOptions;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;

/**
 * 用于缓冲和查询开发者与服务芯片
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "cscDeveloperFactory")
public class CscDeveloperFactory {

	class MyLRU extends LinkedHashMap<String, CscComputer> {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				java.util.Map.Entry<String, CscComputer> eldest) {
			return size() > 2000;// 当大于2000时删除老数据
		}
	}

	MyLRU cache;

	public CscDeveloperFactory() {
		cache = new MyLRU();
	}

	public CscHost getHost(String id) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		String cubeql = "select {'tuple':'*'} from tuple csc.host ?(clazz) where {}";
		IQuery<CscHost> q = home.createQuery(cubeql);
		q.setParameter("clazz", CscHost.class.getName());
		IDocument<CscHost> doc = q.getSingleResult();
		if (doc == null)
			return null;
		CscHost h = doc.tuple();
		h.id = doc.docid();
		return h;
	}

	public List<CscHost> getHosts() {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		String cubeql = "select {'tuple':'*'} from tuple csc.host ?(clazz) where {}";
		IQuery<CscHost> q = home.createQuery(cubeql);
		q.setParameter("clazz", CscHost.class.getName());
		List<IDocument<CscHost>> docs = q.getResultList();
		List<CscHost> hosts = new ArrayList<>();
		for (IDocument<CscHost> doc : docs) {
			CscHost h = doc.tuple();
			h.id = doc.docid();
			hosts.add(h);
		}
		return hosts;
	}

	public long getComputerCountOnHost(String hostid) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		String cubeql = "select {'tuple':'*'}.count() from tuple csc.computer ?(clazz) where {'tuple.onHost':'?(onHost)'}";
		IQuery<Integer> q = home.createQuery(cubeql);
		q.setParameter("onHost", hostid);
		return q.count();
	}

	public CscComputer getComputer(String name) {
//		if (cache.containsKey(name)) {
//			return cache.get(name);
//		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		String cubeql = "select {'tuple':'*'} from tuple csc.computer ?(clazz) where {'tuple.owner':'?(owner)'}";
		IQuery<CscComputer> q = home.createQuery(cubeql);
		q.setParameter("clazz", CscComputer.class.getName());
		q.setParameter("owner", name);
		IDocument<CscComputer> doc = q.getSingleResult();
		if (doc == null)
			return null;
		CscComputer d = doc.tuple();
		d.id = doc.docid();
//		cache.put(name, d);
		return d;
	}
	public void removeComputer(String name) {
//		if (cache.containsKey(name)) {
//			return cache.get(name);
//		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		String cubeql = "select {'tuple':'*'} from tuple csc.computer ?(clazz) where {'tuple.owner':'?(owner)'}";
		IQuery<CscComputer> q = home.createQuery(cubeql);
		q.setParameter("clazz", CscComputer.class.getName());
		q.setParameter("owner", name);
		IDocument<CscComputer> doc = q.getSingleResult();
		if (doc == null)
			return ;
		CscComputer d = doc.tuple();
		d.id = doc.docid();
		home.deleteDoc("csc.computer", doc);
//		cache.put(name, d);
	}
	public void updateComputer(String oldComputerId, CscComputer pc) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		IDocument<CscComputer> newdoc = new TupleDocument<CscComputer>(pc);
		UpdateOptions op = new UpdateOptions();
		op.upsert(true);
		home.updateDoc("csc.computer", oldComputerId, newdoc, op);
		pc.id = oldComputerId;
//		cache.put(oldComputerId, pc);
	}

	public void updateAssignedPort(String pcid, String pcport) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		ObjectId id = new ObjectId(pcid);
		Bson filter = new BasicDBObject("_id", id);
		Document update=Document.parse(String.format("{'$set':{'tuple.assignedPort':'%s'}}",pcport));
		home.updateDocOne("csc.host",filter,
				update);
	}

	public String createComputer(CscComputer pc) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ICube home = m.site().diskLnsData().home();
		IDocument<CscComputer> newdoc = new TupleDocument<CscComputer>(pc);
		String id = home.saveDoc("csc.computer", newdoc);
//		cache.put(id, pc);
		pc.id = id;
		return id;
	}
}
