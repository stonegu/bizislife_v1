package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name="mediadetail")
public class MediaDetail implements PojoInterface, NodeDetail, Serializable{
	
	private static final long serialVersionUID = -299455454037765920L;
	
	public static enum MediaType {
		css("css", "css file"),
		folder("fd", "folder"),
		image("img", "image file"),
		javascript("js", "javascript file"),
		pdf("pdf", "pdf file"),
		text("txt", "text file"),

		;
		
		private String code; // max len is 4
		private String desc;
		private MediaType(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public String getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
		public static MediaType fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(MediaType t : MediaType.values()){
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

	@Column(name="mediauuid")
	private String mediauuid;

	@Column(name="prettyname")
	private String prettyname;
    
	@Column(name="nodetype")
	private String nodetype;
	
	@Column(name="contenttype")
	private String contenttype;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="path")
	private String path;
	
	@Column(name="sourceuuid")
	private String sourceuuid;
    
	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="tag")
	private String tag;

	public MediaDetail() {
		super();
	}

	public MediaDetail(Long id, String mediauuid, String prettyname,
			String nodetype, String contenttype, String parentuuid,
			String path, String sourceuuid, Date createdate,
			Long organization_id, Long creator_id, String tag) {
		super();
		this.id = id;
		this.mediauuid = mediauuid;
		this.prettyname = prettyname;
		this.nodetype = nodetype;
		this.contenttype = contenttype;
		this.parentuuid = parentuuid;
		this.path = path;
		this.sourceuuid = sourceuuid;
		this.createdate = createdate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
		this.tag = tag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMediauuid() {
		return mediauuid;
	}
	
	public String getNodeUuid() {
		return mediauuid;
	}

	public void setMediauuid(String mediauuid) {
		this.mediauuid = mediauuid;
	}

	public String getPrettyname() {
		return prettyname;
	}

	public void setPrettyname(String prettyname) {
		this.prettyname = prettyname;
	}

	public String getNodetype() {
		return nodetype;
	}

	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
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

	public String getSourceuuid() {
		return sourceuuid;
	}

	public void setSourceuuid(String sourceuuid) {
		this.sourceuuid = sourceuuid;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public static Comparator<MediaDetail> prettyNameComparator = new Comparator<MediaDetail>() {

		@Override
		public int compare(MediaDetail o1, MediaDetail o2) {
			return o1.getPrettyname().compareToIgnoreCase(o2.getPrettyname());
		}
		
	};


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MediaDetail [id=").append(id).append(", mediauuid=")
				.append(mediauuid).append(", prettyname=").append(prettyname)
				.append(", nodetype=").append(nodetype)
				.append(", contenttype=").append(contenttype)
				.append(", parentuuid=").append(parentuuid).append(", path=")
				.append(path).append(", sourceuuid=").append(sourceuuid)
				.append(", createdate=").append(createdate)
				.append(", organization_id=").append(organization_id)
				.append(", creator_id=").append(creator_id).append(", tag=")
				.append(tag).append("]");
		return builder.toString();
	}

}
