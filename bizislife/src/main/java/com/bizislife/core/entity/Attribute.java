package com.bizislife.core.entity;

import java.util.Date;

public interface Attribute {
	public AttributeType getType();
	public String getUuid();
	public void setUuid(String uuid);
	public String getTitle();
	public void setTitle(String title);
	public Object getValue();
	public void setValue(Object value);
	
	public Date getEditDate();
	public void setEditDate(Date date);
	
	public String getDataLink();
	public void setDataLink(String link);
	
	public String toJson();
}
