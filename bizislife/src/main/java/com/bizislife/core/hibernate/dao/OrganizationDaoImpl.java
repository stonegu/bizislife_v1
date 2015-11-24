package com.bizislife.core.hibernate.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.core.hibernate.pojo.OrgMeta;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.Organizationprofile;
import com.bizislife.util.BizHibernateTemplate;
import com.bizislife.util.WebUtil;

@Repository("organizationDao")
//@Transactional
public class OrganizationDaoImpl implements OrganizationDao{

    private static final Logger logger = LoggerFactory.getLogger(OrganizationDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Organization> findAllOrgainzations() {
		String q = "FROM Organization";
//		return (List<Organization>)hibernateTemplate.find(q);
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}

	@Override
	public Organization getOrganizationById(Long orgId) {
		if(orgId!=null){
//			return hibernateTemplate.get(Organization.class, orgId);
			return entityManager.find(Organization.class, orgId);
		}
		return null;
	}

	@Override
	public Long saveOrg(Organization org) {
		// only do the very basic check
		if(org!=null && StringUtils.isNotBlank(org.getOrguuid())){
//			return (Long)hibernateTemplate.save(org);
			if(org.getId()!=null){
				return entityManager.merge(org).getId();
			}else{
				entityManager.persist(org);
				return org.getId();
			}
		}
		return null;
	}

	@Override
	public Long saveOrgProfile(Organizationprofile profile) {
		// only do the very basic check
		if(profile!=null && profile.getOrganization_id()!=null){
//			return (Long)hibernateTemplate.save(profile);
			if(profile.getId()!=null){
				return entityManager.merge(profile).getId();
			}else{
				entityManager.persist(profile);
				return profile.getId();
			}
		}
		return null;
	}

	@Override
	public Long saveOrgMeta(OrgMeta meta) {
		if(meta!=null && meta.getOrgid()!=null){
			if(meta.getId()!=null){
				return entityManager.merge(meta).getId();
			}else{
				entityManager.persist(meta);
				return meta.getId();
			}
		}
		
		return null;
	}

	@Override
	public void delOrgById(Long id) {
		if(id!=null){
//			Organization org = hibernateTemplate.get(Organization.class, id);
			Organization org = entityManager.find(Organization.class, id);
			if(org!=null){
//				hibernateTemplate.delete(org);
				entityManager.remove(org);
			}
		}
	}

	@Override
	public void delOrgProfileById(Long id) {
		if(id!=null){
//			Organizationprofile profile = hibernateTemplate.get(Organizationprofile.class, id);
			Organizationprofile profile = entityManager.find(Organizationprofile.class, id);
			if(profile!=null){
//				hibernateTemplate.delete(profile);
				entityManager.remove(profile);
			}
		}
	}

	@Override
	public Organizationprofile getOrgProfileById(Long id) {
		if(id!=null){
//			return hibernateTemplate.get(Organizationprofile.class, id);
			return entityManager.find(Organizationprofile.class, id);
		}
		return null;
	}

	@Override
	public OrgMeta getOrgMeta(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM OrgMeta where orgid = ").append(orgId);
//			List<Organizationprofile> orgProfiles = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<OrgMeta> orgMeta = query.getResultList();
			if(orgMeta!=null && orgMeta.size()>0) return orgMeta.get(0);
		}
		return null;
	}

	@Override
	public OrgMeta getOrgMetaByHostname(String hostname) {
		if(StringUtils.isNotBlank(hostname)){
			// remove port if possible
//			if(hostname.indexOf(":")>-1){
//				hostname = hostname.substring(0, hostname.indexOf(":"));
//			}
			// remove subdomain or www if possible
//			String[] hostnameSegments = hostname.split(".");
//			if(hostnameSegments.length>2){
//				int length = hostnameSegments.length;
//				StringBuilder hostnameWithoutSubDomain = new StringBuilder();
//				hostnameWithoutSubDomain.append(hostnameSegments[length-2]).append(".").append(hostnameSegments[length-1]);
//				hostname = hostnameWithoutSubDomain.toString();
//			}
			
			// FROM bizislifeii.orgmeta where domains like "%127.0.0.1%"
			StringBuilder q = new StringBuilder("FROM OrgMeta where domains like '%").append(hostname).append("%'");
//			List<Organizationprofile> orgProfiles = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<OrgMeta> orgMetas = query.getResultList();
			if(orgMetas!=null && orgMetas.size()>0){
				for(OrgMeta om : orgMetas){
					if(StringUtils.isNotBlank(om.getDomains())){
						String[] domains = om.getDomains().split(",");
						if(WebUtil.isContainDomain(domains, hostname)){
							return om;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Long activateOrg(Long orgId) {
		if(orgId!=null){
//			Organization org = hibernateTemplate.get(Organization.class, orgId);
			Organization org = entityManager.find(Organization.class, orgId);
			if(org!=null){
				org.setActivatedate(new Date());
//				hibernateTemplate.update(org);
				entityManager.flush();
				return orgId;
			}
		}
		return null;
	}

	@Override
	public List<Organization> findOrgsByName(String orgName) {
		if(StringUtils.isNotBlank(orgName)){
			StringBuilder q = new StringBuilder("FROM Organization where orgname =:orgName");
//			return hibernateTemplate.find(q.toString(), orgName);
			Query query = entityManager.createQuery(q.toString()).setParameter("orgName", orgName);
			return query.getResultList();
		}
		
		return null;
	}

	@Override
	public Organization getOrgByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM Organization where orguuid = '").append(uuid.trim()).append("'");
//			List<Organization> orgs = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Organization> orgs = query.getResultList();
			if(orgs!=null && orgs.size()>0) return orgs.get(0);
		}
		return null;
	}

	@Override
	public Organizationprofile getOrgProfileByOrgId(Long id) {
		if(id!=null){
			StringBuilder q = new StringBuilder("FROM Organizationprofile where organization_id = ").append(id);
//			List<Organizationprofile> orgProfiles = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Organizationprofile> orgProfiles = query.getResultList();
			if(orgProfiles!=null && orgProfiles.size()>0) return orgProfiles.get(0);
		}
		return null;
	}

	@Override
	public Organization getBizorg() {
//		List<Organization> orgs = hibernateTemplate.find("FROM Organization where orglevel = '0'");
		Query query = entityManager.createQuery("FROM Organization where orglevel = '0'");
		List<Organization> orgs = query.getResultList();
		if(orgs!=null && orgs.size()>0) return orgs.get(0);
		return null;
	}

	@Override
	public List<Organization> findOrgsBySystemName(String orgSysName) {
		if(StringUtils.isNotBlank(orgSysName)){
			StringBuilder q = new StringBuilder("FROM Organization where orgsysname =:orgSysName");
//			return hibernateTemplate.find(q.toString(), orgSysName);
			Query query = entityManager.createQuery(q.toString()).setParameter("orgSysName", orgSysName);
			return query.getResultList();
		}
		return null;
	}

}
