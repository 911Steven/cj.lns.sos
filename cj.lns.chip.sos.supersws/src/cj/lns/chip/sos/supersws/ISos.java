package cj.lns.chip.sos.supersws;

import java.util.List;

import org.apache.commons.cli.CommandLine;

import cj.lns.chip.sos.supersws.core.Supersws;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.studio.ecm.IAssembly;

public interface ISos {

	void close();

	void load(CommandLine line);

	void setMain(IAssembly main);

	void setHomeDir(String homeDir);

	String getVersion();

	List<SwsInfo> getSuperswsList();

	Supersws getSupersws(String swsid);

	void deleteSupersws(String portal);

	String createSupersws(String name, String owner, String scene,
			String canvas, String theme, String desc);

	List<SwsInfo> findSwsListByLevel(int level, String skip, String limit);

	void delSws(String name);

	List<SosUser> findUserListByCode(String user, String skip, String limit);

	void delUser(String name);

	Object viewUserByCode(String name);

	void addSosRoleToUser(String name, String optionValue);

	void removeSosRoleToUser(String name, String optionValue);

}
