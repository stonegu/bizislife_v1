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
@Table(name="entitytreelevelview")
public class EntityTreeLevelView implements Cloneable, PojoInterface, TreeLevelView, Serializable{
	
	private static final long serialVersionUID = 3897365604276276683L;

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

	public EntityTreeLevelView() {
		super();
	}

	public EntityTreeLevelView(Long id, String parentuuid, String nodes,
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

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("parentuuid", parentuuid).append("nodes", nodes).toString();
    }

	@Override
	public EntityTreeLevelView clone() {
		EntityTreeLevelView clonedView = new EntityTreeLevelView();
		
		clonedView.setCreatedate(new Date());
		clonedView.setCreator_id(Long.valueOf(this.creator_id.intValue()));
		clonedView.setNodes(this.nodes);
		clonedView.setOrganization_id(Long.valueOf(this.organization_id));
		clonedView.setParentuuid(this.parentuuid);
		
		return clonedView;
	}
}
