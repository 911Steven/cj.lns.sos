package cj.lns.chip.sos.website.framework;

public interface ISnsReceptionSelector {
	String KEY = "snsReceptionSelector";
	String selectDeviceUrl(int factor);
	String selectImUrl(int factor);
	String selectFsUrl(int factor);
}
