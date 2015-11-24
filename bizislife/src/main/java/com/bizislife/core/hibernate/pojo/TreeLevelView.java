package com.bizislife.core.hibernate.pojo;

import java.util.Date;

public interface TreeLevelView extends PojoInterface{
	public String getParentuuid();
	public String getNodes();
	public void setNodes(String nodes);
	public void setParentuuid(String parentuuid);
	public void setCreatedate(Date createdate);
	public void setOrganization_id(Long organization_id);
	public void setCreator_id(Long creator_id);
}
