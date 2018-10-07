package cj.lns.chip.sos.service.db;

import java.util.HashMap;
import java.util.Map;

public class DbConfig {
	Map<String, String> map;
	public DbConfig() {
		map=new HashMap<>();
	}
	public Map<String, String> getMap() {
		return map;
	}
}
