package com.bizislife.core.service;

import java.util.*;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralTreeNode;
import com.bizislife.core.controller.component.MediaTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.PageTreeNode;
import com.bizislife.core.controller.component.TopicTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;

public interface TreeService {
	
	
	public ApiResponse copyTreeNodeToAnotherTree(Class nodeDetailClass, String nodeUuid, String targetUuid);
	
	/**
	 * Note: check getSouceOrgFoderInTargetTree
	 * 
	 * @param theType
	 * @param sourceOrgId
	 * @param targetOrgId
	 * @return create one if not exist, otherwise return the exist one.
	 */
	public String createSourceOrgFolderInTargetTree(Class theType, Long sourceOrgId, Long targetOrgId);
	
	/**
	 * delete node detail and node in treelevelview
	 * 
	 * @param nodeDetailClass
	 * @param nodeUuid
	 * @return
	 */
	public ApiResponse delNodeDetail(Class nodeDetailClass, String nodeUuid);
	
	/**
	 * delete tree node from treeLevelView Only. if nodes has subNodes, delete all subnodes and their childrens also.
	 * 
	 * @param currentNodeUuid
	 */
	public void delTreeNodeByUuid(Class nodeClass, String currentNodeUuid);
	
	public List<ContainerTreeNode> findContainerTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	/**
	 * this method is to find all nodes in TreeLevelView, and xstream it to get all TreeNodes<br/>
	 * this method is deprecated, you need to check findTreeNodesByParentNodeUuid_v2 for more information.
	 * 
	 * @param parentNodeUuid
	 * @return
	 */
	@Deprecated
	public List<MediaTreeNode> findMediaTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	public List<ModuleTreeNode> findModuleTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	/**
	 * @param theType
	 * @param targetOrgId find or create folder under this organization
	 * @param parentFolderUuid find or create folder under this folder
	 * @param folderName find or create folder using this name
	 * @return
	 */
	public String findOrCreateFolderBasedOnName(Class theType, Long targetOrgId, String parentFolderUuid, String folderName);
	
	public List<PageTreeNode> findPageTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	/**
	 * this function is not good at supporting large number of products in one folder.
	 * 
	 * @param parentNodeUuid
	 * @return
	 */
	@Deprecated
	public List<EntityTreeNode> findProductTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	/**
	 * @param theClass
	 * @param nodeUuid
	 * @param recursive true: find all subfolders, include subfolders for subfolder. 
	 * @return
	 */
	public void findSubFoldersUnderFolder(String targetUuid, List<TreeNode> results, Class<? extends TreeNode> theClass, String folderUuid, boolean recursive, boolean isPermissionFilterNeed);
	
	public List<TopicTreeNode> findTopicTreeNodesByParentNodeUuid(String parentNodeUuid);
	
	/**
	 * this method is to find all nodes in nodeDetail (media, entity, page, ...), and transfer to treeNodss.<br/> 
	 * Note: this method also double check all found treeNodes is exist in treeLevelView.
	 * 
	 * @param nodeClass MediaDetail, EntityDetail, PageDetail, ...
	 * @param parentNodeUuid
	 * @param targetUuid user or group uuid
	 * @return
	 */
	public List<TreeNode> findTreeNodesByParentNodeUuid_v2(String targetUuid, Class nodeClass, String parentNodeUuid, boolean includeFold, boolean includeLeaf, boolean isPermissionFilterNeed);
	
	public GeneralTreeNode generateTree(Class<? extends TreeNode> theClass, boolean folderOnly, Long orgId, int levelOfTree);
	
//	public void generalTreeFurtherProcess(GeneralTreeNode productCatTree, String pageUuid, List<String> currentSelectedEntityUuids);
	
	public ContainerTreeNode getContainerTreeRoot(Long orgId, String pageId);
	
	public ContainerTreeNode getContaienrTreeRootWithSubNodes(String pageId);
	
	public MediaTreeNode getMediaTreeRoot(Long orgId);
	
	public ModuleTreeNode getModuleTreeRoot(Long orgId);
	
	//public ModuleTreeNode getModuleWholeTree(Long orgId);
	
	public PageTreeNode getPageTreeRoot(Long orgId, PageTreeLevelView.Type type);
	
	public EntityTreeNode getProductTreeRoot(Long orgId);
	
	/**
	 * @param theType the type of tree
	 * @param sourceOrgId
	 * @param targetOrgId
	 * @return get folder's uuid <br/>
	 * Note: <br/>
	 * this method is used for find folder in module tree to hold all moduledetails from source org when sharing entity and page.<br/>
	 * For example:<br/>
	 * org1 has a folder "bizislife" under org1's moduletree's root "Modules", all moduledetails from bizislife will be copied under "bizislife" folder when copy entity and page.
	 * 
	 */
	public String getSouceOrgFoderInTargetTree(Class theType, Long sourceOrgId, Long targetOrgId);
	
	public TopicTreeNode getTopicTree();
	
	public TopicTreeNode getTopicTreeRoot(Long orgId);
	
	public Tree getTree(Tree.TreeCategory treeCategory);
	
	/**
	 * create org's desktop page tree if no page tree there.
	 * @param orgId
	 * @return pagetree root uuid
	 */
	public String initialPageTree(Long orgId, PageTreeLevelView.Type type);
	
	
	/**
	 * create org's media tree if no media tree there.
	 * @param orgId
	 * @return mediatree root uuid
	 */
	public String initialMeidaTree(Long orgId);
	
