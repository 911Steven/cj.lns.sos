package cj.lns.chip.sos.service.portal;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景信息，虽然是一个场景多个终端子场，但在结构上并不定义，而是直列，留到在使用该结构是解析
 * <pre>
 * ·一个场景有且仅有一个画布
 * ·主题在一个portal内的所有同类终端的场景中共享，即所有同类终端画布共享主题。
 * 
 * 因为：eclipse中透视图不仅是插件集的切换，也是布局的切换，而它们都可在同一主题下。
 * </pre>
 * @author carocean
 *
 */
public class SceneInfo {
	private String sceneTitle;
	private String sceneName;
	String canvasName;//画布名为画布文件名，一般情况下与场景全名对应(含终端类型的名称）
	private String rootRegion;
	private String sceneDesc;
	private List<Region> regions;
	
	public SceneInfo() {
		regions=new ArrayList<Region>();
	}
	public String getCanvasName() {
		return canvasName;
	}
	public void setCanvasName(String canvasName) {
		this.canvasName = canvasName;
	}
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}


	public String getRootRegion() {
		return rootRegion;
	}

	public void setRootRegion(String rootRegion) {
		this.rootRegion = rootRegion;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public String getSceneDesc() {
		return sceneDesc;
	}

	public void setSceneDesc(String sceneDesc) {
		this.sceneDesc = sceneDesc;
	}

	public String getSceneTitle() {
		return sceneTitle;
	}

	public void setSceneTitle(String sceneTitle) {
		this.sceneTitle = sceneTitle;
	}
	

}
