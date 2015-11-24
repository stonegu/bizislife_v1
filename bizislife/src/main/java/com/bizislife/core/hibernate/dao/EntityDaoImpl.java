package com.bizislife.core.hibernate.dao;

import java.sql.SQLException;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import scala.math.Numeric.LongIsIntegral;

import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.util.BizHibernateTemplate;

@Repository("entityDao")
public class EntityDaoImpl implements EntityDao{

    private static final Logger logger = LoggerFactory.getLogger(EntityDaoImpl.class);

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
	public List<EntityTreeLevelView> findOrgEntityTreeLevelViews(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM EntityTreeLevelView where organization_id = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public Long saveEntityDetail(EntityDetail entityDetail) {
		if(entityDetail!=null){
			if(entityDetail.getId()!=null){
				return entityManager.merge(entityDetail).getId();
//				hibernateTemplate.update(entityDetail);
//				return entityDetail.getId();
			}else{
//				return (Long)hibernateTemplate.save(entityDetail);
				entityManager.persist(entityDetail);
				return entityDetail.getId();
			}
		}
		return null;
	}

	@Override
	public Long saveEntityTreeLevelView(EntityTreeLevelView entityTreeLevelView) {
		if(entityTreeLevelView!=null){
			if(entityTreeLevelView.getId()!=null){
				return entityManager.merge(entityTreeLevelView).getId();
//				hibernateTemplate.update(entityTreeLevelView);
//				return entityTreeLevelView.getId();
			}else{
				entityManager.persist(entityTreeLevelView);
				return entityTreeLevelView.getId();
//				return (Long)hibernateTemplate.save(entityTreeLevelView);
			}
		}
		return null;
	}

	@Override
	public EntityTreeLevelView getProductTreeRootViewLevel(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM EntityTreeLevelView where organization_id = ").append(orgId).append(" and parentuuid is null");
			Query query = entityManager.createQuery(q.toString());
//			List<EntityTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			List<EntityTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}

	@Override
	public EntityDetail getEntityDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM EntityDetail where entityuuid = '").append(uuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<EntityDetail> entityDetails = query.getResultList();
//			List<EntityDetail> entityDetails = hibernateTemplate.find(q.toString());
			if(entityDetails!=null && entityDetails.size()>0){
				return entityDetails.get(0);
			}
		}
		return null;
	}

	@Override
	public int countAllProductsUnderCurrentFolderByCurrentFolderUuid(final String folderUuid, EntityDetail.EntityType type, boolean oneLevel, String sqlQueryToUse) {
		if(StringUtils.isNotBlank(folderUuid)){
			
			StringBuilder q = null;
			
			if(StringUtils.isNotBlank(sqlQueryToUse)){
				q = new StringBuilder(sqlQueryToUse);
			}else{
				//StringBuilder q = new StringBuilder("SELECT count(*) FROM EntityDetail where path is not null AND type='").append(EntityDetail.EntityType.entity.getCode()).append("' AND path like '%").append(uuid).append("%'");
				q = new StringBuilder("SELECT count(*) FROM EntityDetail where");
				if(oneLevel){
					q.append(" parentuuid = '").append(folderUuid).append("'");
				}else{
					q.append(" path is not null AND path like '%").append(folderUuid).append("%'");
				}
				
				if(type!=null){
					q.append(" AND type='").append(type.getCode()).append("'");
				}
				
			}
			
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null){
				return countResult.intValue();
			}
			
		}
		
		return 0;
	}

	@Override
	public int countSubFoldersByCurrentFolderUuid(final String folderUuid, boolean oneLevel) {
		if(StringUtils.isNotBlank(folderUuid)){
			
//			StringBuilder q = new StringBuilder("SELECT count(*) FROM EntityDetail where path is not null AND type='").append(EntityDetail.EntityType.folder.getCode()).append("' AND path like '%").append(folderUuid).append("%'");
			StringBuilder q = new StringBuilder("SELECT count(*) FROM EntityDetail where type='").append(EntityDetail.EntityType.folder.getCode()).append("'");
			if(oneLevel){
				q.append(" AND parentuuid = '").append(folderUuid).append("'");
			}else{
				q.append(" AND path is not null AND path like '%").append(folderUuid).append("%'");
			}
			
			
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM EntityDetail where path is not null AND type='").append(EntityDetail.EntityType.productFolder.getCode()).append("' AND path like '%").append(uuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
		}
		return 0;
	}

	@Override
	public EntityTreeLevelView getEntityTreeLevelViewByParentUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			StringBuilder q = new StringBuilder("FROM EntityTreeLevelView where parentuuid = '").append(parentNodeUuid).append("'");
