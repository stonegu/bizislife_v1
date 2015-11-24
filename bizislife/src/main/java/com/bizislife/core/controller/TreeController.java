package com.bizislife.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bizislife.core.controller.component.*;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.service.*;
import com.bizislife.core.view.dto.*;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Controller
public class TreeController {
	private static final Logger logger = LoggerFactory.getLogger(TreeController.class);

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private MediaService mediaService;
    
    @Autowired
    private Jaxb2Marshaller jaxbMarshaller;

    
	@RequestMapping(value="/leftNavMain")
	public @ResponseBody List<JsTreeNode> leftNavTree (
			
			@RequestParam(value = "operation", required = false) String operation,
//			@RequestParam(value = "title", required = false) String title,
//			@RequestParam(value = "type", required = false) String type,
//			@RequestParam(value = "parentsIds[]", required = false) String[] parentsIds,
//			
//			@RequestParam(value = "originalPosition[]", required = false) String[] originalPositionIds,
//			@RequestParam(value = "targetPosition[]", required = false) String[] targetPositionIds,
			
			
			HttpServletResponse response, HttpServletRequest request){
		
		JsTreeNode node = new JsTreeNode();
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		if(operation.equals("get_leftTree")){
			
			// read xml and unmarshal to the LeftNavNode
			FileInputStream inputStream;
			try {
				// unmarshal
				if(request.getSession()!=null){
					String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF");
					
					inputStream = new FileInputStream(new File(new StringBuilder(realPath).append("/xml/oxm/leftNavMain.xml").toString()));
					StreamSource source = new StreamSource(inputStream);
					LeftNavNode left = (LeftNavNode)jaxbMarshaller.unmarshal(source);
					
					node = left.switchToJsTreeNode("", null, null);
					
					nodes = node.getChildren();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(operation.equals("get_organizations")){
			AccountDto currentAccount = accountService.getCurrentAccount();
			if(currentAccount!=null){
				FileInputStream inputStream;

				String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF");
				try {
					inputStream = new FileInputStream(new File(new StringBuilder(realPath).append("/xml/oxm/organizationTree.xml").toString()));
					StreamSource source = new StreamSource(inputStream);
					LeftNavNode left = (LeftNavNode)jaxbMarshaller.unmarshal(source);
					
					// set addOrg node
					if(currentAccount.isBizAccount() && currentAccount.isSystemDefaultAccount()){
						LeftNavNode addorgNode = new LeftNavNode();
						addorgNode.setPrettyName("Add Organization");
						addorgNode.setSystemName("addOrganization");
						addorgNode.setUrl("/addOrganization");
						nodes.add(addorgNode.switchToJsTreeNode("", null, currentAccount));
					}
					
					// get all org based on account
					//List<Organization> orgs = accountService.getOrgsByAccount(currentAccount.getId());
					List<Organization> orgs = null;
					
					if(currentAccount.isBizAccount()){
						orgs = accountService.findAllOrgs();
					}else{
						Organization currentOrg = accountService.getOrgById(currentAccount.getOrganization_id());
						orgs = new ArrayList<Organization>();
						orgs.add(currentOrg);
					}
					// create all orgs' nodes
					if(orgs!=null && orgs.size()>0){
						for(Organization org : orgs){
							
							LeftNavNode orgNode = new LeftNavNode();
							orgNode.setPrettyName(org.getOrgsysname());
							orgNode.setSystemName(org.getOrguuid());
							orgNode.setUrl(new StringBuilder("/getOrgInfo?org=").append(org.getOrguuid()).toString());
							if(left!=null){
								// clone left
								LeftNavNode ln = left.clone();
								ln.attachSuffixToSystemName(org.getOrguuid());
								ln.attachParamsToUrl(new StringBuilder("org=").append(org.getOrguuid()).toString());
								orgNode.setSubnodes(ln.getSubnodes());
								nodes.add(orgNode.switchToJsTreeNode("", null, currentAccount));
							}
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return nodes;
	}
    
    
    
	@RequestMapping(value="/accountNav")
	public @ResponseBody JsTreeNode accountNav (
			
			@RequestParam(value = "operation", required = false) String operation,
			
			HttpServletResponse response, HttpServletRequest request){
		
		JsTreeNode node = new JsTreeNode();
		
		if(operation.equals("get_accountTree")){

			AccountDto currentAccount = accountService.getCurrentAccount();
			if(currentAccount!=null){
				// read xml and unmarshal to the LeftNavNode
				FileInputStream inputStream;
				try {
					// unmarshal
					if(request.getSession()!=null){
						String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF");
						inputStream = new FileInputStream(new File(new StringBuilder(realPath).append("/xml/oxm/accountTree.xml").toString()));
						StreamSource source = new StreamSource(inputStream);
						LeftNavNode left = (LeftNavNode)jaxbMarshaller.unmarshal(source);
						if(left!=null){
							left.setSystemName(currentAccount.getAccountuuid());
							left.setPrettyName(currentAccount.getLoginname());
							left.setUrl("/getAcctInfo?acct="+currentAccount.getAccountuuid());
							
							// set all subnode's info ...
							if(left.getSubnodes()!=null && left.getSubnodes().size()>0){
								for(LeftNavNode sn : left.getSubnodes()){
									if("accountSettings".equals(sn.getSystemName())){
										sn.setUrl("/getAcctInfo?acct="+currentAccount.getAccountuuid());
										sn.setSystemName("accountSettings_"+currentAccount.getAccountuuid());
									}
								}
							}
							
							node = left.switchToJsTreeNode("", null, null);
							// replace root with loginAccount info
//							if(node!=null){
//								node.getAttr().setId(currentAccount.getAccountuuid());
//								node.getData().setTitle(currentAccount.getLoginname());
//							}
						}
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				return null;
			}
			
			
		}
		
		return node;
	}
    
	
	@RequestMapping(value="/topicTreeMain")
	public @ResponseBody List<JsTreeNode> topicTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			
			HttpServletResponse response, HttpServletRequest request){
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_topicTree") && currentAccount!=null){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
		    		List<Subscribe> subscribes = null;
		    		if(currentAccount!=null){
		    			subscribes = messageService.findAccountSubscribes(currentAccount.getId());
		    		}
		    		String[] checkedList = null;
		    		if(subscribes!=null && subscribes.size()>0){
		    			checkedList = new String[subscribes.size()];
		    			for(int i=0; i<subscribes.size(); i++){
		    				checkedList[i] = subscribes.get(i).getTopicuuid();
		    			}
		    		}

					if(StringUtils.isBlank(parentNodeUuid)){
						
						TopicTreeNode rootNode = treeService.getTopicTreeRoot(org.getId());
						if(rootNode!=null){
							
							Topic topic = messageService.getTopicByUuid(rootNode.getSystemName());
							if(topic!=null){
								
								boolean editable = false;
								if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && currentAccount.getOrganization_id().equals(topic.getOrg_id())) 
									editable = true;
								
								treeService.updateTopicTreeNode(rootNode, editable);
								JsTreeNode node = rootNode.switchToJsTreeNode("", checkedList, currentAccount);
					    		// apply subscribable for the node and it's subnodes
					    		messageService.applySubscribable(node);
								nodes.add(node);
							}
							
						}
					}else{
						// get parent info
						Topic parentTopic = messageService.getTopicByUuid(parentNodeUuid);
						List<TopicTreeNode> topicTreeNodes = treeService.findTopicTreeNodesByParentNodeUuid(parentNodeUuid);
						
						boolean editable = false;
						if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && currentAccount.getOrganization_id().equals(parentTopic.getOrg_id())) 
							editable = true;
						
						for(TopicTreeNode n : topicTreeNodes){
							treeService.updateTopicTreeNode(n, editable);
							JsTreeNode node = n.switchToJsTreeNode(parentTopic.getTopicroute(), checkedList, currentAccount);
				    		// apply subscribable for the node and it's subnodes
				    		messageService.applySubscribable(node);
							nodes.add(node);
						}
					}
				}
			}
			
		}
		
		return nodes;
		
	}

	
	@RequestMapping(value="/topicTreeMain_old")
	public @ResponseBody JsTreeNode topicTreeMain_old (
			
			@RequestParam(value = "operation", required = false) String operation,
			
			
			HttpServletResponse response, HttpServletRequest request){

		JsTreeNode node = new JsTreeNode();
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_topicTree")){
	    	TopicTreeNode topicTree = treeService.getTopicTree();
	    	
	    	if(topicTree!=null){
	    		List<Subscribe> subscribes = null;
	    		if(currentAccount!=null){
	    			subscribes = messageService.findAccountSubscribes(currentAccount.getId());
	    		}
	    		String[] checkedList = null;
	    		if(subscribes!=null && subscribes.size()>0){
	    			checkedList = new String[subscribes.size()];
	    			for(int i=0; i<subscribes.size(); i++){
	    				checkedList[i] = subscribes.get(i).getTopicuuid();
	    			}
	    		}
	    		
	    		node = topicTree.switchToJsTreeNode("", checkedList, currentAccount);
	    		// apply subscribable for the node and it's subnodes
	    		messageService.applySubscribable(node);
	    		
	    		return node;
	    	}
			
		}
		
		return node;
		
	}
		
	@RequestMapping(value="/productTreeMain")
	public @ResponseBody List<JsTreeNode> productTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			@RequestParam(value = "isViewInclude", required = false) Boolean isViewInclude,
			@RequestParam(value = "isPermissionInclude", required = false) Boolean isPermissionInclude, // true: include permission selection checkbox beside node name
			@RequestParam(value = "isPermissionFilterNeeded", required = false) Boolean isPermissionFilterNeeded, // true: will filter the nodes based on currentAccount.mergedPermission
			@RequestParam(value = "permissionUuid", required = false) String permissionUuid,
			
			HttpServletResponse response, HttpServletRequest request){

		AccountDto currentAccount = accountService.getCurrentAccount();
		
		Permission permissionDto = permissionService.getPermissionDto(permissionUuid);
		// determine what permission.types should be in the selectable list
		Map<Permission.Type, Boolean> selectablePermissionTypeMap = new HashMap<Permission.Type, Boolean>();
		if(permissionDto!=null && permissionDto.getAccount_id()!=null){
			for(Permission.Type ptype : Permission.Type.values()){
				selectablePermissionTypeMap.put(ptype, true);
			}
		}else if(permissionDto!=null && permissionDto.getGroup_id()!=null){
			// Accountgroup.GroupAccessLevel = Global will only have preview and copy selections
			Accountgroup group = accountService.getGroup(permissionDto.getGroup_id());
			if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode())){
				selectablePermissionTypeMap.put(Permission.Type.preview, true);
				selectablePermissionTypeMap.put(Permission.Type.copy, true);
			}else if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Private.getCode())){
				for(Permission.Type ptype : Permission.Type.values()){
					selectablePermissionTypeMap.put(ptype, true);
				}
			}
		}
		
		
		if(isViewInclude==null) isViewInclude = true;
		if(isPermissionInclude==null) isPermissionInclude = false;
		if(isPermissionFilterNeeded==null) isPermissionFilterNeeded = true;
		
		Permission currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);

		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		JsTreeNode node = new JsTreeNode();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_productTree")){
			
			
			
			
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						
						EntityTreeNode entityTreeNode = treeService.getProductTreeRoot(org.getId());
						
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(entityTreeNode.getSystemName(), currentAccountMergedPermission);
						}else{
							currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						

						if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							// add more info to entityTreeNode based on EntityDetail
							treeService.updateEntityTreeNodeForProduct(entityTreeNode, currentNodePermissionedStuffs_forMerged, null, currentAccountMergedPermission);
							// add permission sets for node
							if(isPermissionInclude){
								permissionService.updateTreeNodeForPermission(EntityDetail.class, entityTreeNode, permissionDto, null, selectablePermissionTypeMap);
							}
							
							if(entityTreeNode!=null){
								node = entityTreeNode.switchToJsTreeNode(null, null, null);
								nodes.add(node);
							}
						}
						
					}else{
						
						// the node path
						String path = null;
						EntityDetail parentDetail = productService.getEntityDetailByUuid(parentNodeUuid);
						if(parentDetail!=null){
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getEntityuuid();
							}else{
								path = parentDetail.getEntityuuid();
							}
						}
						Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
						}else{
							closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						// find all views for the entity based on closestParentNodePermissionedStuff
						if(isViewInclude){
							
//							if(!isPermissionFilterNeeded || 
//									(
//											// only the permission-modify allowed can have the views
//											closestParentNodePermissionedStuffs_forMerged!=null && 
//											(closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview)==null || (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode()))) 
//													&& (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read)==null || (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())))
//													&& (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.modify)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode()))
//									)
//								){
//								
//							}
							
							
							List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(parentNodeUuid);
							if(views!=null && views.size()>0){
								
								for(InstanceView v : views){
									if(isPermissionFilterNeeded){
										treeService.updateInstanceViewForTreeNode(v, parentNodeUuid);
									}else{
										treeService.updateInstanceViewForTreeNode(v, null);
									}
									
									nodes.add(v.switchToJsTreeNode(null, null, null));
								}
							}
							
						}
						
//						List<EntityTreeNode> entityTreeNodes = treeService.findProductTreeNodesByParentNodeUuid(parentNodeUuid);
						
						List<TreeNode> entityTreeNodes = treeService.findTreeNodesByParentNodeUuid_v2(currentAccount.getAccountuuid(), 
								EntityDetail.class, parentNodeUuid, true, true, isPermissionFilterNeeded);
						
						if(entityTreeNodes!=null){
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuff = permissionService.findClosestParentNodePermissionedStuff(path, permissionDto);

							for(TreeNode n : entityTreeNodes){
								
								Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
								if(isPermissionFilterNeeded){
									currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
								}else{
									currentNodePermissionedStuffs_forMerged = null;
								}
								
								treeService.updateEntityTreeNodeForProduct((EntityTreeNode)n, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged, currentAccountMergedPermission);
								// add permission sets for node
								if(isPermissionInclude){
									permissionService.updateTreeNodeForPermission(EntityDetail.class, 
											n, 
											permissionDto, 
											closestParentNodePermissionedStuff,
											selectablePermissionTypeMap);
								}
								
								nodes.add(n.switchToJsTreeNode(null, null, null));
								
							}
						}
					}
				}
			}
			
		}else if(operation.equals("get_schedForView")){
			List<InstanceViewSchedule> viewScheds = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(parentNodeUuid);
			if(viewScheds!=null){
				for(InstanceViewSchedule s : viewScheds){
					
					if(isPermissionFilterNeeded){
						EntityDetail entitydetail = productService.getEntityDetailByUuid(s.getModuleinstanceuuid());
						if(entitydetail!=null){
							treeService.updateViewScheduleForTreeNode(s, entitydetail.getEntityuuid());
						}
						
					}else{
						
					}
					
					nodes.add(s.switchToJsTreeNode(null, null, null));
				}
			}
			
			
		}
		
		Collections.sort(nodes, JsTreeNode.viewFirstFolderSecondLeafLastComparator);
		return nodes;
		
	}
	
	@RequestMapping(value="/mediaTreeMain")
	public @ResponseBody List<JsTreeNode> mediaTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			@RequestParam(value = "isPermissionInclude", required = false) Boolean isPermissionInclude,
			@RequestParam(value = "isPermissionFilterNeeded", required = false) Boolean isPermissionFilterNeeded, // true: will filter the nodes based on currentAccount.mergedPermission
			@RequestParam(value = "permissionUuid", required = false) String permissionUuid, // will be passed to here from permission setup page for account and group
			
			@RequestParam(value = "folderOnly", required = false) Boolean folderOnly,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(folderOnly==null) folderOnly = false;
		
		if(isPermissionInclude==null) isPermissionInclude = false;
		if(isPermissionFilterNeeded==null) isPermissionFilterNeeded = true;
		Permission currentAccountMergedPermission = null;
		if(isPermissionFilterNeeded){
//			currentAccountMergedPermission = currentAccount.getMergedPermission();
			currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
		}
		
		
		Permission permissionDto = permissionService.getPermissionDto(permissionUuid);
		// determine what permission.types should be in the selectable list
		Map<Permission.Type, Boolean> selectablePermissionTypeMap = new HashMap<Permission.Type, Boolean>();
		if(permissionDto!=null && permissionDto.getAccount_id()!=null){
			
			selectablePermissionTypeMap.put(Permission.Type.preview, true);
			selectablePermissionTypeMap.put(Permission.Type.modify, true);
			
		}else if(permissionDto!=null && permissionDto.getGroup_id()!=null){
			// Accountgroup.GroupAccessLevel = Global will only have preview and copy selections
			Accountgroup group = accountService.getGroup(permissionDto.getGroup_id());
			if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode())){
				selectablePermissionTypeMap.put(Permission.Type.preview, true);
			}else if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Private.getCode())){
				selectablePermissionTypeMap.put(Permission.Type.preview, true);
				selectablePermissionTypeMap.put(Permission.Type.modify, true);
			}
		}
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		JsTreeNode node = new JsTreeNode();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_mediaTree") || operation.equals("get_imageTree")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						MediaTreeNode mediaTreeNode = treeService.getMediaTreeRoot(org.getId());
						
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(mediaTreeNode.getSystemName(), currentAccountMergedPermission);
						}else{
							currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							
							// add more info to entityTreeNode based on EntityDetail
							treeService.updateMediaTreeNodeForMedia(mediaTreeNode, currentNodePermissionedStuffs_forMerged, null);
							
							if(isPermissionInclude){
								permissionService.updateTreeNodeForPermission(MediaDetail.class, mediaTreeNode, permissionDto, null, selectablePermissionTypeMap);
							}
							
							
							if(mediaTreeNode!=null){
								
								if(operation.equals("get_mediaTree")){
									node = mediaTreeNode.switchToJsTreeNode(null, null, null);
								}else if(operation.equals("get_imageTree")){
									node = mediaTreeNode.switchToJsTreeNode_type(MediaType.image);
								}
								
								nodes.add(node);
							}
						}
						
					}else{
						
						
						// the node path
						String path = null;
						MediaDetail parentDetail = mediaService.getMediaDetailByUuid(parentNodeUuid);
						
						if(parentDetail!=null){
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getMediauuid();
							}else{
								path = parentDetail.getMediauuid();
							}
						}
						Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
						}else{
							closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						
						
						
						// old version for test:
//						long start_findNodes_old = System.nanoTime();
//						List<MediaTreeNode> mediaTreeNodes_old = treeService.findMediaTreeNodesByParentNodeUuid(parentNodeUuid);
//						long end_findNodes_old = System.nanoTime();
//						System.out.printf("Took %.3f seconds to find nodes in old version%n", (end_findNodes_old - start_findNodes_old) / 1e9);
						
						
						
						
						//long start_findNodes = System.nanoTime();
						List<TreeNode> mediaTreeNodes = treeService.findTreeNodesByParentNodeUuid_v2(currentAccount.getAccountuuid(), MediaDetail.class, parentNodeUuid, true, !folderOnly, isPermissionFilterNeeded);
//						long end_findNodes = System.nanoTime();
//						System.out.printf("Took %.3f seconds to find nodes%n", (end_findNodes - start_findNodes) / 1e9);
						
						if(mediaTreeNodes!=null && mediaTreeNodes.size()>0){
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuff = permissionService.findClosestParentNodePermissionedStuff(path, permissionDto);

							long start_updateNodes = System.nanoTime();
							for(TreeNode tnode : mediaTreeNodes){
								MediaTreeNode n = (MediaTreeNode)tnode;
								
								Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
								if(isPermissionFilterNeeded){
									currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
								}else{
									currentNodePermissionedStuffs_forMerged = null;
								}
								
								treeService.updateMediaTreeNodeForMedia_v2(n, false, folderOnly, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
								
								if(isPermissionInclude){
									permissionService.updateTreeNodeForPermission(MediaDetail.class, n, permissionDto, closestParentNodePermissionedStuff, selectablePermissionTypeMap);
								}
								
								if(n.getType().equals(MediaDetail.MediaType.folder) || (!n.getType().equals(MediaDetail.MediaType.folder) && !folderOnly)){
									if(operation.equals("get_mediaTree")){
										node = n.switchToJsTreeNode(null, null, null);
									}else if(operation.equals("get_imageTree")){
										node = n.switchToJsTreeNode_type(MediaType.image);
									}
									nodes.add(node);
								}
								
							}
							long end_updateNodes = System.nanoTime();
							System.out.printf("Took %.3f seconds to update nodes%n", (end_updateNodes - start_updateNodes) / 1e9);
							
						}
						
					}
				}
			}
			
		}
		Collections.sort(nodes, JsTreeNode.viewFirstFolderSecondLeafLastComparator);
		
		return nodes;
		
	}
	
