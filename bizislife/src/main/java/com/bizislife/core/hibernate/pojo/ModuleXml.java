package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

@Deprecated
@Entity
@Table(name="modulexml")
public class ModuleXml implements PojoInterface, Serializable {
	
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
	
	
	
	private static final long serialVersionUID = -2800371971997096549L;

	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="moduleuuid")
	private String moduleuuid;

	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;

	@Column(name="xml")
	private String xml;
	
	@Column(name="orgid")
	Long orgid;
	
	@Column(name="visibility")
	String visibility;

	public ModuleXml() {
		super();
	}

	public ModuleXml(Long id, String moduleuuid, String name, String description, String xml, Long orgid, String visibility) {
		super();
		this.id = id;
		this.moduleuuid = moduleuuid;
		this.name = name;
		this.description = description;
		this.xml = xml;
		this.orgid = orgid;
		this.visibility = visibility;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public String getModuleuuid() {
		return moduleuuid;
	}

	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleXml [id=").append(id).append(", name=")
				.append(name).append(", xml=").append(xml).append("]");
		return builder.toString();
	}

}
