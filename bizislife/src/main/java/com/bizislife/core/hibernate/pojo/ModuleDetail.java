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
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;


@Entity
@Table(name="moduledetail")
public class ModuleDetail implements Cloneable, PojoInterface, NodeDetail, Serializable{

	private static final long serialVersionUID = 636495162054055942L;

	public static enum Visibility{
		visibleToAll("1"), // public
		visibleInsideOrg("0"), // private
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
	
	public static enum Type {
		folder("fd", "folder"),
		module("md", "module"),
		productModule("pm", "product module"),
		
		// all type
		all("all", "all type (module & productModule)"),
		
		// for ModuleTreeNode only: 
		instance("ins", "module instance"),
//		ProductInstanceView("piv", "product instance view"),
//		NormalInstanceView("niv", "normal instance view"),
		
		
		;

		private String code; // max len is 4
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
	
	@Column(name="moduleuuid")
	private String moduleuuid;

	@Column(name="prettyname")
	private String prettyname;
	
	@Column(name="description")
	private String description;
	
	@Column(name="type")
	private String type;
	
	@Column(name="parentuuid")
	private String parentuuid;
	
	@Column(name="path")
	private String path;

	@Column(name="xml")
	private String xml;
	
	@Column(name="jsp")
	private String jsp;
	
	@Column(name="css")
	private String css;
	
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="from_org_id")
	private Long from_org_id;
	
	@Column(name="from_module_uuid")
	private String from_module_uuid;
	
	@Column(name="visibility")
	private String visibility;

	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="creator_id")
	private Long creator_id;
	
	@Transient
	private Map<String, String> thisThatUuids; // the map to hold this & that uuids for groups and attrs when run equal method.
	

	public ModuleDetail() {
		super();
	}

	public ModuleDetail(Long id, String moduleuuid, String prettyname,
			String description, String type, String parentuuid, String path, String xml, String jsp, String css,
			Long organization_id, Long from_org_id, String from_module_uuid, String visibility, Date createdate,
			Long creator_id) {
		super();
		this.id = id;
		this.moduleuuid = moduleuuid;
		this.prettyname = prettyname;
		this.description = description;
		this.type = type;
		this.parentuuid = parentuuid;
		this.path = path;
		this.xml = xml;
		this.jsp = jsp;
		this.css = css;
		this.organization_id = organization_id;
		this.from_org_id = from_org_id;
		this.from_module_uuid = from_module_uuid;
		this.visibility = visibility;
		this.createdate = createdate;
		this.creator_id = creator_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModuleuuid() {
		return moduleuuid;
	}

	public String getNodeUuid() {
		return moduleuuid;
	}
	
	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
	}

	public String getPrettyname() {
		return prettyname;
	}

