package com.bizislife.core.hibernate.dao;

import java.util.*;

import com.bizislife.core.hibernate.pojo.*;

public interface OrganizationDao {
	
	public Long activateOrg(Long orgId);
	
	public void delOrgById(Long id);
	
	public void delOrgProfileById(Long id);
	
	public Organization getBizorg();
	
	public List<Organization> findAllOrgainzations();
	
	public List<Organization> findOrgsByName(String orgName);
	
	public List<Organization> findOrgsBySystemName(String orgSysName);
	
	public Organization getOrgByUuid(String uuid);
	
	public Organization getOrganizationById(Long orgId);
	
	public OrgMeta getOrgMeta(Long orgId);
	
	public OrgMeta getOrgMetaByHostname(String hostname);
	
	public Organizationprofile getOrgProfileById(Long id);
	
	public Organizationprofile getOrgProfileByOrgId(Long id);
	
	public Long saveOrg(Organization org);
	
	public Long saveOrgMeta(OrgMeta orgMeta);
	
	public Long saveOrgProfile(Organizationprofile profile);
 
}
