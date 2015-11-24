package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.hibernate.pojo.PageDetail.Type;

@Entity
@Table(name="containerdetail")
public class ContainerDetail implements PojoInterface, NodeDetail, Serializable{

	private static final long serialVersionUID = 6453261058061403602L;
	
	
	// 0 for left to right, 1 for top to bottom. if the parent is 0, current container will be 1.
	public static enum Direction {
		Left2Right("0", "left to right"), // the resize handler will be on top and bottom
		Top2Bottom("1", "top to bottom"), // the resize handler will be on right and left
		;
		
		private String code;
		private String desc;
		private Direction(String code, String desc) {
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
	
	public static enum BOOLEAN {
		enable("1"),
		disable("0"),
		
		;
		private String code;

		private BOOLEAN(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}
	

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="containeruuid")
	private String containeruuid;

	@Column(name="prettyname")
	private String prettyname;
    
	@Column(name="pageuuid")
	private String pageuuid;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="path")
	private String path;
	
	@Column(name="direction")
	private String direction;
	
	@Column(name="topposition")
	private Integer topposition;
	
	@Column(name="leftposition")
	private Integer leftposition;
	
	@Column(name="width")
	private Integer width;
	
	@Column(name="height")
	private Integer height;
	
	@Column(name="hexcolor")
	private String hexColor;
	
	@Column(name="classnames")
	private String classnames;
	
	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="deletable")
	private String deletable;
	
	@Column(name="editable")
	private String editable;
	
	@Column(name="moduleuuid")
	private String moduleuuid;

	public ContainerDetail() {
		super();
	}

	public ContainerDetail(Long id, String containeruuid, String prettyname,
			String pageuuid, String parentuuid, String path, String direction,
			Integer topposition, Integer leftposition, Integer width,
			Integer height, String hexColor, Date createdate, Long organization_id,
			Long creator_id, String deletable, String editable) {
		super();
		this.id = id;
		this.containeruuid = containeruuid;
		this.prettyname = prettyname;
		this.pageuuid = pageuuid;
		this.parentuuid = parentuuid;
		this.path = path;
		this.direction = direction;
		this.topposition = topposition;
		this.leftposition = leftposition;
		this.width = width;
		this.height = height;
		this.hexColor = hexColor;
		this.createdate = createdate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
		this.deletable = deletable;
		this.editable = editable;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContaineruuid() {
		return containeruuid;
	}
	
	public String getNodeUuid() {
		return containeruuid;
	}

	public void setContaineruuid(String containeruuid) {
		this.containeruuid = containeruuid;
	}

	public String getPrettyname() {
		return prettyname;
	}

	public void setPrettyname(String prettyname) {
		this.prettyname = prettyname;
	}

	public String getPageuuid() {
		return pageuuid;
	}

	public void setPageuuid(String pageuuid) {
		this.pageuuid = pageuuid;
	}

	public String getParentuuid() {
		return parentuuid;
	}

	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Integer getTopposition() {
		return topposition;
	}

	public void setTopposition(Integer topposition) {
		this.topposition = topposition;
	}

	public Integer getLeftposition() {
		return leftposition;
	}

	public void setLeftposition(Integer leftposition) {
		this.leftposition = leftposition;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	public String getClassnames() {
		return classnames;
	}

	public void setClassnames(String classnames) {
		this.classnames = classnames;
	}
	
	public void addClassname(String classname){
		if(StringUtils.isNotBlank(this.classnames)){
			this.classnames = this.classnames + " " + classname;
		}else{
			this.classnames = classname;
		}
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

	public String getDeletable() {
		return deletable;
	}

	public void setDeletable(String deletable) {
		this.deletable = deletable;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getModuleuuid() {
		return moduleuuid;
	}

	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContainerDetail [id=").append(id)
				.append(", containeruuid=").append(containeruuid)
				.append(", prettyname=").append(prettyname)
				.append(", pageuuid=").append(pageuuid).append(", parentuuid=")
				.append(parentuuid).append(", path=").append(path)
				.append(", direction=").append(direction)
				.append(", topposition=").append(topposition)
				.append(", leftposition=").append(leftposition)
				.append(", width=").append(width).append(", height=")
				.append(height).append(", createdate=").append(createdate)
				.append(", organization_id=").append(organization_id)
				.append(", creator_id=").append(creator_id)
				.append(", deletable=").append(deletable).append(", editable=")
				.append(editable).append("]");
		return builder.toString();
	}

}
