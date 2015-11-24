package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="orgcanjoin")
public class OrgCanJoin implements PojoInterface, Serializable{

	private static final long serialVersionUID = -3057868652632278687L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name="organization_id")
	private Long organization_id;

	@Column(name="totalaccountcanjoin")
	private Integer totalaccountcanjoin;
	
	@Column(name="joinkey")
	private String joinkey;

	public OrgCanJoin() {
		super();
	}

	public OrgCanJoin(Long id, Long organization_id, Integer totalaccountcanjoin, String joinkey) {
		super();
		this.id = id;
		this.organization_id = organization_id;
		this.totalaccountcanjoin = totalaccountcanjoin;
		this.joinkey = joinkey;
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

	public Integer getTotalaccountcanjoin() {
		return totalaccountcanjoin;
	}

	public void setTotalaccountcanjoin(Integer totalaccountcanjoin) {
		this.totalaccountcanjoin = totalaccountcanjoin;
	}

	public String getJoinkey() {
		return joinkey;
	}

	public void setJoinkey(String joinkey) {
		this.joinkey = joinkey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrgCanJoin [id=").append(id)
				.append(", organization_id=").append(organization_id)
				.append(", totalaccountcanjoin=").append(totalaccountcanjoin)
				.append("]");
		return builder.toString();
	}

}
