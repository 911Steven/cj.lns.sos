package cj.lns.chip.sos.website;

import cj.lns.chip.sos.website.framework.FrameworkEntrypointFilter;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.net.web.IHttpFilter;
@CjService(name="serviceosEntrypointFilter")
public class ServiceosEntrypointFilter extends FrameworkEntrypointFilter implements IHttpFilter{

	@Override
	public int sort() {
		return 0;
	}


}
