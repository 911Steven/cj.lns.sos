package cj.lns.chip.sos.service.sws.user;

import java.util.ArrayList;
import java.util.List;

/**
 * 组及联系人集合
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public class GroupContactCollectionInfo extends ContactGroupInfo{
	List<ContactInfo> contacts;
	public GroupContactCollectionInfo() {
		contacts=new ArrayList<ContactInfo>();
	}
	public List<ContactInfo> getContacts() {
		return contacts;
	}
}