	public void setPrettyname(String prettyname) {
		this.prettyname = prettyname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public Long getFrom_org_id() {
		return from_org_id;
	}

	public void setFrom_org_id(Long from_org_id) {
		this.from_org_id = from_org_id;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
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

	public String getFrom_module_uuid() {
		return from_module_uuid;
	}

	public void setFrom_module_uuid(String from_module_uuid) {
		this.from_module_uuid = from_module_uuid;
	}

	public String getJsp() {
		return jsp;
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	/* 
	 * 
	 * clone ModuleDetail, the uuid for moduleDetail and it's attributes will be changed!! And the createDate will be the currentDate.<br/>
	 * 
	 * @param oldNewUuidMap pass a new empty HashMap if you like get the oldNewUuids for moduledetail, otherwise pass null;
	 * 
	 */
	public ModuleDetail clone(Map<String, String> oldNewUuidMap){
		String cloneUuid = UUID.randomUUID().toString();
		
//		Map<String, String> oldNewUuidMap = new HashMap<String, String>();
		if(oldNewUuidMap==null) oldNewUuidMap = new HashMap<String, String>();
		oldNewUuidMap.put(this.moduleuuid, cloneUuid);

		
		ModuleDetail cloneDetail = new ModuleDetail();
		cloneDetail.setCreatedate(new Date());
		cloneDetail.setCreator_id(Long.valueOf(this.creator_id));
		cloneDetail.setDescription(this.description);
		cloneDetail.setFrom_module_uuid(this.moduleuuid);
		cloneDetail.setFrom_org_id(Long.valueOf(this.organization_id));
		cloneDetail.setModuleuuid(cloneUuid);
		cloneDetail.setOrganization_id(Long.valueOf(this.organization_id));
		cloneDetail.setParentuuid(this.parentuuid);
		cloneDetail.setPath(this.path);
		cloneDetail.setPrettyname(this.prettyname);
		cloneDetail.setType(this.type);
		cloneDetail.setVisibility(this.visibility);
		
		// xml: replace old uuid with new uuid;
		Module sourceModule = SitedesignHelper.getModuleFromXml(this.xml);
		if(sourceModule!=null && sourceModule.getAttrGroupList()!=null && sourceModule.getAttrGroupList().size()>0){
			for(AttrGroup g : sourceModule.getAttrGroupList()){
				String newGroupUuid = UUID.randomUUID().toString();
				oldNewUuidMap.put(g.getGroupUuid(), newGroupUuid);
				g.setGroupUuid(newGroupUuid);
				
				if(g.getAttrList()!=null && g.getAttrList().size()>0){
					for(ModuleAttribute a : g.getAttrList()){
						String newAttrUuid = UUID.randomUUID().toString();
						oldNewUuidMap.put(a.getUuid(), newAttrUuid);
						a.setUuid(newAttrUuid);
					}
				}
			}
			
			cloneDetail.setXml(SitedesignHelper.getXmlFromModule(sourceModule));
			
		}
		
		// css: replace old uuid with new uuid;
		String css = this.css;
		// jsp: replace old uuid with new uuid;
		String jsp = this.jsp;
		
		if(oldNewUuidMap.size()>0){
			for(Map.Entry<String, String> id : oldNewUuidMap.entrySet()){
				if(css!=null) css.replaceAll(id.getKey(), id.getValue());
				if(jsp!=null) jsp.replaceAll(id.getKey(), id.getValue());
			}
		}
		cloneDetail.setCss(css);
		cloneDetail.setJsp(jsp);
		
		return cloneDetail;
		
		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleDetail [id=").append(id).append(", moduleuuid=")
				.append(moduleuuid).append(", prettyname=").append(prettyname)
				.append(", description=").append(description).append(", type=")
				.append(type).append(", parentuuid=").append(parentuuid)
				.append(", path=").append(path).append(", organization_id=")
				.append(organization_id).append(", from_org_id=")
				.append(from_org_id).append(", from_module_uuid=")
				.append(from_module_uuid).append(", visibility=")
				.append(visibility).append(", createdate=").append(createdate)
				.append(", creator_id=").append(creator_id).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((css == null) ? 0 : css.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((jsp == null) ? 0 : jsp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((visibility == null) ? 0 : visibility.hashCode());
		result = prime * result + ((xml == null) ? 0 : xml.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleDetail other = (ModuleDetail) obj;
		if (css == null) {
			if (other.css != null)
				return false;
		} else if (!css.equals(other.css))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (jsp == null) {
			if (other.jsp != null)
				return false;
		} else if (!jsp.equals(other.jsp))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (visibility == null) {
			if (other.visibility != null)
				return false;
		} else if (!visibility.equals(other.visibility))
			return false;
		if (xml == null) {
			if (other.xml != null)
				return false;
		} else{
			
			if(other.xml==null) return false;
			
			Module module = SitedesignHelper.getModuleFromXml(this.xml);
			Module otherModule = SitedesignHelper.getModuleFromXml(other.xml);
			
			boolean equalResult = module.equals(otherModule);
			if(module.getThisThatUuids()!=null && module.getThisThatUuids().size()>0){
				addThisThatUuids(module.getThisThatUuids());
			}
			
			return equalResult;
			
			
		}
		return true;
	}

	public Map<String, String> getThisThatUuids() {
		return thisThatUuids;
	}
	private void addThisThatUuids(String thisUuid, String thatUuid){
		if(thisThatUuids==null) thisThatUuids = new HashMap<String, String>();
		thisThatUuids.put(thisUuid, thatUuid);
	}
	private void addThisThatUuids(Map<String, String> theThisThatUuids){
		if(theThisThatUuids!=null && theThisThatUuids.size()>0){
			if(thisThatUuids==null) thisThatUuids = new HashMap<String, String>();
			thisThatUuids.putAll(theThisThatUuids);
		}
	}
	
}
