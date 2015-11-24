package com.bizislife.core.siteDesign.module;

import java.math.BigDecimal;

@Deprecated
public class ModulePriceAttribute extends ModuleNumberAttribute{
	public static final String MODULEATTRIBUTEDESC = "Price attribute";

	
	public ModulePriceAttribute() {
		super();
	}

	// default price attribute: array - false; scale: 2
	public ModulePriceAttribute(String uuid, String name, String title,
			Boolean visibility, String documentation, Boolean required,
			BigDecimal minValue, BigDecimal maxValue,
			BigDecimal defaultValue) {
		super(uuid, name, title, visibility, documentation, required, false, minValue,
				maxValue, defaultValue, 2);
	}
	

}
