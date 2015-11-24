package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="orgmeta")
public class OrgMeta implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 7577109719149045134L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="orgid")
	private Long orgid;
	
	@Column(name="orguuid")
	private String orgUuid;

	@Column(name="domains")
	private String domains; // www.webislife.com, webislife.net, 

	public OrgMeta() {
		super();
	}

	public OrgMeta(Long id, Long orgid, String orgUuid, String domains) {
		super();
		this.id = id;
		this.orgid = orgid;
		this.orgUuid = orgUuid;
		this.domains = domains;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getOrgUuid() {
		return orgUuid;
	}

	public void setOrgUuid(String orgUuid) {
		this.orgUuid = orgUuid;
	}

	public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

	@Override
	public String toString() {
		return "OrgMeta [id=" + id + ", orgid=" + orgid + ", orgUuid="
				+ orgUuid + ", domains=" + domains + "]";
	}

}
