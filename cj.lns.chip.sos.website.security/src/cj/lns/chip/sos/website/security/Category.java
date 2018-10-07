package cj.lns.chip.sos.website.security;

import java.util.ArrayList;
import java.util.List;

public class Category {
	String categoryId;
	String categoryName;
	String desc;
	int sort;
	List<ISecurityResourceImpl> resourceImpls;
	public Category() {
		resourceImpls=new ArrayList<>();
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public Category(String categoryId, String categoryName, String desc) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.desc = desc;
		resourceImpls=new ArrayList<>();
	}
	public List<ISecurityResourceImpl> getResourceImpls() {
		return resourceImpls;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
