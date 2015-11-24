package com.bizislife.core.hibernate.dao;

import java.util.List;

import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule;
import com.bizislife.core.hibernate.pojo.ContainerTreeLevelView;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.InstanceViewSchedule;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleXml;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.PageDetail.Type;

public interface SiteDesignDao {
	
	public int countAllModuleDetailInOrg(Long orgId);
	
	public int countAllModuleInstanceInOrg(Long orgId);
	
	public int countAllModulesUnderCurrentFolderByCurrentFolderUuid(String folderUuid);
	
	/**
	 * @param orgId
	 * @param type count all (desktop, mobile, folder) if type is null. PageDetail.Type.Desktop for desktop, PageDetail.Type.Mobile for mobile, PageDetail.Type.Page for desktop & mobile
	 * @return
	 */
	public int countAllPagesInOrg(Long orgId, PageDetail.Type type);
	
	/**
	 * @param folderUuid
	 * @param type PageDetail.Type.Desktop & PageDetail.Type.Mobile
	 * @return
	 */
	public int countAllPagesUnderCurrentFolderByCurrentFolderUuid(String folderUuid, PageDetail.Type type);
	
	public int countAllProductInOrgInOrg(Long orgId);

	public int countAllProductModulesUnderCurrentFolderByCurrentFolderUuid(String folderUuid);
	
	public int countInstanceByModuleUuid(String moduleUuid);
	
	public int countModuleSubFoldersByCurrentFolderUuid(String folderUuid);
	
	public int countPageSubFoldersByCurrentFolderUuid(String folderUuid);
	
	public int countSubContainersByCurrentContainerUuid(String containerUuid);
	
	public void delContainerDetailById(Long id);
	
	public void delContainerModuleScheduleByUuid(String uuid);
	
	public void delContainerTreeLevelView(Long id);
	
	public void delInstanceViewbyId(Long viewId);
	
	/**
	 * del all instanceViews by instanceUuid, it also del all instanceViews' jsp & css
	 * 
	 * @param instanceUuid
	 */
	@Deprecated
	public void delInstanceViewByInstanceUuid(String instanceUuid);
	
	public void delInstanceViewScheduleByUuid(String uuid);
	
	public void delInstanceViewSchedulesByInstanceUuid(String instanceUuid);
	
	public void delModuleDetail(Long detailId);
	
	public void delModuleInstanceByUuid(String instanceUuid);
	
	public void delModuleInstanceScheduleByUuid(String uuid);
	
	public void delModuleTreeLevelView(Long viewid);
	
	/**
	 * delete module node from tree level view only. 
	 * 
	 * @param parentid
	 * @param nodeNeedToDel
	 */
	@Deprecated
	public void delModuleTreeNode(String parentid, String nodeNeedToDel);
	
	public void delModuleMetaById(Long id);
	
	public void delPageDetailById(Long id);
	
	public void delPageMetaById(Long id);
	
	public void delPageTreeLevelView(Long id);
	
	public List<ContainerDetail> findContainerDetailsByIds(String ids);
	
	public List<ContainerDetail> findContainerDetailsHasDefaultModule(String moduleDetailUuid);
	
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByContainerUuid(String containerUuid);
	
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByModuleUuid(String moduleUuid);
	
	public List<ContainerTreeLevelView> findContainerTreeLevelViewForPage(String pageUuid);
	
	public List<InstanceView> findInstanceViewsByInstanceUuid(String instanceUuid);
	
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceUuid(String instanceUuid);
	
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceViewUuid(String viewUuid);
	
