package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.TopicTreeNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Entity
@Table(name="activitylog")
public class ActivityLog implements PojoInterface, Serializable{
	
	public static enum ActivityType {
		announceProduct,
		changePageContainer,
		cloneProduct,
		deletePageContainer,
		newAcct,
		newOrg,
		newPage,
		newPageContainer,
		newProduct,
		pagePublish,
		suspendAccount,
		userLogin,
		;
		
	}

	private static final long serialVersionUID = -6963282353230668158L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="account_id")
	private Long account_id;

	@Column(name="organization_id")
	private Long organization_id;
    
	@Column(name="type")
	private String type;
	
	@Column(name="data")
	private String data;
	
	@Column(name="createdate")
	private Date createdate;
	
	public ActivityLog() {
		super();
	}

	public ActivityLog(Long id, Long account_id, Long organization_id,
			String type, String data, Date createdate) {
		super();
		this.id = id;
		this.account_id = account_id;
		this.organization_id = organization_id;
		this.type = type;
		this.data = data;
		this.createdate = createdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public ActivityType getType() {
		if(StringUtils.isNotBlank(type)){
			for(ActivityType t : ActivityType.values()){
				if(t.name().equals(type.trim())){
					return t;
				}
			}
		}
		
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public ActivityLogData getActivityLogData(){
		if(StringUtils.isNotBlank(this.data)){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(ActivityLogData.class);
			return (ActivityLogData)stream.fromXML(this.data);
		}
		return null;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("data", data).toString();
    }
}