//			List<EntityTreeLevelView> levelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<EntityTreeLevelView> levelViews = query.getResultList();
			if(levelViews!=null && levelViews.size()>0){
				return levelViews.get(0);
			}
		}
		return null;
	}

	@Override
	public EntityDetail getProductTreeRoot(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM EntityDetail WHERE path IS NULL AND organization_id = ").append(orgId);
//			List<EntityDetail> details = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<EntityDetail> details = query.getResultList();
			if(details!=null && details.size()>0) return details.get(0);
		}
		return null;
	}

	@Override
	public List<EntityDetail> findEntitiesUsingModule(String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder q = new StringBuilder("FROM EntityDetail WHERE moduleuuid = '").append(moduleUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public EntityTreeLevelView getEntityTreeLevelViewHasNode(String nodeuuid) {
		if(StringUtils.isNotBlank(nodeuuid)){
			StringBuilder q = new StringBuilder("FROM EntityTreeLevelView WHERE nodes LIKE '%").append(nodeuuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<EntityTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0) return views.get(0);
		}
		
		return null;
	}

	@Override
	public void delEntityTreeLevelView(Long id) {
		if(id!=null){
			EntityTreeLevelView view = entityManager.find(EntityTreeLevelView.class, id);
			if(view!=null) entityManager.remove(view);
		}
		
	}

	@Override
	public void delEntityDetailById(Long id) {
		if(id!=null){
			EntityDetail detail = entityManager.find(EntityDetail.class, id);
			if(detail!=null) entityManager.remove(detail);
		}
		
	}

	@Override
	public List<NodeDetail> findEntityDetailsUnderFolder(String folderNodeUuid) {
		if(StringUtils.isNotBlank(folderNodeUuid)){
			StringBuilder q = new StringBuilder("FROM EntityDetail WHERE path LIKE '%").append(folderNodeUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<EntityDetail> findEntityDetailsUnderFolderFromIdToTotalReturnNumber(EntityType type,
			String folderNodeUuid, int totalResultsNum, Long beginingIdx, boolean oneLevel, boolean descending) {
		
		StringBuilder q = new StringBuilder("FROM EntityDetail WHERE ");
		
		// for oneLevel
		if(oneLevel){
			q.append("parentuuid = '").append(folderNodeUuid).append("'");
		}else{
			q.append("path LIKE '%").append(folderNodeUuid).append("%'");
		}
		
		// FOR TYPE
		if(type!=null){
			q.append(" AND type ='").append(type.getCode()).append("'");
		}
		
		if(descending){
			// for beginingIdx
			if(beginingIdx!=null && beginingIdx.longValue()>0){
				q.append(" AND id<=").append(beginingIdx.longValue());
			}
			
			// ORDER
			q.append(" ORDER BY id DESC");
			
		}else{
			// for beginingIdx
			if(beginingIdx!=null && beginingIdx.longValue()>0){
				q.append(" AND id>=").append(beginingIdx.longValue());
			}
			
			// ORDER
			q.append(" ORDER BY id ASC");
			
		}
		
		// for totalResultsNum
		Query query = entityManager.createQuery(q.toString());
		query.setMaxResults(totalResultsNum);
		
		return query.getResultList();
	}

	@Override
	public List<NodeDetail> findEntityDetailsUnderFolderFromOffsetToTotalReturnNumber(
			EntityType type, String folderNodeUuid, int totalResultsNum,
			int offset, boolean oneLevel, boolean descending, String sqlQueryToUse) {
		
//		if(offset<0) offset = 0;
		

		
		StringBuilder q = null;
		if(StringUtils.isNotBlank(sqlQueryToUse)){
			q = new StringBuilder(sqlQueryToUse);
		}else {
			q = new StringBuilder("FROM EntityDetail WHERE ");
			
			// for oneLevel
			if(oneLevel){
				q.append("parentuuid = '").append(folderNodeUuid).append("'");
			}else{
				q.append("path LIKE '%").append(folderNodeUuid).append("%'");
			}
			
			// FOR TYPE
			if(type!=null){
				q.append(" AND type ='").append(type.getCode()).append("'");
			}
			
			if(descending){
				// ORDER
				q.append(" ORDER BY id DESC");
			}else{
				// ORDER
				q.append(" ORDER BY id ASC");
			}
		}
		
		// for totalResultsNum
		Query query = entityManager.createQuery(q.toString());
		if(offset>=0){
			query.setFirstResult(offset);
		}
		if(totalResultsNum>0){
			query.setMaxResults(totalResultsNum);
		}
		
		return query.getResultList();
	}

	@Override
	public List<EntityDetail> findOrgEntityDetails(Long orgId, EntityDetail.EntityType entityType) {
		StringBuilder q = new StringBuilder("FROM EntityDetail where organization_id=").append(orgId);
		if(entityType!=null){
			q.append(" and type = '").append(entityType.getCode()).append("'");
		}
		Query query = entityManager.createQuery(q.toString());
		
		return query.getResultList();
	}

	@Override
	public List<NodeDetail> findEntityDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf) {
		if(includeFold || includeLeaf){
			if(StringUtils.isNotBlank(folderUuid)){
				StringBuilder q = new StringBuilder("FROM EntityDetail where parentuuid = '").append(folderUuid).append("'");
				if(!includeFold || !includeLeaf){
					if(includeFold){ // folder only
						q.append(" and type = '").append(EntityDetail.EntityType.folder.getCode()).append("'");
					}else{ // leaf only
						q.append(" and type != '").append(EntityDetail.EntityType.folder.getCode()).append("'");
					}
				}
				
				Query query = entityManager.createQuery(q.toString());
				return query.getResultList();
			}
			
		}
		
		return null;
	}

//	@Override
//	public EntityTreeLevelView getProductTreeViewLevel(String parentNodeUuid) {
//		if(StringUtils.isNotBlank(parentNodeUuid)){
//			
//			StringBuilder q = new StringBuilder("FROM EntityTreeLevelView where parentuuid = '").append(parentNodeUuid).append("'");
//			List<EntityTreeLevelView> levelViews = hibernateTemplate.find(q.toString());
//			if(levelViews!=null && levelViews.size()>0){
//				return levelViews.get(0);
//			}
//		}
//		return null;
//	}

    
    
    
}
