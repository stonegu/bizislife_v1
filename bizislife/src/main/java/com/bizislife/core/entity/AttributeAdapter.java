package com.bizislife.core.entity;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class AttributeAdapter implements Attribute{
	private String uuid;
	private String title;
	
	private Date editDate;
	private String dataLink;

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getDataLink() {
		return dataLink;
	}

	public void setDataLink(String dataLink) {
		this.dataLink = dataLink;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("title", title).append("uuid", uuid).toString();
    }
}
