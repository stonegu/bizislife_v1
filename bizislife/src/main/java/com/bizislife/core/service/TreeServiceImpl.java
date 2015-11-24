package com.bizislife.core.service;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralTreeNode;
import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.JsTreeNode.NodeType;
import com.bizislife.core.controller.component.MediaTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.PageTreeNode;
import com.bizislife.core.controller.component.Privilege;
import com.bizislife.core.controller.component.TopicTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.controller.helper.EntityHelp;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.entity.converter.AttributeConverter;
import com.bizislife.core.entity.converter.EntityConverter;
import com.bizislife.core.hibernate.dao.AccountDao;
import com.bizislife.core.hibernate.dao.EntityDao;
import com.bizislife.core.hibernate.dao.GroupDao;
import com.bizislife.core.hibernate.dao.MediaDao;
import com.bizislife.core.hibernate.dao.MessageDao;
import com.bizislife.core.hibernate.dao.OrganizationDao;
import com.bizislife.core.hibernate.dao.PermissionDao;
import com.bizislife.core.hibernate.dao.SiteDesignDao;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule;
import com.bizislife.core.hibernate.pojo.ContainerTreeLevelView;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.EntityTreeLevelView;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.InstanceViewSchedule;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.MediaTreeLevelView;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.hibernate.pojo.TopicTreeLevelView;
import com.bizislife.core.hibernate.pojo.Tree;
import com.bizislife.core.hibernate.pojo.Tree.TreeCategory;
import com.bizislife.core.hibernate.pojo.TreeLevelView;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute.SortType;
import com.bizislife.core.siteDesign.module.ModuleImageAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class TreeServiceImpl implements TreeService{
	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	GroupDao groupDao;

	@Autowired
	MessageDao messageDao;
	
	@Autowired
	EntityDao entityDao;

	@Autowired
	MediaDao mediaDao;
	
	@Autowired
	OrganizationDao orgDao;
	
	@Autowired
	SiteDesignDao siteDesignDao;
	
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    private ProductService productService;

	@Autowired
	MessageService messageService;
	
	@Autowired
	PermissionService permissionService;
	
	@Autowired
	PermissionDao permissionDao;

	@Autowired
	MessageFromPropertiesService messageFromPropertiesService;

    @Autowired
    protected ApplicationConfiguration applicationConfig;

	@Override
	@Transactional
	public Long newTree(String route, Tree.TreeCategory cat) {
		if(StringUtils.isNotBlank(route)){
			// put topic into the tree.
			Tree tree = messageDao.getTreeByCategory(cat);
			String[] tRoutes = route.trim().split("\\.");
			
			// ****** for topic tree
			if(cat.equals(Tree.TreeCategory.Topic)){
				Topic tempTopic = null;
				if(tree!=null){
					XStream stream = new XStream(new DomDriver());
					stream.processAnnotations(TopicTreeNode.class);
					TopicTreeNode topicTree = (TopicTreeNode)stream.fromXML(tree.getXmldata());
					boolean topicTreeChanged = false;
					if(tRoutes.length>1 && tRoutes[0].equals(topicTree.getPrettyName())){
						StringBuilder routePath = new StringBuilder();
						routePath.append("sys");
						TopicTreeNode currentTreeNode = topicTree;
						for(int i=1; i<tRoutes.length; i++){
							routePath.append(".").append(tRoutes[i].trim());
							// try to find the node that has name "tRoutes[i]"
							boolean found = false;
							List<TopicTreeNode> subNodes = currentTreeNode.getSubnodes();
							if(subNodes!=null && subNodes.size()>0){
								for(TopicTreeNode n : subNodes){
									if(n.getPrettyName().equals(tRoutes[i])){
										found = true;
										currentTreeNode = n;
										break;
									}
								}
							}
							if(!found){ // create a tree node and it's subnodes & reset currentTreeNode
								topicTreeChanged = true;
								TopicTreeNode tn = new TopicTreeNode();
								tn.setPrettyName(tRoutes[i].trim());
								// find topic uuid for the topic
								tempTopic = messageDao.getTopicByTopicroute(routePath.toString());
								tn.setSystemName(tempTopic!=null?tempTopic.getTopicuuid():UUID.randomUUID().toString());
								if(tempTopic!=null){
									Privilege privilege = new Privilege(tempTopic.getOrg_id(), null, null);
									tn.setPrivilege(privilege);
								}
								if(subNodes!=null) subNodes.add(tn);
								else {
									subNodes = new ArrayList<TopicTreeNode>();
									subNodes.add(tn);
									currentTreeNode.setSubnodes(subNodes);
								}
								currentTreeNode = tn;
							}
						}
					}
					
					if(topicTreeChanged){
						StringWriter sw = new StringWriter();
						stream.marshal(topicTree, new CompactWriter(sw));
						tree.setXmldata(sw.toString());
						messageDao.saveTree(tree);
					}
					return tree.getId();
				}else{ // create a tree 
					
					// create a topicTreeNode
					StringBuilder routePath = new StringBuilder();
					TopicTreeNode root = null;
					TopicTreeNode superNode = null;
					for(int i=0; i<tRoutes.length; i++){
						if(i>0) routePath.append(".");
						routePath.append(tRoutes[i].trim());
						tempTopic = messageDao.getTopicByTopicroute(routePath.toString());
						TopicTreeNode tn = new TopicTreeNode();
						tn.setPrettyName(tRoutes[i].trim());
						tn.setSystemName(tempTopic!=null?tempTopic.getTopicuuid():UUID.randomUUID().toString());
						if(tempTopic!=null){
							Privilege privilege = new Privilege(tempTopic.getOrg_id(), null, null);
							tn.setPrivilege(privilege);
						}
						if(i==0){
							root = tn;
						}
						if(superNode!=null){
							List<TopicTreeNode> subnodes = new ArrayList<TopicTreeNode>();
							subnodes.add(tn);
							superNode.setSubnodes(subnodes);
						}
						superNode = tn;
					}
					// save to tree table
					if(root!=null){
						XStream stream = new XStream(new DomDriver());
						stream.processAnnotations(TopicTreeNode.class);
						// marshal tree : use compactWriter to remove space
						StringWriter sw = new StringWriter();
						stream.marshal(root, new CompactWriter(sw));
						Tree newTree = new Tree(null, cat.name(), sw.toString());
						Long treeId = messageDao.saveTree(newTree);
						return treeId;
					}
				}
				
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Tree getTree(TreeCategory treeCategory) {
		if(treeCategory!=null){
			return messageDao.getTreeByCategory(treeCategory);
		}
		return null;
	}

	@Override
	public TopicTreeNode getTopicTree() {
		Tree tree = getTree(Tree.TreeCategory.Topic);
		if(tree!=null && tree.getXmldata()!=null){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(TopicTreeNode.class);
			return (TopicTreeNode)stream.fromXML(tree.getXmldata());
			
		}
		return null;
	}

	@Override
	@Transactional
	public String initialMeidaTree(Long orgId) {
		if(orgId!=null){
			Date now = new Date();
			
			MediaDetail mediaRoot = null;
			
			// find org's product tree
			List<MediaTreeLevelView> mediaTreeLevelViews = mediaDao.findOrgMediaTreeLevelViews(orgId);
			if(mediaTreeLevelViews==null || mediaTreeLevelViews.size()<1){
				XStream stream = new XStream(new DomDriver());

				String rootuuid = UUID.randomUUID().toString();
				String rootName = "Media";
				mediaRoot = new MediaDetail(null, 
						rootuuid, 
						rootName, 
						MediaDetail.MediaType.folder.getCode(), 
						null, 
						null, 
						null, 
						null, 
						now, 
						orgId, 
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()), 
						null);
				Long rootId = mediaDao.saveMediaDetail(mediaRoot);
				
				// create a tree
				List<MediaTreeNode> treeNodes = new ArrayList<MediaTreeNode>();
				MediaTreeNode treeNode = new MediaTreeNode();
				treeNode.setPrettyName(rootName);
				treeNode.setSystemName(rootuuid);
				treeNodes.add(treeNode);
				// marshal treeNodeList
				stream.processAnnotations(ArrayList.class);
				StringWriter sw2 = new StringWriter();
				stream.alias("treeNode", MediaTreeNode.class);
				stream.marshal(treeNodes, new CompactWriter(sw2));
				// create treeLevelView
				if(rootId!=null){
					MediaTreeLevelView treeLevelView = new MediaTreeLevelView(null, 
							null, 
							sw2.toString(), // list of entityTreeNodes 
							now, 
							orgId, 
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
					Long treeLevelViewId = mediaDao.saveMediaTreeLevelView(treeLevelView);
				}

			}else{
				mediaRoot = mediaDao.getMediaTreeRoot(orgId);
			}
			
			if(mediaRoot!=null) return mediaRoot.getMediauuid();
		}
		
		return null;

	}

	@Override
	@Transactional
	public String initialProductTree(Long orgId) {
		if(orgId!=null){
			Date now = new Date();
			EntityDetail productRoot = null;
			
			// find org's product tree
			List<EntityTreeLevelView> entityTreeLevelViews = entityDao.findOrgEntityTreeLevelViews(orgId);
			if(entityTreeLevelViews==null || entityTreeLevelViews.size()<1){
				XStream stream = new XStream(new DomDriver());
				//stream.autodetectAnnotations(true);
				stream.registerConverter(new EntityConverter());
				stream.registerConverter(new AttributeConverter());
				// create one
				// 1) entityDetail
				String entityuuid = UUID.randomUUID().toString();
				// create an entity and marshal it
				//Entity et = new FolderEntity();
				//stream.processAnnotations(FolderEntity.class);
//				StringWriter sw1 = new StringWriter();
//				// marshal
//				stream.marshal(et, new CompactWriter(sw1));
				String foldName = "Product";
				productRoot = new EntityDetail(null, 
						entityuuid, 
						foldName,
						null,
						null,
						EntityDetail.EntityType.folder.getCode(), 
						null,
						null,
						now,
						null,
						orgId, 
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
						null,
						EntityDetail.Visibility.show.getCode()
						);
				Long entityId = entityDao.saveEntityDetail(productRoot);
				
				// 2) create a product tree
				List<EntityTreeNode> treeNodes = new ArrayList<EntityTreeNode>();
				EntityTreeNode treeNode = new EntityTreeNode();
				treeNode.setPrettyName(foldName);
				treeNode.setSystemName(entityuuid);
				treeNodes.add(treeNode);
				// marshal treeNodeList
				stream.processAnnotations(ArrayList.class);
				StringWriter sw2 = new StringWriter();
				stream.alias("treeNode", EntityTreeNode.class);
				stream.marshal(treeNodes, new CompactWriter(sw2));
				// create treeLevelView
				if(entityId!=null){
					EntityTreeLevelView treeLevelView = new EntityTreeLevelView(null, 
							null, 
							sw2.toString(), // list of entityTreeNodes 
							now, 
							orgId, 
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
					Long treeLevelViewId = entityDao.saveEntityTreeLevelView(treeLevelView);
				}
				
			}else{ // find productRoot
				productRoot = entityDao.getProductTreeRoot(orgId);
			}
			
			if(productRoot!=null) return productRoot.getEntityuuid();
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public EntityTreeNode getProductTreeRoot(Long orgId) {
		if(orgId!=null){
			EntityTreeLevelView treelevelView = entityDao.getProductTreeRootViewLevel(orgId);
			if(treelevelView!=null){
				// unmarshal list of EntityTreeNode in levelview
				if(treelevelView.getNodes()!=null){
					XStream stream = new XStream(new DomDriver());
					//stream.processAnnotations(TopicTreeNode.class);
					stream.alias("treeNode", EntityTreeNode.class);
					List<EntityTreeNode> entityTreeNodes = (List<EntityTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
					if(entityTreeNodes!=null && entityTreeNodes.size()>0){
						return entityTreeNodes.get(0);
					}
				}
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public MediaTreeNode getMediaTreeRoot(Long orgId) {
		if(orgId!=null){
			MediaTreeLevelView treelevelView = mediaDao.getMediaTreeRootViewLevel(orgId);
			if(treelevelView!=null){
				// unmarshal list of EntityTreeNode in levelview
				if(treelevelView.getNodes()!=null){
					XStream stream = new XStream(new DomDriver());
					//stream.processAnnotations(TopicTreeNode.class);
					stream.alias("treeNode", MediaTreeNode.class);
					List<MediaTreeNode> mediaTreeNodes = (List<MediaTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
					if(mediaTreeNodes!=null && mediaTreeNodes.size()>0){
						return mediaTreeNodes.get(0);
					}
				}
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleTreeNode getModuleTreeRoot(Long orgId) {
		if(orgId!=null){
			ModuleTreeLevelView treeLevelView = siteDesignDao.getModuleTreeRootViewLevel(orgId);
			if(treeLevelView!=null && treeLevelView.getNodes()!=null){
				
				
//				XStream stream = new XStream(new DomDriver());
//				stream.alias("treeNode", ModuleTreeNode.class);
//				List<ModuleTreeNode> moduleTreeNodes = (List<ModuleTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
				
				List<ModuleTreeNode> moduleTreeNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, treeLevelView.getNodes());
				if(moduleTreeNodes!=null && moduleTreeNodes.size()>0){
					return moduleTreeNodes.get(0);
				}
				
			}
		}
		
		return null;
	}
	
//	@Override
//	@Transactional(readOnly=true)
//	public ModuleTreeNode getModuleWholeTree(Long orgId){
//		if(orgId!=null){
//			ModuleTreeLevelView rootLevelView = siteDesignDao.getModuleTreeRootViewLevel(orgId);
//			if(rootLevelView!=null && rootLevelView.getNodes()!=null){
//				
//				List<ModuleTreeNode> rootTreeNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, rootLevelView.getNodes());
//				if(rootTreeNodes!=null && rootTreeNodes.size()>0){
//					ModuleTreeNode root = rootTreeNodes.get(0);
//					
//					// generate whole tree from the root.
//					...
//					
//					
//					
//					
//					return root;
//				}
//			}
//			
//			
//		}
//		
//		return null;
//	}

	@Deprecated
	@Override
	@Transactional(readOnly=true)
	public List<EntityTreeNode> findProductTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			EntityTreeLevelView treelevelView = entityDao.getEntityTreeLevelViewByParentUuid(parentNodeUuid);

			// unmarshal list of EntityTreeNode in levelview
			if(treelevelView!=null && treelevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				//stream.processAnnotations(TopicTreeNode.class);
				stream.alias("treeNode", EntityTreeNode.class);
				List<EntityTreeNode> entityTreeNodes = (List<EntityTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
				return entityTreeNodes;
			}
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<MediaTreeNode> findMediaTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			MediaTreeLevelView treelevelView = mediaDao.getMediaTreeLevelViewByParentUuid(parentNodeUuid);

			// unmarshal list of EntityTreeNode in levelview
			if(treelevelView!=null && treelevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				//stream.processAnnotations(TopicTreeNode.class);
				stream.alias("treeNode", MediaTreeNode.class);
				List<MediaTreeNode> mediaTreeNodes = (List<MediaTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
				return mediaTreeNodes;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public void updateEntityTreeNodeForProduct(EntityTreeNode entityTreeNode, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs, String targetUuid) {
		if(StringUtils.isNotBlank(targetUuid)){
			Account targetAccount = accountDao.getAccountByUuid(targetUuid);
			Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
			
			Permission mergedPermission = null;
			if(targetAccount!=null){
				mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
			}else if(targetGroup!=null){
				mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
			}
			
			updateEntityTreeNodeForProduct(entityTreeNode, currentNodePermissionedStuffs, closestParentNodePermissionedStuffs, mergedPermission);
		}
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public void updateEntityTreeNodeForProduct(EntityTreeNode entityTreeNode, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs, Permission mergedPermission) {
		if(entityTreeNode!=null && entityTreeNode.getSystemName()!=null){
			// get EntityDetail by uuid
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityTreeNode.getSystemName());
			if(entityDetail!=null){
				
				Map<Permission.Type, Boolean> relatedPermissionValues = permissionService.getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
				
				// set type
				entityTreeNode.setType(EntityDetail.EntityType.fromCode(entityDetail.getType()));
				
				// set default pic
				ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(entityDetail.getModuleuuid());
				String defaultImage = EntityHelp.getDefaultImageFromEntity(entityDetail, moduleDetail);
				
				if(StringUtils.isNotBlank(defaultImage)) entityTreeNode.setDefaultImageSysName(defaultImage);
				
				// add more info
				// add class based on permission: preview, read, copy, modify
				if(entityDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){ // for folder
					if(StringUtils.isNotBlank(entityDetail.getParentuuid())){ // not the root
						// read: toConfig; copy: toCopy; modify: create delete toRename newView
						if(relatedPermissionValues.get(Permission.Type.read)){
							entityTreeNode.addCssClassInfos("toConfig");
						}
// disable folder duplicate function for system abuse!!!						
						if(relatedPermissionValues.get(Permission.Type.copy)){
							entityTreeNode.addCssClassInfos("toShare"); //toShare: between different trees
						}
						
						if(relatedPermissionValues.get(Permission.Type.modify)){
							entityTreeNode.addCssClassInfos("create delete toRename newView");
						}
						//entityTreeNode.setCssClassInfos("create delete toRename toConfig newView");
					}else{
						if(relatedPermissionValues.get(Permission.Type.read)){
							entityTreeNode.addCssClassInfos("toConfig");
						}
// disable folder duplicate function for system abuse!!!						
						if(relatedPermissionValues.get(Permission.Type.copy)){
							entityTreeNode.addCssClassInfos("toShare"); //toShare: between different trees
						}
						if(relatedPermissionValues.get(Permission.Type.modify)){
							entityTreeNode.addCssClassInfos("create newView");
						}
//						entityTreeNode.setCssClassInfos("create toConfig newView");
					}
				}else{ // for leaf - product node
					if(relatedPermissionValues.get(Permission.Type.read)){
						entityTreeNode.addCssClassInfos("toConfig");
					}
					if(relatedPermissionValues.get(Permission.Type.copy)){
						entityTreeNode.addCssClassInfos("toCopy toShare"); // toCopy: inside tree, toShare: between different trees
					}
					if(relatedPermissionValues.get(Permission.Type.modify)){
						entityTreeNode.addCssClassInfos("delete toRename newView");
					}
//					entityTreeNode.setCssClassInfos("delete toRename toConfig newView");
					
					// for product links 
					entityTreeNode.addCssClassInfos("pageLinks");
					
					
				}
				
				// count all product entity types and put into EntityTreeNode's childrenNumbers
				if(entityDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
					
					
					ApiResponse sqlSegmentResponse = permissionService.generateSqlConditionSegmentForProductList(mergedPermission, entityDetail.getEntityuuid());
					
					// find count numbers for subfolder
					//int totalSubFolders = entityDao.countSubFoldersByCurrentFolderUuid(entityDetail.getEntityuuid(), false);
					int totalSubFolders = productService.countProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, entityDetail.getEntityuuid(), true, false);
					if(totalSubFolders>0){
						entityTreeNode.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
					}
					
					// find count numbers for products under current folder (includes all products under current folder's subfolder)
					int totalSubProducts = productService.countProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, entityDetail.getEntityuuid(), false, true);
					if(totalSubProducts>0){
						entityTreeNode.addChildrenNumber(JsTreeNode.NodeType.instance, totalSubProducts);
					}
				}
				
				// find if has views defined for the entity
				List<InstanceView> views = siteDesignDao.findInstanceViewsByInstanceUuid(entityDetail.getEntityuuid());
				if(views!=null && views.size()>0){
					entityTreeNode.addChildrenNumber(JsTreeNode.NodeType.instanceView, views.size());
					
					// add special indicator for entity .............
//					String nodeName = entityTreeNode.getPrettyName();
//					nodeName = nodeName + "<span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' domvalue='{\"topOffset\":20, \"leftOffset\":-30, \"ajaxCall\":{\"url\":\"/getEntityInfo\", \"params\":\"entityid="+entityDetail.getEntityuuid()+"\"}}'>"+views.size()+"</span>";
//					entityTreeNode.setPrettyName(nodeName);
				}
				
			}
			
			
		}
	}
	
	@Deprecated
	@Override
	public void updateEntityTreeNodeForModuleInstance(EntityTreeNode entityTreeNode) {
		if(entityTreeNode!=null && entityTreeNode.getSystemName()!=null){
			// get EntityDetail by uuid
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityTreeNode.getSystemName());
			if(entityDetail!=null){
				// set type
				entityTreeNode.setType(EntityDetail.EntityType.fromCode(entityDetail.getType()));
				// add more info 
//				if(entityDetail.getType().equals(EntityDetail.EntityType.productFolder.getCode())){ // for folder
//					if(StringUtils.isNotBlank(entityDetail.getParentuuid())){ // not the root
//						entityTreeNode.setCssClassInfos("create delete");
//					}else{
//						entityTreeNode.setCssClassInfos("create");
//					}
//				}else{ // for leaf - product node
//					entityTreeNode.setCssClassInfos("delete");
//				}
				entityTreeNode.addCssClassInfos("toViewManage "+EntityDetail.EntityType.entity.name());
				
				// count all product entity types and put into EntityTreeNode's childrenNumbers
				if(entityDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){ // only calculate numbers for current entity is folder
					// find count numbers for subfolder
					int totalSubFolders = entityDao.countSubFoldersByCurrentFolderUuid(entityDetail.getEntityuuid(), false);
					if(totalSubFolders>0){
						entityTreeNode.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
					}
					// find count numbers for products under current folder (includes all products under current folder's subfolder)
					int totalSubProducts = entityDao.countAllProductsUnderCurrentFolderByCurrentFolderUuid(entityDetail.getEntityuuid(), EntityType.entity, false, null);
					if(totalSubProducts>0){
						entityTreeNode.addChildrenNumber(JsTreeNode.NodeType.instance, totalSubProducts);
					}
				}
			}
			
			
		}
	}



	@Override
	@Transactional(readOnly=true)
	public void updateMediaTreeNodeForMedia(MediaTreeNode mediaTreeNode,
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs) {
		if(mediaTreeNode!=null && mediaTreeNode.getSystemName()!=null){
			
			
			Map<Permission.Type, Boolean> relatedPermissionValues = permissionService.getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
			
			// get MediaDetail by uuid
			MediaDetail mediaDetail = mediaDao.getMediaDetailByUuid(mediaTreeNode.getSystemName());
			
			if(mediaDetail!=null){
				// set type
				mediaTreeNode.setType(MediaType.fromCode(mediaDetail.getNodetype()));
				// add more info 
				if(mediaDetail.getNodetype().equals(MediaType.folder.getCode())){ // for folder
					if(StringUtils.isNotBlank(mediaDetail.getParentuuid())){ // not the root
						if(relatedPermissionValues.get(Permission.Type.modify)){
							mediaTreeNode.addCssClassInfos("create delete toRename");
						}
					}else{
						if(relatedPermissionValues.get(Permission.Type.modify)){
							mediaTreeNode.addCssClassInfos("create");
						}
					}
				}else{ // for leaf - product node
					if(relatedPermissionValues.get(Permission.Type.modify)){
						mediaTreeNode.addCssClassInfos("delete");
					}
					if(relatedPermissionValues.get(Permission.Type.preview)){
						mediaTreeNode.addCssClassInfos("preview");
					}
					
				}
				
			}

			// count all media entity types and put into EntityTreeNode's childrenNumbers
			if(mediaDetail.getNodetype().equals(MediaType.folder.getCode())){ // only calculate numbers for current node is folder
				// find count numbers for subfolder
				int totalSubFolders = mediaDao.countSubFoldersByCurrentFolderUuid(mediaDetail.getMediauuid());
				if(totalSubFolders>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
				}
				// find count numbers for media under current folder (includes all medias under current folder's subfolder)
				int totalSubImgs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaDetail.getMediauuid(), MediaType.image);
				if(totalSubImgs>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafImg, totalSubImgs);
				}
				// find count numbers for media under current folder (includes all medias under current forlder's subfolder)
				int totalSubPdfs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaDetail.getMediauuid(), MediaType.pdf);
				if(totalSubPdfs>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafPdf, totalSubPdfs);
				}
				
				int totalSubCss = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaDetail.getMediauuid(), MediaType.css);
				if(totalSubCss>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafCss, totalSubCss);
				}
				
				int totalSubJs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaDetail.getMediauuid(), MediaType.javascript);
				if(totalSubJs>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafJS, totalSubJs);
				}
				
				int totalSubText = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaDetail.getMediauuid(), MediaType.text);
				if(totalSubText>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafText, totalSubText);
				}
				
			}
			
		}
		
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public void updateMediaTreeNodeForMedia_v2(MediaTreeNode mediaTreeNode, boolean isRoot, boolean folderOnly,
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs) {
		if(mediaTreeNode!=null && mediaTreeNode.getSystemName()!=null && mediaTreeNode.getType()!=null){
			
			
			Map<Permission.Type, Boolean> relatedPermissionValues = permissionService.getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
			
			
			// add more info 
			if(mediaTreeNode.getType().equals(MediaType.folder)){ // for folder
				if(!isRoot){ // not the root
					if(relatedPermissionValues.get(Permission.Type.modify)){
						mediaTreeNode.addCssClassInfos("create delete toRename");
					}
				}else{
					if(relatedPermissionValues.get(Permission.Type.modify)){
						mediaTreeNode.addCssClassInfos("create");
					}
				}
			}else{ // for leaf - product node
				if(relatedPermissionValues.get(Permission.Type.modify)){
					mediaTreeNode.addCssClassInfos("delete");
				}
				if(relatedPermissionValues.get(Permission.Type.preview)){
					mediaTreeNode.addCssClassInfos("preview");
				}
				
			}

			// count all media entity types and put into EntityTreeNode's childrenNumbers
			if(mediaTreeNode.getType().equals(MediaType.folder)){ // only calculate numbers for current node is folder
				// find count numbers for subfolder
				int totalSubFolders = mediaDao.countSubFoldersByCurrentFolderUuid(mediaTreeNode.getSystemName());
				if(totalSubFolders>0){
					mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
				}
				
				if(!folderOnly){
					
					// find count numbers for media under current folder (includes all medias under current folder's subfolder)
					int totalSubImgs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaTreeNode.getSystemName(), MediaType.image);
					if(totalSubImgs>0){
						mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafImg, totalSubImgs);
					}
					// find count numbers for media under current folder (includes all medias under current forlder's subfolder)
					int totalSubPdfs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaTreeNode.getSystemName(), MediaType.pdf);
					if(totalSubPdfs>0){
						mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafPdf, totalSubPdfs);
					}
					
					int totalSubCss = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaTreeNode.getSystemName(), MediaType.css);
					if(totalSubCss>0){
						mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafCss, totalSubCss);
					}
					
					int totalSubJs = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaTreeNode.getSystemName(), MediaType.javascript);
					if(totalSubJs>0){
						mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafJS, totalSubJs);
					}
					
					int totalSubText = mediaDao.countAllMediasUnderCurrentFolderByCurrentFolderUuid(mediaTreeNode.getSystemName(), MediaType.text);
					if(totalSubText>0){
						mediaTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafText, totalSubText);
					}
					
				}
				
			}
			
		}
	}

	

	@Override
	@Transactional(readOnly=true)
	public void updateModuleTreeNode(ModuleTreeNode node, boolean isInstanceInclude, 
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs) {
		
		if(node!=null && node.getSystemName()!=null){
			
			Map<Permission.Type, Boolean> relatedPermissionValues = permissionService.getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);			
			
			// get the detail
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(node.getSystemName());
			
			if(moduleDetail!=null){
				node.setType(ModuleDetail.Type.fromCode(moduleDetail.getType()));
			}
			
			// add more info 
			if(node.getType().equals(ModuleDetail.Type.folder)){ // for folder
				if(StringUtils.isNotBlank(moduleDetail.getParentuuid())){ // not the root
					// read: toConfig; copy: toCopy; modify: create delete toRename newView
// disable folder duplicate function (toCopy) for system abuse!!!						
					if(relatedPermissionValues.get(Permission.Type.copy)){
						node.addCssClass("toShare"); //toShare: between different trees
					}
					if(relatedPermissionValues.get(Permission.Type.modify)){
						node.addCssClass("toCreate toDelete toRename");
					}
				}else{
// disable folder duplicate function (toCopy) for system abuse!!!						
					if(relatedPermissionValues.get(Permission.Type.copy)){
						node.addCssClass("toShare"); //toShare: between different trees
					}
					if(relatedPermissionValues.get(Permission.Type.modify)){
						node.addCssClass("toCreate");
					}
				}
			}else{ // for leaf
				if(node.getType().equals(ModuleDetail.Type.module)){
					node.addCssClass(ModuleDetail.Type.module.name());
					
					if(relatedPermissionValues.get(Permission.Type.read)){
						node.addCssClass("toConfig");
					}
					if(relatedPermissionValues.get(Permission.Type.copy)){
						node.addCssClass("toCopy toShare"); // toCopy: inside tree, toShare: between different trees
					}
					if(relatedPermissionValues.get(Permission.Type.modify)){
						node.addCssClass("toDelete toInstance toRename");
					}
					
				}
				else if(node.getType().equals(ModuleDetail.Type.productModule)){
					node.addCssClass(ModuleDetail.Type.productModule.name());
					if(relatedPermissionValues.get(Permission.Type.read)){
						node.addCssClass("toConfig");
					}
					if(relatedPermissionValues.get(Permission.Type.copy)){
						node.addCssClass("toCopy toShare"); // toCopy: inside tree, toShare: between different trees
					}
					if(relatedPermissionValues.get(Permission.Type.modify)){
						node.addCssClass("toDelete toRename");
					}
				}
			}
			
			// get children number
			if(moduleDetail.getType().equals(ModuleDetail.Type.folder.getCode())){
				int totalSubFolders = siteDesignDao.countModuleSubFoldersByCurrentFolderUuid(moduleDetail.getModuleuuid());
				if(totalSubFolders>0){
					node.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
				}
				
				int totalModule = siteDesignDao.countAllModulesUnderCurrentFolderByCurrentFolderUuid(moduleDetail.getModuleuuid());
				if(totalModule>0){
					node.addChildrenNumber(JsTreeNode.NodeType.module, totalModule);
				}
				
			}else if(moduleDetail.getType().equals(ModuleDetail.Type.module.getCode()) && isInstanceInclude){
				int totalModuleInstance = siteDesignDao.countInstanceByModuleUuid(moduleDetail.getModuleuuid());
				if(totalModuleInstance>0){
					node.addChildrenNumber(JsTreeNode.NodeType.instance, totalModuleInstance);
				}
			}
			
			// find if any page using the module or scheduled to use the module
			List<ContainerModuleSchedule> containerModuleSchedules = siteDesignDao.findContainerModuleSchedulesByModuleUuid(node.getSystemName());
			if(containerModuleSchedules!=null && containerModuleSchedules.size()>0){
				String prettyname = node.getPrettyName();
				node.setPrettyName(prettyname+"<span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' domvalue='{\"topOffset\":20, \"leftOffset\":-280, \"width\":300, \"ajaxCall\":{\"url\":\"/getModuleDetailInfo\", \"params\":\"moduleid="+node.getSystemName()+"\"}}'>"+containerModuleSchedules.size()+"</span>");
			}
			
			
		}
		
	}
	
	@Override
	@Transactional(readOnly=true)
	public void updateModuleTreeNodeForSched(ModuleTreeNode node) {
		if(node!=null && node.getSystemName()!=null){
			// get the detail
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(node.getSystemName());
			
			if(moduleDetail!=null){
				node.setType(ModuleDetail.Type.fromCode(moduleDetail.getType()));
				
				// replace module's name for product module
				if(node.getType().equals(ModuleDetail.Type.productModule)){
					node.setPrettyName(ModuleDetail.Type.productModule.getDesc()+"("+node.getPrettyName()+")");
				}
				
			}
			
			// get children number
			if(moduleDetail.getType().equals(ModuleDetail.Type.folder.getCode())){
				int totalSubFolders = siteDesignDao.countModuleSubFoldersByCurrentFolderUuid(moduleDetail.getModuleuuid());
				if(totalSubFolders>0){
					node.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
				}
				
				int totalModule = siteDesignDao.countAllModulesUnderCurrentFolderByCurrentFolderUuid(moduleDetail.getModuleuuid());
				if(totalModule>0){
					node.addChildrenNumber(JsTreeNode.NodeType.module, totalModule);
				}
				
			}
			
			// find if any page using the module or scheduled to use the module
			List<ContainerModuleSchedule> containerModuleSchedules = siteDesignDao.findContainerModuleSchedulesByModuleUuid(node.getSystemName());
			if(containerModuleSchedules!=null && containerModuleSchedules.size()>0){
				String prettyname = node.getPrettyName();
				node.setPrettyName("<span class='prettyname'>"+prettyname+"</span><span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; ' domvalue='{\"topOffset\":20, \"leftOffset\":-30, \"ajaxCall\":{\"url\":\"/getModuleDetailInfo\", \"params\":\"moduleid="+node.getSystemName()+"\"}}'>["+containerModuleSchedules.size()+"]</span>");
			}else{
				String prettyname = node.getPrettyName();
				node.setPrettyName("<span class='prettyname'>"+prettyname+"</span>");
			}
			
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public void updateModuleTreeNodeForInstance(ModuleTreeNode node, String moduleDetailUuid) {
		if(node!=null && StringUtils.isNotBlank(node.getSystemName())){
			node.setType(ModuleDetail.Type.instance);
			
			
			ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(moduleDetailUuid);
			AccountDto loginAccount = accountService.getCurrentAccount();
			
			if(loginAccount!=null){
				if(moduledetail!=null){
					
					boolean moduleDetailReadPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, moduleDetailUuid);
					boolean moduleDetailModifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleDetailUuid);
					
					if(moduleDetailModifyPermission){
						node.setCssClassInfos("toInstanceEdit toInstanceDel newView toRename "+ModuleDetail.Type.instance.name());
					}else if(moduleDetailReadPermission){
						node.setCssClassInfos("toInstanceEdit "+ModuleDetail.Type.instance.name());
					}
				}else{
					if(moduleDetailUuid==null){
						node.setCssClassInfos("toInstanceEdit toInstanceDel newView toRename "+ModuleDetail.Type.instance.name());
					}
				}
			}
			
			// find if any views under the instance.
			List<InstanceView> instanceViews = siteDesignDao.findInstanceViewsByInstanceUuid(node.getSystemName());
//			int normalInstanceViews = 0;
//			int productInstanceViews = 0;
//			if(instanceViews!=null && instanceViews.size()>0){
//				for(InstanceView v : instanceViews){
//					if(StringUtils.equals(v.getViewtype(), InstanceView.Type.NormalInstanceView.getCode())){
//						normalInstanceViews = normalInstanceViews + 1;
//					}else if(StringUtils.equals(v.getViewtype(), InstanceView.Type.ProductInstanceView.getCode())){
//						productInstanceViews = productInstanceViews + 1;
//					}
//				}
//			}
			if(instanceViews!=null && instanceViews.size()>0){
				node.addChildrenNumber(JsTreeNode.NodeType.instanceView, instanceViews.size());
			}
//			if(productInstanceViews>0){
//				node.addChildrenNumber(ModuleDetail.Type.ProductInstanceView, productInstanceViews);
//			}
			
			
			// find if any page using the instance or scheduled to use the instance
			List<ModuleInstanceSchedule> moduleInstanceSchedules = siteDesignDao.findModuleInstanceSchedulesByInstanceUuid(node.getSystemName());
			if(moduleInstanceSchedules!=null && moduleInstanceSchedules.size()>0){
				String prettyname = node.getPrettyName();
				node.setPrettyName(prettyname+"<span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' domvalue='{\"topOffset\":20, \"leftOffset\":-280, \"width\":300, \"ajaxCall\":{\"url\":\"/getModuleInstanceInfo\", \"params\":\"instanceid="+node.getSystemName()+"\"}}'>"+moduleInstanceSchedules.size()+"</span>");
			}
		}
		
	}
	
	@Override
	@Transactional(readOnly=true)
	public void updateOrgNodeForSharedModuleTree(Organization o, Permission permission){
		if(o!=null && StringUtils.isNotBlank(o.getOrguuid())){
			
			// for css class
			if(o.isBizOrg()){
				o.addCssClass("bizOrg");
			}
			
			// assume there always have children under the org node: treeController.sharedModuleTreeMain only pass the org has shared module here.
			Map<NodeType, Integer> childrenNumbers = new HashMap<JsTreeNode.NodeType, Integer>();
			childrenNumbers.put(NodeType.module, 1);
			o.setChildrenNumbers(childrenNumbers);
			
		}
	}

	@Override
	@Transactional(readOnly=true)
	public PageTreeNode getPageTreeRoot(Long orgId, PageTreeLevelView.Type type) {
		if(orgId!=null){
			PageTreeLevelView treelevelView = siteDesignDao.getPageTreeRootViewLevel(orgId, type);
			if(treelevelView!=null){
				// unmarshal list of EntityTreeNode in levelview
				if(treelevelView.getNodes()!=null){
					XStream stream = new XStream(new DomDriver());
					//stream.processAnnotations(TopicTreeNode.class);
					stream.alias("treeNode", PageTreeNode.class);
					List<PageTreeNode> pageTreeNodes = (List<PageTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
					if(pageTreeNodes!=null && pageTreeNodes.size()>0){
						return pageTreeNodes.get(0);
					}
				}
			}
			
		}

		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<PageTreeNode> findPageTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			PageTreeLevelView treelevelView = siteDesignDao.getPageTreeLevelViewByParentUuid(parentNodeUuid);

			// unmarshal list of EntityTreeNode in levelview
			if(treelevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				//stream.processAnnotations(TopicTreeNode.class);
				stream.alias("treeNode", PageTreeNode.class);
				List<PageTreeNode> pageTreeNodes = (List<PageTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
				return pageTreeNodes;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public void updatePageTreeNodeForPage(PageTreeNode pageTreeNode, 
			PageDetail.Type type, 
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs) {
		
		if(pageTreeNode!=null && pageTreeNode.getSystemName()!=null){
			
			// get PageDetail by uuid
			PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(pageTreeNode.getSystemName());
			if(pageDetail!=null){
				
				Map<Permission.Type, Boolean> relatedPermissionValues = permissionService.getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
				
				// set type
				pageTreeNode.setType(PageDetail.Type.fromCode(pageDetail.getType()));
				
				// add more info 
				if(pageDetail.getType().equals(PageDetail.Type.Folder.getCode())){ // for folder
					
					if(StringUtils.isNotBlank(pageDetail.getParentuuid())){ // not the root
						if(relatedPermissionValues.get(Permission.Type.modify)){
							pageTreeNode.addCssClass("create delete toRename");
						}
					}else{
						if(relatedPermissionValues.get(Permission.Type.modify)){
							pageTreeNode.addCssClass("create");
						}
					}
				}else{ // for leaf - page node
					
					if(relatedPermissionValues.get(Permission.Type.read)){
						pageTreeNode.addCssClass("toConfig");
					}

					
					if(relatedPermissionValues.get(Permission.Type.copy)){
						pageTreeNode.addCssClass("toCopy toShare"); // toCopy: inside tree, toShare: between different trees
					}
					
					if(relatedPermissionValues.get(Permission.Type.modify)){
						pageTreeNode.addCssClass("delete pageDetail toRename");
					}
					
				}
				
				// add page type (desktop / mobile to the node's class
				pageTreeNode.addCssClass(type.getCode());
				
				// count all product entity types and put into EntityTreeNode's childrenNumbers
				if(pageDetail.getType().equals(PageDetail.Type.Folder.getCode())){ // only calculate numbers for current entity is folder
					// find count numbers for subfolder
					int totalSubFolders = siteDesignDao.countPageSubFoldersByCurrentFolderUuid(pageDetail.getPageuuid());
					if(totalSubFolders>0){
						pageTreeNode.addChildrenNumber(JsTreeNode.NodeType.folder, totalSubFolders);
					}
					// find count numbers for pages under current folder (includes all pages under current folder's subfolder)
					int totalSubPages = siteDesignDao.countAllPagesUnderCurrentFolderByCurrentFolderUuid(pageDetail.getPageuuid(), type);
					if(totalSubPages>0){
						pageTreeNode.addChildrenNumber(JsTreeNode.NodeType.leafPage, totalSubPages);
					}
				}
			}
			
			
		}
		
	}

	@Override
	@Transactional
	public String initialPageTree(Long orgId, PageTreeLevelView.Type type) {
		if(orgId!=null){
			Date now = new Date();
			
			PageDetail pageRoot = null;
			
			// find org's product tree
//			List<PageTreeLevelView> pageTreeLevelViews = siteDesignDao.findOrgPageTreeLevelViews(orgId, type);
			PageTreeLevelView pageTreeRootViewLevel = siteDesignDao.getPageTreeRootViewLevel(orgId, type);
			if(pageTreeRootViewLevel==null || StringUtils.isNotBlank(pageTreeRootViewLevel.getNodes())){
				XStream stream = new XStream(new DomDriver());
				//stream.autodetectAnnotations(true);
				// create one
				// 1) entityDetail
				String pageuuid = UUID.randomUUID().toString();
				
				String foldName = null;
				if(type.equals(PageTreeLevelView.Type.Desktop)){
					foldName = "Pages";
				}else if(type.equals(PageTreeLevelView.Type.Mobile)){
					foldName = "Mobile";
				}
				
				pageRoot = new PageDetail(null, 
						pageuuid, 
						foldName,
						null,
						null,
						PageDetail.Type.Folder.getCode(), 
						null,
						null,
						now, 
						orgId, 
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
				Long pageId = siteDesignDao.savePageDetail(pageRoot);
				
				// 2) create a page tree
				List<PageTreeNode> treeNodes = new ArrayList<PageTreeNode>();
				PageTreeNode treeNode = new PageTreeNode();
				treeNode.setPrettyName(foldName);
				treeNode.setSystemName(pageuuid);
				treeNodes.add(treeNode);
				// marshal treeNodeList
				stream.processAnnotations(ArrayList.class);
				StringWriter sw2 = new StringWriter();
				stream.alias("treeNode", PageTreeNode.class);
				stream.marshal(treeNodes, new CompactWriter(sw2));
				// create treeLevelView
				if(pageId!=null){
					PageTreeLevelView treeLevelView = new PageTreeLevelView(null, type.getCode(), 
							null, 
							sw2.toString(), // list of entityTreeNodes 
							now, 
							orgId, 
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
					Long treeLevelViewId = siteDesignDao.savePageTreeLevelView(treeLevelView);
				}
				
			}else{
				
				List<PageTreeNode> nodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, pageTreeRootViewLevel.getNodes());
				if(nodes!=null && nodes.size()>0){
					pageRoot = siteDesignDao.getPageDetailByUuid(nodes.get(0).getSystemName());
				}
			}
			
			if(pageRoot!=null) return pageRoot.getPageuuid();
		}
		return null;
	}

	// create org's module tree root Modules
	@Override
	@Transactional
	public String initialModuleTree(Long orgId) {
		Organization org = orgDao.getOrganizationById(orgId);
//		EntityDetail entityRoot = entityDao.getProductTreeRoot(orgId);


		if(org!=null){
			// get org 
			
			ModuleDetail root = null;
			
			List<ModuleTreeLevelView> treeLevelViews = siteDesignDao.findOrgModuleTreeLevelViews(orgId);
			if(treeLevelViews==null || treeLevelViews.size()<1){
				Date now = new Date();
				XStream stream = new XStream(new DomDriver());

				// create a detail node for root modules
				String rootuuid = UUID.randomUUID().toString();
				String rootPrettyName = "Modules";
				root = new ModuleDetail(null, 
						rootuuid, 
						rootPrettyName, 
						"module root for "+org.getOrgname(), 
						ModuleDetail.Type.folder.getCode(),
						null,
						null,
						null,
						null,
						null,
						org.getId(), 
						null,
						null,
						ModuleDetail.Visibility.visibleInsideOrg.getCode(), 
						now, 
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
				Long rootId = siteDesignDao.saveModuleDetail(root);
				
				// create a tree view for root
				if(rootId!=null){
					
					List<ModuleTreeNode> rootNodes = new ArrayList<ModuleTreeNode>();
					ModuleTreeNode rootNode = new ModuleTreeNode();
					rootNode.setPrettyName(rootPrettyName);
					rootNode.setSystemName(rootuuid);
					rootNodes.add(rootNode);
					// marshal treeNodeList
					stream.processAnnotations(ArrayList.class);
					StringWriter sw1 = new StringWriter();
					stream.alias("treeNode", ModuleTreeNode.class);
					stream.marshal(rootNodes, new CompactWriter(sw1));
					
					ModuleTreeLevelView rootLevelView = new ModuleTreeLevelView(null, 
							null, 
							sw1.toString(), 
							now, 
							org.getId(), 
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
					Long rootLevelViewId = siteDesignDao.saveModuleTreeLevelView(rootLevelView);
					
					
				}
			}else{ // get the moduleTree root
				root = siteDesignDao.getModuleTreeRoot(orgId);
			}
			if(root!=null) return root.getModuleuuid();
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ContainerTreeNode getContainerTreeRoot(Long orgId, String pageId) {
		if(orgId!=null && StringUtils.isNotBlank(pageId)){
			ContainerTreeLevelView treelevelView = siteDesignDao.getContainerTreeRootViewLevel(orgId, pageId);
			if(treelevelView!=null){
				// unmarshal list of EntityTreeNode in levelview
				if(treelevelView.getNodes()!=null){
					XStream stream = new XStream(new DomDriver());
					//stream.processAnnotations(TopicTreeNode.class);
					stream.alias("treeNode", ContainerTreeNode.class);
					List<ContainerTreeNode> pageTreeNodes = (List<ContainerTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
					if(pageTreeNodes!=null && pageTreeNodes.size()>0){
						return pageTreeNodes.get(0);
					}
				}
			}
			
		}

		return null;
	}

	/**
	 * @param detail
	 * @return ContainerTreeNode with all details but without subNodes
	 */
	private ContainerTreeNode getContainerTreeNodeFromDetail(ContainerDetail detail){
		if(detail!=null){
			ContainerTreeNode tn = new ContainerTreeNode();
			tn.setDeletable(detail.getDeletable()!=null&&detail.getDeletable().equals("1")?true:false);
			tn.setDirection(detail.getDirection());
			tn.setEditable(detail.getEditable()!=null&&detail.getEditable().equals("1")?true:false);
			tn.setHeight(detail.getHeight().intValue());
			tn.setLeftposition(detail.getLeftposition().intValue());
			tn.setPrettyName(detail.getPrettyname());
			tn.setSystemName(detail.getContaineruuid());
			tn.setTopposition(detail.getTopposition().intValue());
			tn.setWidth(detail.getWidth().intValue());
			tn.setHexColor(detail.getHexColor());
			tn.setCssClassInfos(detail.getClassnames());
			
			return tn;
		}
		return null;
	}
	
	/**
	 * This is a recursive method to populate subnodes for all nodes.
	 * @param currentNode
	 * @param containerDetailListMap
	 * @return current containerTreeNode with subnodes
	 */
	private ContainerTreeNode addSubnodesForNode(ContainerTreeNode currentNode, Map<String, List<ContainerDetail>> containerDetailListMap){
		if(currentNode!=null){
			if(containerDetailListMap!=null && containerDetailListMap.size()>0){
				List<ContainerDetail> subnodes = containerDetailListMap.get(currentNode.getSystemName());
				if(subnodes!=null && subnodes.size()>0){
					for(ContainerDetail c : subnodes){
						ContainerTreeNode tn = getContainerTreeNodeFromDetail(c);
						if(tn!=null){
							tn = addSubnodesForNode(tn, containerDetailListMap);
							currentNode.addSubnode(tn);
						}
					}
					//sort containers by position
					if(currentNode.getSubnodes()!=null && currentNode.getSubnodes().size()>0){
						Collections.sort(currentNode.getSubnodes(), ContainerTreeNode.positionSort);
					}
				}
			}
			return currentNode;
		}
		return null;
	}
	
	/**
	 * this method generate a tree structure from a list of containerDetail
	 * 
	 * @param containerDetails
	 * @return
	 */
	private ContainerTreeNode containerTreeGenerator(List<ContainerDetail> containerDetails){
		
		Map<String, List<ContainerDetail>> containerDetailListMap = new HashMap<String, List<ContainerDetail>>(); // create a parentUuid : containerDetailList for easy generate node:subNodes tree structure
		ContainerDetail rootContainer = null;
		if(containerDetails!=null && containerDetails.size()>0){
			for(ContainerDetail cd : containerDetails){
				//.... generate containerDetailListMap ...
				if(StringUtils.isNotBlank(cd.getParentuuid())){
					if(containerDetailListMap.get(cd.getParentuuid())!=null){
						containerDetailListMap.get(cd.getParentuuid()).add(cd);
					}else{
						List<ContainerDetail> dlist = new ArrayList<ContainerDetail>();
						dlist.add(cd);
						containerDetailListMap.put(cd.getParentuuid(), dlist);
					}
				}
				
				if(StringUtils.isBlank(cd.getParentuuid())){
					rootContainer = cd;
				}
			}
			
//			// sort container by the position
//			if(containerDetailListMap!=null && containerDetailListMap.size()>0){
//				for(Map.Entry<String, List<ContainerDetail>> entry : containerDetailListMap.entrySet()){
//					Collections.sort(entry.getValue(), );
//				}
//			}
			
			// generate ContainerTreeNode with subNodes by containerDetailMap and rootContainerUuid
			if(rootContainer!=null){
				ContainerTreeNode ctRoot = getContainerTreeNodeFromDetail(rootContainer);
				ctRoot = addSubnodesForNode(ctRoot, containerDetailListMap);
				return ctRoot;
			}
		}
		
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public ContainerTreeNode getContaienrTreeRootWithSubNodes(String pageId) {
		if(StringUtils.isNotBlank(pageId)){
			// get all containerDetails for pageId in orgId
			List<ContainerDetail> containerDetails = siteDesignDao.findPageContainerDetails(pageId);
			return containerTreeGenerator(containerDetails);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public void updateContainerTreeNodeForContainer(ContainerTreeNode containerTreeNode, String pageuuid) {
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		// get containerDetail
		ContainerDetail containerDetail = siteDesignDao.getContainerDetailByUuid(containerTreeNode.getSystemName());
		
		PageDetail pagedetail = siteDesignDao.getPageDetailByUuid(pageuuid);
		

		// set hexcolor
		if(loginAccount!=null && containerDetail!=null){
			containerTreeNode.setHexColor(containerDetail.getHexColor());
			
			int totalContainers = siteDesignDao.countSubContainersByCurrentContainerUuid(containerTreeNode.getSystemName());
			List<ContainerModuleSchedule> cmScheds = siteDesignDao.findContainerModuleSchedulesByContainerUuid(containerTreeNode.getSystemName());
			containerTreeNode.setChildrenNumbers(totalContainers+(cmScheds!=null?cmScheds.size():0));
			
			if(pageuuid!=null){ // pageuuid!=null means: need permissionfilter
				if(pagedetail!=null){
					
					boolean modifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, pageuuid);
					boolean readPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, pageuuid);
					
					// clean CssClassInfos: treeNode has some cssInfos from ContainerDetail's classnames, which is editable in container setup page for adding 
					//		some special class name for containers in pageRetrieve functions. To avoid putting special cssInfo for treeNode in coontainer setup page,
					//		we need to remove classnames first, and add special classInfos for treeNode.
					containerTreeNode.removeCssClassInfos();
					if(modifyPermissionAllow){
						// for container coloring
						containerTreeNode.setCssClassInfos("toColoring");
						// add class for treeNode (toRename, toDelete, toContainerModuleSched, toNewContainer)
						if(!containerTreeNode.isRoot()){
							// for del:
							containerTreeNode.setCssClassInfos("delete");
							// for rename
							containerTreeNode.setCssClassInfos("toRename");
						}
						// for toContainerModuleSched
						if(totalContainers<1){
							containerTreeNode.setCssClassInfos("toContainerModuleSched");
						}
						// for toNewContainer
						if(cmScheds==null || cmScheds.size()==0){
							containerTreeNode.setCssClassInfos("toNewContainer");
						}
						// for container's default module
						containerTreeNode.setCssClassInfos("toDefaultModule");
						
					}else if(readPermissionAllow){
						// for container's default module
						containerTreeNode.setCssClassInfos("toDefaultModule");
					}
					
				}
				
			}else{ // no need permission filter
				// for container coloring
				containerTreeNode.setCssClassInfos("toColoring");
				// add class for treeNode (toRename, toDelete, toContainerModuleSched, toNewContainer)
				if(!containerTreeNode.isRoot()){
					// for del:
					containerTreeNode.setCssClassInfos("delete");
					// for rename
					containerTreeNode.setCssClassInfos("toRename");
				}
				// for toContainerModuleSched
				if(totalContainers<1){
					containerTreeNode.setCssClassInfos("toContainerModuleSched");
				}
				// for toNewContainer
				if(cmScheds==null || cmScheds.size()==0){
					containerTreeNode.setCssClassInfos("toNewContainer");
				}
				// for container's default module
				containerTreeNode.setCssClassInfos("toDefaultModule");

				
			}
			
			
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<ContainerTreeNode> findContainerTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			ContainerTreeLevelView treelevelView = siteDesignDao.getContainerTreeLevelViewByParentUuid(parentNodeUuid);

			// unmarshal list of EntityTreeNode in levelview
			if(treelevelView!=null && treelevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				//stream.processAnnotations(TopicTreeNode.class);
				stream.alias("treeNode", ContainerTreeNode.class);
				List<ContainerTreeNode> treeNodes = (List<ContainerTreeNode>)stream.fromXML(treelevelView.getNodes().trim());
				return treeNodes;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleTreeNode> findModuleTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			ModuleTreeLevelView treeLevelView = siteDesignDao.getModuleTreeLevelViewByParentUuid(parentNodeUuid);
			
			if(treeLevelView!=null && treeLevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				//stream.processAnnotations(TopicTreeNode.class);
				stream.alias("treeNode", ModuleTreeNode.class);
				List<ModuleTreeNode> treeNodes = (List<ModuleTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
				return treeNodes;
			}
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TopicTreeNode> findTopicTreeNodesByParentNodeUuid(String parentNodeUuid) {
		if(StringUtils.isNotBlank(parentNodeUuid)){
			TopicTreeLevelView levelView = messageDao.getTopicTreeLevelViewByParentId(parentNodeUuid);
			if(levelView!=null && levelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				stream.alias("treeNode", TopicTreeNode.class);
				stream.processAnnotations(ArrayList.class);
				List<TopicTreeNode> nts = (List<TopicTreeNode>)stream.fromXML(levelView.getNodes());
				return nts;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public TopicTreeNode getTopicTreeRoot(Long orgId) {
		if(orgId!=null){
			TopicTreeLevelView rootLevelView = messageDao.getRootTopicTreeLevelView();
			if(rootLevelView!=null && rootLevelView.getNodes()!=null){
				XStream stream = new XStream(new DomDriver());
				stream.alias("treeNode", TopicTreeNode.class);
				stream.processAnnotations(ArrayList.class);
				List<TopicTreeNode> rts = (List<TopicTreeNode>)stream.fromXML(rootLevelView.getNodes());
				if(rts!=null && rts.size()>0) return rts.get(0);
			}
		}
		
		return null;
	}

	@Override
	public void updateTopicTreeNode(TopicTreeNode node, boolean editable) {
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		if(editable){
			node.addCssClassInfos("toConfig");
		}
		
		// count all types child number
		int totalSystemTopic = messageDao.countTotalSystemTopicUnderNode(node.getSystemName());
		node.addChildrenNumber(Topic.TopicType.SystemTopic, totalSystemTopic);
		
	}

	
	@Override
	@Transactional
	public ApiResponse delNodeDetail(Class nodeDetailClass, String nodeUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(currentAccount!=null && nodeDetailClass!=null && StringUtils.isNotBlank(nodeUuid)){
			
			// ----- for mediaDetail
			if(nodeDetailClass.equals(MediaDetail.class)){
				MediaDetail detail = mediaDao.getMediaDetailByUuid(nodeUuid);
				if(detail!=null){
					boolean isDelPermissionAllowed = true;
					
					if(detail.getNodetype().equals(MediaDetail.MediaType.folder.getCode())){
						
						// get all folder nodes ids
						List<String> folderWithSubfoldersIds = new ArrayList<String>();
						// get all nodes id (itself and all sub nodes
						List<Long> folderWithSubnodesIds = new ArrayList<Long>();
						folderWithSubnodesIds.add(detail.getId());
						folderWithSubfoldersIds.add(detail.getMediauuid());
						List<NodeDetail> subDetailNodes = mediaDao.findMedialDetailsUnderFolder(detail.getMediauuid());
						if(subDetailNodes!=null && subDetailNodes.size()>0){
							for(NodeDetail d : subDetailNodes){
								folderWithSubnodesIds.add(((MediaDetail)d).getId());
								if(((MediaDetail)d).getNodetype().equals(MediaDetail.MediaType.folder.getCode())){
									folderWithSubfoldersIds.add(((MediaDetail)d).getMediauuid());
								}
							}
						}
						
						// permission check
						//... need to check all children's permission also!!!!
						StringBuilder permissionErrorMsg = new StringBuilder();
						// permission check
						Permission mergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
						for(String folderUuid : folderWithSubfoldersIds){
							isDelPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, folderUuid);
							if(!isDelPermissionAllowed){
								permissionErrorMsg.append("User "+currentAccount.getFirstname()+" doesn't have permission to delete the media node : "+folderUuid+"\n");
								break;
							}
						}
						
						if(isDelPermissionAllowed){
							
							// the map to hold all deletable permissionedStuffs in Permission:PermissionedStuffs map
							Map<String, Set<Long>> permissinWithStuffs = new HashMap<String, Set<Long>>();
							
							// del all treeLevelviews with parentUuid is in folderWithSubfoldersIds list
							if(folderWithSubfoldersIds.size()>0){
								for(String uid : folderWithSubfoldersIds){
									MediaTreeLevelView mtv = mediaDao.getMediaTreeLevelViewByParentUuid(uid);
									if(mtv!=null){
										mediaDao.delMediaTreeLevelView(mtv.getId());
									}
									
									// find all deletable permissionStuffs for the module
									List<PermissionedStuff> pStuffs = permissionDao.findPermissionedStuffForStuff(uid);
									if(pStuffs!=null && pStuffs.size()>0){
										for(PermissionedStuff s : pStuffs){
											if(permissinWithStuffs.get(s.getPermissionuuid())!=null){
												permissinWithStuffs.get(s.getPermissionuuid()).add(s.getId());
											}else{
												Set<Long> stuffIds = new HashSet<Long>();
												stuffIds.add(s.getId());
												permissinWithStuffs.put(s.getPermissionuuid(), stuffIds);
											}
										}
									}
									
								}
							}
							
							// delete all permissionedstuffs from permissionPermissionedStuff relationship
							if(permissinWithStuffs.size()>0){
								for(Map.Entry<String, Set<Long>> e : permissinWithStuffs.entrySet()){
									Permission permission = permissionDao.getPermissionByUuid(e.getKey());
									if(permission!=null && permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
										for(Long sid : e.getValue()){
											permission.removePermissionedStuff(sid);
										}
										permissionDao.savePermission(permission);
									}
								}
							}
							
							// remove folder node from tree level view
							MediaTreeLevelView treeLevelView = mediaDao.getMediaTreeLevelViewHasNode(detail.getMediauuid());
							if(treeLevelView!=null){
								if(StringUtils.isNotBlank(treeLevelView.getNodes())){
									List<TreeNode> treeNodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, treeLevelView.getNodes());
									if(treeNodes!=null && treeNodes.size()>0){
										TreeNode delNode = null;
										for(TreeNode n : treeNodes){
											if(n.getSystemName().equals(nodeUuid)){
												delNode = n;
												break;
											}
										}
										if(delNode!=null){
											treeNodes.remove(delNode);
											if(treeNodes.size()>0){
												String updatedXml = TreeHelp.getXmlFromTreeNodes(treeNodes);
												treeLevelView.setNodes(updatedXml);
												mediaDao.saveMediaTreeLevelView(treeLevelView);
												
											}else{
												// del treelevelview since there has no node inside the levelview
												mediaDao.delMediaTreeLevelView(treeLevelView.getId());
											}
											
										}
									}
											
								}else{ // del the treelevelview if it has no any nodes
									mediaDao.delMediaTreeLevelView(treeLevelView.getId());
								}
							}
							
							// del all nodes details
							if(folderWithSubnodesIds.size()>0){
								for(Long id : folderWithSubnodesIds){
									mediaDao.delMediaDetailById(id);
								}
							}
							
							apires.setSuccess(true);
							apires.setResponse1(nodeUuid);
							
						}else{
							apires.setResponse1(permissionErrorMsg.toString());
						}
						
					}else{
						
						// permission check
						//... need to check all children's permission also!!!!
						StringBuilder permissionErrorMsg = new StringBuilder();
						// permission check
						Permission mergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
						isDelPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, detail.getParentuuid());

						if(isDelPermissionAllowed){
							
							// the map to hold all deletable permissionedStuffs in Permission:PermissionedStuffs map
							Map<String, Set<Long>> permissinWithStuffs = new HashMap<String, Set<Long>>();
							List<PermissionedStuff> pStuffs = permissionDao.findPermissionedStuffForStuff(detail.getMediauuid());
							if(pStuffs!=null && pStuffs.size()>0){
								for(PermissionedStuff s : pStuffs){
									if(permissinWithStuffs.get(s.getPermissionuuid())!=null){
										permissinWithStuffs.get(s.getPermissionuuid()).add(s.getId());
									}else{
										Set<Long> stuffIds = new HashSet<Long>();
										stuffIds.add(s.getId());
										permissinWithStuffs.put(s.getPermissionuuid(), stuffIds);
									}
								}
							}

							// delete all permissionedstuffs from permissionPermissionedStuff relationship
							if(permissinWithStuffs.size()>0){
								for(Map.Entry<String, Set<Long>> e : permissinWithStuffs.entrySet()){
									Permission permission = permissionDao.getPermissionByUuid(e.getKey());
									if(permission!=null && permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
										for(Long sid : e.getValue()){
											permission.removePermissionedStuff(sid);
										}
										permissionDao.savePermission(permission);
									}
								}
							}
							
							// find mediatreelevelview has nodeUuid
							MediaTreeLevelView treeLevelView = mediaDao.getMediaTreeLevelViewHasNode(detail.getMediauuid()); 
							if(treeLevelView!=null){
								if(StringUtils.isNotBlank(treeLevelView.getNodes())){
									List<TreeNode> treeNodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, treeLevelView.getNodes());
									if(treeNodes!=null && treeNodes.size()>0){
										TreeNode delNode = null;
										for(TreeNode n : treeNodes){
											if(n.getSystemName().equals(nodeUuid)){
												delNode = n;
												break;
											}
										}
										if(delNode!=null){
											treeNodes.remove(delNode);
											if(treeNodes.size()>0){
												String updatedXml = TreeHelp.getXmlFromTreeNodes(treeNodes);
												treeLevelView.setNodes(updatedXml);
												mediaDao.saveMediaTreeLevelView(treeLevelView);
												
											}else{
												// del treelevelview since there has no node inside the levelview
												mediaDao.delMediaTreeLevelView(treeLevelView.getId());
											}
											
										}
									}
											
								}else{ // del the treelevelview if it has no any nodes
									mediaDao.delMediaTreeLevelView(treeLevelView.getId());
									
									
								}
							}
							
						}else{
							permissionErrorMsg.append("User "+currentAccount.getFirstname()+" doesn't have permission to delete the media node : "+detail.getMediauuid()+"\n");
							apires.setResponse1(permissionErrorMsg.toString());
						}
						
					}
					
					
					if(isDelPermissionAllowed){
						mediaDao.delMediaDetailById(detail.getId());
						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
					}
					

				}else{
					apires.setResponse1("System can't find mediaDetail by nodeUuid: "+nodeUuid);
				}
			}else if (nodeDetailClass.equals(ModuleDetail.class)){ // ----- for moduleDetail
				
				ModuleDetail detail = siteDesignDao.getModuleDetailByUuid(nodeUuid);
				if(detail!=null){
					
					// get all moduledetails (self + all sub-moduledetails)
					List<ModuleDetail> allModuleDetails = new ArrayList<ModuleDetail>();
					allModuleDetails.add(detail);
					if(detail.getType().equals(ModuleDetail.Type.folder.getCode())){
						// find all sub-moduleDetails
						List<NodeDetail> allSubModuleDetails = siteDesignDao.findModuleDetailsUnderFolder(nodeUuid);
						if(allSubModuleDetails!=null && allSubModuleDetails.size()>0){
							for(NodeDetail d : allSubModuleDetails){
								allModuleDetails.add((ModuleDetail)d);
							}
//							allModuleDetails.addAll(allSubModuleDetails);
						}
					}
					
					//... need to check all children's permission also!!!!
					StringBuilder permissionErrorMsg = new StringBuilder();
					// permission check
					boolean isDelPermissionAllowed = true;
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
					for(ModuleDetail d : allModuleDetails){
						isDelPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, d.getModuleuuid());
						if(!isDelPermissionAllowed){
							permissionErrorMsg.append("User "+currentAccount.getFirstname()+" doesn't have permission to delete the module node : "+d.getPrettyname()+"\n");
							break;
						}
					}
					
					if(isDelPermissionAllowed){
						
						// the map to hold all deletable permissionedStuffs in Permission:PermissionedStuffs map
						Map<String, Set<Long>> permissinWithStuffs = new HashMap<String, Set<Long>>();
						
						for(ModuleDetail d : allModuleDetails){
							// delete all containermoduleschedule & moduleinstanceschedule
							if(d.getType().equals(ModuleDetail.Type.module.getCode()) || d.getType().equals(ModuleDetail.Type.productModule.getCode())){
								List<ContainerModuleSchedule> cms = siteDesignDao.findContainerModuleSchedulesByModuleUuid(d.getModuleuuid());
								if(cms!=null && cms.size()>0){
									for(ContainerModuleSchedule s : cms){
										siteDesignDao.delContainerModuleScheduleByUuid(s.getUuid());
									}
								}
								List<ModuleInstanceSchedule> mis = siteDesignDao.findModuleInstanceSchedulesByModuleUuid(d.getModuleuuid());
								if(mis!=null && mis.size()>0){
									for(ModuleInstanceSchedule s : mis){
										siteDesignDao.delModuleInstanceScheduleByUuid(s.getUuid());
									}
								}
							}
							
							// delete all instances for all the deleted ModuleDetails
							if(!d.getType().equals(ModuleDetail.Type.folder.getCode())){
								// for module instance
								List<ModuleInstance> moduleInstances = siteDesignDao.findModuleInstancesByModuleUuid(d.getModuleuuid());
								if(moduleInstances!=null && moduleInstances.size()>0){
									for(ModuleInstance mi : moduleInstances){
										siteDesignService.delModuleInstanceByUuid(mi.getModuleinstanceuuid());
									}
								}
								// for entitydetail
								List<EntityDetail> entityDetails = entityDao.findEntitiesUsingModule(d.getModuleuuid());
								if(entityDetails!=null && entityDetails.size()>0){
									for(EntityDetail ed : entityDetails){
										productService.deleteModuleSelectionForEntity(ed.getEntityuuid());
									}
								}
								
								// delete all module jsp & css
								siteDesignService.delModuleViewJspFile(d.getModuleuuid(), d.getOrganization_id());
								siteDesignService.delModuleViewCssFile(d.getModuleuuid(), d.getOrganization_id());
							}
							
							// find all deletable permissionStuffs for the module
							List<PermissionedStuff> pStuffs = permissionDao.findPermissionedStuffForStuff(d.getModuleuuid());
							if(pStuffs!=null && pStuffs.size()>0){
								for(PermissionedStuff s : pStuffs){
									if(permissinWithStuffs.get(s.getPermissionuuid())!=null){
										permissinWithStuffs.get(s.getPermissionuuid()).add(s.getId());
									}else{
										Set<Long> stuffIds = new HashSet<Long>();
										stuffIds.add(s.getId());
										permissinWithStuffs.put(s.getPermissionuuid(), stuffIds);
									}
								}
							}
							
							// and remove default module setup in containers
							List<ContainerDetail> containersHasDefaultModule = siteDesignDao.findContainerDetailsHasDefaultModule(d.getModuleuuid());
							if(containersHasDefaultModule!=null){
								for(ContainerDetail cd : containersHasDefaultModule){
									cd.setModuleuuid(null);
									siteDesignDao.saveContainerDetail(cd);
								}
							}
							
							// remove modulemeta
							Module module = SitedesignHelper.getModuleFromXml(d.getXml());
							if(module!=null && module.getAttrGroupList()!=null){
								Map<String, List<String>> uuidsWithType = SitedesignHelper.findAllElementUuids(module);
								for(Map.Entry<String, List<String>> entry : uuidsWithType.entrySet()) {
									if(entry.getValue()!=null){
										for(String uid : entry.getValue()){
											ModuleMeta modulemeta = siteDesignDao.getModuleMetaByTargetUuid(uid);
											if(modulemeta!=null){
												siteDesignDao.delModuleMetaById(modulemeta.getId());
											}
										}
									}
								}
							}
							
						}
						
						// delete all permissionedstuffs from permissionPermissionedStuff relationship
						if(permissinWithStuffs.size()>0){
							for(Map.Entry<String, Set<Long>> e : permissinWithStuffs.entrySet()){
								Permission permission = permissionDao.getPermissionByUuid(e.getKey());
								if(permission!=null && permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
									for(Long sid : e.getValue()){
										permission.removePermissionedStuff(sid);
									}
									permissionDao.savePermission(permission);
								}
							}
						}
						
						//delete all ModuleTreeLevelView
						delTreeNodeByUuid(ModuleTreeNode.class, detail.getModuleuuid());
						
						// delete folder and subfolder and all modules under the folder (delete ModuleDetail)
						for(ModuleDetail d : allModuleDetails){
							siteDesignDao.delModuleDetail(d.getId());
						}
						
						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}

				}else{
					apires.setResponse1("System can't find mediaDetail by nodeUuid: "+nodeUuid);
				}
			}else if(nodeDetailClass.equals(EntityDetail.class)){ // ----- for entityDetail
				EntityDetail detail = entityDao.getEntityDetailByUuid(nodeUuid);
				if(detail!=null){
					// find all entityDetails (including all folder and subfolders and products)
					List<EntityDetail> allEntityDetails = new ArrayList<EntityDetail>();
					allEntityDetails.add(detail);
					if(detail.getType().equals(EntityDetail.EntityType.folder.getCode())){
						List<NodeDetail> subEntityDetails = entityDao.findEntityDetailsUnderFolder(detail.getEntityuuid());
						if(subEntityDetails!=null && subEntityDetails.size()>0){
							for(NodeDetail d : subEntityDetails){
								allEntityDetails.add((EntityDetail)d);
							}
							
//							allEntityDetails.addAll(subEntityDetails);
						}
					}
					
					//... need to check all children's permission also!!!!
					StringBuilder permissionErrorMsg = new StringBuilder();
					// permission check
					boolean isDelPermissionAllowed = true;
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
					for(EntityDetail d : allEntityDetails){
						isDelPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, d.getEntityuuid());
						if(!isDelPermissionAllowed){
							permissionErrorMsg.append("User "+currentAccount.getFirstname()+" doesn't have permission to delete the entity node : "+d.getName()+"\n");
							break;
						}
					}
					
					if(isDelPermissionAllowed){
						
						
						
						// the map to hold all deletable permissionedStuffs in Permission:PermissionedStuffs map
						Map<String, Set<Long>> permissinWithStuffs = new HashMap<String, Set<Long>>();

						// delete all views
						for(EntityDetail d : allEntityDetails){
							
							List<InstanceView> views = siteDesignDao.findInstanceViewsByInstanceUuid(d.getEntityuuid());
							if(views!=null && views.size()>0){
								for(InstanceView view : views){
									siteDesignService.delInstanceViewByUuid(view.getInstanceviewuuid());
								}
							}
							
							// find all deletable permissionStuffs for the entity
							List<PermissionedStuff> pStuffs = permissionDao.findPermissionedStuffForStuff(d.getEntityuuid());
							if(pStuffs!=null && pStuffs.size()>0){
								for(PermissionedStuff s : pStuffs){
									if(permissinWithStuffs.get(s.getPermissionuuid())!=null){
										permissinWithStuffs.get(s.getPermissionuuid()).add(s.getId());
									}else{
										Set<Long> stuffIds = new HashSet<Long>();
										stuffIds.add(s.getId());
										permissinWithStuffs.put(s.getPermissionuuid(), stuffIds);
									}
								}
							}
							
							// find all modulemeta to delete
							Module module = SitedesignHelper.getModuleFromXml(d.getDetail());
							if(module!=null && module.getAttrGroupList()!=null){
								Map<String, List<String>> uuidsWithType = SitedesignHelper.findAllElementUuids(module);
								for(Map.Entry<String, List<String>> entry : uuidsWithType.entrySet()) {
									if(entry.getValue()!=null){
										for(String uid : entry.getValue()){
											ModuleMeta modulemeta = siteDesignDao.getModuleMetaByTargetUuid(uid);
											if(modulemeta!=null){
												siteDesignDao.delModuleMetaById(modulemeta.getId());
											}
										}
									}
								}
							}
							
						}
						
						// delete all permissionedstuffs from permissionPermissionedStuff relationship
						if(permissinWithStuffs.size()>0){
							for(Map.Entry<String, Set<Long>> e : permissinWithStuffs.entrySet()){
								Permission permission = permissionDao.getPermissionByUuid(e.getKey());
								if(permission!=null && permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
									for(Long sid : e.getValue()){
										permission.removePermissionedStuff(sid);
									}
									permissionDao.savePermission(permission);
								}
							}
						}
						
						// delete tree
						delTreeNodeByUuid(EntityTreeNode.class, detail.getEntityuuid());
						
						// delete all entitydetails
						for(EntityDetail d : allEntityDetails){
							entityDao.delEntityDetailById(d.getId());
						}

						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					
					
				}else{
					apires.setResponse1("System can't find entityDetail by nodeUuid: "+nodeUuid);
				}
				
			}else if(nodeDetailClass.equals(ContainerDetail.class)){ // ---- for containerDetail
				ContainerDetail detail = siteDesignDao.getContainerDetailByUuid(nodeUuid);
				if(detail!=null){
					PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(detail.getPageuuid());
					
					// check page modify permisson
					boolean isDelPermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.modify, pageDetail.getPageuuid());
					
					if(isDelPermissionAllowed){
						
						List<ContainerDetail> delDetails = new ArrayList<ContainerDetail>();
						delDetails.add(detail);
						
						// find all subDetails under current detail
						List<ContainerDetail> subDetails = siteDesignDao.findSubContainerDetails(nodeUuid);
						if(subDetails!=null && subDetails.size()>0){
							delDetails.addAll(subDetails);
						}
						
						// find and delete all containermoduleschedule & moduleinstanceschedule under these details
						List<ContainerModuleSchedule> delCmScheds = new ArrayList<ContainerModuleSchedule>();
						for(ContainerDetail d : delDetails){
							List<ContainerModuleSchedule> cmScheds = siteDesignDao.findContainerModuleSchedulesByContainerUuid(d.getContaineruuid());
							if(cmScheds!=null && cmScheds.size()>0){
								delCmScheds.addAll(cmScheds);
							}
						}
						
						List<ModuleInstanceSchedule> delMiScheds = new ArrayList<ModuleInstanceSchedule>();
						for(ContainerModuleSchedule s : delCmScheds){
							List<ModuleInstanceSchedule> miScheds = siteDesignDao.findModuleInstanceSchedulesByContainerModuleScheduleUuid(s.getUuid());
							if(miScheds!=null && miScheds.size()>0){
								delMiScheds.addAll(miScheds);
							}
						}
						
						for(ModuleInstanceSchedule s : delMiScheds){
							siteDesignDao.delModuleInstanceScheduleByUuid(s.getUuid());
						}
						for(ContainerModuleSchedule s : delCmScheds){
							siteDesignDao.delContainerModuleScheduleByUuid(s.getUuid());
						}
						
						// delete tree
						delTreeNodeByUuid(ContainerTreeNode.class, detail.getContaineruuid());
						
						// delete all details
						StringBuilder deletedContainerDetailIds = new StringBuilder();
						int count = 0;
						for(ContainerDetail d : delDetails){
							if(count==0){
								deletedContainerDetailIds.append(d.getContaineruuid());
							}else{
								deletedContainerDetailIds.append(", ").append(d.getContaineruuid());
							}
							count++;
							siteDesignDao.delContainerDetailById(d.getId());
						}
						
						
						
						// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed 4) add pageMeta
						Organization org = orgDao.getOrganizationById(detail.getOrganization_id());
						Accountprofile creator = accountDao.getAccountProfileByAccountId(currentAccount.getId());
						
						// 1) log the activity
						String key_oid = "orgId";
						String key_oname = "orgName";
						String key_cid = "operatorId";
						String key_cname = "operatorName";
						String key_ctuuid = "containerUuids";
						ActivityLogData activityLogData = new ActivityLogData();
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put(key_oid, org.getId());
						dataMap.put(key_oname, org.getOrgname());
						dataMap.put(key_cname, 
								creator!=null?
										(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
										:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc());
						dataMap.put(key_cid, creator.getAccount_id());
						dataMap.put(key_ctuuid, deletedContainerDetailIds);
						activityLogData.setDataMap(dataMap);
						String desc = messageFromPropertiesService.getMessageSource().getMessage("deletePageContainer", 
								new Object[] { 
									creator!=null?
										(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
										:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc(), 
									deletedContainerDetailIds, 
									PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase(),
									pageDetail.getPageuuid()}, Locale.US);
						activityLogData.setDesc(desc);
						Long activityLogId = messageService.newActivity(currentAccount.getId(), detail.getOrganization_id(), ActivityType.deletePageContainer, activityLogData);
						
						// 2) create a topic
						Topic topic = new Topic(null,
								UUID.randomUUID().toString(),
								"Container delete",
								new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".modify").toString(),
								Topic.AccessLevel.privateTopic.getCode(),
								Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
								org.getId(),
								"Container delete",
								new Date(),
								null,
								Topic.TopicType.SystemTopic.getCode(),
								null,
								null,
								null
								);
						Long topicId = messageService.newTopicWithAncestor(topic);
						
						// 3) post to newsfeed
						if(activityLogId!=null){
							// do post ...
							messageService.postFeed(topicId, activityLogId);
						}
						
						// 4) add change to pageMeta's changelist
						siteDesignService.addToPageChangeList(pageDetail.getId(), activityLogId, desc);
						
						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
						apires.setResponse2(pageDetail.getPageuuid());
						
					}else{
						apires.setResponse1("You don't have permission to delete container");
					}
					
					
				}else{
					apires.setResponse1("System can't find containerDetail by nodeUuid: "+nodeUuid);
				}
			}else if(nodeDetailClass.equals(PageDetail.class)){ // del page
				
				PageDetail pagedetail = siteDesignDao.getPageDetailByUuid(nodeUuid);
				if(pagedetail!=null){
					
					boolean isDelPermissionAllowed = true;
					
					if(pagedetail.getType().equals(PageDetail.Type.Folder.getCode())){ // for folder
						
						List<PageDetail> delDetails = new ArrayList<PageDetail>();
						delDetails.add(pagedetail);
						
						// find all sub-details
						List<PageDetail> subDetails = siteDesignDao.findSubPageDetails(nodeUuid);
						if(subDetails!=null && subDetails.size()>0){
							delDetails.addAll(subDetails);
						}
						
						StringBuilder permissionErrorMsg = new StringBuilder();
						for(PageDetail pd : delDetails){
							if(!isDelPermissionAllowed){
								permissionErrorMsg.append("User "+currentAccount.getFirstname()+" doesn't have permission to delete the page node : "+pd.getPrettyname()+"\n");
								break;
							}
							
							isDelPermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.modify, pd.getPageuuid());
							
						}
						
						if(isDelPermissionAllowed){
							
							// the map to hold all deletable permissionedStuffs in Permission:PermissionedStuffs map
							Map<String, Set<Long>> permissinWithStuffs = new HashMap<String, Set<Long>>();
							
							// del tree
							delTreeNodeByUuid(PageTreeNode.class, nodeUuid);
							
							for(PageDetail pd : delDetails){
								
								String delPageUuid = pd.getPageuuid();

								// del containers
								List<ContainerDetail> pageContainers = siteDesignDao.findPageContainerDetails(pd.getPageuuid());
								if(pageContainers!=null && pageContainers.size()>0){
									for(ContainerDetail cd : pageContainers){
										delNodeDetail(ContainerDetail.class, cd.getContaineruuid());
									}
								}
								
								// del detail
								siteDesignDao.delPageDetailById(pd.getId());
							
								// del all meta
								// Note: the reason to del pagemeta after del pagedetail is: pageMeta will be recreated during the del container process!!!
								PageMeta pagemeta = siteDesignDao.getPageMetaByPageUuid(delPageUuid);
								if(pagemeta!=null) siteDesignDao.delPageMetaById(pagemeta.getId());
								
								// extra steps to del root containerTreeLevelView & pagemeta
								List<ContainerTreeLevelView> containerTreeLevelViews = siteDesignDao.findContainerTreeLevelViewForPage(delPageUuid);
								if(containerTreeLevelViews!=null && containerTreeLevelViews.size()>0){
									for(ContainerTreeLevelView v : containerTreeLevelViews){
										siteDesignDao.delContainerTreeLevelView(v.getId());
									}
								}
								
								
								// find all deletable permissionStuffs for the module
								List<PermissionedStuff> pStuffs = permissionDao.findPermissionedStuffForStuff(pd.getPageuuid());
								if(pStuffs!=null && pStuffs.size()>0){
									for(PermissionedStuff s : pStuffs){
										if(permissinWithStuffs.get(s.getPermissionuuid())!=null){
											permissinWithStuffs.get(s.getPermissionuuid()).add(s.getId());
										}else{
											Set<Long> stuffIds = new HashSet<Long>();
											stuffIds.add(s.getId());
											permissinWithStuffs.put(s.getPermissionuuid(), stuffIds);
										}
									}
								}
							}
							
							// delete all permissionedstuffs from permissionPermissionedStuff relationship
							if(permissinWithStuffs.size()>0){
								for(Map.Entry<String, Set<Long>> e : permissinWithStuffs.entrySet()){
									Permission permission = permissionDao.getPermissionByUuid(e.getKey());
									if(permission!=null && permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
										for(Long sid : e.getValue()){
											permission.removePermissionedStuff(sid);
										}
										permissionDao.savePermission(permission);
									}
								}
							}
							
							
							
						}else{
							apires.setResponse1(permissionErrorMsg.toString());
						}
						
					}else{ // for page
						
						isDelPermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.modify, pagedetail.getPageuuid());
						
						if(isDelPermissionAllowed){
							
							// del tree
							delTreeNodeByUuid(PageTreeNode.class, nodeUuid);
							
							// find all containers and del
							List<ContainerDetail> pageContainers = siteDesignDao.findPageContainerDetails(nodeUuid);
							if(pageContainers!=null && pageContainers.size()>0){
								for(ContainerDetail cd : pageContainers){
									delNodeDetail(ContainerDetail.class, cd.getContaineruuid());
								}
							}
							
							// del current pagedetail
							siteDesignDao.delPageDetailById(pagedetail.getId());
							
							// del meta
							// Note: the reason to del pagemeta after del pagedetail is: pageMeta will be recreated during the del container process!!!
							PageMeta pageMeta = siteDesignDao.getPageMetaByPageUuid(nodeUuid);
							if(pageMeta!=null){
								siteDesignDao.delPageMetaById(pageMeta.getId());
							}
							
							// extra steps to del root containerTreeLevelView & pagemeta
							List<ContainerTreeLevelView> containerTreeLevelViews = siteDesignDao.findContainerTreeLevelViewForPage(nodeUuid);
							if(containerTreeLevelViews!=null && containerTreeLevelViews.size()>0){
								for(ContainerTreeLevelView v : containerTreeLevelViews){
									siteDesignDao.delContainerTreeLevelView(v.getId());
								}
							}
							
						}else{
							apires.setResponse1("You don't have permission for del action.");
						}
						
					}
					
					if(isDelPermissionAllowed){
						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
					}
					
				}else{
					apires.setResponse1("System can't find pagedetail by nodeUuid: "+nodeUuid);
				}
				
			}else if(nodeDetailClass.equals(Permission.class)){ // permission delete
				
				Permission permission = permissionDao.getPermissionByUuid(nodeUuid);
				if(permission!=null){
					// hubba's admin can delete permission, org's admin can delete org's permission
					if(currentAccount.isSystemDefaultAccount() && 
							(currentAccount.isBizAccount() || currentAccount.getOrganization_id().longValue()==permission.getTargetorg().longValue())){
						
						// 1. remove account or group relationship with permission
						// 2. find permission and permissionedStuff and remove.
						Account permissionAccount = null;
						Accountgroup permissionGroup = null;
						if(permission.getAccount_id()!=null){
							permissionAccount = accountDao.getAccountPojoById(permission.getAccount_id());
						}else if(permission.getGroup_id()!=null){
							permissionGroup = groupDao.getGroup(permission.getGroup_id());
						}
						// for 1
						if(permissionAccount!=null && permissionAccount.getPermissions()!=null && permissionAccount.getPermissions().size()>0){
							boolean updated = false;
							for(Permission p : permissionAccount.getPermissions()){
								if(p.getId().longValue()==permission.getId().longValue()){
									updated = true;
									permissionAccount.getPermissions().remove(p);
									break;
								}
							}
							if(updated){
								accountDao.saveAccount(permissionAccount);
							}
						}
						if(permissionGroup!=null && permissionGroup.getPermissions()!=null && permissionGroup.getPermissions().size()>0){
							boolean updated = false;
							for(Permission p : permissionGroup.getPermissions()){
								if(p.getId().longValue()==permission.getId().longValue()){
									updated = true;
									permissionGroup.getPermissions().remove(p);
									break;
								}
							}
							if(updated){
								groupDao.saveGroup(permissionGroup);
							}
						}
						
						// for 2:
						permissionDao.delPermissionById(permission.getId());
						
						apires.setSuccess(true);
						apires.setResponse1(nodeUuid);
						
						
					}else{
						apires.setResponse1("Only admin account can delete permission.");
					}

					
				}else{
					apires.setResponse1("System can't find permission by id: "+nodeUuid);
				}
				
			}
			
			
		}else{
			apires.setResponse1("System can delete the node, because the nodeUuid is null or node's class is not defined!");
		}
		
		return apires;
	}
	
	
	@Override
	@Transactional
	public ApiResponse copyTreeNodeToAnotherTree(Class nodeDetailClass, String nodeUuid, String targetUuid){
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		if(loginAccount!=null && nodeDetailClass!=null && StringUtils.isNotBlank(nodeUuid) && StringUtils.isNotBlank(targetUuid)){
			if(nodeDetailClass.equals(ModuleDetail.class)){
				
				ModuleDetail moduledetail_source = siteDesignDao.getModuleDetailByUuid(nodeUuid);
				ModuleDetail moduledetail_target = siteDesignDao.getModuleDetailByUuid(targetUuid);
				
				if(moduledetail_source!=null && moduledetail_target!=null && moduledetail_target.getType().equals(ModuleDetail.Type.folder.getCode())){
					
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					// check the modify permission for moduledetail_target for loginAccount
					boolean modifyPermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, moduledetail_target.getModuleuuid());
					if(modifyPermissionAllow){

						// check if moduledetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String moduleDetailRootUuid = null;
						
						if(StringUtils.isNotBlank(moduledetail_source.getPath())){
							int firstDivideIndex = moduledetail_source.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								moduleDetailRootUuid = moduledetail_source.getPath().substring(0, firstDivideIndex);
							}else{
								moduleDetailRootUuid = moduledetail_source.getPath();
							}
						}
						if(StringUtils.isNotBlank(moduleDetailRootUuid)){
							if(StringUtils.isNotBlank(moduledetail_target.getPath())){
								isInsideTree = (moduledetail_target.getPath().indexOf(moduleDetailRootUuid)>-1);
							}else{ // the root
								isInsideTree = (moduledetail_target.getModuleuuid().indexOf(moduleDetailRootUuid)>-1);
							}
						}

						if(!isInsideTree){
							
							// the list holds all moduleDetails that need to be copied!!
							List<ModuleDetail> copyModuleList = new ArrayList<ModuleDetail>();
							copyModuleList.add(moduledetail_source);
							
							// find all subfolders and moduledetails under the folder
							if(moduledetail_source.getType().equals(ModuleDetail.Type.folder.getCode())){
								List<NodeDetail> subMds = siteDesignDao.findModuleDetailsUnderFolder(moduledetail_source.getModuleuuid());
								if(subMds!=null && subMds.size()>0){
									for(NodeDetail d : subMds){
										copyModuleList.add((ModuleDetail)d);
									}
									
//									copyModuleList.addAll(subMds);
								}
							}
							
							// the list holds all moduleDetails that has permission to be copied!!
							Map<String, ModuleDetail> hasCopyPermissionUuidWithModuleMap = new HashMap<String, ModuleDetail>(); // ********
							// check the permissions for all moduledetails
							for(ModuleDetail m : copyModuleList){
								if(permissionService.isPermissionAllowed(mergedPermission, Permission.Type.copy, m.getModuleuuid())){
									hasCopyPermissionUuidWithModuleMap.put(m.getModuleuuid(), m);
								}
							}
							
							// find all treeLevelViews has node in hasCopyPermissionUuidWithModuleMap
							Map<String, ModuleTreeLevelView> parentuuidWithTreeViewMap = new HashMap<String, ModuleTreeLevelView>(); // *******
							for(Map.Entry<String, ModuleDetail> e : hasCopyPermissionUuidWithModuleMap.entrySet()){
								ModuleTreeLevelView tview = siteDesignDao.getModuleTreeLevelViewHasNode(e.getKey());
								if(tview!=null && parentuuidWithTreeViewMap.get(tview.getParentuuid())==null){
									parentuuidWithTreeViewMap.put(tview.getParentuuid(), tview);
								}
							}
							
							// remove all node in treeView if node's systemID isnot inside hasCopyPermissionUuidWithModuleMap
							// also, find all treeViews without any nodes inside, and remove
							// also, find all treeViews' parentUUid isnot inside hasCopyPermissionUuidWithModuleMap, these treeViews will be the root views
							List<ModuleTreeLevelView> rootviews = new ArrayList<ModuleTreeLevelView>();
							List<TreeLevelView> hasCopyPermissionTreeViews = new ArrayList<TreeLevelView>();
							List<String> moduleDetailUuidsInTree = new ArrayList<String>(); // this list holds all moduleDetail's uuids which remaining in the tree, the purpose for this list is for double-check the hasCopyPermissionUuidWithModuleMap
							for(Map.Entry<String, ModuleTreeLevelView> e : parentuuidWithTreeViewMap.entrySet()){
								ModuleTreeLevelView clonedTv = e.getValue().clone();
								clonedTv.setCreator_id(loginAccount.getId());
								clonedTv.setOrganization_id(moduledetail_target.getOrganization_id().longValue());
								
								List<ModuleTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, clonedTv.getNodes());
								List<ModuleTreeNode> tnodes_del = new ArrayList<ModuleTreeNode>();
								if(tnodes!=null && tnodes.size()>0){
									for(ModuleTreeNode n : tnodes){
										if(hasCopyPermissionUuidWithModuleMap.get(n.getSystemName())==null){
											tnodes_del.add(n);
										}else{
											moduleDetailUuidsInTree.add(n.getSystemName());
										}
									}
									if(tnodes_del.size()>0){
										tnodes.removeAll(tnodes_del);
									}
									
									// has nodes inside hasCopyPermissionUuidWithModuleMap
									if(tnodes.size()>0){
										clonedTv.setNodes(TreeHelp.getXmlFromTreeNodes(tnodes));
										hasCopyPermissionTreeViews.add(clonedTv);
										
										if(hasCopyPermissionUuidWithModuleMap.get(clonedTv.getParentuuid())==null){
											rootviews.add(clonedTv);
										}
									}
								}
							}
							
							// finally:-) clone ModuleDetails from hasCopyPermissionUuidWithModuleMap based on moduleDetailUuidsInTree, 
							//            Most Important is to replace the uuid & path !!!
							// key val: hasCopyPermissionUuidWithModuleMap, moduleDetailUuidsInTree, rootviews, hasCopyPermissionTreeViews, moduledetail_source, moduledetail_target
							Map<String, ModuleDetail> finalCopiedUuidWithModuleDetails = new HashMap<String, ModuleDetail>();
							Map<String, String> oldFolderuuidWithNewFolderuuidMap = new HashMap<String, String>();// hold all old new folder uuid for path replacement.
//							oldFolderuuidWithNewFolderuuidMap.put(key, value)
							StringBuilder usedParentUuidsForTreeViews = new StringBuilder();
							
							
							// oldNewModuleUuidsMap: Map<old-moduleDetailUuid, Map<[old-moduleDetailUuid, new-moduleDetailUuid],[old-groupUuid, new-groupUuid], [old-attrUuid, new-attrUuid]>>
							Map<String, Map<String, String>> oldNewModuleUuidsMap = new HashMap<String, Map<String, String>>();
							
							for(String uuid : moduleDetailUuidsInTree){
								ModuleDetail md = hasCopyPermissionUuidWithModuleMap.get(uuid);
								if(md!=null){
									Map<String, String> oldNewUuids = new HashMap<String, String>();
									ModuleDetail copiedMd = md.clone(oldNewUuids);
									copiedMd.setCreator_id(loginAccount.getId());
									copiedMd.setOrganization_id(moduledetail_target.getOrganization_id().longValue());
									
									if(oldNewUuids.size()>0){
										oldNewModuleUuidsMap.put(md.getModuleuuid(), oldNewUuids);
									}
									
									
									if(copiedMd.getType().equals(ModuleDetail.Type.folder.getCode())){
										oldFolderuuidWithNewFolderuuidMap.put(md.getModuleuuid(), copiedMd.getModuleuuid());
									}
									
									finalCopiedUuidWithModuleDetails.put(copiedMd.getModuleuuid(), copiedMd);
									
									// replaced uuid in treeView
									for(TreeLevelView tv : hasCopyPermissionTreeViews){
										// replace parentUuid in treeview
										if(StringUtils.equals(tv.getParentuuid(), moduledetail_source.getParentuuid())){
											((ModuleTreeLevelView)tv).setParentuuid(moduledetail_target.getModuleuuid());
											usedParentUuidsForTreeViews.append(moduledetail_target.getModuleuuid()).append(",");
										}else if(StringUtils.equals(tv.getParentuuid(), md.getModuleuuid())){
											((ModuleTreeLevelView)tv).setParentuuid(copiedMd.getModuleuuid());
											usedParentUuidsForTreeViews.append(copiedMd.getModuleuuid()).append(",");
										}
										
										// replace systemUUid in treeView's nodes
										if(StringUtils.contains(tv.getNodes(), md.getModuleuuid())){
											((ModuleTreeLevelView)tv).setNodes(tv.getNodes().replaceAll(md.getModuleuuid(), copiedMd.getModuleuuid()));
										}
									}
								}
							}
							
							// double check all parentUuids and node's systemids are replace in hasCopyPermissionTreeViews.
							// replace parentUuids to moduledetail_target.getModuleuuid() for some nodes which parent folders' copy permission is false
							for(TreeLevelView tv : hasCopyPermissionTreeViews){
								if(usedParentUuidsForTreeViews.indexOf(tv.getParentuuid())<0){ // the tv's parent has never been changed, this could because the parent folder are not allowed to copy
									oldFolderuuidWithNewFolderuuidMap.put(tv.getParentuuid(), moduledetail_target.getModuleuuid());
									((ModuleTreeLevelView)tv).setParentuuid(moduledetail_target.getModuleuuid());
									// find all nodes in finalCopiedUuidWithModuleDetails, and set node's parentuuid = moduledetail_source.getParentuuid():
									// reason: since these nodes' parentFolder doesn't allow to have copy permission, move these nodes to the drag root!!
									List<ModuleTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, tv.getNodes());
									if(tnodes!=null && tnodes.size()>0){
										for(ModuleTreeNode n : tnodes){
											if(finalCopiedUuidWithModuleDetails.get(n.getSystemName())!=null){
												finalCopiedUuidWithModuleDetails.get(n.getSystemName()).setParentuuid(moduledetail_source.getParentuuid());
											}
										}
									}
								}
							}
							
							// replace path in moduleDetail: this can't be merged inside previous loop, since all new moduleDetail uuids are generated in previous loop.
							boolean everythingIsFine = true;
							String oldBasePath = moduledetail_source.getPath();
							String newBasePath = StringUtils.isNotBlank(moduledetail_target.getPath())?moduledetail_target.getPath()+"/"+moduledetail_target.getModuleuuid():moduledetail_target.getModuleuuid();
							if(StringUtils.isBlank(newBasePath)){
								everythingIsFine = false;
							}

							for(Map.Entry<String, ModuleDetail> me : finalCopiedUuidWithModuleDetails.entrySet()){
								
								if(!everythingIsFine) break;
								
								String parentUuid = null;
								String path = null;
								
								if(StringUtils.isBlank(me.getValue().getParentuuid()) || StringUtils.equals(me.getValue().getParentuuid(), moduledetail_source.getParentuuid())){
									parentUuid = moduledetail_target.getModuleuuid();
									path = newBasePath;
								}else{
									
									parentUuid = oldFolderuuidWithNewFolderuuidMap.get(me.getValue().getParentuuid());
									
									path = me.getValue().getPath();
									// base path replacement
									if(StringUtils.isBlank(oldBasePath)){
										path = newBasePath+"/"+path;
									}else{
										path = StringUtils.replace(path, oldBasePath, newBasePath);
									}
									// all parentUuid replacement for path
									for(Map.Entry<String, String> e : oldFolderuuidWithNewFolderuuidMap.entrySet()){
										path = StringUtils.replace(path, e.getKey(), e.getValue());
									}
									
								}
								
								if(StringUtils.isNotBlank(parentUuid) && StringUtils.isNotBlank(path)){
									me.getValue().setParentuuid(parentUuid);
									me.getValue().setPath(path);
								}else{
									everythingIsFine = false;
								}
							}
							
							if(everythingIsFine){
								// do the save for finalCopiedUuidWithModuleDetails & hasCopyPermissionTreeViews
								for(Map.Entry<String, ModuleDetail> e : finalCopiedUuidWithModuleDetails.entrySet()){
									// save:
									Long mid = siteDesignDao.saveModuleDetail(e.getValue());
									
									// write jsp & css to file if moduledetail has jsp & css
									if(mid!=null){
										if(StringUtils.isNotBlank(e.getValue().getJsp())){
											siteDesignService.writeModuleJspToFile(mid);
										}
										if(StringUtils.isNotBlank(e.getValue().getCss())){
											siteDesignService.writeModuleCssToFile(mid);
										}
									}
									
									
								}
								
								for(TreeLevelView v : hasCopyPermissionTreeViews){
									// save or merge : in most of the time, you need merge the new treeview with the exist treeview which have same parentUuid!!
									ModuleTreeLevelView existTreeView = siteDesignDao.getModuleTreeLevelViewByParentUuid(v.getParentuuid());
									if(existTreeView!=null){
										List<ModuleTreeNode> existNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, existTreeView.getNodes());
										List<ModuleTreeNode> newNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, v.getNodes());
										if(existNodes==null){
											existNodes = new ArrayList<ModuleTreeNode>();
										}
										existNodes.addAll(newNodes);
										existTreeView.setNodes(TreeHelp.getXmlFromTreeNodes(existNodes));
										siteDesignDao.saveModuleTreeLevelView(existTreeView);
										
									}else{
										siteDesignDao.saveModuleTreeLevelView((ModuleTreeLevelView)v);
									}
								}
								
								apires.setSuccess(true);
								apires.setResponse1(oldNewModuleUuidsMap);
								
							}
							
							
						}else{
							apires.setResponse1("You can't move node inside tree bying using copyTreeNodeToAnotherTree function!!");
						}
						
						
					}else{
						apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have edit permission for target: "+moduledetail_target.getPrettyname());
					}
					
					
				}else{
					apires.setResponse1("System can't find moduledetails by uuid: "+nodeUuid+" and/or "+targetUuid);
				}
			}else if(nodeDetailClass.equals(EntityDetail.class)){
				
				EntityDetail entitydetail_source = entityDao.getEntityDetailByUuid(nodeUuid);
				EntityDetail entitydetail_target = entityDao.getEntityDetailByUuid(targetUuid);
				
				if(entitydetail_source!=null && entitydetail_target!=null && entitydetail_target.getType().equals(EntityDetail.EntityType.folder.getCode())){
					
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					// check the modify permission for entitydetail_target for loginAccount
					boolean modifyPermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, entitydetail_target.getEntityuuid());
					if(modifyPermissionAllow){

						// check if entitydetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String entityDetailRootUuid = null;
						
						if(StringUtils.isNotBlank(entitydetail_source.getPath())){
							int firstDivideIndex = entitydetail_source.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								entityDetailRootUuid = entitydetail_source.getPath().substring(0, firstDivideIndex);
							}else{
								entityDetailRootUuid = entitydetail_source.getPath();
							}
						}
						if(StringUtils.isNotBlank(entityDetailRootUuid)){
							if(StringUtils.isNotBlank(entitydetail_target.getPath())){
								isInsideTree = (entitydetail_target.getPath().indexOf(entityDetailRootUuid)>-1);
							}else{
								isInsideTree = (entitydetail_target.getEntityuuid().indexOf(entityDetailRootUuid)>-1);
							}
						}

						if(!isInsideTree){
							
							// the list holds all entityDetails that need to be copied!!
							List<EntityDetail> copyEntityList = new ArrayList<EntityDetail>();
							copyEntityList.add(entitydetail_source);
							
							// find all subfolders and entitydetails under the folder
							if(entitydetail_source.getType().equals(EntityDetail.EntityType.folder.getCode())){
								List<NodeDetail> subEds = entityDao.findEntityDetailsUnderFolder(entitydetail_source.getEntityuuid());
								if(subEds!=null && subEds.size()>0){
									for(NodeDetail d : subEds){
										copyEntityList.add((EntityDetail)d);
									}
//									copyEntityList.addAll(subEds);
								}
							}
							
							// the list holds all entityDetails that has permission to be copied!!
							Map<String, EntityDetail> hasCopyPermissionUuidWithEntityMap = new HashMap<String, EntityDetail>(); // ********
							// check the permissions for all moduledetails
							for(EntityDetail m : copyEntityList){
								if(permissionService.isPermissionAllowed(mergedPermission, Permission.Type.copy, m.getEntityuuid())){
									
									// check entity's template copy permission
									boolean templatePermissionAllow = false;
									
									if(StringUtils.isBlank(m.getModuleuuid())){
										templatePermissionAllow = true;
									}else{
										templatePermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.copy, m.getModuleuuid());
									}
									
									if(templatePermissionAllow){
										hasCopyPermissionUuidWithEntityMap.put(m.getEntityuuid(), m);
									}
									
									
								}
							}
							
							// find all treeLevelViews has node in hasCopyPermissionUuidWithEntityMap
							Map<String, EntityTreeLevelView> parentuuidWithTreeViewMap = new HashMap<String, EntityTreeLevelView>(); // *******
							for(Map.Entry<String, EntityDetail> e : hasCopyPermissionUuidWithEntityMap.entrySet()){
								EntityTreeLevelView tview = entityDao.getEntityTreeLevelViewHasNode(e.getKey());
								if(tview!=null && parentuuidWithTreeViewMap.get(tview.getParentuuid())==null){
									parentuuidWithTreeViewMap.put(tview.getParentuuid(), tview);
								}
							}
							
							// remove all node in treeView if node's systemID isnot inside hasCopyPermissionUuidWithEntityMap
							// also, find all treeViews without any nodes inside, and remove
							// also, find all treeViews' parentUUid isnot inside hasCopyPermissionUuidWithEntityMap, these treeViews will be the root views
							List<EntityTreeLevelView> rootviews = new ArrayList<EntityTreeLevelView>();
							List<TreeLevelView> hasCopyPermissionTreeViews = new ArrayList<TreeLevelView>();
							List<String> entityDetailUuidsInTree = new ArrayList<String>(); // this list holds all entityDetail's uuids which remaining in the tree, the purpose for this list is for double-check the hasCopyPermissionUuidWithEntityMap
							for(Map.Entry<String, EntityTreeLevelView> e : parentuuidWithTreeViewMap.entrySet()){
								EntityTreeLevelView clonedTv = e.getValue().clone();
								clonedTv.setCreator_id(loginAccount.getId());
								clonedTv.setOrganization_id(entitydetail_target.getOrganization_id().longValue());
								
								List<EntityTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, clonedTv.getNodes());
								List<EntityTreeNode> tnodes_del = new ArrayList<EntityTreeNode>();
								if(tnodes!=null && tnodes.size()>0){
									for(EntityTreeNode n : tnodes){
										if(hasCopyPermissionUuidWithEntityMap.get(n.getSystemName())==null){
											tnodes_del.add(n);
										}else{
											entityDetailUuidsInTree.add(n.getSystemName());
										}
									}
									if(tnodes_del.size()>0){
										tnodes.removeAll(tnodes_del);
									}
									
									// has nodes inside hasCopyPermissionUuidWithEntityMap
									if(tnodes.size()>0){
										clonedTv.setNodes(TreeHelp.getXmlFromTreeNodes(tnodes));
										hasCopyPermissionTreeViews.add(clonedTv);
										
										if(hasCopyPermissionUuidWithEntityMap.get(clonedTv.getParentuuid())==null){
											rootviews.add(clonedTv);
										}
									}
								}
							}
							
							// finally:-) clone EntityDetails from hasCopyPermissionUuidWithEntityMap based on entityDetailUuidsInTree, 
							//            Most Important is to replace the uuid & path !!!
							// key val: hasCopyPermissionUuidWithEntityMap, entityDetailUuidsInTree, rootviews, hasCopyPermissionTreeViews, entitydetail_source, entitydetail_target
							Map<String, EntityDetail> finalCopiedUuidWithEntityDetails = new HashMap<String, EntityDetail>();
							Map<String, String> oldFolderuuidWithNewFolderuuidMap = new HashMap<String, String>();// hold all old new folder uuid for path replacement.
//							oldFolderuuidWithNewFolderuuidMap.put(key, value)
							StringBuilder usedParentUuidsForTreeViews = new StringBuilder();
							//
							Set<String> moduleDetailUUidsForCopy = new HashSet<String>();
							for(String uuid : entityDetailUuidsInTree){
								EntityDetail ed = hasCopyPermissionUuidWithEntityMap.get(uuid);
								if(ed!=null){
									EntityDetail copiedEd = ed.clone();
									copiedEd.setCreator_id(loginAccount.getId());
									copiedEd.setOrganization_id(entitydetail_target.getOrganization_id().longValue());
									copiedEd.setExpiredate(null);
									
									if(StringUtils.isNotBlank(copiedEd.getModuleuuid())){
										moduleDetailUUidsForCopy.add(copiedEd.getModuleuuid());
									}
//									copiedEd.setModuleuuid(???);
									
									
									if(copiedEd.getType().equals(EntityDetail.EntityType.folder.getCode())){
										oldFolderuuidWithNewFolderuuidMap.put(ed.getEntityuuid(), copiedEd.getEntityuuid());
									}
									
									finalCopiedUuidWithEntityDetails.put(copiedEd.getEntityuuid(), copiedEd);
									
									// replaced uuid in treeView
									for(TreeLevelView tv : hasCopyPermissionTreeViews){
										// replace parentUuid in treeview
										if(StringUtils.equals(tv.getParentuuid(), entitydetail_source.getParentuuid())){
											((EntityTreeLevelView)tv).setParentuuid(entitydetail_target.getEntityuuid());
											usedParentUuidsForTreeViews.append(entitydetail_target.getEntityuuid()).append(",");
										}else if(StringUtils.equals(tv.getParentuuid(), ed.getEntityuuid())){
											((EntityTreeLevelView)tv).setParentuuid(copiedEd.getEntityuuid());
											usedParentUuidsForTreeViews.append(copiedEd.getEntityuuid()).append(",");
										}
										
										// replace systemUUid in treeView's nodes
										if(StringUtils.contains(tv.getNodes(), ed.getEntityuuid())){
											((EntityTreeLevelView)tv).setNodes(tv.getNodes().replaceAll(ed.getEntityuuid(), copiedEd.getEntityuuid()));
										}
									}
								}
							}
							
							// double check all parentUuids and node's systemids are replace in hasCopyPermissionTreeViews.
							// replace parentUuids to moduledetail_target.getModuleuuid() for some nodes which parent folders' copy permission is false
							for(TreeLevelView tv : hasCopyPermissionTreeViews){
								if(usedParentUuidsForTreeViews.indexOf(tv.getParentuuid())<0){ // the tv's parent has never been changed, this could because the parent folder are not allowed to copy
									oldFolderuuidWithNewFolderuuidMap.put(tv.getParentuuid(), entitydetail_target.getEntityuuid());
									((EntityTreeLevelView)tv).setParentuuid(entitydetail_target.getEntityuuid());
									// find all nodes in finalCopiedUuidWithEntityDetails, and set node's parentuuid = entitydetail_source.getParentuuid():
									// reason: since these nodes' parentFolder doesn't allow to have copy permission, move these nodes to the drag root!!
									List<EntityTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, tv.getNodes());
									if(tnodes!=null && tnodes.size()>0){
										for(EntityTreeNode n : tnodes){
											if(finalCopiedUuidWithEntityDetails.get(n.getSystemName())!=null){
												finalCopiedUuidWithEntityDetails.get(n.getSystemName()).setParentuuid(entitydetail_source.getParentuuid());
											}
										}
									}
								}
							}
							
							// replace path in entityDetail: this can't be merged inside previous loop, since all new entityDetail uuids are generated in previous loop.
							boolean everythingIsFine = true;
							String oldBasePath = entitydetail_source.getPath();
							String newBasePath = StringUtils.isNotBlank(entitydetail_target.getPath())?entitydetail_target.getPath()+"/"+entitydetail_target.getEntityuuid():entitydetail_target.getEntityuuid();
							if(StringUtils.isBlank(newBasePath)){
								everythingIsFine = false;
							}

							for(Map.Entry<String, EntityDetail> ee : finalCopiedUuidWithEntityDetails.entrySet()){
								
								if(!everythingIsFine) break;
								
								String parentUuid = null;
								String path = null;
								
								if(StringUtils.isBlank(ee.getValue().getParentuuid()) || StringUtils.equals(ee.getValue().getParentuuid(), entitydetail_source.getParentuuid())){
									parentUuid = entitydetail_target.getEntityuuid();
									path = newBasePath;
								}else{
									
									parentUuid = oldFolderuuidWithNewFolderuuidMap.get(ee.getValue().getParentuuid());
									
									path = ee.getValue().getPath();
									// base path replacement
									if(StringUtils.isBlank(oldBasePath)){
										path = newBasePath+"/"+path;
									}else{
										path = StringUtils.replace(path, oldBasePath, newBasePath);
									}
									// all parentUuid replacement for path
									for(Map.Entry<String, String> e : oldFolderuuidWithNewFolderuuidMap.entrySet()){
										path = StringUtils.replace(path, e.getKey(), e.getValue());
									}
									
								}
								
								if(StringUtils.isNotBlank(parentUuid) && StringUtils.isNotBlank(path)){
									ee.getValue().setParentuuid(parentUuid);
									ee.getValue().setPath(path);
								}else{
									everythingIsFine = false;
								}
							}
							
							if(everythingIsFine){
								
								// list org's all moduleDetails
								List<ModuleDetail> orgModuleDetailsWithoutFolder = siteDesignDao.findOrgModulesWithoutFolder(entitydetail_target.getOrganization_id());
								//List<ModuleDetail> orgModuleDetailsOnlyFolder = siteDesignDao.findOrgModulesOnlyFolder(entitydetail_target.getOrganization_id());
								
								// *** copy all moduleDetailUUidsForCopy to target
								// oldNewModuleUuidsMap: Map<old-moduleDetailUuid, Map<[old-moduleDetailUuid, new-moduleDetailUuid],[old-groupUuid, new-groupUuid], [old-attrUuid, new-attrUuid]>>
								Map<String, Map<String, String>> oldNewModuleUuidsMap = new HashMap<String, Map<String, String>>();
								if(moduleDetailUUidsForCopy.size()>0){
									// ceate a folder in target org's moduletree: (sourceOrgSysName)
									// 1. find or create if has source org folder in the tree:
									String sourceOrgFoderUuidInTargetTree = createSourceOrgFolderInTargetTree(ModuleDetail.class, entitydetail_source.getOrganization_id(), entitydetail_target.getOrganization_id());
									if(sourceOrgFoderUuidInTargetTree!=null){
										// 2. find or create a folder based on date under sourceOrgFoderUuidInTargetTree
										Date now = new Date();
										String datePattern = "yyyy-MM-dd";
										SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
										String nowDate = dateformat.format(now);
										String todayFolderToHoldAllModules = findOrCreateFolderBasedOnName(ModuleDetail.class, entitydetail_target.getOrganization_id(), sourceOrgFoderUuidInTargetTree, nowDate);
										
										for(String muuid : moduleDetailUUidsForCopy){
											
											if(oldNewModuleUuidsMap.get(muuid)==null){
												ModuleDetail sourceModuledetail = siteDesignDao.getModuleDetailByUuid(muuid);
												if(sourceModuledetail!=null){
													// find if orgModuleDetailsWithoutFolder has same module
													boolean sameModuleFindInTarget = false;
													if(orgModuleDetailsWithoutFolder!=null){
														for(ModuleDetail md : orgModuleDetailsWithoutFolder){
															if(sourceModuledetail.equals(md)){ // be very careful the order of the comparison (which is this, which is that), which will determine the key & value of thisThatUuids
																Map<String, String> oldNewUuids = new HashMap<String, String>();
																oldNewUuids.put(sourceModuledetail.getModuleuuid(), md.getModuleuuid());
																
																if(sourceModuledetail.getThisThatUuids()!=null && sourceModuledetail.getThisThatUuids().size()>0){
																	oldNewUuids.putAll(sourceModuledetail.getThisThatUuids());
																}
																
																sameModuleFindInTarget = true;
																
																if(oldNewUuids.size()>0){
																	oldNewModuleUuidsMap.put(sourceModuledetail.getModuleuuid(), oldNewUuids);
																}
																
																break;
															}
														}
													}
													if(!sameModuleFindInTarget){ // clone a moduleDetail
														ApiResponse apiresFromModuleCopy = copyTreeNodeToAnotherTree(ModuleDetail.class, sourceModuledetail.getModuleuuid(), todayFolderToHoldAllModules);
														
														if(apiresFromModuleCopy!=null && apiresFromModuleCopy.isSuccess()){
															Map<String, Map<String, String>> oldNewUuidsFromModuleCopy = (Map<String, Map<String, String>>)apiresFromModuleCopy.getResponse1();
															if(oldNewUuidsFromModuleCopy!=null && oldNewUuidsFromModuleCopy.size()>0){
																oldNewModuleUuidsMap.putAll(oldNewUuidsFromModuleCopy);
															}
														}
													}
													
												}
												
											}
											
										}
										
									}
								}
								
								// do the save for finalCopiedUuidWithModuleDetails & hasCopyPermissionTreeViews
								for(Map.Entry<String, EntityDetail> e : finalCopiedUuidWithEntityDetails.entrySet()){
									
									// update the moduleuuid with new uuid
									if(e.getValue().getModuleuuid()!=null && oldNewModuleUuidsMap.get(e.getValue().getModuleuuid())!=null){
										Map<String, String> allOldNewUuidsForModuleDetail = oldNewModuleUuidsMap.get(e.getValue().getModuleuuid());
										// replace the moduleUuid
										e.getValue().setModuleuuid(allOldNewUuidsForModuleDetail.get(e.getValue().getModuleuuid()));
										
										// replace the group's moduleGroupUuid and attr's moduleAttrUuid in EntityDetail's detail
										Module entityModule = SitedesignHelper.getModuleFromXml(e.getValue().getDetail());
										if(entityModule!=null && entityModule.getAttrGroupList()!=null && entityModule.getAttrGroupList().size()>0){
											
											boolean moduleChanged = false;
											for(AttrGroup g : entityModule.getAttrGroupList()){
												if(g.getModuleGroupUuid()!=null && allOldNewUuidsForModuleDetail.get(g.getModuleGroupUuid())!=null){
													g.setModuleGroupUuid(allOldNewUuidsForModuleDetail.get(g.getModuleGroupUuid()));
													moduleChanged = true;
												}
												
												if(g.getAttrList()!=null && g.getAttrList().size()>0){
													for(ModuleAttribute a : g.getAttrList()){
														if(a.getModuleAttrUuid()!=null && allOldNewUuidsForModuleDetail.get(a.getModuleAttrUuid())!=null){
															a.setModuleAttrUuid(allOldNewUuidsForModuleDetail.get(a.getModuleAttrUuid()));
															moduleChanged = true;
														}
													}
												}
												
											}
											if(moduleChanged){
												e.getValue().setDetail(SitedesignHelper.getXmlFromModule(entityModule));
											}
											
										}
										
									}
									
									// save:
									Long eid = entityDao.saveEntityDetail(e.getValue());
									
								}
								
								for(TreeLevelView v : hasCopyPermissionTreeViews){
									// save or merge : in most of the time, you need merge the new treeview with the exist treeview which have same parentUuid!!
									EntityTreeLevelView existTreeView = entityDao.getEntityTreeLevelViewByParentUuid(v.getParentuuid());
									if(existTreeView!=null){
										List<EntityTreeNode> existNodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, existTreeView.getNodes());
										List<EntityTreeNode> newNodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, v.getNodes());
										if(existNodes==null){
											existNodes = new ArrayList<EntityTreeNode>();
										}
										existNodes.addAll(newNodes);
										existTreeView.setNodes(TreeHelp.getXmlFromTreeNodes(existNodes));
										entityDao.saveEntityTreeLevelView(existTreeView);
										
									}else{
										entityDao.saveEntityTreeLevelView((EntityTreeLevelView)v);
									}
								}
								
								apires.setSuccess(true);
								
							}
							
							
						}else{
							apires.setResponse1("You can't move node inside tree bying using copyTreeNodeToAnotherTree function!!");
						}
						
						
					}else{
						apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have edit permission for target: "+entitydetail_target.getName());
					}
					
					
				}else{
					apires.setResponse1("System can't find entitydetails by uuid: "+nodeUuid+" and/or "+targetUuid);
				}
				
			}else if(nodeDetailClass.equals(PageDetail.class)){
				
				PageDetail pagedetail_source = siteDesignDao.getPageDetailByUuid(nodeUuid);
				PageDetail pagedetail_target = siteDesignDao.getPageDetailByUuid(targetUuid);
				
				if(pagedetail_source!=null && pagedetail_target!=null && pagedetail_target.getType().equals(PageDetail.Type.Folder.getCode())){
					
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					// check the modify permission for entitydetail_target for loginAccount
					boolean modifyPermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, pagedetail_target.getPageuuid());
					if(modifyPermissionAllow){

						// check if entitydetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String pageDetailRootUuid = null;
						
						if(StringUtils.isNotBlank(pagedetail_source.getPath())){
							int firstDivideIndex = pagedetail_source.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								pageDetailRootUuid = pagedetail_source.getPath().substring(0, firstDivideIndex);
							}else{
								pageDetailRootUuid = pagedetail_source.getPath();
							}
						}
						if(StringUtils.isNotBlank(pageDetailRootUuid)){
							if(StringUtils.isNotBlank(pagedetail_target.getPath())){
								isInsideTree = (pagedetail_target.getPath().indexOf(pageDetailRootUuid)>-1);
							}else{
								isInsideTree = (pagedetail_target.getPageuuid().indexOf(pageDetailRootUuid)>-1);
							}
						}

						if(!isInsideTree){
							
							// the list holds all pageDetails that need to be copied!!
							List<PageDetail> copyPageList = new ArrayList<PageDetail>();
							copyPageList.add(pagedetail_source);
							
							// find all subfolders and pagedetails under the folder
							if(pagedetail_source.getType().equals(PageDetail.Type.Folder.getCode())){
								List<NodeDetail> subPds = siteDesignDao.findPageDetailsUnderFolder(pagedetail_source.getPageuuid());
								if(subPds!=null && subPds.size()>0){
									for(NodeDetail d : subPds){
										copyPageList.add((PageDetail)d);
									}
									
//									copyPageList.addAll(subPds);
								}
							}
							
							
							// hold all containerDetailUuids need to be copied!
//							Set<String> containerDetailUuidsForCopy = new HashSet<String>();
							
							// hold all moduledetailUuids need to be copied!
							Set<String> moduleDetailUuidsForCopy = new HashSet<String>();
							
							// hold all pageMetas need to be copied!
//							Set<String> pageMetaUuidsForCopy = new HashSet<String>();
							
							// the list holds all pagedetails that has permission to be copied!!
							Map<String, PageDetail> hasCopyPermissionUuidWithPageMap = new HashMap<String, PageDetail>(); // ********
							// check the permissions for all moduledetails
							for(PageDetail p : copyPageList){
								if(permissionService.isPermissionAllowed(mergedPermission, Permission.Type.copy, p.getPageuuid())){
									
									// check entity's template copy permission
									boolean pageDefaultModulesPermissionAllow = true;
									
									// get page containers
									List<String> containerUUids_mustCopy = SitedesignHelper.findAllContainerUuidsFromPageDetail(p.getDetail());
									List<ContainerDetail> pageContainers = siteDesignDao.findPageContainerDetails(p.getPageuuid());
									// get pageContainers' default modules
//									List<String> pageModuleDetails = new ArrayList<String>();
//									List<String> pageContainerUuids = new ArrayList<String>();
									if(pageContainers!=null && pageContainers.size()>0 && containerUUids_mustCopy!=null && containerUUids_mustCopy.size()>0){
										for(ContainerDetail c : pageContainers){
											if(!pageDefaultModulesPermissionAllow) break;
											
//											pageContainerUuids.add(c.getContaineruuid());
											
											if(StringUtils.isNotBlank(c.getModuleuuid()) && containerUUids_mustCopy.contains(c.getContaineruuid())){
												pageDefaultModulesPermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.copy, c.getModuleuuid());
//												pageModuleDetails.add(c.getModuleuuid());
											}
										}
									}
									
									if(pageDefaultModulesPermissionAllow){
//										if(pageModuleDetails.size()>0)
//											moduleDetailUuidsForCopy.addAll(pageModuleDetails);
										
//										if(pageContainerUuids.size()>0)
//											containerDetailUuidsForCopy.addAll(pageContainerUuids);
//										
//										PageMeta pageMeta = siteDesignDao.getPageMetaByPageUuid(p.getPageuuid());
//										if(pageMeta!=null && pageMeta.getPageuuid().equals(p.getPageuuid())){
//											pageMetaUuidsForCopy.add(pageMeta.getPagemetauuid());
//										}
										
										hasCopyPermissionUuidWithPageMap.put(p.getPageuuid(), p);

										
									}
									
								}
							}
							
							// find all treeLevelViews has node in hasCopyPermissionUuidWithPageMap
							Map<String, PageTreeLevelView> parentuuidWithTreeViewMap = new HashMap<String, PageTreeLevelView>(); // *******
							for(Map.Entry<String, PageDetail> e : hasCopyPermissionUuidWithPageMap.entrySet()){
								PageTreeLevelView tview = siteDesignDao.getPageTreeLevelViewHasNode(e.getKey());
								if(tview!=null && parentuuidWithTreeViewMap.get(tview.getParentuuid())==null){
									parentuuidWithTreeViewMap.put(tview.getParentuuid(), tview);
								}
							}
							
							// remove all node in treeView if node's systemID isnot inside hasCopyPermissionUuidWithPageMap
							// also, find all treeViews without any nodes inside, and remove
							// also, find all treeViews' parentUUid isnot inside hasCopyPermissionUuidWithPageMap, these treeViews will be the root views
							List<PageTreeLevelView> rootviews = new ArrayList<PageTreeLevelView>();
							List<TreeLevelView> hasCopyPermissionTreeViews = new ArrayList<TreeLevelView>();
							List<String> pageDetailUuidsInTree = new ArrayList<String>(); // this list holds all entityDetail's uuids which remaining in the tree, the purpose for this list is for double-check the hasCopyPermissionUuidWithEntityMap
							for(Map.Entry<String, PageTreeLevelView> e : parentuuidWithTreeViewMap.entrySet()){
								PageTreeLevelView clonedTv = e.getValue().clone();
								clonedTv.setCreator_id(loginAccount.getId());
								clonedTv.setOrganization_id(pagedetail_target.getOrganization_id().longValue());
								
								List<PageTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, clonedTv.getNodes());
								List<PageTreeNode> tnodes_del = new ArrayList<PageTreeNode>();
								if(tnodes!=null && tnodes.size()>0){
									for(PageTreeNode n : tnodes){
										if(hasCopyPermissionUuidWithPageMap.get(n.getSystemName())==null){
											tnodes_del.add(n);
										}else{
											pageDetailUuidsInTree.add(n.getSystemName());
										}
									}
									if(tnodes_del.size()>0){
										tnodes.removeAll(tnodes_del);
									}
									
									// has nodes inside hasCopyPermissionUuidWithEntityMap
									if(tnodes.size()>0){
										clonedTv.setNodes(TreeHelp.getXmlFromTreeNodes(tnodes));
										hasCopyPermissionTreeViews.add(clonedTv);
										
										if(hasCopyPermissionUuidWithPageMap.get(clonedTv.getParentuuid())==null){
											rootviews.add(clonedTv);
										}
									}
								}
							}
							
							// finally:-) clone PageDetails from hasCopyPermissionUuidWithPageMap based on pageDetailUuidsInTree, 
							//            Most Important is to replace the uuid & path !!!
							// key val: hasCopyPermissionUuidWithPageMap, pageDetailUuidsInTree, rootviews, hasCopyPermissionTreeViews, pagedetail_source, pagedetail_target
							Map<String, PageDetail> finalCopiedUuidWithPageDetails = new HashMap<String, PageDetail>();
							Map<String, String> oldFolderuuidWithNewFolderuuidMap = new HashMap<String, String>();// hold all old new folder uuid for path replacement.
