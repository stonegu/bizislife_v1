package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;

@Entity
@Table(name="pagedetail")
public class PageDetail implements PojoInterface, NodeDetail, Serializable{

	private static final long serialVersionUID = 6282426714929323017L;
	public static final String FAKEPAGEUUID = "fakeSystemName"; // this uuid is used for create a fake page for preview.
	
	public static enum Type {

		Folder("fd", "folder"),
		Desktop("dk", "desktop page"),
		Mobile("mb", "mobile page"),
		Page("pg", "general page type for desktop and mobile"),
		
		// next type is for pageRetriver:
		category("ct", "category page"),
		product("pd", "product page"),
		;
		
		private String code; // max len is 2
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

	@Column(name="pageuuid")
	private String pageuuid;

	@Column(name="prettyname")
	private String prettyname;
	
	@Column(name="url")
	private String url;
    
	@Column(name="detail")
	private String detail;
	
	@Column(name="type")
	private String type;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="path")
	private String path;
	
	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="from_org_id")
	private Long from_org_id;
	
	@Column(name="from_page_uuid")
	private String from_page_uuid;
	

	public PageDetail() {
		super();
	}

	public PageDetail(Long id, String pageuuid, String prettyname, String url,
			String detail, String type, String parentuuid, String path,
			Date createdate, Long organization_id, Long creator_id) {
		super();
		this.id = id;
		this.pageuuid = pageuuid;
		this.prettyname = prettyname;
		this.url = url;
		this.detail = detail;
		this.type = type;
		this.parentuuid = parentuuid;
		this.path = path;
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

	public String getPageuuid() {
		return pageuuid;
	}
	
	public String getNodeUuid() {
		return pageuuid;
	}

	public void setPageuuid(String pageuuid) {
		this.pageuuid = pageuuid;
	}

	public String getPrettyname() {
		return prettyname;
	}

	public void setPrettyname(String prettyname) {
		this.prettyname = prettyname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public Long getFrom_org_id() {
		return from_org_id;
	}

	public void setFrom_org_id(Long from_org_id) {
		this.from_org_id = from_org_id;
	}

	public String getFrom_page_uuid() {
		return from_page_uuid;
	}

	public void setFrom_page_uuid(String from_page_uuid) {
		this.from_page_uuid = from_page_uuid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageDetail [id=");
		builder.append(id);
		builder.append(", pageuuid=");
		builder.append(pageuuid);
		builder.append(", prettyname=");
		builder.append(prettyname);
		builder.append(", url=");
		builder.append(url);
		builder.append(", detail=");
		builder.append(detail);
		builder.append(", type=");
		builder.append(type);
		builder.append(", parentuuid=");
		builder.append(parentuuid);
		builder.append(", path=");
		builder.append(path);
		builder.append(", createdate=");
		builder.append(createdate);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", creator_id=");
		builder.append(creator_id);
		builder.append("]");
		return builder.toString();
	}
	
//  Note: move clone function to service (SiteDesignService.pageClone) since clone need to clone containers and containertreelevelviews.	
//	@Override
//	public PageDetail clone() {
//		PageDetail clonedDetail = new PageDetail();
//		
//		String cloneUuid = UUID.randomUUID().toString();
//		
//		clonedDetail.setCreatedate(new Date());
//		clonedDetail.setCreator_id(Long.valueOf(this.creator_id.longValue()));
//		clonedDetail.setOrganization_id(Long.valueOf(this.organization_id.longValue()));
//		clonedDetail.setPageuuid(cloneUuid);
//		clonedDetail.setParentuuid(this.parentuuid);
//		clonedDetail.setPath(this.path);
//		clonedDetail.setPrettyname(this.prettyname);
//		clonedDetail.setType(this.type);
//		clonedDetail.setUrl(this.url);
//		
//		clonedDetail.setDetail(this.detail);
//		
//		
//		return clonedDetail;
//	}


	
}
