package com.bizislife.core.siteDesign.module;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleProductListAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleProductListAttribute extends ModuleAttribute{

	public static final String MODULEATTRIBUTEDESC = "Product list";
	private static final long serialVersionUID = 8253607995915171551L;
	
	public static final int ALL_PRODUCT_NUM = 10000000;
	public static final int DEFAULT_PRODUCTINPAGE_NUM = 20;
	
	private Integer totalNumProductsInPage; // total products (not folder) should be displayed in one page
	private Boolean hasPagination; // pagination is for products only, not for categories (folders)
//	private String desktopProductPageUuid; // note: productPageUuid==null or productPageUuid not exist, system will pick a page to display product automatically.
//	private String mobileProductPageuuid;
	
	public ModuleProductListAttribute() {
		this.hasPagination = false;
	}

	public ModuleProductListAttribute(int totalNumProductsInPage, boolean hasPagination) {
		super();
		this.totalNumProductsInPage = totalNumProductsInPage;
		this.hasPagination = hasPagination;
//		this.desktopProductPageUuid = desktopProductPageUuid;
//		this.mobileProductPageuuid = mobileProductPageuuid;
	}


	public Integer getTotalNumProductsInPage() {
		return totalNumProductsInPage;
	}

	public void setTotalNumProductsInPage(Integer totalNumProductsInPage) {
		this.totalNumProductsInPage = totalNumProductsInPage;
	}

	public Boolean getHasPagination() {
		return hasPagination;
	}

	public void setHasPagination(Boolean hasPagination) {
		this.hasPagination = hasPagination;
	}

//	public String getDesktopProductPageUuid() {
//		return desktopProductPageUuid;
//	}
//
//	public void setDesktopProductPageUuid(String desktopProductPageUuid) {
//		this.desktopProductPageUuid = desktopProductPageUuid;
//	}
//
//	public String getMobileProductPageuuid() {
//		return mobileProductPageuuid;
//	}
//
//	public void setMobileProductPageuuid(String mobileProductPageuuid) {
//		this.mobileProductPageuuid = mobileProductPageuuid;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleProductListAttribute [totalNumProductsInPage=")
				.append(totalNumProductsInPage).append(", hasPagination=")
				.append(hasPagination).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((hasPagination == null) ? 0 : hasPagination.hashCode());
		result = prime
				* result
				+ ((totalNumProductsInPage == null) ? 0
						: totalNumProductsInPage.hashCode());
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
		ModuleProductListAttribute other = (ModuleProductListAttribute) obj;
		if (hasPagination == null) {
			if (other.hasPagination != null)
				return false;
		} else if (!hasPagination.equals(other.hasPagination))
			return false;
		if (totalNumProductsInPage == null) {
			if (other.totalNumProductsInPage != null)
				return false;
		} else if (!totalNumProductsInPage.equals(other.totalNumProductsInPage))
			return false;
		return true;
	}

}
