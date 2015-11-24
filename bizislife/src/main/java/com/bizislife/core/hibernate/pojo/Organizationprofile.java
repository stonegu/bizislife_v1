package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="organizationprofile")
public class Organizationprofile implements PojoInterface, Serializable{

	private static final long serialVersionUID = 1689234013450313201L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="profileimage")
	private String profileimage;
	
	@Column(name="industry")
	private String industry;

	@Column(name="subcategory")
	private String subcategory;

	@Column(name="tag")
	private String tag;
	
	@Column(name="founded_date")
	private Date founded_date;

	@Column(name="description")
	private String description;
	
	@Column(name="businesshour")
	private String businesshour;
	
	@Column(name="paypal_username")
	private String paypal_username;
	
	@Column(name="paypal_password")
	private String paypal_password;
	
	@Column(name="paypal_signature")
	private String paypal_signature;
	
	@Column(name="paypal_appid")
	private String paypal_appid;
	
	@Column(name="createdate")
	private Date createdate;
	
	public Organizationprofile() {
		super();
	}

	public Organizationprofile(Long id, Long organization_id,
			String profileimage, String industry, String subcategory,
			String tag, Date founded_date, String description,
			String businesshour, Date createdate) {
		super();
		this.id = id;
		this.organization_id = organization_id;
		this.profileimage = profileimage;
		this.industry = industry;
		this.subcategory = subcategory;
		this.tag = tag;
		this.founded_date = founded_date;
		this.description = description;
		this.businesshour = businesshour;
		this.createdate = createdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public String getProfileimage() {
		return profileimage;
	}

	public void setProfileimage(String profileimage) {
		this.profileimage = profileimage;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Date getFounded_date() {
		return founded_date;
	}

	public void setFounded_date(Date founded_date) {
		this.founded_date = founded_date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBusinesshour() {
		return businesshour;
	}

	public void setBusinesshour(String businesshour) {
		this.businesshour = businesshour;
	}

	public String getPaypal_username() {
		return paypal_username;
	}

	public void setPaypal_username(String paypal_username) {
		this.paypal_username = paypal_username;
	}

	public String getPaypal_password() {
		return paypal_password;
	}

	public void setPaypal_password(String paypal_password) {
		this.paypal_password = paypal_password;
	}

	public String getPaypal_signature() {
		return paypal_signature;
	}

	public void setPaypal_signature(String paypal_signature) {
		this.paypal_signature = paypal_signature;
	}

	public String getPaypal_appid() {
		return paypal_appid;
	}

	public void setPaypal_appid(String paypal_appid) {
		this.paypal_appid = paypal_appid;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("organization id", organization_id).toString();
    }
	
}
