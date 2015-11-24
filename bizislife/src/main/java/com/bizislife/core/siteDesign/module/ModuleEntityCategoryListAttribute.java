package com.bizislife.core.siteDesign.module;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleProductCatNavAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleEntityCategoryListAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "Category list";

	private static final long serialVersionUID = 544691537550031893L;

	public static enum Type {
		Product,
		//media,
		;
	}
	
	public static enum SortType {
		NotSort,
		Name,
		//Weight, // admin can set weight for each product category, or even product
		;
	}
	
	private String catType;
	private String sortType;
	private int levelOfCategory; // how many level of category will be down from root
//	private String deskCatListPageuuid; // the page that catlist's node need to go
//	private String mobileCatListPageuuid; // the page that catlist's node need to go
	
	
	public ModuleEntityCategoryListAttribute() {
		super();
	}

	public ModuleEntityCategoryListAttribute(String catType, String sortType,
			int levelOfCategory, String deskCatListPageuuid,
			String mobileCatListPageuuid) {
		super();
		this.catType = catType;
		this.sortType = sortType;
		this.levelOfCategory = levelOfCategory;
//		this.deskCatListPageuuid = deskCatListPageuuid;
//		this.mobileCatListPageuuid = mobileCatListPageuuid;
	}

	public String getCatType() {
		return catType;
	}

	public void setCatType(String catType) {
		this.catType = catType;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public int getLevelOfCategory() {
		return levelOfCategory;
	}

	public void setLevelOfCategory(int levelOfCategory) {
		this.levelOfCategory = levelOfCategory;
	}

//	public String getDeskCatListPageuuid() {
//		return deskCatListPageuuid;
//	}
//
//	public void setDeskCatListPageuuid(String deskCatListPageuuid) {
//		this.deskCatListPageuuid = deskCatListPageuuid;
//	}
//
//	public String getMobileCatListPageuuid() {
//		return mobileCatListPageuuid;
//	}
//
//	public void setMobileCatListPageuuid(String mobileCatListPageuuid) {
//		this.mobileCatListPageuuid = mobileCatListPageuuid;
//	}

	@Override
	public String toString() {
		return "ModuleEntityCategoryListAttribute [catType=" + catType
				+ ", sortType=" + sortType + ", levelOfCategory="
				+ levelOfCategory + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((catType == null) ? 0 : catType.hashCode());
		result = prime * result + levelOfCategory;
		result = prime * result
				+ ((sortType == null) ? 0 : sortType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleEntityCategoryListAttribute other = (ModuleEntityCategoryListAttribute) obj;
		if (catType == null) {
			if (other.catType != null)
				return false;
		} else if (!catType.equals(other.catType))
			return false;
		if (levelOfCategory != other.levelOfCategory)
			return false;
		if (sortType == null) {
			if (other.sortType != null)
				return false;
		} else if (!sortType.equals(other.sortType))
			return false;
		return true;
	}

	
}
