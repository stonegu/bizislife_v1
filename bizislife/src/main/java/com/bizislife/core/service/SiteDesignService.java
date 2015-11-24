package com.bizislife.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.Pagination;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.InstanceViewSchedule;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleXml;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.ScheduleInterface;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.ModuleAttribute;

public interface SiteDesignService {
	
	public Long addToPageChangeList(Long pageid, Long activityLogId, String change);
	
	
	/**
	 * @param path the path provided here only includes ancestor's path, which means the path doesn't include itself.
	 * @return
	 */
	public boolean checkContainerPathLevel(String path);
	
	
	public ApiResponse clonePageContainer(String pageDetailUuid, String newPageDetailUuid);
	
//	public ApiResponse cloneContainer(String containerUuid);
	
	/**
	 * only used for clone inside the same tree, only clone node not folder
	 * 
	 * @param nodeType
	 * @param parentNodeUuid
	 * @param nodeName
	 * @param cloneFromUuid
	 * @return
	 */
	public ApiResponse cloneModuleNode(ModuleDetail.Type newNodeType, String parentNodeUuid, String nodeName, String cloneFromUuid);
	
	/**
	 * @param pageuuid the page need to be cloned
	 * @param destinationFolderUuid where the new page should go.
	 * @return
	 */
	public ApiResponse clonePage(String pageuuid, String destinationFolderUuid);
	
	/**
	 * this method will generate margin-top or margin-left for container and it's subnodes based on position, width, height<br/>
	 * the pre-requirement is containerTreeNode and it's subnodes are sorted by ContainerTreeNode.positionSort
	 * 
	 * @param previousContainerNode the container just before currentContainerNode
	 * @param currentContainerNode the containerNode need to be set margin space.
	 */
	public void containerTreeNodeMarginSpaceGenerator(ContainerTreeNode parentContainerNode, ContainerTreeNode previousContainerNode, ContainerTreeNode currentContainerNode);
	
	/**
	 * this method will generate margin-width for container and it's subnodes based on parent container
	 * 
	 * @param parentContainerNode
	 * @param currentContainerNode
	 */
	public void containerTreeNodeRelativeWidthHeightGenerator(ContainerTreeNode parentContainerNode, ContainerTreeNode currentContainerNode);
	
	public int countAllModuleDetailInOrg(Long orgId);
	
	public int countAllModuleInstanceInOrg(Long orgId);
	
	/**
	 * @param orgId
	 * @param type count all (desktop, mobile, folder) if type is null. PageDetail.Type.Desktop for desktop, PageDetail.Type.Mobile for mobile, PageDetail.Type.Page for desktop & mobile
	 * @return
	 */
	public int countAllPagesInOrg(Long orgId, PageDetail.Type type);
	
	public int countAllProductInOrgInOrg(Long orgId);
	
	public ApiResponse delAttrGroupSet(String moduledetailUuid, String attrGroupUuid);
	
	public void delContainerModuleScheduleByUuid(String uuid);
	
	public ApiResponse delInstanceAttr(String instanceUuid, String groupUuid, String attrUuid);
	
	public ApiResponse delInstanceAttrGroupSet(String instanceUuid, String attrGroupUuid);
	
	public ApiResponse delInstanceViewByUuid(String viewUuid);
	
	public ApiResponse delInstanceViewCssFile(String viewUuid, Long orgId);

	public ApiResponse delInstanceViewJspFile(String viewUuid, Long orgId);
	
	public ApiResponse delInstanceViewScheduleByUuid(String uuid);
	
	public ApiResponse delModuleAttr(String moduleUuid, String attrGroupUuid, String attrUuid);
	
	public ApiResponse delModuleInstanceByUuid(String instanceUuid);
	
	public void delModuleInstanceScheduleByUuid(String uuid);
	
	public ApiResponse delModuleViewCssFile(String moduleUuid, Long orgId);
	public ApiResponse delModuleViewJspFile(String moduleUuid, Long orgId);
	
