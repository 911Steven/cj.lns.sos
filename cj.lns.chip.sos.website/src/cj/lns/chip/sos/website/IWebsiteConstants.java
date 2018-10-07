package cj.lns.chip.sos.website;

public interface IWebsiteConstants {
	int FIXED_APP_HOME=-1;
	int FIXED_APP_CYBERPORT=-5;
	int FIXED_APP_COMPUTER_LEVEL2=-6;
	int FIXED_APP_COMPUTER_LEVEL1=-7;
	int FIXED_APP_CONTROLPANEL=-10;
	/**
	 * 静态资源大于该值将视为溢出，溢出的大资源则以chunked方式晌应，默认256k
	 */
	int STATIC_RESOURCE_OVERFLOW_VALUE=262144;
}
