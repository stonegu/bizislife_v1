package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="permissionedstuff")
public class PermissionedStuff implements PojoInterface, Serializable{
	
	public static enum Category{
		media,
		moduledetail,
		page,
		product,
		;
		
	}
	
	public static enum AllowDeny{
		allow("1"),
		deny("0"),
		
		;
		private String code;

		private AllowDeny(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}
	
	private static final long serialVersionUID = -6285099828046358422L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="category")
	private String category;

	@Column(name="permissiontype")
	private String permissiontype;
	
	@Column(name="allowdeny")
	private String allowdeny;
	
	@Column(name="pointuuid")
	private String pointuuid;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="permissionuuid")
	private String permissionuuid;
	
	@Column(name="stufforg")
	private Long stufforg;

	public PermissionedStuff() {
		super();
	}

	public PermissionedStuff(Long id, String category, String permissiontype,
			String allowdeny, String pointuuid, String parentuuid, String permissionuuid, Long stufforg) {
		super();
		this.id = id;
		this.category = category;
		this.permissiontype = permissiontype;
		this.allowdeny = allowdeny;
		this.pointuuid = pointuuid;
		this.parentuuid = parentuuid;
		this.permissionuuid = permissionuuid;
		this.stufforg = stufforg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPermissiontype() {
		return permissiontype;
	}

	public void setPermissiontype(String permissiontype) {
		this.permissiontype = permissiontype;
	}

	public String getAllowdeny() {
		return allowdeny;
	}

	public void setAllowdeny(String allowdeny) {
		this.allowdeny = allowdeny;
	}

	public String getPointuuid() {
		return pointuuid;
	}

	public void setPointuuid(String pointuuid) {
		this.pointuuid = pointuuid;
	}

	public String getParentuuid() {
		return parentuuid;
	}

	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}

	public String getPermissionuuid() {
		return permissionuuid;
	}

	public void setPermissionuuid(String permissionuuid) {
		this.permissionuuid = permissionuuid;
	}
	
	public Long getStufforg() {
		return stufforg;
	}

	public void setStufforg(Long stufforg) {
		this.stufforg = stufforg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PermissionedStuff [id=").append(id)
				.append(", category=").append(category)
				.append(", permissiontype=").append(permissiontype)
				.append(", allowdeny=").append(allowdeny)
				.append(", parentuuid=").append(parentuuid)
				.append(", pointuuid=").append(pointuuid).append("]");
		return builder.toString();
	}

}
