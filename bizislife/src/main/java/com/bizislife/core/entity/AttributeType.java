package com.bizislife.core.entity;

public enum AttributeType {
	IMAGE("image"),
	// for integer only
	INTEGER("integer"),
	// for real number (floating point number: float & double)
	REAL("real"),

	STRING("string"),
	;
	
	private String typeCode;

	private AttributeType(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCode() {
		return typeCode;
	}
	

}
