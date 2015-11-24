package com.bizislife.core.siteDesign.module;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

//@XStreamAlias("moduleAttr")
public class ModuleAttribute implements Serializable{
	
	@XStreamOmitField
	private static final long serialVersionUID = -109856145426119644L;
	
	private String type; // keep the class name here: ModuleIntegerAttr, ModuleStringAttr
	
	private String moduleAttrUuid; // used for instance, this uuid is attrUuid from module, which is indicated where this attr instance from.
	
	private String uuid;
	private String from_attrUuid; // used for copy or clone: from which moduleAttribute.
	private String name; // it will be used in form's element name, like input's name.
	private String title; // all attribute has a title, which is editable in instance. (every instance can have a distinguish title)
	
	// true: the attribute's values are editable in instance. false: the attribute's values are not editable in instance. 
	// Default is true
	private Boolean editable; 
	private String documentation; // the document to description the moduleDetail's attribute. Not editable in instance.
	
	// value required: true - must provide value for the attribute in instance, or system will give a default one: boolean - false, number - 0, text - "default value required"...
	// Default is false;
	private Boolean required;
	private Boolean array; // true: attribute can be array. Default is false  
	
	public ModuleAttribute() {
		super();
	}
	public ModuleAttribute(String uuid, String name, String title, Boolean editable,
			String documentation, Boolean required, Boolean array) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.title = title;
		this.editable = editable;
		this.documentation = documentation;
		this.required = required;
		this.array = array;
	}
	
	@JsonIgnore
	public String getModuleAttrUuid() {
		return moduleAttrUuid;
	}
	public void setModuleAttrUuid(String moduleAttrUuid) {
		this.moduleAttrUuid = moduleAttrUuid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@JsonIgnore
	public String getFrom_attrUuid() {
		return from_attrUuid;
	}
	public void setFrom_attrUuid(String from_attrUuid) {
		this.from_attrUuid = from_attrUuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@JsonIgnore
	public Boolean getEditable() {
		return editable;
	}
	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	public String getDocumentation() {
		return documentation;
	}
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	@JsonIgnore
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
	public Boolean getArray() {
		return array;
	}
	public void setArray(Boolean array) {
		this.array = array;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleAttribute [type=");
		builder.append(type);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", title=");
		builder.append(title);
		builder.append(", editable=");
		builder.append(editable);
		builder.append(", documentation=");
		builder.append(documentation);
		builder.append(", required=");
		builder.append(required);
		builder.append(", array=");
		builder.append(array);
		builder.append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((array == null) ? 0 : array.hashCode());
		result = prime * result
				+ ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((required == null) ? 0 : required.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((editable == null) ? 0 : editable.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) { // two attributes have same structure
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		ModuleAttribute other = (ModuleAttribute) obj;
		
		if (array == null) {
			if (other.array != null)
				return false;
		} else if (!array.equals(other.array))
			return false;
		
		if (documentation == null) {
			if (other.documentation != null)
				return false;
		} else if (!documentation.equals(other.documentation))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;
		
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		
		if (editable == null) {
			if (other.editable != null)
				return false;
		} else if (!editable.equals(other.editable))
			return false;
		
		return true;
	}

}
