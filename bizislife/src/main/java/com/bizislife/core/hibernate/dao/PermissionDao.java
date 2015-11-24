package com.bizislife.core.hibernate.dao;

import java.util.List;

import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;

public interface PermissionDao {
	
	public void delPermissionById(Long id);
	
	public List<PermissionedStuff> findPermissionedStuffForStuff(String stuffUuid);

	public Permission getPermissionById(Long id);
	
	public Permission getPermissionByUuid(String uuid);
	
	public Long savePermission(Permission permission);
	
	public Long savePermissionedStuff(PermissionedStuff stuff);
	
	public Long saveOrgCanJoin(OrgCanJoin ocj);
	
}
