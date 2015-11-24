package com.bizislife.core.hibernate.dao;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule;
import com.bizislife.core.hibernate.pojo.ContainerTreeLevelView;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.InstanceViewSchedule;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleXml;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView.Type;
import com.bizislife.core.service.SiteDesignService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Repository("siteDesignDao")
public class SiteDesignDaoImpl implements SiteDesignDao {
    private static final Logger logger = LoggerFactory.getLogger(SiteDesignDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    
    @Autowired
    private SiteDesignService siteDesignService;
    
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ModuleXml getModuleXmlById(Long id) {
		if(id!=null){
//			return hibernateTemplate.get(ModuleXml.class, id);
			return entityManager.find(ModuleXml.class, id);
		}
		return null;
	}


	@Override
	public PageTreeLevelView getPageTreeRootViewLevel(Long orgId, PageTreeLevelView.Type type) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM PageTreeLevelView where organization_id = ").append(orgId).append(" and parentuuid is null and type = '").append(type.getCode()).append("'");
//			List<PageTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<PageTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}

	@Override
	public List<PageTreeLevelView> findOrgPageTreeLevelViews(Long orgId, Type type) {
		if(orgId!=null && type!=null){
			StringBuilder q = new StringBuilder("FROM PageTreeLevelView where organization_id = ").append(orgId).append(" and type='").append(type.getCode()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public Long savePageDetail(PageDetail pageDetail) {
		if(pageDetail!=null){
			if(pageDetail.getId()!=null){
//				hibernateTemplate.update(pageDetail);
//				return pageDetail.getId();
				return entityManager.merge(pageDetail).getId();
			}else{
//				return (Long)hibernateTemplate.save(pageDetail);
				entityManager.persist(pageDetail);
				return pageDetail.getId();
			}
		}
		return null;
	}


	@Override
	public Long savePageTreeLevelView(PageTreeLevelView pageTreeLevelView) {
		if(pageTreeLevelView!=null){
			if(pageTreeLevelView.getId()!=null){
//				hibernateTemplate.update(pageTreeLevelView);
//				return pageTreeLevelView.getId();
				return entityManager.merge(pageTreeLevelView).getId();
			}else{
//				return (Long)hibernateTemplate.save(pageTreeLevelView);
				entityManager.persist(pageTreeLevelView);
				return pageTreeLevelView.getId();
			}
		}
		return null;
	}

	@Override
	public int countAllPagesInOrg(Long orgId, PageDetail.Type type){
		if(orgId!=null){
			StringBuilder q = new StringBuilder("SELECT count(*) FROM PageDetail where organization_id = ").append(orgId);
			
			if(type!=null){
				if(type.equals(PageDetail.Type.Page)){
					q.append(" AND (type = '").append(PageDetail.Type.Desktop.getCode()).append("' OR type = '").append(PageDetail.Type.Mobile.getCode()).append("')");
				}else{
					q.append(" AND type = '").append(type.getCode()).append("'");
				}
			}
			
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
			
		}
		
		return 0;
	}

	@Override
	public int countAllPagesUnderCurrentFolderByCurrentFolderUuid(final String folderUuid, final PageDetail.Type type) {
		if(StringUtils.isNotBlank(folderUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM PageDetail where path is not null AND  type='").append(type.getCode()).append("' AND path like '%").append(folderUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM PageDetail where path is not null AND  type='").append(type.getCode()).append("' AND path like '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public int countPageSubFoldersByCurrentFolderUuid(final String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
			
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM PageDetail where path is not null AND type='").append(PageDetail.Type.Folder.getCode()).append("' AND path like '%").append(folderUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM PageDetail where path is not null AND type='").append(PageDetail.Type.Folder.getCode()).append("' AND path like '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public PageDetail getPageDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM PageDetail where pageuuid = '").append(uuid).append("'");
//			List<PageDetail> pageDetails = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<PageDetail> pageDetails = query.getResultList();
			if(pageDetails!=null && pageDetails.size()>0){
				return pageDetails.get(0);
			}
		}
		return null;
	}


	@Override
	public PageTreeLevelView getPageTreeLevelViewByParentUuid(String parentUuid) {
		if(StringUtils.isNotBlank(parentUuid)){
			StringBuilder q = new StringBuilder("FROM PageTreeLevelView where parentuuid = '").append(parentUuid).append("'");
//			List<PageTreeLevelView> levelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<PageTreeLevelView> levelViews = query.getResultList();
			if(levelViews!=null && levelViews.size()>0){
				return levelViews.get(0);
			}
		}
		return null;
	}


	@Override
	public Long saveContainerDetail(ContainerDetail containerDetail) {
		if(containerDetail!=null){
			if(containerDetail.getId()!=null){
//				hibernateTemplate.update(containerDetail);
//				return containerDetail.getId();
				return entityManager.merge(containerDetail).getId();
			}else{
//				return (Long)hibernateTemplate.save(containerDetail);
				entityManager.persist(containerDetail);
				return containerDetail.getId();
			}
		}
		return null;
	}


	@Override
	public Long saveContainerTreeLevelView(ContainerTreeLevelView containerTreeLevelView) {
		if(containerTreeLevelView!=null){
			if(containerTreeLevelView.getId()!=null){
//				hibernateTemplate.update(containerTreeLevelView);
//				return containerTreeLevelView.getId();
				return entityManager.merge(containerTreeLevelView).getId();
			}else{
//				return (Long)hibernateTemplate.save(containerTreeLevelView);
				entityManager.persist(containerTreeLevelView);
				return containerTreeLevelView.getId();
			}
		}
		return null;
	}


	@Override
	public ContainerTreeLevelView getContainerTreeRootViewLevel(Long orgId, String pageId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ContainerTreeLevelView where organization_id = ").append(orgId).append(" and pageuuid='").append(pageId).append("' and parentuuid is null");
//			List<ContainerTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ContainerTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}


	@Override
	public int countSubContainersByCurrentContainerUuid(final String containerUuid) {
		if(StringUtils.isNotBlank(containerUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM ContainerDetail where path is not null AND path like '%").append(containerUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ContainerDetail where path is not null AND path like '%").append(containerUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public ContainerDetail getContainerDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ContainerDetail where containeruuid = '").append(uuid).append("'");
//			List<ContainerDetail> details = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ContainerDetail> details = query.getResultList();
			if(details!=null && details.size()>0){
				return details.get(0);
			}
		}
		return null;
	}


	@Override
	public List<ContainerDetail> findPageContainerDetails(String pageid) {
		if(StringUtils.isNotBlank(pageid)){
			StringBuilder q = new StringBuilder("FROM ContainerDetail where pageuuid = '").append(pageid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public ContainerTreeLevelView getContainerTreeLevelViewByParentUuid(String parentUuid) {
		if(StringUtils.isNotBlank(parentUuid)){
			
			StringBuilder q = new StringBuilder("FROM ContainerTreeLevelView where parentuuid = '").append(parentUuid).append("'");
//			List<ContainerTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ContainerTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		
		return null;
	}


	@Override
	public List<ContainerDetail> findContainerDetailsByIds(String ids) {
		if(StringUtils.isNotBlank(ids)){
			StringBuilder q = new StringBuilder("FROM ContainerDetail where containeruuid in (").append(ids).append(")");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<ContainerDetail> findContainerDetailsHasDefaultModule(String moduleDetailUuid) {
		if(StringUtils.isNotBlank(moduleDetailUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerDetail where moduleuuid = '" +moduleDetailUuid+ "'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}
	

	@Override
	public List<ModuleDetail> findOrgModules(Long orgId) {
		
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ModuleDetail where (type = 'md' or type = 'pm') and organization_id = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}
	
	@Override
	public List<ModuleDetail> findOrgModulesOnlyFolder(Long orgId){
		if(orgId!=null){
			List<ModuleDetail> orgModules = findOrgModules(orgId);
			
			if(orgModules!=null && orgModules.size()>0){
				List<ModuleDetail> orgModulesOnlyFolder = new ArrayList<ModuleDetail>();
				
				for(ModuleDetail d : orgModules){
					if(d.getType().equals(ModuleDetail.Type.folder)){
						orgModulesOnlyFolder.add(d);
					}
				}
				
				if(orgModulesOnlyFolder.size()>0) return orgModulesOnlyFolder;
			}
		}
		
		return null;
	}

	@Override
	public List<ModuleDetail> findOrgModulesWithoutFolder(Long orgId) {
		if(orgId!=null){
			List<ModuleDetail> orgModules = findOrgModules(orgId);
			
			if(orgModules!=null && orgModules.size()>0){
				List<ModuleDetail> orgModulesWithoutFolder = new ArrayList<ModuleDetail>();
				
				for(ModuleDetail d : orgModules){
					if(!d.getType().equals(ModuleDetail.Type.folder)){
						orgModulesWithoutFolder.add(d);
					}
				}
				
				if(orgModulesWithoutFolder.size()>0) return orgModulesWithoutFolder;
			}
		}
		return null;
	}

	@Override
	public Long saveModuleDetail(ModuleDetail moduleDetail) {
		if(moduleDetail!=null){
			if(moduleDetail.getId()==null){
//				return (Long)hibernateTemplate.save(moduleDetail);
				entityManager.persist(moduleDetail);
				return moduleDetail.getId();
			}else{
//				hibernateTemplate.update(moduleDetail);
//				return moduleDetail.getId();
				return entityManager.merge(moduleDetail).getId();
			}
		}
		
		return null;
	}


	@Override
	public Long saveModuleTreeLevelView(ModuleTreeLevelView treeLevelView) {
		if(treeLevelView!=null){
			if(treeLevelView.getId()==null){
//				return (Long)hibernateTemplate.save(treeLevelView);
				entityManager.persist(treeLevelView);
				return treeLevelView.getId();
			}else{
//				hibernateTemplate.update(treeLevelView);
//				return treeLevelView.getId();
				return entityManager.merge(treeLevelView).getId();
			}
		}
		
		return null;
	}


	@Override
	public List<ModuleTreeLevelView> findOrgModuleTreeLevelViews(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ModuleTreeLevelView where organization_id = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public ModuleTreeLevelView getModuleTreeRootViewLevel(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ModuleTreeLevelView where parentuuid is null and organization_id = ").append(orgId);
//			List<ModuleTreeLevelView> levelviews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleTreeLevelView> levelviews = query.getResultList();
			if(levelviews!=null && levelviews.size()>0){
				return levelviews.get(0);
			}
		}
		
		return null;
	}


	@Override
	public int countAllModulesUnderCurrentFolderByCurrentFolderUuid(final String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND type='").append(ModuleDetail.ModuleType.module.getCode()).append("' AND path like '%").append(folderUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND (type='").append(ModuleDetail.Type.module.getCode()).append("' OR type='").append(ModuleDetail.Type.productModule.getCode()).append("') AND path like '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public int countAllProductModulesUnderCurrentFolderByCurrentFolderUuid(final String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND type='").append(ModuleDetail.ModuleType.productModule.getCode()).append("' AND path like '%").append(folderUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND type='").append(ModuleDetail.Type.productModule.getCode()).append("' AND path like '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public int countModuleSubFoldersByCurrentFolderUuid(final String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND type='").append(ModuleDetail.ModuleType.folder.getCode()).append("' AND path like '%").append(folderUuid).append("%'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleDetail where path is not null AND type='").append(ModuleDetail.Type.folder.getCode()).append("' AND path like '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public ModuleDetail getModuleDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ModuleDetail where moduleuuid = '").append(uuid).append("'");
//			List<ModuleDetail> details = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleDetail> details = query.getResultList();
			if(details!=null && details.size()>0){
				return details.get(0);
			}
		}
		
		return null;
	}


	@Override
	public ModuleTreeLevelView getModuleTreeLevelViewByParentUuid(String parentUuid) {
		if(StringUtils.isNotBlank(parentUuid)){
			
			StringBuilder q = new StringBuilder("FROM ModuleTreeLevelView where parentuuid = '").append(parentUuid).append("'");
//			List<ModuleTreeLevelView> treeLevelViews = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleTreeLevelView> treeLevelViews = query.getResultList();
			if(treeLevelViews!=null && treeLevelViews.size()>0){
				return treeLevelViews.get(0);
			}
		}
		return null;
	}


	@Override
	public ModuleDetail getModuleDetailById(Long id) {
		if(id!=null){
//			return hibernateTemplate.get(ModuleDetail.class, id);
			return entityManager.find(ModuleDetail.class, id);
		}
		return null;
	}


	@Override
	public Long saveModuleInstance(ModuleInstance inst) {
		if(inst!=null){
			if(inst.getId()==null){
//				return (Long)hibernateTemplate.save(inst);
				entityManager.persist(inst);
				return inst.getId();
			}
			else{
//				hibernateTemplate.update(inst);
//				return inst.getId();
				return entityManager.merge(inst).getId();
			}
		}
		return null;
	}


	@Override
	public int countInstanceByModuleUuid(final String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
//			return (Integer) hibernateTemplate.execute( 
//					new HibernateCallback() {
//						public Object doInHibernate(Session session) throws HibernateException,SQLException {
//							//SELECT count(*) FROM bizislifeii.moduleinstance where moduleuuid = '834193d4-5b12-426a-8a79-d911617d19c2'
//							Query query = session.createQuery(new StringBuilder("SELECT count(*) FROM ModuleInstance where moduleuuid ='").append(moduleUuid).append("'").toString());
//							return ( (Long) query.iterate().next() ).intValue();
//						}
//					} );
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleInstance where moduleuuid ='").append(moduleUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}

	@Override
	public ModuleInstance getModuleInstanceByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstance where moduleinstanceuuid = '").append(uuid.trim()).append("'");
//			List<ModuleInstance> ins = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleInstance> ins = query.getResultList();
			if(ins!=null && ins.size()>0){
				return ins.get(0);
			}
		}
		
		return null;
	}

	@Override
	public List<ModuleInstance> findModuleInstancesByModuleUuid(String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstance WHERE moduleuuid = '").append(moduleUuid.trim()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByContainerUuid(String containerUuid) {
		if(StringUtils.isNotBlank(containerUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerModuleSchedule where containeruuid = '").append(containerUuid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}


	@Override
	public Long saveContainerModuleSchedule(ContainerModuleSchedule sch) {
		if(sch!=null){
			if(sch.getId()==null){
//				return (Long)hibernateTemplate.save(sch);
				entityManager.persist(sch);
				return sch.getId();
			}else{
//				hibernateTemplate.update(sch);
//				return sch.getId();
				return entityManager.merge(sch).getId();
			}
		}
		return null;
	}


	@Override
	public void delContainerModuleScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ContainerModuleSchedule where uuid = '").append(uuid).append("'");
//			List<ContainerModuleSchedule> schs = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ContainerModuleSchedule> schs = query.getResultList();
			if(schs!=null && schs.size()>0){
//				hibernateTemplate.deleteAll(schs);
				for(ContainerModuleSchedule s : schs){
					entityManager.remove(s);
				}
			}
		}
	}


	@Override
	public ContainerModuleSchedule getContainerModuleScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ContainerModuleSchedule where uuid = '").append(uuid).append("'");
//			List<ContainerModuleSchedule> schs = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ContainerModuleSchedule> schs = query.getResultList();
			if(schs!=null && schs.size()>0) return schs.get(0);
		}
		
		return null;
	}


	@Override
	public List<InstanceView> findInstanceViewsByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			StringBuilder q = new StringBuilder("FROM InstanceView where moduleinstanceuuid = '").append(instanceUuid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public InstanceView getInstanceViewByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM InstanceView where instanceviewuuid = '").append(uuid).append("'");
//			List<InstanceView> views = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<InstanceView> views = query.getResultList();
			if(views!=null && views.size()>0){
				return views.get(0);
			}
		}
		return null;
	}


	@Override
	public Long saveInstanceView(InstanceView view) {
		if(view!=null){
			if(view.getId()==null){
//				return (Long)hibernateTemplate.save(view);
				entityManager.persist(view);
				return view.getId();
			}else{
//				hibernateTemplate.update(view);
//				return view.getId();
				return entityManager.merge(view).getId();
			}
		}
		return null;
	}


	@Override
	public InstanceView getDefaultViewForInstance(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			StringBuilder q = new StringBuilder("FROM InstanceView where moduleinstanceuuid = '").append(instanceUuid.trim()).append("' and isdefault = '1'");
//			List<InstanceView> views = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<InstanceView> views = query.getResultList();
			if(views!=null && views.size()>0){
				return views.get(0);
			}
		}
		return null;
	}


	@Override
	public InstanceView getInstanceViewById(Long id) {
		if(id!=null){
//			return hibernateTemplate.get(InstanceView.class, id);
			return entityManager.find(InstanceView.class, id);
		}
		return null;
	}


	@Override
	public List<InstanceView> findOrgInstanceViews(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM InstanceView where orgid = ").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public ModuleDetail getOrgProductModule(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ModuleDetail where type = 'pm' and organization_id = ").append(orgId);
//			List<ModuleDetail> productModules = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleDetail> productModules = query.getResultList();
			if(productModules!=null && productModules.size()>0) return productModules.get(0);
			
		}
		
		return null;
	}


	@Override
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByContainerModuleScheduleUuid(String containerModuleScheduleUuid) {
		if(StringUtils.isNotBlank(containerModuleScheduleUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstanceSchedule where containermodulescheduleuuid = '").append(containerModuleScheduleUuid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public Long saveModuleInstanceSchedule(ModuleInstanceSchedule sche) {
		if(sche!=null){
			if(sche.getId()==null){
//				return (Long)hibernateTemplate.save(sche);
				entityManager.persist(sche);
				return sche.getId();
			}else{
//				hibernateTemplate.update(sche);
//				return sche.getId();
				return entityManager.merge(sche).getId();
			}
		}
		return null;
	}


	@Override
	public ModuleInstanceSchedule getModuleInstanceScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstanceSchedule where uuid = '").append(uuid).append("'");
//			List<ModuleInstanceSchedule> sches = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleInstanceSchedule> sches = query.getResultList();
			if(sches!=null && sches.size()>0) return sches.get(0);
		}
		return null;
	}


	@Override
	public void delModuleInstanceScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstanceSchedule where uuid = '").append(uuid).append("'");
//			List<ModuleInstanceSchedule> sches = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleInstanceSchedule> sches = query.getResultList();
			
			if(sches!=null && sches.size()>0){
//				hibernateTemplate.deleteAll(sches);
				for(ModuleInstanceSchedule s : sches){
					entityManager.remove(s);
				}
			}
		}
	}


	@Override
	public Long saveInstanceViewSchedule(InstanceViewSchedule ivSchedule) {
		if(ivSchedule!=null){
			if(ivSchedule.getId()==null){
//				return (Long)hibernateTemplate.save(ivSchedule);
				entityManager.persist(ivSchedule);
				return ivSchedule.getId();
			}else{
//				hibernateTemplate.update(ivSchedule);
//				return ivSchedule.getId();
				return entityManager.merge(ivSchedule).getId();
			}
			
		}
		return null;
	}


	@Override
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			StringBuilder q = new StringBuilder("FROM InstanceViewSchedule where moduleinstanceuuid = '").append(instanceUuid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceViewUuid(String viewUuid) {
		if(StringUtils.isNotBlank(viewUuid)){
			StringBuilder q = new StringBuilder("FROM InstanceViewSchedule where instanceviewuuid = '").append(viewUuid).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public void delInstanceViewScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM InstanceViewSchedule where uuid = '").append(uuid).append("'");
//			List<InstanceViewSchedule> scheds = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<InstanceViewSchedule> scheds = query.getResultList();
			if(scheds!=null && scheds.size()>0){
//				hibernateTemplate.deleteAll(scheds);
				for(InstanceViewSchedule s : scheds){
					entityManager.remove(s);
				}
			}
		}
	}


	@Override
	public InstanceViewSchedule getInstanceViewScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM InstanceViewSchedule where uuid = '").append(uuid).append("'");
//			List<InstanceViewSchedule> scheds = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<InstanceViewSchedule> scheds = query.getResultList();
			if(scheds!=null && scheds.size()>0){
				return scheds.get(0);
			}
		}
		
		return null;
	}


	@Override
	public PageMeta getPageMetaByPageUuid(String pageUuid) {
		if(StringUtils.isNotBlank(pageUuid)){
			StringBuilder q = new StringBuilder("FROM PageMeta WHERE pageuuid = '").append(pageUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<PageMeta> pagemetas = query.getResultList();
			if(pagemetas!=null && pagemetas.size()>0) return pagemetas.get(0);
		}
		return null;
	}


	@Override
	public Long savePageMeta(PageMeta pageMeta) {
		if(pageMeta!=null){
			if(pageMeta.getId()==null){
				entityManager.persist(pageMeta);
				return pageMeta.getId();
			}else{
				return entityManager.merge(pageMeta).getId();
			}
		}
		return null;
	}


	@Override
	public PageDetail getPageDetailById(Long id) {
		if(id!=null){
			return entityManager.find(PageDetail.class, id);
		}
		return null;
	}


	@Override
	public PageDetail getOrgPageByTypeAndUrl(Long orgId, PageDetail.Type pageType, String url) {
		if(orgId!=null && pageType!=null && StringUtils.isNotBlank(url)){
			StringBuilder q = new StringBuilder("FROM PageDetail where organization_id = ").append(orgId).append(" and type = '").append(pageType.getCode()).append("' and url='").append(url).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<PageDetail> details = query.getResultList();
			if(details!=null && details.size()>0) return details.get(0);
		}
		return null;
	}


	@Override
	public void delModuleInstanceByUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			
			StringBuilder q = new StringBuilder("FROM ModuleInstance where moduleinstanceuuid = '").append(instanceUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<ModuleInstance> insts = query.getResultList();
			if(insts!=null && insts.size()>0){
				for(ModuleInstance s : insts){
					entityManager.remove(s);
				}
			}
		}
	}


	@Override
	public void delModuleTreeNode(String parentid, String nodeNeedToDel) {
		if(StringUtils.isNotBlank(parentid) && StringUtils.isNotBlank(nodeNeedToDel)){
			StringBuilder q = new StringBuilder("FROM ModuleTreeLevelView WHERE parentuuid = '").append(parentid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<ModuleTreeLevelView> views = query.getResultList();
			if(views.size()!=1){
				logger.error("system find more than 1 ModuleTreeLevelView for parentuuid = "+parentid);
			}else{
				
				ModuleTreeLevelView currentView = views.get(0);
				
				if(StringUtils.isNotBlank(currentView.getNodes())){
					List<ModuleTreeNode> moduleTreeNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, currentView.getNodes());
					
					if(moduleTreeNodes!=null && moduleTreeNodes.size()>0){
						ModuleTreeNode delNode = null;
						for(ModuleTreeNode n : moduleTreeNodes){
							if(n.getSystemName().equals(nodeNeedToDel)){
								delNode = n;
								break;
							}
						}
						if(delNode!=null){
							moduleTreeNodes.remove(delNode);
							if(moduleTreeNodes.size()>0){
								String updatedNodes = TreeHelp.getXmlFromTreeNodes(moduleTreeNodes);
								currentView.setNodes(updatedNodes);
							}else{
								entityManager.remove(currentView);
							}
							
						}
					}
					
				}else{
					// del view if no nodes inside
					entityManager.remove(currentView);
				}
				
				
			}
			
		}
		
	}

	@Override
	public void delInstanceViewByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			// find all instanceviews by instance uuid
			StringBuilder q = new StringBuilder("FROM InstanceView WHERE moduleinstanceuuid = '").append(instanceUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<InstanceView> views = query.getResultList();
			if(views!=null && views.size()>0){
				Long orgId = views.get(0).getOrgid();
				List<String> viewUuids = new ArrayList<String>();
				for(InstanceView v : views){
					viewUuids.add(v.getInstanceviewuuid());
					entityManager.remove(v);
				}
				if(viewUuids.size()>0){
					for(String viewUUid: viewUuids){
						siteDesignService.delInstanceViewCssFile(viewUUid, orgId);
						siteDesignService.delInstanceViewJspFile(viewUUid, orgId);
					}
				}
			}
			
		}
	}


	@Override
	public void delInstanceViewSchedulesByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			StringBuilder q = new StringBuilder("FROM InstanceViewSchedule WHERE moduleinstanceuuid = '").append(instanceUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<InstanceViewSchedule> sches = query.getResultList();
			if(sches!=null && sches.size()>0){
				for(InstanceViewSchedule s : sches){
					entityManager.remove(s);
				}
			}
		}
	}


	@Override
	public void delModuleDetail(Long detailId) {
		if(detailId!=null){
			ModuleDetail detail = entityManager.find(ModuleDetail.class, detailId);
			if(detail!=null) entityManager.remove(detail);
		}
		
	}


	@Override
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByModuleUuid(String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerModuleSchedule WHERE moduleuuid = '").append(moduleUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByModuleUuid(String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstanceSchedule WHERE moduleuuid = '").append(moduleUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
			
		}
		return null;
	}


	@Override
	public void delModuleTreeLevelView(Long viewid) {
		if(viewid!=null){
			ModuleTreeLevelView view = entityManager.find(ModuleTreeLevelView.class, viewid);
			if(view!=null) entityManager.remove(view);
		}
	}


	@Override
	public ModuleTreeLevelView getModuleTreeLevelViewHasNode(String nodeUuid) {
		if(StringUtils.isNotBlank(nodeUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleTreeLevelView WHERE nodes LIKE '%").append(nodeUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<ModuleTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0){
				return views.get(0);
			}
		}
		return null;
	}
	
	@Override
	public PageTreeLevelView getPageTreeLevelViewHasNode(String pageUuid){
		if(StringUtils.isNotBlank(pageUuid)){
			StringBuilder q = new StringBuilder("FROM PageTreeLevelView WHERE nodes LIKE '%").append(pageUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<PageTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0) return views.get(0);
		}
		
		return null;
	}
	
	@Override
	public ContainerTreeLevelView getContainerTreeLevelViewHasNode(String containerUuid){
		if(StringUtils.isNotBlank(containerUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerTreeLevelView WHERE nodes LIKE '%").append(containerUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<ContainerTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0) return views.get(0);
		}
		
		return null;
	}


	@Override
	public List<NodeDetail> findModuleDetailsUnderFolder(String folderUuid) {
		if(StringUtils.isNotBlank(folderUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleDetail WHERE path LIKE '%").append(folderUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public void delInstanceViewbyId(Long viewId) {
		if(viewId!=null){
			InstanceView view = entityManager.find(InstanceView.class, viewId);
			if(view!=null) entityManager.remove(view);
		}
	}


	@Override
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleInstanceSchedule WHERE moduleinstanceuuid = '").append(instanceUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public Long saveModuleMeta(ModuleMeta moduleMeta) {
		if(moduleMeta!=null){
			if(moduleMeta.getId()!=null){
				return entityManager.merge(moduleMeta).getId();
			}else{
				entityManager.persist(moduleMeta);
				return moduleMeta.getId();
			}
		}
		return null;
	}


	@Override
	public ModuleMeta getModuleMetaByTargetUuid(String targetUuid) {
		if(StringUtils.isNotBlank(targetUuid)){
			StringBuilder q = new StringBuilder("FROM ModuleMeta WHERE targetuuid = '").append(targetUuid.trim()).append("'");
			Query query = entityManager.createQuery(q.toString());
			List<ModuleMeta> metas = query.getResultList();
			if(metas!=null && metas.size()>0){
				return metas.get(0);
			}
		}
		
		return null;
	}


	@Override
	public List<PageDetail> findOrgPagesByType(Long orgId, com.bizislife.core.hibernate.pojo.PageDetail.Type pagetype) {
		
		if(pagetype!=null && orgId!=null){
			StringBuilder q = new StringBuilder("FROM PageDetail WHERE organization_id = ").append(orgId).append(" AND TYPE = '").append(pagetype.getCode()).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}


	@Override
	public void delContainerDetailById(Long id) {
		if(id!=null){
			ContainerDetail detail = entityManager.find(ContainerDetail.class, id);
			if(detail!=null) entityManager.remove(detail);
		}
		
	}


	@Override
	public List<ContainerDetail> findSubContainerDetails(String parentDetailUuid) {
		if(StringUtils.isNotBlank(parentDetailUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerDetail WHERE path LIKE '%").append(parentDetailUuid.trim()).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public void delContainerTreeLevelView(Long id) {
		if(id!=null){
			ContainerTreeLevelView view = entityManager.find(ContainerTreeLevelView.class, id);
			if(view!=null) entityManager.remove(view);
		}
	}


	@Override
	public void delPageMetaById(Long id) {
		if(id!=null){
			PageMeta meta = entityManager.find(PageMeta.class, id);
			if(meta!=null) entityManager.remove(meta);
		}
	}


	@Override
	public void delPageTreeLevelView(Long id) {
		if(id!=null){
			PageTreeLevelView view = entityManager.find(PageTreeLevelView.class, id);
			if(view!=null) entityManager.remove(view);
		}
	}


	@Override
	public List<PageDetail> findSubPageDetails(String parentDetailUuid) {
		if(StringUtils.isNotBlank(parentDetailUuid)){
			StringBuilder q = new StringBuilder("FROM PageDetail WHERE path LIKE '%").append(parentDetailUuid.trim()).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public void delPageDetailById(Long id) {
		if(id!=null){
			PageDetail detail = entityManager.find(PageDetail.class, id);
			if(detail!=null) entityManager.remove(detail);
		}
	}
	
	@Override
	public void delModuleMetaById(Long id) {
		if(id!=null){
			ModuleMeta moduleMeta = entityManager.find(ModuleMeta.class, id);
			if(moduleMeta!=null) entityManager.remove(moduleMeta);
		}
	}


	@Override
	public List<ContainerTreeLevelView> findContainerTreeLevelViewForPage(String pageUuid) {
		if(StringUtils.isNotBlank(pageUuid)){
			StringBuilder q = new StringBuilder("FROM ContainerTreeLevelView where pageuuid = '").append(pageUuid).append("'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public List<NodeDetail> findPageDetailsUnderFolder(String pagedetailUuid) {
		if(StringUtils.isNotBlank(pagedetailUuid)){
			StringBuilder q = new StringBuilder("FROM PageDetail WHERE path LIKE '%").append(pagedetailUuid).append("%'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public ModuleDetail getModuleTreeRoot(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM ModuleDetail WHERE path IS NULL AND organization_id = ").append(orgId);
//			List<EntityDetail> details = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<ModuleDetail> details = query.getResultList();
			if(details!=null && details.size()>0) return details.get(0);
		}
		return null;
	}


	@Override
	public PageDetail getPageTreeRoot(Long orgId, PageDetail.Type pagetype) {
		if(orgId!=null && pagetype!=null){
			
			PageTreeLevelView.Type treeviewType = null;
			if(pagetype.equals(PageDetail.Type.Desktop)){
				treeviewType = PageTreeLevelView.Type.Desktop;
			}else if(pagetype.equals(PageDetail.Type.Mobile)){
				treeviewType = PageTreeLevelView.Type.Mobile;
			}
			
			if(treeviewType!=null){
				
				// the reason to get the root from PageTreeLevelView is only PageTreeLevelView declares folder belongs to desktop or mobile!!!
				PageTreeLevelView treeviewRoot = getPageTreeRootViewLevel(orgId, treeviewType);
				
				if(treeviewRoot!=null && StringUtils.isNotBlank(treeviewRoot.getNodes())){
					StringBuilder q = new StringBuilder("FROM PageDetail WHERE path IS NULL AND organization_id =").append(orgId);
					Query query = entityManager.createQuery(q.toString());
					List<PageDetail> details = query.getResultList();
					
					if(details!=null && details.size()>0){
						for(PageDetail d : details){
							if(treeviewRoot.getNodes().indexOf(d.getPageuuid())>-1){
								return d;
							}
						}
					}
					
				}
				
			}
			
		}
		
		return null;
	}

	@Override
	public int countAllModuleDetailInOrg(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleDetail where (type = '").append(ModuleDetail.Type.module.getCode())
					.append("' or type = '").append(ModuleDetail.Type.productModule.getCode()).append("') and organization_id = ").append(orgId);
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}

	@Override
	public int countAllModuleInstanceInOrg(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("SELECT count(*) FROM ModuleInstance where type = '").append(ModuleDetail.Type.instance.getCode()).append("' and orgid = ").append(orgId);
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}

	@Override
	public int countAllProductInOrgInOrg(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("SELECT count(*) FROM EntityDetail where type = '").append(EntityDetail.EntityType.entity.getCode()).append("' and organization_id = ").append(orgId);
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null) return countResult.intValue();
		}
		return 0;
	}


	@Override
	public List<NodeDetail> findModuleDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf) {
		if(includeFold || includeLeaf){
			if(StringUtils.isNotBlank(folderUuid)){
				StringBuilder q = new StringBuilder("FROM ModuleDetail where parentuuid = '").append(folderUuid).append("'");
				if(!includeFold || !includeLeaf){
					if(includeFold){ // folder only
						q.append(" and type = '").append(ModuleDetail.Type.folder.getCode()).append("'");
					}else{ // leaf only
						q.append(" and type != '").append(ModuleDetail.Type.folder.getCode()).append("'");
					}
				}
				
				Query query = entityManager.createQuery(q.toString());
				return query.getResultList();
			}
			
		}
		
		return null;
	}


	@Override
	public List<NodeDetail> findPageDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf) {
		if(includeFold || includeLeaf){
			
			if(StringUtils.isNotBlank(folderUuid)){
				StringBuilder q = new StringBuilder("FROM PageDetail where parentuuid = '").append(folderUuid).append("'");
				if(!includeFold || !includeLeaf){
					if(includeFold){ // folder only
						q.append(" and type = '").append(PageDetail.Type.Folder.getCode()).append("'");
					}else{ // leaf only
						q.append(" and type != '").append(PageDetail.Type.Folder.getCode()).append("'");
					}
				}
				
				Query query = entityManager.createQuery(q.toString());
				return query.getResultList();
			}
		}
		
		return null;
	}

}
