package com.bizislife.core.view.dto;

import java.io.Serializable;

/**
 * @author Stone
 * 
 * this class hold organization's all payment info and what org can do.
 * some data is from orgMeta
 *
 */
@Deprecated
public class OrgPaymentProfile implements Serializable{
	private Long orgid;

	// data from orgMeta:
	private Long pagesCanHave;
	private Long containersCanHave;
	private Long moduledetailsCanHave;
	private Long instancesCanHave;
	private Long productsCanHave;
	private Long maxCharsPerModuledetail;
	private Long maxCharsPerInstance;
	private Long maxCharsPerJsp;
	private Long maxCharsPerCss;
	
	// data from ...
	
	public OrgPaymentProfile() {
		super();
	}

	public OrgPaymentProfile(Long orgid, Long pagesCanHave,
			Long containersCanHave, Long moduledetailsCanHave,
			Long instancesCanHave, Long productsCanHave, Long maxCharsPerModuledetail,
			Long maxCharsPerInstance, Long maxCharsPerJsp, Long maxCharsPerCss) {
		super();
		this.orgid = orgid;
		this.pagesCanHave = pagesCanHave;
		this.containersCanHave = containersCanHave;
		this.moduledetailsCanHave = moduledetailsCanHave;
		this.instancesCanHave = instancesCanHave;
		this.productsCanHave = productsCanHave;
		this.maxCharsPerModuledetail = maxCharsPerModuledetail;
		this.maxCharsPerInstance = maxCharsPerInstance;
		this.maxCharsPerJsp = maxCharsPerJsp;
		this.maxCharsPerCss = maxCharsPerCss;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public Long getPagesCanHave() {
		return pagesCanHave;
	}

	public void setPagesCanHave(Long pagesCanHave) {
		this.pagesCanHave = pagesCanHave;
	}

	public Long getContainersCanHave() {
		return containersCanHave;
	}

	public void setContainersCanHave(Long containersCanHave) {
		this.containersCanHave = containersCanHave;
	}

	public Long getModuledetailsCanHave() {
		return moduledetailsCanHave;
	}

	public void setModuledetailsCanHave(Long moduledetailsCanHave) {
		this.moduledetailsCanHave = moduledetailsCanHave;
	}

	public Long getInstancesCanHave() {
		return instancesCanHave;
	}

	public void setInstancesCanHave(Long instancesCanHave) {
		this.instancesCanHave = instancesCanHave;
	}

	public Long getProductsCanHave() {
		return productsCanHave;
	}

	public void setProductsCanHave(Long productsCanHave) {
		this.productsCanHave = productsCanHave;
	}

	public Long getMaxCharsPerModuledetail() {
		return maxCharsPerModuledetail;
	}

	public void setMaxCharsPerModuledetail(Long maxCharsPerModuledetail) {
		this.maxCharsPerModuledetail = maxCharsPerModuledetail;
	}

	public Long getMaxCharsPerInstance() {
		return maxCharsPerInstance;
	}

	public void setMaxCharsPerInstance(Long maxCharsPerInstance) {
		this.maxCharsPerInstance = maxCharsPerInstance;
	}

	public Long getMaxCharsPerJsp() {
		return maxCharsPerJsp;
	}

	public void setMaxCharsPerJsp(Long maxCharsPerJsp) {
		this.maxCharsPerJsp = maxCharsPerJsp;
	}

	public Long getMaxCharsPerCss() {
		return maxCharsPerCss;
	}

	public void setMaxCharsPerCss(Long maxCharsPerCss) {
		this.maxCharsPerCss = maxCharsPerCss;
	}

	
}