	public List<NodeDetail> findModuleDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf);
	
	public List<NodeDetail> findModuleDetailsUnderFolder(String folderUuid);
	
	public List<ModuleInstance> findModuleInstancesByModuleUuid(String moduleUuid);
	
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByContainerModuleScheduleUuid(String containerModuleScheduleUuid);
	
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByInstanceUuid(String instanceUuid);
	
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByModuleUuid(String moduleUuid);
	
	public List<InstanceView> findOrgInstanceViews(Long orgId);
	
	public List<ModuleDetail> findOrgModules(Long orgId);
	
	public List<ModuleDetail> findOrgModulesOnlyFolder(Long orgId);
	
	public List<ModuleDetail> findOrgModulesWithoutFolder(Long orgId);
	
	public List<ModuleTreeLevelView> findOrgModuleTreeLevelViews(Long orgId);
	
	public List<PageDetail> findOrgPagesByType(Long orgId, PageDetail.Type pagetype);
	
	public List<PageTreeLevelView> findOrgPageTreeLevelViews(Long orgId, PageTreeLevelView.Type type);
	
	public List<ContainerDetail> findPageContainerDetails(String pageid);
	
	public List<NodeDetail> findPageDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf);
	
	public List<NodeDetail> findPageDetailsUnderFolder(String pagedetailUuid);
	
	public List<ContainerDetail> findSubContainerDetails(String parentDetailUuid);
	
	public List<PageDetail> findSubPageDetails(String parentDetailUuid);
	
	public ContainerDetail getContainerDetailByUuid(String uuid);
	
	public ContainerModuleSchedule getContainerModuleScheduleByUuid(String uuid);
	
	public ContainerTreeLevelView getContainerTreeLevelViewByParentUuid(String parentUuid);
	
	public ContainerTreeLevelView getContainerTreeLevelViewHasNode(String containerUuid);
	
	public ContainerTreeLevelView getContainerTreeRootViewLevel(Long orgId, String pageId);
	
	public InstanceView getDefaultViewForInstance(String instanceUuid);
	
	public InstanceView getInstanceViewById(Long id);
	
	public InstanceView getInstanceViewByUuid(String uuid);
	
	public InstanceViewSchedule getInstanceViewScheduleByUuid(String uuid);
	
	public ModuleDetail getModuleDetailById(Long id);
	
	public ModuleDetail getModuleDetailByUuid(String uuid);
	
	public ModuleInstance getModuleInstanceByUuid(String uuid);
	
	public ModuleInstanceSchedule getModuleInstanceScheduleByUuid(String uuid);
	
	public ModuleMeta getModuleMetaByTargetUuid(String targetUuid);
	
	public ModuleTreeLevelView getModuleTreeLevelViewByParentUuid(String parentUuid);
	
	public ModuleTreeLevelView getModuleTreeLevelViewHasNode(String nodeUuid);
	
	public ModuleDetail getModuleTreeRoot(Long orgId);
	
	public ModuleTreeLevelView getModuleTreeRootViewLevel(Long orgId);
	
	public ModuleXml getModuleXmlById(Long id);
	
	public PageDetail getOrgPageByTypeAndUrl(Long orgId, Type pageType, String url);
	
	@Deprecated
	public ModuleDetail getOrgProductModule(Long orgId);
	
	public PageDetail getPageDetailById(Long id);
	
	public PageDetail getPageDetailByUuid(String uuid);
	
	public PageMeta getPageMetaByPageUuid(String pageUuid);
	
	public PageTreeLevelView getPageTreeLevelViewByParentUuid(String parentUuid);
	
	public PageTreeLevelView getPageTreeLevelViewHasNode(String pageUuid);
	
	public PageDetail getPageTreeRoot(Long orgId, PageDetail.Type pagetype);
	
	public PageTreeLevelView getPageTreeRootViewLevel(Long orgId, PageTreeLevelView.Type type);
	
	public Long saveContainerDetail(ContainerDetail containerDetail);
	
	public Long saveContainerModuleSchedule(ContainerModuleSchedule sch);
	
	public Long saveContainerTreeLevelView(ContainerTreeLevelView containerTreeLevelView);
	
	public Long saveInstanceView(InstanceView view);
	
	public Long saveInstanceViewSchedule(InstanceViewSchedule ivSchedule);
	
	public Long saveModuleDetail(ModuleDetail moduleDetail);
	
	public Long saveModuleInstance(ModuleInstance inst);
	
	public Long saveModuleInstanceSchedule(ModuleInstanceSchedule sche);
	
	public Long saveModuleMeta(ModuleMeta moduleMeta);
	
	public Long saveModuleTreeLevelView(ModuleTreeLevelView treeLevelView);
	
	public Long savePageDetail(PageDetail pageDetail);
	
	public Long savePageMeta(PageMeta pageMeta);
	
	public Long savePageTreeLevelView(PageTreeLevelView pageTreeLevelView);

}