	/**
	 * @param orgId
	 * @return root ModuleTree uuid
	 */
	public String initialModuleTree(Long orgId);
	
	/**
	 * create org's product tree if no product tree there.
	 * @param orgId
	 * @return product tree root uuid
	 */
	public String initialProductTree(Long orgId);
	
	/**
	 * this function is for moving nodes inside same tree!!
	 * 
	 * @param nodeDetailClass
	 * @param nodeUuid
	 * @param originalPosition
	 * @param targetPosition
	 * @return
	 */
	@Deprecated
	public ApiResponse moveTreeNode(Class nodeDetailClass, String nodeUuid, String targetUuid);
	/**
	 * moveTreeNode version 2: trying to replace above one: moveTreeNode
	 * 
	 * this function is for moving nodes inside same tree!!
	 * 
	 * @param nodeDetailClass
	 * @param nodeUuid
	 * @param originalPosition
	 * @param targetPosition
	 * @return
	 */
	
	public ApiResponse moveTreeNode_v2(Class nodeDetailClass, String nodeUuid, String targetUuid);
	
	/**
	 * @param route
	 * @param cat
	 * @return tree id
	 */
	public Long newTree(String route, Tree.TreeCategory cat);
	
	/**
	 * this method is used for generate url for each node, and set current selected nodes for tree.<br/>
	 * 
	 * @param productCatTree
	 * @param currentPageUuid
	 * @param currentSelectedEntityUuid: folder uuid or product uuid
	 * 
	 */
	public void productTreeFurtherProcess(String hostname, GeneralTreeNode productCatTree, String currentPageUuid, 
			List<String> currentSelectedEntitysUuids, String orgUuid,
			String upperDefinedCategoryPageUuid, String upperDefinedProductPageUuid);
	
	public void updateContainerTreeNodeForContainer(ContainerTreeNode containerTreeNode, String pageuuid);
	
	public void updateEntityTreeNodeForModuleInstance(EntityTreeNode entityTreeNode);
	
	public void updateEntityTreeNodeForProduct(EntityTreeNode entityTreeNode, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs, String targetUuid);
	public void updateEntityTreeNodeForProduct(EntityTreeNode entityTreeNode, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs, Permission mergedPermission);
	
	/**
	 * @param view
	 * @param detailUuid 
	 * 		view can preview and read only if moduledetail/entitydetail has read permission is allowed
	 * 		view can modify only if moduledetail/entitydetail has modify permission is allow.
	 * 		view can't copy now!!
	 * 		<strong>Note: detailUuid=null means that permission check is not necessary!<br/>
	 * 		Also, detailUuid can be moduledetailUuid in the case that instanceView For moduledetail's instance or 
	 * 		can be entitydetailUuid in the case that instanceView for EntityDetail 
	 * 		</strong>
	 *  
	 */
	public void updateInstanceViewForTreeNode(InstanceView view, String detailUuid);
	
	public void updateMediaTreeNodeForMedia(MediaTreeNode mediaTreeNode,
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	public void updateMediaTreeNodeForMedia_v2(MediaTreeNode mediaTreeNode, boolean isRoot, boolean folderOnly,
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	/**
	 * @param node
	 * @param isInstanceInclude boolean flag to determine if tree should include instances for module
	 */
	public void updateModuleTreeNode(ModuleTreeNode node, boolean isInstanceInclude, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	public void updateModuleTreeNodeForSched(ModuleTreeNode node);
	
	/**
	 * @param node
	 * @param moduleDetailUuid 
	 * 		Instance can preview and read only if moduledetail has read permission is allowed
	 * 		Instance can modify only if moduledetail has modify permission is allow.
	 * 		Instance can't copy now!!<br/>
	 * 		<strong>Note: moduleDetailUuid=null means that permission check is not necessary!</strong>
	 *  
	 */
	public void updateModuleTreeNodeForInstance(ModuleTreeNode node, String moduleDetailUuid);
	
	public void updateOrgNodeForSharedModuleTree(Organization o, Permission permission);

	/**
	 * @param pageTreeNode
	 * @param type PageDetail.Type.Desktop / PageDetail.Type.Mobile
	 * @param currentNodePermissionedStuffs
	 * @param closestParentNodePermissionedStuffs
	 */
	public void updatePageTreeNodeForPage(PageTreeNode pageTreeNode, PageDetail.Type type, 
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	public void updateTopicTreeNode(TopicTreeNode node, boolean editable);
	
	/**
	 * @param sched
	 * @param detailUuid 
	 * 		schedule can preview and read only if moduledetail/entitydetail has read permission is allowed
	 * 		schedule can modify only if moduledetail/entitydetail has modify permission is allow.
	 * 		schedule can't copy now!!
	 * 		<strong>Note: detailUuid=null means that permission check is not necessary!<br/>
	 * 		Also, detailUuid can be moduledetailUuid in the case that instanceView For moduledetail's instance or 
	 * 		can be entitydetailUuid in the case that instanceView for EntityDetail 
	 * 		</strong>
	 *  
	 */
	public void updateViewScheduleForTreeNode(InstanceViewSchedule sched, String detailUuid);
}