	/**
	 * @param instanceUuids : instances that you need to find activated view and schedule
	 * @param activatedViewsAndScheds : map to hold all activated views or schedules' uuids with type (InstanceView/InstanceViewSchedule) 
	 */
	public void findAllActivatedViewsAndScheds(List<String> instanceUuids, Map<String, String> activatedViewsAndScheds);
	
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByContainerUuid(String containerUuid);
	
	
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByModuleUuid(String moduleUuid);
	
	/**
	 * @param viewUuid
	 * @return for instance uuids, only return instance has the view. For entityDetail, this method will not only return the entityDetail has the view, but also return all ancestors for the entityDetail
	 */
	public List<String> findInstanceChainFromView(String viewUuid);
	
	/**
	 * @param schedUuid
	 * @return for instance uuids, only return instance has the schedule. For entityDetail, this method will not only return the entityDetail has the schedule, but also return all ancestors for the entityDetail
	 */
	public List<String> findInstanceChainFromViewSchedule(String schedUuid);
	
	public List<InstanceView> findInstanceViewsByInstanceUuid(String instanceUuid);
	
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceUuid(String instanceUuid);
	
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceViewUuid(String viewUuid);
	
	/**
	 * recursive method to find all leafs for the root
	 * 
	 * @param root the tree needs to be searched
	 * @param leafs the list of leafs been found. leafs can't be null.
	 */
	public void findLeafsFromContainerTreeRoot(ContainerTreeNode root, List<ContainerTreeNode> leafs);
	
	public List<ModuleInstance> findModuleInstancesByModuleUuid(String moduleUuid);
	
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByContainerModuleScheduleUuid(String containerModuleScheduleUuid);
	
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByInstanceUuid(String instanceUuid);

	public List<InstanceView> findOrgInstanceViews(Long orgId);
	
	/**
	 * @param orgId
	 * @return org's all modules, including all product modules
	 */
	public List<ModuleDetail> findOrgModules(Long orgId);
	
	/**
	 * @param orgId
	 * @return org's all modules except all product modules, all product modules will be using one generic product module to represent.
	 */
	public List<ModuleDetail> findOrgModulesWithProModulesAggregate(Long orgId);
	
	public List<PageDetail> findOrgPagesByType(String orguuid, PageDetail.Type pagetype);
	
	/**
	 * @param orgId
	 * @param moduleType
	 * @return pages using moduletype in at lest one container. first to check ContainerModuleSchedule, next to check container's default module.
	 */
	public List<PageDetail> findPagesByModuleTypeForOrg(Long orgId, ModuleDetail.Type moduleType);
	
	public List<ContainerDetail> findPageContainers(String pageuuid);
	
	/**
	 * @param containerRoot
	 * @return map for Map<containerUUid, Map<title, result>>, <br/>
	 * Map<title, result> example:<br/> 
	 * example 1: <"modulecss", "org/1/adfadsfa.css"; moduleuuid", "afdafafdaf">.<br/>
	 * example 2: <"modulecss", "org/1/adfadsfa.css"; moduleuuid", "afdafafdaf"; "instanceuuid", "afdafafdaf">.<br/>
	 * example 3: <"modulecss", "org/1/adfadsfa.css"; "viewcss", "org/1/assssaaa.css"; "moduleuuid", "afdafafdaf"; "instanceuuid", "afdafafdaf", "viewuuid", "afdasdfaadfaf">.<br/>
	 * Note:<br/>
	 * some scheduled module has scheduled instance. some instance has schedule/default view 
	 * 
	 */
	public Map<String, Map<String, String>> generateContentInfoForContainerTreeLeaf(ContainerTreeNode containerRoot, String entityUuid);
	
	public void generateContainerPureUnitMap(ContainerTreeNode container, Map<String, String> containerPureUnitMap);
	
	public ContainerDetail getContainerByUuid(String uuid);
	
