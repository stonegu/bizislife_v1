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
@Table(name="moduleinstance")
public class ModuleInstance implements PojoInterface, ModuleInstanceInterface, Serializable{
	
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

	public static enum IsDefault{
		yes("1"),
		no("0"),
		;
		private String code;

		private IsDefault(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public IsDefault fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(IsDefault v : IsDefault.values()){
					if(v.getCode().equals(code.trim())){
						return v;
					}
				}
			}
			return null;
		}
	}
	
	private static final long serialVersionUID = -8363449463893541264L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="moduleinstanceuuid")
	private String moduleinstanceuuid;

	@Column(name="name")
	private String name;

	@Column(name="moduleuuid")
	private String moduleuuid;

	@Column(name="type")
	private String type;

	@Column(name="instance")
	private String instance;

	@Column(name="orgid")
	private Long orgid;

	@Column(name="visibility")
	private String visibility;
	
	@Column(name="isdefault")
	private String isdefault;

	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="creator_id")
	private Long creator_id;
	
	public ModuleInstance() {
		super();
	}

	public ModuleInstance(Long id, String moduleinstanceuuid, String name,
			String moduleuuid, String type, String instance, Long orgid,
			String visibility, String isdefault, Date createdate, Long creator_id) {
		super();
		this.id = id;
		this.moduleinstanceuuid = moduleinstanceuuid;
		this.name = name;
		this.moduleuuid = moduleuuid;
		this.type = type;
		this.instance = instance;
		this.orgid = orgid;
		this.visibility = visibility;
		this.isdefault = isdefault;
		this.createdate = createdate;
		this.creator_id = creator_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModuleinstanceuuid() {
		return moduleinstanceuuid;
	}

	public String getUuid() {
		return moduleinstanceuuid;
	}
	
	public void setModuleinstanceuuid(String moduleinstanceuuid) {
		this.moduleinstanceuuid = moduleinstanceuuid;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstance() {
		return instance;
	}
	
	public String getxml() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
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

	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleInstance [id=").append(id)
				.append(", moduleinstanceuuid=").append(moduleinstanceuuid)
				.append(", name=").append(name).append(", moduleuuid=")
				.append(moduleuuid).append(", type=").append(type)
				.append(", instance=").append(instance).append(", orgid=")
				.append(orgid).append(", visibility=").append(visibility)
				.append(", isdefault=").append(isdefault)
				.append(", createdate=").append(createdate)
				.append(", creator_id=").append(creator_id).append("]");
		return builder.toString();
	}


}
