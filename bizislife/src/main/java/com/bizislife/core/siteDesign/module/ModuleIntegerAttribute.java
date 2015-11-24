package com.bizislife.core.siteDesign.module;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@Deprecated
@XStreamAlias("moduleIntegerAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleIntegerAttribute extends ModuleAttribute {
	
	public static final String MODULEATTRIBUTEDESC = "Integer attribute";

	private static final long serialVersionUID = 6082465113869673933L;

	private Integer minValue;
	private Integer maxValue;
	private Integer defaultValue;
	

	public ModuleIntegerAttribute() {
		super();
	}
	public ModuleIntegerAttribute(String uuid, String name, String title, Boolean visibility,
			String documentation, Boolean required, Boolean array, Integer minValue, Integer maxValue, Integer defaultValue) {
		super(uuid, name, title, visibility, documentation, required, array);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.defaultValue = defaultValue;
	}
	
	public Integer getMinValue() {
		return minValue;
	}
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}
	public Integer getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Integer defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleIntegerAttribute [minValue=").append(minValue)
				.append(", maxValue=").append(maxValue)
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