//							oldFolderuuidWithNewFolderuuidMap.put(key, value)
							StringBuilder usedParentUuidsForTreeViews = new StringBuilder();
							//
//							Set<String> moduleDetailUUidsForCopy = new HashSet<String>();
							for(String uuid : pageDetailUuidsInTree){
								PageDetail pd = hasCopyPermissionUuidWithPageMap.get(uuid);
								if(pd!=null){
									
									ApiResponse pagedetailCloneRes = siteDesignService.clonePage(pd.getPageuuid(), targetUuid);
									
									Long clonedPageId = null;
									if(pagedetailCloneRes.getResponse1()!=null){
										clonedPageId = (Long)pagedetailCloneRes.getResponse1();
									}
									
									
									PageDetail copiedPd = siteDesignDao.getPageDetailById(clonedPageId);
									
									if(copiedPd!=null){
//										copiedPd.setCreator_id(loginAccount.getId());
//										copiedPd.setOrganization_id(pagedetail_target.getOrganization_id().longValue());
										
										List<ContainerDetail> copiedContainersForCopiedPg = siteDesignDao.findPageContainerDetails(copiedPd.getPageuuid());
										if(copiedContainersForCopiedPg!=null && copiedContainersForCopiedPg.size()>0){
											for(ContainerDetail cd : copiedContainersForCopiedPg){
												if(StringUtils.isNotBlank(cd.getModuleuuid())){
													moduleDetailUuidsForCopy.add(cd.getModuleuuid());
												}
											}
										}
										
										
										if(copiedPd.getType().equals(PageDetail.Type.Folder.getCode())){
											oldFolderuuidWithNewFolderuuidMap.put(pd.getPageuuid(), copiedPd.getPageuuid());
										}
										
										finalCopiedUuidWithPageDetails.put(copiedPd.getPageuuid(), copiedPd);
										
										// replaced uuid in treeView
										for(TreeLevelView tv : hasCopyPermissionTreeViews){
											// replace parentUuid in treeview
											if(StringUtils.equals(tv.getParentuuid(), pagedetail_source.getParentuuid())){
												((PageTreeLevelView)tv).setParentuuid(pagedetail_target.getPageuuid());
												usedParentUuidsForTreeViews.append(pagedetail_target.getPageuuid()).append(",");
											}else if(StringUtils.equals(tv.getParentuuid(), pd.getPageuuid())){
												((PageTreeLevelView)tv).setParentuuid(copiedPd.getPageuuid());
												usedParentUuidsForTreeViews.append(copiedPd.getPageuuid()).append(",");
											}
											
											// replace systemUUid in treeView's nodes
											if(StringUtils.contains(tv.getNodes(), pd.getPageuuid())){
												((PageTreeLevelView)tv).setNodes(tv.getNodes().replaceAll(pd.getPageuuid(), copiedPd.getPageuuid()));
											}
										}
										
									}
									
								}
							}
							
							// double check all parentUuids and node's systemids are replace in hasCopyPermissionTreeViews.
							// replace parentUuids to pagedetail_target.getPageuuid() for some nodes which parent folders' copy permission is false
							// NOTE: this for loop could be never reached because page folder are not allowed to copy or share!!!
							for(TreeLevelView tv : hasCopyPermissionTreeViews){
								if(usedParentUuidsForTreeViews.indexOf(tv.getParentuuid())<0){ // the tv's parent has never been changed, this could because the parent folder are not allowed to copy
									oldFolderuuidWithNewFolderuuidMap.put(tv.getParentuuid(), pagedetail_target.getPageuuid());
									((PageTreeLevelView)tv).setParentuuid(pagedetail_target.getPageuuid());
									// find all nodes in finalCopiedUuidWithEntityDetails, and set node's parentuuid = entitydetail_source.getParentuuid():
									// reason: since these nodes' parentFolder doesn't allow to have copy permission, move these nodes to the drag root!!
									List<PageTreeNode> tnodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, tv.getNodes());
									if(tnodes!=null && tnodes.size()>0){
										for(PageTreeNode n : tnodes){
											if(finalCopiedUuidWithPageDetails.get(n.getSystemName())!=null){
												finalCopiedUuidWithPageDetails.get(n.getSystemName()).setParentuuid(pagedetail_source.getParentuuid());
											}
										}
									}
								}
							}
							
							// replace path in pageDetail: this can't be merged inside previous loop, since all new entityDetail uuids are generated in previous loop.
							
							// NOTE: Don't need this part since path and parent's uuid are replaced in clonePage function.
							boolean everythingIsFine = true;
