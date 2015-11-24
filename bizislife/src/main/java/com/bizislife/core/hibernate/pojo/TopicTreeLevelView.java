package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="topictreelevelview")
public class TopicTreeLevelView implements PojoInterface, TreeLevelView, Serializable{
	
	private static final long serialVersionUID = 5142015885262778781L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="parentuuid")
	private String parentuuid;
    
	@Column(name="nodes")
	private String nodes;
	
	@Column(name="createdate")
	private Date createdate;

	public TopicTreeLevelView() {
		super();
	}

	public TopicTreeLevelView(Long id, String parentuuid, String nodes,
			Date createdate) {
		super();
		this.id = id;
		this.parentuuid = parentuuid;
		this.nodes = nodes;
		this.createdate = createdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParentuuid() {
		return parentuuid;
	}

	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}

	public String getNodes() {
		return nodes;
	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
	public void setOrganization_id(Long organization_id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCreator_id(Long creator_id) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TopicTreeLevelView [id=");
		builder.append(id);
		builder.append(", parentuuid=");
		builder.append(parentuuid);
		builder.append(", nodes=");
		builder.append(nodes);
		builder.append(", createdate=");
		builder.append(createdate);
		builder.append("]");
		return builder.toString();
	}




}
