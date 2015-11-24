package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;


@Entity
@Table(name="pagetreelevelview")
public class PageTreeLevelView implements Cloneable, PojoInterface, TreeLevelView, Serializable{
	
	private static final long serialVersionUID = -7552796930892516722L;

	public static enum Type {
		Desktop("dk", "desktop view"),
		Mobile("mb", "mobile view"),
		;
		
		private String code;
		private String desc;
		private Type(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public String getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
		public static Type fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(Type t : Type.values()){
					if(t.getCode().equals(code)){
						return t;
					}
				}
			}
			return null;
		}
	}
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="type")
	private String type;
	
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

	public PageTreeLevelView() {
		super();
	}

	public PageTreeLevelView(Long id, String type, String parentuuid,
			String nodes, Date createdate, Long organization_id, Long creator_id) {
		super();
		this.id = id;
		this.type = type;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageTreeLevelView [id=").append(id).append(", type=")
				.append(type).append(", parentuuid=").append(parentuuid)
				.append(", nodes=").append(nodes).append(", createdate=")
				.append(createdate).append(", organization_id=")
				.append(organization_id).append(", creator_id=")
				.append(creator_id).append("]");
		return builder.toString();
	}
	
	@Override
	public PageTreeLevelView clone() {
		PageTreeLevelView clonedView = new PageTreeLevelView();
		clonedView.setCreatedate(new Date());
		clonedView.setCreator_id(Long.valueOf(this.creator_id.intValue()));
		clonedView.setNodes(this.nodes);
		clonedView.setOrganization_id(Long.valueOf(this.organization_id));
		clonedView.setParentuuid(this.parentuuid);
		clonedView.setType(this.type);
		
		return clonedView;
	}
}
