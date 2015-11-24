package com.bizislife.core.service;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.entity.*;
import com.bizislife.core.entity.converter.AttributeConverter;
import com.bizislife.core.entity.converter.EntityConverter;
import com.bizislife.core.hibernate.dao.*;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.EntityTreeLevelView;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.hibernate.pojo.Tree;
import com.bizislife.core.hibernate.pojo.PageDetail.Type;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	GroupDao groupDao;

	@Autowired
	EntityDao entityDao;
	
	@Autowired
	OrganizationDao orgDao;
	
	@Autowired
	SiteDesignDao sitedesignDao;
	
	@Autowired
	SiteDesignService sitedesignService;
	
	@Autowired
	TreeService treeService;
	
	@Autowired
	PermissionService permissionService;
	
	@Autowired
	MessageService messageService;

	@Autowired
	MessageFromPropertiesService messageFromPropertiesService;
	
    @Autowired
    protected ApplicationConfiguration applicationConfig;

    @Override
	public Entity getEntityFromXML(EntityType type, String entityInXML){
		if(StringUtils.isNotBlank(entityInXML) && type!=null){
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new EntityConverter());
			stream.registerConverter(new AttributeConverter());
			if(type.equals(EntityDetail.EntityType.entity)){
				stream.alias("productEntity", ProductEntity.class);
				return (ProductEntity)stream.fromXML(entityInXML);
			}else if(type.equals(EntityDetail.EntityType.folder)){
				stream.alias("folderEntity", FolderEntity.class);
				return (FolderEntity)stream.fromXML(entityInXML);
			}
		}
		return null;
	}
	
	@Override
	public String getXmlFromEntity(Entity entity){
		if(entity!=null){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(entity.getClass());
			stream.registerConverter(new EntityConverter());
			stream.registerConverter(new AttributeConverter());
			
			return stream.toXML(entity);
		}
		return null;
	}
	
	

	@Override
	@Transactional
	public ApiResponse cloneProductNode(EntityDetail.EntityType nodeType, String parentNodeUuid, String nodeName, String cloneFromUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		// get parent node detail
		EntityDetail parentNodeDetail = entityDao.getEntityDetailByUuid(parentNodeUuid);
		// get cloneFrom node detail
		EntityDetail cloneSourceDetail = entityDao.getEntityDetailByUuid(cloneFromUuid);
		
		boolean isPermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.copy, cloneFromUuid);
		
		if(nodeType!=null && StringUtils.isNotBlank(nodeName) && currentAccount!=null && parentNodeDetail!=null && cloneSourceDetail!=null && isPermissionAllowed){
			
			Organization belongToOrg = orgDao.getOrganizationById(parentNodeDetail.getOrganization_id());
			
			Date now = new Date();
			String cloneUuid = UUID.randomUUID().toString();
			
			// the map to hold all old with new uuid
			Map<String, String> oldNewUuidMap = new HashMap<String, String>();
			oldNewUuidMap.put(cloneSourceDetail.getEntityuuid(), cloneUuid);
			
			EntityDetail cloneDetail = new EntityDetail();
			cloneDetail.setCreatedate(now);
			cloneDetail.setCreator_id(currentAccount.getId());
			cloneDetail.setEntityuuid(cloneUuid);
			cloneDetail.setModuleuuid(cloneSourceDetail.getModuleuuid());
			cloneDetail.setName(nodeName);
			cloneDetail.setOrganization_id(parentNodeDetail.getOrganization_id());
			cloneDetail.setParentuuid(cloneSourceDetail.getParentuuid());
			cloneDetail.setPath(cloneSourceDetail.getPath());
			
//			cloneDetail.setSourceuuid(cloneSourceDetail.getEntityuuid());
			
			cloneDetail.setFrom_entity_uuid(cloneSourceDetail.getEntityuuid());
			cloneDetail.setFrom_org_id(cloneSourceDetail.getOrganization_id());
			
			cloneDetail.setTag(cloneSourceDetail.getTag());
			cloneDetail.setType(cloneSourceDetail.getType());
			cloneDetail.setVisibility(cloneSourceDetail.getVisibility());
			
			Module instance = SitedesignHelper.getModuleFromXml(cloneSourceDetail.getDetail());
			if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
				for(AttrGroup g : instance.getAttrGroupList()){
					String newGroupUuid = UUID.randomUUID().toString();
					oldNewUuidMap.put(g.getGroupUuid(), newGroupUuid);
					g.setGroupUuid(newGroupUuid);
					
					if(g.getAttrList()!=null && g.getAttrList().size()>0){
						for(ModuleAttribute a : g.getAttrList()){
							String newAttrUuid = UUID.randomUUID().toString();
							oldNewUuidMap.put(a.getUuid(), newAttrUuid);
							a.setUuid(newAttrUuid);
						}
					}
				}
				cloneDetail.setDetail(SitedesignHelper.getXmlFromModule(instance));
			}
			
			// for entity level view
			// create a new entityTreeNode for level view
			EntityTreeNode node = new EntityTreeNode();
			node.setPrettyName(nodeName);
			node.setSystemName(cloneUuid);

			// get EntityTreeLevelView by parent's uuid
			EntityTreeLevelView levelView = entityDao.getEntityTreeLevelViewByParentUuid(parentNodeUuid);
			if(levelView==null){
				levelView = new EntityTreeLevelView(null, parentNodeUuid.trim(), null, now, parentNodeDetail.getOrganization_id(), currentAccount.getId());
			}
			// update levelview's nodes
			XStream stream = new XStream(new DomDriver());
			if(StringUtils.isNotBlank(levelView.getNodes())){
				stream.alias("treeNode", EntityTreeNode.class);
				List<EntityTreeNode> nodes = (List<EntityTreeNode>)stream.fromXML(levelView.getNodes().trim());
				if(nodes==null){
					nodes = new ArrayList<EntityTreeNode>();
				}
				nodes.add(node);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", EntityTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				levelView.setNodes(sw.toString());
			}else{
				List<EntityTreeNode> nodes = new ArrayList<EntityTreeNode>();
				nodes.add(node);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", EntityTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				levelView.setNodes(sw.toString());
			}
			
			Long clonedetailId = entityDao.saveEntityDetail(cloneDetail);
			Long viewId = entityDao.saveEntityTreeLevelView(levelView);
			
			if(clonedetailId!=null && viewId!=null){
				apires.setSuccess(true);
				apires.setResponse1(cloneUuid);
				
				
				
				
				
				Accountprofile accountProfile = accountDao.getAccountProfileByAccountId(currentAccount.getId());
				StringBuilder accountName = new StringBuilder();
				if(accountProfile!=null){
					accountName.append(accountProfile.getFirstname()).append(" ").append(accountProfile.getLastname());
				}

				// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed
				// 1) log the activity
				ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					
					dataMap.put("operatorId", currentAccount.getId());
					dataMap.put("operatorName", accountName.toString());
					dataMap.put("orgId", belongToOrg.getId());
					dataMap.put("orgName", belongToOrg.getOrgname());
					dataMap.put("newProductUuid", cloneDetail.getEntityuuid());
					dataMap.put("newProductName", cloneDetail.getName());
					dataMap.put("sourceProductUuid", cloneSourceDetail.getEntityuuid());
					dataMap.put("sourceProductName", cloneSourceDetail.getName());
					
					activityLogData.setDataMap(dataMap);
					
					String desc = messageFromPropertiesService.getMessageSource().getMessage("cloneProduct", 
							new Object[] { 
								accountName.toString(), 
								cloneDetail.getName(),
								cloneSourceDetail.getName(),
								belongToOrg.getOrgname()
								}, Locale.US);
					activityLogData.setDesc(desc);
				Long activityLogId = messageService.newActivity(currentAccount.getId(), belongToOrg.getId(), ActivityType.cloneProduct, activityLogData);
				
				// 2) create a topic
				Topic topic = new Topic(null,
						UUID.randomUUID().toString(),
						new StringBuilder("New product information is created in ").append(belongToOrg.getOrgname()).toString(),
						new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(belongToOrg.getOrgsysname()).append(".").append("product.new").toString(),
						Topic.AccessLevel.privateTopic.getCode(),
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
						belongToOrg.getId(),
						"This topic is used when new product information is created",
						now,
						null,
						Topic.TopicType.SystemTopic.getCode(),
						null,
						null,
						null
						);
				Long topicId = messageService.newTopicWithAncestor(topic);
				
				// 3) create a tree node(s)
//				Long treeId = null;
//				if(topicId!=null){
//					treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//				}
				
				// 4) post to newsfeed
				if(activityLogId!=null){
					// do post ...
					messageService.postFeed(topicId, activityLogId);
					
				}
				
				
				
				
				
				
				
				
				
				
				
			}else{
				apires.setResponse1("System error, refresh the page and try again!");
			}

		}
		return apires;
	}
	
	@Override
	@Transactional
	public String newProductNode(EntityType nodeType, String parentNodeUuid, String nodeName) {
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		if(loginAccount!=null && nodeType!=null && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(nodeName)){
			XStream stream = new XStream(new DomDriver());

			Date now = new Date();
			
			// get parent node detail
			EntityDetail parentEntityDetail = entityDao.getEntityDetailByUuid(parentNodeUuid.trim());
			if(parentEntityDetail!=null){
				
				Organization belongToOrg = orgDao.getOrganizationById(parentEntityDetail.getOrganization_id());
				
				// for template should be used here:
				// note: if nodeType is entity, new product should get the folder's templated
				String folderSelectedModuleDetailUuid = null;
				String folderSelectedModuleXml = null;
				if(nodeType.equals(EntityType.entity)){
					if(StringUtils.isNotBlank(parentEntityDetail.getModuleuuid()) && StringUtils.isNotBlank(parentEntityDetail.getDetail())){
						folderSelectedModuleDetailUuid = parentEntityDetail.getModuleuuid();
						folderSelectedModuleXml = SitedesignHelper.duplicateInstance(parentEntityDetail.getDetail());
						
					}else{
						EntityDetail closestFolderHasModuleSetup = getClosestFolderHasModuleDefined(parentNodeUuid);
						if(closestFolderHasModuleSetup!=null){
							folderSelectedModuleDetailUuid = closestFolderHasModuleSetup.getModuleuuid();
							folderSelectedModuleXml = SitedesignHelper.duplicateInstance(closestFolderHasModuleSetup.getDetail());
						}
					}
				}
				
				StringBuilder path = new StringBuilder();
				if(StringUtils.isNotBlank(parentEntityDetail.getPath())){
					path.append(parentEntityDetail.getPath()).append("/");
				}
				path.append(parentEntityDetail.getEntityuuid());
				
				String newNodeUuid = UUID.randomUUID().toString();
				EntityDetail entityDetail = new EntityDetail(null, 
						newNodeUuid, 
						nodeName.trim(), 
						folderSelectedModuleDetailUuid,
						folderSelectedModuleXml, 
						nodeType.getCode(), 
						parentNodeUuid, 
						path.toString(), 
						now,
						null,
						parentEntityDetail.getOrganization_id(), 
						loginAccount.getId(),
						null,
						EntityDetail.Visibility.hide.getCode());
				
				// for entity level view
				// create a new entityTreeNode for level view
				EntityTreeNode node = new EntityTreeNode();
				node.setPrettyName(nodeName);
				node.setSystemName(newNodeUuid);

				// get EntityTreeLevelView by parent's uuid
				EntityTreeLevelView levelView = entityDao.getEntityTreeLevelViewByParentUuid(parentNodeUuid);
				if(levelView==null){
					levelView = new EntityTreeLevelView(null, parentNodeUuid.trim(), null, now, parentEntityDetail.getOrganization_id(), loginAccount.getId());
				}
				// update levelview's nodes
				if(StringUtils.isNotBlank(levelView.getNodes())){
					stream.alias("treeNode", EntityTreeNode.class);
					List<EntityTreeNode> nodes = (List<EntityTreeNode>)stream.fromXML(levelView.getNodes().trim());
					if(nodes==null){
						nodes = new ArrayList<EntityTreeNode>();
					}
					nodes.add(node);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", EntityTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					levelView.setNodes(sw.toString());
				}else{
					List<EntityTreeNode> nodes = new ArrayList<EntityTreeNode>();
					nodes.add(node);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", EntityTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					levelView.setNodes(sw.toString());
				}
				
				entityDao.saveEntityDetail(entityDetail);
				entityDao.saveEntityTreeLevelView(levelView);
				
				
				Accountprofile accountProfile = accountDao.getAccountProfileByAccountId(loginAccount.getId());
				StringBuilder accountName = new StringBuilder();
				if(accountProfile!=null){
					accountName.append(accountProfile.getFirstname()).append(" ").append(accountProfile.getLastname());
				}
				
				// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed
				// 1) log the activity
				ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					
					dataMap.put("operatorId", loginAccount.getId());
					dataMap.put("operatorName", accountName.toString());
					dataMap.put("orgId", belongToOrg.getId());
					dataMap.put("orgName", belongToOrg.getOrgname());
					dataMap.put("newProductUuid", newNodeUuid);
					dataMap.put("newProductName", entityDetail.getName());
					
					activityLogData.setDataMap(dataMap);
					
					String desc = messageFromPropertiesService.getMessageSource().getMessage("newProduct", 
							new Object[] { 
								accountName.toString(), 
								entityDetail.getName(),
								belongToOrg.getOrgname()
								}, Locale.US);
					activityLogData.setDesc(desc);
				Long activityLogId = messageService.newActivity(loginAccount.getId(), belongToOrg.getId(), ActivityType.newProduct, activityLogData);
				
				// 2) create a topic
				Topic topic = new Topic(null,
						UUID.randomUUID().toString(),
						new StringBuilder("New product information is created in ").append(belongToOrg.getOrgname()).toString(),
						new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(belongToOrg.getOrgsysname()).append(".").append("product.new").toString(),
						Topic.AccessLevel.privateTopic.getCode(),
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
						belongToOrg.getId(),
						"This topic is used when new product information is created",
						now,
						null,
						Topic.TopicType.SystemTopic.getCode(),
						null,
						null,
						null
						);
				Long topicId = messageService.newTopicWithAncestor(topic);
				
				// 3) create a tree node(s)
//				Long treeId = null;
//				if(topicId!=null){
//					treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//				}
				
				// 4) post to newsfeed
				if(activityLogId!=null){
					// do post ...
					messageService.postFeed(topicId, activityLogId);
					
				}
				
				return newNodeUuid;
				
			}
			
			
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Entity getEntityWithAttrsByNodeUuid(String nodeUuid) {
		if(StringUtils.isNotBlank(nodeUuid)){
			// get entitydetail from db
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(nodeUuid);
			if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getType()) && StringUtils.isNotBlank(entityDetail.getDetail())){
				EntityDetail.EntityType entityType = EntityType.fromCode(entityDetail.getType());
				if(entityType!=null){
					return getEntityFromXML(entityType, entityDetail.getDetail());
				}
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public EntityDetail getEntityDetailByUuid(String nodeUuid) {
		if(StringUtils.isNotBlank(nodeUuid)){
			return entityDao.getEntityDetailByUuid(nodeUuid);
		}
		return null;
	}

	@Override
	@Transactional
	public String newAttribute(String productUuid, String attrType, String attrName, String attrValue) {
		if(StringUtils.isNotBlank(productUuid) && StringUtils.isNotBlank(attrType) && StringUtils.isNotBlank(attrName)){
//			XStream stream = new XStream(new DomDriver());
//			stream.autodetectAnnotations(true);
//			stream.registerConverter(new EntityConverter());
//			stream.registerConverter(new AttributeConverter());
			String attrUuid = UUID.randomUUID().toString();
			// find entitydetail by productUuid
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(productUuid);
			Entity entityWithAttrs = null;
			if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getType()) && StringUtils.isNotBlank(entityDetail.getDetail())){
				EntityDetail.EntityType entityType = EntityType.fromCode(entityDetail.getType());
				if(entityType!=null){
					entityWithAttrs = getEntityFromXML(entityType, entityDetail.getDetail());
					
				}
			}
			
			if(entityWithAttrs!=null){
				// create a attribute
				Attribute attrObj = null;
				try {
					Class attrClass = Class.forName(new StringBuilder("com.bizislife.core.entity.").append(attrType).toString());
					Constructor attrContstructor = attrClass.getConstructor();
					if(attrClass.equals(RealAttribute.class)){
						attrObj = (RealAttribute)attrContstructor.newInstance();
						if(attrObj!=null){
							((RealAttribute)attrObj).setUuid(attrUuid);
							((RealAttribute)attrObj).setTitle(attrName);
							if(NumberUtils.isNumber(attrValue)){
								((RealAttribute)attrObj).setValue(Double.valueOf(attrValue));
							}
						}
					}else if(attrClass.equals(StringAttribute.class)){
						attrObj = (StringAttribute)attrContstructor.newInstance();
						if(attrObj!=null){
							((StringAttribute)attrObj).setUuid(attrUuid);
							((StringAttribute)attrObj).setTitle(attrName);
							((StringAttribute)attrObj).setValue(attrValue);
						}
					}else if(attrClass.equals(ImgAttribute.class)){
						attrObj = (ImgAttribute)attrContstructor.newInstance();
						if(attrObj!=null){
							((ImgAttribute)attrObj).setUuid(attrUuid);
							((ImgAttribute)attrObj).setTitle(attrName);
							((ImgAttribute)attrObj).setValue(attrValue);
						}
					}
					
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (SecurityException e) {
					e.printStackTrace();
					return null;
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				} catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
				
				if(attrObj!=null) entityWithAttrs.addAttribute(attrObj);
//				stream.registerConverter(new EntityConverter());
//				stream.registerConverter(new AttributeConverter());

				entityDetail.setDetail(getXmlFromEntity(entityWithAttrs));
				entityDao.saveEntityDetail(entityDetail);
				return attrUuid;
				
				
			}
		}
		
		return null;
	}

	@Override
	@Transactional
	public String updateAttribute(String productUuid, String attrType, String attrName, String attrValue, String attrUuid) {
		
		if(StringUtils.isNotBlank(productUuid) && StringUtils.isNotBlank(attrType) && StringUtils.isNotBlank(attrName) && StringUtils.isNotBlank(attrUuid)){
//			XStream stream = new XStream(new DomDriver());
//			stream.registerConverter(new EntityConverter());
//			stream.registerConverter(new AttributeConverter());

			// find entitydetail by productUuid
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(productUuid);
			Entity entityWithAttrs = null;
			if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getType()) && StringUtils.isNotBlank(entityDetail.getDetail())){
				EntityDetail.EntityType entityType = EntityType.fromCode(entityDetail.getType());
				if(entityType!=null){
					entityWithAttrs = getEntityFromXML(entityType, entityDetail.getDetail());
				}
			}
			
			if(entityWithAttrs!=null && entityWithAttrs.getAllAttributes()!=null && entityWithAttrs.getAllAttributes().size()>0){
				
				Attribute targetAttr = null;
				for(Attribute a : entityWithAttrs.getAllAttributes()){
					if(a.getUuid().equals(attrUuid)){
						targetAttr = a;
						break;
					}
				}
				
				if(targetAttr!=null){
					targetAttr.setTitle(attrName);
					targetAttr.setUuid(attrUuid);
					targetAttr.setValue(attrValue);
					
					entityDetail.setDetail(getXmlFromEntity(entityWithAttrs));
					entityDao.saveEntityDetail(entityDetail);
					return attrUuid;
					
				}
				
			}
		}
		
		return null;
	}

	@Override
	@Transactional
	public String delAttribute(String entityUuid, String attrUuid) {
		
		if(StringUtils.isNotBlank(entityUuid) && StringUtils.isNotBlank(attrUuid)){
			
//			XStream stream = new XStream(new DomDriver());
//			stream.registerConverter(new EntityConverter());
//			stream.registerConverter(new AttributeConverter());

			// find entitydetail by productUuid
			EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityUuid);
			Entity entityWithAttrs = null;
			if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getType()) && StringUtils.isNotBlank(entityDetail.getDetail())){
				EntityDetail.EntityType entityType = EntityType.fromCode(entityDetail.getType());
				if(entityType!=null){
					entityWithAttrs = getEntityFromXML(entityType, entityDetail.getDetail());
				}
			}
			
			if(entityWithAttrs!=null && entityWithAttrs.getAllAttributes()!=null && entityWithAttrs.getAllAttributes().size()>0){
				
				Attribute targetAttr = null;
				for(Attribute a : entityWithAttrs.getAllAttributes()){
					if(a.getUuid().equals(attrUuid)){
						targetAttr = a;
						break;
					}
				}
				
				if(targetAttr!=null){
					entityWithAttrs.getAllAttributes().remove(targetAttr);
					entityDetail.setDetail(getXmlFromEntity(entityWithAttrs));
					entityDao.saveEntityDetail(entityDetail);
					return attrUuid;
				}
			}
			
			
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long saveEntityDetail(EntityDetail entityDetail) {
		if(entityDetail!=null){
			return entityDao.saveEntityDetail(entityDetail);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public EntityDetail getClosestFolderHasModuleDefined(String productUuid) {
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(productUuid);
		if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getPath())){
			String[] foldersUuids = entityDetail.getPath().split("/");
			for(int i=foldersUuids.length-1; i>-1; i--){ // from closest folder to farest folder
				EntityDetail folderDetail = entityDao.getEntityDetailByUuid(foldersUuids[i]);
				if(folderDetail!=null){
					if(StringUtils.isNotBlank(folderDetail.getModuleuuid()) && StringUtils.isNotBlank(folderDetail.getDetail())){
						return folderDetail;
					}else if(StringUtils.isNotBlank(folderDetail.getModuleuuid()) || StringUtils.isNotBlank(folderDetail.getDetail())){ // clean folder's moduledetail setup
						folderDetail.setModuleuuid(null);
						folderDetail.setDetail(null);
					}
				}
			}
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse deleteModuleSelectionForEntity(String entityuuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		EntityDetail detail = entityDao.getEntityDetailByUuid(entityuuid);
		if(detail!=null){
			// permission check
			boolean editPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, entityuuid);
			if(editPermissionAllowed){
				detail.setModuleuuid(null);
				detail.setDetail(null);
				
				// find all instanceviews for the category or product and remove
				List<InstanceView> views = sitedesignDao.findInstanceViewsByInstanceUuid(detail.getEntityuuid());
				if(views!=null && views.size()>0){
					for(InstanceView view : views){
//						view.setModuleuuid(null);
						sitedesignService.delInstanceViewByUuid(view.getInstanceviewuuid());
//						sitedesignDao.delInstanceViewbyId(view.getId());
					}
				}
				
				apires.setSuccess(true);
				
			}else{
				apires.setResponse1("Permission is not allowed to modify entity : "+entityuuid);
			}
			
		}else{
			apires.setResponse1("There has no enough information to get the entitydetail. entityUuid: "+entityuuid);
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public void emptyProductDetailByUuid(String productDetailUuid) {
		EntityDetail detail = entityDao.getEntityDetailByUuid(productDetailUuid);
		if(detail!=null){
			detail.setModuleuuid(null);
			detail.setDetail(null);
			
			// find all instanceviews to del
			List<InstanceView> views = sitedesignDao.findInstanceViewsByInstanceUuid(detail.getEntityuuid());
			if(views!=null && views.size()>0){
				for(InstanceView view : views){
					sitedesignService.delInstanceViewByUuid(view.getInstanceviewuuid());
				}
			}
			
		}
		
	}
	

	@Override
	@Transactional
	public ApiResponse updateEntityDetailValue(String entityUuid, String updateValueName, String updateValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityUuid);
		if(entityDetail!=null){
			
			// permission check
			AccountDto loginAccount = accountService.getCurrentAccount();
			boolean isModifyPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, entityUuid);
			if(isModifyPermissionAllowed){
				
				if(StringUtils.isNotBlank(updateValueName)){
					if(updateValueName.equals("name")){
						if(StringUtils.isNotBlank(updateValue)){
							// update treelevelvew
							EntityTreeLevelView view = entityDao.getEntityTreeLevelViewHasNode(entityUuid);
							if(view!=null && StringUtils.isNotBlank(view.getNodes())){
								List<EntityTreeNode> nodes = TreeHelp.getTreeNodesFromXml(EntityTreeNode.class, view.getNodes());
								if(nodes!=null){
									for(EntityTreeNode n : nodes){
										if(n.getSystemName().equals(entityUuid)){
											n.setPrettyName(updateValue.trim());
										}
									}
									view.setNodes(TreeHelp.getXmlFromTreeNodes(nodes));
								}
								
							}
							
							// update entitydetail
							entityDetail.setName(updateValue.trim());
							Long id = entityDao.saveEntityDetail(entityDetail);
							if(id!=null){
								apires.setSuccess(true);
							}
						}else{
							apires.setResponse1("Name can't be empty!");
						}
					}
				}else{
					apires.setResponse1("System can't update value for valuename: "+updateValueName);
				}
				
			}else{
				apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have permission to modify node : "+entityDetail.getName());
			}
			
			
		}else{
			apires.setResponse1("No enough information to get entityDetail. entityuuid: "+entityUuid);
		}
		
		return apires;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<NodeDetail> findProductsInFolderBySqlSegmentResponse(ApiResponse sqlSegmentResponse, String folderUuid, int totalNumProductsInPage, int offset, boolean includeFold, boolean includeLeaf) {
		
		List<NodeDetail> entityDetails = null;
		
		if(includeFold || includeLeaf){
			
			// determine find type by includeFold and includeLeaf
			EntityDetail.EntityType entityType = null; // include leaf and fold
			if(!includeFold || !includeLeaf){
				if(includeFold){ // folder only
					entityType = EntityDetail.EntityType.folder;
				}else { // leaf only
					entityType = EntityDetail.EntityType.entity;
				}
			}
			
			
			if(sqlSegmentResponse!=null){
				StringBuilder productListSqlStatement = null;
				if(sqlSegmentResponse.isSuccess()){ // folder permission is allowed
					if(sqlSegmentResponse.getResponse1()!=null){
						// for product list
						productListSqlStatement = new StringBuilder("FROM EntityDetail WHERE ");
						// parent
						productListSqlStatement.append("parentuuid = '").append(folderUuid).append("'");
						
						// FOR TYPE
						if(entityType!=null){
							productListSqlStatement.append(" AND type ='").append(entityType.getCode()).append("'");
						}
//						if(!includeFold || !includeLeaf){
//							if(includeFold){ // folder only
//								productListSqlStatement.append(" AND type ='").append(EntityDetail.EntityType.folder.getCode()).append("'");
//							}else{ // leaf only
//								productListSqlStatement.append(" AND type ='").append(EntityDetail.EntityType.entity.getCode()).append("'");
//							}
//						}
						
						// extra statement
						productListSqlStatement.append(" AND ").append((String)sqlSegmentResponse.getResponse1());
						
						// ORDER
						productListSqlStatement.append(" ORDER BY id ASC");
						
						entityDetails = entityDao.findEntityDetailsUnderFolderFromOffsetToTotalReturnNumber(entityType, folderUuid, totalNumProductsInPage, offset, true, false, productListSqlStatement!=null?productListSqlStatement.toString():null);
						
					}else{
						entityDetails = entityDao.findEntityDetailsUnderFolderFromOffsetToTotalReturnNumber(entityType, folderUuid, totalNumProductsInPage, offset, true, false, null);
					}
				}else{ // folder permission is denied
					if(sqlSegmentResponse.getResponse1()!=null){
						// for product list
						productListSqlStatement = new StringBuilder("FROM EntityDetail WHERE ");
						// parent
						productListSqlStatement.append("parentuuid = '").append(folderUuid).append("'");
						
						// FOR TYPE
						if(entityType!=null){
							productListSqlStatement.append(" AND type ='").append(entityType.getCode()).append("'");
						}
//						if(!includeFold || !includeLeaf){
//							if(includeFold){ // folder only
//								productListSqlStatement.append(" AND type ='").append(EntityDetail.EntityType.folder.getCode()).append("'");
//							}else{ // leaf only
//								productListSqlStatement.append(" AND type ='").append(EntityDetail.EntityType.entity.getCode()).append("'");
//							}
//						}
						
						// extra statement
						productListSqlStatement.append(" AND ").append((String)sqlSegmentResponse.getResponse1());
						
						// ORDER
						productListSqlStatement.append(" ORDER BY id ASC");
						
						entityDetails = entityDao.findEntityDetailsUnderFolderFromOffsetToTotalReturnNumber(entityType, folderUuid, totalNumProductsInPage, offset, true, false, productListSqlStatement!=null?productListSqlStatement.toString():null);
					}
				}
			}
			
		}
		
		return entityDetails;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<NodeDetail> findProductsInFolderByMergedPermission(Permission mergedPermission, String folderUuid, int totalNumProductsInPage, int offset, boolean includeFold, boolean includeLeaf) {
		if(includeFold || includeLeaf){
			if(mergedPermission!=null && StringUtils.isNotBlank(folderUuid)){
				
				ApiResponse sqlSegmentResponse = permissionService.generateSqlConditionSegmentForProductList(mergedPermission, folderUuid);
				return findProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, folderUuid, totalNumProductsInPage, offset, includeFold, includeLeaf);
			}
			
		}
		return null;
	}
	
	

	@Override
	@Transactional(readOnly=true)
	public List<NodeDetail> findProductsInfolder(String targetUuid, String folderUuid) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
		EntityDetail folderDetail = entityDao.getEntityDetailByUuid(folderUuid);
		
		if((targetAccount!=null || targetGroup!=null) && folderDetail!=null){
			Permission mergedPermission = null;
			if(targetAccount!=null){
				mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
			}else if(targetGroup!=null){
				mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
			}
			
			return findProductsInFolderByMergedPermission(mergedPermission, folderUuid, -1, -1, false, true);
			
//			if(entityDetails!=null && entityDetails.size()>0) return entityDetails;
			
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<NodeDetail> findProductsInfolderForPreview(String targetUuid, String folderUuid, int totalNumProductsInPage, int pageIdx) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
		
		if(targetAccount!=null || targetGroup!=null){
			
			if(totalNumProductsInPage<=0) totalNumProductsInPage = Integer.MAX_VALUE;
			
			EntityDetail folderDetail = entityDao.getEntityDetailByUuid(folderUuid);
			
			if(folderDetail!=null){
				
				
				// get user merged permission to determine which product can be return.
				Permission mergedPermission = null;
				if(targetAccount!=null){
					mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
				}else if(targetGroup!=null){
					mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
				}
				
				ApiResponse sqlSegmentResponse = permissionService.generateSqlConditionSegmentForProductList(mergedPermission, folderUuid);
				
				
				int totalNode = countProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, folderUuid, false, true);
				
				if(totalNode>totalNumProductsInPage){
					int numberOfPages = totalNode / totalNumProductsInPage;
					int remainder = totalNode % totalNumProductsInPage;
					if(remainder>0){
						numberOfPages++;
					}
					
					if(pageIdx>numberOfPages){
						pageIdx = numberOfPages;
					}
				}
				
				if(pageIdx<=1) pageIdx = 1;
				
				// calcu offset
				int offset = totalNumProductsInPage * (pageIdx-1);
				
				return findProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, folderUuid, totalNumProductsInPage, offset, false, true);
				
			}
			
		}
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse setDefaultPageForEntity(String entityUuid, String pageUuid, PageDetail.Type thePageType, PageDetail.Type theSiteType) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		EntityDetail entitydetail = entityDao.getEntityDetailByUuid(entityUuid);
		PageDetail pagedetail = sitedesignDao.getPageDetailByUuid(pageUuid);
		if(entitydetail!=null && thePageType!=null && theSiteType!=null){
			
			if(pagedetail!=null){ // page selected
				if(thePageType.equals(PageDetail.Type.category) && entitydetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
					if(theSiteType.equals(PageDetail.Type.Desktop)){
						entitydetail.setCatpageuuid_desktop(pageUuid);
					}else if(theSiteType.equals(PageDetail.Type.Mobile)){
						entitydetail.setCatpageuuid_mobile(pageUuid);
					}
					
				}else if(thePageType.equals(PageDetail.Type.product)){
					if(theSiteType.equals(PageDetail.Type.Desktop)){
						entitydetail.setProdpageuuid_desktop(pageUuid);
					}else if(theSiteType.equals(PageDetail.Type.Mobile)){
						entitydetail.setProdpageuuid_mobile(pageUuid);
					}
				}
				
			}else{ // page not selected
				if(thePageType.equals(PageDetail.Type.category) && entitydetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
					if(theSiteType.equals(PageDetail.Type.Desktop)){
						entitydetail.setCatpageuuid_desktop(null);
					}else if(theSiteType.equals(PageDetail.Type.Mobile)){
						entitydetail.setCatpageuuid_mobile(null);
					}
					
				}else if(thePageType.equals(PageDetail.Type.product)){
					if(theSiteType.equals(PageDetail.Type.Desktop)){
						entitydetail.setProdpageuuid_desktop(null);
					}else if(theSiteType.equals(PageDetail.Type.Mobile)){
						entitydetail.setProdpageuuid_mobile(null);
					}
				}
				
			}
			
			Long entityId = entityDao.saveEntityDetail(entitydetail);
			if(entityId!=null){
				apires.setSuccess(true);
			}else{
				apires.setResponse1("Default page ("+pagedetail.getPageuuid()+") is not saved for entity: "+entitydetail.getEntityuuid());
			}
			
			
		}else{
			apires.setResponse1("No enough information for process!");
		}
		
		return apires;
	}

	@Override
	public String getEntityUrl(String hostname, EntityTreeNode entityNode, PageDetail.Type sitetype, String orguuid, String categoryPageName, String productPageName) {
		if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
			StringBuilder url = new StringBuilder("http://").append(applicationConfig.getHostName()).append("/");
			if(sitetype.equals(PageDetail.Type.Desktop)){
				url.append("getPage/org/").append(orguuid);
			}else if(sitetype.equals(PageDetail.Type.Mobile)){
				url.append("getMobilePage/org/").append(orguuid);
			}
			
			if(entityNode.getType().equals(EntityDetail.EntityType.folder)){
				url.append("/pageurl/").append(categoryPageName).append("?categoryid=").append(entityNode.getSystemName());
			}else if(entityNode.getType().equals(EntityDetail.EntityType.entity)){
				url.append("/pageurl/").append(productPageName).append("?entityid=").append(entityNode.getSystemName());
			}
			
			return url.toString();
			
		}else{
			StringBuilder url = new StringBuilder();
			url.append("http://").append(hostname).append("/");
			if(entityNode.getType().equals(EntityDetail.EntityType.folder)){
				url.append("page/").append(categoryPageName).append("?categoryid=").append(entityNode.getSystemName());
			}else if(entityNode.getType().equals(EntityDetail.EntityType.entity)){
				url.append("page/").append(productPageName).append("?entityid=").append(entityNode.getSystemName());
			}
			return url.toString();
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public String getCategoryOrProductPageUuidForEntityNode(String entityUuid, PageDetail.Type pagetype, PageDetail.Type sitetype, String upperDefinedCategoryPageUuid, String upperDefinedProductPageUuid) {
		String categoryOrProductPageUuid = null;
		
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityUuid);
		if(entityDetail!=null && sitetype!=null && (sitetype.equals(PageDetail.Type.Desktop) || sitetype.equals(PageDetail.Type.Mobile))){
//			String[] paths = StringUtils.isNotBlank(entityDetail.getPath())?entityDetail.getPath().split("/"):null;
			
			if(entityDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
				
				// 1. find self
				if(sitetype.equals(PageDetail.Type.Desktop)){
					if(pagetype.equals(PageDetail.Type.category)){
						if(StringUtils.isNotBlank(entityDetail.getCatpageuuid_desktop())){
							categoryOrProductPageUuid = entityDetail.getCatpageuuid_desktop();
						}
					}else if(pagetype.equals(PageDetail.Type.product)){
						if(StringUtils.isNotBlank(entityDetail.getProdpageuuid_desktop())){
							categoryOrProductPageUuid = entityDetail.getProdpageuuid_desktop();
						}
					}
					
					
				}else if(sitetype.equals(PageDetail.Type.Mobile)){
					if(pagetype.equals(PageDetail.Type.category)){
						if(StringUtils.isNotBlank(entityDetail.getCatpageuuid_mobile())){
							categoryOrProductPageUuid = entityDetail.getCatpageuuid_mobile();
						}
					}else if(pagetype.equals(PageDetail.Type.product)){
						if(StringUtils.isNotBlank(entityDetail.getProdpageuuid_mobile())){
							categoryOrProductPageUuid = entityDetail.getProdpageuuid_mobile();
						}
					}
					
				}
				
				// 2. find parent
				if(categoryOrProductPageUuid==null && StringUtils.isNotBlank(entityDetail.getParentuuid())){
					if(StringUtils.isNotBlank(upperDefinedCategoryPageUuid)){
						categoryOrProductPageUuid = upperDefinedCategoryPageUuid;
					}else{
						categoryOrProductPageUuid = getCategoryOrProductPageUuidForEntityNode(entityDetail.getParentuuid(), pagetype, sitetype, null, null);
					}
				}
				
				// 3. find random one
//				if(pagetype.equals(PageDetail.Type.category)){
//					categoryOrProductPageUuid = sitedesignService.getRandomCategoryPageUuid(entityDetail.getOrganization_id());
//				}else if(pagetype.equals(PageDetail.Type.product)){
//					categoryOrProductPageUuid = sitedesignService.getRandomProductPageUuid(entityDetail.getOrganization_id());
//				}
				
			}else if(entityDetail.getType().equals(EntityDetail.EntityType.entity.getCode())){
				
				// 1. find self
				if(sitetype.equals(PageDetail.Type.Desktop)){
					if(StringUtils.isNotBlank(entityDetail.getProdpageuuid_desktop())){
						categoryOrProductPageUuid = entityDetail.getProdpageuuid_desktop();
					}
				}else if(sitetype.equals(PageDetail.Type.Mobile)){
					if(StringUtils.isNotBlank(entityDetail.getProdpageuuid_mobile())){
						categoryOrProductPageUuid = entityDetail.getProdpageuuid_mobile();
					}
				}
				
				// 2. find parent
				if(categoryOrProductPageUuid==null && StringUtils.isNotBlank(entityDetail.getParentuuid())){
					if(StringUtils.isNotBlank(upperDefinedProductPageUuid)){
						categoryOrProductPageUuid = upperDefinedProductPageUuid;
					}else{
						categoryOrProductPageUuid = getCategoryOrProductPageUuidForEntityNode(entityDetail.getParentuuid(), pagetype, sitetype, null, null);
					}
				}
				
				// 3. find random one
//				categoryOrProductPageUuid = sitedesignService.getRandomProductPageUuid(entityDetail.getOrganization_id());
				
			}
			
		}
		
		return categoryOrProductPageUuid;
	}

	@Override
	public int countProductsInFolderBySqlSegmentResponse(ApiResponse sqlSegmentResponse, String folderUuid, boolean includeFold, boolean includeLeaf) {
		
		int totalNode = 0;
		
		if(includeFold || includeLeaf){
			
			EntityDetail.EntityType entityType = null; // include leaf and fold
			if(!includeFold || !includeLeaf){
				if(includeFold){ // folder only
					entityType = EntityDetail.EntityType.folder;
				}else { // leaf only
					entityType = EntityDetail.EntityType.entity;
				}
			}
			
			StringBuilder countSqlStatement = null;
			if(sqlSegmentResponse!=null){
				if(sqlSegmentResponse.isSuccess()){ // folder permission is allowed
					if(sqlSegmentResponse.getResponse1()!=null){
						// for count
						countSqlStatement = new StringBuilder("SELECT count(*) FROM EntityDetail where");
						countSqlStatement.append(" parentuuid = '").append(folderUuid).append("'");
						
						if(entityType!=null){
							countSqlStatement.append(" AND type='").append(entityType.getCode()).append("'");
						}
						
						countSqlStatement.append(" AND ").append((String)sqlSegmentResponse.getResponse1());
						totalNode = entityDao.countAllProductsUnderCurrentFolderByCurrentFolderUuid(folderUuid, entityType, true, countSqlStatement!=null?countSqlStatement.toString():null);
					}else{
						totalNode = entityDao.countAllProductsUnderCurrentFolderByCurrentFolderUuid(folderUuid, entityType, true, null);
					}
				}else{ // folder permission is denied
					if(sqlSegmentResponse.getResponse1()!=null){
						// for count
						countSqlStatement = new StringBuilder("SELECT count(*) FROM EntityDetail where");
						countSqlStatement.append(" AND parentuuid = '").append(folderUuid).append("'");
						
						if(entityType!=null){
							countSqlStatement.append(" AND type='").append(entityType.getCode()).append("'");
						}
						
						countSqlStatement.append(" AND ").append((String)sqlSegmentResponse.getResponse1());
						totalNode = entityDao.countAllProductsUnderCurrentFolderByCurrentFolderUuid(folderUuid, entityType, true, countSqlStatement!=null?countSqlStatement.toString():null);
					}
				}
			}
			
		}
		
		return totalNode;
	}


}