	public ContainerModuleSchedule getContainerModuleScheduleByUuid(String uuid);
	
	/**
	 * @param containerUuid
	 * @return the map for module's css, instanceview's css, ...<br/>
	 * Map<title, result> example:<br/> 
	 * example 1: <"modulecss", "org/1/adfadsfa.css"; moduleuuid", "afdafafdaf">.<br/>
	 * example 2: <"modulecss", "org/1/adfadsfa.css"; moduleuuid", "afdafafdaf"; "instanceuuid", "afdafafdaf">.<br/>
	 * example 3: <"modulecss", "org/1/adfadsfa.css"; "viewcss", "org/1/assssaaa.css"; "moduleuuid", "afdafafdaf"; "instanceuuid", "afdafafdaf", "viewuuid", "afdasdfaadfaf">.<br/>
	 * Note:<br/>
	 * some scheduled module has scheduled instance. some instance has schedule/default view 
	 */
	public Map<String, String> getContentInfoForContainer(String containerUuid, String entityUuid);
	
	/**
	 * @param instanceUuid
	 * @return instanceView from current instance (including product) only. <br/>
	 */
	public InstanceView getCurrentInstanceView(String instanceUuid);
	
	/**
	 * search through the list of schedules (ScheduleInterface.class) based on Date now, find one schedule fits the date now with highest priority
	 * 
	 * @param schedules
	 * @param now
	 * @return
	 */
	public <T> ScheduleInterface getCurrentSchedule(List<T> schedules, Date now);
	
	
	/**
	 * Note: pagination is for leaf only.
	 * 
	 * @param targetUuid accountuuid / groupuuid
	 * @param nodeClass EntityDetail, MediaDetail, 
	 * @param folderUuid folder that holds all nodes
	 * @param totalNumNodesInPage
	 * @param pageIndex current pageIndex
	 * @param targetPageUuid the pagination is for which page
	 * @return
	 */
	public Pagination getPaginationForNodeDetail(String targetUuid, String hostname, Class nodeClass, String folderUuid, int totalNumNodesInPage, int pageIndex, String targetPageUuid);
	
	
	public InstanceView getInstanceViewByUuid(String uuid);
	
	/**
	 * Note: used only for product
	 * @param instanceUuid product's uuid
	 * @param moduleDetailUuid moduleDetail used for entity
	 * @return closest category's instanceView. if product's category don't have scheduled or default instanceView, then goto category's category, ...
	 * Note: <br/> 
	 * find the view from the category which moduledetail should same as product's moduledetail.
	 */
	public InstanceView getInstanceViewFromCategory(String instanceUuid, String moduleDetailUuid);
	
	public InstanceViewSchedule getInstanceViewScheduleByUuid(String uuid);
	
	public ModuleAttribute getModuleAttrByUuid(String moduleUuid, String attrsGroupUuid, String moduleAttrUuid);
	
	public AttrGroup getModuleAttrGroupByUuid(String moduleuuid, String groupuuid);
	
	public ModuleDetail getModuleDetailByUuid(String uuid);
	
	public ModuleInstance getModuleInstanceByUuid(String uuid);
	
	public ModuleInstanceSchedule getModuleInstanceScheduleByUuid(String uuid);
	
	public ModuleMeta getModuleMetaByTargetUuid(String targetuuid);
	
	public PageDetail getOrgPageByTypeAndUrl(Long orgId, PageDetail.Type pageType, String url);
	
	public <T> List<ScheduleInterface> getOutDatedSchedule(List<T> schedules, Date now);
	
	public PageDetail getPageDetailByUuid(String pageUuid);
	
	public PageMeta getPageMetaByPageUuid(String pageUuid);
	
	@Deprecated
	public ModuleDetail getOrgProductModule(Long orgId);
	
	/**
	 * @param orgId
	 */
	public String getRandomCategoryPageUuid(Long orgId);
	
	public String getRandomProductPageUuid(Long orgId);
	
	public Map<String, Boolean> isPageContainersScheduledMap(String pageuuid);
	
