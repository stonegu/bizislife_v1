package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;

@Entity
@Table(name="entitydetail")
public class EntityDetail implements Cloneable, PojoInterface, ModuleInstanceInterface, NodeDetail, Serializable{
	
	private static final long serialVersionUID = -4698718830947113429L;
	
	public static enum EntityType {
		folder("fd", "folder"),
		entity("et", "entity"),
		
		// for 
		
		;
		
		private String code; // max len is 2
		private String desc;
		private EntityType(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public String getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
		public static EntityType fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(EntityType t : EntityType.values()){
					if(t.getCode().equals(code)){
						return t;
					}
				}
			}
			return null;
		}
		
	}
	
	public static enum Visibility{
		show("1"), // can be used
		hide("0"), // not ready to use
		;
		
		private String code;

		private Visibility(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public Visibility fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(Visibility v : Visibility.values()){
					if(v.getCode().equals(code.trim())){
						return v;
					}
				}
			}
			return null;
		}
	}

	
//	visibility CHAR(1),

	

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="entityuuid")
	private String entityuuid;

	@Column(name="detail")
	private String detail;
	
	@Column(name="type")
	private String type;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="path")
	private String path;
	
	@Column(name="name")
	private String name;
	
	@Column(name="moduleuuid")
	private String moduleuuid;
    
	@Column(name="createdate")
	private Date createdate;
	
	@Column(name="expiredate")
	private Date expiredate;
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="tag")
	private String tag;
	
	@Column(name="visibility")
	private String visibility;
	
	@Column(name="from_org_id")
	private Long from_org_id;
	
	@Column(name="from_entity_uuid")
	private String from_entity_uuid;
	
	@Column(name="catpageuuid_desktop")
	private String catpageuuid_desktop;
	
	@Column(name="prodpageuuid_desktop")
	private String prodpageuuid_desktop;
	
	@Column(name="catpageuuid_mobile")
	private String catpageuuid_mobile;
	
	@Column(name="prodpageuuid_mobile")
	private String prodpageuuid_mobile;
	
	public EntityDetail() {
		super();
	}


	public EntityDetail(Long id, String entityuuid, String name, String moduleuuid, String detail,
			String type, String parentuuid, String path,
			Date createdate, Date expiredate, Long organization_id, Long creator_id, String tag,
			String visibility) {
		super();
		this.id = id;
		this.entityuuid = entityuuid;
		this.detail = detail;
		this.type = type;
		this.parentuuid = parentuuid;
		this.path = path;
		this.name = name;
		this.moduleuuid = moduleuuid;
		this.createdate = createdate;
		this.expiredate = expiredate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
		this.tag = tag;
		this.visibility = visibility;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityuuid() {
		return entityuuid;
	}
	
	public String getUuid() {
		return entityuuid;
	}
	
	public String getNodeUuid() {
		return entityuuid;
	}
	
	// try to have same method with moduleinstance
	public String getModuleinstanceuuid() {
		return entityuuid;
	}
	

	public void setEntityuuid(String entityuuid) {
		this.entityuuid = entityuuid;
	}

	public String getName() {
		return name;
	}
	
	public String getPrettyname() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModuleuuid() {
		return moduleuuid;
	}

	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
	}

	public String getDetail() {
		return detail;
	}
	
	public String getxml() {
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

	public Date getExpiredate() {
		return expiredate;
	}

	public void setExpiredate(Date expiredate) {
		this.expiredate = expiredate;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
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
	
	public Long getFrom_org_id() {
		return from_org_id;
	}


	public void setFrom_org_id(Long from_org_id) {
		this.from_org_id = from_org_id;
	}


	public String getFrom_entity_uuid() {
		return from_entity_uuid;
	}


	public void setFrom_entity_uuid(String from_entity_uuid) {
		this.from_entity_uuid = from_entity_uuid;
	}

	public String getCatpageuuid_desktop() {
		return catpageuuid_desktop;
	}


	public void setCatpageuuid_desktop(String catpageuuid_desktop) {
		this.catpageuuid_desktop = catpageuuid_desktop;
	}


	public String getProdpageuuid_desktop() {
		return prodpageuuid_desktop;
	}


	public void setProdpageuuid_desktop(String prodpageuuid_desktop) {
		this.prodpageuuid_desktop = prodpageuuid_desktop;
	}


	public String getCatpageuuid_mobile() {
		return catpageuuid_mobile;
	}


	public void setCatpageuuid_mobile(String catpageuuid_mobile) {
		this.catpageuuid_mobile = catpageuuid_mobile;
	}


	public String getProdpageuuid_mobile() {
		return prodpageuuid_mobile;
	}


	public void setProdpageuuid_mobile(String prodpageuuid_mobile) {
		this.prodpageuuid_mobile = prodpageuuid_mobile;
	}


	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("entityuuid", entityuuid).append("tag", tag).toString();
    }


	@Override
	public EntityDetail clone() {
		
		EntityDetail clonedDetail = new EntityDetail();
		
		String cloneUuid = UUID.randomUUID().toString();
//		Map<String, String> oldNewUuidMap = new HashMap<String, String>();
//		oldNewUuidMap.put(this.moduleuuid, cloneUuid);
		
		
		clonedDetail.setCreatedate(new Date());
		clonedDetail.setCreator_id(Long.valueOf(this.creator_id));
		clonedDetail.setEntityuuid(cloneUuid);
		clonedDetail.setExpiredate(this.expiredate!=null?new Date(this.expiredate.getTime()):null);
		clonedDetail.setModuleuuid(this.moduleuuid);
		clonedDetail.setName(this.name);
		clonedDetail.setOrganization_id(Long.valueOf(this.organization_id));
		clonedDetail.setParentuuid(this.parentuuid);
		clonedDetail.setPath(this.path);
		clonedDetail.setTag(this.tag);
		clonedDetail.setType(this.type);
		clonedDetail.setVisibility(this.visibility);
		
		clonedDetail.setFrom_org_id(this.organization_id.longValue());
		clonedDetail.setFrom_entity_uuid(this.entityuuid);
		
		// xml: replace old uuid with new uuid;
		Module sourceModule = SitedesignHelper.getModuleFromXml(this.detail);
		if(sourceModule!=null && sourceModule.getAttrGroupList()!=null && sourceModule.getAttrGroupList().size()>0){
			for(AttrGroup g : sourceModule.getAttrGroupList()){
				String newGroupUuid = UUID.randomUUID().toString();
//				oldNewUuidMap.put(g.getGroupUuid(), newGroupUuid);
				g.setGroupUuid(newGroupUuid);
				
				if(g.getAttrList()!=null && g.getAttrList().size()>0){
					for(ModuleAttribute a : g.getAttrList()){
						String newAttrUuid = UUID.randomUUID().toString();
//						oldNewUuidMap.put(a.getUuid(), newAttrUuid);
						a.setUuid(newAttrUuid);
					}
				}
			}
			
			clonedDetail.setDetail(SitedesignHelper.getXmlFromModule(sourceModule));
		}
		
		
		
		return clonedDetail;
		
	}

}