//	@RequestMapping(value="/imageTreeMain")
//	public @ResponseBody List<JsTreeNode> imageTreeMain (
//			
//			@RequestParam(value = "operation", required = false) String operation,
//			@RequestParam(value = "org", required = false) String orgUuid,
//			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
//			
//			HttpServletResponse response, HttpServletRequest request){
//
//		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
//		JsTreeNode node = new JsTreeNode();
////		AccountDto currentAccount = accountService.getCurrentAccount();
//		
//		if(operation.equals("get_imageTree")){
//			if(StringUtils.isNotBlank(orgUuid)){
//				// get org by orguuid
//				Organization org = accountService.getOrgByUuid(orgUuid);
//				if(org!=null){
//					if(StringUtils.isBlank(parentNodeUuid)){
//						MediaTreeNode mediaTreeNode = treeService.getMediaTreeRoot(org.getId());
//						// add more info to entityTreeNode based on EntityDetail
//						treeService.updateMediaTreeNodeForMedia(mediaTreeNode);
//						if(mediaTreeNode!=null){
//							node = mediaTreeNode.switchToJsTreeNode_type(MediaType.image);
//							nodes.add(node);
//						}
//					}else{
//						List<MediaTreeNode> mediaTreeNodes = treeService.findMediaTreeNodesByParentNodeUuid(parentNodeUuid);
//						for(MediaTreeNode n : mediaTreeNodes){
//							treeService.updateMediaTreeNodeForMedia(n);
//							nodes.add(n.switchToJsTreeNode_type(MediaType.image));
//						}
//					}
//				}
//			}
//			
//		}
//		
//		return nodes;
//		
//	}
	
	
	@RequestMapping(value="/pageTreeMain")
	public @ResponseBody List<JsTreeNode> pageTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			@RequestParam(value = "isPermissionInclude", required = false) Boolean isPermissionInclude,
			@RequestParam(value = "isPermissionFilterNeeded", required = false) Boolean isPermissionFilterNeeded, // true: will filter the nodes based on currentAccount.mergedPermission
			@RequestParam(value = "permissionUuid", required = false) String permissionUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(isPermissionInclude==null) isPermissionInclude = false;
		if(isPermissionFilterNeeded==null) isPermissionFilterNeeded = true;
		Permission currentAccountMergedPermission = null;
		if(isPermissionFilterNeeded){
//			currentAccountMergedPermission = currentAccount.getMergedPermission();
			currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
		}
		
		
		Permission permissionDto = permissionService.getPermissionDto(permissionUuid);
		// determine what permission.types should be in the selectable list
		Map<Permission.Type, Boolean> selectablePermissionTypeMap = new HashMap<Permission.Type, Boolean>();
		if(permissionDto!=null && permissionDto.getAccount_id()!=null){
			for(Permission.Type ptype : Permission.Type.values()){
				selectablePermissionTypeMap.put(ptype, true);
			}
		}else if(permissionDto!=null && permissionDto.getGroup_id()!=null){
			// Accountgroup.GroupAccessLevel = Global will only have preview and copy selections
			Accountgroup group = accountService.getGroup(permissionDto.getGroup_id());
			if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode())){
				selectablePermissionTypeMap.put(Permission.Type.preview, true);
				selectablePermissionTypeMap.put(Permission.Type.copy, true);
			}else if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Private.getCode())){
				for(Permission.Type ptype : Permission.Type.values()){
					selectablePermissionTypeMap.put(ptype, true);
				}
			}
		}


		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		JsTreeNode node = new JsTreeNode();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_mobileTree")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						PageTreeNode pageTreeNode = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Mobile);
						
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(pageTreeNode.getSystemName(), currentAccountMergedPermission);
						}else{
							currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							
							// add more info to pageTreeNode based on EntityDetail
							treeService.updatePageTreeNodeForPage(pageTreeNode, PageDetail.Type.Mobile, currentNodePermissionedStuffs_forMerged, null);
							
							if(isPermissionInclude){
								permissionService.updateTreeNodeForPermission(PageDetail.class, pageTreeNode, permissionDto, null, selectablePermissionTypeMap);
							}
							
							if(pageTreeNode!=null){
								node = pageTreeNode.switchToJsTreeNode(null, null, null);
								nodes.add(node);
							}
							
						}
						
					}else{
						
						// the node path
						String path = null;
//						EntityDetail parentDetail = productService.getEntityDetailByUuid(parentNodeUuid);
						PageDetail parentDetail = siteDesignService.getPageDetailByUuid(parentNodeUuid);
						
						if(parentDetail!=null){
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getPageuuid();
							}else{
								path = parentDetail.getPageuuid();
							}
						}
						Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
						}else{
							closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						List<PageTreeNode> pageTreeNodes = treeService.findPageTreeNodesByParentNodeUuid(parentNodeUuid);
						
						if(pageTreeNodes!=null && pageTreeNodes.size()>0){
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuff = permissionService.findClosestParentNodePermissionedStuff(path, permissionDto);

							for(PageTreeNode n : pageTreeNodes){
								
								Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
								if(isPermissionFilterNeeded){
									currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
								}else{
									currentNodePermissionedStuffs_forMerged = null;
								}
								
								
								if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(PageDetail.Type.Folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
									treeService.updatePageTreeNodeForPage(n, PageDetail.Type.Mobile, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
									
									if(isPermissionInclude){
										permissionService.updateTreeNodeForPermission(PageDetail.class, n, permissionDto, closestParentNodePermissionedStuff, selectablePermissionTypeMap);
									}
									
									nodes.add(n.switchToJsTreeNode(null, null, null));
									
								}
							}
						}
						
					}
					
				}
			}
			
		}else if(operation.equals("get_desktopTree")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						PageTreeNode pageTreeNode = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Desktop);
						
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(pageTreeNode.getSystemName(), currentAccountMergedPermission);
						}else{
							currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							// add more info to pageTreeNode based on EntityDetail
							treeService.updatePageTreeNodeForPage(pageTreeNode, PageDetail.Type.Desktop, currentNodePermissionedStuffs_forMerged, null);
							
							if(isPermissionInclude){
								permissionService.updateTreeNodeForPermission(PageDetail.class, pageTreeNode, permissionDto, null, selectablePermissionTypeMap);
							}
							
							if(pageTreeNode!=null){
								node = pageTreeNode.switchToJsTreeNode(null, null, null);
								nodes.add(node);
							}
							
						}
						
					}else{
						
						// the node path
						String path = null;
//						EntityDetail parentDetail = productService.getEntityDetailByUuid(parentNodeUuid);
						PageDetail parentDetail = siteDesignService.getPageDetailByUuid(parentNodeUuid);
						
						if(parentDetail!=null){
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getPageuuid();
							}else{
								path = parentDetail.getPageuuid();
							}
						}
						Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
						if(isPermissionFilterNeeded){
							closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
						}else{
							closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForProduct(org.getId());
						}
						
						
						List<PageTreeNode> pageTreeNodes = treeService.findPageTreeNodesByParentNodeUuid(parentNodeUuid);
						
						if(pageTreeNodes!=null && pageTreeNodes.size()>0){
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuff = permissionService.findClosestParentNodePermissionedStuff(path, permissionDto);

							for(PageTreeNode n : pageTreeNodes){
								
								Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
								if(isPermissionFilterNeeded){
									currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
								}else{
									currentNodePermissionedStuffs_forMerged = null;
								}
								
								treeService.updatePageTreeNodeForPage(n, PageDetail.Type.Desktop, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
								
								if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(PageDetail.Type.Folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
									if(isPermissionInclude){
										permissionService.updateTreeNodeForPermission(PageDetail.class, n, permissionDto, closestParentNodePermissionedStuff, selectablePermissionTypeMap);
									}
									
									nodes.add(n.switchToJsTreeNode(null, null, null));
								}
								
							}
							
						}
					}
					
				}
			}
			
		}else if(operation.equals("getPageRoots")){ // get desktop page & mobile page roots
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					
					// for desktop
					PageTreeNode desktopRoot = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Desktop);

					Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged_desk = null;
					if(isPermissionFilterNeeded){
						currentNodePermissionedStuffs_forMerged_desk = permissionService.findCurrentNodePermissionedStuff(desktopRoot.getSystemName(), currentAccountMergedPermission);
					}else{
						currentNodePermissionedStuffs_forMerged_desk = permissionService.getFullPermissionForProduct(org.getId());
					}

					if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged_desk, null)){
						treeService.updatePageTreeNodeForPage(desktopRoot, PageDetail.Type.Desktop, currentNodePermissionedStuffs_forMerged_desk, null);
						
						if(isPermissionInclude){
							permissionService.updateTreeNodeForPermission(PageDetail.class, desktopRoot, permissionDto, null, selectablePermissionTypeMap);
						}

						if(desktopRoot!=null){
							nodes.add(desktopRoot.switchToJsTreeNode(null, null, null));
						}
						
					}
					
					// for mobile
					PageTreeNode mobileRoot = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Mobile);
					
					Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged_mobile = null;
					if(isPermissionFilterNeeded){
						currentNodePermissionedStuffs_forMerged_mobile = permissionService.findCurrentNodePermissionedStuff(desktopRoot.getSystemName(), currentAccountMergedPermission);
					}else{
						currentNodePermissionedStuffs_forMerged_mobile = permissionService.getFullPermissionForProduct(org.getId());
					}

					if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged_mobile, null)){
						treeService.updatePageTreeNodeForPage(mobileRoot, PageDetail.Type.Mobile, currentNodePermissionedStuffs_forMerged_mobile, null);
						
						if(isPermissionInclude){
							permissionService.updateTreeNodeForPermission(PageDetail.class, mobileRoot, permissionDto, null, selectablePermissionTypeMap);
						}
						
						if(mobileRoot!=null){
							nodes.add(mobileRoot.switchToJsTreeNode(null, null, null));
						}
						
					}
					
				}
			}
		}
		
		
		
		
		Collections.sort(nodes, JsTreeNode.viewFirstFolderSecondLeafLastComparator);
		return nodes;
		
	}
	
	@Deprecated
	@RequestMapping(value="/desktopTreeMain")
	public @ResponseBody List<JsTreeNode> desktopTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			HttpServletResponse response, HttpServletRequest request){

		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		JsTreeNode node = new JsTreeNode();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_desktopTree")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						PageTreeNode pageTreeNode = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Desktop);
						// add more info to pageTreeNode based on EntityDetail
						treeService.updatePageTreeNodeForPage(pageTreeNode, PageDetail.Type.Desktop, null, null);
						if(pageTreeNode!=null){
							node = pageTreeNode.switchToJsTreeNode(null, null, null);
							nodes.add(node);
						}
					}else{
						List<PageTreeNode> pageTreeNodes = treeService.findPageTreeNodesByParentNodeUuid(parentNodeUuid);
						for(PageTreeNode n : pageTreeNodes){
							treeService.updatePageTreeNodeForPage(n, PageDetail.Type.Desktop, null, null);
							nodes.add(n.switchToJsTreeNode(null, null, null));
						}
					}
					
				}
			}
			
		}
		
		return nodes;
		
	}
	
	@Deprecated
	@RequestMapping(value="/mobileTreeMain")
	public @ResponseBody List<JsTreeNode> mobileTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			HttpServletResponse response, HttpServletRequest request){

		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		JsTreeNode node = new JsTreeNode();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_mobileTree")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){
						PageTreeNode pageTreeNode = treeService.getPageTreeRoot(org.getId(), PageTreeLevelView.Type.Mobile);
						// add more info to pageTreeNode based on EntityDetail
						treeService.updatePageTreeNodeForPage(pageTreeNode, PageDetail.Type.Mobile, null, null);
						if(pageTreeNode!=null){
							node = pageTreeNode.switchToJsTreeNode(null, null, null);
							nodes.add(node);
						}
					}else{
						List<PageTreeNode> pageTreeNodes = treeService.findPageTreeNodesByParentNodeUuid(parentNodeUuid);
						for(PageTreeNode n : pageTreeNodes){
							treeService.updatePageTreeNodeForPage(n, PageDetail.Type.Mobile, null, null);
							nodes.add(n.switchToJsTreeNode(null, null, null));
						}
					}
					
				}
			}
			
		}
		
		return nodes;
		
	}
	
	@RequestMapping(value="/containerTreeMain")
	public @ResponseBody List<JsTreeNode> containerTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "pageid", required = false) String pageUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		Date now = new Date();
		AccountDto loginaccount = accountService.getCurrentAccount();

		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
