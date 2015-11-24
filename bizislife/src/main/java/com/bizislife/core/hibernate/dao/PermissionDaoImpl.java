package com.bizislife.core.hibernate.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;

@Repository("permissionDao")
public class PermissionDaoImpl implements PermissionDao{
    private static final Logger logger = LoggerFactory.getLogger(PermissionDaoImpl.class);
    
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private DaoUtil daoUtil;

	@Override
	public Long savePermission(Permission permission) {
		if(permission.getId()!=null){
//			Set<PermissionedStuff> permissionedStuffs = (Set<PermissionedStuff>)daoUtil.getRelationshipReferences(permission.getPermissionedStuffs()); 
//			permission.setPermissionedStuffs(permissionedStuffs);
			
			return entityManager.merge(permission).getId();
		}else{
			Set<PermissionedStuff> permissionedStuffs = (Set<PermissionedStuff>)daoUtil.getRelationshipReferences(permission.getPermissionedStuffs());
			
			permission.removeAllPermissionedStuff();
			if(permissionedStuffs!=null && permissionedStuffs.size()>0){
				for(PermissionedStuff ps : permissionedStuffs){
					permission.addPermissionedStuff(ps);
				}
			}
			
//			permission.setPermissionedStuffs(permissionedStuffs);
			
			entityManager.persist(permission);
			return permission.getId();
		}
	}

	@Override
	public Permission getPermissionByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM Permission where uuid = '").append(uuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<Permission> permissions = query.getResultList();
			if(permissions!=null && permissions.size()>0){
				return permissions.get(0);
			}
		}
		return null;
	}

	@Override
	public Long savePermissionedStuff(PermissionedStuff stuff) {
		if(stuff!=null){
			if(stuff.getId()!=null){
				return entityManager.merge(stuff).getId();
			}else{
				entityManager.persist(stuff);
				return stuff.getId();
			}
		}
		
		return null;
		
	}

	@Override
	public Permission getPermissionById(Long id) {
		if(id!=null){
			return entityManager.find(Permission.class, id);
		}
		return null;
	}

	@Override
	public List<PermissionedStuff> findPermissionedStuffForStuff(String stuffUuid) {
		if(StringUtils.isNotBlank(stuffUuid)){
			StringBuilder q = new StringBuilder("FROM PermissionedStuff where pointuuid = '").append(stuffUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		
		return null;
	}

	@Override
	public Long saveOrgCanJoin(OrgCanJoin ocj) {
		if(ocj!=null){
			if(ocj.getId()!=null){
				return entityManager.merge(ocj).getId();
			}else{
				entityManager.persist(ocj);
				return ocj.getId();
			}
		}
		
		return null;
	}

	@Override
	public void delPermissionById(Long id) {
		if(id!=null){
			Permission perm = getPermissionById(id);
			
			if(perm!=null){
//				hibernateTemplate.delete(contact);
				entityManager.remove(perm);
			}
		}
	}


}
