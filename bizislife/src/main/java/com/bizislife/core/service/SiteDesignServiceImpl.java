package com.bizislife.core.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dozer.Mapper;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.AccountController;
import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.PageTreeNode;
import com.bizislife.core.controller.component.Pagination;
import com.bizislife.core.controller.component.PaginationNode;
import com.bizislife.core.controller.component.PaginationNode.PaginationNodeType;
import com.bizislife.core.controller.helper.SwallowingJspRenderer;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.entity.MetaData;
import com.bizislife.core.entity.converter.ContainerToPageDetailConvertor;
import com.bizislife.core.entity.converter.ModuleConverter;
import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.bizislife.core.entity.converter.ModuleAttrGroupConverter;
import com.bizislife.core.hibernate.dao.AccountDao;
import com.bizislife.core.hibernate.dao.EntityDao;
import com.bizislife.core.hibernate.dao.GroupDao;
import com.bizislife.core.hibernate.dao.OrganizationDao;
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
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.PaymentPlan;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.ScheduleInterface;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.hibernate.pojo.Tree;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleXml;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageDetail.Type;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleImageAttribute;
import com.bizislife.core.siteDesign.module.ModuleIntegerAttribute;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute;
import com.bizislife.core.siteDesign.module.ModuleMoneyAttribute;
import com.bizislife.core.siteDesign.module.ModuleNumberAttribute;
import com.bizislife.core.siteDesign.module.ModuleProductListAttribute;
import com.bizislife.core.siteDesign.module.ModuleStringAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.ModuleTextAttribute;
import com.bizislife.core.siteDesign.module.Money;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.DateUtils;
import com.bizislife.util.HtmlEscape;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.validation.ValidationSet;
import com.sun.mail.imap.Rights;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class SiteDesignServiceImpl implements SiteDesignService {
	private static final Logger logger = LoggerFactory.getLogger(SiteDesignServiceImpl.class);

	@Autowired 
	ServletContext servletContext;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	SiteDesignDao siteDesignDao;
	
	@Autowired
	OrganizationDao orgDao;
	
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	EntityDao entityDao;
	
	@Autowired
	GroupDao groupDao;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	PermissionService permissionService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	TreeService treeService;

	@Autowired
	MessageFromPropertiesService messageFromPropertiesService;

    @Autowired
    protected ApplicationConfiguration applicationConfig;
    
	@Autowired
    private Mapper mapper;

	@Autowired
	private SwallowingJspRenderer jspRenderer;

    @Override
	@Transactional
	public String newPageNode(Type nodeType, String parentNodeUuid, String nodeName, PageTreeLevelView.Type viewType) {
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount!=null && nodeType!=null && viewType!=null && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(nodeName)){
			XStream stream = new XStream(new DomDriver());

			Date now = new Date();
			
			// get parent node detail
			PageDetail parentPageDetail = siteDesignDao.getPageDetailByUuid(parentNodeUuid.trim());
			if(parentPageDetail!=null){
				
				// permission check:
				boolean editPermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.modify, parentPageDetail.getPageuuid());
				
				if(editPermissionAllowed){
					
					StringBuilder path = new StringBuilder();
					if(StringUtils.isNotBlank(parentPageDetail.getPath())){
						path.append(parentPageDetail.getPath()).append("/");
					}
					path.append(parentPageDetail.getPageuuid());
					
					// new page detail
					String newPageNodeUuid = UUID.randomUUID().toString();
					// generate page url for page
					String url = null;
					if(nodeType.equals(PageDetail.Type.Desktop) || nodeType.equals(PageDetail.Type.Mobile)){
						url = newPageNodeUuid;
					}
					PageDetail pageDetail = new PageDetail(null, 
							newPageNodeUuid, 
							nodeName.trim(),
							url,
							null, 
							nodeType.getCode(), 
							parentNodeUuid, 
							path.toString(), 
							now, 
							parentPageDetail.getOrganization_id(), 
							currentAccount.getId());
					
					// for page level view
					// create a new pageTreeNode for level view
					PageTreeNode node = new PageTreeNode();
					node.setPrettyName(nodeName.trim());
					node.setSystemName(newPageNodeUuid);

					// get PageTreeLevelView by parent's uuid
					PageTreeLevelView levelView = siteDesignDao.getPageTreeLevelViewByParentUuid(parentNodeUuid);
					if(levelView==null){
						levelView = new PageTreeLevelView(
								null,
								viewType.getCode(),
								parentNodeUuid.trim(), 
								null, 
								now, 
								parentPageDetail.getOrganization_id(), 
								currentAccount.getId());
					}
					// update levelview's nodes
					if(StringUtils.isNotBlank(levelView.getNodes())){
						stream.alias("treeNode", PageTreeNode.class);
						List<PageTreeNode> nodes = (List<PageTreeNode>)stream.fromXML(levelView.getNodes().trim());
						if(nodes==null){
							nodes = new ArrayList<PageTreeNode>();
						}
						nodes.add(node);
						stream.processAnnotations(ArrayList.class);
						StringWriter sw = new StringWriter();
						stream.alias("treeNode", PageTreeNode.class);
						stream.marshal(nodes, new CompactWriter(sw));
						levelView.setNodes(sw.toString());
					}else{
						List<PageTreeNode> nodes = new ArrayList<PageTreeNode>();
						nodes.add(node);
						stream.processAnnotations(ArrayList.class);
						StringWriter sw = new StringWriter();
						stream.alias("treeNode", PageTreeNode.class);
						stream.marshal(nodes, new CompactWriter(sw));
						levelView.setNodes(sw.toString());
					}
					
					siteDesignDao.savePageDetail(pageDetail);
					siteDesignDao.savePageTreeLevelView(levelView);
					
					// create root containertreelevelview and containerdetail for this page
					if(nodeType.equals(Type.Desktop) || nodeType.equals(Type.Mobile)){
						// new containerDetail
						String containeruuid = UUID.randomUUID().toString();
//						StringBuilder containerPrettyName = new StringBuilder(nodeType.name()).append("DefaultContainer");
						StringBuilder containerPrettyName = new StringBuilder();
						if(nodeType.equals(Type.Desktop)){
							containerPrettyName.append("DefaultContainer");
						}else if(nodeType.equals(Type.Mobile)){
							containerPrettyName.append(nodeType.name()).append("DefaultContainer");
						}
						
						// generate a random color for the container:
						String hexcolor = WebUtil.hexColorRandomGenerator();
						ContainerDetail containerDetail = new ContainerDetail(null, 
								containeruuid, 
								containerPrettyName.toString(), 
								newPageNodeUuid,
								null, 
								null, 
								ContainerDetail.Direction.Top2Bottom.getCode(), 
								0, 0, applicationConfig.getPageDesignArea().get("width"), applicationConfig.getPageDesignArea().get("height"), 
								hexcolor,
								now, parentPageDetail.getOrganization_id(), currentAccount.getId(),
								ContainerDetail.BOOLEAN.disable.getCode(), ContainerDetail.BOOLEAN.disable.getCode()
								);
						
						// for container level view
						// create a new containerTreeNode for level view
						ContainerTreeNode containerTreeNode = new ContainerTreeNode();
						containerTreeNode.setPrettyName(containerPrettyName.toString());
						containerTreeNode.setSystemName(containeruuid);
						
						// create a containerTreeLevelView 
						ContainerTreeLevelView containerTreeLevelView = new ContainerTreeLevelView(
								null, 
								newPageNodeUuid, 
								null, null, now, parentPageDetail.getOrganization_id(), currentAccount.getId());
						// update containerTreeLevelView's nodes
						List<ContainerTreeNode> containerTreeNodes = new ArrayList<ContainerTreeNode>();
						containerTreeNodes.add(containerTreeNode);
						stream.processAnnotations(ArrayList.class);
						StringWriter sw = new StringWriter();
						stream.alias("treeNode", ContainerTreeNode.class);
						stream.marshal(containerTreeNodes, new CompactWriter(sw));
						containerTreeLevelView.setNodes(sw.toString());
						
						// save levelView and detail
						siteDesignDao.saveContainerDetail(containerDetail);
						siteDesignDao.saveContainerTreeLevelView(containerTreeLevelView);
						
					}
					
					// create a pagemeta for the page
					PageMeta pagemeta = new PageMeta(null, UUID.randomUUID().toString(), newPageNodeUuid, nodeName.trim(), PageMeta.DefaultCss.previewMode.getCode() ,null, null);
					siteDesignDao.savePageMeta(pagemeta);
					
					
					// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed 4) add pageMeta
					Organization org = orgDao.getOrganizationById(pageDetail.getOrganization_id());
					Accountprofile creator = accountDao.getAccountProfileByAccountId(currentAccount.getId());
					// 1) log the activity
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					String key_puuid = "pageUuid";
					ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put(key_oid, org.getId());
					dataMap.put(key_oname, org.getOrgname());
					dataMap.put(key_cname, 
							creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc());
					dataMap.put(key_cid, creator.getAccount_id());
					dataMap.put(key_puuid, pageDetail.getPageuuid());
					activityLogData.setDataMap(dataMap);
					String desc = messageFromPropertiesService.getMessageSource().getMessage("newPage", 
							new Object[] { 
								creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc(), 
								PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase(),
								pageDetail.getPageuuid()}, Locale.US);
					activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(currentAccount.getId(), pageDetail.getOrganization_id(), ActivityType.newPage, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							"New page creation",
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".create").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"new page creation",
							now,
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
					addToPageChangeList(pageDetail.getId(), activityLogId, desc);
					
					return newPageNodeUuid;
				}
				
			}
			
			
			
		}
		return null;
	}
    
    @Override
    @Transactional
	public ApiResponse cloneModuleNode(ModuleDetail.Type newNodeType, String parentNodeUuid, String nodeName, String cloneFromUuid){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
		// get parent node detail
		ModuleDetail parentNodeDetail = siteDesignDao.getModuleDetailByUuid(parentNodeUuid);
		// get cloneFrom node detail
		ModuleDetail cloneSourceDetail = siteDesignDao.getModuleDetailByUuid(cloneFromUuid);
		// get permission for clone
		boolean isPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.copy, cloneFromUuid);
		
		if(newNodeType!=null && (newNodeType.equals(ModuleDetail.Type.module) || newNodeType.equals(ModuleDetail.Type.productModule)) 
				&& StringUtils.isNotBlank(nodeName) 
				&& loginAccount!=null 
				&& parentNodeDetail!=null 
				&& cloneSourceDetail!=null && (cloneSourceDetail.getType().equals(ModuleDetail.Type.module.getCode()) || cloneSourceDetail.getType().equals(ModuleDetail.Type.productModule.getCode())) 
				&& isPermissionAllowed){
			Date now = new Date();
			
			ModuleDetail cloneDetail = cloneSourceDetail.clone(null);
			
			cloneDetail.setCreatedate(now);
			cloneDetail.setCreator_id(loginAccount.getId());
			cloneDetail.setPrettyname(nodeName);
			
			
			// for module level view
			// new moduleTreeNode
			ModuleTreeNode treenode = new ModuleTreeNode();
			treenode.setSystemName(cloneDetail.getModuleuuid());
			treenode.setPrettyName(nodeName);
			
			// add treenode to level view
			ModuleTreeLevelView treeLevelView = siteDesignDao.getModuleTreeLevelViewByParentUuid(parentNodeUuid);
			if(treeLevelView==null){
				treeLevelView = new ModuleTreeLevelView(null, 
						parentNodeUuid, 
						null, 
						now, 
						parentNodeDetail.getOrganization_id(), 
						loginAccount.getId());
			}
			// update the nodes info
			XStream stream = new XStream(new DomDriver());
			if(StringUtils.isNotBlank(treeLevelView.getNodes())){
				stream.alias("treeNode", ModuleTreeNode.class);
				List<ModuleTreeNode> nodes = (List<ModuleTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
				if(nodes==null){
					nodes = new ArrayList<ModuleTreeNode>();
				}
				nodes.add(treenode);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", ModuleTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				treeLevelView.setNodes(sw.toString());
			}else{
				List<ModuleTreeNode> nodes = new ArrayList<ModuleTreeNode>();
				nodes.add(treenode);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", ModuleTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				treeLevelView.setNodes(sw.toString());
			}
			
			Long newModuleId = siteDesignDao.saveModuleDetail(cloneDetail);
			Long newModuleTreeLevelViewId = siteDesignDao.saveModuleTreeLevelView(treeLevelView);
			
			if(newModuleId!=null && newModuleTreeLevelViewId!=null){
				apires.setSuccess(true);
				apires.setResponse1(cloneDetail.getModuleuuid());
				
				if(StringUtils.isNotBlank(cloneDetail.getJsp())){
					writeModuleJspToFile(newModuleId);
				}
				if(StringUtils.isNotBlank(cloneDetail.getCss())){
					writeModuleCssToFile(newModuleId);
				}
				
				
			}else{
				apires.setResponse1("System error, refresh the page and try again!");
			}
			
		}else{
			if(!isPermissionAllowed){
				apires.setResponse1("Permission is not allow for clone the Module: "+cloneFromUuid);
			}else{
				apires.setResponse1("System has some issues to get currentAccount, parentNodeDetail: "+parentNodeUuid+", cloneSourceDetail: "+cloneFromUuid);
			}
		}
    	
    	return apires;
    }

    
    
	@Override
	@Transactional
	public String newModuleNode(ModuleDetail.Type nodeType, String parentNodeUuid, String nodeName) {
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount!=null && nodeType!=null && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(nodeName)){
			Date now = new Date();
			
			
			// get parent node detail
			ModuleDetail parentNodeDetail = siteDesignDao.getModuleDetailByUuid(parentNodeUuid);
			if(parentNodeDetail!=null){
				
				StringBuilder path = new StringBuilder();
				if(StringUtils.isNotBlank(parentNodeDetail.getPath())){
					path.append(parentNodeDetail.getPath()).append("/");
				}
				path.append(parentNodeDetail.getModuleuuid());

				// new module detail
				String newModuleNodeUuid = UUID.randomUUID().toString();
				ModuleDetail moduledetail = new ModuleDetail(null, 
						newModuleNodeUuid, 
						nodeName, 
						null, 
						nodeType.getCode(), 
						parentNodeUuid, 
						path.toString(), 
						null,
						null,
						null,
						parentNodeDetail.getOrganization_id(), 
						null, 
						null, 
						ModuleDetail.Visibility.visibleInsideOrg.getCode(), 
						now, 
						currentAccount.getId());

				// for module level view
				// new moduleTreeNode
				ModuleTreeNode treenode = new ModuleTreeNode();
				treenode.setSystemName(newModuleNodeUuid);
				treenode.setPrettyName(nodeName);
				
				// add treenode to level view
				ModuleTreeLevelView treeLevelView = siteDesignDao.getModuleTreeLevelViewByParentUuid(parentNodeUuid);
				if(treeLevelView==null){
					treeLevelView = new ModuleTreeLevelView(null, 
							parentNodeUuid, 
							null, 
							now, 
							parentNodeDetail.getOrganization_id(), 
							currentAccount.getId());
				}
				// update the nodes info
				XStream stream = new XStream(new DomDriver());
				if(StringUtils.isNotBlank(treeLevelView.getNodes())){
					stream.alias("treeNode", ModuleTreeNode.class);
					List<ModuleTreeNode> nodes = (List<ModuleTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
					if(nodes==null){
						nodes = new ArrayList<ModuleTreeNode>();
					}
					nodes.add(treenode);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", ModuleTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					treeLevelView.setNodes(sw.toString());
				}else{
					List<ModuleTreeNode> nodes = new ArrayList<ModuleTreeNode>();
					nodes.add(treenode);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", ModuleTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					treeLevelView.setNodes(sw.toString());
				}
				
				Long newModuleId = siteDesignDao.saveModuleDetail(moduledetail);
				Long newModuleTreeLevelViewId = siteDesignDao.saveModuleTreeLevelView(treeLevelView);
				
				return newModuleNodeUuid;
				
			}
			
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ContainerDetail getContainerByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return siteDesignDao.getContainerDetailByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional
	public Long newContainerDetail(ContainerDetail container) {
		
		if(container!=null){
			Date now = new Date();
			Long treeViewId = newContainerTreeNode(container);
			if(treeViewId!=null){
				
				Long containerDetailId = siteDesignDao.saveContainerDetail(container);
				
				
				// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed 4) add pageMeta
				if(containerDetailId!=null){
					Organization org = orgDao.getOrganizationById(container.getOrganization_id());
					Accountprofile creator = accountDao.getAccountProfileByAccountId(container.getCreator_id());
					PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(container.getPageuuid());
					// 1) log the activity
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					String key_ctuuid = "containerUuid";
					ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put(key_oid, org.getId());
					dataMap.put(key_oname, org.getOrgname());
					dataMap.put(key_cname, 
							creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc());
					dataMap.put(key_cid, creator.getAccount_id());
					dataMap.put(key_ctuuid, container.getContaineruuid());
					activityLogData.setDataMap(dataMap);
					String desc = messageFromPropertiesService.getMessageSource().getMessage("newPageContainer", 
							new Object[] { 
								creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc(), 
								container.getContaineruuid(), 
								PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase(),
								container.getPageuuid()}, Locale.US);
					activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(container.getCreator_id(), container.getOrganization_id(), ActivityType.newPageContainer, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							"New container creation",
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".modify").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"new container creation",
							now,
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
					addToPageChangeList(pageDetail.getId(), activityLogId, desc);
					
				}
				
				return containerDetailId;
			}
		}
		return null;
	}

	private Long newContainerTreeNode(ContainerDetail container) {
		if(container!=null){
			// do all the validation: 1) total allowed containerLevel, 2) available space 3) optimization new container's position.
			boolean pass = true;
			
			//  total level check
			pass = checkContainerPathLevel(container.getPath());

			if(pass){ // availabe space check
				
				// get parent container
				ContainerDetail parentContainer = siteDesignDao.getContainerDetailByUuid(container.getParentuuid());
				if(parentContainer==null) {
					logger.error("System can't get parent container for new container - parentId: "+container.getParentuuid());
					return null;
				}
				
				Date now = new Date();
				XStream stream = new XStream(new DomDriver());
				// get ContainerTreeLevelView by parent id
				ContainerTreeLevelView treeLevelView = siteDesignDao.getContainerTreeLevelViewByParentUuid(container.getParentuuid());
				if(treeLevelView==null){
					treeLevelView = new ContainerTreeLevelView(null, 
							container.getPageuuid(), container.getParentuuid(), null, now, container.getOrganization_id(), container.getCreator_id());
				}
				
				List<ContainerTreeNode> nodes = null;
				if(StringUtils.isNotBlank(treeLevelView.getNodes())){
					stream.alias("treeNode", ContainerTreeNode.class);
					nodes = (List<ContainerTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
					if(nodes!=null && nodes.size()>0){
						// get all nodes' system ids
						int size = nodes.size();
						StringBuilder nodeIds = new StringBuilder();
						for(ContainerTreeNode n : nodes){
							size--;
							nodeIds.append("'").append(n.getSystemName()).append("'");
							if(size>0) nodeIds.append(",");
						}
						// get all containerDetails
						List<ContainerDetail> containerDetails = siteDesignDao.findContainerDetailsByIds(nodeIds.toString());
						if(containerDetails!=null && containerDetails.size()==nodes.size()){
							// check available space for new container in it's position.
							pass = checkAvailableSpaceForNewContainer(containerDetails, container, parentContainer);
							if(pass){
								// optimize new container's position
								optimizeNewContainerPosition(containerDetails, parentContainer, container);
							}
						}else{
							logger.error("the number of containerDetails and the number of containerTreeNodes are not match for "+nodeIds.toString());
							pass = false;
						}
					}
				}
				
				// available space check & optimization if no siblings find
				if(nodes==null){
					pass = checkAvailableSpaceForNewContainer(null, container, parentContainer);
					if(pass){
						optimizeNewContainerPosition(null, parentContainer, container);
					}
				}
				
				if(pass){
					// create treenode and treelevelview
					ContainerTreeNode containerTreeNode = new ContainerTreeNode();
					containerTreeNode.setPrettyName(container.getPrettyname().toString());
					containerTreeNode.setSystemName(container.getContaineruuid());
					if(nodes==null){
						nodes = new ArrayList<ContainerTreeNode>();
					}
					nodes.add(containerTreeNode);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", ContainerTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					treeLevelView.setNodes(sw.toString());
					return siteDesignDao.saveContainerTreeLevelView(treeLevelView);

				}
			}
		}
		return null;
	}

	/**
	 * find available space for newContainer's point position. New container can be created between two exist containers, or between parent container's top and exist container or between parent container's bottom and exist containers
	 * 
	 * @param existContainers
	 * @param newContainer
	 * @return
	 */
	private int availableSpaceForNewContainer(List<ContainerDetail> existContainers, ContainerDetail newContainer, ContainerDetail parentContainer){
		if(newContainer!=null && parentContainer!=null 
				&& newContainer.getDirection()!=null && !StringUtils.equals(newContainer.getDirection().trim(), parentContainer.getDirection().trim())){
			if(ContainerDetail.Direction.Top2Bottom.getCode().equals(newContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestLeftRightPosition(existContainers, newContainer, parentContainer);
				if(posi!=null){
					return posi.get("right")-posi.get("left");
				}
			}else if(ContainerDetail.Direction.Left2Right.getCode().equals(newContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestTopBottomPosition(existContainers, newContainer, parentContainer);
				if(posi!=null){
					return posi.get("bottom")-posi.get("top");
				}
			}
		}
		
		return -1;
	}
	
	private Map<String, Integer> getClosestTopBottomPosition(List<ContainerDetail> existContainers, ContainerDetail newContainer, ContainerDetail parentContainer){
		if(newContainer!=null && parentContainer!=null
				&& newContainer.getDirection()!=null && parentContainer.getDirection()!=null
				&& ContainerDetail.Direction.Left2Right.getCode().equals(newContainer.getDirection().trim())
				&& ContainerDetail.Direction.Top2Bottom.getCode().equals(parentContainer.getDirection().trim())){
			
			Map<String, Integer> posi = new HashMap<String, Integer>();
			if(existContainers==null || existContainers.size()==0){
				posi.put("top", 0);
				posi.put("bottom", parentContainer.getHeight());
			}else{
				int topsidePosition = 0; //initial closest top side position with parent's top-side
				int bottomsidePosition = parentContainer.getHeight(); // initial closest bottom side position with parent's bottom side
				for(ContainerDetail c : existContainers){
					int cBottomPosition = c.getTopposition() + c.getHeight();
					
					// return null (no space) if new container's top position is not defined
					if(newContainer.getTopposition()==null) return null;
					// return null (no space) if new container is inside one of it's siblings
//					if(newContainer.getTopposition()>c.getTopposition() && newContainer.getTopposition()<cBottomPosition) return null;
					
					// get closest top position
					if(newContainer.getTopposition()>cBottomPosition && topsidePosition<cBottomPosition){
						topsidePosition = cBottomPosition;
					}
					
					// get closest bottom position
					if(newContainer.getTopposition()<=c.getTopposition() && bottomsidePosition>c.getTopposition()){
						bottomsidePosition = c.getTopposition();
					}

				}
				
				// +1 pixel if topsidePosition!=0; -1 pixel if bottomsidePosition!=parentContainer.getHeight();
				if(topsidePosition>0) topsidePosition = topsidePosition + 1; // avoid overlap with above container
				if(bottomsidePosition<parentContainer.getHeight()) bottomsidePosition = bottomsidePosition - 1; // avoid overlap with below container
				
				posi.put("top", topsidePosition);
				posi.put("bottom", bottomsidePosition);
			}
			return posi;
		}
			
		return null;
	}

	private Map<String, Integer> getClosestLeftRightPosition(List<ContainerDetail> existContainers, ContainerDetail newContainer, ContainerDetail parentContainer){
		if(newContainer!=null && parentContainer!=null 
				&& newContainer.getDirection()!=null && parentContainer.getDirection()!=null
				&& ContainerDetail.Direction.Top2Bottom.getCode().equals(newContainer.getDirection().trim())
				&& ContainerDetail.Direction.Left2Right.getCode().equals(parentContainer.getDirection().trim())){
			Map<String, Integer> posi = new HashMap<String, Integer>();
			if(existContainers==null || existContainers.size()==0){
				posi.put("left", 0);
				posi.put("right", parentContainer.getWidth());
			}else{
				
				int leftsidePosition = 0; // initial closest left side position with parent's left-side
				int rightsidePosition = parentContainer.getWidth(); // initial closest right side position with parent's right-side
				for(ContainerDetail c : existContainers){
					int cRightPosition = c.getLeftposition() + c.getWidth();

					// return null (no space) if new container's left position is not defined
					if(newContainer.getLeftposition()==null) return null;
					// return null (no space) if new container is inside one of it's siblings
//					if(newContainer.getLeftposition()>c.getLeftposition() && newContainer.getLeftposition()<cRightPosition) return null;
					
					// get closest left position
					if(newContainer.getLeftposition()>=cRightPosition && leftsidePosition<cRightPosition){
						leftsidePosition = cRightPosition;
					}
					
					// get closest right position
					if(newContainer.getLeftposition()<=c.getLeftposition() && rightsidePosition>c.getLeftposition()){
						rightsidePosition = c.getLeftposition();
					}
				}
				
				// +1 pixel if leftsidePosition!=0; -1 pixel if rightsidePosition!=parentContainer.getWidth();
				if(leftsidePosition>0) leftsidePosition = leftsidePosition + 1; // avoid overlap with left container
				if(rightsidePosition<parentContainer.getWidth()) rightsidePosition = rightsidePosition - 1; // avoid overlap with right container
				
				posi.put("left", leftsidePosition);
				posi.put("right", rightsidePosition);
				
			}
			
			return posi;
			
		}
		return null;
	}
	
	private boolean checkAvailableSpaceForNewContainer(List<ContainerDetail> existContainers, ContainerDetail newContainer, ContainerDetail parentContainer){
		boolean pass = false;
		int availableSpace = availableSpaceForNewContainer(existContainers, newContainer, parentContainer);
		int minSpace = 10000000;
		if("1".equals(newContainer.getDirection().trim())){
			minSpace = applicationConfig.getContainerDefaultSize().get("width");
		}else if("0".equals(newContainer.getDirection().trim())){
			minSpace = applicationConfig.getContainerDefaultSize().get("height");
		}
		if(availableSpace>=minSpace) pass = true;
		
		return pass;
	}
	
	/**
	 * optimize new container's x or y position based on exist containers. there have a little movement for new container (up, down, right, left) if new container is too close to next container or parent's borders.
	 * Note: this method doesn't check availableSpaceForNewContainer, you need do this first. This method only moves new container to left if new container's right side doesn't inside available space, or move new container to top if new container's bottom side doesn't inside available space.
	 * 
	 * @param existContainers
	 * @param newContainer
	 */
	private void optimizeNewContainerPosition(List<ContainerDetail> existContainers, ContainerDetail parentContainer, ContainerDetail newContainer){
		if(parentContainer!=null && newContainer!=null){
			
			if(ContainerDetail.Direction.Top2Bottom.getCode().equals(newContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestLeftRightPosition(existContainers, newContainer, parentContainer);
				int cRightPosition = newContainer.getLeftposition() + newContainer.getWidth();
				int diff = cRightPosition - posi.get("right");
				if(diff>0){
					newContainer.setLeftposition(newContainer.getLeftposition()-diff);
				}
				
				
			}else if(ContainerDetail.Direction.Left2Right.getCode().equals(newContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestTopBottomPosition(existContainers, newContainer, parentContainer);
				int cBottomPosition = newContainer.getTopposition() + newContainer.getHeight();
				int diff = cBottomPosition - posi.get("bottom");
				if(diff>0){
					newContainer.setTopposition(newContainer.getTopposition()-diff);
				}
			}
			
		}
	}
	
	/**
	 * @param existContainers
	 * @param parentContainer
	 * @param resizedContainer
	 * @return if the resizedContainer is optimized. 
	 */
	private void optimizeResizeContainerPosition(List<ContainerDetail> existContainers, ContainerDetail parentContainer, ContainerDetail resizedContainer, ContainerDetail containerBeforeResize){
		if(parentContainer!=null && resizedContainer!=null){
			if(ContainerDetail.Direction.Top2Bottom.getCode().equals(resizedContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestLeftRightPosition(existContainers, containerBeforeResize, parentContainer);
				int cLeftPosi = resizedContainer.getLeftposition();
				int cRightPosi = resizedContainer.getLeftposition() + resizedContainer.getWidth();
				
				int rightDiff = cRightPosi - posi.get("right");
				if(rightDiff>0) resizedContainer.setWidth(resizedContainer.getWidth() - rightDiff);
				
				int leftDiff = cLeftPosi - posi.get("left");
				if(leftDiff<0) {
					resizedContainer.setLeftposition(posi.get("left"));
					resizedContainer.setWidth(resizedContainer.getWidth() + leftDiff);
				}
				
			}else if(ContainerDetail.Direction.Left2Right.getCode().equals(resizedContainer.getDirection().trim())){
				Map<String, Integer> posi = getClosestTopBottomPosition(existContainers, containerBeforeResize, parentContainer);
				int cTopPosi = resizedContainer.getTopposition();
				int cBottomPosi = resizedContainer.getTopposition() + resizedContainer.getHeight();
				
				int bottomDiff = cBottomPosi - posi.get("bottom");
				if(bottomDiff>0) resizedContainer.setHeight(resizedContainer.getHeight() - bottomDiff);
				
				int topDiff = cTopPosi - posi.get("top");
				if(topDiff<0) {
					resizedContainer.setTopposition(posi.get("top"));
					resizedContainer.setHeight(resizedContainer.getHeight() + topDiff);
				}
			}
		}
		
	}
	
	@Override
	public boolean checkContainerPathLevel(String path){
		boolean pass = false;
		if(StringUtils.isNotBlank(path)){
			String [] paths = path.trim().split("/");
			int maxPathLevelPreDefined = applicationConfig.getTotalAllowedContainerLevel();
			if(paths.length+1<=maxPathLevelPreDefined) return true;
		}
		return pass; 
	}

	@Override
	@Transactional
	public ApiResponse resizeContainer(String containerId, int left, int top, int width, int height) {
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ContainerDetail container = siteDesignDao.getContainerDetailByUuid(containerId);
		ContainerDetail containerBeforeResize = mapper.map(container, ContainerDetail.class);
		if(currentAccount!=null && container!=null){
			boolean updateNeed = false;
			
			// get original top, left, width, height
			int top_ori = container.getTopposition();
			int left_ori = container.getLeftposition();
			int width_ori = container.getWidth();
			int height_ori = container.getHeight();
			
			// reset container's position
			if(ContainerDetail.Direction.Top2Bottom.getCode().equals(container.getDirection().trim())){
				// change left and width only
				container.setLeftposition(left);
				container.setWidth(width);
			}else if(ContainerDetail.Direction.Left2Right.getCode().equals(container.getDirection().trim())){
				// change top and height only
				container.setTopposition(top);
				container.setHeight(height);
			}
			
			ContainerDetail parentContainer = siteDesignDao.getContainerDetailByUuid(container.getParentuuid());
			if(parentContainer==null) {
				logger.error("System can't get parent container for new container - parentId: "+container.getParentuuid());
				return null;
			}
			
			Date now = new Date();
			XStream stream = new XStream(new DomDriver());
			// get ContainerTreeLevelView by parent id
			ContainerTreeLevelView treeLevelView = siteDesignDao.getContainerTreeLevelViewByParentUuid(container.getParentuuid());
			
			List<ContainerTreeNode> nodes = null;
			if(StringUtils.isNotBlank(treeLevelView.getNodes())){
				stream.alias("treeNode", ContainerTreeNode.class);
				nodes = (List<ContainerTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
				if(nodes!=null && nodes.size()>0){
					// get all nodes' system ids
					int size = nodes.size();
					StringBuilder nodeIds = new StringBuilder();
					for(ContainerTreeNode n : nodes){
						size--;
						nodeIds.append("'").append(n.getSystemName()).append("'");
						if(size>0) nodeIds.append(",");
					}
					// get all containerDetails
					List<ContainerDetail> containerDetails = siteDesignDao.findContainerDetailsByIds(nodeIds.toString());
					
					if(containerDetails!=null && containerDetails.size()==nodes.size()){
						// remove self for calculating proper closest position
						ListIterator<ContainerDetail> containerIters = containerDetails.listIterator();
						while(containerIters.hasNext()){
							ContainerDetail removableContainer = containerIters.next();
							if(removableContainer.getId().equals(container.getId())){
								containerIters.remove();
								break;
							}
						}
						
						optimizeResizeContainerPosition(containerDetails, parentContainer, container, containerBeforeResize);
						
						
					}else{
						logger.error("the number of containerDetails and the number of containerTreeNodes are not match for "+nodeIds.toString());
					}
				}
			}
			
			if(nodes==null){
				optimizeResizeContainerPosition(null, parentContainer, container, containerBeforeResize);
			}
			
			// check if sized been changed or not been changed after optimization.
			if(container.getTopposition()!=top_ori 
					|| container.getLeftposition()!=left_ori 
					|| container.getWidth()!=width_ori 
					|| container.getHeight()!=height_ori) 
				updateNeed = true;
			
			if(updateNeed){
				Long containerDetailId = siteDesignDao.saveContainerDetail(container);
				
				
				// activity log
				// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed 4) add pageMeta
				if(containerDetailId!=null){
					Organization org = orgDao.getOrganizationById(container.getOrganization_id());
					Accountprofile creator = accountDao.getAccountProfileByAccountId(currentAccount.getId());
					PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(container.getPageuuid());
					// 1) log the activity
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					String key_ctuuid = "containerUuid";
					ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put(key_oid, org.getId());
					dataMap.put(key_oname, org.getOrgname());
					dataMap.put(key_cname, 
							creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc());
					dataMap.put(key_cid, creator.getAccount_id());
					dataMap.put(key_ctuuid, container.getContaineruuid());
					activityLogData.setDataMap(dataMap);
					String desc = messageFromPropertiesService.getMessageSource().getMessage("changePageContainer", 
							new Object[] { 
								creator!=null?
									(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
									:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc(), 
								"resizes",
								container.getContaineruuid(), 
								container.getPageuuid(),
								container.getPrettyname()+" resize"
								},
								Locale.US);
					activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(currentAccount.getId(), container.getOrganization_id(), ActivityType.changePageContainer, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							"Container modification",
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".modify").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"Container modification",
							now,
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
					addToPageChangeList(pageDetail.getId(), activityLogId, desc);
					
					
					// for return value
					apires.setSuccess(true);
					apires.setResponse1(container);
					apires.setResponse2(pageDetail.getPageuuid());
				}
				
				
			}
			
			return apires;
			
		}
		
		
		return apires;
	}

	@Override
	@Transactional
	public Long updateModuleDetailForXmlJspDesc(String moduleUuid, String moduleDesc, String moduleXml, String moduleJsp, String moduleCss) {
		if(StringUtils.isNotBlank(moduleUuid)){
			//get moduledetail
			ModuleDetail detail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			// get module instance based on moduleUuid
			// NOTE: module xml can't be modified if module has instance(s) already.
			List<ModuleInstance> instances = siteDesignDao.findModuleInstancesByModuleUuid(moduleUuid);
			if(detail!=null){
				detail.setDescription(moduleDesc.trim());
				detail.setJsp(StringEscapeUtils.escapeHtml(moduleJsp.trim()));
				detail.setCss(moduleCss.trim());
				if(instances==null || instances.size()==0){
					detail.setXml(moduleXml.trim());
				}
				Long moduleId = siteDesignDao.saveModuleDetail(detail);
				return moduleId;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleDetail getModuleDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return siteDesignDao.getModuleDetailByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse writeModuleJspToFile(Long moduleId) {
		
		ApiResponse apires = null;
		
		StringBuilder filePathToOrgModule = new StringBuilder(servletContext.getRealPath("/WEB-INF/view/module/org"));
		// get jsp from detail
		ModuleDetail detail = siteDesignDao.getModuleDetailById(moduleId);
		if(detail!=null && StringUtils.isNotBlank(detail.getJsp())){
			if(filePathToOrgModule.indexOf("/")>-1){
				filePathToOrgModule.append("/").append(detail.getOrganization_id());
			}else{
				filePathToOrgModule.append("\\").append(detail.getOrganization_id());
			}
			
			File folder = new File(filePathToOrgModule.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(filePathToOrgModule).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			// create or replace jsp
			if(folderExist){
				
				StringBuilder fileFullPath = new StringBuilder(filePathToOrgModule);
				if(fileFullPath.indexOf("/")>-1){
					fileFullPath.append("/").append(detail.getModuleuuid()).append(".jsp");
				}else{
					fileFullPath.append("\\").append(detail.getModuleuuid()).append(".jsp");
				}
				
    			File destinationFile = new File(fileFullPath.toString());

    			FileOutputStream outStream = null;
				try {
					
			        // add taglib if view without taglib
			        String detailJsp = detail.getJsp();
//			        if(detailJsp.indexOf("http://java.sun.com/jsp/jstl/functions")<0){
//			        	detailJsp = "&lt;%@ taglib prefix=&quot;fn&quot; uri=&quot;http://java.sun.com/jsp/jstl/functions&quot;%&gt;\n"+detailJsp;
//			        }
//			        
//			        if(detailJsp.indexOf("http://java.sun.com/jsp/jstl/core")<0){
//			        	detailJsp = "&lt;%@taglib prefix=&quot;c&quot; uri=&quot;http://java.sun.com/jsp/jstl/core&quot;%&gt;\n"+detailJsp;
//			        }
					
					outStream = new FileOutputStream(destinationFile, false);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			        final BufferedWriter fbw = new BufferedWriter(writer);
			        String outputString = StringEscapeUtils.unescapeHtml(detailJsp);
			        if(outputString!=null){
				        outputString = StringUtils.replaceEach(outputString, new String[]{"<%", "%>"}, new String[]{"&lt;%--", "--%&gt;"});
				        
				        outputString = "<%@taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\"%>\n"
				        		+ "<%@ taglib prefix=\"fn\" uri=\"http://java.sun.com/jsp/jstl/functions\"%>\n"
				        		+ "<%@page contentType=\"text/html; charset=UTF-8\" %>"
				        		+ outputString;
			        }else{
			        	outputString = "";
			        }
			        
			        
			        
			        
		            fbw.write(outputString);
		            fbw.newLine();
		            fbw.flush();
		            fbw.close();
		            writer.close();
		            
					apires = new ApiResponse();
					apires.setSuccess(true);
					apires.setResponse1("jsp: '"+detail.getModuleuuid()+".jsp' is created");

		            
				} catch (FileNotFoundException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} finally{
					if(outStream!=null)
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}else{
				apires = new ApiResponse();
				apires.setSuccess(false);
				apires.setResponse1("folder: '"+filePathToOrgModule+"' can't be created!");
			}
			
			
			
		}
	
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleDetail> findOrgModules(Long orgId) {
		if(orgId!=null){
			return siteDesignDao.findOrgModules(orgId);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleDetail> findOrgModulesWithProModulesAggregate(Long orgId) {
		if(orgId!=null){
			List<ModuleDetail> modules = siteDesignDao.findOrgModules(orgId);
			if(modules!=null && modules.size()>0){
				List<ModuleDetail> results = new ArrayList<ModuleDetail>();
				boolean hasProductModule = false;
				for(ModuleDetail m : modules){
					if(m.getType().equals(ModuleDetail.Type.productModule.getCode())){
						hasProductModule = true;
					}else{
						results.add(m);
					}
				}
				if(hasProductModule){
					ModuleDetail genericProductModule = SitedesignHelper.newProductModule(orgId);
					results.add(genericProductModule);
				}
				return results;
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public Long saveModuleInstance(ModuleInstance instance) {
		return siteDesignDao.saveModuleInstance(instance);
	}

	@Override
	@Transactional
	public Long newModuleInstance(ModuleInstance inst) {
		if(inst!=null){
			// moduletreelevelvew
			ModuleTreeNode treenode = new ModuleTreeNode();
			treenode.setSystemName(inst.getModuleinstanceuuid());
			treenode.setPrettyName(inst.getName());
			ModuleTreeLevelView treeLevelView = siteDesignDao.getModuleTreeLevelViewByParentUuid(inst.getModuleuuid());
			if(treeLevelView==null){
				treeLevelView = new ModuleTreeLevelView(null, 
						inst.getModuleuuid(), 
						null, 
						inst.getCreatedate(), 
						inst.getOrgid(), 
						inst.getCreator_id());
			}
			// update the nodes info
			XStream stream = new XStream(new DomDriver());
			if(StringUtils.isNotBlank(treeLevelView.getNodes())){
				stream.alias("treeNode", ModuleTreeNode.class);
				List<ModuleTreeNode> nodes = (List<ModuleTreeNode>)stream.fromXML(treeLevelView.getNodes().trim());
				if(nodes==null){
					nodes = new ArrayList<ModuleTreeNode>();
				}
				nodes.add(treenode);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", ModuleTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				treeLevelView.setNodes(sw.toString());
			}else{
				List<ModuleTreeNode> nodes = new ArrayList<ModuleTreeNode>();
				nodes.add(treenode);
				stream.processAnnotations(ArrayList.class);
				StringWriter sw = new StringWriter();
				stream.alias("treeNode", ModuleTreeNode.class);
				stream.marshal(nodes, new CompactWriter(sw));
				treeLevelView.setNodes(sw.toString());
			}
			Long newModuleTreeLevelViewId = siteDesignDao.saveModuleTreeLevelView(treeLevelView);			
			Long newModuleInstanceId = siteDesignDao.saveModuleInstance(inst);
			if(newModuleTreeLevelViewId!=null && newModuleInstanceId!=null) return newModuleInstanceId;
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleInstance getModuleInstanceByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return siteDesignDao.getModuleInstanceByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleInstance> findModuleInstancesByModuleUuid(String moduleUuid) {
		if(StringUtils.isNotBlank(moduleUuid)){
			return siteDesignDao.findModuleInstancesByModuleUuid(moduleUuid);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByContainerUuid(String containerUuid) {
		if(StringUtils.isNotBlank(containerUuid)){
			return siteDesignDao.findContainerModuleSchedulesByContainerUuid(containerUuid);
		}
		return null;
	}

	@Override
	@Transactional
	public Long saveContainerModuleSchedule(ContainerModuleSchedule sch) {
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ContainerDetail containerdetail = siteDesignDao.getContainerDetailByUuid(sch.getContaineruuid());
		
		if(loginaccount!=null && sch!=null && containerdetail!=null){
			
			// permission check
			boolean editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerdetail.getPageuuid());
			if(editPermissionAllowed){
				return siteDesignDao.saveContainerModuleSchedule(sch);
			}
		}
		return null;
	}

	@Override
	@Transactional
	public void delContainerModuleScheduleByUuid(String uuid) {
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ContainerModuleSchedule containerModuleSched = siteDesignDao.getContainerModuleScheduleByUuid(uuid);
		if(loginaccount!=null && containerModuleSched!=null){
			ContainerDetail containerDetail = siteDesignDao.getContainerDetailByUuid(containerModuleSched.getContaineruuid());
			
			if(containerDetail!=null){
				// permission check:
				boolean editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
				
				if(editPermissionAllowed){
					siteDesignDao.delContainerModuleScheduleByUuid(uuid);
				}
			}
			
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public ContainerModuleSchedule getContainerModuleScheduleByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return siteDesignDao.getContainerModuleScheduleByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse writeModuleCssToFile(Long moduleId) {
		
		ApiResponse apires = null;
		
		StringBuilder filePathToOrgModule = new StringBuilder(servletContext.getRealPath("/css/org"));
		// get css from detail
		ModuleDetail detail = siteDesignDao.getModuleDetailById(moduleId);
		if(detail!=null && StringUtils.isNotBlank(detail.getCss())){
			
			if(filePathToOrgModule.indexOf("/")>-1){
				filePathToOrgModule.append("/").append(detail.getOrganization_id());
			}else{
				filePathToOrgModule.append("\\").append(detail.getOrganization_id());
			}
			
			File folder = new File(filePathToOrgModule.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(filePathToOrgModule).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			// create or replace jsp
			if(folderExist){
				
				StringBuilder fileFullPath = new StringBuilder(filePathToOrgModule);
				if(fileFullPath.indexOf("/")>-1){
					fileFullPath.append("/").append(detail.getModuleuuid()).append(".css");
				}else{
					fileFullPath.append("\\").append(detail.getModuleuuid()).append(".css");
				}
				
    			File destinationFile = new File(fileFullPath.toString());

    			FileOutputStream outStream = null;
				try {
					outStream = new FileOutputStream(destinationFile, false);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			        final BufferedWriter fbw = new BufferedWriter(writer);
			        String outputString = detail.getCss();
			        outputString = StringEscapeUtils.unescapeHtml(outputString);
			        if(outputString!=null){
			        	outputString = StringUtils.replaceEach(outputString, new String[]{"<%", "%>"}, new String[]{"&lt;%--", "--%&gt;"});
			        }else{
			        	outputString = "";
			        }
		            fbw.write(outputString);
		            fbw.newLine();
		            fbw.flush();
		            fbw.close();
		            writer.close();
		            
					apires = new ApiResponse();
					apires.setSuccess(true);
					apires.setResponse1("css: '"+detail.getModuleuuid()+".css' is created");

		            
				} catch (FileNotFoundException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} finally{
					if(outStream!=null)
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}else{
				apires = new ApiResponse();
				apires.setSuccess(false);
				apires.setResponse1("folder: '"+filePathToOrgModule+"' can't be created!");
			}
			
			
			
		}
	
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public List<InstanceView> findInstanceViewsByInstanceUuid(String instanceUuid) {
		if(StringUtils.isNotBlank(instanceUuid)){
			return siteDesignDao.findInstanceViewsByInstanceUuid(instanceUuid);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public InstanceView getInstanceViewByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return siteDesignDao.getInstanceViewByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional
	public Long saveInstanceView(InstanceView view) {
		if(view!=null)
			return siteDesignDao.saveInstanceView(view);
		return null;
	}

	@Override
	@Transactional
	public void setDefaultInstanceView(String instanceViewUuid, InstanceView.IsDefault isDefault) {
		if(StringUtils.isNotBlank(instanceViewUuid) && isDefault!=null){
			// get view
			InstanceView view = siteDesignDao.getInstanceViewByUuid(instanceViewUuid);
			if(view!=null){
				if(!view.getIsdefault().equals(isDefault.getCode())){
					if(isDefault.equals(InstanceView.IsDefault.no)){
						view.setIsdefault(InstanceView.IsDefault.no.getCode());
					}else if(isDefault.equals(InstanceView.IsDefault.yes)){
						// find old default view for the instance, and updated to no-default
						InstanceView preDefaultView = siteDesignDao.getDefaultViewForInstance(view.getModuleinstanceuuid());
						if(preDefaultView!=null){
							preDefaultView.setIsdefault(InstanceView.IsDefault.no.getCode());
							siteDesignDao.saveInstanceView(preDefaultView);
							view.setIsdefault(InstanceView.IsDefault.yes.getCode());
						}else{
							view.setIsdefault(InstanceView.IsDefault.yes.getCode());
						}
						
					}
					siteDesignDao.saveInstanceView(view);
				}
				
				
			}
		}
	}

	@Override
	@Transactional
	public ApiResponse writeInstanceViewCssToFile(Long viewId) {
		ApiResponse apires = null;
		
		StringBuilder filePathToOrgModule = new StringBuilder(servletContext.getRealPath("/css/org"));
		// get css from InstanceView
		//ModuleDetail detail = siteDesignDao.getModuleDetailById(moduleId);
		InstanceView view = siteDesignDao.getInstanceViewById(viewId);
		if(view!=null && StringUtils.isNotBlank(view.getCss())){
			if(filePathToOrgModule.indexOf("/")>-1){
				filePathToOrgModule.append("/").append(view.getOrgid());
			}else{
				filePathToOrgModule.append("\\").append(view.getOrgid());
			}
			
			File folder = new File(filePathToOrgModule.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(filePathToOrgModule).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			// create or replace jsp
			if(folderExist){
				
				StringBuilder fileFullPath = new StringBuilder(filePathToOrgModule);
				if(fileFullPath.indexOf("/")>-1){
					fileFullPath.append("/").append(view.getInstanceviewuuid()).append(".css");
				}else{
					fileFullPath.append("\\").append(view.getInstanceviewuuid()).append(".css");
				}
				
				
    			File destinationFile = new File(fileFullPath.toString());

    			FileOutputStream outStream = null;
				try {
					outStream = new FileOutputStream(destinationFile, false);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			        final BufferedWriter fbw = new BufferedWriter(writer);
			        
			        String outputString = view.getCss();
			        outputString = StringEscapeUtils.unescapeHtml(outputString);
			        if(outputString!=null){
			        	outputString = StringUtils.replaceEach(outputString, new String[]{"<%", "%>"}, new String[]{"&lt;%--", "--%&gt;"});
			        }else{
			        	outputString = "";
			        }
			        
		            fbw.write(outputString);
		            fbw.newLine();
		            fbw.flush();
		            fbw.close();
		            writer.close();
		            
					apires = new ApiResponse();
					apires.setSuccess(true);
					apires.setResponse1("css: '"+view.getInstanceviewuuid()+".css' is created");

		            
				} catch (FileNotFoundException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} finally{
					if(outStream!=null)
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}else{
				apires = new ApiResponse();
				apires.setSuccess(false);
				apires.setResponse1("folder: '"+filePathToOrgModule+"' can't be created!");
			}
		}
	
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse writeInstanceViewJspToFile(Long viewId) {
		ApiResponse apires = null;
		
		StringBuilder filePathToOrgModule = new StringBuilder(servletContext.getRealPath("/WEB-INF/view/module/org"));
		// get jsp from view
//		ModuleDetail detail = siteDesignDao.getModuleDetailById(moduleId);
		InstanceView view = siteDesignDao.getInstanceViewById(viewId);
		if(view!=null && StringUtils.isNotBlank(view.getJsp())){
			if(filePathToOrgModule.indexOf("/")>-1){
				filePathToOrgModule.append("/").append(view.getOrgid());
			}else{
				filePathToOrgModule.append("\\").append(view.getOrgid());
			}
			
			File folder = new File(filePathToOrgModule.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(filePathToOrgModule).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			// create or replace jsp
			if(folderExist){
				
				StringBuilder fileFullPath = new StringBuilder(filePathToOrgModule);
				if(fileFullPath.indexOf("/")>-1){
					fileFullPath.append("/").append(view.getInstanceviewuuid()).append(".jsp");
				}else{
					fileFullPath.append("\\").append(view.getInstanceviewuuid()).append(".jsp");
				}
				
    			File destinationFile = new File(fileFullPath.toString());

    			FileOutputStream outStream = null;
				try {
					outStream = new FileOutputStream(destinationFile, false);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			        final BufferedWriter fbw = new BufferedWriter(writer);
			        
			        // add taglib if view without taglib
			        String viewJsp = view.getJsp();
			        String outputString = StringEscapeUtils.unescapeHtml(viewJsp);
			        
			        if(outputString!=null){
				        outputString = StringUtils.replaceEach(outputString, new String[]{"<%", "%>"}, new String[]{"&lt;%--", "--%&gt;"});
				        
				        outputString = "<%@taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\"%>\n"
				        		+ "<%@ taglib prefix=\"fn\" uri=\"http://java.sun.com/jsp/jstl/functions\"%>\n"
				        		+ "<%@page contentType=\"text/html; charset=UTF-8\" %>"
				        		+ outputString;
			        }else{
			        	outputString = "";
			        }
			        
			        
		            fbw.write(outputString);
		            fbw.newLine();
		            fbw.flush();
		            fbw.close();
		            writer.close();
		            
					apires = new ApiResponse();
					apires.setSuccess(true);
					apires.setResponse1("jsp: '"+view.getInstanceviewuuid()+".jsp' is created");

		            
				} catch (FileNotFoundException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} finally{
					if(outStream!=null)
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}else{
				apires = new ApiResponse();
				apires.setSuccess(false);
				apires.setResponse1("folder: '"+filePathToOrgModule+"' can't be created!");
			}
			
			
			
		}
	
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public List<InstanceView> findOrgInstanceViews(Long orgId) {
		if(orgId!=null){
			return siteDesignDao.findOrgInstanceViews(orgId);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleDetail getOrgProductModule(Long orgId) {
		return siteDesignDao.getOrgProductModule(orgId);
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByContainerModuleScheduleUuid(String containerModuleScheduleUuid) {
		return siteDesignDao.findModuleInstanceSchedulesByContainerModuleScheduleUuid(containerModuleScheduleUuid);
	}

	@Override
	@Transactional
	public Long saveModuleInstanceSchedule(ModuleInstanceSchedule sche) {
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ContainerDetail containerdetail = siteDesignDao.getContainerDetailByUuid(sche.getContaineruuid());
		
		if(loginaccount!=null && sche!=null && containerdetail!=null){
			// permission check
			boolean editPermissinAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerdetail.getPageuuid());
			if(editPermissinAllowed){
				return siteDesignDao.saveModuleInstanceSchedule(sche);
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleInstanceSchedule getModuleInstanceScheduleByUuid(String uuid) {
		return siteDesignDao.getModuleInstanceScheduleByUuid(uuid);
	}

	@Override
	@Transactional
	public void delModuleInstanceScheduleByUuid(String uuid) {
		AccountDto loginaccount = accountService.getCurrentAccount();
		ModuleInstanceSchedule moduleInstanceSched = siteDesignDao.getModuleInstanceScheduleByUuid(uuid);
		if(moduleInstanceSched!=null){
			ContainerDetail containerdetail = siteDesignDao.getContainerDetailByUuid(moduleInstanceSched.getContaineruuid());
			if(loginaccount!=null && containerdetail!=null){
				
				// permission check
				boolean editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerdetail.getPageuuid());
				
				if(editPermissionAllowed){
					siteDesignDao.delModuleInstanceScheduleByUuid(uuid);
				}
				
			}
			
		}
		
	}

	@Override
	@Transactional
	public Long saveInstanceViewSchedule(InstanceViewSchedule ivSchedule) {
		
		return siteDesignDao.saveInstanceViewSchedule(ivSchedule);
	}

	@Override
	@Transactional(readOnly=true)
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceUuid(String instanceUuid) {
		return siteDesignDao.findInstanceViewSchedulesByInstanceUuid(instanceUuid);
	}

	@Override
	@Transactional(readOnly=true)
	public List<InstanceViewSchedule> findInstanceViewSchedulesByInstanceViewUuid(String viewUuid) {
		return siteDesignDao.findInstanceViewSchedulesByInstanceViewUuid(viewUuid);
	}

	@Override
	@Transactional(readOnly=true)
	public InstanceViewSchedule getInstanceViewScheduleByUuid(String uuid) {
		return siteDesignDao.getInstanceViewScheduleByUuid(uuid);
	}

	@Override
	@Transactional
	public ApiResponse delInstanceViewScheduleByUuid(String uuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		InstanceViewSchedule instanceviewsched = siteDesignDao.getInstanceViewScheduleByUuid(uuid);
		
		if(loginaccount!=null && instanceviewsched!=null){
			InstanceView instanceView = siteDesignDao.getInstanceViewByUuid(instanceviewsched.getInstanceviewuuid());
			
			if(instanceView!=null){
				
				boolean delPermissionAllowed = false;
				
				if(InstanceView.Type.NormalInstanceView.getCode().equals(instanceView.getViewtype())){
					delPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleuuid());
				}else if(InstanceView.Type.ProductInstanceView.getCode().equals(instanceView.getViewtype())){
					delPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleinstanceuuid());
				}
				
				if(delPermissionAllowed){
					siteDesignDao.delInstanceViewScheduleByUuid(uuid);
					
					apires.setSuccess(true);
				}else{
					apires.setResponse1("you don't have permission to delete");
				}
				
			}else{
				apires.setResponse1("System can't find instanceView using schedule "+uuid);
			}
			
		}else{
			apires.setResponse1("System can't find instanceViewSchedule by "+uuid+" or your session could be expired!");
		}
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public PageMeta getPageMetaByPageUuid(String pageUuid) {
		return siteDesignDao.getPageMetaByPageUuid(pageUuid);
	}

	@Override
	@Transactional
	public ApiResponse pageDetailGenerator(String pageUuid, ContainerTreeNode containerTreeNode) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(currentAccount!=null && containerTreeNode!=null){
			Date now = new Date();
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new ContainerToPageDetailConvertor());
			stream.processAnnotations(new Class[]{ContainerTreeNode.class});
			
			PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(pageUuid);
			if(pageDetail!=null){
				pageDetail.setDetail(stream.toXML(containerTreeNode));
				
				Long pageDetailId = siteDesignDao.savePageDetail(pageDetail);
				
				if(pageDetailId!=null){
					
					// clean page change history in pageMeta
					PageMeta pageMeta = siteDesignDao.getPageMetaByPageUuid(pageUuid);
					if(pageMeta!=null){
						pageMeta.setChangelist(null);
						siteDesignDao.savePageMeta(pageMeta);
					}
					
					apires.setSuccess(true);
					apires.setResponse1(pageDetail.getPageuuid());
					
					
					// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) create a tree node(s) by topic route 4) post to accounts newsfeed
					Organization org = orgDao.getOrganizationById(pageDetail.getOrganization_id());
					Accountprofile currentAccountProfile = accountDao.getAccountProfileByAccountId(currentAccount.getId());
					// 1) log the activity:
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					String key_pageuuid = "pageUuid";
					ActivityLogData activityLogData = new ActivityLogData();
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put(key_oid, org.getId());
						dataMap.put(key_oname, org.getOrgname());
						dataMap.put(key_cid, currentAccount.getId());
						// for create name
						StringBuilder creatorname = new StringBuilder();
						if(currentAccountProfile!=null){
							creatorname.append(currentAccountProfile.getFirstname()).append(" ").append(currentAccountProfile.getLastname());
						}else{
							creatorname.append(currentAccount.getLoginname());
						}
						dataMap.put(key_cname, creatorname.toString());
						dataMap.put(key_pageuuid, pageDetail.getPageuuid());
						activityLogData.setDataMap(dataMap);
						String desc = messageFromPropertiesService.getMessageSource().getMessage("pagePublish", new Object[] { creatorname.toString(), PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase(), pageDetail.getPrettyname(), org.getOrgname() }, Locale.US);
						activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(currentAccount.getId(), org.getId(), ActivityType.pagePublish, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							"Page Publish",
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".publish").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"Page publish",
							now,
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
					
				}
			}
			
		}
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public PageDetail getPageDetailByUuid(String pageUuid) {
		return siteDesignDao.getPageDetailByUuid(pageUuid);
	}

	@Override
	@Transactional
	public void setPageCss(String pageUuid, String css) {
		PageMeta pageMeta = siteDesignDao.getPageMetaByPageUuid(pageUuid);
		if(pageMeta!=null){
			pageMeta.setCss(css);
		}
	}

	@Override
	@Transactional
	public void setPageUrl(String pageUuid, String url) {
		PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(pageUuid);
		if(pageDetail!=null){
			pageDetail.setUrl(url);
		}
	}

	@Override
	@Transactional
	public ApiResponse writePageCssToFile(Long pageId) {

		ApiResponse apires = null;
		
		StringBuilder filePathToOrgPageCss = new StringBuilder(servletContext.getRealPath("/css/org"));
//		InstanceView view = siteDesignDao.getInstanceViewById(viewId);
		PageDetail pagedetail = siteDesignDao.getPageDetailById(pageId);
		PageMeta pageMeta = null;
		if(pagedetail!=null){
			pageMeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());
		}
		if(pageMeta!=null && StringUtils.isNotBlank(pageMeta.getCss())){
			if(filePathToOrgPageCss.indexOf("/")>-1){
				filePathToOrgPageCss.append("/").append(pagedetail.getOrganization_id());
			}else{
				filePathToOrgPageCss.append("\\").append(pagedetail.getOrganization_id());
			}
			
			
			File folder = new File(filePathToOrgPageCss.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(filePathToOrgPageCss).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			// create or replace jsp
			if(folderExist){
				
				StringBuilder fileFullPath = new StringBuilder(filePathToOrgPageCss);
				if(fileFullPath.indexOf("/")>-1){
					fileFullPath.append("/").append(pagedetail.getPageuuid()).append(".css");
				}else{
					fileFullPath.append("\\").append(pagedetail.getPageuuid()).append(".css");
				}
				
    			File destinationFile = new File(fileFullPath.toString());

    			FileOutputStream outStream = null;
				try {
					outStream = new FileOutputStream(destinationFile, false);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			        final BufferedWriter fbw = new BufferedWriter(writer);
			        
			        String outputString = pageMeta.getCss();
			        outputString = StringEscapeUtils.unescapeHtml(outputString);
			        if(outputString!=null){
			        	outputString = StringUtils.replaceEach(outputString, new String[]{"<%", "%>"}, new String[]{"&lt;%--", "--%&gt;"});
			        }else{
			        	outputString = "";
			        }
			        
			        
		            fbw.write(outputString);
		            fbw.newLine();
		            fbw.flush();
		            fbw.close();
		            writer.close();
		            
					apires = new ApiResponse();
					apires.setSuccess(true);
					apires.setResponse1("css: '"+pagedetail.getPageuuid()+".css' is created");

		            
				} catch (FileNotFoundException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					apires = new ApiResponse();
					apires.setSuccess(false);
					apires.setResponse1(e.toString());
					logger.error(e.toString());
					e.printStackTrace();
				} finally{
					if(outStream!=null)
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}else{
				apires = new ApiResponse();
				apires.setSuccess(false);
				apires.setResponse1("folder: '"+filePathToOrgPageCss+"' can't be created!");
			}
		}
	
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public PageDetail getOrgPageByTypeAndUrl(Long orgId, Type pageType, String url) {
		return siteDesignDao.getOrgPageByTypeAndUrl(orgId, pageType, url);
	}

	@Override
	public void containerTreeNodeMarginSpaceGenerator(ContainerTreeNode parentContainerNode, ContainerTreeNode previousContainerNode, ContainerTreeNode currentContainerNode) {
		if(currentContainerNode!=null){
			
			// calc the margin
			if(currentContainerNode.getDirection().equals(ContainerDetail.Direction.Left2Right.getCode())){
				if(previousContainerNode==null){
					if(currentContainerNode.getTopposition()!=null) currentContainerNode.setMarginTop(currentContainerNode.getTopposition());
				}else{
					if(currentContainerNode.getTopposition()>(previousContainerNode.getTopposition()+previousContainerNode.getHeight())){
						currentContainerNode.setMarginTop(currentContainerNode.getTopposition()-(previousContainerNode.getTopposition()+previousContainerNode.getHeight()));
					}
				}
			}else if(currentContainerNode.getDirection().equals(ContainerDetail.Direction.Top2Bottom.getCode())){
				if(previousContainerNode==null){
					if(currentContainerNode.getLeftposition()!=null) currentContainerNode.setMarginLeft(currentContainerNode.getLeftposition());
				}else{
					if(currentContainerNode.getLeftposition()>(previousContainerNode.getLeftposition()+previousContainerNode.getWidth())){
						currentContainerNode.setMarginLeft(currentContainerNode.getLeftposition()-(previousContainerNode.getLeftposition()+previousContainerNode.getWidth()));
					}
					
				}
				
				// calc the relative margin left
				if(parentContainerNode!=null && currentContainerNode.getMarginLeft()>0){
					currentContainerNode.setRelativeMarginLeft(
							Double.valueOf(
								new java.text.DecimalFormat("#.##").format(currentContainerNode.getMarginLeft().doubleValue()/parentContainerNode.getWidth().doubleValue()*100)
							)
					);
				}
			}
			
			if(currentContainerNode.getSubnodes()!=null && currentContainerNode.getSubnodes().size()>0){
				for(int i=0; i<currentContainerNode.getSubnodes().size(); i++){
					if(i==0){
						containerTreeNodeMarginSpaceGenerator(currentContainerNode, null, currentContainerNode.getSubnodes().get(i));
					}else if(i>0){
						containerTreeNodeMarginSpaceGenerator(currentContainerNode, currentContainerNode.getSubnodes().get(i-1), currentContainerNode.getSubnodes().get(i));
					}
				}
			}
			
		}
		
		
	}

	@Override
	public void containerTreeNodeRelativeWidthHeightGenerator(ContainerTreeNode parentContainerNode, ContainerTreeNode currentContainerNode) {
		if(currentContainerNode!=null){
			if(parentContainerNode!=null){
				currentContainerNode.setRelativeWidth(
					Double.valueOf(
						new java.text.DecimalFormat("#.##").format(currentContainerNode.getWidth().doubleValue()/parentContainerNode.getWidth().doubleValue()*100)
					)
				);
				
				currentContainerNode.setRelativeHeight(
					Double.valueOf(
						new java.text.DecimalFormat("#.##").format(currentContainerNode.getHeight().doubleValue()/parentContainerNode.getHeight().doubleValue()*100)
					)
				);
			}else{
				currentContainerNode.setRelativeWidth(100.00d);
			}
			
			if(currentContainerNode.getSubnodes()!=null && currentContainerNode.getSubnodes().size()>0){
				for(ContainerTreeNode node : currentContainerNode.getSubnodes()){
					containerTreeNodeRelativeWidthHeightGenerator(currentContainerNode, node);
				}
			}
		}
	}

	// hold leaf container's content information
	// Map<ContainerUuid, 
	//						Map<
	//							type : ModuleDetail.Type.productModule || ModuleDetail.Type.module;
	//							modulecss : modulecss's location
	//							moduleuuid : uuid
	//							instanceuuid : uuid
	//							viewuuid : uuid
	//							viewcss : css location
	//							>
	@Override
	public Map<String, Map<String, String>> generateContentInfoForContainerTreeLeaf(ContainerTreeNode containerRoot, String entityUuid) {
		if(containerRoot!=null){
			List<ContainerTreeNode> leafs = new ArrayList<ContainerTreeNode>();
			findLeafsFromContainerTreeRoot(containerRoot, leafs);
			if(leafs!=null && leafs.size()>0){
				Map<String, Map<String, String>> containerContentInfoMap = new HashMap<String, Map<String, String>>();
				for(ContainerTreeNode node : leafs){
					Map<String, String> content = getContentInfoForContainer(node.getSystemName(), entityUuid);
					if(content!=null){
						containerContentInfoMap.put(node.getSystemName(), content);
					}
				}
				if(containerContentInfoMap.size()>0) return containerContentInfoMap;
			}
		}
		return null;
	}

	@Override
	public void findLeafsFromContainerTreeRoot(ContainerTreeNode root, List<ContainerTreeNode> leafs) {
		if(root!=null && leafs!=null){
			if(root.getSubnodes()==null || root.getSubnodes().size()==0){
				leafs.add(root);
			}else if(root.getSubnodes()!=null && root.getSubnodes().size()>0){
				for(ContainerTreeNode node: root.getSubnodes()){
					findLeafsFromContainerTreeRoot(node, leafs);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, String> getContentInfoForContainer(String containerUuid, String entityUuid) {
		
		ContainerDetail containerDetail = siteDesignDao.getContainerDetailByUuid(containerUuid);
		
		if(containerDetail!=null){
			
			Map<String, String> contentForContainer = new HashMap<String, String>();
			
			Date now = new Date();
			List<ContainerModuleSchedule> containerModuleSchedules = siteDesignDao.findContainerModuleSchedulesByContainerUuid(containerUuid);
			ContainerModuleSchedule currentContainerModuleSchedule = null;
			if(containerModuleSchedules!=null && containerModuleSchedules.size()>0){
				currentContainerModuleSchedule = (ContainerModuleSchedule)getCurrentSchedule(containerModuleSchedules, now);
			}
			
			ModuleDetail moduleDetail = null;
			if(currentContainerModuleSchedule!=null){ // get moduledetail from containerModuleSchedule
				moduleDetail = siteDesignDao.getModuleDetailByUuid(currentContainerModuleSchedule.getModuleuuid());
			}else { // get container's default moduleDetail
				moduleDetail = siteDesignDao.getModuleDetailByUuid(containerDetail.getModuleuuid());
			}
			
			if(moduleDetail!=null){
				
				if(moduleDetail.getType().equals(ModuleDetail.Type.productModule.getCode())){ // handle product module
					contentForContainer.put("type", ModuleDetail.Type.productModule.name());
					if(StringUtils.isNotBlank(moduleDetail.getCss())){
						contentForContainer.put("modulecss", "/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid()+".css");
					}
					contentForContainer.put("moduleuuid", moduleDetail.getModuleuuid());
					InstanceView currentViewForEntity = getCurrentInstanceView(entityUuid);
					
					// get category view if currentInstanceView is null
					EntityDetail entityDetail = entityDao.getEntityDetailByUuid(entityUuid);
					if(currentViewForEntity==null && entityDetail!=null && StringUtils.isNotBlank(entityDetail.getModuleuuid())){
						currentViewForEntity = getInstanceViewFromCategory(entityUuid, entityDetail.getModuleuuid());
					}
					
					
					if(currentViewForEntity!=null){
						contentForContainer.put("viewuuid", currentViewForEntity.getInstanceviewuuid());
						
						if(StringUtils.isNotBlank(currentViewForEntity.getCss())){
							contentForContainer.put("viewcss", "/org/"+currentViewForEntity.getOrgid()+"/"+currentViewForEntity.getInstanceviewuuid()+".css");
						}
					}
					
					// the module instance (product detail) will not be get here! it will be get from PageRetriever.getContainerModuleContent
					
					
				}else{ // handle everything except product module
					
					contentForContainer.put("type", ModuleDetail.Type.module.name());
					
					
					// find module instance based on module uuid and moduleinstanceSchedule
					ModuleInstance currentModuleInstance = null;
					List<ModuleInstanceSchedule> moduleInstanceSchedules = null;
					if(currentContainerModuleSchedule!=null){
						moduleInstanceSchedules = siteDesignDao.findModuleInstanceSchedulesByContainerModuleScheduleUuid(currentContainerModuleSchedule.getUuid());
					}
					if(moduleInstanceSchedules!=null && moduleInstanceSchedules.size()>0){
						ModuleInstanceSchedule currentModuleInstanceSchedule = (ModuleInstanceSchedule)getCurrentSchedule(moduleInstanceSchedules, now);
						if(currentModuleInstanceSchedule!=null){
							currentModuleInstance = siteDesignDao.getModuleInstanceByUuid(currentModuleInstanceSchedule.getModuleinstanceuuid());
						}
					}
					
					// find view based on moduleInstance
					InstanceView currentInstanceView = null;
					
					if(currentModuleInstance!=null){
						currentInstanceView = getCurrentInstanceView(currentModuleInstance.getModuleinstanceuuid());
					}
					
					// find jsp css, ...
					if(currentModuleInstance!=null){ // has module instance, get module css, module jsp, instance and view information.
						if(StringUtils.isNotBlank(moduleDetail.getCss())){
							contentForContainer.put("modulecss", "/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid()+".css");
						}
						contentForContainer.put("moduleuuid", currentModuleInstance.getModuleuuid());
						contentForContainer.put("instanceuuid", currentModuleInstance.getModuleinstanceuuid());
						if(currentInstanceView!=null){
							contentForContainer.put("viewuuid", currentInstanceView.getInstanceviewuuid());
							if(StringUtils.isNotBlank(currentInstanceView.getCss())){
								contentForContainer.put("viewcss", "/org/"+currentInstanceView.getOrgid()+"/"+currentInstanceView.getInstanceviewuuid()+".css");
							}
						}
					}else{ // has no module instance, then get module css and jsp only
						
						// find module jsp, css
						if(StringUtils.isNotBlank(moduleDetail.getCss())){
							contentForContainer.put("modulecss", "/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid()+".css");
						}
						contentForContainer.put("moduleuuid", moduleDetail.getModuleuuid());
					}
				}
				
			}
			
			if(contentForContainer.size()>0){
				return contentForContainer;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public InstanceView getCurrentInstanceView(String instanceUuid){
		if(StringUtils.isNotBlank(instanceUuid)){
			
			// find views based on module id
			InstanceView currentInstanceView = null;
			List<InstanceViewSchedule> instanceViewSchedules = siteDesignDao.findInstanceViewSchedulesByInstanceUuid(instanceUuid);
			
			if(instanceViewSchedules!=null && instanceViewSchedules.size()>0){
				InstanceViewSchedule currentInstanceViewSchedule = (InstanceViewSchedule)getCurrentSchedule(instanceViewSchedules, new Date());
				if(currentInstanceViewSchedule!=null){
					currentInstanceView = siteDesignDao.getInstanceViewByUuid(currentInstanceViewSchedule.getInstanceviewuuid());
				}
			}
			
			// if no instance view find in instanceViewSchedule, use the default view for the moduleInstance
			if(currentInstanceView==null){
				List<InstanceView> instanceViews = siteDesignDao.findInstanceViewsByInstanceUuid(instanceUuid);
				if(instanceViews!=null && instanceViews.size()>0){
					for(InstanceView v : instanceViews){
						if(v.getIsdefault().equals(InstanceView.IsDefault.yes.getCode())){
							currentInstanceView = v;
							break;
						}
					}
				}
			}
			
			return currentInstanceView;
			
		}
		return null;
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public InstanceView getInstanceViewFromCategory(String instanceUuid, String moduleDetailUuid) {
		
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(instanceUuid);
		if(entityDetail!=null && StringUtils.isNotBlank(moduleDetailUuid)){
			EntityDetail categoryDetail = entityDao.getEntityDetailByUuid(entityDetail.getParentuuid());
			
			if(categoryDetail!=null){
				// check if category is using moduleDetailUuid
				if(StringUtils.equals(categoryDetail.getModuleuuid(), moduleDetailUuid)){
					
					InstanceView cateView = getCurrentInstanceView(categoryDetail.getEntityuuid());
					if(cateView!=null){
						return cateView;
					}else{ // upper level
						return getInstanceViewFromCategory(categoryDetail.getEntityuuid(), moduleDetailUuid);
					}
					
					
				}else{ // upper level
					return getInstanceViewFromCategory(categoryDetail.getEntityuuid(), moduleDetailUuid);
				}
			}
		}
		
		return null;
	}
	
	
	@Override
	public <T> ScheduleInterface getCurrentSchedule(List<T> schedules, Date now) {
		if(schedules!=null && schedules.size()>0){
			if(now==null) now = new Date();
			
			if(ScheduleInterface.class.isAssignableFrom(schedules.get(0).getClass())){ // make sure the list is the ScheduleInterface list
				ScheduleInterface currentSchedule = null;
				for(T sche : schedules){
					ScheduleInterface schedule = (ScheduleInterface)sche;
					
					/**** find schedule for date now ****/
					// if startdate is null and enddate is null too, this means the schedule doesn't setup proper yet!!!
					// if startdate is null but enddate isn't null, this means "from now to enddate".
					// if startdate isn't null but enddate is null, this means "from startdate to foreever"
					if(schedule.getStartdate()!=null || schedule.getEnddate()!=null){
						ScheduleInterface tempSchedule = null;
						if(schedule.getStartdate()==null){
							if(schedule.getEnddate().after(now) || schedule.getEnddate().equals(now)){
								tempSchedule = schedule;
							}
						}else if(schedule.getEnddate()==null){
							if(schedule.getStartdate().before(now) || schedule.getStartdate().equals(now)){
								tempSchedule = schedule;
							}
						}else if(schedule.getStartdate()!=null && schedule.getEnddate()!=null){
							if((schedule.getStartdate().before(now) || schedule.getStartdate().equals(now)) 
									&& (schedule.getEnddate().after(now) || schedule.getEnddate().equals(now))){
								tempSchedule = schedule;
							}
						}
						
						if(tempSchedule!=null){
							if(currentSchedule==null){
								currentSchedule = tempSchedule;
							}else{
								if(currentSchedule.getPriority().intValue()<tempSchedule.getPriority().intValue()){
									currentSchedule = tempSchedule;
								}
							}
						}
					}
				}
				
				return currentSchedule;
			}
		}
		
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public <T> List<ScheduleInterface> getOutDatedSchedule(List<T> schedules, Date now){
		
		if(schedules!=null && schedules.size()>0){
			if(ScheduleInterface.class.isAssignableFrom(schedules.get(0).getClass())){
				if(now==null) now = new Date();
				
				List<ScheduleInterface> outdatedScheds = new ArrayList<ScheduleInterface>();
				for(T s : schedules){
					ScheduleInterface sched = (ScheduleInterface)s;
					
					if(sched.getEnddate()!=null && sched.getEnddate().compareTo(now)<0){
						outdatedScheds.add(sched);
					}
				}
				if(outdatedScheds.size()>0) return outdatedScheds;
				
			}

		}
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse newAttrGroupSet(String moduleUuid) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
		if(moduleDetail!=null){
			if(StringUtils.isNotBlank(moduleDetail.getXml())){
				 Module aModule = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				 if(aModule!=null){
						String aGroupUuid = UUID.randomUUID().toString();
						String aGroupRandomName = RandomStringUtils.randomAlphanumeric(8);
						// check the group name exist or not
						boolean groupNameExist = false;
						do {
							if(groupNameExist){
								aGroupRandomName = RandomStringUtils.randomAlphanumeric(8);
							}
							
							if(aModule.getAttrGroupList()!=null && aModule.getAttrGroupList().size()>0){
								for(AttrGroup g : aModule.getAttrGroupList()){
									if(aGroupRandomName.equals(g.getGroupName())){
										groupNameExist = true;
										break;
									}
								}
							}
						} while (groupNameExist);
						AttrGroup aGroup = new AttrGroup();
						aGroup.setArray(Boolean.FALSE);
						aGroup.setGroupName(aGroupRandomName);
						aGroup.setGroupUuid(aGroupUuid);
						aModule.addAttrGroup(aGroup);
						
						String groupXml = SitedesignHelper.getXmlFromModule(aModule);
						
						// char's usage check:
//						int moduleDetailUsageWith100Multipled = paymentService.countModuleDetailUsage(moduleDetail);
						int moduleDetailUsageWith100Multipled = 0;
						PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(moduleDetail.getOrganization_id(),new Date());
						if(plan!=null){
							int usedXmlLength = groupXml.length();
							moduleDetailUsageWith100Multipled = paymentService.countCharsUsage(usedXmlLength, plan.getMaxcharspermoduledetail());
						}
						
						if(moduleDetailUsageWith100Multipled>100){
							apires.setResponse1("You reach the maximum storage usage for ModuleDetail with the new value.");
						}else{
							moduleDetail.setXml(groupXml);
							Long id = siteDesignDao.saveModuleDetail(moduleDetail);
							if(id!=null){
								apires.setSuccess(true);
								apires.setResponse1(aGroupUuid);
								apires.setResponse3(moduleDetailUsageWith100Multipled);
							}else{
								apires.setResponse1("System can't save the ModuleDetail in creating a new AttrGroup");
							}
							
						}
					 
				 }
			}else{
				// create a new module and it's groupset
				String aGroupUuid = UUID.randomUUID().toString();
				String aGroupRandomName = RandomStringUtils.randomAlphanumeric(8);
				Module aModule = new Module();
				AttrGroup aGroup = new AttrGroup();
				aGroup.setArray(Boolean.FALSE);
				aGroup.setGroupName(aGroupRandomName);
				aGroup.setGroupUuid(aGroupUuid);
				aModule.addAttrGroup(aGroup);
				
				String groupXml = SitedesignHelper.getXmlFromModule(aModule);
				
				moduleDetail.setXml(groupXml);
				Long id = siteDesignDao.saveModuleDetail(moduleDetail);
				
				int charUsageWith100Multipled = paymentService.countModuleDetailUsage(moduleDetail);
				
				if(id!=null){
					apires.setSuccess(true);
					apires.setResponse1(aGroupUuid);
					apires.setResponse3(charUsageWith100Multipled);
				}else{
					apires.setResponse1("System can't save the ModuleDetail in creating a new AttrGroup");
				}
			}
		}else{
			apires.setResponse1("System can't find the moduleDetail by "+moduleUuid);
		}
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public AttrGroup getModuleAttrGroupByUuid(String moduleuuid, String groupuuid) {
		if(StringUtils.isNotBlank(moduleuuid) && StringUtils.isNotBlank(groupuuid)){
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleuuid);
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					for(AttrGroup g : module.getAttrGroupList()){
						if(g.getGroupUuid().equals(groupuuid)){
							return g;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse delAttrGroupSet(String moduledetailUuid, String attrGroupUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(moduledetailUuid) && StringUtils.isNotBlank(attrGroupUuid)){
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduledetailUuid);
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
//					AttrGroup delGroup = null;
					int delGroupIdx = -1;
					for(int gidx=0; gidx<module.getAttrGroupList().size(); gidx++){
						if(module.getAttrGroupList().get(gidx).getGroupUuid().equals(attrGroupUuid)){
							delGroupIdx = gidx;
							break;
						}
					}
					if(delGroupIdx>=0){
						// find groupUuid with it's all attrs' uuids
						List<String> deleteableGroupWithAttrUuids = new ArrayList<String>();
						deleteableGroupWithAttrUuids.add(attrGroupUuid);
						AttrGroup deleteableGroup = module.getAttrGroupList().get(delGroupIdx);
						if(deleteableGroup.getAttrList()!=null){
							for(ModuleAttribute deleteableAttr : deleteableGroup.getAttrList()){
								deleteableGroupWithAttrUuids.add(deleteableAttr.getUuid());
							}
						}
						// remove modulemeta
						if(deleteableGroupWithAttrUuids.size()>0){
							for(String muid : deleteableGroupWithAttrUuids){
								ModuleMeta modulemeta = siteDesignDao.getModuleMetaByTargetUuid(muid);
								if(modulemeta!=null) {
									siteDesignDao.delModuleMetaById(modulemeta.getId());
								}
							}
						}
						
						module.getAttrGroupList().remove(delGroupIdx);
						String moduleXml = SitedesignHelper.getXmlFromModule(module);
						moduleDetail.setXml(moduleXml);
						siteDesignDao.saveModuleDetail(moduleDetail);
						
						// find chars usage:
						int moduleCharsUsageWith100Multipled = paymentService.countModuleDetailUsage(moduleDetail);
						apires.setResponse3(moduleCharsUsageWith100Multipled);
						
						apires.setResponse1(attrGroupUuid);
						apires.setSuccess(true);
						
						
					}else{
						apires.setResponse1("System can't find group that need to be deleted.");
					}
				}else{
					apires.setResponse1("moduleDetail doesn't have group set");
				}
			}else{
				apires.setResponse1("System can't find moduledetail by "+moduledetailUuid+" or moduledetail don't have xml predefined!");
			}
		}else{
			apires.setResponse1("System don't have enough information to delAttrGroupSet");
		}
		
		
		return apires;
		
	}

	@Override
	@Transactional
	public ApiResponse delInstanceAttrGroupSet(String instanceUuid, String attrGroupUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		Long orgId = null;
		
		ModuleInstanceInterface moduleInstance = siteDesignDao.getModuleInstanceByUuid(instanceUuid);
		if(moduleInstance==null){
			moduleInstance = entityDao.getEntityDetailByUuid(instanceUuid);
		}
		
		if(moduleInstance!=null && StringUtils.isNotBlank(attrGroupUuid)){
			Module instance = null;
			
			if(moduleInstance.getClass().equals(ModuleInstance.class)){
				instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
				orgId = ((ModuleInstance)moduleInstance).getOrgid();
			}else if(moduleInstance.getClass().equals(EntityDetail.class)){
				instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
				orgId = ((EntityDetail)moduleInstance).getOrganization_id();
			}
			
			
			if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
//				AttrGroup delGroup = null;
				int delGroupIdx = -1;
				for(int gidx=0; gidx<instance.getAttrGroupList().size(); gidx++){
					if(instance.getAttrGroupList().get(gidx).getGroupUuid().equals(attrGroupUuid)){
						delGroupIdx = gidx;
						break;
					}
				}
				if(delGroupIdx>-1){
					
					// find groupUuid with it's all attrs' uuids
					List<String> deleteableGroupWithAttrUuids = new ArrayList<String>();
					deleteableGroupWithAttrUuids.add(attrGroupUuid);
					AttrGroup deleteableGroup = instance.getAttrGroupList().get(delGroupIdx);
					if(deleteableGroup.getAttrList()!=null){
						for(ModuleAttribute deleteableAttr : deleteableGroup.getAttrList()){
							deleteableGroupWithAttrUuids.add(deleteableAttr.getUuid());
						}
					}
					// remove modulemeta
					if(deleteableGroupWithAttrUuids.size()>0){
						for(String muid : deleteableGroupWithAttrUuids){
							ModuleMeta modulemeta = siteDesignDao.getModuleMetaByTargetUuid(muid);
							if(modulemeta!=null) {
								siteDesignDao.delModuleMetaById(modulemeta.getId());
							}
						}
					}
					
					
					instance.getAttrGroupList().remove(delGroupIdx);
					String updatedXml = SitedesignHelper.getXmlFromModule(instance);
					
					// get instanceUsage info
					int instanceCharUsage = 0;
					PaymentPlan paymentplan = paymentService.getPaymentPlanAtPointOfDate(orgId, new Date());
					if(paymentplan!=null){
						int updatedxmllength = updatedXml!=null?updatedXml.length():0;
						instanceCharUsage = paymentService.countCharsUsage(updatedxmllength, paymentplan.getMaxcharsperinstance());
					}
					
					Long id = null;
					if(moduleInstance.getClass().equals(ModuleInstance.class)){
						((ModuleInstance)moduleInstance).setInstance(updatedXml);
						id = siteDesignDao.saveModuleInstance((ModuleInstance)moduleInstance);
					}else if(moduleInstance.getClass().equals(EntityDetail.class)){
						((EntityDetail)moduleInstance).setDetail(updatedXml);
						id = entityDao.saveEntityDetail((EntityDetail)moduleInstance);
					}
					
					if(id!=null){
						apires.setSuccess(true);
						apires.setResponse3(instanceCharUsage);
					}else{
						apires.setResponse1("System can't update module instance: \n"+updatedXml);
					}
				}
			}else{
				apires.setResponse1("System can't find target based on instanceUuid="+instanceUuid+" and attrGroupUuid="+attrGroupUuid);
			}
		}else{
			apires.setResponse1("System can't find target based on instanceUuid="+instanceUuid+" and attrGroupUuid="+attrGroupUuid);
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public ApiResponse delInstanceAttr(String instanceUuid, String groupUuid, String attrUuid){
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ModuleInstanceInterface moduleInstance = siteDesignDao.getModuleInstanceByUuid(instanceUuid);
		Long orgId = null;
		if(moduleInstance==null){
			moduleInstance = entityDao.getEntityDetailByUuid(instanceUuid);
		}
		
		if(moduleInstance!=null && StringUtils.isNotBlank(groupUuid) && StringUtils.isNotBlank(attrUuid)){
			
			Module instance = null;
			
			if(moduleInstance.getClass().equals(ModuleInstance.class)){
				instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
				orgId = ((ModuleInstance)moduleInstance).getOrgid();
			}else if(moduleInstance.getClass().equals(EntityDetail.class)){
				instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
				orgId = ((EntityDetail)moduleInstance).getOrganization_id();
			}
			
			
			if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
				for(AttrGroup g : instance.getAttrGroupList()){
					if(g.getGroupUuid().equals(groupUuid)){
						if(g.getAttrList()!=null && g.getAttrList().size()>0){
//							ModuleAttribute delAttr = null;
							int delAttrIdx = -1;
							for(int aidx = 0; aidx<g.getAttrList().size(); aidx++){
								if(g.getAttrList().get(aidx).getUuid().equals(attrUuid)){
									delAttrIdx = aidx;
									
									break;
								}
							}
							if(delAttrIdx>=0){
								
								// find modulemeta to delete
								ModuleMeta moduleMeta = siteDesignDao.getModuleMetaByTargetUuid(attrUuid);
								if(moduleMeta!=null){
									siteDesignDao.delModuleMetaById(moduleMeta.getId());;
								}
								
								g.getAttrList().remove(delAttrIdx);
								String updatedxml = SitedesignHelper.getXmlFromModule(instance);
								
								int instanceUsedCharSizeWith100Mutipled = 0;
								PaymentPlan paymentplan = paymentService.getPaymentPlanAtPointOfDate(orgId, new Date());
								if(paymentplan!=null){
									int usedxmllength = updatedxml.length();
									instanceUsedCharSizeWith100Mutipled = paymentService.countCharsUsage(usedxmllength, paymentplan.getMaxcharsperinstance());
								}

								
								Long id = null;
								
								if(moduleInstance.getClass().equals(ModuleInstance.class)){
									((ModuleInstance)moduleInstance).setInstance(updatedxml);
									id = siteDesignDao.saveModuleInstance((ModuleInstance)moduleInstance);
									
								}else if(moduleInstance.getClass().equals(EntityDetail.class)){
									((EntityDetail)moduleInstance).setDetail(updatedxml);
									id = entityDao.saveEntityDetail((EntityDetail)moduleInstance);
								}
								
								if(id!=null){
									apires.setSuccess(true);
									apires.setResponse1(attrUuid);
									apires.setResponse3(instanceUsedCharSizeWith100Mutipled);

								}else{
									apires.setResponse1("System can't update module instance: \n"+updatedxml);
								}
							}
						}else{
							apires.setResponse1("Module instance's group ("+groupUuid+") don't have attributes defined!");
						}
						break;
					}
				}
			}else{
				apires.setResponse1("System can't find module instance or instance has no groups based on instanceUuid="+instanceUuid);
			}
			
			
		}else{
			apires.setResponse1("System can't find target based on instanceUuid="+instanceUuid+" and groupUuid="+groupUuid+" and attrUuid="+attrUuid);
		}
		return apires;
		
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleAttribute getModuleAttrByUuid(String moduleUuid, String attrsGroupUuid, String moduleAttrUuid) {
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(attrsGroupUuid) && StringUtils.isNotBlank(moduleAttrUuid)){
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					for(AttrGroup g : module.getAttrGroupList()){
						if(g.getGroupUuid().equals(attrsGroupUuid)){
							if(g.getAttrList()!=null && g.getAttrList().size()>0){
								for(ModuleAttribute a : g.getAttrList()){
									if(a.getUuid().equals(moduleAttrUuid)){
										ModuleAttribute resultAttr = null;
										
										// ************* create a clone attr based on different type ************* //
										if(a.getClass().equals(ModuleTextAttribute.class)){
											resultAttr = mapper.map(a, ModuleTextAttribute.class);
										}else if(a.getClass().equals(ModuleNumberAttribute.class)){
											resultAttr = mapper.map(a, ModuleNumberAttribute.class);
										}else if(a.getClass().equals(ModuleImageAttribute.class)){
											resultAttr = mapper.map(a, ModuleImageAttribute.class);
										}else if(a.getClass().equals(ModuleLinkAttribute.class)){
											resultAttr = mapper.map(a, ModuleLinkAttribute.class);
										}else if(a.getClass().equals(ModuleMoneyAttribute.class)){
											resultAttr = mapper.map(a, ModuleMoneyAttribute.class);
										}else if(a.getClass().equals(ModuleProductListAttribute.class)){
											resultAttr = mapper.map(a, ModuleProductListAttribute.class);
										}else if(a.getClass().equals(ModuleEntityCategoryListAttribute.class)){
											resultAttr = mapper.map(a, ModuleEntityCategoryListAttribute.class);
										}
										return resultAttr;
									}
								}
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
	public ApiResponse newModuleAttr(String moduleUuid, String attrsGroupUuid, String attrClassName) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(attrsGroupUuid) && StringUtils.isNotBlank(attrClassName)){
			
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					for(AttrGroup g : module.getAttrGroupList()){
						if(g.getGroupUuid().equals(attrsGroupUuid)){
							// new attr
							try {
								Class attrClass = Class.forName(attrClassName.trim());
								if(attrClass!=null){
									Constructor attrConstructor = attrClass.getConstructor();
									if(attrConstructor!=null){
										String newAttrUuid = UUID.randomUUID().toString();
										String newAttrName = RandomStringUtils.randomAlphanumeric(8);
										// check att name duplicate
										if(g.getAttrList()!=null && g.getAttrList().size()>0){
											boolean attrNameExist = false;
											do {
												if(attrNameExist){
													newAttrName = RandomStringUtils.randomAlphanumeric(8);
												}
												for(ModuleAttribute a : g.getAttrList()){
													if(a.getName().equals(newAttrName)){
														attrNameExist = true;
														break;
													}
												}
											} while (attrNameExist);
										}
										
										ModuleAttribute newAttr = null;
										
										// ************** add all other attrs' new instance here ***********//
										if(attrClass.equals(ModuleTextAttribute.class)){
											newAttr = (ModuleTextAttribute)attrConstructor.newInstance();
											// set default value for ModuleTextAttribute
											((ModuleTextAttribute)newAttr).setTextArea(Boolean.FALSE);
											
										}else if(attrClass.equals(ModuleNumberAttribute.class)){
											newAttr = (ModuleNumberAttribute)attrConstructor.newInstance();
										}else if(attrClass.equals(ModuleImageAttribute.class)){
											newAttr = (ModuleImageAttribute)attrConstructor.newInstance();
										}else if(attrClass.equals(ModuleLinkAttribute.class)){
											newAttr = (ModuleLinkAttribute)attrConstructor.newInstance();
										}else if(attrClass.equals(ModuleMoneyAttribute.class)){
											newAttr = (ModuleMoneyAttribute)attrConstructor.newInstance();
										}else if(attrClass.equals(ModuleProductListAttribute.class)){
											newAttr = (ModuleProductListAttribute)attrConstructor.newInstance();
										}else if(attrClass.equals(ModuleEntityCategoryListAttribute.class)){
											newAttr = (ModuleEntityCategoryListAttribute)attrConstructor.newInstance();
										}
										
										newAttr.setUuid(newAttrUuid);
										newAttr.setName(newAttrName);
										
										// set default value
										newAttr.setArray(Boolean.FALSE);
										newAttr.setEditable(Boolean.TRUE);
										newAttr.setRequired(Boolean.FALSE);
										
										g.addAttr(newAttr);
										
										String updatedModuleXml = SitedesignHelper.getXmlFromModule(module);
										
										// check module's char usage
										int moduleDetailUsageWith100Multipled = 0;
										PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(moduleDetail.getOrganization_id(),new Date());
										if(plan!=null){
											int usedXmlLength = updatedModuleXml.length();
											moduleDetailUsageWith100Multipled = paymentService.countCharsUsage(usedXmlLength, plan.getMaxcharspermoduledetail());
										}
										
										if(moduleDetailUsageWith100Multipled>100){
											apires.setResponse1("You reach the maximum storage usage for ModuleDetail with the new value.");
										}else{
											moduleDetail.setXml(updatedModuleXml);
											Long moduledetailId = siteDesignDao.saveModuleDetail(moduleDetail);
											
											if(moduledetailId!=null){
												apires.setSuccess(true);
												apires.setResponse1(newAttrUuid);
												apires.setResponse3(moduleDetailUsageWith100Multipled);
											}else{
												apires.setResponse1("System can't save the ModuleDetail in creating a new attribute");
											}
										}
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								logger.error(e.toString());
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
								logger.error(e.toString());
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								logger.error(e.toString());
							} catch (InstantiationException e) {
								e.printStackTrace();
								logger.error(e.toString());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								logger.error(e.toString());
							} catch (InvocationTargetException e) {
								e.printStackTrace();
								logger.error(e.toString());
							}
							
							break;
						}
					}
				}else{
					apires.setResponse1("System can't find attrGroup in moduledetail");
				}
			}else{
				apires.setResponse1("System can't find moduleDetail or moduleDetail doesn't have attrGroup defined!");
			}
		}else{
			apires.setResponse1("System doesn't have enough information to process new attribute action.");
		}
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse delModuleAttr(String moduleUuid, String attrGroupUuid, String attrUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(attrGroupUuid) && StringUtils.isNotBlank(attrUuid)){
			ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					for(AttrGroup g : module.getAttrGroupList()){
						if(g.getGroupUuid().equals(attrGroupUuid)){
							if(g.getAttrList()!=null && g.getAttrList().size()>0){
//								ModuleAttribute delAttr = null;
								int delAttrIdx = -1;
								for(int aidx=0; aidx<g.getAttrList().size(); aidx++){
									if(g.getAttrList().get(aidx).getUuid().equals(attrUuid)){
										delAttrIdx = aidx;
										break;
									}
								}
								if(delAttrIdx>-1){
									g.getAttrList().remove(delAttrIdx);
									
									String updatedModuleXml = SitedesignHelper.getXmlFromModule(module);
									
									
									moduleDetail.setXml(updatedModuleXml);
									siteDesignDao.saveModuleDetail(moduleDetail);
									
									// find modulemeta to delete
									ModuleMeta moduleMeta = siteDesignDao.getModuleMetaByTargetUuid(attrUuid);
									if(moduleMeta!=null){
										siteDesignDao.delModuleMetaById(moduleMeta.getId());;
									}

									// find moduledetail's char usage:
									int moduleDetailUsageWith100Multipled = paymentService.countModuleDetailUsage(moduleDetail);
									apires.setSuccess(true);
									apires.setResponse3(moduleDetailUsageWith100Multipled);
								}
							}
							
							break;
						}
					}
				}else{
					apires.setResponse1("Moduledetail doesn't have attribute groups defined!");
				}
			}else{
				apires.setResponse1("System can't find moduleDetail by "+moduleUuid + ", or moduleDetail doesn't have xml information.");
			}
		}else {
			apires.setResponse1("System doesn't have enought information to process delete attribute.");
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateModuleAttrValue(String moduleUuid, String attrGroupUuid, String attrUuid, String updateValueName, String updateValue) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(attrGroupUuid) && StringUtils.isNotBlank(attrUuid) && StringUtils.isNotBlank(updateValueName)){
			ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			if(moduledetail!=null && StringUtils.isNotBlank(moduledetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
				
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					// check permission
					AccountDto loginAccount = accountService.getCurrentAccount();
					boolean isModifyPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleUuid);
					if(isModifyPermissionAllowed){
						for(AttrGroup g : module.getAttrGroupList()){
							if(g.getGroupUuid().equals(attrGroupUuid)){
								if(g.getAttrList()!=null && g.getAttrList().size()>0){
									for(ModuleAttribute a : g.getAttrList()){
										if(a.getUuid().equals(attrUuid)){
											
											Field field = null;
											try {
												
												List<Field> fields = new ArrayList<Field>();
												// find all current class fields
												for(Field f : a.getClass().getDeclaredFields()){
													fields.add(f);
												}
												// find all superclass fields
												for(Field f : a.getClass().getSuperclass().getDeclaredFields()){
													fields.add(f);
												}
												
												// find field in fields list with the name
												if(fields.size()>0){
													for(Field f : fields){
														if(f.getName().equals(updateValueName)){
															field = f;
															break;
														}else if(f.getName().equals("money") && (updateValueName.equals("moneyAmount") || updateValueName.equals("moneyCurrency"))){
															field = f;
															break;
														}
													}
												}
												
												if(field!=null){
													
													ApiResponse valRes = moduleFieldValidation(g, a, updateValueName, updateValue); 
													
													if(valRes.isSuccess()){
														
														field.setAccessible(true);
														
														Class fieldType = field.getType();
														if(fieldType.equals(String.class)){
															field.set(a, updateValue);
														}else if(fieldType.equals(Integer.class) || fieldType.equals(int.class)){
															if(NumberUtils.isNumber(updateValue)){
																BigDecimal val = new BigDecimal(updateValue);
																field.set(a, val.intValue());
																updateValue = Integer.toString(val.intValue());
															}else if(StringUtils.isBlank(updateValue)){
																field.set(a, null);
															}
														}else if(fieldType.equals(Boolean.class)){
															if(updateValue.equals("1")){
																field.set(a, Boolean.TRUE);
																updateValue = Boolean.TRUE.toString();
															}else if(updateValue.equals("0")){
																field.set(a, Boolean.FALSE);
																updateValue = Boolean.FALSE.toString();
															}
														}else if(fieldType.equals(BigDecimal.class)){
															if(NumberUtils.isNumber(updateValue)){
																field.set(a, new BigDecimal(updateValue));
															}else if(StringUtils.isBlank(updateValue)){
																field.set(a, null);
															}
														}else if(fieldType.equals(Money.class)){
															if(field.get(a)!=null){
																
																Money money = (Money)field.get(a);
																if(updateValueName.equals("moneyAmount")){
																	money.setAmount(new BigDecimal(updateValue));
																}else if(updateValueName.equals("moneyCurrency")){
																	try{
																		Currency currency = Currency.getInstance(updateValue);
																		money.setCurrency(currency);
																	}catch (Exception e) {
																		Currency currency = Currency.getInstance(Locale.US);
																		money.setCurrency(currency);
																		updateValue = currency.getCurrencyCode();
																	}
																}
																
															}else{
																if(updateValueName.equals("moneyAmount")){
																	Money money = new Money(new BigDecimal(updateValue));
																	field.set(a, money);
																}else if(updateValueName.equals("moneyCurrency")){
																	Currency aCurrency = null;
																	try{
																		aCurrency = Currency.getInstance(updateValue);
																	}catch (Exception e) {
																		aCurrency = Currency.getInstance(Locale.US);
																	}
																	Money money = new Money(new BigDecimal("0"), aCurrency);
																	field.set(a, money);
																}
																
															}
															
														}
														
														String updatedXml = SitedesignHelper.getXmlFromModule(module);
														
														PaymentPlan paymentPlan = paymentService.getPaymentPlanAtPointOfDate(moduledetail.getOrganization_id(), new Date());
														if(paymentPlan!=null){
															
															int updatedXmlLength = updatedXml.length();
															if(updatedXmlLength>paymentPlan.getMaxcharspermoduledetail()){
																apires.setResponse1("You reach the maximum storage usage for ModuleDetail with the new value.");
															}else{
																moduledetail.setXml(updatedXml);
																apires.setSuccess(true);
																apires.setResponse1(updateValue);
																apires.setResponse3(paymentService.countCharsUsage(updatedXmlLength, paymentPlan.getMaxcharspermoduledetail()));
															}
															
														}else{
															apires.setResponse1("System can't find payment plan for org"+moduledetail.getOrganization_id());
														}
													}else{
														apires = valRes;
													}
													
												}
												
											} catch (SecurityException e) {
												e.printStackTrace();
												logger.error(e.toString());
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
												logger.error(e.toString());
											} catch (IllegalAccessException e) {
												e.printStackTrace();
												logger.error(e.toString());
											}
											
											break;
										}
									}
								}
								
								break;
							}
						}
						
					}else{
						apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have permission to modify node: "+moduleUuid);
					}
				}
			}
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public ApiResponse updateModuleInstanceValue(String instanceUuid, String updateValueName, String updateValue){
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ModuleInstance moduleInstance = siteDesignDao.getModuleInstanceByUuid(instanceUuid);
		
		if(moduleInstance!=null && StringUtils.isNotBlank(updateValueName) && StringUtils.isNotBlank(updateValue)){
			
			// permission check
			boolean editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, moduleInstance.getModuleuuid());
			
			if(editPermissionAllowed){
				
				boolean updated = false;
				
				if(updateValueName.equals("name")){ // update name
					
					
					// name validation
					boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
					if(nameVali){
	    				moduleInstance.setName(updateValue.trim());
	    				updated = true;
	    				
	    				// update treelevelView
						ModuleTreeLevelView view = siteDesignDao.getModuleTreeLevelViewHasNode(moduleInstance.getModuleinstanceuuid());
						
						if(view!=null){
							boolean treeviewUpdated = false;
							List<ModuleTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, view.getNodes());
							if(nodes!=null && nodes.size()>0){
								for(ModuleTreeNode n : nodes){
									if(n.getSystemName().equals(moduleInstance.getModuleinstanceuuid())){
										n.setPrettyName(updateValue.trim());
										treeviewUpdated = true;
										break;
									}
								}
								if(treeviewUpdated){
									String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
									view.setNodes(updatedxml);
									siteDesignDao.saveModuleTreeLevelView(view);
								}
							}
						}
						
					}else{
						apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
					}
				}
				
				if(updated){
					Long id = siteDesignDao.saveModuleInstance(moduleInstance);
					if(id!=null){
						apires.setSuccess(true);
					}
				}
			}else{
				apires.setResponse1("you don't have permission to update");
			}
			
		}
		
		return apires;
	}
	
	
	
	@Override
	@Transactional
	public ApiResponse updateModuleInstanceAttrValue(String instanceUuid, String attrGroupUuid, String attrUuid, String updateValueName, String updateValue) {
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(loginaccount!=null && StringUtils.isNotBlank(instanceUuid) && StringUtils.isNotBlank(attrGroupUuid) && StringUtils.isNotBlank(attrUuid) && StringUtils.isNotBlank(updateValueName)){
			
			boolean editPermissionAllowed = false;
			Long orgId = null;
			
			ModuleInstanceInterface moduleInstance = null;
			moduleInstance = siteDesignDao.getModuleInstanceByUuid(instanceUuid);
			
			if(moduleInstance==null){
				moduleInstance = entityDao.getEntityDetailByUuid(instanceUuid);
				
				editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, ((EntityDetail)moduleInstance).getEntityuuid());
				
			}else{
				editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, ((ModuleInstance)moduleInstance).getModuleuuid());
			
			}
			
			if(editPermissionAllowed){
				if(moduleInstance!=null){
					Module instance = null;
					String moduleDetailUuid = null;
					
					if(moduleInstance.getClass().equals(ModuleInstance.class)){
						instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
						moduleDetailUuid = ((ModuleInstance)moduleInstance).getModuleuuid();
						orgId = ((ModuleInstance)moduleInstance).getOrgid();
					}else if(moduleInstance.getClass().equals(EntityDetail.class)){
						instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
						moduleDetailUuid = ((EntityDetail)moduleInstance).getModuleuuid();
						orgId = ((EntityDetail)moduleInstance).getOrganization_id();
					}
					
					if(StringUtils.isNotBlank(moduleDetailUuid) && instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
						for(AttrGroup g : instance.getAttrGroupList()){
							if(g.getGroupUuid().equals(attrGroupUuid)){
								if(g.getAttrList()!=null && g.getAttrList().size()>0){
									for(ModuleAttribute a : g.getAttrList()){
										if(a.getUuid().equals(attrUuid)){
											
											// filled all extra info for instance's attr, like minlength, ...
											ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleDetailUuid);
											if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
												Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
												if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
													for(AttrGroup mg : module.getAttrGroupList()){
														if(mg.getGroupUuid().equals(g.getModuleGroupUuid())){
															
															if(mg.getAttrList()!=null && mg.getAttrList().size()>0){
																for(ModuleAttribute ma : mg.getAttrList()){
																	if(ma.getUuid().equals(a.getModuleAttrUuid())){
																		SitedesignHelper.updateAttr(a, ma);
																		
																		break;
																	}
																}
															}
															
															break;
															
														}
													}
												}
												
											}
											
											
											Field field = null;
											try {
												
												List<Field> fields = new ArrayList<Field>();
												// find all current class fields
												for(Field f : a.getClass().getDeclaredFields()){
													fields.add(f);
												}
												// find all superclass fields
												for(Field f : a.getClass().getSuperclass().getDeclaredFields()){
													fields.add(f);
												}
												
												// find field in fields list with the name
												if(fields.size()>0){
													for(Field f : fields){
														if(f.getName().equals(updateValueName)){
															field = f;
															break;
														}else if(f.getName().equals("money") && (updateValueName.equals("moneyAmount") || updateValueName.equals("moneyCurrency"))){
															field = f;
															break;
														}
													}
												}
												
												if(field!=null){
													
													ApiResponse valRes = moduleFieldValidation(g, a, updateValueName, updateValue); 
													
													if(valRes.isSuccess()){
														
														field.setAccessible(true);
														
														Class fieldType = field.getType();
														if(fieldType.equals(String.class)){
															field.set(a, updateValue);
														}else if(fieldType.equals(Integer.class) || fieldType.equals(int.class)){
															if(NumberUtils.isNumber(updateValue)){
																BigDecimal val = new BigDecimal(updateValue);
																field.set(a, val.intValue());
																updateValue = Integer.toString(val.intValue());
															}else if(StringUtils.isBlank(updateValue)){
																field.set(a, null);
															}
														}else if(fieldType.equals(Boolean.class)){
															if(updateValue.equals("1")){
																field.set(a, Boolean.TRUE);
																updateValue = Boolean.TRUE.toString();
															}else if(updateValue.equals("0")){
																field.set(a, Boolean.FALSE);
																updateValue = Boolean.FALSE.toString();
															}
														}else if(fieldType.equals(BigDecimal.class)){
															if(NumberUtils.isNumber(updateValue)){
																field.set(a, new BigDecimal(updateValue));
															}else if(StringUtils.isBlank(updateValue)){
																field.set(a, null);
															}
														}else if(fieldType.equals(Money.class)){
															if(field.get(a)!=null){
																
																Money money = (Money)field.get(a);
																if(updateValueName.equals("moneyAmount")){
																	money.setAmount(new BigDecimal(updateValue));
																}else if(updateValueName.equals("moneyCurrency")){
																	try{
																		Currency currency = Currency.getInstance(updateValue);
																		money.setCurrency(currency);
																	}catch (Exception e) {
																		Currency currency = Currency.getInstance(Locale.US);
																		money.setCurrency(currency);
																		updateValue = currency.getCurrencyCode();
																	}
																}
																
															}else{
																if(updateValueName.equals("moneyAmount")){
																	Money money = new Money(new BigDecimal(updateValue));
																	field.set(a, money);
																}else if(updateValueName.equals("moneyCurrency")){
																	Currency aCurrency = null;
																	try{
																		aCurrency = Currency.getInstance(updateValue);
																	}catch (Exception e) {
																		aCurrency = Currency.getInstance(Locale.US);
																	}
																	Money money = new Money(new BigDecimal("0"), aCurrency);
																	field.set(a, money);
																}
																
															}
															
														}													
														// remove all extra info for instance's attribute
														SitedesignHelper.removeAttrExtraInfo(a);
														
														String updatedXml = SitedesignHelper.getXmlFromModule(instance);
														
														// max instance char length check:
														int instanceUsageInfoWith100Mutipled = 0;
														PaymentPlan paymentplan = paymentService.getPaymentPlanAtPointOfDate(orgId, new Date());
														if(paymentplan!=null){
															long maxInstanceCharsCanHave = paymentplan.getMaxcharsperinstance();
															int updatedXmlLength = updatedXml.length();
															instanceUsageInfoWith100Mutipled = paymentService.countCharsUsage(updatedXmlLength, maxInstanceCharsCanHave);
														}
														
														if(instanceUsageInfoWith100Mutipled>100){
															apires.setResponse1("You reach the maximum storage usage for Instance/Product with the new value.");
														}else{
															if(moduleInstance.getClass().equals(ModuleInstance.class)){
																((ModuleInstance)moduleInstance).setInstance(updatedXml);
																
															}else if(moduleInstance.getClass().equals(EntityDetail.class)){
																((EntityDetail)moduleInstance).setDetail(updatedXml);
															}
															
															apires.setSuccess(true);
															apires.setResponse1(updateValue);
															apires.setResponse3(instanceUsageInfoWith100Mutipled);
															
														}
														
													}else{
														apires = valRes;
													}
													
												}
												
											} catch (SecurityException e) {
												e.printStackTrace();
												logger.error(e.toString());
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
												logger.error(e.toString());
											} catch (IllegalAccessException e) {
												e.printStackTrace();
												logger.error(e.toString());
											}
											
											break;
										}
									}
								}
								
								break;
							}
						}
					}
				}
				
			}
			
		}
		
		return apires;
	}

	
	private <T> ApiResponse moduleFieldValidation(T parent, T current, String updateValueName, String newValue){
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(true);
		
		if(parent.getClass().equals(Module.class) && current.getClass().equals(AttrGroup.class)){ // field belongs to group		
			
			if(updateValueName.equals("groupName")){
				// max length check
				if(StringUtils.isBlank(newValue) || newValue.length()>30){
					apires.setSuccess(false);
					apires.setResponse1("Group name can't be empty and can't be larger than 30 characters.");
				}
				
				// for group name duplication check
				if(((Module)parent).getAttrGroupList()!=null){
					for(AttrGroup g : ((Module)parent).getAttrGroupList()){
						if(g.getGroupName().equals(newValue)){
							apires.setSuccess(false);
							apires.setResponse1("Group name \""+newValue+"\" already exist!");
							break;
						}
					}
				}
				
				// for group name format check
				if(!ValidationSet.isAlphaNumUnderscoreOnly(newValue)){
					apires.setSuccess(false);
					apires.setResponse1("Group name can only accept english character, number, and underscore.");
				}
			}
			
		}else if(parent.getClass().equals(AttrGroup.class) && ModuleAttribute.class.isAssignableFrom(current.getClass())){ // field belongs to attr

			if(updateValueName.equals("name")){
				
				// max length check
				if(StringUtils.isBlank(newValue) || newValue.length()>30){
					apires.setSuccess(false);
					apires.setResponse1("Attribute name can't be empty and can't be larger than 30 characters.");
				}
				
				// attr's name duplication check
				if(((AttrGroup)parent).getAttrList()!=null){
					for(ModuleAttribute a : ((AttrGroup)parent).getAttrList()){
						if(a.getName().equals(newValue)){
							apires.setSuccess(false);
							apires.setResponse1("Attr name \""+newValue+"\" already exist!");
						}
					}
				}
				// for attr name format check
				if(!ValidationSet.isAlphaNumUnderscoreOnly(newValue)){
					apires.setSuccess(false);
					apires.setResponse1("Attribute name can only accept english character, number, and underscore.");
				}
			}
			// attr's default value check
			
			else if(updateValueName.equals("defaultValue")){
				
				
//				List<String> a = SitedesignHelper.moduleAttributeValidation(current);
				
				
				
				if(current.getClass().equals(ModuleTextAttribute.class)){
					
					ModuleTextAttribute currentTextAttr = mapper.map(current, ModuleTextAttribute.class);
					currentTextAttr.setDefaultValue(newValue);
					
					List<String> validationErrorInfos = SitedesignHelper.moduleAttributeValidation(currentTextAttr);
					if(validationErrorInfos!=null && validationErrorInfos.size()>0){
						StringBuilder errorInfo = new StringBuilder();
						int i = 0;
						for(String info : validationErrorInfos){
							if(i>0) errorInfo.append("<br/>");
							errorInfo.append(info);
							i++;
						}
						apires.setSuccess(false);
						apires.setResponse1(errorInfo.toString());
					}
					
				}else if(current.getClass().equals(ModuleNumberAttribute.class)){
					
					if(NumberUtils.isNumber(newValue)){
						
						BigDecimal nv = new BigDecimal(newValue);
						
						ModuleNumberAttribute currentNumberAttr = mapper.map(current, ModuleNumberAttribute.class);
						currentNumberAttr.setDefaultValue(nv);
						
						List<String> validationErrorInfos = SitedesignHelper.moduleAttributeValidation(currentNumberAttr);
						if(validationErrorInfos!=null && validationErrorInfos.size()>0){
							StringBuilder errorInfo = new StringBuilder();
							int i = 0;
							for(String info : validationErrorInfos){
								if(i>0) errorInfo.append("<br/>");
								errorInfo.append(info);
								i++;
							}
							apires.setSuccess(false);
							apires.setResponse1(errorInfo.toString());
						}
						
					}else{
						apires.setSuccess(false);
						apires.setResponse1(newValue+" is not a number.");
					}
					
				}
				
				
			}
			else if(updateValueName.equals("linkValue")){
				if(current.getClass().equals(ModuleLinkAttribute.class)){
					ModuleLinkAttribute currentLinkAttr = mapper.map(current, ModuleLinkAttribute.class);
					// set href default value to avoid href validation. 
					currentLinkAttr.setHref("http://www.bizislife.com");
					
					currentLinkAttr.setLinkValue(newValue);
					
					List<String> validationErrorInfos = SitedesignHelper.moduleAttributeValidation(currentLinkAttr);
					if(validationErrorInfos!=null && validationErrorInfos.size()>0){
						StringBuilder errorInfo = new StringBuilder();
						int i = 0;
						for(String info : validationErrorInfos){
							if(i>0) errorInfo.append("<br/>");
							errorInfo.append(info);
							i++;
						}
						apires.setSuccess(false);
						apires.setResponse1(errorInfo.toString());
					}
					
				}
			}
			else if(updateValueName.equals("href")){
				if(current.getClass().equals(ModuleLinkAttribute.class)){
					ModuleLinkAttribute currentLinkAttr = mapper.map(current, ModuleLinkAttribute.class);
					currentLinkAttr.setLinkValue("bizislife");
					
					currentLinkAttr.setHref(newValue);
					
					List<String> validationErrorInfos = SitedesignHelper.moduleAttributeValidation(currentLinkAttr);
					if(validationErrorInfos!=null && validationErrorInfos.size()>0){
						StringBuilder errorInfo = new StringBuilder();
						int i = 0;
						for(String info : validationErrorInfos){
							if(i>0) errorInfo.append("<br/>");
							errorInfo.append(info);
							i++;
						}
						apires.setSuccess(false);
						apires.setResponse1(errorInfo.toString());
					}
					
				}
			}
			
			
			else if(updateValueName.equals("totalNumProductsInPage")){
				if(!NumberUtils.isDigits(newValue)){
					apires.setSuccess(false);
					apires.setResponse1(newValue+": fraction is not allowed here.");
				}else{
					Integer num = Integer.valueOf(newValue);
					if(num<=0){
						apires.setSuccess(false);
						apires.setResponse1("totalNumProductsInPage must bigger than 0");
					}
				}
			}else if(updateValueName.equals("moneyAmount")){
				if(!NumberUtils.isNumber(newValue)){
					apires.setSuccess(false);
					apires.setResponse1(newValue+" is not a number");
				}else{
					// check scale
					BigDecimal newValueInBigDecimal = new BigDecimal(newValue);
					if(newValueInBigDecimal.scale() > 2){
						apires.setSuccess(false);
						apires.setResponse1("Number of decimals is "
								+ newValueInBigDecimal.scale() + ", but currency only takes "
								+ 2 + " decimals.");
					}
					
					
				}
			}else if(updateValueName.equals("defaultPicture")){
				if(((ModuleImageAttribute)current).getArray()!=null && ((ModuleImageAttribute)current).getArray() && newValue.equals("1")){
					apires.setSuccess(false);
					apires.setResponse1("Array image attribute can't be default image for product.");
				}
			}else if(updateValueName.equals("array")){
				if(current.getClass().equals(ModuleImageAttribute.class)){
					if(((ModuleImageAttribute)current).getDefaultPicture()!=null && ((ModuleImageAttribute)current).getDefaultPicture() && newValue.equals("1")){
						apires.setSuccess(false);
						apires.setResponse1("Default image attribure can't be array.");
					}
				}
			}
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateModuleGroupValue(String moduleUuid, String attrGroupUuid, String updateValueName, String updateValue) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(attrGroupUuid) && StringUtils.isNotBlank(updateValueName)){
			ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
			if(moduledetail!=null && StringUtils.isNotBlank(moduledetail.getXml())){
				
				Module module = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
				if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
					// permission check:
					AccountDto loginAccount = accountService.getCurrentAccount();
					boolean isModifyPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleUuid);
					
					if(isModifyPermissionAllowed){
						
						for(AttrGroup g : module.getAttrGroupList()){
							if(g.getGroupUuid().equals(attrGroupUuid)){
								try {
									Field field = g.getClass().getDeclaredField(updateValueName);
									if(field!=null){
										
										ApiResponse valRes = moduleFieldValidation(module, g, updateValueName, updateValue); 
										
										if(valRes.isSuccess()){
											
											field.setAccessible(true);
											
											Class fieldType = field.getType();
											if(fieldType.equals(String.class)){
												field.set(g, updateValue);
											}else if(fieldType.equals(Integer.class)){
												if(NumberUtils.isNumber(updateValue)){
													field.set(g, Integer.valueOf(updateValue));
												}
											}else if(fieldType.equals(Boolean.class)){
												if(updateValue.equals("1")){
													field.set(g, Boolean.TRUE);
													updateValue = Boolean.TRUE.toString();
												}else if(updateValue.equals("0")){
													field.set(g, Boolean.FALSE);
													updateValue = Boolean.FALSE.toString();
												}
											}else if(fieldType.equals(BigDecimal.class)){
												field.set(g, new BigDecimal(updateValue));
											}
											
											String updatedXml = SitedesignHelper.getXmlFromModule(module);
											
											PaymentPlan paymentPlan = paymentService.getPaymentPlanAtPointOfDate(moduledetail.getOrganization_id(), new Date());
											if(paymentPlan!=null){
												int updatedXmlLength = updatedXml.length();
												if(updatedXmlLength>paymentPlan.getMaxcharspermoduledetail()){
													apires.setResponse1("You reach the maximum storage usage for ModuleDetail with the new value.");
												}else{
													moduledetail.setXml(updatedXml);
													apires.setSuccess(true);
													apires.setResponse1(updateValue);
													apires.setResponse3(paymentService.countCharsUsage(updatedXmlLength, paymentPlan.getMaxcharspermoduledetail()));
													
													// extra information should be returned back
													if(updateValueName.equals("groupName") || updateValueName.equals("array")){
														apires.setResponse2("You are trying to update the \""+updateValueName+"\", you need to update module's jsp and instances' jsp also.");
													}
												}
											}else{
												apires.setResponse1("System can't find payment plan for org"+moduledetail.getOrganization_id());
											}
											
										}else{
											apires = valRes;
										}
										
									}
								} catch (SecurityException e) {
									e.printStackTrace();
									logger.error(e.toString());
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
									logger.error(e.toString());
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									logger.error(e.toString());
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									logger.error(e.toString());
								}
								
								break;
							}
						}
						
					}else{
						apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have modify permission for node: "+moduleUuid);
					}
				}
			}
		}
		
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateModuleDetailByFieldnameValue(String moduleUuid, String updateValueName, String updateValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
		if(moduleDetail!=null && StringUtils.isNotBlank(updateValueName)){
			
			// permissin check
			AccountDto loginAccount = accountService.getCurrentAccount();
			boolean isModifyPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleUuid);
			if(isModifyPermissionAllowed){
				boolean updated = false;
				if(updateValueName.equals("description")){
					
					if(updateValue.length()<1000){
						moduleDetail.setDescription(HtmlEscape.escapeTags(updateValue));
						updated = true;
					}else{
						apires.setResponse1("Description can have 1000 characters maximum.");
					}
					
				}else if(updateValueName.equals("jsp")){
					// remove jsp expression (<%= xxx %>), jsp scriptlet (<% ... %>), jsp declaration (<%! ... %>) from jsp
					// also not allow jsp directives (<%@ ...%>)
//					updateValue = StringUtils.replaceEach(updateValue, new String[]{"<%", "%>"}, new String[]{"<%--", "--%>"});
					// 
//					String escapedHtmlString = StringEscapeUtils.escapeHtml(updateValue!=null?updateValue.trim():null);
					
					
					
					String escapedHtmlString = HtmlEscape.escapeTags(updateValue);
					
					// check max jsp's char usage:
					int charUsageWith100Multipled = 0;
					PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(moduleDetail.getOrganization_id(), new Date());
					if(plan!=null){
						int usedCharLength = escapedHtmlString!=null?escapedHtmlString.length():0;
						charUsageWith100Multipled = paymentService.countCharsUsage(usedCharLength, plan.getMaxcharsperjsp());
					}
					if(charUsageWith100Multipled<=100){
						moduleDetail.setJsp(escapedHtmlString);
						//updateValue = escapedHtmlString;
						updated = true;
						
						apires.setResponse3(charUsageWith100Multipled);
					}else{
						apires.setResponse1("You reach the maximum storage usage for moduleDetail's jsp (used space/total space = "+ charUsageWith100Multipled +"/100).");
					}
					
					
				}else if(updateValueName.equals("css")){
					// remove jsp expression (<%= xxx %>), jsp scriptlet (<% ... %>), jsp declaration (<%! ... %>) from jsp
					// also not allow jsp directives (<%@ ...%>)
//					updateValue = StringUtils.replaceEach(updateValue, new String[]{"<%", "%>"}, new String[]{"<%--", "--%>"});
					// 
					
					// check max jsp's char usage:
					int charUsageWith100Multipled = 0;
					PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(moduleDetail.getOrganization_id(), new Date());
					if(plan!=null){
						int usedCharLength = updateValue!=null?updateValue.length():0;
						charUsageWith100Multipled = paymentService.countCharsUsage(usedCharLength, plan.getMaxcharspercss());
					}
					if(charUsageWith100Multipled<=100){
						moduleDetail.setCss(HtmlEscape.escapeTags(updateValue));
						updated = true;
						
						apires.setResponse3(charUsageWith100Multipled);
					}else{
						apires.setResponse1("You reach the maximum storage usage for moduleDetail's css (used space/total space = "+ charUsageWith100Multipled +"/100).");
					}
					
				}else if(updateValueName.equals("type")){
					if(updateValue!=null && updateValue.trim().equals("1")){
						moduleDetail.setType(ModuleDetail.Type.productModule.getCode());
						updated = true;
						updateValue = "true";
					}else{
						moduleDetail.setType(ModuleDetail.Type.module.getCode());
						updated = true;
						updateValue = "false";
					}
				}else if(updateValueName.equals("prettyname")){
					// validation the prettyname
					boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
					if(nameVali){
						moduleDetail.setPrettyname(updateValue!=null?updateValue.trim():null);
						updated = true;
						
						// update the name in tree levelview
						ModuleTreeLevelView view = siteDesignDao.getModuleTreeLevelViewHasNode(moduleDetail.getModuleuuid());
						if(view!=null){
							boolean treeviewUpdated = false;
							List<ModuleTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ModuleTreeNode.class, view.getNodes());
							if(nodes!=null && nodes.size()>0){
								for(ModuleTreeNode n : nodes){
									if(n.getSystemName().equals(moduleDetail.getModuleuuid())){
										n.setPrettyName(updateValue!=null?updateValue.trim():null);
										treeviewUpdated = true;
										break;
									}
								}
								if(treeviewUpdated){
									String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
									view.setNodes(updatedxml);
								}
							}
						}
						
					}else{
						apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
					}
					
				}
				if(updated){
					
					Long moduleId = siteDesignDao.saveModuleDetail(moduleDetail);
					if(moduleId!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue);
					}
					
				}
				
			}else{
				apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have permssion to modify node : "+moduleUuid);
			}
			
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateInstanceViewByFieldnameValue(String viewUuid, String updateValueName, String updateValue) {
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(viewUuid) && StringUtils.isNotBlank(updateValueName)){
			InstanceView instanceView = siteDesignDao.getInstanceViewByUuid(viewUuid);
			
			if(instanceView!=null){
				
				// permission check;
				boolean editPermissionAllowed = false;
				if(InstanceView.Type.NormalInstanceView.getCode().equals(instanceView.getViewtype())){
					editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleuuid());
				}else if(InstanceView.Type.ProductInstanceView.getCode().equals(instanceView.getViewtype())){
					editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleinstanceuuid());
				}
				
				if(editPermissionAllowed){
					
					boolean updated = false;
					if(updateValueName.equals("description")){
						
						if(updateValue.length()<1000){
							instanceView.setDescription(updateValue!=null?updateValue.trim():null);
							updated = true;
						}else{
							apires.setResponse1("Description can have 1000 characters maximum.");
						}
						
					}else if(updateValueName.equals("jsp")){
						String escapedHtmlString = StringEscapeUtils.escapeHtml(updateValue!=null?updateValue.trim():null);
						
						// check max jsp's char usage:
						int charUsageWith100Multipled = 0;
						PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(instanceView.getOrgid(), new Date());
						if(plan!=null){
							int usedCharLength = escapedHtmlString!=null?escapedHtmlString.length():0;
							charUsageWith100Multipled = paymentService.countCharsUsage(usedCharLength, plan.getMaxcharsperjsp());
						}
						
						if(charUsageWith100Multipled<=100){
							instanceView.setJsp(escapedHtmlString);
							//updateValue = escapedHtmlString;
							updated = true;
							
							apires.setResponse3(charUsageWith100Multipled);
						}else{
							apires.setResponse1("You reach the maximum storage usage for instanceview's jsp (used space/total space = "+ charUsageWith100Multipled +"/100).");
						}
					}else if(updateValueName.equals("css")){
						
						int charUsageWith100Multipled = 0;
						PaymentPlan plan = paymentService.getPaymentPlanAtPointOfDate(instanceView.getOrgid(), new Date());
						if(plan!=null){
							int usedCharLength = updateValue!=null?updateValue.length():0;
							charUsageWith100Multipled = paymentService.countCharsUsage(usedCharLength, plan.getMaxcharspercss());
						}
						
						if(charUsageWith100Multipled<=100){
							
							instanceView.setCss(updateValue!=null?updateValue.trim():null);
							updated = true;
							apires.setResponse3(charUsageWith100Multipled);
							
						}else{
							apires.setResponse1("You reach the maximum storage usage for instanceview's css (used space/total space = "+ charUsageWith100Multipled +"/100).");
						}
						
						
					}else if(updateValueName.equals("isdefault")){
						if(updateValue!=null && updateValue.trim().equals("1")){
							
							// set all other views for the instance not as default;
							List<InstanceView> views = siteDesignDao.findInstanceViewsByInstanceUuid(instanceView.getModuleinstanceuuid());
							if(views!=null && views.size()>0){
								for(InstanceView v : views){
									v.setIsdefault(InstanceView.IsDefault.no.getCode());
								}
							}
							
							instanceView.setIsdefault(InstanceView.IsDefault.yes.getCode());
							updated = true;
							updateValue = "yes";
						}else{
							instanceView.setIsdefault(InstanceView.IsDefault.no.getCode());
							updated = true;
							updateValue = "no";
						}
						
						
						List<String> instanceUuids = findInstanceChainFromView(instanceView.getInstanceviewuuid());
	        			Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
	        			findAllActivatedViewsAndScheds(instanceUuids, activatedViewsAndScheds);
	        			
	        			if(activatedViewsAndScheds.size()>0){
	        				List<String> activatedUuids = new ArrayList<String>();
	        				for(Map.Entry<String, String> entry : activatedViewsAndScheds.entrySet()){
	        					activatedUuids.add(entry.getKey());
	        				}
	            			apires.setResponse2(activatedUuids);
	        			}
						
						
					}else if(updateValueName.equals("viewname")){
						// validation the name
						boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
						if(nameVali){
							instanceView.setViewname(updateValue!=null?updateValue.trim():null);
							updated = true;
						}else{
							apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
						}
					}
					if(updated){
						Long viewId = siteDesignDao.saveInstanceView(instanceView);
						if(viewId!=null){
							apires.setSuccess(true);
							apires.setResponse1(updateValue);
						}else{
							apires.setResponse1("System can save the instance view, you can refresh the page and try again!");
						}
					}
					
				}else{
					apires.setResponse1("You don't have permission to update.");
				}
				
			}
			
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateInstanceViewScheduleByFieldnameValue(String schedUuid, String updateValueName, String updateValue) {
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(loginaccount!=null && StringUtils.isNotBlank(schedUuid) && StringUtils.isNotBlank(updateValueName)){
			InstanceViewSchedule sched = siteDesignDao.getInstanceViewScheduleByUuid(schedUuid);
			
			if(sched!=null){
				
				// permission check
				boolean editPermissionAllowed = false;
				InstanceView instanceView = siteDesignDao.getInstanceViewByUuid(sched.getInstanceviewuuid());
				if(instanceView!=null){
					if(InstanceView.Type.NormalInstanceView.getCode().equals(instanceView.getViewtype())){
						editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleuuid());
					}else if(InstanceView.Type.ProductInstanceView.getCode().equals(instanceView.getViewtype())){
						editPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleinstanceuuid());
					}
				}
				
				if(editPermissionAllowed){
					
					boolean updated = false;
					if(updateValueName.equals("startdate")){
						
						String datePattern = "yyyy-MM-dd";
						SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);

						Date startdate = null;
						try {
							startdate = StringUtils.isNotBlank(updateValue)?dateformat.parse(updateValue):null;
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// check startdate is before enddate
						boolean passed = true;
						if(startdate!=null && sched.getEnddate()!=null){
							if(startdate.after(sched.getEnddate())){
								passed = false;
								apires.setResponse1("'From Date' can't be after 'To Date'!");
							}
						}
						if(passed){
							sched.setStartdate(startdate);
							updated = true;
						}
					}else if(updateValueName.equals("enddate")){
						String datePattern = "yyyy-MM-dd";
						SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);

						Date endDate = null;
						try {
							endDate = StringUtils.isNotBlank(updateValue)?dateformat.parse(updateValue):null;
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// check startdate is before enddate
						boolean passed = true;
						if(endDate!=null && sched.getStartdate()!=null){
							if(endDate.before(sched.getStartdate())){
								passed = false;
								apires.setResponse1("'From Date' can't be after 'To Date'!");
							}
						}
						if(passed){
							sched.setEnddate(endDate!=null?DateUtils.getEnd(endDate):null);
							updated = true;
						}
					}else if(updateValueName.equals("priority")){
						Integer priority = null;
						if(StringUtils.isNotBlank(updateValue) && NumberUtils.isNumber(updateValue)){
							priority = Integer.valueOf(updateValue);
						}
						sched.setPriority(priority);
						updated = true;
						
						InstanceViewSchedule.PriorityLevel newlevel = InstanceViewSchedule.PriorityLevel.fromCode(priority.intValue());
						if(newlevel!=null){
							updateValue = newlevel.name();
						}else{
							updateValue = null;
						}
					}else if(updateValueName.equals("name")){
						
				    	boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
				    	if(nameVali){
			        		sched.setSchedulename(updateValue);
			        		updated = true;
				    		
				    	}else{
				    		apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
				    	}
						
					}
					if(updated){
						Long id = siteDesignDao.saveInstanceViewSchedule(sched);
						if(id!=null){
							apires.setSuccess(true);
							apires.setResponse1(updateValue);
							
			    			List<String> instanceUuids = findInstanceChainFromViewSchedule(sched.getUuid());
			    			
			    			if(instanceUuids.size()>0){
			        			Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
			        			findAllActivatedViewsAndScheds(instanceUuids, activatedViewsAndScheds);
			        			
			        			if(activatedViewsAndScheds.size()>0){
			        				List<String> activatedUuids = new ArrayList<String>();
			        				for(Map.Entry<String, String> entry : activatedViewsAndScheds.entrySet()){
			        					activatedUuids.add(entry.getKey());
			        				}
			            			apires.setResponse2(activatedUuids);
			        			}
			    			}
							
						}
					}
				}
			}
			
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public ApiResponse delInstanceViewByUuid(String viewUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		InstanceView view = siteDesignDao.getInstanceViewByUuid(viewUuid);
		if(loginAccount!=null && view!=null){
			
			// check edit permission
			boolean editPermissionAllowed = false;
			if(InstanceView.Type.NormalInstanceView.getCode().equals(view.getViewtype())){
				editPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, view.getModuleuuid());
			}else if(InstanceView.Type.ProductInstanceView.getCode().equals(view.getViewtype())){
				editPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, view.getModuleinstanceuuid());
			}
			
			if(editPermissionAllowed){
				String moduleInstanceUuid = view.getModuleinstanceuuid();
				
				// del all jsp css and schedule
				delInstanceViewCssFile(view.getInstanceviewuuid(), view.getOrgid());
				delInstanceViewJspFile(view.getInstanceviewuuid(), view.getOrgid());
				
				// del all instanceViewSchedule 
				List<InstanceViewSchedule> scheds = siteDesignDao.findInstanceViewSchedulesByInstanceViewUuid(view.getInstanceviewuuid());
				if(scheds!=null && scheds.size()>0){
					for(InstanceViewSchedule sd : scheds){
						siteDesignDao.delInstanceViewScheduleByUuid(sd.getUuid());
					}
				}
				
				// del view
				siteDesignDao.delInstanceViewbyId(view.getId());
				
				apires.setSuccess(true);
				
				List<String> instanceUuids = new ArrayList<String>();
				instanceUuids.add(moduleInstanceUuid);
				Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
				findAllActivatedViewsAndScheds(instanceUuids, activatedViewsAndScheds);
				
				if(activatedViewsAndScheds.size()>0){
					List<String> activatedUuids = new ArrayList<String>();
					for(Map.Entry<String, String> entry : activatedViewsAndScheds.entrySet()){
						activatedUuids.add(entry.getKey());
					}
	    			apires.setResponse1(activatedUuids);
				}
				
			}else{
				apires.setResponse1("You don't have permission to delete");
			}
			
		}else{
			apires.setResponse1("No enough information to delete instance view (viewUuid: "+viewUuid+").");
		}
		
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse delModuleInstanceByUuid(String currentNodeId) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ModuleInstance instance = siteDesignDao.getModuleInstanceByUuid(currentNodeId);
		if(loginaccount!=null && instance!=null){
			
			boolean delPermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instance.getModuleuuid());
			
			if(delPermissionAllowed){
				
				// 1. remove views belong to instance
				List<InstanceView> views = siteDesignDao.findInstanceViewsByInstanceUuid(instance.getModuleinstanceuuid());
				if(views!=null && views.size()>0){
					for(InstanceView view : views){
						delInstanceViewByUuid(view.getInstanceviewuuid());
					}
				}
				
				// 2. remove moduleinstanceschedule if instance has
				List<ModuleInstanceSchedule> miScheds = siteDesignDao.findModuleInstanceSchedulesByInstanceUuid(instance.getModuleinstanceuuid());
				if(miScheds!=null && miScheds.size()>0){
					for(ModuleInstanceSchedule sched : miScheds){
						siteDesignDao.delModuleInstanceScheduleByUuid(sched.getUuid());
					}
				}
				
				// 3. remove tree node from tree.
				treeService.delTreeNodeByUuid(ModuleTreeNode.class, currentNodeId);
				
				// 4. remove moduleMetas
				Module module = SitedesignHelper.getModuleFromXml(instance.getInstance());
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
				
				// 5. remove instance
				siteDesignDao.delModuleInstanceByUuid(instance.getModuleinstanceuuid());

				apires.setSuccess(true);
				
			}
			
		}
		return apires;
	}

	@Override
	public ApiResponse delInstanceViewCssFile(String viewUuid, Long orgId) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		if(StringUtils.isNotBlank(viewUuid)){
			StringBuilder filepath = new StringBuilder();
			filepath.append(servletContext.getRealPath("/css/org"));
			if(filepath.indexOf("/")>-1){
				filepath.append("/").append(orgId).append("/");
			}else{
				filepath.append("\\").append(orgId).append("\\");
			}
			filepath.append(viewUuid).append(".css");
			
			
			File file = new File(filepath.toString());
			if(file.exists()){
				if(file.delete()){
					apires.setSuccess(true);
					apires.setResponse1("File \""+filepath.toString()+"\" is deleted!");
				}else{
					apires.setResponse1("File \""+filepath.toString()+"\" can't be deleted!");
				}
			}else{
				apires.setSuccess(true);
				apires.setResponse1("File \""+filepath.toString()+"\" is not exist.");
			}
		}
		
		return apires;
	}

	@Override
	public ApiResponse delInstanceViewJspFile(String viewUuid, Long orgId) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		if(StringUtils.isNotBlank(viewUuid)){
			StringBuilder filepath = new StringBuilder();
			filepath.append(servletContext.getRealPath("/WEB-INF/view/module/org/"));
			if(filepath.indexOf("/")>-1){
				filepath.append("/").append(orgId).append("/").append(viewUuid).append(".jsp");
			}else{
				filepath.append("\\").append(orgId).append("\\").append(viewUuid).append(".jsp");
			}
			File file = new File(filepath.toString());
			if(file.exists()){
				if(file.delete()){
					apires.setSuccess(true);
					apires.setResponse1("File \""+filepath.toString()+"\" is deleted!");
				}else{
					apires.setResponse1("File \""+filepath.toString()+"\" can't be deleted!");
				}
			}else{
				apires.setSuccess(true);
				apires.setResponse1("File \""+filepath.toString()+"\" is not exist.");
			}
		}
		
		return apires;
	}

	@Override
	public ApiResponse delModuleViewCssFile(String moduleUuid, Long orgId) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder filepath = new StringBuilder();
//			filepath.append(servletContext.getRealPath("/css/org")).append(orgId).append("/").append(moduleUuid).append(".css");
			
			filepath.append(servletContext.getRealPath("/css/org"));
			if(filepath.indexOf("/")>-1){
				filepath.append("/").append(orgId).append("/");
			}else{
				filepath.append("\\").append(orgId).append("\\");
			}
			filepath.append(moduleUuid).append(".css");

			File file = new File(filepath.toString());
			if(file.exists()){
				if(file.delete()){
					apires.setSuccess(true);
					apires.setResponse1("File \""+filepath.toString()+"\" is deleted!");
				}else{
					apires.setResponse1("File \""+filepath.toString()+"\" can't be deleted!");
				}
			}else{
				apires.setSuccess(true);
				apires.setResponse1("File \""+filepath.toString()+"\" is not exist.");
			}
		}
		
		return apires;
	}

	@Override
	public ApiResponse delModuleViewJspFile(String moduleUuid, Long orgId) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		if(StringUtils.isNotBlank(moduleUuid)){
			StringBuilder filepath = new StringBuilder();
			filepath.append(servletContext.getRealPath("/WEB-INF/view/module/org/"));
			if(filepath.indexOf("/")>-1){
				filepath.append("/").append(orgId).append("/").append(moduleUuid).append(".jsp");
			}else{
				filepath.append("\\").append(orgId).append("\\").append(moduleUuid).append(".jsp");
			}
			File file = new File(filepath.toString());
			if(file.exists()){
				if(file.delete()){
					apires.setSuccess(true);
					apires.setResponse1("File \""+filepath.toString()+"\" is deleted!");
				}else{
					apires.setResponse1("File \""+filepath.toString()+"\" can't be deleted!");
				}
			}else{
				apires.setSuccess(true);
				apires.setResponse1("File \""+filepath.toString()+"\" is not exist.");
			}
		}
		
		return apires;
	}

	@Override
	@Transactional
	public Long saveModuleTreeLevelView(ModuleTreeLevelView view) {
		return siteDesignDao.saveModuleTreeLevelView(view);
	}

	@Override
	@Transactional(readOnly=true)
	public void findAllActivatedViewsAndScheds(List<String> instanceUuids, Map<String, String> activatedViewsAndScheds) {
		if(instanceUuids!=null && instanceUuids.size()>0 && activatedViewsAndScheds!=null){
			
			for(String instanceuuid : instanceUuids){
				
				InstanceView currentInstanceView = null;
				
				List<InstanceViewSchedule> instanceViewSchedules = siteDesignDao.findInstanceViewSchedulesByInstanceUuid(instanceuuid);
				if(instanceViewSchedules!=null && instanceViewSchedules.size()>0){
					InstanceViewSchedule currentInstanceViewSchedule = (InstanceViewSchedule)getCurrentSchedule(instanceViewSchedules, new Date());
					
					// find activated schedule for instance (note: one instance can only have one activated view and activated schedule)
					if(currentInstanceViewSchedule!=null){
						activatedViewsAndScheds.put(currentInstanceViewSchedule.getUuid(), currentInstanceViewSchedule.getClass().getSimpleName());
						currentInstanceView = siteDesignDao.getInstanceViewByUuid(currentInstanceViewSchedule.getInstanceviewuuid());
					}
				}
				
				if(currentInstanceView!=null){
					activatedViewsAndScheds.put(currentInstanceView.getInstanceviewuuid(), currentInstanceView.getClass().getSimpleName());
				}else{ // try to get the default view
					List<InstanceView> instanceViews = siteDesignDao.findInstanceViewsByInstanceUuid(instanceuuid);
					if(instanceViews!=null && instanceViews.size()>0){
						for(InstanceView v : instanceViews){
							if(v.getIsdefault().equals(InstanceView.IsDefault.yes.getCode())){
								currentInstanceView = v;
								break;
							}
						}
					}
					if(currentInstanceView!=null){
						activatedViewsAndScheds.put(currentInstanceView.getInstanceviewuuid(), currentInstanceView.getClass().getSimpleName());
					}
				}
			}
			
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<String> findInstanceChainFromView(String viewUuid) {
		InstanceView view = siteDesignDao.getInstanceViewByUuid(viewUuid);
		if(view!=null){
			
			List<String> instanceUuids = new ArrayList<String>();
			
			if(view.getViewtype().equals(InstanceView.Type.NormalInstanceView.getCode())){
				instanceUuids.add(view.getModuleinstanceuuid());
			}else if(view.getViewtype().equals(InstanceView.Type.ProductInstanceView.getCode())){
				instanceUuids.add(view.getModuleinstanceuuid());
				EntityDetail entityDetail = entityDao.getEntityDetailByUuid(view.getModuleinstanceuuid());
				if(entityDetail!=null && StringUtils.isNotBlank(entityDetail.getPath())){
					String[] paths = entityDetail.getPath().split("/");
					for(String path : paths){
						instanceUuids.add(path);
					}
				}
			}
			
			return instanceUuids;
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<String> findInstanceChainFromViewSchedule(String schedUuid) {
		
		InstanceViewSchedule sched = siteDesignDao.getInstanceViewScheduleByUuid(schedUuid);
		if(sched!=null){
			return findInstanceChainFromView(sched.getInstanceviewuuid());
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse updateContainerDetailByFieldnameValue(String containerUuid, String updateValueName, String updateValue){
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		Date now = new Date();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		boolean updated = false;
		
		ContainerDetail containerDetail = siteDesignDao.getContainerDetailByUuid(containerUuid);
		if(loginaccount!=null && containerDetail!=null && StringUtils.isNotBlank(updateValueName)){
			
			// permission check
			boolean editpermissionallowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
			
			if(editpermissionallowed){
				
				boolean pagePublishRequired = false;
				StringBuilder changeDetailInfo = new StringBuilder();
				
				if(updateValueName.equals("prettyname")){
					if(StringUtils.isNotBlank(updateValue)){
						// name validation
						boolean nameVali = ValidationSet.isAlphaNumUnderscoreOnly(updateValue);
						if(nameVali){
							
							containerDetail.setPrettyname(updateValue.trim());
							updated = true;
							
							pagePublishRequired = true;
							changeDetailInfo.append("container (").append(containerDetail.getPrettyname()).append(") prettyname changed");
							
							// update prettyname in tree level
							ContainerTreeLevelView treelevelview = siteDesignDao.getContainerTreeLevelViewHasNode(containerUuid);
							if(treelevelview!=null){
								boolean treeviewUpdated = false;
								List<ContainerTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ContainerTreeNode.class, treelevelview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(ContainerTreeNode n : nodes){
										if(n.getSystemName().equals(containerDetail.getContaineruuid())){
											n.setPrettyName(updateValue);
											treeviewUpdated = true;
											break;
										}
									}
									if(treeviewUpdated){
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										treelevelview.setNodes(updatedxml);
									}
								}
							}
							
						}else{
							apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, _ only.");
						}
						
					}else{
						apires.setResponse1("Container prettyname can't be empty!");
					}
				}else if(updateValueName.equals("hexcolor")){
//					System.out.println("hexcolor");
					if(StringUtils.isNotBlank(updateValue)){
						
						boolean isValidate = ValidationSet.isValidHexColor("#"+updateValue);
						if(isValidate){
							containerDetail.setHexColor(updateValue.trim());
							updated = true;
							pagePublishRequired = true;
							changeDetailInfo.append("container (").append(containerDetail.getPrettyname()).append(") hexcolor changed");
						}else{
							apires.setResponse1(updateValue+" hasn't been passed validation");
						}
						
					}else{
						apires.setResponse1("HexColor can't be empty!");
					}
				}else if(updateValueName.equals("moduleuuid")){
				
					// check if moduleDetail exist:
					ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(updateValue.trim());
					if(moduleDetail!=null){
						containerDetail.setModuleuuid(updateValue.trim());
						updated = true;
					}else{
						apires.setResponse1("System can't find ModuleDetail by moduleId: "+updateValue);
					}
				}else if(updateValueName.equals("classnames")){
					
					boolean classnameVali = ValidationSet.isAlphaNumUnderscoreSpaceOnly(updateValue);
					if(classnameVali){
						
						// max length validation : 255 chars
						if(updateValue.trim().length()<=255){
							containerDetail.setClassnames(updateValue.trim());
							updated = true;
							pagePublishRequired = true;
							changeDetailInfo.append("container (").append(containerDetail.getPrettyname()).append(") classname changed");
						}else{
							apires.setResponse1("Maximum length for classnames is 255 characters.");
						}
						
					}else{
						apires.setResponse1("Classname validation is not passed, classname can use a-z, A-Z, 0-9, _, space only.");
					}
					
				}
				
				if(updated){
					Long containerId = siteDesignDao.saveContainerDetail(containerDetail);
					if(containerId!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue);
						apires.setResponse2(containerUuid);
						
						apires.setResponse3(containerDetail.getPageuuid());
						
						if(pagePublishRequired){
							// activity log
							// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed 4) add pageMeta
							if(containerId!=null){
								Organization org = orgDao.getOrganizationById(containerDetail.getOrganization_id());
								Accountprofile creator = accountDao.getAccountProfileByAccountId(loginaccount.getId());
								PageDetail pageDetail = siteDesignDao.getPageDetailByUuid(containerDetail.getPageuuid());
								// 1) log the activity
								String key_oid = "orgId";
								String key_oname = "orgName";
								String key_cid = "operatorId";
								String key_cname = "operatorName";
								String key_ctuuid = "containerUuid";
								ActivityLogData activityLogData = new ActivityLogData();
								Map<String, Object> dataMap = new HashMap<String, Object>();
								dataMap.put(key_oid, org.getId());
								dataMap.put(key_oname, org.getOrgname());
								dataMap.put(key_cname, 
										creator!=null?
												(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
												:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc());
								dataMap.put(key_cid, creator.getAccount_id());
								dataMap.put(key_ctuuid, containerDetail.getContaineruuid());
								activityLogData.setDataMap(dataMap);
								String desc = messageFromPropertiesService.getMessageSource().getMessage("changePageContainer", 
										new Object[] { 
											creator!=null?
												(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
												:DatabaseRelatedCode.AccountRelated.accountIdForAnnonymous.getDesc(), 
											"updates",
											containerDetail.getContaineruuid(), 
											containerDetail.getPageuuid(),
											changeDetailInfo.toString()
											}, Locale.US);
								activityLogData.setDesc(desc);
								Long activityLogId = messageService.newActivity(loginaccount.getId(), containerDetail.getOrganization_id(), ActivityType.changePageContainer, activityLogData);
								
								// 2) create a topic
								Topic topic = new Topic(null,
										UUID.randomUUID().toString(),
										"Container modification",
										new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".page.").append(PageDetail.Type.fromCode(pageDetail.getType()).name().toLowerCase()).append(".modify").toString(),
										Topic.AccessLevel.privateTopic.getCode(),
										Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
										org.getId(),
										"Container modification",
										now,
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
								addToPageChangeList(pageDetail.getId(), activityLogId, desc);
							}
						}
						
					}
				}
				
			}else{
				apires.setResponse1("You don't have permission to update.");
			}
			
			
		}else{
			apires.setResponse1("Your session could be expired Or no enough information to update the containerDetail. (contianerUuid: "+containerUuid+", updateValueName: "+updateValueName+")");
		}
		
		
		return apires;
		
	}
	
	@Override
	@Transactional
	public ApiResponse updatePageDetailByFieldnameValue(String pageUuid, String updateValueName, String updateValue) {
		
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		boolean updated = false;
		
		PageDetail pagedetail = siteDesignDao.getPageDetailByUuid(pageUuid);
		Organization org = orgDao.getOrganizationById(pagedetail.getOrganization_id());
		if(loginaccount!=null && org!=null && pagedetail!=null && StringUtils.isNotBlank(updateValueName)){
			
			// permission check
			boolean editpermissionAllowed = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, pagedetail.getPageuuid());
			
			if(editpermissionAllowed){
				PageDetail.Type pagetype = PageDetail.Type.fromCode(pagedetail.getType());
				
				if(updateValueName.equals("prettyname")){ // update prettyname
					if(StringUtils.isNotBlank(updateValue)){
						
						// validation prettyname
						boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
						if(nameVali){
							pagedetail.setPrettyname(updateValue.trim());
							updated = true;
							
							// update prettyname in leveltree.
							PageTreeLevelView treelevelview = siteDesignDao.getPageTreeLevelViewHasNode(pageUuid);
							if(treelevelview!=null){
								boolean treeviewUpdated = false;
								List<PageTreeNode> nodes = TreeHelp.getTreeNodesFromXml(PageTreeNode.class, treelevelview.getNodes());
								if(nodes!=null && nodes.size()>0){
									for(PageTreeNode n : nodes){
										if(n.getSystemName().equals(pagedetail.getPageuuid())){
											n.setPrettyName(updateValue);
											treeviewUpdated = true;
											break;
										}
									}
									if(treeviewUpdated){
										String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
										treelevelview.setNodes(updatedxml);
									}
								}
							}
							
						}else{
							apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");

						}
						
					}else{
						apires.setResponse1("Page prettyname can't be empty!");
					}
				}else if(updateValueName.equals("url")){ // update url
					// url validation
					apires = passUrlValidation(updateValue, pagedetail.getOrganization_id(), pagetype);
					if(apires.isSuccess()){
						updated = true;
						pagedetail.setUrl(updateValue.trim());
						
						// put new open page link in response2 : http://localhost:8080/getPage/org/76858e56-1d59-45a2-b6a6-37c8fc8503e7/pageurl/testpage1
						StringBuilder newLink = new StringBuilder();
						newLink.append("http://").append(applicationConfig.getHostName()).append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(pagedetail.getUrl());
						apires.setResponse2(newLink.toString());
					}
					
				}else if(updateValueName.equals("css")){ // update css
					// get pagemeta
					PageMeta pagemeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());
					if(pagemeta==null){ // create pagemeta if null
						String metauuid = UUID.randomUUID().toString();
						pagemeta= new PageMeta(null, metauuid, pagedetail.getPageuuid(), "title", PageMeta.DefaultCss.previewMode.getCode(),null, null);
					}
					pagemeta.setCss(HtmlEscape.escapeTags(updateValue));
					
					Long pagemetaId = siteDesignDao.savePageMeta(pagemeta);
					if(pagemetaId!=null){
		        		writePageCssToFile(pagedetail.getId());

						apires.setSuccess(true);
						apires.setResponse1(updateValue);
					}
				}else if(updateValueName.equals("defaultcss")){
					PageMeta.DefaultCss defaultCss = PageMeta.DefaultCss.getDefaultCssForCode(updateValue.trim());
					if(defaultCss!=null){
						PageMeta pagemeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());
						if(pagemeta==null){ // create pagemeta if null
							String metauuid = UUID.randomUUID().toString();
							pagemeta= new PageMeta(null, metauuid, pagedetail.getPageuuid(), null, PageMeta.DefaultCss.previewMode.getCode(), null, null);
						}
						pagemeta.setDefaultcss(defaultCss.getCode());
						Long pagemetaid = siteDesignDao.savePageMeta(pagemeta);
						if(pagemetaid!=null){
							apires.setSuccess(true);
							apires.setResponse1(defaultCss.name());
						}
					}
					
					
				}else if(updateValueName.equals("pagetitle")){ // update page title
					
					PageMeta pagemeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());
					if(pagemeta==null){ // create pagemeta if null
						String metauuid = UUID.randomUUID().toString();
						pagemeta= new PageMeta(null, metauuid, pagedetail.getPageuuid(), null, PageMeta.DefaultCss.previewMode.getCode(), null, null);
					}
					pagemeta.setTitle(HtmlEscape.escapeTags(updateValue));
					
					Long pagemetaid = siteDesignDao.savePageMeta(pagemeta);
					if(pagemetaid!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue.trim());
					}
				}else if(updateValueName.equals("headcontent")){
					
					// get pagemeta
					PageMeta pagemeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());
					if(pagemeta==null){ // create pagemeta if null
						String metauuid = UUID.randomUUID().toString();
						pagemeta= new PageMeta(null, metauuid, pagedetail.getPageuuid(), "title", PageMeta.DefaultCss.previewMode.getCode(),null, null);
					}

					String escapedHtmlString = HtmlEscape.escapeTags(updateValue);
					pagemeta.setHeadcontent(escapedHtmlString);
					
					Long pagemetaId = siteDesignDao.savePageMeta(pagemeta);
					if(pagemetaId!=null){
//		        		writePageCssToFile(pagedetail.getId());

						apires.setSuccess(true);
						apires.setResponse1(updateValue);
					}
					
				}
				
				
				if(updated){
					Long pagedetailId = siteDesignDao.savePageDetail(pagedetail);
					if(pagedetailId!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue);
					}
				}
				
			}else{
				apires.setResponse1("You don't have permission to update");
			}
			
		}else{
			apires.setResponse1("Your session could be expired Or no enough information to update pagedetail. (pageUuid: "+pageUuid + "updateValueName: "+updateValueName+")");
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse removeModuleMetaValue(String targetId, String type){
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		ModuleMeta moduleMeta = siteDesignDao.getModuleMetaByTargetUuid(targetId);
		if(moduleMeta!=null){
			MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
			if(metaData!=null){
				// don't remove based on the type:
				if(type.equals("hexcolor")){
					metaData.setHexcolor(null);
					moduleMeta.setMetadata(SitedesignHelper.getXmlFromMetaData(metaData));
					
					Long modulemetaId = siteDesignDao.saveModuleMeta(moduleMeta);
					if(modulemetaId!=null){
						apires.setSuccess(true);
					}else{
						apires.setResponse1("ModuleMeta isn't updated for targetId: "+targetId+", remove type: "+type);
					}
				}
				
			}else{
				apires.setResponse1("System can't find metaData defind in moduleMeta: "+targetId);
			}
			
		}else{
			apires.setResponse1("System can't find ModuleMeta based on targetId: "+targetId);
		}
		
		return apires;
	}
	
	
	@Override
	@Transactional
	public ApiResponse updateModuleMetaValue(String targetId, String updateValueName, String updateValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(targetId) && StringUtils.isNotBlank(updateValueName)){
			
			ModuleMeta moduleMeta = siteDesignDao.getModuleMetaByTargetUuid(targetId);
			if(moduleMeta==null){
				moduleMeta = new ModuleMeta();
				moduleMeta.setUuid(UUID.randomUUID().toString());
				moduleMeta.setTargetuuid(targetId);
			}
			
			// for coloring
			if(updateValueName.equals("hexcolor")){
				
				boolean isValidate = ValidationSet.isValidHexColor("#"+updateValue);
				
				if(isValidate){
					MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
					if(metaData==null) metaData = new MetaData();
					
					metaData.setHexcolor(updateValue.trim());
					
					moduleMeta.setMetadata(SitedesignHelper.getXmlFromMetaData(metaData));
					
					Long modulemetaId = siteDesignDao.saveModuleMeta(moduleMeta);
					if(modulemetaId!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue);
						
					}else{
						apires.setResponse1("ModuleMeta isn't updated for targetId: "+targetId+", updateValueName: "+updateValueName+", updateValue: "+updateValue);
					}
					
				}else{
					apires.setResponse1(updateValue+" hasn't been passed validation");
				}

				
			}
			
		}else{
			apires.setResponse1("targetId and updateValueName can't be empty");
		}
		
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public ModuleMeta getModuleMetaByTargetUuid(String targetuuid) {
		return siteDesignDao.getModuleMetaByTargetUuid(targetuuid);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PageDetail> findOrgPagesByType(String orguuid, Type pagetype) {
		
		Organization org = orgDao.getOrgByUuid(orguuid);
		if(org!=null){
			return siteDesignDao.findOrgPagesByType(org.getId(), pagetype);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ContainerDetail> findPageContainers(String pageuuid) {
		return siteDesignDao.findPageContainerDetails(pageuuid);
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, Boolean> isPageContainersScheduledMap(String pageuuid) {
		List<ContainerDetail> containers = siteDesignDao.findPageContainerDetails(pageuuid);
		if(containers!=null && containers.size()>0){
			Map<String, Boolean> isPageContainersScheduledMap = new HashMap<String, Boolean>();
			for(ContainerDetail c : containers){
				List<ContainerModuleSchedule> cmScheds = siteDesignDao.findContainerModuleSchedulesByContainerUuid(c.getContaineruuid());
				if(cmScheds!=null && cmScheds.size()>0){
					isPageContainersScheduledMap.put(c.getContaineruuid(), Boolean.TRUE);
				}else{
					isPageContainersScheduledMap.put(c.getContaineruuid(), Boolean.FALSE);
				}
			}
			return isPageContainersScheduledMap;
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ApiResponse passUrlValidation(String url, Long orgid, PageDetail.Type type) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		List<PageDetail> pagedetails = siteDesignDao.findOrgPagesByType(orgid, type);
		if(StringUtils.isNotBlank(url)){
			if(ValidationSet.isAlphaNumOnly(url)){
				if(pagedetails!=null && pagedetails.size()>0){
					boolean findsameurl = false;
					for(PageDetail p : pagedetails){
						if(StringUtils.equals(p.getUrl(), url)){
							findsameurl = true;
							break;
						}
					}
					if(findsameurl){
						apires.setResponse1("Same url '"+url+"' is existed in Organization!");
					}else{
						apires.setSuccess(true);
					}
				}
			}else{
				apires.setResponse1("url can only have english characters and numbers!");
			}
		}else{
			apires.setResponse1("url can't be empty!");
		}
		
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ModuleInstanceSchedule> findModuleInstanceSchedulesByInstanceUuid(String instanceUuid) {
		return siteDesignDao.findModuleInstanceSchedulesByInstanceUuid(instanceUuid);
	}

	@Override
	@Transactional
	public Long addToPageChangeList(Long pageid, Long activityLogId, String change) {
		PageDetail pagedetail = siteDesignDao.getPageDetailById(pageid);
		if(pagedetail!=null && StringUtils.isNotBlank(change)){
			
			XStream stream = new XStream(new DomDriver());
			StringWriter sw = new StringWriter();

			Date now = new Date();
			String changeDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(now);
			
			PageMeta pageMeta = siteDesignDao.getPageMetaByPageUuid(pagedetail.getPageuuid());

			// add new changelist
			if(pageMeta!=null){
				if(pageMeta.getChangelist()!=null){
					Map<Long, String> changelist = (Map<Long, String>)stream.fromXML(pageMeta.getChangelist());
					if(changelist==null) changelist = new HashMap<Long, String>();
					changelist.put(activityLogId!=null?activityLogId:-1l, changeDate+": "+change);
					
					stream.marshal(changelist, new CompactWriter(sw));
					pageMeta.setChangelist(sw.toString());
					
				}else{
					Map<Long, String> changelist = new HashMap<Long, String>();
					changelist.put(activityLogId!=null?activityLogId:-1l, changeDate+": "+change);
					
					stream.marshal(changelist, new CompactWriter(sw));
					pageMeta.setChangelist(sw.toString());
				}
				
			}else{
				Map<Long, String> changelist = new HashMap<Long, String>();
				changelist.put(activityLogId!=null?activityLogId:-1l, changeDate+": "+change);
				
				stream.marshal(changelist, new CompactWriter(sw));

				pageMeta = new PageMeta(null, 
						UUID.randomUUID().toString(), 
						pagedetail.getPageuuid(),
						"title",
						PageMeta.DefaultCss.previewMode.getCode(),
						null,
						sw.toString());
			}
			
			siteDesignDao.savePageMeta(pageMeta);
			
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse clonePage(String pageuuid, String destinationFolderUuid) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(false);
		
		PageDetail sourcePageDetail = siteDesignDao.getPageDetailByUuid(pageuuid);
		PageDetail destinationFolder = siteDesignDao.getPageDetailByUuid(destinationFolderUuid);
		
		if(sourcePageDetail!=null && destinationFolder!=null){
			// the map holds all old new uuids for all kinds of detail (pagedetail, containerdetail, ...)
			Map<String, String> oldNewUuids = new HashMap<String, String>();
			
			
			Date now = new Date();
			
			PageDetail clonedDetail = new PageDetail();
			
			String cloneUuid = UUID.randomUUID().toString();
			
			clonedDetail.setCreatedate(now);
			clonedDetail.setCreator_id(Long.valueOf(sourcePageDetail.getCreator_id().longValue()));
			clonedDetail.setOrganization_id(Long.valueOf(destinationFolder.getOrganization_id().longValue()));
			clonedDetail.setPageuuid(cloneUuid);
			clonedDetail.setParentuuid(destinationFolder.getPageuuid());
			
			clonedDetail.setFrom_org_id(sourcePageDetail.getOrganization_id().longValue());
			clonedDetail.setFrom_page_uuid(sourcePageDetail.getPageuuid());
			
			// for path
			StringBuilder newPath = new StringBuilder();
			if(StringUtils.isNotBlank(destinationFolder.getPath())){
				newPath.append(destinationFolder.getPath()).append("/").append(destinationFolder.getPageuuid());
			}else{
				newPath.append(destinationFolder.getPageuuid());
			}
			clonedDetail.setPath(newPath.toString());
			clonedDetail.setPrettyname(sourcePageDetail.getPrettyname());
			clonedDetail.setType(sourcePageDetail.getType());
			clonedDetail.setUrl(UUID.randomUUID().toString());
			
			Long id = siteDesignDao.savePageDetail(clonedDetail);

			// **** for pageMeta ***
			// note: cloned page will not include all new changes
			PageMeta sourcePageMeta = siteDesignDao.getPageMetaByPageUuid(sourcePageDetail.getPageuuid());
			PageMeta clonedPageMeta = null;
			if(sourcePageMeta!=null){
				
				String newPagemetaUuid = UUID.randomUUID().toString();
				
				clonedPageMeta = new PageMeta();
				
				clonedPageMeta.setCss(sourcePageMeta.getCss());
				clonedPageMeta.setDefaultcss(sourcePageMeta.getDefaultcss());
				clonedPageMeta.setHeadcontent(sourcePageMeta.getHeadcontent());
				clonedPageMeta.setPagemetauuid(newPagemetaUuid);
				clonedPageMeta.setPageuuid(cloneUuid);
				clonedPageMeta.setTitle(sourcePageMeta.getTitle());
				
				siteDesignDao.savePageMeta(clonedPageMeta);
			}
			
			
			// **** for container ***
			if(id!=null && StringUtils.isNotBlank(sourcePageDetail.getDetail())){
				clonePageContainer(pageuuid, cloneUuid);
				
				res.setResponse1(id);
			}
			
			
			
		}
		
		return res;
	}

	@Override
	@Transactional
	public ApiResponse clonePageContainer(String pageDetailUuid, String newPageDetailUuid) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(true);
		
		PageDetail pagedetail_source = siteDesignDao.getPageDetailByUuid(pageDetailUuid);
		PageDetail pagedetail_new = siteDesignDao.getPageDetailByUuid(newPageDetailUuid);
		if(pagedetail_source!=null && pagedetail_new!=null){
			
			Date now = new Date();
			AccountDto loginAccount = accountService.getCurrentAccount();
			
			// list of page containerDetails
			List<ContainerDetail> pageContainerDetails_source = siteDesignDao.findPageContainerDetails(pagedetail_source.getPageuuid());
			// list of new page containerDetails
			List<ContainerDetail> pageContainerDetails_new = new ArrayList<ContainerDetail>();
			
			// list of page containertreelevelviews
			List<ContainerTreeLevelView> treeviews_source = siteDesignDao.findContainerTreeLevelViewForPage(pagedetail_source.getPageuuid());
			// list of new containerTreeviews
			List<ContainerTreeLevelView> containerTreeViews_new = new ArrayList<ContainerTreeLevelView>();
			
			// containerNode in pageDetail
			if(StringUtils.isNotBlank(pagedetail_source.getDetail())){
				// get all containers' system ids here (all confirmed containers will be listed here!!)
				List<String> containerUUids_mustCopy = SitedesignHelper.findAllContainerUuidsFromPageDetail(pagedetail_source.getDetail());
				if(containerUUids_mustCopy.size()>0 && pageContainerDetails_source!=null && pageContainerDetails_source.size()>0){
					ContainerDetail rootcontainer = null;
					Map<String, String> oldNewUuids = new HashMap<String, String>(); // hold all old/new uuids for all container.
					
					for(ContainerDetail cd : pageContainerDetails_source){
						if(containerUUids_mustCopy.contains(cd.getContaineruuid())){
							
							String newUuid = UUID.randomUUID().toString();
							oldNewUuids.put(cd.getContaineruuid(), newUuid);
							
							ContainerDetail newcontainer = new ContainerDetail();
							newcontainer.setContaineruuid(newUuid);
							newcontainer.setCreatedate(now);
							newcontainer.setCreator_id(loginAccount.getId());
							newcontainer.setDeletable(cd.getDeletable());
							newcontainer.setDirection(cd.getDirection());
							newcontainer.setEditable(cd.getEditable());
							newcontainer.setHeight(cd.getHeight().intValue());
							newcontainer.setHexColor(cd.getHexColor());
							newcontainer.setLeftposition(cd.getLeftposition().intValue());
							newcontainer.setModuleuuid(cd.getModuleuuid());
							newcontainer.setOrganization_id(pagedetail_new.getOrganization_id().longValue());
							newcontainer.setPageuuid(newPageDetailUuid);
							newcontainer.setParentuuid(cd.getParentuuid()); // will be changed after get all oldnewUuids
							newcontainer.setPath(cd.getPath()); // will be changed after get all oldnewUuids
							newcontainer.setPrettyname(cd.getPrettyname());
							newcontainer.setTopposition(cd.getTopposition().intValue());
							newcontainer.setWidth(cd.getWidth().intValue());
							
							pageContainerDetails_new.add(newcontainer);
							
							// find root
							if(StringUtils.isBlank(cd.getParentuuid())){
								rootcontainer = newcontainer;
							}
						}
					}
					
					// change old uuid to new uuid
					if(oldNewUuids.size()>0 && pageContainerDetails_new.size()>0){
						for(ContainerDetail cd : pageContainerDetails_new){
							// for parentuuid
							if(StringUtils.isNotBlank(cd.getParentuuid())){
								cd.setParentuuid(oldNewUuids.get(cd.getParentuuid()));
							}
							
							// for path
							if(StringUtils.isNotBlank(cd.getPath())){
								String[] paths = cd.getPath().split("/");
								if(paths!=null && paths.length>0){
									StringBuilder newPath = new StringBuilder();
									for(int i=0; i<paths.length; i++){
										if(i>0){
											newPath.append("/");
										}
										newPath.append(oldNewUuids.get(paths[i]));
									}
									cd.setPath(newPath.toString());
								}
							}
						}
					}
					
					// create treeview
					if(rootcontainer!=null && treeviews_source!=null && treeviews_source.size()>0){
						
						for(ContainerTreeLevelView tv : treeviews_source){
							
							if(StringUtils.isNotBlank(tv.getNodes())){
								List<ContainerTreeNode> nodes = TreeHelp.getTreeNodesFromXml(ContainerTreeNode.class, tv.getNodes());
								
								if(nodes!=null && nodes.size()>0){
									List<ContainerTreeNode> nodes_new = new ArrayList<ContainerTreeNode>();
									for(ContainerTreeNode n : nodes){
										if(oldNewUuids.get(n.getSystemName())!=null){
											n.setSystemName(oldNewUuids.get(n.getSystemName()));
											nodes_new.add(n);
										}
									}
									
									if(nodes_new.size()>0 && // must has subnodes
										(StringUtils.isBlank(tv.getParentuuid()) // root 
											|| oldNewUuids.get(tv.getParentuuid())!=null // parentUuid must be in the oldNewUuids map
										)
									){
										String nodes_new_xml = TreeHelp.getXmlFromTreeNodes(nodes_new);
										
										ContainerTreeLevelView tv_new = new ContainerTreeLevelView();
										tv_new.setCreatedate(now);
										tv_new.setCreator_id(loginAccount.getId());
										tv_new.setNodes(nodes_new_xml);
										tv_new.setOrganization_id(pagedetail_new.getOrganization_id().longValue());
										tv_new.setPageuuid(newPageDetailUuid);
										tv_new.setParentuuid(StringUtils.isBlank(tv.getParentuuid())?null:oldNewUuids.get(tv.getParentuuid()));
										
										containerTreeViews_new.add(tv_new);
										
									}
								}
							}
							
						}
					}
					
					
				}
			}
			
			// save all new containerdetails & treeviews
			if(pageContainerDetails_new.size()>0 && containerTreeViews_new.size()>0){
				
				for(ContainerDetail d : pageContainerDetails_new){
					siteDesignDao.saveContainerDetail(d);
				}
				
				for(ContainerTreeLevelView cv : containerTreeViews_new){
					siteDesignDao.saveContainerTreeLevelView(cv);
				}
				
			}
			
			// update the pageDetail's detail
	    	ContainerTreeNode containerTreeNode = treeService.getContaienrTreeRootWithSubNodes(newPageDetailUuid);
	    	if(containerTreeNode!=null){
	    		// assume containerTreeNode and its subnodes are sorted by ContainerTreeNode.positionSort first!!!
	    		containerTreeNodeMarginSpaceGenerator(null, null, containerTreeNode);
	    		containerTreeNodeRelativeWidthHeightGenerator(null, containerTreeNode);
	    		pageDetailGenerator(newPageDetailUuid, containerTreeNode);
	    	}
			
		}
		
		return res;
	}

	@Override
	@Transactional(readOnly=true)
	public List<PageDetail> findPagesByModuleTypeForOrg(Long orgId, ModuleDetail.Type moduleType) {
		if(orgId!=null && moduleType!=null){
			List<PageDetail> orgPages = siteDesignDao.findOrgPagesByType(orgId, PageDetail.Type.Desktop);
			List<PageDetail> orgMobilePages = siteDesignDao.findOrgPagesByType(orgId, PageDetail.Type.Mobile);
			
			if(orgPages==null) orgPages = new ArrayList<PageDetail>();
			if(orgMobilePages!=null && orgMobilePages.size()>0) orgPages.addAll(orgMobilePages);
			
			// loop through all pages to find product pages : page has productModule in using based on schedule or container's default setup.
			if(orgPages!=null && orgPages.size()>0){
				
				// list to hold all moduleType pages : page has moduleType module is using.
				List<PageDetail> moduletypePages = new ArrayList<PageDetail>();
				
				for(PageDetail p : orgPages){
					List<String> containerUuids = SitedesignHelper.findAllContainerUuidsFromPageDetail(p.getDetail());
					
					if(containerUuids!=null && containerUuids.size()>0){
						for(String cuuid : containerUuids){
							
							ContainerDetail containerDetail = siteDesignDao.getContainerDetailByUuid(cuuid);
							if(containerDetail!=null){
								Date now = new Date();
								List<ContainerModuleSchedule> containerModuleSchedules = siteDesignDao.findContainerModuleSchedulesByContainerUuid(cuuid);
								ContainerModuleSchedule currentContainerModuleSchedule = null;
								if(containerModuleSchedules!=null && containerModuleSchedules.size()>0){
									currentContainerModuleSchedule = (ContainerModuleSchedule)getCurrentSchedule(containerModuleSchedules, now);
								}
								
								ModuleDetail moduleDetail = null;
								if(currentContainerModuleSchedule!=null){ // get moduledetail from containerModuleSchedule
									moduleDetail = siteDesignDao.getModuleDetailByUuid(currentContainerModuleSchedule.getModuleuuid());
								}else { // get container's default moduleDetail
									moduleDetail = siteDesignDao.getModuleDetailByUuid(containerDetail.getModuleuuid());
								}
								
								if(moduleDetail!=null && moduleDetail.getType().equals(moduleType.getCode())){
									moduletypePages.add(p);
									break;
								}
								
							}
							
						}
					}
					
				}
				
				if(moduletypePages.size()>0) return moduletypePages;
			}
			
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ContainerModuleSchedule> findContainerModuleSchedulesByModuleUuid(String moduleUuid) {
		return siteDesignDao.findContainerModuleSchedulesByModuleUuid(moduleUuid);
	}

	@Override
	@Transactional(readOnly=true)
	public Pagination getPaginationForNodeDetail(String targetUuid, String hostname, Class nodeClass,
			String folderUuid, int totalNumNodesInPage, int currentPageIdx, String targetPageUuid) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);

		EntityDetail folder = entityDao.getEntityDetailByUuid(folderUuid);
		
		if((targetAccount!=null || targetGroup!=null) && folder!=null){
			
			
			Permission mergedPermission = null;
			if(targetAccount!=null){
				mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
			}else if(targetGroup!=null){
				mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
			}
			
			ApiResponse sqlSegmentResponse = permissionService.generateSqlConditionSegmentForProductList(mergedPermission, folderUuid);
			
			Organization org = orgDao.getOrganizationById(folder.getOrganization_id());
			
			PageDetail targetPage = null;
			if(StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
				targetPage = SitedesignHelper.createFakePage(org);
			}else{
				targetPage = siteDesignDao.getPageDetailByUuid(targetPageUuid);
			}
			if(targetPage!=null){
				
				
				if(totalNumNodesInPage<=0) totalNumNodesInPage = Integer.MAX_VALUE;
				if(currentPageIdx<1) currentPageIdx = 1;
				
				// for entitydetail's pagination.
				if(nodeClass.equals(EntityDetail.class)){
//					EntityTreeLevelView treeview = entityDao.getEntityTreeLevelViewByParentUuid(folderUuid);
//					if(treeview!=nul)
					
					
//					int totalNode = entityDao.countAllProductsUnderCurrentFolderByCurrentFolderUuid(folderUuid, true, null);
					int totalNode = productService.countProductsInFolderBySqlSegmentResponse(sqlSegmentResponse, folderUuid, false, true);
					
					
					
					// default show 10 pages: NOTE: this number must bigger than 2 (>2)!!!
					int DEFAULTNUMBEROFPAGE = 5;
					
					int totalNumberOfPagesToShow = DEFAULTNUMBEROFPAGE;
					
					if(totalNode>totalNumNodesInPage){
						
						int numberOfPages = totalNode / totalNumNodesInPage;
						int remainder = totalNode % totalNumNodesInPage;
						if(remainder>0){
							numberOfPages++;
						}
						
						if(currentPageIdx>numberOfPages){
							currentPageIdx = numberOfPages;
						}
						
						Pagination pagination = new Pagination();
						
						// go to first
						StringBuilder goFirstUrl = new StringBuilder();
						
						if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
							if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
								if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
									goFirstUrl.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=1");
								}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
									goFirstUrl.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=1");
								}
							}else{
								goFirstUrl.append("/page/").append(targetPage.getUrl())
								.append("?categoryid=").append(folderUuid).append("&pageidx=1");
							}
						}else{
							goFirstUrl.append("http://"+applicationConfig.getHostName());
						}
						
						PaginationNode goFirstPageNode = new PaginationNode("<<", goFirstUrl.toString(), "page 1", PaginationNodeType.goFirst, currentPageIdx==1);
						pagination.addPaginationNode(goFirstPageNode);
						
						// go to previous
						StringBuilder goPreviousUrl = new StringBuilder();
						if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
							if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
								if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
									goPreviousUrl.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx<=1?1:(currentPageIdx-1));
								}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
									goPreviousUrl.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx<=1?1:(currentPageIdx-1));
								}
							}else{
								goPreviousUrl.append("/page/").append(targetPage.getUrl())
								.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx<=1?1:(currentPageIdx-1));
							}
							
						}else{
							goPreviousUrl.append("http://"+applicationConfig.getHostName());
						}
						PaginationNode goPreviousPageNode = new PaginationNode("<", goPreviousUrl.toString(), "page "+(currentPageIdx<=1?1:(currentPageIdx-1)), PaginationNodeType.goPrevious, currentPageIdx==(currentPageIdx<=1?1:(currentPageIdx-1)));
						pagination.addPaginationNode(goPreviousPageNode);
						
						
						// check the totalNumberOfPagesToShow again!!
						if(totalNumberOfPagesToShow>numberOfPages) totalNumberOfPagesToShow = numberOfPages;
						
						// check currentPageIdx again!!
						if(currentPageIdx>numberOfPages){
							currentPageIdx = numberOfPages;
						}
						
						// determine which position is the middle position
						int middlePagePosition = totalNumberOfPagesToShow/2 + 1;
						
						// page numbers to show is determined by currentPagePosition & pageIndex
						if(currentPageIdx<=middlePagePosition){ // display from 1 to numberofpages
							
							for(int i=1; i<=totalNumberOfPagesToShow; i++){
								
								// create url based on numberOfPages
								StringBuilder url = new StringBuilder();
								if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
									if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
										if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
											url.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
											.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
										}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
											url.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
											.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
										}
									}else{
										url.append("/page/").append(targetPage.getUrl())
										.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
									}
								}else{
									url.append("http://"+applicationConfig.getHostName());
								}
								
								PaginationNode paginationNode = new PaginationNode(Integer.toString(i), url.toString(), "page "+i, PaginationNodeType.number, currentPageIdx==i);
								pagination.addPaginationNode(paginationNode);
							}
							
						}else{ // display from (currentPageIdx-totalNumberOfPagesToShow/2) to (currentPageIdx+totalNuberOfPagesToShow/2-1)
							
							int toIdx = currentPageIdx + middlePagePosition - 1;
							if(totalNumberOfPagesToShow%2==0){
								toIdx = toIdx - 1;
							}
							// in case that currentPage's position beyond middlePagePosition, ie: 1 2 3 [4 5 (6) 7] 8 : leftswitchNum=0 -> 1 2 3 4 [5 6 (7) 8] : leftswitchNum=0 -> 1 2 3 4 [5 6 7 (8)] : leftswitchNum=1 
							int leftswitchNum = (numberOfPages-toIdx>=0)?0:(toIdx-numberOfPages);
							toIdx = toIdx - leftswitchNum;
							
							int fromIdx = currentPageIdx - middlePagePosition + 1;
							fromIdx = fromIdx - leftswitchNum;
							
							for(int i=fromIdx; i<=toIdx; i++){
								
								// create url based on numberOfPages
								StringBuilder url = new StringBuilder();
								if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
									if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
										if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
											url.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
											.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
										}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
											url.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
											.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
										}
									}else{
										url.append("/page/").append(targetPage.getUrl())
										.append("?categoryid=").append(folderUuid).append("&pageidx=").append(i);
									}
									
								}else{
									url.append("http://"+applicationConfig.getHostName());
								}
								
								PaginationNode paginationNode = new PaginationNode(Integer.toString(i), url.toString(), "page "+i, PaginationNodeType.number, currentPageIdx==i);
								pagination.addPaginationNode(paginationNode);
							}
						}
						
						// go to next
						StringBuilder goNextUrl = new StringBuilder();
						if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
							if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
								if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
									goNextUrl.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx>=numberOfPages?numberOfPages:(currentPageIdx+1));
								}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
									goNextUrl.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx>=numberOfPages?numberOfPages:(currentPageIdx+1));
								}
							}else{
								goNextUrl.append("/page/").append(targetPage.getUrl())
								.append("?categoryid=").append(folderUuid).append("&pageidx=").append(currentPageIdx>=numberOfPages?numberOfPages:(currentPageIdx+1));
							}
							
						}else{
							goNextUrl.append("http://"+applicationConfig.getHostName());
						}
						PaginationNode goNextPageNode = new PaginationNode(">", goNextUrl.toString(), "page "+(currentPageIdx>=numberOfPages?numberOfPages:(currentPageIdx+1)), PaginationNodeType.goNext, currentPageIdx==(currentPageIdx>=numberOfPages?numberOfPages:(currentPageIdx+1)));
						pagination.addPaginationNode(goNextPageNode);
						
						// go to last
						StringBuilder goLastUrl = new StringBuilder();
						if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
							if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
								if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
									goLastUrl.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(numberOfPages);
								}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
									goLastUrl.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=").append(numberOfPages);
								}
							}else{
								goLastUrl.append("/page/").append(targetPage.getUrl())
								.append("?categoryid=").append(folderUuid).append("&pageidx=").append(numberOfPages);
							}
						}else{
							goLastUrl.append("http://"+applicationConfig.getHostName());
						}
						PaginationNode goLastPageNode = new PaginationNode(">>", goLastUrl.toString(), "page "+numberOfPages, PaginationNodeType.goLast, currentPageIdx==numberOfPages);
						pagination.addPaginationNode(goLastPageNode);
						
						
						// for view all link
						StringBuilder viewAllUrl = new StringBuilder();
						if(!StringUtils.equals(targetPageUuid, PageDetail.FAKEPAGEUUID)){
							if(StringUtils.isBlank(hostname) || hostname.trim().equals(applicationConfig.getHostName().trim())){
								if(targetPage.getType().equals(PageDetail.Type.Desktop.getCode())){
									viewAllUrl.append("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=0");
								}else if(targetPage.getType().equals(PageDetail.Type.Mobile.getCode())){
									viewAllUrl.append("/getMobilePage/org/").append(org.getOrguuid()).append("/pageurl/").append(targetPage.getUrl())
									.append("?categoryid=").append(folderUuid).append("&pageidx=0");
								}
							}else{
								viewAllUrl.append("/page/").append(targetPage.getUrl())
								.append("?categoryid=").append(folderUuid).append("&pageidx=0");
							}
						}else{
							viewAllUrl.append("http://"+applicationConfig.getHostName());
						}
						pagination.setViewAllUrl(viewAllUrl.toString());
						
						// for extraInfo
						StringBuilder paginationExtraInfo = new StringBuilder("showing ");
						int fromNodeNumber = (currentPageIdx - 1) * totalNumNodesInPage + 1;
						int toNodeNumber = currentPageIdx * (totalNumNodesInPage==Integer.MAX_VALUE?totalNode:totalNumNodesInPage);
						paginationExtraInfo.append(fromNodeNumber);
						if(toNodeNumber<=totalNode){
							paginationExtraInfo.append("-").append(toNodeNumber);
						}
						paginationExtraInfo.append(" of ").append(totalNode);
						pagination.setExtraInfo(paginationExtraInfo.toString());
						
						return pagination;
						
					}
					
				}
				
				else if(nodeClass.equals(MediaDetail.class)){
					// todo media's pagination
				}
				
			}
			
		}
		
		return null;
	}

	@Override
	public String getRandomCategoryPageUuid(Long orgId) {
		
		
		return null;
	}

	@Override
	public String getRandomProductPageUuid(Long orgId) {
		
		
		return null;
	}

	@Override
	public void generateContainerPureUnitMap(ContainerTreeNode container, Map<String, String> containerPureUnitMap) {
		
		if(container!=null && containerPureUnitMap!=null){
			
			if(StringUtils.equals(container.getDirection(), "0")){
				if(container.getSubnodes()!=null && container.getSubnodes().size()>0){
					containerPureUnitMap.put(container.getSystemName(), "pure-g-r pure-u-1");
				}else{
					containerPureUnitMap.put(container.getSystemName(), "pure-u-1");
				}
			}else{
				Double relativeWidth = container.getRelativeWidth();
				if(relativeWidth!=null){
					long pureUnit = Math.round(relativeWidth.doubleValue()*24/100);
					StringBuilder pureUnitString = new StringBuilder();
					if(pureUnit==24d){
						pureUnitString.append("pure-u-1");
					}else{
						pureUnitString.append("pure-u-").append(pureUnit).append("-24");
					}
					
					if(container.getSubnodes()!=null && container.getSubnodes().size()>0){
						containerPureUnitMap.put(container.getSystemName(), "pure-g-r " + pureUnitString.toString());
					}else{
						containerPureUnitMap.put(container.getSystemName(), pureUnitString.toString());
					}
					
				}
			}
			
			if(container.getSubnodes()!=null && container.getSubnodes().size()>0){
				for(ContainerTreeNode n : container.getSubnodes()){
					generateContainerPureUnitMap(n, containerPureUnitMap);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public int countAllPagesInOrg(Long orgId, Type type) {
		return siteDesignDao.countAllPagesInOrg(orgId, type);
	}

	@Override
	@Transactional(readOnly=true)
	public int countAllModuleDetailInOrg(Long orgId) {
		return siteDesignDao.countAllModuleDetailInOrg(orgId);
	}

	@Override
	@Transactional(readOnly=true)
	public int countAllModuleInstanceInOrg(Long orgId) {
		return siteDesignDao.countAllModuleInstanceInOrg(orgId);
	}

	@Override
	@Transactional(readOnly=true)
	public int countAllProductInOrgInOrg(Long orgId) {
		return siteDesignDao.countAllProductInOrgInOrg(orgId);
	}

	@Override
	@Transactional
	public ApiResponse moduleDetailAttributesSort(String moduleDetailUuid, String groupUuid, String attriUuid, int position) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		if(loginAccount!=null){
			boolean isPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleDetailUuid);
			
			if(isPermissionAllowed){
				ModuleDetail moduleDetail = siteDesignDao.getModuleDetailByUuid(moduleDetailUuid);
				if(moduleDetail!=null){
					if(moduleDetail.getXml()!=null && moduleDetail.getXml().indexOf(groupUuid)>-1 && moduleDetail.getXml().indexOf(attriUuid)>-1){
						Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
						int relocateAttrIdx = -1;
						for(AttrGroup g : module.getAttrGroupList()){
							if(g.getAttrList()!=null && g.getGroupUuid().equals(groupUuid)){
								for(int i=0; i<g.getAttrList().size(); i++){
									if(g.getAttrList().get(i).getUuid().equals(attriUuid)){
										relocateAttrIdx = i;
										break;
									}
								}
								if(relocateAttrIdx>-1){
									ModuleAttribute relocateAttr = g.getAttrList().get(relocateAttrIdx);
									g.getAttrList().remove(relocateAttrIdx);
									g.getAttrList().add(position-1, relocateAttr);
									
									//Collections.swap(g.getAttrList(), relocateAttrIdx, position-1);
								}
								break;
							}
						}
						
						if(relocateAttrIdx>-1){
							String updatedXml = SitedesignHelper.getXmlFromModule(module);
							moduleDetail.setXml(updatedXml);
							siteDesignDao.saveModuleDetail(moduleDetail);
							apires.setSuccess(true);
						}
						
						
					}else{
						apires.setResponse1("System can't find attrgroup or attribute in module: (moduleid: "+moduleDetailUuid+", groupid: "+groupUuid+", attrid: "+attriUuid+").");
					}
				}else{
					apires.setResponse1("System can't find moduledetail by id : "+moduleDetailUuid);
				}
			}else{
				apires.setResponse1("User " + loginAccount.getLoginname() + " doesn't have permission to modify this module (" + moduleDetailUuid +" )");
			}
			
		}else {
			apires.setResponse1("System can't find login user information, you may need to login again");
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse newInstanceAttr(String moduleUuid,
			String moduleGroupUuid, String moduleAttrUuid, String instanceUuid) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		if(loginAccount!=null){
			boolean editPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleUuid);
			if(editPermissionAllow){
				ModuleDetail moduledetail = siteDesignDao.getModuleDetailByUuid(moduleUuid);
				if(moduledetail!=null){
					Module module = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
					if(module!=null){
						AttrGroup moduleGroup = module.getGroupByUuid(moduleGroupUuid);
						ModuleAttribute moduleAttr = module.getModuleAttributeByUuid(moduleAttrUuid);
						if(moduleGroup!=null && moduleAttr!=null){
							
							ModuleInstance moduleInstance = siteDesignDao.getModuleInstanceByUuid(instanceUuid);
							EntityDetail entityDetail = entityDao.getEntityDetailByUuid(instanceUuid);
							if(moduleInstance!=null || entityDetail!=null){
								
								String instanceModuleXmlString = null;
								if(moduleInstance!=null){
									instanceModuleXmlString = moduleInstance.getInstance();
								}else if(entityDetail!=null){
									instanceModuleXmlString = entityDetail.getDetail();
								}
								
								Module instanceModule = SitedesignHelper.getModuleFromXml(instanceModuleXmlString);
								
								
								// check attribute exist or not,
								// and find which group should this new attribute in:
								boolean attributeExist = false;
								AttrGroup instanceGroupToHoldNewAttr = null;
								if(instanceModule!=null && instanceModule.getAttrGroupList()!=null){
									for(AttrGroup instanceGroup : instanceModule.getAttrGroupList()){
										if(StringUtils.equals(instanceGroup.getModuleGroupUuid(), moduleGroupUuid)){
											instanceGroupToHoldNewAttr = instanceGroup;
										}
										
										if(instanceGroup.getAttrList()!=null){
											for(ModuleAttribute instanceAttr : instanceGroup.getAttrList()){
												if(StringUtils.equals(instanceAttr.getModuleAttrUuid(), moduleAttrUuid)){
													attributeExist = true;
													break;
												}
											}
										}
										if(attributeExist) break;
									}
								}
								
								if(!attributeExist){
									ModuleAttribute newAttr = SitedesignHelper.getDefaultModuleAttr(moduleAttr);
									
									if(instanceGroupToHoldNewAttr!=null){ // add new attr into this group
										instanceGroupToHoldNewAttr.addAttr(newAttr);
									}else{ // create a new group and add new attr in
										if(instanceModule==null){
											instanceModule = new Module();
										}
										
										AttrGroup newAttrGroup = new AttrGroup();
										newAttrGroup.setGroupUuid(UUID.randomUUID().toString());
										newAttrGroup.setModuleGroupUuid(moduleGroupUuid);
										newAttrGroup.addAttr(newAttr);
										instanceModule.addAttrGroup(newAttrGroup);
									}
									
									String updatedInstanceModuleXml = SitedesignHelper.getXmlFromModule(instanceModule);
									
									Long id = null;
									if(moduleInstance!=null){
										moduleInstance.setInstance(updatedInstanceModuleXml);
										id = siteDesignDao.saveModuleInstance(moduleInstance);
									}else if(entityDetail!=null){
										entityDetail.setDetail(updatedInstanceModuleXml);
										id = entityDao.saveEntityDetail(entityDetail);
									}
									
									if(id!=null){
										apires.setSuccess(true);
									}else{
										apires.setResponse1("System get exception during the saving process, try again late.");
									}
								}else{
									apires.setResponse1("ModuleDetail's attr ("+moduleAttrUuid+") already been used in instance ("+instanceUuid+")");
								}
								
							}else{
								apires.setResponse1("System can't find instance (include entityDetail) by id: "+instanceUuid); 
							}
						}else{
							apires.setResponse1("System can't find group ("+moduleGroupUuid+") and/or attribute ("+moduleAttrUuid+") in moduleDetail ("+moduleUuid+")");
						}
					}else{
						apires.setResponse1("System can't get module from moduleDetail by id: "+ moduleUuid);
					}
				}else{
					apires.setResponse1("System can't find moduleDetail by id: "+moduleUuid);
				}
				 
			}else{
				apires.setResponse1("User ("+loginAccount.getLoginname()+") doesn't have permission to edit this instance.");
			}
		}else{
			apires.setResponse1("You may need to login again!");
		}
		
		return apires;
	}

//	@Override
//	@Transactional
//	public ApiResponse cloneContainer(String containerUuid) {
//		ApiResponse res = new ApiResponse();
//		res.setSuccess(false);
//		
//		ContainerDetail containerdetail = siteDesignDao.getContainerDetailByUuid(containerUuid);
//		
//		if(containerdetail!=null){
//			String cloneUuid = UUID.randomUUID().toString();
//
//			ContainerDetail clonedDetail = new ContainerDetail();
//			clonedDetail.setContaineruuid(cloneUuid);
//			/// ....
//			/// ....
//			/// ....
//			/// ....
//			
//			// create containertreenode
//			ContainerTreeNode treeNode = new ContainerTreeNode();
////			treeNode.set...
//			
//			
//			List<ContainerDetail> subContainers = siteDesignDao.findSubContainerDetails(containerUuid);
//			if(subContainers!=null && subContainers.size()>0){
//				for(ContainerDetail cd : subContainers){
//					ApiResponse subres = cloneContainer(cd.getContaineruuid());
//					
//					if(subres!=null){
//						
//					}
//					
//				}
//			}
//			
//		}
//		
//		return res;
//	}


}
