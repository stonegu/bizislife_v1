package com.bizislife.core.hibernate.pojo;

public interface NodeDetail {
	public String getNodeUuid();
	public String getPath();
	public void setPath(String path);
	public String getParentuuid();
	public void setParentuuid(String parentuuid);
	public Long getOrganization_id();
}
