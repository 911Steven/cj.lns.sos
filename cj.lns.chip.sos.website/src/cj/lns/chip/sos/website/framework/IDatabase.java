package cj.lns.chip.sos.website.framework;

import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.plugins.moduleable.IModuleContainer;

public interface IDatabase {
	void init(IPin dbPin, IModuleContainer container);
}
