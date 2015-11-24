package com.bizislife.core.hibernate.dao;

import java.util.*;

import com.bizislife.core.hibernate.pojo.*;

public interface GroupDao {
	
	public void delGroupById(Long id);
	
	/**
	 * @param orgId
	 * @return return all global groups for all organizations if orgId is null. <br/>
	 * return all global groups for organization if orgId is not null.
	 * 
	 */
	public List<Accountgroup> findGlobalGroups(Long orgId);
	
	public List<Accountgroup> findGroupsByIds(Long[] groupids);
	
	public List<Accountgroup> findGroupsByType(Accountgroup.GroupType type, Long orgId);
	
	/**
	 * @param orgId
	 * @return all org's groups
	 */
	public List<Accountgroup> findOrgGroups(Long orgId);
	
	/**
	 * @param orgId
	 * @return return all private groups for all organizations if orgId is null. <br/>
	 * return all private groups for organization if orgId is not null.
	 * 
	 */
	public List<Accountgroup> findPrivateGroups(Long orgId);
	
//	public void groupVisibleToOrgs(Long groupId, Long[] orgIds);
	
	public Accountgroup getGroup(Long id);
	
	public Accountgroup getGroupByUuid(String groupuuid);
	
	public Long saveGroup(Accountgroup group);
	
}