	public ApiResponse moduleDetailAttributesSort(String moduleDetailUuid, String groupUuid, String attriUuid, int position);
	
	public ApiResponse newAttrGroupSet(String moduleUuid);
	
	/**
	 * save containerDetail and create a containerNodeTree in treeLevelViwe
	 * 
	 * @param container
	 * @return
	 */
	public Long newContainerDetail(ContainerDetail container);
	
	public ApiResponse newInstanceAttr(String moduleUuid, String moduleGroupUuid, String moduleAttrUuid, String instanceUuid);
	
	public ApiResponse newModuleAttr(String moduleUuid, String attrsGroupUuid, String attrClassName);
	
	/**
	 * save new moduleinstance and create a moduletreelevelvew
	 * 
	 * @param inst
	 * @return new moduleinstance id
	 */
	public Long newModuleInstance(ModuleInstance inst);
	
	public String newModuleNode(ModuleDetail.Type nodeType, String parentNodeUuid, String nodeName);
	
	public String newPageNode(PageDetail.Type nodeType, String parentNodeUuid, String nodeName, PageTreeLevelView.Type viewType);
	
	public ApiResponse pageDetailGenerator(String pageUuid, ContainerTreeNode containerTreeNode);
	
	public ApiResponse passUrlValidation(String url, Long orgid, PageDetail.Type type);
	
	public ApiResponse removeModuleMetaValue(String targetId, String type);
	
	public ApiResponse resizeContainer(String containerId, int left, int top, int width, int height);
	
	public Long saveContainerModuleSchedule(ContainerModuleSchedule sch);
	
	public Long saveInstanceView(InstanceView view);
	
	public Long saveInstanceViewSchedule(InstanceViewSchedule ivSchedule);
	
	public Long saveModuleInstance(ModuleInstance instance);
	
	public Long saveModuleInstanceSchedule(ModuleInstanceSchedule sche);
	
	public Long saveModuleTreeLevelView(ModuleTreeLevelView view);
	
	public void setDefaultInstanceView(String instanceViewUuid, InstanceView.IsDefault isDefault);

	public void setPageCss(String pageUuid, String css);
	
	public void setPageUrl(String pageUuid, String url);
	
	public ApiResponse updateContainerDetailByFieldnameValue(String containerUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateInstanceViewByFieldnameValue(String viewUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateInstanceViewScheduleByFieldnameValue(String schedUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateModuleAttrValue(String moduleUuid, String attrGroupUuid, String attrUuid, String updateValueName, String updateValue);
	
	/**
	 * this could be update moduleInstance or entitydetail
	 * 
	 * @param instanceUuid
	 * @param attrGroupUuid
	 * @param attrUuid
	 * @param updateValueName
	 * @param updateValue
	 * @return
	 */
	public ApiResponse updateModuleInstanceAttrValue(String instanceUuid, String attrGroupUuid, String attrUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateModuleInstanceValue(String instanceUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateModuleDetailByFieldnameValue(String moduleUuid, String updateValueName, String updateValue);
	
	public Long updateModuleDetailForXmlJspDesc(String moduleUuid, String moduleDesc, String moduleXml, String moduleJsp, String moduleCss);
	
	public ApiResponse updateModuleGroupValue(String moduleUuid, String attrGroupUuid, String updateValueName, String updateValue);
	
	public ApiResponse updateModuleMetaValue(String targetId, String updateValueName, String updateValue);
	
	public ApiResponse updatePageDetailByFieldnameValue(String pageUuid, String updateValueName, String updateValue);
	
	public ApiResponse writeInstanceViewCssToFile(Long viewId);

	public ApiResponse writeInstanceViewJspToFile(Long viewId);
	
	public ApiResponse writeModuleCssToFile(Long moduleId);

	public ApiResponse writeModuleJspToFile(Long moduleId);
	
	public ApiResponse writePageCssToFile(Long pageId);

}
