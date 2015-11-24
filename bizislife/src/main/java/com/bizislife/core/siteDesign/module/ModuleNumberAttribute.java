package com.bizislife.core.siteDesign.module;

import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleNumberAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleNumberAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "Number attribute";

	private static final long serialVersionUID = -5649362159926398531L;

	private BigDecimal minValue;
	private BigDecimal maxValue;
	private BigDecimal defaultValue;
	private Integer scale; // how many digits after the decimal. 2: 0.00; 3: 0.000; 0: 0
	
	public ModuleNumberAttribute() {
		super();
	}

	public ModuleNumberAttribute(String uuid, String name, String title, Boolean visibility,
			String documentation, Boolean required, Boolean array,
			BigDecimal minValue, BigDecimal maxValue,
			BigDecimal defaultValue, Integer scale) {
		super(uuid, name, title, visibility, documentation, required, array);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.defaultValue = defaultValue;
		this.scale = scale;
	}

	public BigDecimal getMinValue() {
		return minValue;
	}

	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	public BigDecimal getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	@JsonIgnore
	public BigDecimal getDefaultValue() {
		return defaultValue;
	}
	//another method to get the defaultValue for easy jsp access:
	public BigDecimal getValue(){
		return defaultValue;
	}

	public void setDefaultValue(BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleNumberAttribute [minValue=");
		builder.append(minValue);
		builder.append(", maxValue=");
		builder.append(maxValue);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", scale=");
		builder.append(scale);
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
				+ ((maxValue == null) ? 0 : maxValue.hashCode());
		result = prime * result
				+ ((minValue == null) ? 0 : minValue.hashCode());
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
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
		ModuleNumberAttribute other = (ModuleNumberAttribute) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (maxValue == null) {
			if (other.maxValue != null)
				return false;
		} else if (!maxValue.equals(other.maxValue))
			return false;
		if (minValue == null) {
			if (other.minValue != null)
				return false;
		} else if (!minValue.equals(other.minValue))
			return false;
		if (scale == null) {
			if (other.scale != null)
				return false;
		} else if (!scale.equals(other.scale))
			return false;
		return true;
	}


}