//		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(operation.equals("get_container")){
			if(StringUtils.isNotBlank(orgUuid)){
				// get org by orguuid
				Organization org = accountService.getOrgByUuid(orgUuid);
				if(org!=null){
					if(StringUtils.isBlank(parentNodeUuid)){ // parent is the page, so, to getContainerTreeRoot
						
						ContainerTreeNode containerTreeNode = treeService.getContainerTreeRoot(org.getId(), pageUuid);
						if(containerTreeNode!=null) containerTreeNode.setRoot(true);
						// add more info to pageTreeNode based on EntityDetail
						treeService.updateContainerTreeNodeForContainer(containerTreeNode, pageUuid);
						if(containerTreeNode!=null){
							JsTreeNode node = containerTreeNode.switchToJsTreeNode(null, null, null);
							nodes.add(node);
						}
					}else{ // parent is the container, so, findContainerTreeNodesByParentNodeUuid OR findSchedules
						
						List<ContainerTreeNode> containerTreeNodes = treeService.findContainerTreeNodesByParentNodeUuid(parentNodeUuid);
						if(containerTreeNodes!=null && containerTreeNodes.size()>0){
							// find pageuuid
							ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerTreeNodes.get(0).getSystemName());
//							PageDetail pagedetail = siteDesignService.getPageDetailByUuid(containerTreeNodes.get(0).getSystemName());
							if(containerDetail!=null){
								for(ContainerTreeNode n : containerTreeNodes){
									n.setRoot(false);
									treeService.updateContainerTreeNodeForContainer(n, containerDetail.getPageuuid());
									nodes.add(n.switchToJsTreeNode(null, null, null));
								}
							}
							
						}else{ // no containerTreeNode find, to find containerModuleSchedules
							// find parent container
							ContainerDetail parentContainer = siteDesignService.getContainerByUuid(parentNodeUuid);
							
							List<ContainerModuleSchedule> cmScheds = siteDesignService.findContainerModuleSchedulesByContainerUuid(parentNodeUuid);
							if(parentContainer!=null && cmScheds!=null && cmScheds.size()>0){
								List<ScheduleInterface> outdatedScheds = siteDesignService.getOutDatedSchedule(cmScheds, now);
								Map<String, Boolean> outdatedSchedIds = new HashMap<String, Boolean>();
								if(outdatedScheds!=null && outdatedScheds.size()>0){
									for(ScheduleInterface s : outdatedScheds){
										outdatedSchedIds.put(s.getUuid(), Boolean.FALSE);
									}
								}
								for(ContainerModuleSchedule cms : cmScheds){
									JsTreeNode schedNode = TreeHelp.containerModuleScheduleSwitchToJsTreeNode(cms);
									if(schedNode!=null){
										// determine outdate or not
										if(outdatedSchedIds.get(cms.getUuid())!=null){
											schedNode.getData().setTitle("<span class='outdated'>"+schedNode.getData().getTitle()+"</span>");
										}
										
										// find page's read and edit permission
										boolean pageReadPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.read, parentContainer.getPageuuid());
										boolean pageModifyPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, parentContainer.getPageuuid());
										
										
										// set cssClassInfo here (toRename, toDelete, toContainerModuleSched, toNewContainer, toModuleinstancesched)
										if(pageModifyPermission){
											schedNode.getAttr().addCssClass("toRename delete toConfig");
											// if module is regular module, add class "toModuleinstancesched", if module is product module, don't add class "toModuleinstancesched"
											ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(cms.getModuleuuid());
											if(moduledetail!=null && !moduledetail.getType().equals(ModuleDetail.Type.productModule.getCode())){
												schedNode.getAttr().addCssClass("toModuleinstancesched");
											}
										}else if(pageReadPermission){
											schedNode.getAttr().addCssClass("toConfig");
										}
										
										// set children info here
										List<ModuleInstanceSchedule> miScheds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(cms.getUuid());
										if(miScheds!=null && miScheds.size()>0){
											schedNode.setState("closed");
										}else{
											schedNode.getAttr().addCssClass("noMiSched");
											schedNode.setState("");
										}
										nodes.add(schedNode);
									}
								}
							}
						}
						
					}
					
				}
			}
			
		}
		
		return nodes;
		
	}
	
	
	
	@RequestMapping(value="/containerSchedTree")
	public @ResponseBody List<JsTreeNode> containerSchedTree (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		Date now = new Date();
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		Organization org = accountService.getOrgByUuid(orgUuid);
		if(StringUtils.isNotBlank(operation) && org!=null && StringUtils.isNotBlank(parentNodeUuid)){
			if(operation.trim().equals("get_miSched")){
				List<ModuleInstanceSchedule> scheds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(parentNodeUuid);
				
				if(scheds!=null && scheds.size()>0){
					
					// find pageuuid for permission check
					ContainerModuleSchedule containerModuleSchedule = siteDesignService.getContainerModuleScheduleByUuid(parentNodeUuid);
					if(containerModuleSchedule!=null){
						
						ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerModuleSchedule.getContaineruuid());
						if(containerDetail!=null){
							
							// ..
							boolean pageReadPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.read, containerDetail.getPageuuid());
							boolean pageModifyPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
							
							List<ScheduleInterface> outdatedScheds = siteDesignService.getOutDatedSchedule(scheds, now);
							Map<String, Boolean> outdatedSchedIds = new HashMap<String, Boolean>();
							if(outdatedScheds!=null && outdatedScheds.size()>0){
								for(ScheduleInterface s : outdatedScheds){
									outdatedSchedIds.put(s.getUuid(), Boolean.FALSE);
								}
							}
							
							for(ModuleInstanceSchedule sched : scheds){
								JsTreeNode schedNode = TreeHelp.moduleInstanceScheduleSwitchToJsTreeNode(sched);
								if(schedNode!=null){
									if(outdatedSchedIds.get(sched.getUuid())!=null){
										schedNode.getData().setTitle("<span class='outdated'>"+schedNode.getData().getTitle()+"</span>");
									}
									
									if(pageModifyPermission){
										// set cssClassInfo here (toRename, toDelete, toContainerModuleSched, toNewContainer, toModuleinstancesched)
										schedNode.getAttr().addCssClass("toRename delete toConfig");
									}else if(pageReadPermission){
										schedNode.getAttr().addCssClass("toConfig");
									}
									
									schedNode.setState("");
									nodes.add(schedNode);
									
								}
							}
							
						}
						
					}
					
				}
			}
		}
		
		return nodes;

	}


	@RequestMapping(value="/sharedPageTreeMain")
	public @ResponseBody List<JsTreeNode> sharedPageTreeMain(
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "orgNodeId", required = false) String orgUuid,
			@RequestParam(value = "folderNodeId", required = false) String folderNodeUuid,
			@RequestParam(value = "targetOrgid", required = false) String targetOrgUuid,
			@RequestParam(value = "pageType", required = false) String pageType_code,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		Organization targetOrg = accountService.getOrgByUuid(targetOrgUuid);
		
		PageTreeLevelView.Type pagetype = null;
		PageDetail.Type pageDetailType = null;
		if(StringUtils.equals("desktop", pageType_code)){ // "desktop" is from sitedesignController.websiteDesktop
			pagetype = PageTreeLevelView.Type.Desktop;
			pageDetailType = PageDetail.Type.Desktop;
		}else if(StringUtils.equals("mobile", pageType_code)){ // "mobile" is from sitedesignController.websiteMobile
			pagetype = PageTreeLevelView.Type.Mobile;
			pageDetailType = PageDetail.Type.Mobile;
		}
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		if(loginAccount!=null && targetOrg!=null && pagetype!=null){
			
			
			
			// find latest updated merged permission for account:
			Permission currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
			
			if(StringUtils.isNotBlank(orgUuid)){ // get org's product root
				
				Organization targetOrgForProducts = accountService.getOrgByUuid(orgUuid);
				if(targetOrgForProducts!=null){
					
//					EntityTreeNode root = treeService.getProductTreeRoot(targetOrgForProducts.getId());
					
					PageTreeNode root = treeService.getPageTreeRoot(targetOrgForProducts.getId(), pagetype);
					
					if(root!=null){
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(root.getSystemName(), currentAccountMergedPermission);
						if(permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
//							treeService.updateEntityTreeNodeForProduct(root, currentNodePermissionedStuffs_forMerged, null);
							treeService.updatePageTreeNodeForPage(root, pageDetailType, currentNodePermissionedStuffs_forMerged, null);
//							treeService.updateModuleTreeNode(root, false, currentNodePermissionedStuffs_forMerged, null);
							nodes.add(root.switchToJsTreeNode(null, null, null));
						}
						
					}
				}
				
			}else if(StringUtils.isNotBlank(folderNodeUuid)){ // get folder's shared folders and/or products
				
				// the node path
				String path = null;
				
//				EntityDetail parentDetail = productService.getEntityDetailByUuid(folderNodeUuid);
				PageDetail parentDetail = siteDesignService.getPageDetailByUuid(folderNodeUuid);
				if(parentDetail!=null){
					if(StringUtils.isNotBlank(parentDetail.getPath())){
						path = parentDetail.getPath()+"/"+parentDetail.getPageuuid();
					}else{
						path = parentDetail.getPageuuid();
					}
				}
				
				Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
				
				if(parentDetail!=null){
					List<PageTreeNode> treeNodes = treeService.findPageTreeNodesByParentNodeUuid(folderNodeUuid);
					
					if(treeNodes!=null){
						for(PageTreeNode n : treeNodes){
							Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
							
							treeService.updatePageTreeNodeForPage(n, pageDetailType, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
							
//							if(permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(EntityDetail.EntityType.folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
							if(permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(PageDetail.Type.Folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
								
								// check page's containers' default modules are allowed for page only (not folder)
								boolean templateAllowed = true;
								if(n.getType()!=null && (n.getType().equals(PageDetail.Type.Desktop) || n.getType().equals(PageDetail.Type.Mobile))){
									
									PageDetail pdetail = siteDesignService.getPageDetailByUuid(n.getSystemName());
									if(pdetail!=null){
										
										if(StringUtils.isNotBlank(pdetail.getDetail())){
											List<String> containersUuids = SitedesignHelper.findAllContainerUuidsFromPageDetail(pdetail.getDetail()); 
											
											if(containersUuids!=null && containersUuids.size()>0){
												for(String cuuid : containersUuids){
													if(!templateAllowed) break;
													
													ContainerDetail containerDetail = siteDesignService.getContainerByUuid(cuuid);
													if(containerDetail!=null && StringUtils.isNotBlank(containerDetail.getModuleuuid())){
														templateAllowed = permissionService.isPermissionAllowed(currentAccountMergedPermission, Permission.Type.copy, containerDetail.getModuleuuid());
														
													}
												}
											}
										}
										
									}else{
										templateAllowed = false;
									}
								}
								
								if(templateAllowed){
									nodes.add(n.switchToJsTreeNode(null, null, null));
								}
							}
						}
					}
				}
				
			}else{ // get all org's names
				
				// get all orgs from mergedPermission: the currentAccount can access these org's shared modules 
				if(currentAccountMergedPermission!=null && currentAccountMergedPermission.getPermissionedStuffs()!=null && currentAccountMergedPermission.getPermissionedStuffs().size()>0){
					Set<Long> orgIds = new HashSet<Long>();
					for(PermissionedStuff s : currentAccountMergedPermission.getPermissionedStuffs()){
						if(s.getCategory().equals(PermissionedStuff.Category.page.name())){
							orgIds.add(s.getStufforg());
						}
					}
					if(orgIds.size()>0){
						for(Long oid : orgIds){
							// not include self organization
							if(oid.intValue()!=targetOrg.getId().intValue()){
								Organization o = accountService.getOrgById(oid);
								treeService.updateOrgNodeForSharedModuleTree(o, currentAccountMergedPermission);
								nodes.add(o.switchToJsTreeNode(null, null, null));
							}
						}
					}
					
				}
				
			}
			
		}
		
		return nodes;
		
	}
	
	
	
	
	@RequestMapping(value="/sharedProductTreeMain")
	public @ResponseBody List<JsTreeNode> sharedProductTreeMain(
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "orgNodeId", required = false) String orgUuid,
			@RequestParam(value = "folderNodeId", required = false) String folderNodeUuid,
			@RequestParam(value = "targetOrgid", required = false) String targetOrgUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		Organization targetOrg = accountService.getOrgByUuid(targetOrgUuid);
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		if(loginAccount!=null && targetOrg!=null){
			// find latest updated merged permission for account:
			Permission currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
			
			if(StringUtils.isNotBlank(orgUuid)){ // get org's product root
				
				Organization targetOrgForProducts = accountService.getOrgByUuid(orgUuid);
				if(targetOrgForProducts!=null){
					
					EntityTreeNode root = treeService.getProductTreeRoot(targetOrgForProducts.getId());
					
					if(root!=null){
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(root.getSystemName(), currentAccountMergedPermission);
						if(permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							treeService.updateEntityTreeNodeForProduct(root, currentNodePermissionedStuffs_forMerged, null, currentAccountMergedPermission);
//							treeService.updateModuleTreeNode(root, false, currentNodePermissionedStuffs_forMerged, null);
							nodes.add(root.switchToJsTreeNode(null, null, null));
						}
						
					}
				}
				
			}else if(StringUtils.isNotBlank(folderNodeUuid)){ // get folder's shared folders and/or products
				// the node path
				String path = null;
				
				EntityDetail parentDetail = productService.getEntityDetailByUuid(folderNodeUuid);
				if(parentDetail!=null){
					if(StringUtils.isNotBlank(parentDetail.getPath())){
						path = parentDetail.getPath()+"/"+parentDetail.getEntityuuid();
					}else{
						path = parentDetail.getEntityuuid();
					}
				}
				
				Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
				
				if(parentDetail!=null){
					//List<EntityTreeNode> treeNodes = treeService.findProductTreeNodesByParentNodeUuid(folderNodeUuid);
					List<TreeNode> treeNodes = treeService.findTreeNodesByParentNodeUuid_v2(loginAccount.getAccountuuid(), 
							EntityDetail.class, folderNodeUuid, true, true, true);
					if(treeNodes!=null){
						for(TreeNode n : treeNodes){
							Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
							
							treeService.updateEntityTreeNodeForProduct((EntityTreeNode)n, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged, currentAccountMergedPermission);
							
							// check product's module is allowed for product only (not folder)
							boolean templateAllowed = true;
							if(((EntityTreeNode)n).getType()!=null && ((EntityTreeNode)n).getType().equals(EntityDetail.EntityType.entity)){
								
								EntityDetail edetail = productService.getEntityDetailByUuid(n.getSystemName());
								if(edetail!=null){
									templateAllowed = permissionService.isPermissionAllowed(currentAccountMergedPermission, Permission.Type.copy, edetail.getModuleuuid());
								}else{
									templateAllowed = false;
								}
							}
							
							if(templateAllowed){
								nodes.add(n.switchToJsTreeNode(null, null, null));
							}
						}
					}
				}
				
			}else{ // get all org's names
				
				// get all orgs from mergedPermission: the currentAccount can access these org's shared modules 
				if(currentAccountMergedPermission!=null && currentAccountMergedPermission.getPermissionedStuffs()!=null && currentAccountMergedPermission.getPermissionedStuffs().size()>0){
					Set<Long> orgIds = new HashSet<Long>();
					for(PermissionedStuff s : currentAccountMergedPermission.getPermissionedStuffs()){
						if(s.getCategory().equals(PermissionedStuff.Category.product.name())){
							orgIds.add(s.getStufforg());
						}
					}
					if(orgIds.size()>0){
						for(Long oid : orgIds){
							// not include self organization
							if(oid.intValue()!=targetOrg.getId().intValue()){
								Organization o = accountService.getOrgById(oid);
								treeService.updateOrgNodeForSharedModuleTree(o, currentAccountMergedPermission);
								nodes.add(o.switchToJsTreeNode(null, null, null));
							}
						}
					}
					
				}
				
				
				
			}
		}
		
		return nodes;
	}
	
	
	@RequestMapping(value="/sharedModuleTreeMain")
	public @ResponseBody List<JsTreeNode> sharedModuleTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "orgNodeId", required = false) String orgUuid,
			@RequestParam(value = "folderNodeId", required = false) String folderNodeUuid,
			@RequestParam(value = "targetOrgid", required = false) String targetOrgUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		Organization targetOrg = accountService.getOrgByUuid(targetOrgUuid);
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		if(loginAccount!=null && targetOrg!=null){
			// find latest updated merged permission for account:
			Permission currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
			
			if(StringUtils.isNotBlank(orgUuid)){ // get org's module root
				
				Organization targetOrgForModules = accountService.getOrgByUuid(orgUuid);
				if(targetOrgForModules!=null){
					ModuleTreeNode root = treeService.getModuleTreeRoot(targetOrgForModules.getId());
					if(root!=null){
						Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(root.getSystemName(), currentAccountMergedPermission);
						if(permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
							treeService.updateModuleTreeNode(root, false, currentNodePermissionedStuffs_forMerged, null);
							nodes.add(root.switchToJsTreeNode(null, null, null));
						}
						
					}
				}
				
			}else if(StringUtils.isNotBlank(folderNodeUuid)){ // get folder's shared folders and/or modules
				
				// the node path
				String path = null;
				ModuleDetail parentDetail = siteDesignService.getModuleDetailByUuid(folderNodeUuid);
				if(parentDetail!=null){
					if(StringUtils.isNotBlank(parentDetail.getPath())){
						path = parentDetail.getPath()+"/"+parentDetail.getModuleuuid();
					}else{
						path = parentDetail.getModuleuuid();
					}
				}
				
				Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
				
				// get ModuleDetail by uuid to determine the type of the parent
				if(parentDetail!=null){
					List<ModuleTreeNode> treeNodes = treeService.findModuleTreeNodesByParentNodeUuid(folderNodeUuid);
					if(treeNodes!=null){
						for(ModuleTreeNode n : treeNodes){
							Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
							
							treeService.updateModuleTreeNode(n, false, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
							
							if(permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(ModuleDetail.Type.folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
								nodes.add(n.switchToJsTreeNode(null, null, null));
								
							}
						}
					}
				}
				
			}else{ // get all org's names
				// get all orgs from mergedPermission: the currentAccount can access these org's shared modules 
				if(currentAccountMergedPermission!=null && currentAccountMergedPermission.getPermissionedStuffs()!=null && currentAccountMergedPermission.getPermissionedStuffs().size()>0){
					Set<Long> orgIds = new HashSet<Long>();
					for(PermissionedStuff s : currentAccountMergedPermission.getPermissionedStuffs()){
						// check permissionedStuff is for product / module / page ...
						if(s.getCategory().equals(PermissionedStuff.Category.moduledetail.name())){
							orgIds.add(s.getStufforg());
						}
					}
					if(orgIds.size()>0){
						for(Long oid : orgIds){
							// not include self organization
							if(oid.intValue()!=targetOrg.getId().intValue()){
								Organization o = accountService.getOrgById(oid);
								treeService.updateOrgNodeForSharedModuleTree(o, currentAccountMergedPermission);
								nodes.add(o.switchToJsTreeNode(null, null, null));
							}
						}
					}
					
				}
				
			}
			
		}
		
		
		return nodes;
	}
		
	
	
	
	@RequestMapping(value="/moduleTreeMain")
	public @ResponseBody List<JsTreeNode> moduleTreeMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,
			
			@RequestParam(value = "isInstanceInclude", required = false) Boolean isInstanceInclude,
			@RequestParam(value = "isPermissionInclude", required = false) Boolean isPermissionInclude,
			@RequestParam(value = "isPermissionFilterNeeded", required = false) Boolean isPermissionFilterNeeded, // true: will filter the nodes based on currentAccount.mergedPermission
			@RequestParam(value = "permissionUuid", required = false) String permissionUuid,
			
			HttpServletResponse response, HttpServletRequest request){
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		if(isInstanceInclude==null) isInstanceInclude = true;
		if(isPermissionInclude==null) isPermissionInclude = false;
		if(isPermissionFilterNeeded==null) isPermissionFilterNeeded = true;
		
		Permission permissionDto = permissionService.getPermissionDto(permissionUuid);
		// determine what permission.types should be in the selectable list
		Map<Permission.Type, Boolean> selectablePermissionTypeMap = new HashMap<Permission.Type, Boolean>();
		if(permissionDto!=null && permissionDto.getAccount_id()!=null){
			for(Permission.Type ptype : Permission.Type.values()){
				selectablePermissionTypeMap.put(ptype, true);
			}
		}else if(permissionDto!=null && permissionDto.getGroup_id()!=null){
			// Accountgroup.GroupAccessLevel = Global will only have preview and copy selections
			Accountgroup group = accountService.getGroup(permissionDto.getGroup_id());
			if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode())){
				selectablePermissionTypeMap.put(Permission.Type.preview, true);
				selectablePermissionTypeMap.put(Permission.Type.copy, true);
			}else if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Private.getCode())){
				for(Permission.Type ptype : Permission.Type.values()){
					selectablePermissionTypeMap.put(ptype, true);
				}
			}
		}
		
		
		Permission currentAccountMergedPermission = null;
		if(isPermissionFilterNeeded){
//			currentAccountMergedPermission = currentAccount.getMergedPermission();
			currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
		}

		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		// get org by orguuid
		Organization targetOrg = accountService.getOrgByUuid(orgUuid);
		if(targetOrg!=null){
			if(StringUtils.isBlank(parentNodeUuid)){ // get the module root
				ModuleTreeNode root = treeService.getModuleTreeRoot(targetOrg.getId());
				if(root!=null){
					
					Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
					if(isPermissionFilterNeeded){
						currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(root.getSystemName(), currentAccountMergedPermission);
					}else{
						currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForModule(targetOrg.getId());
					}
					

					if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
						treeService.updateModuleTreeNode(root, isInstanceInclude, currentNodePermissionedStuffs_forMerged, null);
						
						if(isPermissionInclude){
							permissionService.updateTreeNodeForPermission(ModuleDetail.class, root, permissionDto, null, selectablePermissionTypeMap);
						}
						nodes.add(root.switchToJsTreeNode(null, null, null));
					}
					
				}
				
			}else{
				
				if("get_productTree".equals(operation)){ // ***** no more useful ******
					List<EntityTreeNode> treeNodes = treeService.findProductTreeNodesByParentNodeUuid(parentNodeUuid);
					if(treeNodes!=null){
						for(EntityTreeNode n : treeNodes){
							treeService.updateEntityTreeNodeForModuleInstance(n);
							nodes.add(n.switchToJsTreeNode(null, null, null));
						}
					}
					
				}else if("get_moduleTree".equals(operation)){
					
					// the node path
					String path = null;
					ModuleDetail parentDetail = siteDesignService.getModuleDetailByUuid(parentNodeUuid);
					if(parentDetail!=null){
						if(StringUtils.isNotBlank(parentDetail.getPath())){
							path = parentDetail.getPath()+"/"+parentDetail.getModuleuuid();
						}else{
							path = parentDetail.getModuleuuid();
						}
					}
					
					Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
					if(isPermissionFilterNeeded){
						closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
					}else{
						closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForModule(targetOrg.getId());
					}
					
					
					// get ModuleDetail by uuid to determine the type of the parent
					if(parentDetail!=null){
						List<ModuleTreeNode> treeNodes = treeService.findModuleTreeNodesByParentNodeUuid(parentNodeUuid);
						if(treeNodes!=null){
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuff = permissionService.findClosestParentNodePermissionedStuff(path, permissionDto);
							for(ModuleTreeNode n : treeNodes){
								
								if(parentDetail.getType().equals(ModuleDetail.Type.module.getCode())){
									
//									if(!isPermissionFilterNeeded || 
//											(
//													// only the permission-modify allowed can have the instance
//													closestParentNodePermissionedStuffs_forMerged!=null && 
//													(closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview)==null || (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode()))) 
//															&& (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read)==null || (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())))
//															&& (closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.modify)!=null && closestParentNodePermissionedStuffs_forMerged.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode()))
//											)
//										){
//										treeService.updateModuleTreeNodeForInstance(n);
//										nodes.add(n.switchToJsTreeNode(null, null, null));
//									}
									
									if(!isPermissionFilterNeeded){
										treeService.updateModuleTreeNodeForInstance(n, null);
										nodes.add(n.switchToJsTreeNode(null, null, null));
									}else{
										treeService.updateModuleTreeNodeForInstance(n, parentDetail.getModuleuuid());
										nodes.add(n.switchToJsTreeNode(null, null, null));
									}
									
								}else{
									
									Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
									
									// get full permission if isPermissionFilterNeeded is false
									if(!isPermissionFilterNeeded){
										currentNodePermissionedStuffs_forMerged = null;
									}else{
										currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);
									}
									treeService.updateModuleTreeNode(n, isInstanceInclude, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);

									if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(ModuleDetail.Type.folder) ,currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
										
										
										if(isPermissionInclude){
											permissionService.updateTreeNodeForPermission(ModuleDetail.class, 
													n, 
													permissionDto, 
													closestParentNodePermissionedStuff,
													selectablePermissionTypeMap);
										}
										nodes.add(n.switchToJsTreeNode(null, null, null));
										
									}
									
								}
							}
							
							if(nodes!=null && nodes.size()>0){
								Collections.sort(nodes, JsTreeNode.viewFirstFolderSecondLeafLastComparator);
							}
						}
					}
				}else if("get_moduleTreeForSched".equals(operation)){ // modify the product module to have name like : product module (xxx)
					ModuleDetail parentDetail = siteDesignService.getModuleDetailByUuid(parentNodeUuid);
					if(parentDetail!=null && parentDetail.getType().equals(ModuleDetail.Type.folder.getCode())){ // only get module, not instance (module will under folder, instance will under module only)
						List<ModuleTreeNode> treeNodes = treeService.findModuleTreeNodesByParentNodeUuid(parentNodeUuid);
						if(treeNodes!=null){
							for(ModuleTreeNode n : treeNodes){
								treeService.updateModuleTreeNodeForSched(n);
								nodes.add(n.switchToJsTreeNode(null, null, null));
							}
						}
					}
					
					
				}else if("get_instanceViewTree".equals(operation)){
					//ModuleInstance instance = siteDesignService.getModuleInstanceByUuid(parentNodeUuid);
					List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(parentNodeUuid);
					if(views!=null){
						for(InstanceView v : views){
							if(isPermissionFilterNeeded){
								treeService.updateInstanceViewForTreeNode(v, v.getModuleuuid());
							}else{
								treeService.updateInstanceViewForTreeNode(v, null);
							}
							nodes.add(v.switchToJsTreeNode(null, null, null));
						}
					}
				}else if("get_viewScheduleTree".equals(operation)){
					List<InstanceViewSchedule> viewScheds = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(parentNodeUuid);
					if(viewScheds!=null){
						for(InstanceViewSchedule s : viewScheds){
							
							
							if(isPermissionFilterNeeded){
								ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(s.getModuleinstanceuuid());
								EntityDetail entitydetail = productService.getEntityDetailByUuid(s.getModuleinstanceuuid());
								
								String pointUuid = null;
								if(moduleInstance!=null){
									pointUuid = moduleInstance.getModuleuuid();
								}else if(entitydetail!=null){
									pointUuid = entitydetail.getEntityuuid();
								}
								
								treeService.updateViewScheduleForTreeNode(s, pointUuid);
							}else{
								treeService.updateViewScheduleForTreeNode(s, null);
								
							}
							
							nodes.add(s.switchToJsTreeNode(null, null, null));
							
							
							
						}
					}
				}
				
				
			}
		}
		
		return nodes;
		
	}
	
	
	
	
	
	

	@RequestMapping(value="/moduleTreeForProductMain")
	public @ResponseBody List<JsTreeNode> moduleTreeForProductMain (
			
			@RequestParam(value = "operation", required = false) String operation,
			@RequestParam(value = "org", required = false) String orgUuid,
			@RequestParam(value = "parentNodeId", required = false) String parentNodeUuid,

			@RequestParam(value = "isPermissionFilterNeeded", required = false) Boolean isPermissionFilterNeeded, // true: will filter the nodes based on currentAccount.mergedPermission
			
			HttpServletResponse response, HttpServletRequest request){
		
		if(isPermissionFilterNeeded==null) isPermissionFilterNeeded = true;
		
		AccountDto currentAccount = accountService.getCurrentAccount();
		
		Permission currentAccountMergedPermission = null;
		if(isPermissionFilterNeeded){
//			currentAccountMergedPermission = currentAccount.getMergedPermission();
			currentAccountMergedPermission = permissionService.getMergedPermissionForAccount(currentAccount.getId(), true);
		}
		
		List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
		
		// get org by orguuid
		Organization targetOrg = accountService.getOrgByUuid(orgUuid);
		if(targetOrg!=null){
			if(StringUtils.isBlank(parentNodeUuid)){ // get the module root
				ModuleTreeNode root = treeService.getModuleTreeRoot(targetOrg.getId());
				if(root!=null){
					
					
					Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = null;
					if(isPermissionFilterNeeded){
						currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(root.getSystemName(), currentAccountMergedPermission);
					}else{
						currentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForModule(targetOrg.getId());
					}
					
					if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(true, currentNodePermissionedStuffs_forMerged, null)){
						treeService.updateModuleTreeNode(root, true, currentNodePermissionedStuffs_forMerged, null);
						nodes.add(root.switchToJsTreeNode(null, null, null));
					}
					
				}
				
			}else{
				
				if("get_moduleTree".equals(operation)){
					
					// get ModuleDetail by uuid to determine the type of the parent
					ModuleDetail parentDetail = siteDesignService.getModuleDetailByUuid(parentNodeUuid);
					if(parentDetail!=null){
						
						List<ModuleTreeNode> treeNodes = treeService.findModuleTreeNodesByParentNodeUuid(parentNodeUuid);
						if(treeNodes!=null){
							
							// the node path
							String path = null;
							if(StringUtils.isNotBlank(parentDetail.getPath())){
								path = parentDetail.getPath()+"/"+parentDetail.getModuleuuid();
							}else{
								path = parentDetail.getModuleuuid();
							}
							
							Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = null;
							if(isPermissionFilterNeeded){
								closestParentNodePermissionedStuffs_forMerged = permissionService.findClosestParentNodePermissionedStuff(path, currentAccountMergedPermission);
							}else{
								closestParentNodePermissionedStuffs_forMerged = permissionService.getFullPermissionForModule(targetOrg.getId());
							}
							
							for(ModuleTreeNode n : treeNodes){
								Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = permissionService.findCurrentNodePermissionedStuff(n.getSystemName(), currentAccountMergedPermission);

								// get full permission if isPermissionFilterNeeded is false
								if(!isPermissionFilterNeeded){
									currentNodePermissionedStuffs_forMerged = null;
								}
								
								treeService.updateModuleTreeNode(n, true, currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
								
								if(n.getType().equals(ModuleDetail.Type.folder) || n.getType().equals(ModuleDetail.Type.productModule)){
									
									if(!isPermissionFilterNeeded || permissionService.isNodeInListAllowed(n.getType()!=null && n.getType().equals(ModuleDetail.Type.folder), currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged)){
										nodes.add(n.switchToJsTreeNode(null, null, null));
									}
								}

							}
						}
					}
				}
				
				
				
				
			}
		}
		
		return nodes;
		
	}
	
	
	
	
	
	
}
