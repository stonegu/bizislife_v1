package com.bizislife.core.hibernate.dao;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountgroup.GroupType;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Permission;

@Repository("groupDao")
//@Transactional
public class GroupDaoImpl implements GroupDao{
    private static final Logger logger = LoggerFactory.getLogger(GroupDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    @Autowired
    DaoUtil daoUtil;
    
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long saveGroup(Accountgroup group) {
		if(group!=null){
			if(group.getId()!=null){
//				hibernateTemplate.update(group);
//				return group.getId();
				return entityManager.merge(group).getId();
			}else{
				Set<Permission> permissions = (Set<Permission>)daoUtil.getRelationshipReferences(group.getPermissions());
				group.removeAllPermissions();
				if(permissions!=null && permissions.size()>0){
					for(Permission p : permissions){
						group.addPermission(p);
					}
				}
				
				Set<OrgCanJoin> orgCanJoins = (Set<OrgCanJoin>)daoUtil.getRelationshipReferences(group.getOrgCanJoins());
				group.removeAllOrgCanJoin();
				if(orgCanJoins!=null && orgCanJoins.size()>0){
					for(OrgCanJoin j : orgCanJoins){
						group.addOrgCanJoin(j);
					}
				}
				
//				group.setPermissions(permissions);
				
				entityManager.persist(group);
				return group.getId();
			}
		}
		
		return null;
	}

	@Override
	public List<Accountgroup> findGlobalGroups(Long orgId) {
		StringBuilder q = new StringBuilder("FROM Accountgroup where accesslevel = ").append(Accountgroup.GroupAccessLevel.Global.getCode());
		if(orgId!=null){
			q.append(" and organization_id=").append(orgId);
		}
		
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
		
	}

	@Override
	public List<Accountgroup> findOrgGroups(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM Accountgroup where organization_id = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<Accountgroup> findGroupsByIds(Long[] groupids) {
		if(groupids!=null && groupids.length>0){
			StringBuilder q = new StringBuilder("FROM Accountgroup where id in (");
			int size = groupids.length;
			for(Long id: groupids){
				size--;
				q.append(id);
				if(size>0) q.append(",");
			}
			q.append(")");
			
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}

//	@Override
//	public void groupVisibleToOrgs(Long groupId, Long[] orgIds) {
//		if(groupId!=null && orgIds!=null && orgIds.length>0){
//			Accountgroup group = entityManager.find(Accountgroup.class, groupId);
//			Set<VisibleOrg> orgs = new HashSet<VisibleOrg>();
//			Organization o = null;
//			for(Long id : orgIds){
//				o = entityManager.find(Organization.class, id);
//				if(o!=null) orgs.add(new VisibleOrg(null, o.getId()));
//			}
//			if(group!=null && orgs.size()>0){
//				group.setVisibleOrgs(orgs);
//				entityManager.merge(group);
//			}
//		}
//		
//	}

	@Override
	public List<Accountgroup> findGroupsByType(GroupType type, Long orgId) {
		if(type!=null){
			StringBuilder q = new StringBuilder("FROM Accountgroup where grouptype = '").append(type.name()).append("'");
			if(orgId!=null){
				q.append(" and organization_id = ").append(orgId);
			}
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<Accountgroup> findPrivateGroups(Long orgId) {
		StringBuilder q = new StringBuilder("FROM Accountgroup where accesslevel = ").append(Accountgroup.GroupAccessLevel.Private.getCode());
		if(orgId!=null){
			q.append(" and organization_id=").append(orgId);
		}
		
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}

	@Override
	public Accountgroup getGroup(Long id) {
		if(id!=null){
			return entityManager.find(Accountgroup.class, id);
		}
		
		return null;
	}

	@Override
	public void delGroupById(Long id) {
		if(id!=null){
			Accountgroup group = entityManager.find(Accountgroup.class, id);
			if(group!=null){
				entityManager.remove(group);
			}
		}
	}

	@Override
	public Accountgroup getGroupByUuid(String groupuuid) {
		if(StringUtils.isNotBlank(groupuuid)){
			StringBuilder q = new StringBuilder("FROM Accountgroup where uuid = '").append(groupuuid).append("'");
//			List<Account> accts = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Accountgroup> groups = query.getResultList();
			if(groups!=null && groups.size()>0){
				return groups.get(0);
			}
		}
		return null;
	}


}
