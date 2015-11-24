package com.bizislife.core.siteDesign.module;

import com.bizislife.core.hibernate.pojo.ModuleDetail;

public enum ModuleAttributeList {

	Boolean(null, null, 
			"<strong>Boolean attribute</strong><br/>Set value true or false", 
			ModuleDetail.Type.all),
	CategoryList(ModuleEntityCategoryListAttribute.class.getName(), "ModuleAttrGeneral", 
			"<strong>Entity category list</strong><br/>This module will only list categories created in product tree. It can be used for top navigation or left navigation in product page.", 
			ModuleDetail.Type.module),
	Color(null, null, 
			"<strong>Color attribute</strong><br/>Pick a color for the value.", 
			ModuleDetail.Type.all),
	Datetime(null, null, 
			"<strong>Date and time attribute</strong><br/>Setup time for the value", 
			ModuleDetail.Type.all),
	Document(null, null, 
			"<strong>Document (pdf, doc, etc.) attribute</strong><br/>Select document for the value.", 
			ModuleDetail.Type.all),
	Link(ModuleLinkAttribute.class.getName(), "ModuleAttrGeneral", 
			"<strong>Link attribute</strong><br/>Setup a link for the value.", 
			ModuleDetail.Type.all),
	Location(null, null, 
			"<strong>Location attribute</strong><br/>Setup google location for the value.", 
			ModuleDetail.Type.all),
	Monetary(ModuleMoneyAttribute.class.getName(), "ModuleAttrGeneral", 
			"<strong>Price attribute</strong><br/>Setup money for the value.", 
			ModuleDetail.Type.all),
	Number(ModuleNumberAttribute.class.getName(), "ModuleAttrGeneral",  
			"<strong>Number attribute, includes integer and float</strong><br/>Setup number for the value.", 
			ModuleDetail.Type.all),
	Phone(null, null, 
			"<strong>Phone attribute</strong><br/>Setup a phone number for the value.", 
			ModuleDetail.Type.all),
	Picture(ModuleImageAttribute.class.getName(), "ModuleAttrGeneral", 
			"<strong>Picture attribute</strong><br/>Setup a picture for the value.", 
			ModuleDetail.Type.all),
	ProductList(ModuleProductListAttribute.class.getName(), "ModuleAttrGeneral", 
			"<strong>Product list</strong><br/>This module will list categories and products created in product tree.",
			ModuleDetail.Type.module),
	Text(ModuleTextAttribute.class.getName(), "ModuleAttrGeneral",
			"<strong>Text attribute</strong><br/>Setup a text string for the value.", 
			ModuleDetail.Type.all),
	Video(null, null, 
			"<strong>Video attribute</strong><br/>Setup a video for the value.", 
			ModuleDetail.Type.all),
	
	;

	private String className;
	private String jspName;
	private String description;
	private ModuleDetail.Type usedFor;
	private ModuleAttributeList(String className, String jspName, String description, ModuleDetail.Type usedFor) {
		this.className = className;
		this.jspName = jspName;
		this.description = description;
		this.usedFor = usedFor;
	}
	
	public String getClassName() {
		return className;
	}
	public String getJspName() {
		return jspName;
	}
	public String getDescription() {
		return description;
	}
	public ModuleDetail.Type getUsedFor() {
		return usedFor;
	}
	
	
}