//							String oldBasePath = pagedetail_source.getPath();
//							String newBasePath = StringUtils.isNotBlank(pagedetail_target.getPath())?pagedetail_target.getPath()+"/"+pagedetail_target.getPageuuid():pagedetail_target.getPageuuid();
//							if(StringUtils.isBlank(newBasePath)){
//								everythingIsFine = false;
//							}
//
//							for(Map.Entry<String, PageDetail> pp : finalCopiedUuidWithPageDetails.entrySet()){
//								
//								if(!everythingIsFine) break;
//								
//								String parentUuid = null;
//								String path = null;
//								
//								if(StringUtils.isBlank(pp.getValue().getParentuuid()) || StringUtils.equals(pp.getValue().getParentuuid(), pagedetail_source.getParentuuid())){
//									parentUuid = pagedetail_target.getPageuuid();
//									path = newBasePath;
//								}else{
//									
//									parentUuid = oldFolderuuidWithNewFolderuuidMap.get(pp.getValue().getParentuuid());
//									
//									path = pp.getValue().getPath();
//									// base path replacement
//									if(StringUtils.isBlank(oldBasePath)){
//										path = newBasePath+"/"+path;
//									}else{
//										path = StringUtils.replace(path, oldBasePath, newBasePath);
//									}
//									// all parentUuid replacement for path
//									for(Map.Entry<String, String> e : oldFolderuuidWithNewFolderuuidMap.entrySet()){
//										path = StringUtils.replace(path, e.getKey(), e.getValue());
//									}
//									
//								}
//								
//								if(StringUtils.isNotBlank(parentUuid) && StringUtils.isNotBlank(path)){
//									pp.getValue().setParentuuid(parentUuid);
//									pp.getValue().setPath(path);
//								}else{
//									everythingIsFine = false;
//								}
//							}
							
							if(everythingIsFine){
								
								// list org's all moduleDetails
								List<ModuleDetail> orgModuleDetailsWithoutFolder = siteDesignDao.findOrgModulesWithoutFolder(pagedetail_target.getOrganization_id());
								//List<ModuleDetail> orgModuleDetailsOnlyFolder = siteDesignDao.findOrgModulesOnlyFolder(entitydetail_target.getOrganization_id());
								
								// *** copy all moduleDetailUuidsForCopy to target
								// oldNewModuleUuidsMap: Map<old-moduleDetailUuid, Map<[old-moduleDetailUuid, new-moduleDetailUuid],[old-groupUuid, new-groupUuid], [old-attrUuid, new-attrUuid]>>
								Map<String, Map<String, String>> oldNewModuleUuidsMap = new HashMap<String, Map<String, String>>();
								if(moduleDetailUuidsForCopy.size()>0){
									// ceate a folder in target org's moduletree: (sourceOrgSysName)
									// 1. find or create if has source org folder in the tree:
									String sourceOrgFoderUuidInTargetTree = createSourceOrgFolderInTargetTree(ModuleDetail.class, pagedetail_source.getOrganization_id(), pagedetail_target.getOrganization_id());
									if(sourceOrgFoderUuidInTargetTree!=null){
										// 2. find or create a folder based on date under sourceOrgFoderUuidInTargetTree
										Date now = new Date();
										String datePattern = "yyyy-MM-dd";
										SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
										String nowDate = dateformat.format(now);
										String todayFolderToHoldAllModules = findOrCreateFolderBasedOnName(ModuleDetail.class, pagedetail_target.getOrganization_id(), sourceOrgFoderUuidInTargetTree, nowDate);
										
										for(String muuid : moduleDetailUuidsForCopy){
											
											if(oldNewModuleUuidsMap.get(muuid)==null){
												ModuleDetail sourceModuledetail = siteDesignDao.getModuleDetailByUuid(muuid);
												if(sourceModuledetail!=null){
													// find if orgModuleDetailsWithoutFolder has same module
													boolean sameModuleFindInTarget = false;
													
													if(orgModuleDetailsWithoutFolder!=null && orgModuleDetailsWithoutFolder.size()>0){
														for(ModuleDetail md : orgModuleDetailsWithoutFolder){
															if(sourceModuledetail.equals(md)){ // be very careful the order of the comparison, which will determine the key & value of thisThatUuids
																Map<String, String> oldNewUuids = new HashMap<String, String>();
																oldNewUuids.put(sourceModuledetail.getModuleuuid(), md.getModuleuuid());
																
																if(sourceModuledetail.getThisThatUuids()!=null && sourceModuledetail.getThisThatUuids().size()>0){
																	oldNewUuids.putAll(sourceModuledetail.getThisThatUuids());
																}
																
																sameModuleFindInTarget = true;
																
																if(oldNewUuids.size()>0){
																	oldNewModuleUuidsMap.put(sourceModuledetail.getModuleuuid(), oldNewUuids);
																}
																
																break;
															}
														}
														
													}
													
													
													if(!sameModuleFindInTarget){ // clone a moduleDetail
														ApiResponse apiresFromModuleCopy = copyTreeNodeToAnotherTree(ModuleDetail.class, sourceModuledetail.getModuleuuid(), todayFolderToHoldAllModules);
														
														if(apiresFromModuleCopy!=null && apiresFromModuleCopy.isSuccess()){
															Map<String, Map<String, String>> oldNewUuidsFromModuleCopy = (Map<String, Map<String, String>>)apiresFromModuleCopy.getResponse1();
															if(oldNewUuidsFromModuleCopy!=null && oldNewUuidsFromModuleCopy.size()>0){
																oldNewModuleUuidsMap.putAll(oldNewUuidsFromModuleCopy);
															}
														}
													}
													
												}
												
											}
											
										}
										
									}
								}
								
								// do the save for finalCopiedUuidWithPageDetails & hasCopyPermissionTreeViews
								for(Map.Entry<String, PageDetail> e : finalCopiedUuidWithPageDetails.entrySet()){
									
									List<ContainerDetail> pageContainers = siteDesignDao.findPageContainerDetails(e.getValue().getPageuuid());
									
									if(pageContainers!=null && pageContainers.size()>0){
										for(ContainerDetail cd : pageContainers){
											if(StringUtils.isNotBlank(cd.getModuleuuid()) && oldNewModuleUuidsMap.get(cd.getModuleuuid())!=null){
												Map<String, String> allOldNewUuidsForModuleDetail = oldNewModuleUuidsMap.get(cd.getModuleuuid());
												cd.setModuleuuid(allOldNewUuidsForModuleDetail.get(cd.getModuleuuid()));
												
												siteDesignDao.saveContainerDetail(cd);
											}
										}
									}
									
									
								}
								
								for(TreeLevelView v : hasCopyPermissionTreeViews){
									// save or merge : in most of the time, you need merge the new treeview with the exist treeview which have same parentUuid!!
									PageTreeLevelView existTreeView = siteDesignDao.getPageTreeLevelViewByParentUuid(v.getParentuuid());
									if(existTreeView!=null){
										List<PageTreeNode> existNodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, existTreeView.getNodes());
										List<PageTreeNode> newNodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, v.getNodes());
										if(existNodes==null){
											existNodes = new ArrayList<PageTreeNode>();
										}
										existNodes.addAll(newNodes);
										existTreeView.setNodes(TreeHelp.getXmlFromTreeNodes(existNodes));
										siteDesignDao.savePageTreeLevelView(existTreeView);
										
									}else{
										siteDesignDao.savePageTreeLevelView((PageTreeLevelView)v);
									}
								}
								
								apires.setSuccess(true);
								
							}
							
							
						}else{
							apires.setResponse1("You can't move node inside tree bying using copyTreeNodeToAnotherTree function!!");
						}
						
						
					}else{
						apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have edit permission for target: "+pagedetail_target.getPrettyname());
					}
					
					
				}else{
					apires.setResponse1("System can't find entitydetails by uuid: "+nodeUuid+" and/or "+targetUuid);
				}
				
			}
			
			
		}else{
			apires.setResponse1("System don't have enough information to process the copy!");
		}
		
		
		return apires;
		
	}
	@Override
	@Transactional
	public ApiResponse moveTreeNode_v2(Class nodeDetailClass, String nodeUuid, String targetUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);

		AccountDto loginAccount = accountService.getCurrentAccount();
		if(loginAccount!=null){
			if(nodeDetailClass!=null && StringUtils.isNotBlank(nodeUuid) && StringUtils.isNotBlank(targetUuid)){
				NodeDetail sourceDetail = null;
				NodeDetail targetDetail = null;
				List<NodeDetail> detailsUnderFolder = null;
				boolean isTargetFolder = false; // is targetNode a folder node?
				boolean isSourceFolder = false; // is sourceNode a folder node?
				TreeLevelView oldview = null;
				List<TreeNode> nodesInOldview = null;
				if(nodeDetailClass.equals(MediaDetail.class)){
					sourceDetail = mediaDao.getMediaDetailByUuid(nodeUuid);
					targetDetail = mediaDao.getMediaDetailByUuid(targetUuid);
					detailsUnderFolder = mediaDao.findMedialDetailsUnderFolder(nodeUuid);
					
					oldview = mediaDao.getMediaTreeLevelViewHasNode(nodeUuid);
					if(oldview!=null){
						nodesInOldview = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, oldview.getNodes());
					}
					
					if(sourceDetail!=null){
						isSourceFolder = ((MediaDetail)sourceDetail).getNodetype().equals(MediaDetail.MediaType.folder.getCode());
					}
					if(targetDetail!=null){
						isTargetFolder = ((MediaDetail)targetDetail).getNodetype().equals(MediaDetail.MediaType.folder.getCode());
					}
					
				}else if(nodeDetailClass.equals(EntityDetail.class)){
					sourceDetail = entityDao.getEntityDetailByUuid(nodeUuid);
					targetDetail = entityDao.getEntityDetailByUuid(targetUuid);
					detailsUnderFolder = entityDao.findEntityDetailsUnderFolder(nodeUuid);
					
					oldview = entityDao.getEntityTreeLevelViewHasNode(nodeUuid);
					if(oldview!=null){
						nodesInOldview = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, oldview.getNodes());
					}
					
					if(sourceDetail!=null){
						isSourceFolder = ((EntityDetail)sourceDetail).getType().equals(EntityDetail.EntityType.folder.getCode());
					}
					if(targetDetail!=null){
						isTargetFolder = ((EntityDetail)targetDetail).getType().equals(EntityDetail.EntityType.folder.getCode());
					}
				}else if(nodeDetailClass.equals(PageDetail.class)){
					sourceDetail = siteDesignDao.getPageDetailByUuid(nodeUuid);
					targetDetail = siteDesignDao.getPageDetailByUuid(targetUuid);
					detailsUnderFolder = siteDesignDao.findPageDetailsUnderFolder(nodeUuid);
					
					oldview = siteDesignDao.getPageTreeLevelViewHasNode(nodeUuid);
					if(oldview!=null){
						nodesInOldview = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, oldview.getNodes());
					}
					
					if(sourceDetail!=null){
						isSourceFolder = ((PageDetail)sourceDetail).getType().equals(PageDetail.Type.Folder.getCode());
					}
					if(targetDetail!=null){
						isTargetFolder = ((PageDetail)targetDetail).getType().equals(PageDetail.Type.Folder.getCode());
					}

				}else if(nodeDetailClass.equals(ModuleDetail.class)){
					sourceDetail = siteDesignDao.getModuleDetailByUuid(nodeUuid);
					targetDetail = siteDesignDao.getModuleDetailByUuid(targetUuid);
					detailsUnderFolder = siteDesignDao.findModuleDetailsUnderFolder(nodeUuid);
					
					oldview = siteDesignDao.getModuleTreeLevelViewHasNode(nodeUuid);
					if(oldview!=null){
						nodesInOldview = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, oldview.getNodes());
					}
					
					if(sourceDetail!=null){
						isSourceFolder = ((ModuleDetail)sourceDetail).getType().equals(ModuleDetail.Type.folder.getCode());
					}
					if(targetDetail!=null){
						isTargetFolder = ((ModuleDetail)targetDetail).getType().equals(ModuleDetail.Type.folder.getCode());
					}
				}
				if(sourceDetail!=null && targetDetail!=null && isTargetFolder){
					
					// permission check:
					StringBuilder permissionErrorMsg = new StringBuilder();
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					boolean modifyPermissionAllow_target = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, targetUuid);
					if(!modifyPermissionAllow_target){
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit "+nodeDetailClass.getSimpleName()+" targetNode: "+targetUuid+"\n");
					}
					boolean modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, nodeUuid);
					if(modifyPermissionAllow_source){
						// also check all children under moduledetail
						if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
							for(NodeDetail d : detailsUnderFolder){
								modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, d.getNodeUuid());
								
								if(!modifyPermissionAllow_source){
									permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit "+nodeDetailClass.getSimpleName()+" sourceNode: "+d.getNodeUuid()+"\n");
									break;
								}
							}
						}
						
					}else{
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit "+nodeDetailClass.getSimpleName()+" sourceNode: "+nodeUuid+"\n");
					}
					
					if(modifyPermissionAllow_source && modifyPermissionAllow_target){
						String targetPosition = StringUtils.isNotBlank(targetDetail.getPath())?targetDetail.getPath()+"/"+targetDetail.getNodeUuid():targetDetail.getNodeUuid();
						// check if sourcedetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String detailRootUuid = null;
						if(StringUtils.isNotBlank(sourceDetail.getPath())){
							int firstDivideIndex = sourceDetail.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								detailRootUuid = sourceDetail.getPath().substring(0, firstDivideIndex);
							}else{
								detailRootUuid = sourceDetail.getPath();
							}
							
						}
						if(StringUtils.isNotBlank(detailRootUuid)){
							isInsideTree = (targetPosition.indexOf(detailRootUuid)>-1);
						}
						
						if(isInsideTree){
							if(isSourceFolder){ // source is folder
								
								// *** update path & parentUuid for folder detail and it's subfolders and entities
								// update children's paths first
								if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
									for(NodeDetail d : detailsUnderFolder){
										d.setPath(d.getPath().replaceAll(sourceDetail.getPath(), targetPosition));
									}
								}
								// then ... update sourceDetail's path
//								sourceDetail.setPath(targetPosition);
//								sourceDetail.setParentuuid(targetDetail.getNodeUuid());

							}
							
							// *** update path & parentuuid for detail
							sourceDetail.setPath(targetPosition);
							sourceDetail.setParentuuid(targetDetail.getNodeUuid());
							
							// *** update parentUuid for permissionedStuff
							List<PermissionedStuff> permissionedStuffs = permissionDao.findPermissionedStuffForStuff(nodeUuid);
							if(permissionedStuffs!=null && permissionedStuffs.size()>0){
								for(PermissionedStuff ps : permissionedStuffs){
									ps.setParentuuid(targetDetail.getNodeUuid());
									permissionDao.savePermissionedStuff(ps);
								}
							}

							
							// *** move node from treeLevel
							// delete from old treelevelview
							TreeNode delNode = null;
							if(nodesInOldview!=null && nodesInOldview.size()>0){
								for(TreeNode n : nodesInOldview){
									if(n.getSystemName().equals(nodeUuid)){
										delNode = n;
										break;
									}
								}
								if(delNode!=null){
									nodesInOldview.remove(delNode);
									String updatedxml = TreeHelp.getXmlFromTreeNodes(nodesInOldview);
									oldview.setNodes(updatedxml);
								}
							}
							if(StringUtils.isBlank(oldview.getNodes())){// remove oldview if no viewNodes
								if(nodeDetailClass.equals(MediaDetail.class)){
									mediaDao.delMediaTreeLevelView(oldview.getId());
								}else if(nodeDetailClass.equals(EntityDetail.class)){
									entityDao.delEntityTreeLevelView(oldview.getId());
								}else if(nodeDetailClass.equals(PageDetail.class)){
									siteDesignDao.delPageTreeLevelView(oldview.getId());
								}else if(nodeDetailClass.equals(ModuleDetail.class)){
									siteDesignDao.delModuleTreeLevelView(oldview.getId());
								}
							}
							
							// add to new view
							TreeLevelView newview = null;
							List<TreeNode> existNodes = null;
							if(nodeDetailClass.equals(MediaDetail.class)){
								newview = mediaDao.getMediaTreeLevelViewByParentUuid(targetUuid);
								if(newview!=null){
									existNodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, newview.getNodes());
								}else{
									newview = new MediaTreeLevelView();
									newview.setCreatedate(new Date());
									newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
									newview.setOrganization_id(sourceDetail.getOrganization_id().longValue());
									newview.setParentuuid(targetDetail.getNodeUuid());
								}
							}else if(nodeDetailClass.equals(EntityDetail.class)){
								newview = entityDao.getEntityTreeLevelViewByParentUuid(targetUuid);
								if(newview!=null){
									existNodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, newview.getNodes());
								}else{
									newview = new EntityTreeLevelView();
									newview.setCreatedate(new Date());
									newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
									newview.setOrganization_id(sourceDetail.getOrganization_id().longValue());
									newview.setParentuuid(targetDetail.getNodeUuid());
								}
							}else if(nodeDetailClass.equals(PageDetail.class)){
								newview = siteDesignDao.getPageTreeLevelViewByParentUuid(targetUuid);
								if(newview!=null){
									existNodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, newview.getNodes());
								}else{
									newview = new PageTreeLevelView();
									newview.setCreatedate(new Date());
									newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
									newview.setOrganization_id(sourceDetail.getOrganization_id().longValue());
									newview.setParentuuid(targetDetail.getNodeUuid());
								}
							}else if(nodeDetailClass.equals(ModuleDetail.class)){
								newview = siteDesignDao.getModuleTreeLevelViewByParentUuid(targetUuid);
								if(newview!=null){
									existNodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, newview.getNodes());
								}else{
									newview = new ModuleTreeLevelView();
									newview.setCreatedate(new Date());
									newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
									newview.setOrganization_id(sourceDetail.getOrganization_id().longValue());
									newview.setParentuuid(targetDetail.getNodeUuid());
								}
							}
							
							if(existNodes==null){
								existNodes = new ArrayList<TreeNode>();
							}
							existNodes.add(delNode);
							newview.setNodes(TreeHelp.getXmlFromTreeNodes(existNodes));
							
							// save
							if(nodeDetailClass.equals(MediaDetail.class)){
								mediaDao.saveMediaTreeLevelView((MediaTreeLevelView)newview);
							}else if(nodeDetailClass.equals(EntityDetail.class)){
								entityDao.saveEntityTreeLevelView((EntityTreeLevelView)newview);
							}else if(nodeDetailClass.equals(PageDetail.class)){
								siteDesignDao.savePageTreeLevelView((PageTreeLevelView)newview);
							}else if(nodeDetailClass.equals(ModuleDetail.class)){
								siteDesignDao.saveModuleTreeLevelView((ModuleTreeLevelView)newview);
							}
							
							apires.setSuccess(true);
							
						}else{
							apires.setResponse1("You can't move node from one tree to another tree bying using moveTreeNode function!!");
						}
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					
				}else{
					apires.setResponse1("System can't find source by id: "+nodeUuid+" or target by id: "+targetUuid+", or could be target is not a folder");
				}
			}else{
				apires.setResponse1("System doesn't have enough information to process node moving - nodeDetailClass: "+nodeDetailClass.getName()+" nodeUuid: "+nodeUuid+" target: "+targetUuid);
			}
		}else{
			apires.setResponse1("You may need to login again to process the node moving.");
		}
		
		return apires;
	}

	@Override
	@Transactional
	@Deprecated
	public ApiResponse moveTreeNode(Class nodeDetailClass, String nodeUuid, String targetUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		if(loginAccount!=null && nodeDetailClass!=null && StringUtils.isNotBlank(nodeUuid) && StringUtils.isNotBlank(targetUuid)){
			
			if(nodeDetailClass.equals(MediaDetail.class)){
				MediaDetail detail = mediaDao.getMediaDetailByUuid(nodeUuid);
				MediaDetail targetDetail = mediaDao.getMediaDetailByUuid(targetUuid);
				if(detail!=null && targetDetail!=null && targetDetail.getNodetype().equals(MediaDetail.MediaType.folder.getCode())){
					
					List<NodeDetail> detailsUnderFolder = mediaDao.findMedialDetailsUnderFolder(detail.getMediauuid());
					
					StringBuilder permissionErrorMsg = new StringBuilder();
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					boolean modifyPermissionAllow_target = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, targetDetail.getMediauuid());
					if(!modifyPermissionAllow_target){
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to drop to meida targetNode: "+targetDetail.getMediauuid()+"\n");
					}
					boolean modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, detail.getMediauuid());
					// also check all children under moduledetail
					if(modifyPermissionAllow_source && detailsUnderFolder!=null && detailsUnderFolder.size()>0){
						for(NodeDetail d : detailsUnderFolder){
							modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, ((MediaDetail)d).getMediauuid());
							
							if(!modifyPermissionAllow_source){
								permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to move media sourceNode: "+((MediaDetail)d).getMediauuid()+"\n");
								break;
							}
						}
					}
					
					if(modifyPermissionAllow_source && modifyPermissionAllow_target){
						
						String targetPosition = StringUtils.isNotBlank(targetDetail.getPath())?targetDetail.getPath()+"/"+targetDetail.getMediauuid():targetDetail.getMediauuid();
						
						
						// check if mediadetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String mediaDetailRootUuid = null;
						
						if(StringUtils.isNotBlank(detail.getPath())){
							int firstDivideIndex = detail.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								mediaDetailRootUuid = detail.getPath().substring(0, firstDivideIndex);
							}else{
								mediaDetailRootUuid = detail.getPath();
							}
						}
						if(StringUtils.isNotBlank(mediaDetailRootUuid)){
							isInsideTree = (targetPosition.indexOf(mediaDetailRootUuid)>-1);
						}

						if(isInsideTree){
							if(detail.getNodetype().equals(MediaDetail.MediaType.folder.getCode())){
								
								// *** update path & parentUuid for folder detail and it's subfolders and entities
								if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
									for(NodeDetail d : detailsUnderFolder){
										((MediaDetail)d).setPath(((MediaDetail)d).getPath().replaceAll(detail.getPath(), targetPosition));
									}
								}
								
								detail.setPath(targetPosition);
								detail.setParentuuid(targetDetail.getMediauuid());
//								mediaDao.saveMediaDetail(detail);
								
							}else{
								
								// *** update path & parentuuid for detail
								detail.setPath(targetPosition);
								detail.setParentuuid(targetDetail.getMediauuid());
//								mediaDao.saveMediaDetail(detail);
								
							}
							
							// *** move node from treeLevel
							// delete from old treelevelview
							MediaTreeNode delNode = null;
							MediaTreeLevelView oldview = mediaDao.getMediaTreeLevelViewHasNode(nodeUuid);
							if(oldview!=null && StringUtils.isNotBlank(oldview.getNodes())){
								List<MediaTreeNode> nodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, oldview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(MediaTreeNode n : nodes){
										if(n.getSystemName().equals(nodeUuid)){
											delNode = n;
											break;
										}
									}
									if(delNode!=null){
										nodes.remove(delNode);
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										oldview.setNodes(updatedxml);
//										mediaDao.saveMediaTreeLevelView(oldview);
									}
								}
								
								if(StringUtils.isBlank(oldview.getNodes())){ // remove oldview if no viewNodes
									mediaDao.delMediaTreeLevelView(oldview.getId());
								}
							}
							
							// add to new view
							MediaTreeLevelView newview = mediaDao.getMediaTreeLevelViewByParentUuid(targetDetail.getMediauuid());
							if(newview!=null){
								List<MediaTreeNode> nodes = null;
								if(StringUtils.isBlank(newview.getNodes())){
									nodes = new ArrayList<MediaTreeNode>();
								}else{
									nodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, newview.getNodes());
								}
								nodes.add(delNode);
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
//								mediaDao.saveMediaTreeLevelView(newview);
								
							}else{ // create a one
								List<MediaTreeNode> nodes = new ArrayList<MediaTreeNode>();
								nodes.add(delNode);
								newview = new MediaTreeLevelView();
								newview.setCreatedate(new Date());
								newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
								newview.setOrganization_id(detail.getOrganization_id().longValue());
								newview.setParentuuid(targetDetail.getMediauuid());
								mediaDao.saveMediaTreeLevelView(newview);
							}
							
							apires.setSuccess(true);
							
						}else{
							apires.setResponse1("You can't move node from one tree to another tree bying using moveTreeNode function!!");
						}
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					
				}else{
					apires.setResponse1("System can't find mediadetail by uuid: "+nodeUuid);
				}
			}else if(nodeDetailClass.equals(EntityDetail.class)){
				EntityDetail detail = entityDao.getEntityDetailByUuid(nodeUuid);
				EntityDetail targetDetail = entityDao.getEntityDetailByUuid(targetUuid);
				if(detail!=null && targetDetail!=null && targetDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
					
					List<NodeDetail> detailsUnderFolder = entityDao.findEntityDetailsUnderFolder(detail.getEntityuuid());
					
					StringBuilder permissionErrorMsg = new StringBuilder();
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					boolean modifyPermissionAllow_target = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, targetDetail.getEntityuuid());
					if(!modifyPermissionAllow_target){
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit targetNode: "+targetDetail.getName()+"\n");
					}
					boolean modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, detail.getEntityuuid());
					// also check all children under moduledetail
					if(modifyPermissionAllow_source && detailsUnderFolder!=null && detailsUnderFolder.size()>0){
						for(NodeDetail d : detailsUnderFolder){
							modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, ((EntityDetail)d).getEntityuuid());
							
							if(!modifyPermissionAllow_source){
								permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit sourceNode: "+((EntityDetail)d).getName()+"\n");
								break;
							}
						}
					}
					
					if(modifyPermissionAllow_source && modifyPermissionAllow_target){
						String targetPosition = StringUtils.isNotBlank(targetDetail.getPath())?targetDetail.getPath()+"/"+targetDetail.getEntityuuid():targetDetail.getEntityuuid();
						
						// check if entitydetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String detailRootUuid = null;
						
						if(StringUtils.isNotBlank(detail.getPath())){
							int firstDivideIndex = detail.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								detailRootUuid = detail.getPath().substring(0, firstDivideIndex);
							}else{
								detailRootUuid = detail.getPath();
							}
							
						}
						if(StringUtils.isNotBlank(detailRootUuid)){
							isInsideTree = (targetPosition.indexOf(detailRootUuid)>-1);
						}

						if(isInsideTree){
							if(detail.getType().equals(EntityDetail.EntityType.folder.getCode())){
								
								// *** update path & parentUuid for folder detail and it's subfolders and entities
								if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
									for(NodeDetail d : detailsUnderFolder){
										((EntityDetail)d).setPath(((EntityDetail)d).getPath().replaceAll(detail.getPath(), targetPosition));
									}
								}
								
								detail.setPath(targetPosition);
								detail.setParentuuid(targetDetail.getEntityuuid());

							}else if(detail.getType().equals(EntityDetail.EntityType.entity.getCode())){
								
								// *** update path & parentuuid for detail
								detail.setPath(targetPosition);
								detail.setParentuuid(targetDetail.getEntityuuid());
								
							}
							
							// *** move node from treeLevel
							// delete from old treelevelview
							EntityTreeNode delNode = null;
							EntityTreeLevelView oldview = entityDao.getEntityTreeLevelViewHasNode(nodeUuid);
							if(oldview!=null && StringUtils.isNotBlank(oldview.getNodes())){
								List<EntityTreeNode> nodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, oldview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(EntityTreeNode n : nodes){
										if(n.getSystemName().equals(nodeUuid)){
											delNode = n;
											break;
										}
									}
									if(delNode!=null){
										nodes.remove(delNode);
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										oldview.setNodes(updatedxml);
//										mediaDao.saveMediaTreeLevelView(oldview);
									}
								}
								
								if(StringUtils.isBlank(oldview.getNodes())){ // remove oldview if no viewNodes
									entityDao.delEntityTreeLevelView(oldview.getId());
								}
							}
							
							// add to new view
							EntityTreeLevelView newview = entityDao.getEntityTreeLevelViewByParentUuid(targetDetail.getEntityuuid());
							if(newview!=null){
								List<EntityTreeNode> nodes = null;
								if(StringUtils.isBlank(newview.getNodes())){
									nodes = new ArrayList<EntityTreeNode>();
								}else{
									nodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, newview.getNodes());
								}
								nodes.add(delNode);
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
//								mediaDao.saveMediaTreeLevelView(newview);
								
							}else{ // create a one
								List<EntityTreeNode> nodes = new ArrayList<EntityTreeNode>();
								nodes.add(delNode);
								newview = new EntityTreeLevelView();
								newview.setCreatedate(new Date());
								newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
								newview.setOrganization_id(detail.getOrganization_id().longValue());
								newview.setParentuuid(targetDetail.getEntityuuid());
								entityDao.saveEntityTreeLevelView(newview);
							}
							
							apires.setSuccess(true);
							
						}else{
							apires.setResponse1("You can't move node from one tree to another tree bying using moveTreeNode function!!");
						}
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					
					
				}else{
					apires.setResponse1("System can't find entitydetail by uuid: "+nodeUuid);
				}
				
			}else if(nodeDetailClass.equals(PageDetail.class)){
				PageDetail pagedetail = siteDesignDao.getPageDetailByUuid(nodeUuid);
				PageDetail targetdetail = siteDesignDao.getPageDetailByUuid(targetUuid);
				
				if(pagedetail!=null && targetdetail!=null && targetdetail.getType().equals(PageDetail.Type.Folder.getCode())){
					
					List<NodeDetail> detailsUnderFolder = siteDesignDao.findPageDetailsUnderFolder(pagedetail.getPageuuid());

					
					// permission check:
					StringBuilder permissionErrorMsg = new StringBuilder();
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					boolean modifyPermissionAllow_target = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, targetdetail.getPageuuid());
					if(!modifyPermissionAllow_target){
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit targetNode: "+targetdetail.getPrettyname()+"\n");
					}
					boolean modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, pagedetail.getPageuuid());
					// also check all children under moduledetail
					if(modifyPermissionAllow_source && detailsUnderFolder!=null && detailsUnderFolder.size()>0){
						for(NodeDetail d : detailsUnderFolder){
							modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, ((PageDetail)d).getPageuuid());
							if(!modifyPermissionAllow_source){
								permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit sourceNode: "+((PageDetail)d).getPrettyname()+"\n");
								break;
							}
						}
					}
					
					if(modifyPermissionAllow_source && modifyPermissionAllow_target){
						
						
						String targetPosition = StringUtils.isNotBlank(targetdetail.getPath())?targetdetail.getPath()+"/"+targetdetail.getPageuuid():targetdetail.getPageuuid();
						
						// check if pagedetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String detailRootUuid = null;
						
						if(StringUtils.isNotBlank(pagedetail.getPath())){
							int firstDivideIndex = pagedetail.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								detailRootUuid = pagedetail.getPath().substring(0, firstDivideIndex);
							}else{
								detailRootUuid = pagedetail.getPath();
							}
							
						}
						if(StringUtils.isNotBlank(detailRootUuid)){
							isInsideTree = (targetPosition.indexOf(detailRootUuid)>-1);
						}

						if(isInsideTree){
							
							if(pagedetail.getType().equals(PageDetail.Type.Folder.getCode())){
								
								// *** update path & parentUuid for folder detail and it's subfolders and entities
								if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
									for(NodeDetail d : detailsUnderFolder){
										((PageDetail)d).setPath(((PageDetail)d).getPath().replaceAll(pagedetail.getPath(), targetPosition));
									}
								}
								
								pagedetail.setPath(targetPosition);
								pagedetail.setParentuuid(targetdetail.getPageuuid());

							}else if(pagedetail.getType().equals(PageDetail.Type.Desktop.getCode()) || pagedetail.getType().equals(PageDetail.Type.Mobile.getCode())){
								
								// *** update path & parentuuid for detail
								pagedetail.setPath(targetPosition);
								pagedetail.setParentuuid(targetdetail.getPageuuid());
								
							}
							
							// *** move node from treeLevel
							// delete from old treelevelview
							PageTreeNode delNode = null;
							PageTreeLevelView oldview = siteDesignDao.getPageTreeLevelViewHasNode(nodeUuid);
							if(oldview!=null && StringUtils.isNotBlank(oldview.getNodes())){
								List<PageTreeNode> nodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, oldview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(PageTreeNode n : nodes){
										if(n.getSystemName().equals(nodeUuid)){
											delNode = n;
											break;
										}
									}
									if(delNode!=null){
										nodes.remove(delNode);
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										oldview.setNodes(updatedxml);
//										mediaDao.saveMediaTreeLevelView(oldview);
									}
								}
								
								if(StringUtils.isBlank(oldview.getNodes())){ // remove oldview if no viewNodes
									siteDesignDao.delPageTreeLevelView(oldview.getId());
								}
							}
							
							
							// add to new view
							PageTreeLevelView newview = siteDesignDao.getPageTreeLevelViewByParentUuid(targetdetail.getPageuuid());
							if(newview!=null){
								List<PageTreeNode> nodes = null;
								if(StringUtils.isBlank(newview.getNodes())){
									nodes = new ArrayList<PageTreeNode>();
								}else{
									nodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, newview.getNodes());
								}
								nodes.add(delNode);
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
//								mediaDao.saveMediaTreeLevelView(newview);
								
							}else{ // create a one
								List<PageTreeNode> nodes = new ArrayList<PageTreeNode>();
								nodes.add(delNode);
								newview = new PageTreeLevelView();
								newview.setCreatedate(new Date());
								newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
								newview.setOrganization_id(pagedetail.getOrganization_id().longValue());
								newview.setParentuuid(targetdetail.getPageuuid());
								siteDesignDao.savePageTreeLevelView(newview);
							}
							
							apires.setSuccess(true);
							
						}else{
							apires.setResponse1("You can't move node from one tree to another tree bying using moveTreeNode function!!");
						}
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					

				}else{
					apires.setResponse1("System can't find pagedetail by uuid: "+nodeUuid);
				}
			}else if(nodeDetailClass.equals(ModuleDetail.class)){ // node: need to check if node is moved inside tree!!!
				
				ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(nodeUuid);
				ModuleDetail targetdetail = siteDesignDao.getModuleDetailByUuid(targetUuid);
				
				if(moduledetail!=null && targetdetail!=null && targetdetail.getType().equals(ModuleDetail.Type.folder.getCode())){
					
					List<NodeDetail> detailsUnderFolder = siteDesignDao.findModuleDetailsUnderFolder(moduledetail.getModuleuuid());
					
					StringBuilder permissionErrorMsg = new StringBuilder();
					Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
					boolean modifyPermissionAllow_target = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, targetdetail.getModuleuuid());
					if(!modifyPermissionAllow_target){
						permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit targetNode: "+targetdetail.getPrettyname()+"\n");
					}
					boolean modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, moduledetail.getModuleuuid());
					// also check all children under moduledetail
					if(modifyPermissionAllow_source && detailsUnderFolder!=null && detailsUnderFolder.size()>0){
						for(NodeDetail d : detailsUnderFolder){
							modifyPermissionAllow_source = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, ((ModuleDetail)d).getModuleuuid());
							if(!modifyPermissionAllow_source){
								permissionErrorMsg.append("User "+loginAccount.getFirstname()+" doesn't have permission to edit sourceNode: "+((ModuleDetail)d).getPrettyname()+"\n");
								break;
							}
						}
					}
					
					if(modifyPermissionAllow_source && modifyPermissionAllow_target){
						
						String targetPosition = StringUtils.isNotBlank(targetdetail.getPath())?targetdetail.getPath()+"/"+targetdetail.getModuleuuid():targetdetail.getModuleuuid();
						
						// check if moduledetail, originalPosition, targetPostion have same root
						boolean isInsideTree = false;
						String moduleDetailRootUuid = null;
						
						if(StringUtils.isNotBlank(moduledetail.getPath())){
							int firstDivideIndex = moduledetail.getPath().indexOf("/");
							if(firstDivideIndex>-1){
								moduleDetailRootUuid = moduledetail.getPath().substring(0, firstDivideIndex);
							}else{
								moduleDetailRootUuid = moduledetail.getPath();
							}
						}
						if(StringUtils.isNotBlank(moduleDetailRootUuid)){
							isInsideTree = (targetPosition.indexOf(moduleDetailRootUuid)>-1);
						}

						if(isInsideTree){
							
							if(moduledetail.getType().equals(ModuleDetail.Type.folder.getCode())){
								
								// *** update path & parentUuid for folder detail and it's subfolders and entities
								if(detailsUnderFolder!=null && detailsUnderFolder.size()>0){
									for(NodeDetail d : detailsUnderFolder){
										((ModuleDetail)d).setPath(((ModuleDetail)d).getPath().replaceAll(moduledetail.getPath(), targetPosition));
									}
								}
								
								moduledetail.setPath(targetPosition);
								moduledetail.setParentuuid(targetdetail.getModuleuuid());

							}else if(moduledetail.getType().equals(ModuleDetail.Type.module.getCode()) || moduledetail.getType().equals(ModuleDetail.Type.productModule.getCode())){
								
								// *** update path & parentuuid for detail
								moduledetail.setPath(targetPosition);
								moduledetail.setParentuuid(targetdetail.getModuleuuid());
								
							}
							
							// *** move node from treeLevel
							// delete from old treelevelview
							ModuleTreeNode delNode = null;
							ModuleTreeLevelView oldview = siteDesignDao.getModuleTreeLevelViewHasNode(nodeUuid);
							if(oldview!=null && StringUtils.isNotBlank(oldview.getNodes())){
								List<ModuleTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, oldview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(ModuleTreeNode n : nodes){
										if(n.getSystemName().equals(nodeUuid)){
											delNode = n;
											break;
										}
									}
									if(delNode!=null){
										nodes.remove(delNode);
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										oldview.setNodes(updatedxml);
//										mediaDao.saveMediaTreeLevelView(oldview);
									}
								}
								
								if(StringUtils.isBlank(oldview.getNodes())){ // remove oldview if no viewNodes
									siteDesignDao.delModuleTreeLevelView(oldview.getId());
								}
							}
							
							// add to new view
							ModuleTreeLevelView newview = siteDesignDao.getModuleTreeLevelViewByParentUuid(targetdetail.getModuleuuid());
							if(newview!=null){
								List<ModuleTreeNode> nodes = null;
								if(StringUtils.isBlank(newview.getNodes())){
									nodes = new ArrayList<ModuleTreeNode>();
								}else{
									nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, newview.getNodes());
								}
								nodes.add(delNode);
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
//								mediaDao.saveMediaTreeLevelView(newview);
								
							}else{ // create a one
								List<ModuleTreeNode> nodes = new ArrayList<ModuleTreeNode>();
								nodes.add(delNode);
								newview = new ModuleTreeLevelView();
								newview.setCreatedate(new Date());
								newview.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
								newview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
								newview.setOrganization_id(moduledetail.getOrganization_id().longValue());
								newview.setParentuuid(targetdetail.getModuleuuid());
								siteDesignDao.saveModuleTreeLevelView(newview);
							}
							
							apires.setSuccess(true);
						}else{
							apires.setResponse1("You can't move node from one tree to another tree bying using moveTreeNode function!!");
						}
						
					}else{
						apires.setResponse1(permissionErrorMsg.toString());
					}
					
				}else{
					apires.setResponse1("System can't find entitydetail by uuid: "+nodeUuid);
				}
			}
			
		}else{
			apires.setResponse1("System doesn't have enough information to process node moving - nodeDetailClass: "+nodeDetailClass.getName()+" nodeUuid: "+nodeUuid+" target: "+targetUuid);
		}
		
		
		return apires;
	}

	@Override
	@Transactional
	public void delTreeNodeByUuid(Class nodeClass, String currentNodeUuid) {
		if(nodeClass!=null && StringUtils.isNotBlank(currentNodeUuid)){
			if(nodeClass.equals(ModuleTreeNode.class)){
				
				ModuleDetail detail = siteDesignDao.getModuleDetailByUuid(currentNodeUuid);
				ModuleInstance instance = siteDesignDao.getModuleInstanceByUuid(currentNodeUuid);
				
				if(detail!=null && detail.getType().equals(ModuleDetail.Type.folder.getCode())){ // is folder
					// find all subfolders
					List<NodeDetail> subDetails = siteDesignDao.findModuleDetailsUnderFolder(currentNodeUuid);
					List<ModuleDetail> allFolderDetails = new ArrayList<ModuleDetail>();
					allFolderDetails.add(detail);
					if(subDetails!=null && subDetails.size()>0){
						for(NodeDetail d : subDetails){
							if(((ModuleDetail)d).getType().equals(ModuleDetail.Type.folder.getCode())){
								allFolderDetails.add(((ModuleDetail)d));
							}
						}
					}
					
					// delete folder node with its all subfolders in levelview
					// 			1. delete all levelviews that levelview's parentUuid is in allFolderDetails
					for(ModuleDetail d : allFolderDetails){
						ModuleTreeLevelView v = siteDesignDao.getModuleTreeLevelViewByParentUuid(d.getModuleuuid());
						if(v!=null){
							siteDesignDao.delModuleTreeLevelView(v.getId());
						}
					}
					//			2. delete currentNode from levelView
					ModuleTreeLevelView view = siteDesignDao.getModuleTreeLevelViewHasNode(detail.getModuleuuid());
					if(view!=null){
						List<ModuleTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, view.getNodes());
						if(nodes!=null && nodes.size()>0){
							ModuleTreeNode delNode = null;
							for(ModuleTreeNode node : nodes){
								if(node.getSystemName().equals(currentNodeUuid)){
									delNode = node;
									break;
								}
							}
							if(delNode!=null){
								nodes.remove(delNode);
							}
						}
						if(nodes.size()==0 && StringUtils.isNotBlank(view.getParentuuid())){
							siteDesignDao.delModuleTreeLevelView(view.getId());
						}else{
							view.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
							siteDesignDao.saveModuleTreeLevelView(view);
						}
						
					}
					
				}else if(detail!=null || instance!=null){ // not folder, but is moduleDetail or moduleInstance
					ModuleTreeLevelView levelview = siteDesignDao.getModuleTreeLevelViewHasNode(currentNodeUuid);
					if(levelview!=null){
						List<ModuleTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, levelview.getNodes());
						if(nodes!=null && nodes.size()>0){
							ModuleTreeNode delNode = null;
							for(ModuleTreeNode node : nodes){
								if(node.getSystemName().equals(currentNodeUuid)){
									delNode = node;
									break;
								}
							}
							if(delNode!=null){
								nodes.remove(delNode);
							}
						}
						if(nodes.size()==0 && StringUtils.isNotBlank(levelview.getParentuuid())){ // remove levelview if nodes.size==0 && levelview is not the root view
							siteDesignDao.delModuleTreeLevelView(levelview.getId());
						}else{
							levelview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
							siteDesignDao.saveModuleTreeLevelView(levelview);
						}
						
					}
				}
				
			}else if(nodeClass.equals(EntityTreeNode.class)){
				
				EntityDetail detail = entityDao.getEntityDetailByUuid(currentNodeUuid);
				if(detail.getType().equals(EntityDetail.EntityType.folder.getCode())){ // is folder
					// find all subfolders
					List<NodeDetail> subDetails = entityDao.findEntityDetailsUnderFolder(currentNodeUuid);
					List<EntityDetail> allFolderDetails = new ArrayList<EntityDetail>();
					allFolderDetails.add(detail);
					if(subDetails!=null && subDetails.size()>0){
						for(NodeDetail d : subDetails){
							if(((EntityDetail)d).getType().equals(EntityDetail.EntityType.folder.getCode())){
								allFolderDetails.add(((EntityDetail)d));
							}
						}
					}
					
					// delete folder node with its all subfolders in levelview
					// 			1. delete all levelviews that levelview's parentUuid is in allFolderDetails
					for(EntityDetail d : allFolderDetails){
						EntityTreeLevelView v = entityDao.getEntityTreeLevelViewByParentUuid(d.getEntityuuid());
						if(v!=null){
							entityDao.delEntityTreeLevelView(v.getId());
						}
					}
					//			2. delete currentNode from levelView
					EntityTreeLevelView view = entityDao.getEntityTreeLevelViewHasNode(detail.getEntityuuid());
					if(view!=null){
						List<EntityTreeNode> nodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, view.getNodes());
						if(nodes!=null && nodes.size()>0){
							EntityTreeNode delNode = null;
							for(EntityTreeNode node : nodes){
								if(node.getSystemName().equals(currentNodeUuid)){
									delNode = node;
									break;
								}
							}
							if(delNode!=null){
								nodes.remove(delNode);
							}
						}
						if(nodes.size()==0 && StringUtils.isNotBlank(view.getParentuuid())){
							entityDao.delEntityTreeLevelView(view.getId());
						}else{
							view.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
							entityDao.saveEntityTreeLevelView(view);
						}
						
					}
					
				}else{ // not folder
					EntityTreeLevelView levelview = entityDao.getEntityTreeLevelViewHasNode(currentNodeUuid);
					if(levelview!=null){
						List<EntityTreeNode> nodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, levelview.getNodes());
						if(nodes!=null && nodes.size()>0){
							EntityTreeNode delNode = null;
							for(EntityTreeNode node : nodes){
								if(node.getSystemName().equals(currentNodeUuid)){
									delNode = node;
									break;
								}
							}
							if(delNode!=null){
								nodes.remove(delNode);
							}
						}
						if(nodes.size()==0 && StringUtils.isNotBlank(levelview.getParentuuid())){ // remove levelview if nodes.size==0 && levelview is not the root view
							entityDao.delEntityTreeLevelView(levelview.getId());
						}else{
							levelview.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
							entityDao.saveEntityTreeLevelView(levelview);
						}
						
					}
				}
				
			}else if(nodeClass.equals(ContainerTreeNode.class)){
				
				List<String> delContainerUuids = new ArrayList<String>();
				delContainerUuids.add(currentNodeUuid);
				// find all sub-containers
				List<ContainerDetail> subContainers = siteDesignDao.findSubContainerDetails(currentNodeUuid);
				if(subContainers!=null && subContainers.size()>0){
					for(ContainerDetail c : subContainers){
						delContainerUuids.add(c.getContaineruuid());
					}
				}
				
				// find all treelevelviews, which parentuuid is in delContainerUuids
				for(String cuuid : delContainerUuids){
					ContainerTreeLevelView levelview = siteDesignDao.getContainerTreeLevelViewByParentUuid(cuuid);
					if(levelview!=null){
						siteDesignDao.delContainerTreeLevelView(levelview.getId());
					}
				}
				
				// remove currentNode from tree
				ContainerTreeLevelView currentView = siteDesignDao.getContainerTreeLevelViewHasNode(currentNodeUuid);
				if(currentView!=null && StringUtils.isNotBlank(currentView.getNodes())){
					List<ContainerTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ContainerTreeNode.class, currentView.getNodes());
					
					
					if(nodes!=null && nodes.size()>0){
						ContainerTreeNode delNode = null;
						for(ContainerTreeNode node : nodes){
							if(node.getSystemName().equals(currentNodeUuid)){
								delNode = node;
								break;
							}
						}
						if(delNode!=null){
							nodes.remove(delNode);
						}
					}
					if(nodes.size()==0 && StringUtils.isNotBlank(currentView.getParentuuid())){ // remove levelview if nodes.size==0 && levelview is not the root view
						//entityDao.delEntityTreeLevelView(levelview.getId());
						siteDesignDao.delContainerTreeLevelView(currentView.getId());
					}else{
						currentView.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
						//entityDao.saveEntityTreeLevelView(levelview);
						siteDesignDao.saveContainerTreeLevelView(currentView);
					}
				}
				
			}else if(nodeClass.equals(PageTreeNode.class)){
				
				List<String> delPageUuids = new ArrayList<String>();
				delPageUuids.add(currentNodeUuid);
				
				// find all subnodes
				List<PageDetail> subDetails = siteDesignDao.findSubPageDetails(currentNodeUuid);
				if(subDetails!=null && subDetails.size()>0){
					for(PageDetail d : subDetails){
						delPageUuids.add(d.getPageuuid());
					}
				}
				
				// del all subview
				if(delPageUuids.size()>0){
					for(String pUuid : delPageUuids){
						PageTreeLevelView view = siteDesignDao.getPageTreeLevelViewByParentUuid(pUuid);
						if(view!=null){
							siteDesignDao.delPageTreeLevelView(view.getId());
						}
					}
				}
				
				// del currentNode from view
				PageTreeLevelView currentView = siteDesignDao.getPageTreeLevelViewHasNode(currentNodeUuid);
				if(currentView!=null && StringUtils.isNotBlank(currentView.getNodes())){
					
					
					
					List<PageTreeNode> nodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, currentView.getNodes());
					if(nodes!=null && nodes.size()>0){
						PageTreeNode delNode = null;
						for(PageTreeNode node : nodes){
							if(node.getSystemName().equals(currentNodeUuid)){
								delNode = node;
								break;
							}
						}
						if(delNode!=null){
							nodes.remove(delNode);
						}
					}
					if(nodes.size()==0 && StringUtils.isNotBlank(currentView.getParentuuid())){ // remove levelview if nodes.size==0 && levelview is not the root view
						//entityDao.delEntityTreeLevelView(levelview.getId());
						siteDesignDao.delPageTreeLevelView(currentView.getId());
					}else{
						currentView.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
						//entityDao.saveEntityTreeLevelView(levelview);
						siteDesignDao.savePageTreeLevelView(currentView);
					}
					
					
					
					
				}
				
				
				
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public void updateInstanceViewForTreeNode(InstanceView view, String detailUuid) {
		
		if(view!=null){
			// for class : newSchedule delView editView toRename
			AccountDto loginAccount = accountService.getCurrentAccount();
			if(loginAccount!=null){
				ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(detailUuid);
				EntityDetail entitydetail = entityDao.getEntityDetailByUuid(detailUuid);
				
				if(moduledetail!=null || entitydetail!=null){
					
					boolean detailReadPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, detailUuid);
					boolean detailModifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, detailUuid);
					
					if(detailModifyPermission){
						view.addCssClassInfos("newSchedule delView editView toRename");
					}else if(detailReadPermission){
						view.addCssClassInfos("editView");
					}
				}else{
					if(detailUuid==null){
						view.addCssClassInfos("newSchedule delView editView toRename");
					}
				}
				
				
			}
			
			// child number
			List<InstanceViewSchedule> viewScheds = siteDesignDao.findInstanceViewSchedulesByInstanceViewUuid(view.getInstanceviewuuid());
			if(viewScheds!=null && viewScheds.size()>0){
				view.setChildrenNumbers(viewScheds.size());
			}
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public void updateViewScheduleForTreeNode(InstanceViewSchedule sched, String detailUuid) {
		
		if(sched!=null){
			AccountDto loginAccount = accountService.getCurrentAccount();
			
			if(loginAccount!=null){
				ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(detailUuid);
				EntityDetail entitydetail = entityDao.getEntityDetailByUuid(detailUuid);
				
				
				if(moduledetail!=null || entitydetail!=null){
					
					boolean detailReadPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, detailUuid);
					boolean detailModifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, detailUuid);
					
					if(detailModifyPermission){
						// for class
						sched.addCssClassInfos("editSchedule delSchedule toRename");
						
					}else if(detailReadPermission){
						// for class
						sched.addCssClassInfos("editSchedule");
						
					}
				}else{
					if(detailUuid==null){
						// for class
						sched.addCssClassInfos("editSchedule delSchedule toRename");
						
					}
					
				}
				
			}
			
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public void findSubFoldersUnderFolder(String targetUuid, List<TreeNode> results, Class<? extends TreeNode> theClass, String folderUuid, boolean recursive, boolean isPermissionFilterNeed) {
		
		if(theClass.equals(EntityTreeNode.class)){
			List<TreeNode> sf = findTreeNodesByParentNodeUuid_v2(targetUuid, EntityDetail.class, folderUuid, true, false, isPermissionFilterNeed);
			if(recursive && sf!=null && sf.size()>0){
				for(TreeNode n : sf){
					findSubFoldersUnderFolder(targetUuid, results, EntityTreeNode.class, n.getSystemName(), recursive, isPermissionFilterNeed);
				}
			}
			if(sf!=null && sf.size()>0)
				results.addAll(sf);
		}else if(theClass.equals(MediaTreeNode.class)){
			List<TreeNode> sf = findTreeNodesByParentNodeUuid_v2(targetUuid, MediaDetail.class, folderUuid, true, false, isPermissionFilterNeed);
			if(recursive && sf!=null && sf.size()>0){
				for(TreeNode n : sf){
					findSubFoldersUnderFolder(targetUuid, results, MediaTreeNode.class, n.getSystemName(), recursive, isPermissionFilterNeed);
				}
			}
			results.addAll(sf);
		}else if(theClass.equals(ModuleTreeNode.class)){
			List<TreeNode> sf = findTreeNodesByParentNodeUuid_v2(targetUuid, ModuleDetail.class, folderUuid, true, false, isPermissionFilterNeed);
			if(recursive && sf!=null && sf.size()>0){
				for(TreeNode n : sf){
					findSubFoldersUnderFolder(targetUuid, results, ModuleTreeNode.class, n.getSystemName(), recursive, isPermissionFilterNeed);
				}
			}
			results.addAll(sf);
		}else if(theClass.equals(PageTreeNode.class)){
			List<TreeNode> sf = findTreeNodesByParentNodeUuid_v2(targetUuid, PageDetail.class, folderUuid, true, false, isPermissionFilterNeed);
			if(recursive && sf!=null && sf.size()>0){
				for(TreeNode n : sf){
					findSubFoldersUnderFolder(targetUuid, results, PageTreeNode.class, n.getSystemName(), recursive, isPermissionFilterNeed);
				}
			}
			results.addAll(sf);
			
		}
	}

	@Override
	@Transactional(readOnly=true)
	public String getSouceOrgFoderInTargetTree(Class theType, Long sourceOrgId, Long targetOrgId) {
		Organization sourceOrg = orgDao.getOrganizationById(targetOrgId);
		
		if(theType!=null && sourceOrg!=null && targetOrgId!=null){
			
			if(theType.equals(ModuleDetail.class)){
				ModuleDetail root = siteDesignDao.getModuleTreeRoot(targetOrgId);
				if(root!=null){
					List<NodeDetail> moduleDetails = siteDesignDao.findModuleDetailsUnderFolder(root.getModuleuuid());
					if(moduleDetails!=null && moduleDetails.size()>0){
						
						for(NodeDetail md : moduleDetails){
							if(((ModuleDetail)md).getType().equals(ModuleDetail.Type.folder.getCode()) && ((ModuleDetail)md).getPrettyname().equals(sourceOrg.getOrgsysname())){
								return ((ModuleDetail)md).getModuleuuid();
							}
						}
					}
				}
				
			}
			
		}
		
		return null;
	}

	@Override
	@Transactional
	public String createSourceOrgFolderInTargetTree(Class theType, Long sourceOrgId, Long targetOrgId) {
		
		Organization sourceOrg = orgDao.getOrganizationById(targetOrgId);
		
		if(theType!=null && sourceOrg!=null && targetOrgId!=null){
			if(theType.equals(ModuleDetail.class)){
				String sourceOrgFolderInTargetTree = getSouceOrgFoderInTargetTree(theType, sourceOrgId, targetOrgId);
				if(sourceOrgFolderInTargetTree!=null) return sourceOrgFolderInTargetTree;
				else{ // create
					ModuleDetail root = siteDesignDao.getModuleTreeRoot(targetOrgId);
					if(root!=null){
						sourceOrgFolderInTargetTree = siteDesignService.newModuleNode(ModuleDetail.Type.folder, root.getModuleuuid(), sourceOrg.getOrgsysname());
						return sourceOrgFolderInTargetTree;
						
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public String findOrCreateFolderBasedOnName(Class theType, Long targetOrgId, String parentFolderUuid, String folderName) {
		
		if(theType!=null && targetOrgId!=null && StringUtils.isNotBlank(parentFolderUuid) && StringUtils.isNotBlank(folderName)){
			if(theType.equals(ModuleDetail.class)){ // for moduledetail
				List<NodeDetail> moduledetails = siteDesignDao.findModuleDetailsUnderFolder(parentFolderUuid);
				boolean findSameNameFolder = false;
				if(moduledetails!=null && moduledetails.size()>0){
					for(NodeDetail md : moduledetails){
						if(((ModuleDetail)md).getOrganization_id().intValue()==targetOrgId.intValue()
							&& ((ModuleDetail)md).getType().equals(ModuleDetail.Type.folder.getCode())
							&& ((ModuleDetail)md).getPrettyname().equals(folderName)
						){
							findSameNameFolder = true;
							return ((ModuleDetail)md).getModuleuuid();
						}
					}
				}
				
				if(!findSameNameFolder){ // create
					String newFolderUuid = siteDesignService.newModuleNode(ModuleDetail.Type.folder, parentFolderUuid, folderName);
					return newFolderUuid;
				}
			}
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public GeneralTreeNode generateTree(Class<? extends TreeNode> theClass, boolean folderOnly, Long orgId, int levelOfTree) {
		
		if(theClass!=null && orgId!=null){
			// 
			if(theClass.equals(EntityTreeNode.class)){ // for product 
				
				EntityDetail.EntityType nodeTypeToReturn = null;
				if(folderOnly){
					nodeTypeToReturn = EntityDetail.EntityType.folder;
				}
				
				List<EntityDetail> treeNodes = entityDao.findOrgEntityDetails(orgId, nodeTypeToReturn);
				
				if(treeNodes!=null && treeNodes.size()>0){
					List<EntityDetail> checkedTreeNodes = new ArrayList<EntityDetail>();
					
					// *** double check treeNodes with treeView
					List<EntityTreeLevelView> treeViews = entityDao.findOrgEntityTreeLevelViews(orgId);
					if(treeViews!=null && treeViews.size()>0){
						for(EntityDetail ed : treeNodes){
							if(StringUtils.isNotBlank(ed.getParentuuid())){
								for(EntityTreeLevelView tv : treeViews){
									if(tv.getNodes()!=null && tv.getNodes().indexOf(ed.getEntityuuid())>-1){
										checkedTreeNodes.add(ed);
										break;
									}
								}
							}else{ // the root
								checkedTreeNodes.add(ed);
							}
						}
					}
					
					if(checkedTreeNodes.size()>0){
						return TreeHelp.generateTreeByNodeDetail(EntityDetail.class, checkedTreeNodes, levelOfTree);
					}
				}
				
				
				
				
			}else if(theClass.equals(MediaTreeNode.class)){
				//
			}
			
		}
		
		return null;
	}

//	@Override
//	@Transactional(readOnly=true)
//	public void productTreeFurtherProcess(GeneralTreeNode productCatTree,
//			String currentPageUuid, String currentSelectedEntityUuid) {
//		
//		PageDetail currentPage = siteDesignDao.getPageDetailByUuid(currentPageUuid);
//		EntityDetail currentEntity = entityDao.getEntityDetailByUuid(currentSelectedEntityUuid);
//		
//		if(currentPage!=null && currentEntity!=null && productCatTree!=null){
//			
//			Organization org = orgDao.getOrganizationById(currentPage.getOrganization_id());
//			
//			String[] currentEntityPaths = StringUtils.isNotBlank(currentEntity.getPath())?currentEntity.getPath().split("/"):null;
//			// allSelectedNodeUuids: holds all selected uuids in chain.
//			List<String> allSelectedNodeUuids = new ArrayList<String>();
//			if(currentEntityPaths!=null && currentEntityPaths.length>0){
//				for(String p : currentEntityPaths){
//					allSelectedNodeUuids.add(p);
//				}
//			}
//			allSelectedNodeUuids.add(currentEntity.getEntityuuid());
//			
//			// partial url
//			StringBuilder partialUrl = new StringBuilder("http://").append(applicationConfig.getHostName());
//			if(currentPage.getType().equals(PageDetail.Type.Desktop.getCode())){
//				partialUrl.append("/getPage");
//			}else{
//				partialUrl.append("/getMobilePage");
//			}
////			partialUrl.append("/org/").append(org.getOrguuid()).append("/pageurl/").append(arg0)
//			
////			generalTreeExtraInfoSetupForNode(productCatTree, allSelectedNodeUuids, )
//			
//		}
//		
//	}

	@Override
	@Transactional(readOnly=true)
	public void productTreeFurtherProcess(String hostname, GeneralTreeNode productCatTree, String currentPageUuid, 
			List<String> currentSelectedEntityUuids, String orgUuid,
			String upperDefinedCategoryPageUuid, String upperDefinedProductPageUuid) {
		PageDetail currentPage = null;
		
		Organization org = orgDao.getOrgByUuid(orgUuid);
		
		if(org!=null) {
			if(StringUtils.equals(currentPageUuid, PageDetail.FAKEPAGEUUID)){
				currentPage = SitedesignHelper.createFakePage(org);
			}else{
				currentPage = siteDesignDao.getPageDetailByUuid(currentPageUuid);
			}
			
			if(productCatTree!=null && currentPage!=null && StringUtils.isNotBlank(orgUuid)){
				
				PageDetail.Type pagetype = null;
				if(productCatTree.getNodetype()!=null){
					if(productCatTree.getNodetype().equals(JsTreeNode.NodeType.folder)){
						pagetype = PageDetail.Type.category;
					}else if(productCatTree.getNodetype().equals(JsTreeNode.NodeType.leaf)){
						pagetype = PageDetail.Type.product;
					}
				}
				
				PageDetail.Type sitetype = null;
				if(currentPage.getType().equals(PageDetail.Type.Desktop.getCode())){
					sitetype = PageDetail.Type.Desktop;
				}else if(currentPage.getType().equals(PageDetail.Type.Mobile.getCode())){
					sitetype = PageDetail.Type.Mobile;
				}
				
				// for selected
				if(currentSelectedEntityUuids!=null && currentSelectedEntityUuids.size()>0){
					if(currentSelectedEntityUuids.contains(productCatTree.getSystemName())) productCatTree.setSelected(true);
				}
				
				// for url
				String categoryOrProductPageUuid_cat = productService.getCategoryOrProductPageUuidForEntityNode(productCatTree.getSystemName(), 
						PageDetail.Type.category, sitetype, upperDefinedCategoryPageUuid, upperDefinedProductPageUuid);
				PageDetail categoryOrProductPage_cat = siteDesignDao.getPageDetailByUuid(categoryOrProductPageUuid_cat);
				String categoryOrProductPageUuid_prod = productService.getCategoryOrProductPageUuidForEntityNode(productCatTree.getSystemName(), 
						PageDetail.Type.product, sitetype, upperDefinedCategoryPageUuid, upperDefinedProductPageUuid);
				PageDetail categoryOrProductPage_prod = siteDesignDao.getPageDetailByUuid(categoryOrProductPageUuid_prod);
				String entityUrl = null;
				
				if(!StringUtils.equals(currentPageUuid, PageDetail.FAKEPAGEUUID)){
					if(pagetype.equals(PageDetail.Type.category)){
						entityUrl = productService.getEntityUrl(hostname, productCatTree.transferToEntityTreeNode(false), sitetype, orgUuid, categoryOrProductPage_cat!=null?categoryOrProductPage_cat.getUrl():"", null);;
					}else if(pagetype.equals(PageDetail.Type.product)){
						entityUrl = productService.getEntityUrl(hostname, productCatTree.transferToEntityTreeNode(false), sitetype, orgUuid, null, categoryOrProductPage_prod!=null?categoryOrProductPage_prod.getUrl():"");;
					}
					
				}else{
					entityUrl = "http://"+applicationConfig.getHostName();
				}
				
				
				productCatTree.setUrl(entityUrl);
				
				if(productCatTree.getSubNodes()!=null && productCatTree.getSubNodes().size()>0){
					for(GeneralTreeNode subTn : productCatTree.getSubNodes()){
						productTreeFurtherProcess(hostname, subTn, currentPageUuid, currentSelectedEntityUuids, orgUuid, categoryOrProductPageUuid_cat, categoryOrProductPageUuid_prod);
						
					}
				}
				
			}
			
		}
		
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<TreeNode> findTreeNodesByParentNodeUuid_v2(String targetUuid, Class nodeClass, String parentNodeUuid, boolean includeFold, boolean includeLeaf, boolean isPermissionFilterNeed) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
		
		List<TreeNode> findTreeNodes = null;

		if(StringUtils.isNotBlank(parentNodeUuid) && nodeClass!=null && (targetAccount!=null || targetGroup!=null)){
			
			Permission mergedPermission = null;
			if(isPermissionFilterNeed){
				if(targetAccount!=null){
					mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
				}else if(targetGroup!=null){
					mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
				}
			}
			
			if(nodeClass.equals(MediaDetail.class)){
				
				// permission check for folder:
				boolean isPreviewPermissionAllowed = true;
				if(isPermissionFilterNeed){
					isPreviewPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.preview, parentNodeUuid);
				}
				
				if(isPreviewPermissionAllowed){ // preview permission allowed for parent
					// get list of NodeDetails in folder
					List<NodeDetail> detailsInFolder = mediaDao.findMedialDetailsInFolder(parentNodeUuid, includeFold, includeLeaf);
					// get treeLevelView for double check if above list of nodeDetails in the tree
					MediaTreeLevelView treelevelView = mediaDao.getMediaTreeLevelViewByParentUuid(parentNodeUuid);
					
					if(detailsInFolder!=null && detailsInFolder.size()>0 && treelevelView!=null && StringUtils.isNotBlank(treelevelView.getNodes())){
						findTreeNodes = new ArrayList<TreeNode>();
						
						for(NodeDetail detail : detailsInFolder){
							if(treelevelView.getNodes().indexOf(detail.getNodeUuid())>-1){ // node exist in tree
								MediaDetail mediaDetail = (MediaDetail)detail;
								MediaTreeNode treeNode = new MediaTreeNode();
								treeNode.setPrettyName(mediaDetail.getPrettyname());
								treeNode.setSystemName(mediaDetail.getMediauuid());
								treeNode.setType(MediaType.fromCode(mediaDetail.getNodetype()));
								
								findTreeNodes.add(treeNode);
							}
						}
					}
					
				}else{ // preview permission denied for parent, but need to check preview permission is allowed for folders (// folder will not be shown only if previewAllow is false!!! - check PermissionService.isNodeInListAllowed) 
					List<NodeDetail> folders = mediaDao.findMedialDetailsInFolder(parentNodeUuid, true, false);
					if(folders!=null){
						findTreeNodes = new ArrayList<TreeNode>();
						
						String path = null;
						MediaDetail parentDetail = mediaDao.getMediaDetailByUuid(parentNodeUuid);
						if(parentDetail!=null){
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getMediauuid();
							}else{
								path = parentDetail.getMediauuid();
							}
						}
						Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
						closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, mergedPermission);
						
						for(NodeDetail f : folders){
							
							Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
							currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(f.getNodeUuid(), mergedPermission);
							
							boolean isNodeInListAllowed = permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
							
							if(isNodeInListAllowed){
								
								MediaTreeNode treeNode = new MediaTreeNode();
								treeNode.setPrettyName(((MediaDetail)f).getPrettyname());
								treeNode.setSystemName(((MediaDetail)f).getMediauuid());
								treeNode.setType(MediaType.fromCode(((MediaDetail)f).getNodetype()));
								
								findTreeNodes.add(treeNode);
							}
							
						}
					}
				}
			}else if(nodeClass.equals(ModuleDetail.class)){
				
				// get list of NodeDetails in folder
				List<NodeDetail> detailsInFolder = siteDesignDao.findModuleDetailsInFolder(parentNodeUuid, includeFold, includeLeaf);
				// get treeLevelView for double check if above list of nodeDetails in the tree
				ModuleTreeLevelView treelevelView = siteDesignDao.getModuleTreeLevelViewByParentUuid(parentNodeUuid);
				
				if(detailsInFolder!=null && detailsInFolder.size()>0 && treelevelView!=null && StringUtils.isNotBlank(treelevelView.getNodes())){
					findTreeNodes = new ArrayList<TreeNode>();
					
					for(NodeDetail detail : detailsInFolder){
						if(treelevelView.getNodes().indexOf(detail.getNodeUuid())>-1){ // node exist in tree
							ModuleDetail moduleDetail = (ModuleDetail)detail;
							ModuleTreeNode treeNode = new ModuleTreeNode();
							treeNode.setPrettyName(moduleDetail.getPrettyname());
							treeNode.setSystemName(moduleDetail.getModuleuuid());
							treeNode.setType(ModuleDetail.Type.fromCode(moduleDetail.getType()));
							
							findTreeNodes.add(treeNode);
						}
					}
				}
			}else if(nodeClass.equals(EntityDetail.class)){
				
				// get list of NodeDetails in folder
				List<NodeDetail> detailsInFolder = null;
				if(isPermissionFilterNeed){
					detailsInFolder = productService.findProductsInFolderByMergedPermission(mergedPermission, parentNodeUuid, -1, -1, includeFold, includeLeaf);
				}else{
					detailsInFolder = entityDao.findEntityDetailsInFolder(parentNodeUuid, includeFold, includeLeaf);
				}
				// get treeLevelView for double check if above list of nodeDetails in the tree
				EntityTreeLevelView treelevelView = entityDao.getEntityTreeLevelViewByParentUuid(parentNodeUuid);
				
				if(detailsInFolder!=null && detailsInFolder.size()>0 && treelevelView!=null && StringUtils.isNotBlank(treelevelView.getNodes())){
					findTreeNodes = new ArrayList<TreeNode>();
					
					for(NodeDetail detail : detailsInFolder){
						if(treelevelView.getNodes().indexOf(detail.getNodeUuid())>-1){ // node exist in tree
							EntityDetail entityDetail = (EntityDetail)detail;
							EntityTreeNode treeNode = new EntityTreeNode();
							treeNode.setPrettyName(entityDetail.getName());
							treeNode.setSystemName(entityDetail.getEntityuuid());
							treeNode.setType(EntityDetail.EntityType.fromCode(entityDetail.getType()));
							
							findTreeNodes.add(treeNode);
						}
					}
				}
				
			}else if(nodeClass.equals(PageDetail.class)){
				// get list of NodeDetails in folder
				List<NodeDetail> detailsInFolder = siteDesignDao.findPageDetailsInFolder(parentNodeUuid, includeFold, includeLeaf);
				// get treeLevelView for double check if above list of nodeDetails in the tree
				PageTreeLevelView treelevelView = siteDesignDao.getPageTreeLevelViewByParentUuid(parentNodeUuid);
				
				if(detailsInFolder!=null && detailsInFolder.size()>0 && treelevelView!=null && StringUtils.isNotBlank(treelevelView.getNodes())){
					findTreeNodes = new ArrayList<TreeNode>();
					
					for(NodeDetail detail : detailsInFolder){
						if(treelevelView.getNodes().indexOf(detail.getNodeUuid())>-1){ // node exist in tree
							PageDetail pageDetail = (PageDetail)detail;
							PageTreeNode treeNode = new PageTreeNode();
							treeNode.setPrettyName(pageDetail.getPrettyname());
							treeNode.setSystemName(pageDetail.getPageuuid());
							treeNode.setType(PageDetail.Type.fromCode(pageDetail.getType()));
							
							findTreeNodes.add(treeNode);
						}
					}
				}
				
			}
		}
		
		return findTreeNodes;
	}


	
	
}
