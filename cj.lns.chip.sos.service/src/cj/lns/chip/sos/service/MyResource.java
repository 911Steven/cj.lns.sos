package cj.lns.chip.sos.service;

import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.pipeline.ExotericalTypePipeline;
import cj.studio.ecm.resource.SystemResource;
@CjService(name="myResource")
public class MyResource extends SystemResource {
/*
 * 本欲为eclipselink到各插件加载模型，但jpa不是在加载时依赖就不启用类加载，
 * 也就是在这种情况下，在其类加载器本身都没打印出搜索。
 */
	public MyResource() {
		ExotericalTypePipeline pipe=new ExotericalTypePipeline(this);
		setPipeline(pipe);
	}
	
	public MyResource(ClassLoader parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected Class<?> loadClass(String sClassName,
			boolean bResolve) throws ClassNotFoundException {
		return super.loadClass(sClassName, bResolve);
	}
	
}
