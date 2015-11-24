package com.bizislife.core.hibernate.dao;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.EntityTreeLevelView;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.MediaTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.util.BizHibernateTemplate;

@Repository("mediaDao")
public class MediaDaoImpl implements MediaDao{

    private static final Logger logger = LoggerFactory.getLogger(MediaDaoImpl.class);

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
	public MediaTreeLevelView getMediaTreeRootViewLevel(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM MediaTreeLevelView where organization_id = ").append(orgId).append(" and parentuuid is null");
//			List<MediaTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<MediaTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}

	@Override
	public MediaTreeLevelView getMediaTreeLevelViewByParentUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			StringBuilder q = new StringBuilder("FROM MediaTreeLevelView where parentuuid = '").append(parentNodeUuid).append("'");
//			List<MediaTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<MediaTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}

	@Override
	public MediaDetail getMediaDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM MediaDetail where mediauuid = '").append(uuid).append("'");
//			List<MediaDetail> mediaDetails = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<MediaDetail> mediaDetails = query.getResultList();
			if(mediaDetails!=null && mediaDetails.size()>0){
				return mediaDetails.get(0);
			}
		}
		return null;
	}

	@Override
	public int countSubFoldersByCurrentFolderUuid(final String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("SELECT count(*) FROM MediaDetail where path is not null AND nodetype='").append(MediaDetail.MediaType.folder.getCode()).append("' AND path like '%").append(uuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM MediaDetail where path is not null AND nodetype='").append(MediaDetail.MediaType.folder.getCode()).append("' AND path like '%").append(uuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
		}
		return 0;
	}

	@Override
	public int countAllMediasUnderCurrentFolderByCurrentFolderUuid(final String uuid, final MediaDetail.MediaType type) {
		if(StringUtils.isNotBlank(uuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM MediaDetail where path is not null AND nodetype='").append(type.getCode()).append("' AND path like '%").append(uuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM MediaDetail where path is not null AND nodetype='").append(type.getCode()).append("' AND path like '%").append(uuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		
		return 0;
	}

	@Override
	public List<MediaTreeLevelView> findOrgMediaTreeLevelViews(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM MediaTreeLevelView where organization_id = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public Long saveMediaDetail(MediaDetail mediaDetail) {
		if(mediaDetail!=null){
			if(mediaDetail.getId()!=null){
//				hibernateTemplate.update(mediaDetail);
//				return mediaDetail.getId();
				return entityManager.merge(mediaDetail).getId();
			}else{
//				return (Long)hibernateTemplate.save(mediaDetail);
				entityManager.persist(mediaDetail);
				return mediaDetail.getId();
			}
		}
		return null;
	}

	@Override
	public Long saveMediaTreeLevelView(MediaTreeLevelView mediaTreeLevelView) {
		if(mediaTreeLevelView!=null){
			if(mediaTreeLevelView.getId()!=null){
//				hibernateTemplate.update(mediaTreeLevelView);
//				return mediaTreeLevelView.getId();
				return entityManager.merge(mediaTreeLevelView).getId();
			}else{
//				return (Long)hibernateTemplate.save(mediaTreeLevelView);
				entityManager.persist(mediaTreeLevelView);
				return mediaTreeLevelView.getId();
			}
		}
		return null;
	}

	@Override
	public void delMediaDetailById(Long id) {
		if(id!=null){
			MediaDetail media = entityManager.find(MediaDetail.class, id);
			if(media!=null){
				entityManager.remove(media);
			}
		}
		
	}

	@Override
	public void delMediaTreeLevelView(Long id) {
		if(id!=null){
			MediaTreeLevelView view = entityManager.find(MediaTreeLevelView.class, id);
			if(view!=null){
				entityManager.remove(view);
			}
		}
	}

	@Override
	public MediaTreeLevelView getMediaTreeLevelViewHasNode(String nodeUuid) {
		if(StringUtils.isNotBlank(nodeUuid)){
			StringBuilder q = new StringBuilder("FROM MediaTreeLevelView WHERE nodes LIKE '%").append(nodeUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<MediaTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0) return views.get(0);
		}
		
		return null;
	}

	@Override
	public List<NodeDetail> findMedialDetailsUnderFolder(String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
			StringBuilder q = new StringBuilder("FROM MediaDetail WHERE path LIKE '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}
	
	@Override
	public List<NodeDetail> findMedialDetailsInFolder(String folderUuid, boolean includeFolder, boolean includeLeaf) {
		
		if(includeFolder || includeLeaf){
			if(StringUtils.isNotBlank(folderUuid)){
				StringBuilder q = new StringBuilder("FROM MediaDetail where parentuuid = '").append(folderUuid).append("'");
				if(!includeFolder || !includeLeaf){
					if(includeFolder){ // folder only
						q.append(" and nodetype = '").append(MediaType.folder.getCode()).append("'");
					}else{ // leaf only
						q.append(" and nodetype != '").append(MediaType.folder.getCode()).append("'");
					}
				}
				
				Query query = entityManager.createQuery(q.toString());
				return query.getResultList();
			}
		}
		return null;
	}
	

	@Override
	public MediaDetail getMediaTreeRoot(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM MediaDetail WHERE path IS NULL AND organization_id = ").append(orgId);
//			List<EntityDetail> details = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<MediaDetail> details = query.getResultList();
			if(details!=null && details.size()>0) return details.get(0);
		}
		return null;
	}

	@Override
	public List<NodeDetail> findMediasWithTypeInOrg(MediaType mtype, Long orgid) {
		if(mtype!=null && orgid!=null){
			StringBuilder q = new StringBuilder("FROM MediaDetail where organization_id = ").append(orgid).append(" and nodetype = '").append(mtype.getCode()).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


}
