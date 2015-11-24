package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Deprecated
@Entity
@Table(name="visibleorg")
public class VisibleOrg implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 5562229064619678063L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name="organization_id")
	private Long organization_id;
	
	public VisibleOrg() {
		super();
	}

	public VisibleOrg(Long id, Long organization_id) {
		super();
		this.id = id;
		this.organization_id = organization_id;
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

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("orgId", organization_id).toString();
    }

}
