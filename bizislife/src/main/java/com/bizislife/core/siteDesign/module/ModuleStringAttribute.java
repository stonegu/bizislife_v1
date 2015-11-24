package com.bizislife.core.siteDesign.module;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@Deprecated
@XStreamAlias("moduleStringAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleStringAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "String attribute";

	private static final long serialVersionUID = 5222180140850974816L;

	private Integer minLength;
	private Integer maxLength;
	private String defaultValue;
	
	public ModuleStringAttribute() {
		super();
	}
	public ModuleStringAttribute(String uuid, String name, String title, Boolean visibility,
			String documentation, Boolean required, Boolean array, Integer minLength, Integer maxLength, String defaultValue) {
		super(uuid, name, title, visibility, documentation, required, array);

		this.minLength = minLength;
		this.maxLength = maxLength;
		this.defaultValue = defaultValue;
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleStringAttr [minLength=").append(minLength)
				.append(", maxLength=").append(maxLength)
				.append(", defaultValue=").append(defaultValue)
				.append(", name=").append(getName())
				.append(", title=").append(getTitle())
				.append(", editable=").append(getEditable())
				.append(", documentation=").append(getDocumentation())
				.append(", required=").append(getRequired())
				.append(", array=").append(getArray()).append("]");
		return builder.toString();
	}
	
}
