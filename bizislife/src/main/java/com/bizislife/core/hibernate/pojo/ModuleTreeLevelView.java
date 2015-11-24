package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="moduletreelevelview")
public class ModuleTreeLevelView implements Cloneable, TreeLevelView, PojoInterface, Serializable{
	
	private static final long serialVersionUID = -5619977679370772315L;

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
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;

	public ModuleTreeLevelView() {
		super();
	}

	public ModuleTreeLevelView(Long id, String parentuuid, String nodes,
			Date createdate, Long organization_id, Long creator_id) {
		super();
		this.id = id;
		this.parentuuid = parentuuid;
		this.nodes = nodes;
		this.createdate = createdate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
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

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public ModuleTreeLevelView clone(){
		ModuleTreeLevelView clonedTreeView = new ModuleTreeLevelView();
		
		clonedTreeView.setCreatedate(new Date());
		clonedTreeView.setCreator_id(Long.valueOf(this.creator_id.intValue()));
		clonedTreeView.setNodes(this.nodes);
		clonedTreeView.setOrganization_id(Long.valueOf(this.organization_id));
		clonedTreeView.setParentuuid(this.parentuuid);
		
		return clonedTreeView;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleTreeLevelView [id=").append(id)
				.append(", parentuuid=").append(parentuuid).append(", nodes=")
				.append(nodes).append(", createdate=").append(createdate)
				.append(", organization_id=").append(organization_id)
				.append(", creator_id=").append(creator_id).append("]");
		return builder.toString();
	}

}
