package com.bizislife.core.siteDesign.module;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleTextAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleTextAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "Text attribute";
	
	private static final long serialVersionUID = 1219447690674375268L;

	private Integer minLength;
	private Integer maxLength;
	private String defaultValue;
	private Boolean textArea;
	
	public ModuleTextAttribute() {
		super();
	}
	public ModuleTextAttribute(String uuid, String name, String title, Boolean visibility,
			String documentation, Boolean required, Boolean array, Integer minLength, Integer maxLength,
			String defaultValue, Boolean textArea) {
		super(uuid, name, title, visibility, documentation, required, array);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.defaultValue = defaultValue;
		this.textArea = textArea;
	}
	
	public Integer getMinLength() {
		return minLength;
	}
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	@JsonIgnore
	public String getDefaultValue() {
		return defaultValue;
	}
	//another method to get the defaultValue for easy jsp access:
	public String getValue(){
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Boolean getTextArea() {
		return textArea;
	}
	public void setTextArea(Boolean textArea) {
		this.textArea = textArea;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleTextAttribute [minLength=");
		builder.append(minLength);
		builder.append(", maxLength=");
		builder.append(maxLength);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", textArea=");
		builder.append(textArea);
		builder.append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((maxLength == null) ? 0 : maxLength.hashCode());
		result = prime * result
				+ ((minLength == null) ? 0 : minLength.hashCode());
		result = prime * result
				+ ((textArea == null) ? 0 : textArea.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleTextAttribute other = (ModuleTextAttribute) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (maxLength == null) {
			if (other.maxLength != null)
				return false;
		} else if (!maxLength.equals(other.maxLength))
			return false;
		if (minLength == null) {
			if (other.minLength != null)
				return false;
		} else if (!minLength.equals(other.minLength))
			return false;
		if (textArea == null) {
			if (other.textArea != null)
				return false;
		} else if (!textArea.equals(other.textArea))
			return false;
		return true;
	}

	
}
