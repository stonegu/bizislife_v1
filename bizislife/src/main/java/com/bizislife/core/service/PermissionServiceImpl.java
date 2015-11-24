package com.bizislife.core.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.MediaTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.PageTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.entity.converter.ContainerToPageDetailConvertor;
import com.bizislife.core.hibernate.dao.AccountDao;
import com.bizislife.core.hibernate.dao.EntityDao;
import com.bizislife.core.hibernate.dao.GroupDao;
import com.bizislife.core.hibernate.dao.MediaDao;
import com.bizislife.core.hibernate.dao.OrganizationDao;
import com.bizislife.core.hibernate.dao.PermissionDao;
import com.bizislife.core.hibernate.dao.SiteDesignDao;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.Permission.Type;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.DateUtils;
import com.bizislife.util.validation.ValidationSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class PermissionServiceImpl implements PermissionService{
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private SiteDesignService siteDesignService;

	@Autowired
	AccountDao accountDao;

	@Autowired
	OrganizationDao orgDao;
	
	@Autowired
	GroupDao groupDao;
	
	@Autowired
	EntityDao entityDao;
	
	@Autowired
	MediaDao mediaDao;
	
	@Autowired
	SiteDesignDao sitedesignDao;

	@Autowired
	PermissionDao permissionDao;

	@Autowired
    private Mapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Override
	@Transactional(readOnly=true)
	public List<Permission> findAccountPermissions(Long accountId) {
		AccountDto loginAccount = accountService.getCurrentAccount();
		Account account = accountDao.getAccountPojoById(accountId);
		if(loginAccount!=null && account!=null && account.getPermissions()!=null && account.getPermissions().size()>0){
			
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz sys account can
				|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==account.getOrganization_id().intValue()) // org's sys acount can
				//|| (loginAccount.getId().intValue()==account.getId().intValue()) // self can
			){
				List<Permission> permissions = new ArrayList<Permission>();
				for(Permission p : account.getPermissions()){
					permissions.add(mapper.map(p, Permission.class));
				}
				return permissions;
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Permission> findGroupPermissions(Long groupId) {
		AccountDto loginAccount = accountService.getCurrentAccount();
		Accountgroup group = groupDao.getGroup(groupId);
		if(loginAccount!=null && group!=null && group.getPermissions()!=null && group.getPermissions().size()>0){
			
			if(group.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())){ // special cass for everyone group!!!
				if(loginAccount.isSystemDefaultAccount()){
					
					// return only the permissions from loginAccount's org
					List<Permission> permissions = new ArrayList<Permission>();
					for(Permission p : group.getPermissions()){
						permissions.add(mapper.map(p, Permission.class));
					}
					if(permissions.size()>0) return permissions;
				}
			}else // only read org's group's permission.
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz sys account can
				|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue()) // org's sys account can
			){
				List<Permission> permissions = new ArrayList<Permission>();
				for(Permission p : group.getPermissions()){
					permissions.add(mapper.map(p, Permission.class));
				}
				if(permissions.size()>0) return permissions;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse newPermissionNode(String target, String targetId, String newNodeName, String targetOrgUuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		Date now = new Date();
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		Account targetAccount = null;
		Accountgroup targetGroup = null;
		
		if(StringUtils.equals("account", target)){
			targetAccount = accountDao.getAccountByUuid(targetId);
//			if(targetAccount!=null) targetOrgId = targetAccount.getOrganization_id();
		}else if(StringUtils.equals("group", target)){
			targetGroup = groupDao.getGroup(Long.valueOf(targetId));
//			if(targetGroup!=null) targetOrgId = targetGroup.getOrganization_id();
		}
		
		Long targetOrgId = null;
		Organization targetOrg = orgDao.getOrgByUuid(targetOrgUuid);
		if(targetOrg!=null) targetOrgId = targetOrg.getId();
		
		if(loginAccount!=null && targetOrgId!=null){
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz sys account can do anything
				|| (targetGroup!=null && targetGroup.getGrouptype().equals(Accountgroup.GroupType.Everyone.name()) && loginAccount.isSystemDefaultAccount()) // system user can add permission for everyone group
				|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==targetOrgId.intValue()) // system user can add permission for org
			){
				
				//.......
				Permission permission = null;
				String uuid = UUID.randomUUID().toString();
				if(targetAccount!=null){
					permission = new Permission(null, uuid, newNodeName.trim(), targetAccount.getId(), null, null, null, targetOrgId, now);
				}else if(targetGroup!=null){
					permission = new Permission(null, uuid, newNodeName.trim(), null, targetGroup.getId(), null, null, targetOrgId, now);
				}
				
				if(permission!=null){
					Long permissionId = permissionDao.savePermission(permission);
					if(permissionId!=null){
						
						if(targetAccount!=null){
							targetAccount.addPermission(permission);
							accountDao.saveAccount(targetAccount);
						}else if(targetGroup!=null){
							targetGroup.addPermission(permission);
							groupDao.saveGroup(targetGroup);
						}
						
						apires.setSuccess(true);
						apires.setResponse1(uuid);
					}
				}
				
			}else{
				apires.setResponse1("You don't have the permission to add permission node!");
			}
			
		}else{
			apires.setResponse1("System can't find loginAccont or targetOrgId, you may need to refresh the page and try again.");
		}
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public <T> void updateTreeNodeForPermission(Class<T> theClass,
			TreeNode treeNode, Permission permissionDto,
			Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs,
			Map<Permission.Type, Boolean> selectablePermissionTypeMap) {
		
		if(permissionDto!=null && treeNode!=null){
			// find current node permissionedStuff (p, r, c, m)
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs = findCurrentNodePermissionedStuff(treeNode.getSystemName(), permissionDto);
			
			// find closest parent node permissioned stuffs
			String path = null;
			NodeDetail detail = null;
			if(theClass.equals(EntityDetail.class)){
				 detail = entityDao.getEntityDetailByUuid(treeNode.getSystemName());
				if(detail!=null) path = ((EntityDetail)detail).getPath();
			}else if(theClass.equals(ModuleDetail.class)){
				detail = sitedesignDao.getModuleDetailByUuid(treeNode.getSystemName());
				if(detail!=null) path = ((ModuleDetail)detail).getPath();
			}else if(theClass.equals(PageDetail.class)){
				detail = sitedesignDao.getPageDetailByUuid(treeNode.getSystemName());
				if(detail!=null) path = ((PageDetail)detail).getPath();
			}else if(theClass.equals(MediaDetail.class)){
				detail = mediaDao.getMediaDetailByUuid(treeNode.getSystemName());
				if(detail!=null) path = ((MediaDetail)detail).getPath();
			}
			if(closestParentNodePermissionedStuffs==null && StringUtils.isNotBlank(path)){
				closestParentNodePermissionedStuffs = findClosestParentNodePermissionedStuff(path, permissionDto);
			}
			
			// apply different permission's type based on permission and parent's permission
			StringBuilder updatedPrettyName = new StringBuilder(treeNode.getPrettyName());
			
			
			// check module's copy permission for entity and page
			if(theClass.equals(EntityDetail.class) && selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.copy)){
				GeneralSelectionType templatePermissionCheckResult = checkPermissionForEntityTemplateWhenEntityPermissionSetup(Permission.Type.copy, (EntityDetail)detail, permissionDto);
				
				if(templatePermissionCheckResult!=null && templatePermissionCheckResult.getSelected()!=null && !templatePermissionCheckResult.getSelected()){
					updatedPrettyName.append(templatePermissionCheckResult.getValue());
				}
				
			}else if(theClass.equals(PageDetail.class) && selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.copy)){
				GeneralSelectionType templatePermissionCheckResult = checkPermissionForPageDefaultModulesWhenPagePermissionSetup(Permission.Type.copy, (PageDetail)detail, permissionDto);
				
				if(templatePermissionCheckResult!=null && templatePermissionCheckResult.getSelected()!=null && !templatePermissionCheckResult.getSelected()){
					updatedPrettyName.append(templatePermissionCheckResult.getValue());
				}
				
			}
			
			
			updatedPrettyName.append("<span class='permissionSets' domvalue='").append(treeNode.getSystemName()).append("' >");
			
			
			// for preview
			if(selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.preview)!=null && selectablePermissionTypeMap.get(Permission.Type.preview)){
				if(currentNodePermissionedStuffs.get(Permission.Type.preview)!=null){
					if(currentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_p checkboxYes domReady_permissionSet' />");
					}else if(currentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_p checkboxNo domReady_permissionSet' />");
					}
				}else if(closestParentNodePermissionedStuffs!=null && closestParentNodePermissionedStuffs.get(Permission.Type.preview)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_p checkboxYesFollow domReady_permissionSet' />");
					}else if(closestParentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_p checkboxNoFollow domReady_permissionSet' />");
					}
				}else{
					updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_p checkboxEmpty domReady_permissionSet' />");
				}
			}
			
			// for read
			if(selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.read)!=null && selectablePermissionTypeMap.get(Permission.Type.read)){
				if(currentNodePermissionedStuffs.get(Permission.Type.read)!=null){
					if(currentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_r checkboxYes domReady_permissionSet' />");
					}else if(currentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_r checkboxNo domReady_permissionSet' />");
					}
				}else if(closestParentNodePermissionedStuffs!=null && closestParentNodePermissionedStuffs.get(Permission.Type.read)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_r checkboxYesFollow domReady_permissionSet' />");
					}else if(closestParentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_r checkboxNoFollow domReady_permissionSet' />");
					}
				}else{
					updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_r checkboxEmpty domReady_permissionSet' />");
				}
			}
			
			// for copy
			if(selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.copy)!=null && selectablePermissionTypeMap.get(Permission.Type.copy)){
				if(currentNodePermissionedStuffs.get(Permission.Type.copy)!=null){
					if(currentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_c checkboxYes domReady_permissionSet' />");
					}else if(currentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_c checkboxNo domReady_permissionSet' />");
					}
				}else if(closestParentNodePermissionedStuffs!=null && closestParentNodePermissionedStuffs.get(Permission.Type.copy)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_c checkboxYesFollow domReady_permissionSet' />");
					}else if(closestParentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_c checkboxNoFollow domReady_permissionSet' />");
					}
				}else{
					updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_c checkboxEmpty domReady_permissionSet' />");
				}
			}
			
			// for modify
			if(selectablePermissionTypeMap!=null && selectablePermissionTypeMap.get(Permission.Type.modify)!=null && selectablePermissionTypeMap.get(Permission.Type.modify)){
				if(currentNodePermissionedStuffs.get(Permission.Type.modify)!=null){
					if(currentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_m checkboxYes domReady_permissionSet' />");
					}else if(currentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_m checkboxNo domReady_permissionSet' />");
					}
				}else if(closestParentNodePermissionedStuffs!=null && closestParentNodePermissionedStuffs.get(Permission.Type.modify)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_m checkboxYesFollow domReady_permissionSet' />");
					}else if(closestParentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
						updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_m checkboxNoFollow domReady_permissionSet' />");
					}
				}else{
					updatedPrettyName.append("<button type='button' class='permissionBtn permissionBtn_m checkboxEmpty domReady_permissionSet' />");
				}
			}
			
			updatedPrettyName.append("</span>");
			
			// add permission icons to the prettyname
//			prettyname = prettyname 
//				+ "<span class='permissionSets' domvalue='"+treeNode.getSystemName()+"' >"
//				+ "<button type='button' class='permissionBtn permissionBtn_p checkboxEmpty domReady_permissionSet' />"
//				+ "<button type='button' class='permissionBtn permissionBtn_r checkboxEmpty domReady_permissionSet' />"
//				+ "<button type='button' class='permissionBtn permissionBtn_c checkboxEmpty domReady_permissionSet' />"
//				+ "<button type='button' class='permissionBtn permissionBtn_m checkboxEmpty domReady_permissionSet' />"
//				+ "</span>";
			
			if(theClass.equals(EntityDetail.class)){
				EntityTreeNode treeNodeImpl = (EntityTreeNode)treeNode;
				treeNodeImpl.setPrettyName(updatedPrettyName.toString());
				//String prettyname = entityTreeNode.getPrettyName();
			}else if(theClass.equals(ModuleDetail.class)){
				ModuleTreeNode treeNodeImpl = (ModuleTreeNode)treeNode;
				treeNodeImpl.setPrettyName(updatedPrettyName.toString());
			}else if(theClass.equals(PageDetail.class)){
				PageTreeNode treeNodeImpl = (PageTreeNode)treeNode;
				treeNodeImpl.setPrettyName(updatedPrettyName.toString());
			}else if(theClass.equals(MediaDetail.class)){
				MediaTreeNode treeNodeImpl = (MediaTreeNode)treeNode;
				treeNodeImpl.setPrettyName(updatedPrettyName.toString());
			}
			
		}
	}

	@Override
	@Transactional
	public ApiResponse updatePermissionStuff(String permissionUuid, String stuffUuid, Type permissionType, String permissionValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		// determine PermissionedStuff.category
		PermissionedStuff.Category category = null;
		ModuleDetail moduleDetail = sitedesignDao.getModuleDetailByUuid(stuffUuid);
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(stuffUuid);
		PageDetail pageDetail = sitedesignDao.getPageDetailByUuid(stuffUuid);
		MediaDetail mediaDetail = mediaDao.getMediaDetailByUuid(stuffUuid);
		Long stuffOrgId = null;
		String stuffParentUuid = null;
		if(moduleDetail!=null){
			category = PermissionedStuff.Category.moduledetail;
			stuffOrgId = moduleDetail.getOrganization_id();
			stuffParentUuid = moduleDetail.getParentuuid();
		}else if(entityDetail!=null){
			category = PermissionedStuff.Category.product;
			stuffOrgId = entityDetail.getOrganization_id();
			stuffParentUuid = entityDetail.getParentuuid();
		}else if(pageDetail!=null){
			category = PermissionedStuff.Category.page;
			stuffOrgId = pageDetail.getOrganization_id();
			stuffParentUuid = pageDetail.getParentuuid();
		}else if(mediaDetail!=null){
			category = PermissionedStuff.Category.media;
			stuffOrgId = mediaDetail.getOrganization_id();
			stuffParentUuid = mediaDetail.getParentuuid();
		}
		
		// get permission
		Permission permission = permissionDao.getPermissionByUuid(permissionUuid);
		
		if(loginAccount!=null && permission!=null && permissionType!=null && category!=null && StringUtils.isNotBlank(permissionValue)){
			
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz sys account can do anything
				|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==stuffOrgId.intValue()) // org's sys can do
			){
				
				// do add or remove permissionedStuff based on permissionValue: checkboxYes, checkboxNo, checkboxEmpty, checkboxYesFollow, checkboxNoFollow
				// checkboxYesFollow, checkboxNoFollow, checkboxEmpty: remove stuff
				// checkboxYes, checkboxNo: add or update stuff
				if(permissionValue.equals("checkboxYesFollow") || permissionValue.equals("checkboxNoFollow") || permissionValue.equals("checkboxEmpty")){
					if(permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
						PermissionedStuff delStuff = null;
						for(PermissionedStuff s : permission.getPermissionedStuffs()){
							if(s.getPointuuid().equals(stuffUuid) && s.getPermissiontype().equals(permissionType.name())){
								delStuff = s;
								break;
							}
						}
						permission.getPermissionedStuffs().remove(delStuff);
						permissionDao.savePermission(permission);
					}
					
				}else if(permissionValue.equals("checkboxYes") || permissionValue.equals("checkboxNo")){
					PermissionedStuff.AllowDeny allowDeny = null;
					if(permissionValue.equals("checkboxYes")){
						allowDeny = PermissionedStuff.AllowDeny.allow; 
					}else if(permissionValue.equals("checkboxNo")){
						allowDeny = PermissionedStuff.AllowDeny.deny;
					}
					
					if(permission.getPermissionedStuffs()!=null && permission.getPermissionedStuffs().size()>0){
						PermissionedStuff existStuff = null;
						for(PermissionedStuff s : permission.getPermissionedStuffs()){
							if(s.getPointuuid().equals(stuffUuid) && s.getPermissiontype().equals(permissionType.name())){
								existStuff = s;
								break;
							}
						}
						if(existStuff!=null){
							existStuff.setAllowdeny(allowDeny.getCode());
							permissionDao.savePermissionedStuff(existStuff);
							
						}else{
							PermissionedStuff stuff = new PermissionedStuff(null, category.name(), permissionType.name(), allowDeny.getCode(), stuffUuid, stuffParentUuid, permission.getUuid(), stuffOrgId);
							Long stuffId = permissionDao.savePermissionedStuff(stuff);
							if(stuffId!=null){
								permission.addPermissionedStuff(stuff);
								permissionDao.savePermission(permission);
							}
						}
						
					}else{
						PermissionedStuff stuff = new PermissionedStuff(null, category.name(), permissionType.name(), allowDeny.getCode(), stuffUuid, stuffParentUuid, permission.getUuid(), stuffOrgId);
						Long stuffId = permissionDao.savePermissionedStuff(stuff);
						if(stuffId!=null){
							permission.addPermissionedStuff(stuff);
							permissionDao.savePermission(permission);
						}
					}
				}
				apires.setSuccess(true);
				
			}else{
				apires.setResponse1("You don't have permission to update the value");
			}
			
		}else{
			apires.setResponse1("Not enough information to set the permission stuff: permissionUuid: "+permissionUuid+", stuffUuid: "+stuffUuid+", permissionType: "+permissionType+", permissionValue: "+permissionValue);
		}
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public Permission getPermissionDto(String permissionUuid) {
		Permission permission = permissionDao.getPermissionByUuid(permissionUuid);
		if(permission!=null){
			return mapper.map(permission, Permission.class);
		}
		return null;
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public Map<Type, PermissionedStuff> findClosestParentNodePermissionedStuff(String path, Permission permissionDto) {
		if(permissionDto!=null){
			
			Map<Type, PermissionedStuff> resultMap = new HashMap<Permission.Type, PermissionedStuff>();
			
			if(permissionDto.getPermissionedStuffs()!=null && permissionDto.getPermissionedStuffs().size()>0 && path!=null){
				String[] nodeAcestorsId = path.split("/"); // the distance in the array is from Farest - Closest.
				for(int i=nodeAcestorsId.length-1; i>=0; i--){
					if(resultMap.size()==4){
						break;
					}
					
					for(PermissionedStuff s : permissionDto.getPermissionedStuffs()){
						if(resultMap.size()==4){
							break;
						}
						
						if(s.getPointuuid().equals(nodeAcestorsId[i])){
							if(s.getPermissiontype().equals(Permission.Type.preview.name())){
								if(resultMap.get(Permission.Type.preview)==null){
									resultMap.put(Permission.Type.preview, s);
								}
							}else if(s.getPermissiontype().equals(Permission.Type.read.name())){
								if(resultMap.get(Permission.Type.read)==null){
									resultMap.put(Permission.Type.read, s);
								}
							}else if(s.getPermissiontype().equals(Permission.Type.copy.name())){
								if(resultMap.get(Permission.Type.copy)==null){
									resultMap.put(Permission.Type.copy, s);
								}
							}else if(s.getPermissiontype().equals(Permission.Type.modify.name())){
								if(resultMap.get(Permission.Type.modify)==null){
									resultMap.put(Permission.Type.modify, s);
								}
							}
						}
						
						
					}
				}
				
			}
			return resultMap;
		}
		
		return null;
	}

	@Override
	public Map<Type, PermissionedStuff> findCurrentNodePermissionedStuff(String nodeUuid, Permission permissionDto) {
		if(StringUtils.isNotBlank(nodeUuid) && permissionDto!=null){
			Map<Permission.Type, PermissionedStuff> resultMap = new HashMap<Permission.Type, PermissionedStuff>();
			
			if(permissionDto.getPermissionedStuffs()!=null && permissionDto.getPermissionedStuffs().size()>0){
				for(PermissionedStuff s : permissionDto.getPermissionedStuffs()){
					if(resultMap.size()==4){
						break;
					}
					
					if(s.getPointuuid().equals(nodeUuid)){
						if(s.getPermissiontype().equals(Permission.Type.preview.name())){
							//currentNodePermissionedStuff_p = s;
							resultMap.put(Permission.Type.preview, s);
						}else if(s.getPermissiontype().equals(Permission.Type.read.name())){
							resultMap.put(Permission.Type.read, s);
						}else if(s.getPermissiontype().equals(Permission.Type.copy.name())){
							resultMap.put(Permission.Type.copy, s);
						}else if(s.getPermissiontype().equals(Permission.Type.modify.name())){
							resultMap.put(Permission.Type.modify, s);
						}
					}
				}
			}
			
			return resultMap;
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Permission getPermissionByUuid(String permissionUuid) {
		return permissionDao.getPermissionByUuid(permissionUuid);
	}

	@Override
	@Transactional
	public ApiResponse updatePermissionValue(String permissionUuid, String valueName, String updateValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		Permission permission = permissionDao.getPermissionByUuid(permissionUuid);
		
		if(loginAccount!=null && loginAccount.isSystemDefaultAccount() && permission!=null && StringUtils.isNotBlank(valueName)){
			
			boolean updated = false;
			if(valueName.equals("startdate")){
				
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
				if(startdate!=null && permission.getEnddate()!=null){
					if(startdate.after(permission.getEnddate())){
						passed = false;
						apires.setResponse1("'From Date' can't be after 'To Date'!");
					}
				}
				if(passed){
					permission.setStartdate(startdate);
					updated = true;
				}
			}else if(valueName.equals("enddate")){
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
				if(endDate!=null && permission.getStartdate()!=null){
					if(endDate.before(permission.getStartdate())){
						passed = false;
						apires.setResponse1("'From Date' can't be after 'To Date'!");
					}
				}
				if(passed){
					permission.setEnddate(endDate!=null?DateUtils.getEnd(endDate):null);
					updated = true;
				}
			}else if(valueName.equals("prettyname")){
				boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(updateValue);
				if(nameVali){
					permission.setName(updateValue!=null?updateValue.trim():null);
					updated = true;
				}else{
					apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
				}
			}
			
			if(updated){
				Long id = permissionDao.savePermission(permission);
				if(id!=null){
					apires.setSuccess(true);
					apires.setResponse1(updateValue);
				}
			}
			
		}else{
			apires.setResponse1("System don't have enough information to update the permission, permissionUuid: "+permissionUuid+", valueName: "+valueName);
		}
		
		return apires;
	}

	@Override
	public void permissionsMerge(Permission holder, Permission plus, boolean permissionIsUsingCheck) {
		Date now = new Date();
		
		// create the holder if holder is null;
		if(holder==null){
			holder = new Permission();
			holder.setCreatedate(now);
			holder.setStartdate(now);
		}else {
			holder.setStartdate(now);
		}
		
//		# if startdate is null and enddate is null too, this means the schedule doesn't setup proper yet!!!
//		# if startdate is null but enddate isn't null, this means "from now to enddate".
//		# if startdate isn't null but enddate is null, this means "from startdate to forever"
		if(plus==null) return;
		
//		if(plus.getStartdate()==null && plus.getEnddate()==null) return;
//		if(plus.getStartdate()!=null && plus.getStartdate().after(now)) return; // not begin yet!
//		if(plus.getEnddate()!=null && plus.getEnddate().before(now)) return; // time passed yet!

		if(permissionIsUsingCheck){
			if(!plus.isPermissionInUsing()) return;
		}
		
		if(plus.getPermissionedStuffs()==null || plus.getPermissionedStuffs().size()<1) return;
		
		// update the holder's endate: always use the closest date for holder's enddate
		if(holder.getEnddate()==null && plus.getEnddate()!=null) holder.setEnddate(new Date(plus.getEnddate().getTime()));
		if(holder.getEnddate()!=null && plus.getEnddate()!=null && holder.getEnddate().after(plus.getEnddate())){
			holder.setEnddate(new Date(plus.getEnddate().getTime()));
		}
		
		// update the permissionedStuffs
		for(PermissionedStuff s : plus.getPermissionedStuffs()){
			Permission.Type type = Permission.Type.getTypeByName(s.getPermissiontype());
			PermissionedStuff existStuff = holder.getPermissionedStuffForPointWithType(s.getPointuuid(), type); 
			if(existStuff==null){
				holder.addPermissionedStuff(mapper.map(s, PermissionedStuff.class));
			}else{
				if(s.getAllowdeny().equals(PermissionedStuff.AllowDeny.deny.getCode())){
					existStuff.setAllowdeny(PermissionedStuff.AllowDeny.deny.getCode());
					existStuff.setId(s.getId());
					existStuff.setPermissionuuid(s.getPermissionuuid());
				}
			}
		}
		
	}

	@Override
	@Transactional
	public Long newPermission(Permission permission) {
		if(permission!=null && (permission.getAccount_id()!=null || permission.getGroup_id()!=null) && StringUtils.isNotBlank(permission.getName())){
			return permissionDao.savePermission(permission);
		}
		return null;
	}

	@Override
	@Transactional
	public Permission addGroupPermission(Long groupId, Permission permission) {
		Accountgroup group = groupDao.getGroup(groupId);
		if(group!=null && permission!=null){
			if(permission.getId()!=null){
				boolean isPermissionExist = false;
				if(group.getPermissions()!=null && group.getPermissions().size()>0){
					for(Permission p : group.getPermissions()){
						if(p.getId().intValue()==permission.getId().intValue()){
							isPermissionExist = true;
							break;
						}
					}
				}
				if(!isPermissionExist){
					group.addPermission(permission);
					groupDao.saveGroup(group);
				}
			}else{
				group.addPermission(permission);
				groupDao.saveGroup(group);
			}
			
			return permissionDao.getPermissionByUuid(permission.getUuid());
		}
		
		return null;
	}

	@Override
	@Transactional
	public void addPermissionedStuffsToPermission(Long permissionId, List<PermissionedStuff> pstuffs) {
		Permission permission = permissionDao.getPermissionById(permissionId);
		if(permission!=null){
			List<PermissionedStuff> addStuffs = new ArrayList<PermissionedStuff>();
			for(PermissionedStuff s : pstuffs){
				if(s.getId()==null || (s.getId()!=null && permission.getPermissionedStuffByStuffid(s.getId())==null)){
					addStuffs.add(s);
				}
			}
			
			if(addStuffs.size()>0){
				for(PermissionedStuff s : addStuffs){
					permission.addPermissionedStuff(s);
				}
				permissionDao.savePermission(permission);
			}
			
		}
	}

	@Override
	public Map<Permission.Type, Boolean> getPermissionTypesValueByCurrentAndClosestParentPermissionedStuffs(
			Map<Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs){
		Map<Permission.Type, Boolean> result = new HashMap<Permission.Type, Boolean>();
		
		// get preview, read, copy, modify permission from current or closestParent
		Boolean previewAllow = null;
		Boolean readAllow = null;
		Boolean copyAllow = null;
		Boolean modifyAllow = null;
		
		if(currentNodePermissionedStuffs!=null && currentNodePermissionedStuffs.size()>0){
			if(currentNodePermissionedStuffs.get(Permission.Type.preview)!=null){
				if(currentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
					previewAllow = Boolean.TRUE;
				}else{
					previewAllow = Boolean.FALSE;
				}
				
			}
			
			if(currentNodePermissionedStuffs.get(Permission.Type.read)!=null){
				if(currentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
					readAllow = Boolean.TRUE;
				}else{
					readAllow = Boolean.FALSE;
				}
				
			}
			
			if(currentNodePermissionedStuffs.get(Permission.Type.copy)!=null){
				if(currentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
					copyAllow = Boolean.TRUE;
				}else{
					copyAllow = Boolean.FALSE;
				}
				
			}
			
			if(currentNodePermissionedStuffs.get(Permission.Type.modify)!=null){
				if(currentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
					modifyAllow = Boolean.TRUE;
				}else{
					modifyAllow = Boolean.FALSE;
				}
			}
		}
		
		if(closestParentNodePermissionedStuffs!=null && closestParentNodePermissionedStuffs.size()>0){
			if(previewAllow==null){
				if(closestParentNodePermissionedStuffs.get(Permission.Type.preview)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.preview).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						previewAllow = Boolean.TRUE;
					}else{
						previewAllow = Boolean.FALSE;
					}
				}
			}
			if(readAllow==null){
				if(closestParentNodePermissionedStuffs.get(Permission.Type.read)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.read).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						readAllow = Boolean.TRUE;
					}else{
						readAllow = Boolean.FALSE;
					}
				}
			}
			
			if(copyAllow==null){
				if(closestParentNodePermissionedStuffs.get(Permission.Type.copy)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.copy).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						copyAllow = Boolean.TRUE;
					}else{
						copyAllow = Boolean.FALSE;
					}
				}
			}
			if(modifyAllow==null){
				if(closestParentNodePermissionedStuffs.get(Permission.Type.modify)!=null){
					if(closestParentNodePermissionedStuffs.get(Permission.Type.modify).getAllowdeny().equals(PermissionedStuff.AllowDeny.allow.getCode())){
						modifyAllow = Boolean.TRUE;
					}else{
						modifyAllow = Boolean.FALSE;
					}
				}
			}
			
		}

		if(previewAllow!=null){
			result.put(Permission.Type.preview, previewAllow);
		}
		if(readAllow!=null){
			result.put(Permission.Type.read, readAllow);
		}
		if(copyAllow!=null){
			result.put(Permission.Type.copy, copyAllow);
		}
		if(modifyAllow!=null){
			result.put(Permission.Type.modify, modifyAllow);
		}
		
		return result;
	}
	
	
	@Override
	public Map<Permission.Type, Boolean> getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(
			Map<Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs){
		Map<Permission.Type, Boolean> result = new HashMap<Permission.Type, Boolean>();
		
		Map<Permission.Type, Boolean> independentValue = getPermissionTypesValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
		if(independentValue!=null && independentValue.size()>0){
			
			// for modify:
			boolean isRelatedModifyAllowed = false;
			if(independentValue.get(Permission.Type.modify)!=null && independentValue.get(Permission.Type.modify) 
					&& (independentValue.get(Permission.Type.preview)==null || independentValue.get(Permission.Type.preview)) 
					&& (independentValue.get(Permission.Type.read)==null || independentValue.get(Permission.Type.read))){
				isRelatedModifyAllowed = true;
			}
			
			// for copy:
			boolean isRelatedCopyAllowed = false;
			if(independentValue.get(Permission.Type.copy)!=null && independentValue.get(Permission.Type.copy) 
					&& (independentValue.get(Permission.Type.preview)==null || independentValue.get(Permission.Type.preview)) 
					&& (independentValue.get(Permission.Type.read)==null || independentValue.get(Permission.Type.read))){
				isRelatedCopyAllowed = true;
			}
			
			// for read:
			boolean isRelatedReadAllowed = false;
			if(independentValue.get(Permission.Type.read)!=null){
				if(independentValue.get(Permission.Type.read)
					&& (independentValue.get(Permission.Type.preview)==null || independentValue.get(Permission.Type.preview))
					){
					isRelatedReadAllowed = true;
				}
			}else{
				if((isRelatedModifyAllowed || isRelatedCopyAllowed)
					&& (independentValue.get(Permission.Type.preview)==null || independentValue.get(Permission.Type.preview))
					){
					isRelatedReadAllowed = true;
				}
			}
			
			// for preview
			boolean isRelatedPreviewAllowed = false;
			if(independentValue.get(Permission.Type.preview)!=null){
				if(independentValue.get(Permission.Type.preview)){
					isRelatedPreviewAllowed = true;
				}
			}else{
				if(isRelatedReadAllowed){
					isRelatedPreviewAllowed = true;
				}
			}
			
			result.put(Permission.Type.preview, isRelatedPreviewAllowed);
			result.put(Permission.Type.read, isRelatedReadAllowed);
			result.put(Permission.Type.copy, isRelatedCopyAllowed);
			result.put(Permission.Type.modify, isRelatedModifyAllowed);
			
		}else{
			result.put(Permission.Type.preview, Boolean.FALSE);
			result.put(Permission.Type.read, Boolean.FALSE);
			result.put(Permission.Type.copy, Boolean.FALSE);
			result.put(Permission.Type.modify, Boolean.FALSE);
		}
		
		return result;
	}
	
	
	@Override
	public boolean isNodeInListAllowed(boolean isFolder, 
			Map<Type, PermissionedStuff> currentNodePermissionedStuffs,
			Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs) {
		
		Boolean previewAllow = null;
		Boolean readAllow = null;
		Boolean copyAllow = null;
		Boolean modifyAllow = null;
		
		Map<Permission.Type, Boolean> permissionTypesValueByCurrentAndClosestParentPermissionedStuffs = getPermissionTypesValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs, closestParentNodePermissionedStuffs);
		if(permissionTypesValueByCurrentAndClosestParentPermissionedStuffs!=null && permissionTypesValueByCurrentAndClosestParentPermissionedStuffs.size()>0){
			previewAllow = permissionTypesValueByCurrentAndClosestParentPermissionedStuffs.get(Permission.Type.preview);
			readAllow = permissionTypesValueByCurrentAndClosestParentPermissionedStuffs.get(Permission.Type.read);
			copyAllow = permissionTypesValueByCurrentAndClosestParentPermissionedStuffs.get(Permission.Type.copy);
			modifyAllow = permissionTypesValueByCurrentAndClosestParentPermissionedStuffs.get(Permission.Type.modify);
		}
		
		// based on previewAllow, readAllow, copyAllow, modifyAllow to determine return result: true or false;
		if(previewAllow==null && isFolder) return true; // folder will not be shown only if previewAllow is false!!!
		if(previewAllow!=null && previewAllow) return true;
		if(previewAllow==null && readAllow!=null && readAllow) return true;
		if(previewAllow==null && readAllow==null && copyAllow!=null && copyAllow) return true;
		if(previewAllow==null && readAllow==null && modifyAllow!=null && modifyAllow) return true;
		
		return false;
		
	}

	@Override
	@Transactional(readOnly=true)
	public Map<Type, PermissionedStuff> getFullPermissionForModule(Long orgId) {
		ModuleDetail root = sitedesignDao.getModuleTreeRoot(orgId);
		if(root!=null){
			Map<Type, PermissionedStuff> result = new HashMap<Permission.Type, PermissionedStuff>();
			
			PermissionedStuff previewStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.moduledetail.name(), 
					Permission.Type.preview.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getModuleuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff readStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.moduledetail.name(), 
					Permission.Type.read.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getModuleuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff copyStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.moduledetail.name(), 
					Permission.Type.copy.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getModuleuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff modifyStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.moduledetail.name(), 
					Permission.Type.modify.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getModuleuuid(),
					root.getParentuuid(),
					null,
					orgId);
			
			result.put(Permission.Type.preview, previewStuff);
			result.put(Permission.Type.read, readStuff);
			result.put(Permission.Type.copy, copyStuff);
			result.put(Permission.Type.modify, modifyStuff);
			
			return result;
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<Type, PermissionedStuff> getFullPermissionForProduct(Long orgId) {
		EntityDetail root = entityDao.getProductTreeRoot(orgId);
		if(root!=null){
			Map<Type, PermissionedStuff> result = new HashMap<Permission.Type, PermissionedStuff>();
			
			PermissionedStuff previewStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.product.name(), 
					Permission.Type.preview.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getEntityuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff readStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.product.name(), 
					Permission.Type.read.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getEntityuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff copyStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.product.name(), 
					Permission.Type.copy.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getEntityuuid(),
					root.getParentuuid(),
					null,
					orgId);
			PermissionedStuff modifyStuff = new PermissionedStuff(null, 
					PermissionedStuff.Category.product.name(), 
					Permission.Type.modify.name(), 
					PermissionedStuff.AllowDeny.allow.getCode(), 
					root.getEntityuuid(),
					root.getParentuuid(),
					null,
					orgId);
			
			result.put(Permission.Type.preview, previewStuff);
			result.put(Permission.Type.read, readStuff);
			result.put(Permission.Type.copy, copyStuff);
			result.put(Permission.Type.modify, modifyStuff);
			
			return result;
			
		}
		
		
		
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public Permission getMergedPermissionForAccount(Long accountId, boolean permissionIsUsingCheck){
		
		Account account = accountDao.getAccountPojoById(accountId);
		if(account!=null){
			// get merge
			Permission mergedPermission = new Permission();
			mergedPermission.setCreatedate(new Date());
			if(account.getAccountGroups()!=null && account.getAccountGroups().size()>0){
				for(Accountgroup g : account.getAccountGroups()){
					if(g.getPermissions()!=null && g.getPermissions().size()>0){
						for(Permission p : g.getPermissions()){
							permissionsMerge(mergedPermission, p, permissionIsUsingCheck);
						}
					}
				}
			}
			if(account.getPermissions()!=null && account.getPermissions().size()>0){
				for(Permission p : account.getPermissions()){
					permissionsMerge(mergedPermission, p, permissionIsUsingCheck);
				}
			}
			
			return mergedPermission;
			
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public Permission getMergedPermissionForGroups(boolean permissionIsUsingCheck, Long... groupids) {
		if(groupids!=null && groupids.length>0){
			
			Permission mergedPermission = new Permission();
			mergedPermission.setCreatedate(new Date());
			
			for(Long groupId : groupids){
				Accountgroup group = groupDao.getGroup(groupId);
				if(group!=null && group.getPermissions()!=null && group.getPermissions().size()>0){
					for(Permission p : group.getPermissions()){
						permissionsMerge(mergedPermission, p, permissionIsUsingCheck);
					}
				}
			}
			
			return mergedPermission;
		}
		
		return null;
	}
	

	

	@Override
	@Transactional(readOnly=true)
	public boolean isPermissionAllowed(Long accountId, Type permissionType, String targetUuid) {
		if(accountId!=null && permissionType!=null && StringUtils.isNotBlank(targetUuid)){
			Permission mergedPermission = getMergedPermissionForAccount(accountId, true);
			return isPermissionAllowed(mergedPermission, permissionType, targetUuid);
		}
		return false;
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public boolean isPermissionAllowedForGroup(Long groupId, Permission.Type permissionType, String targetUuid) {
		if(groupId!=null && permissionType!=null && StringUtils.isNotBlank(targetUuid)){
			Permission mergedPermission = getMergedPermissionForGroups(true, new Long[]{groupId});
			return isPermissionAllowed(mergedPermission, permissionType, targetUuid);
		}
		return false;
	}
	
	@Override
	@Transactional(readOnly=true)
	public boolean isPermissionAllowed(Permission mergedPermission, Permission.Type permissionType, String targetUuid){
		
		ModuleDetail moduledetail = sitedesignDao.getModuleDetailByUuid(targetUuid);
		EntityDetail entitydetail = entityDao.getEntityDetailByUuid(targetUuid);
		PageDetail pagedetail = sitedesignDao.getPageDetailByUuid(targetUuid);
		MediaDetail mediadetail = mediaDao.getMediaDetailByUuid(targetUuid);
		
		if(mergedPermission!=null && permissionType!=null && (moduledetail!=null || entitydetail!=null || pagedetail!=null || mediadetail!=null)){
			
			String path = null;
			if(moduledetail!=null) path = moduledetail.getPath();
			if(entitydetail!=null) path = entitydetail.getPath();
			if(pagedetail!=null) path = pagedetail.getPath();
			if(mediadetail!=null) path = mediadetail.getPath();
			
			return isPermissionAllowed(mergedPermission, permissionType, targetUuid, path);
			
		}
		return false;
	}

	@Override
	@Transactional(readOnly=true)
	public boolean isPermissionAllowed(Permission mergedPermission, Permission.Type permissionType, String targetUuid, String path){
		
		if(mergedPermission!=null && permissionType!=null && StringUtils.isNotBlank(targetUuid)){
			
			Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs_forMerged = findCurrentNodePermissionedStuff(targetUuid, mergedPermission);
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs_forMerged = findClosestParentNodePermissionedStuff(path, mergedPermission);
			
			Map<Permission.Type, Boolean> relatedPermissionValues = getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(currentNodePermissionedStuffs_forMerged, closestParentNodePermissionedStuffs_forMerged);
			
			if(relatedPermissionValues!=null && relatedPermissionValues.size()>0){
				return relatedPermissionValues.get(permissionType);
			}
			
		}
		return false;
		
	}
	
	@Override
	@Transactional
	public OrgCanJoin toggleOrgJoinGroupAllowDeny(String type, String targetOrgUuid, Long groupId) {
		Accountgroup group = groupDao.getGroup(groupId);
		Organization org = orgDao.getOrgByUuid(targetOrgUuid);
		if(group!=null && org!=null && StringUtils.isNotBlank(type)){
			
			OrgCanJoin ocj = null;
			if(group.getOrgCanJoins()!=null && group.getOrgCanJoins().size()>0){
				for(OrgCanJoin j : group.getOrgCanJoins()){
					if(j.getOrganization_id().intValue()==org.getId().intValue()){
						ocj = j;
						break;
					}
				}
			}
			
			if("allow".equals(type)){
				String joinKey = UUID.randomUUID().toString();
				if(ocj!=null) ocj.setJoinkey(joinKey);
				else {
					ocj = new OrgCanJoin(null, org.getId(), 0, joinKey);
					group.addOrgCanJoin(ocj);
				}
				groupDao.saveGroup(group);
				
				return ocj;
			}else if("deny".equals(type)){
				if(ocj!=null){
					group.getOrgCanJoins().remove(ocj);
					groupDao.saveGroup(group);
				}
			}
		}
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse orgCanJoinModify(String type, Long groupId, String targetOrgUuid, String valueName, String value) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(false);
		
		Accountgroup group = groupDao.getGroup(groupId);
		Organization org = orgDao.getOrgByUuid(targetOrgUuid);
		
		if(group!=null && org!=null && StringUtils.isNotBlank(type)){
			
			// find orgCanJoin in the group:
			OrgCanJoin ocj = null;
			if(group.getOrgCanJoins()!=null && group.getOrgCanJoins().size()>0){
				for(OrgCanJoin j : group.getOrgCanJoins()){
					if(j.getOrganization_id().intValue()==org.getId().intValue()){
						ocj = j;
						break;
					}
				}
			}
			
			if(ocj!=null){
				
				if(type.equals("refreshJoinKey")){ // refresh the join key:
					String newjoinkey = UUID.randomUUID().toString();
					ocj.setJoinkey(newjoinkey);
					Long id = permissionDao.saveOrgCanJoin(ocj);
					
					if(id!=null){
						res.setSuccess(true);
						res.setResponse1(newjoinkey);
						return res;
					}else{
						res.setResponse1("System can't update the key, try to refresh the page and update again!");
					}
				}else if(type.equals("valueUpdate")) { // refresh the orgCanJoin value
					if(StringUtils.isNotBlank(valueName)){
						if(valueName.equals("totalaccountcanjoin")){ // update totalaccountcanjoin
							if(NumberUtils.isDigits(value)){
								Integer totalAccountCanJoin = Integer.valueOf(value);
								ocj.setTotalaccountcanjoin(totalAccountCanJoin);
								
								Long id = permissionDao.saveOrgCanJoin(ocj);
								if(id!=null){
									res.setSuccess(true);
									res.setResponse1(value);
									return res;
								}else{
									res.setResponse1("System can't update the totalAccountCanJoin field, try to refresh the page and update again!");
								}
								
							}else{
								res.setResponse1("Not a digital value.");
							}
						}
						
						
					}else{
						res.setResponse1("Update field name is not provided!");
					}
				}
				
			}else{
				res.setResponse1("System can find OrgCanJoin records by groupId: "+groupId+" orgId: "+targetOrgUuid);
			}
			
		}else{
			res.setResponse1("System can't fnd group, organization and/or type by groupid: "+groupId+" organizationId: "+targetOrgUuid+" type: "+type);
		}
		
		return res;
	}

	@Override
	@Transactional(readOnly=true)
	public GeneralSelectionType checkPermissionForEntityTemplateWhenEntityPermissionSetup(Type permissionType, EntityDetail entityDetail, Permission permission) {
		if(permissionType!=null && entityDetail!=null && permission!=null){
			
			// used for find permission is for account or group
			Long targetAccountId = permission.getAccount_id();
			Long targetGroupId = permission.getGroup_id();
			
			if(targetAccountId!=null || targetGroupId!=null){ // either for account or for group!
				
				if(targetAccountId!=null){
					// find entity is allow or deny for permissiontype for targetAccount
					Account targetAccount = accountDao.getAccountPojoById(targetAccountId);
					if(targetAccount!=null){
						// mergedPermission: used for find permission for moduleDetail
						Permission mergedPermission = getMergedPermissionForAccount(targetAccountId, true);
						
						// permissionAllowForEntity to use permission instead of mergedPermission: only need to detect if entity is allow or deny on current permission setup!
						boolean permissionAllowForEntity = isPermissionAllowed(permission, permissionType, entityDetail.getEntityuuid());
						
						if(permissionAllowForEntity){
							
							ModuleDetail moduledetail = sitedesignDao.getModuleDetailByUuid(entityDetail.getModuleuuid());
							
							if(moduledetail!=null){
								boolean permissionAllow = isPermissionAllowed(mergedPermission, permissionType, entityDetail.getModuleuuid());
								if(permissionAllow){
									return new GeneralSelectionType(entityDetail.getEntityuuid(), 
											permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is allow, and the permission for entity's template (module \""+moduledetail.getPrettyname()+"\") is allow too for account ("+targetAccount.getLoginname()+")", 
											Boolean.TRUE);
								}else{
									
									
									//<span domvalue="{&quot;topOffset&quot;:20, &quot;leftOffset&quot;:-30, &quot;ajaxCall&quot;:{&quot;url&quot;:&quot;/getModuleDetailInfo&quot;, &quot;params&quot;:&quot;moduleid=ff71c345-5b7a-4417-9019-44038221afe1&quot;}}" style="color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;" class="detailInfoPopup">1</span>
									
									StringBuilder resulthtml = new StringBuilder();
									resulthtml.append("<span style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' class='detailInfoPopup'");
									
									resulthtml.append(" domvalue='{&quot;topOffset&quot;:20, &quot;leftOffset&quot;:-30, &quot;popupContent&quot;:&quot;");

//									resulthtml.append("adfa adsf adsf adfa");
									resulthtml.append(permissionType.name()).append(" permission for the entity (").append(entityDetail.getName()).append(") is allow, ");
									resulthtml.append("but the permission for entity&#39;s template (module: ").append(moduledetail.getPrettyname()).append(") is deny for account (").append(targetAccount.getLoginname()).append(")<br/>");
									resulthtml.append("The reasons for template (module: ").append(moduledetail.getPrettyname()).append(") be permission deny are:<br/>");
									resulthtml.append("1. permission is not set properly for the template(module) or it&#39;s parent folders for account and account&#39;s groups.<br/>");
									resulthtml.append("2. the From and To date are not set properly for permissions.<br/>");
									
									resulthtml.append("&quot;}'");
									
									resulthtml.append(">");
									resulthtml.append("?</span>");
									
									return new GeneralSelectionType(entityDetail.getEntityuuid(), 
											resulthtml.toString(), 
											Boolean.FALSE);
								}
							}else{
								return new GeneralSelectionType(entityDetail.getEntityuuid(), 
										permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is allow, and there has no template (module) selected for entity, so the entity is "+permissionType.name()+" enabled.", 
										Boolean.TRUE);
							}
							
						}else{
							return new GeneralSelectionType(entityDetail.getEntityuuid(), 
									permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is deny, there are no necessary to check "+permissionType.name()+" permission for entity template(module).", 
									Boolean.TRUE);
						}
					}
					
					
					
				}else if(targetGroupId!=null){
					// find entity's module is allow or deny for permissiontype for targetGroup and everyone group (always including everyone group)
					Accountgroup group = groupDao.getGroup(targetGroupId);
					if(group!=null){
						Accountgroup everyonegroup = accountService.getEveryoneGroup();
						Set<Long> groupIds = new HashSet<Long>();
						groupIds.add(everyonegroup.getId());
						if(everyonegroup.getId().intValue()!=targetGroupId.intValue()){
							groupIds.add(targetGroupId);
						}
						
						Permission mergedPermission = getMergedPermissionForGroups(true, groupIds.toArray(new Long[0]));
						// permissionAllowForEntity to use permission instead of mergedPermission: only need to detect if entity is allow or deny on current permission setup!
						boolean permissionAllowForEntity = isPermissionAllowed(permission, permissionType, entityDetail.getEntityuuid());
						
						if(permissionAllowForEntity){
							
							ModuleDetail moduledetail = sitedesignDao.getModuleDetailByUuid(entityDetail.getModuleuuid());
							
							if(moduledetail!=null){
								boolean permissionAllow = isPermissionAllowed(mergedPermission, permissionType, entityDetail.getModuleuuid());
								if(permissionAllow){
									return new GeneralSelectionType(entityDetail.getEntityuuid(), 
											permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is allow, and the permission for entity's template (module \""+moduledetail.getPrettyname()+"\") is allow too.", 
											Boolean.TRUE);
								}else {
									
									
									StringBuilder resulthtml = new StringBuilder();
									resulthtml.append("<span style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' class='detailInfoPopup'");
									
									resulthtml.append(" domvalue='{&quot;topOffset&quot;:20, &quot;leftOffset&quot;:-30, &quot;popupContent&quot;:&quot;");

//									resulthtml.append("adfa adsf adsf adfa");
									resulthtml.append(permissionType.name()).append(" permission for the entity (").append(entityDetail.getName()).append(") is allow, ");
									resulthtml.append("but the permission for entity&#39;s template (module: ").append(moduledetail.getPrettyname()).append(") is deny for group(s) (").append(group.getGroupname()).append(group.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())?"":"and/or Everyone group").append(")<br/>");
									resulthtml.append("The reasons for template (module: ").append(moduledetail.getPrettyname()).append(") be permission deny are:<br/>");
									resulthtml.append("1. permission is not set properly for the template(module) or it&#39;s parent folders for groups.<br/>");
									resulthtml.append("2. the From and To date are not set properly for permissions.<br/>");
									
									resulthtml.append("&quot;}'");
									
									resulthtml.append(">");
									resulthtml.append("?</span>");
									
									
									
									
									
									
									return new GeneralSelectionType(entityDetail.getEntityuuid(), 
											resulthtml.toString(), 
											Boolean.FALSE);
								}
								
							}else{
								return new GeneralSelectionType(entityDetail.getEntityuuid(), 
										permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is allow, and there has no template (module) selected for entity, so the entity is "+permissionType.name()+" enabled.", 
										Boolean.TRUE);
							}
							
							
						}else{
							return new GeneralSelectionType(entityDetail.getEntityuuid(), 
									permissionType.name()+" permission for the entity ("+entityDetail.getName()+") is deny, there are no necessary to check "+permissionType.name()+" permission for entity template(module).", 
									Boolean.TRUE);
						}
					}
					
				}
				
			}
			
		}
		
		
		return null;
	}

	@Override
	public GeneralSelectionType checkPermissionForPageDefaultModulesWhenPagePermissionSetup(Type permissionType, PageDetail pageDetail, Permission permission) {
		
		if(permissionType!=null && pageDetail!=null && permission!=null){
			
			Long targetAccountId = permission.getAccount_id();
			Long targetGroupId = permission.getGroup_id();
			
			if(targetAccountId!=null || targetGroupId!=null){ // either for account or for group!
				
				boolean permissionAllowForPage = isPermissionAllowed(permission, permissionType, pageDetail.getPageuuid());
				
				if(!permissionAllowForPage){
					return new GeneralSelectionType(pageDetail.getPageuuid(), 
							permissionType.name()+" permission for the page ("+pageDetail.getPrettyname()+") is deny, there are no necessary to check "+permissionType.name()+" permission for all default modules used on the page", 
							Boolean.TRUE);
				}else{ // system need to check all page modules for the same permission type.
					
					// get all modules used on the page (for leaf containers)
					Set<String> usedModuleDetails = new HashSet<String>();
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new ContainerToPageDetailConvertor());
					stream.processAnnotations(new Class[]{ContainerTreeNode.class});
					
					ContainerTreeNode containerRoot = null;
					
					if(StringUtils.isNotBlank(pageDetail.getDetail())){
						containerRoot = (ContainerTreeNode)stream.fromXML(pageDetail.getDetail());
					}

					List<ContainerTreeNode> leafs = new ArrayList<ContainerTreeNode>();
					siteDesignService.findLeafsFromContainerTreeRoot(containerRoot, leafs);
					if(leafs!=null && leafs.size()>0){
						for(ContainerTreeNode cn : leafs){
							ContainerDetail containerDetail = sitedesignDao.getContainerDetailByUuid(cn.getSystemName());
							if(containerDetail!=null && StringUtils.isNotBlank(containerDetail.getModuleuuid())){
								usedModuleDetails.add(containerDetail.getModuleuuid());
							}
						}
					}
					
					if(usedModuleDetails.size()>0){
						
						if(targetAccountId!=null){
							
							Account targetAccount = accountDao.getAccountPojoById(targetAccountId);
							Permission mergedPermission = getMergedPermissionForAccount(targetAccountId, true);
							Boolean pass = null;
							
							StringBuilder resulthtml = new StringBuilder();
							
							resulthtml.append("<span style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' class='detailInfoPopup'");
							resulthtml.append(" domvalue='{&quot;topOffset&quot;:20, &quot;leftOffset&quot;:-30, &quot;popupContent&quot;:&quot;");
							
							for(String mid : usedModuleDetails){
								boolean permissionAllow = isPermissionAllowed(mergedPermission, permissionType, mid);
								if(permissionAllow){
									if(pass==null) pass = Boolean.TRUE;
									resulthtml.append(permissionType.name()+" permission for moduledetail ("+mid+") is allow.<br/>");
								}else{
									ModuleDetail moduledetail = sitedesignDao.getModuleDetailByUuid(mid);
									
									if(pass==null || pass.equals(Boolean.TRUE)){
										pass = Boolean.FALSE;
									}
									resulthtml.append(permissionType.name()).append(" permission for moduledetail (");
									if(moduledetail!=null){
										resulthtml.append(moduledetail.getPrettyname()).append(":");
									}
									
									resulthtml.append(mid).append(") is deny.<br/>");
								}
							}
							
							resulthtml.append("&quot;}'");
							resulthtml.append(">");
							resulthtml.append("?</span>");
							
							return new GeneralSelectionType(pageDetail.getPageuuid(), 
									resulthtml.toString(), 
									pass);
							
						}else if(targetGroupId!=null){
							
							// note: always including everyone group:
							
							Accountgroup everyonegroup = accountService.getEveryoneGroup();
							Set<Long> groupIds = new HashSet<Long>();
							groupIds.add(everyonegroup.getId());
							if(everyonegroup.getId().intValue()!=targetGroupId.intValue()){
								groupIds.add(targetGroupId);
							}
							
							Permission mergedPermission = getMergedPermissionForGroups(true, groupIds.toArray(new Long[0]));
							
							Boolean pass = null;
							
							StringBuilder resulthtml = new StringBuilder();
							resulthtml.append("<span style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' class='detailInfoPopup'");
							resulthtml.append(" domvalue='{&quot;topOffset&quot;:20, &quot;leftOffset&quot;:-30, &quot;popupContent&quot;:&quot;");
						
							for(String mid : usedModuleDetails){
								boolean permissionAllow = isPermissionAllowed(mergedPermission, permissionType, mid);
								if(permissionAllow){
									if(pass==null) pass = Boolean.TRUE;
									resulthtml.append(permissionType.name()+" permission for moduledetail ("+mid+") is allow.<br/>");
								}else{
									ModuleDetail moduledetail = sitedesignDao.getModuleDetailByUuid(mid);
									
									if(pass==null || pass.equals(Boolean.TRUE)){
										pass = Boolean.FALSE;
									}

									resulthtml.append(permissionType.name()).append(" permission for moduledetail (");
									if(moduledetail!=null){
										resulthtml.append(moduledetail.getPrettyname()).append(":");
									}
									
									resulthtml.append(mid).append(") is deny.<br/>");
									
								}
							}
							
							resulthtml.append("&quot;}'");
							resulthtml.append(">");
							resulthtml.append("?</span>");
							
							return new GeneralSelectionType(pageDetail.getPageuuid(), 
									resulthtml.toString(), pass);
						}
						
					}else{
						return new GeneralSelectionType(pageDetail.getPageuuid(), 
								permissionType.name()+" permission for the page ("+pageDetail.getPrettyname()+") is allow, and there has no any modules find for page's containers, so the page is "+permissionType.name()+" enabled.", 
								Boolean.TRUE);
					}
				}
			}
			
		}
		
		
		
		
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ApiResponse getExtraInfoForPermissionStuffUpdate(String permissionUuid, String stuffUuid, Type permissionType) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(true);
		
		// get permission
		Permission permission = permissionDao.getPermissionByUuid(permissionUuid);
		
		EntityDetail entityDetail = entityDao.getEntityDetailByUuid(stuffUuid);
		PageDetail pageDetail = sitedesignDao.getPageDetailByUuid(stuffUuid);
		
		// check sharable possibility for modify copy permission on product/page
		if(permissionType.equals(Permission.Type.copy) && (entityDetail!=null || pageDetail!=null)){
			
			// find all subNodes under current detail:
			// and put into detailsFamily list
			List<NodeDetail> detailsFamily = new ArrayList<NodeDetail>();
			// list to hold all nodes' sharable info
			List<GeneralSelectionType> sharableInfoForDetailsFamily = new ArrayList<GeneralSelectionType>();
			
			if(entityDetail!=null){
				detailsFamily.add(entityDetail);
				
				List<NodeDetail> entitydetailChildrens = entityDao.findEntityDetailsUnderFolder(entityDetail.getEntityuuid());
				if(entitydetailChildrens!=null && entitydetailChildrens.size()>0){
					detailsFamily.addAll(entitydetailChildrens);
				}
			}else if(pageDetail!=null){
				detailsFamily.add(pageDetail);
				
				List<NodeDetail> pagedetailChildrens = sitedesignDao.findPageDetailsUnderFolder(pageDetail.getPageuuid());
				if(pagedetailChildrens!=null && pagedetailChildrens.size()>0){
					detailsFamily.addAll(pagedetailChildrens);
				}
			}
			
			if(detailsFamily.size()>0){
				Class theClass = detailsFamily.get(0).getClass();
				
				List<String> usedModuleDetails = null; // hold all moduleDetail's uuids used for entityDetail or pageDetail.
				for(NodeDetail nd : detailsFamily){
					GeneralSelectionType permissionCheckResult = null;
					if(theClass.equals(EntityDetail.class)){
						permissionCheckResult = checkPermissionForEntityTemplateWhenEntityPermissionSetup(Permission.Type.copy, (EntityDetail)nd, permission);
					}else if(theClass.equals(PageDetail.class)){
						permissionCheckResult = checkPermissionForPageDefaultModulesWhenPagePermissionSetup(Permission.Type.copy, (PageDetail)nd, permission);
					}
					if(permissionCheckResult!=null){
						sharableInfoForDetailsFamily.add(permissionCheckResult);
					}
					
				}
				
			}
			
			apires.setResponse1(sharableInfoForDetailsFamily);
			
			
			
			
		}
		
		
		return apires;
	}

	@Override
	@Transactional
	public void setFullPermissionForGroup(Long groupId) {
		Accountgroup group = groupDao.getGroup(groupId);
		if(group!=null){
			
			Organization org = orgDao.getOrganizationById(group.getOrganization_id());
			if(org!=null){
				Date now = new Date();
				
				// get root nodes for moduleDetail, productDetail, desktopPage, mobilePage
				ModuleDetail moduleRoot = sitedesignDao.getModuleTreeRoot(org.getId());
				EntityDetail entityRoot = entityDao.getProductTreeRoot(org.getId());
				PageDetail desktopRoot = sitedesignDao.getPageTreeRoot(org.getId(), PageDetail.Type.Desktop);
				PageDetail mobileRoot = sitedesignDao.getPageTreeRoot(org.getId(), PageDetail.Type.Mobile);
				MediaDetail mediaRoot = mediaDao.getMediaTreeRoot(org.getId());
				
				// remove all permissions if group has permissions
				if(group.getPermissions()!=null){

//					Set<Permission> groupPerms = group.getPermissions();
					
//					group.setPermissions(null);
					group.removeAllPermissions();
					groupDao.saveGroup(group);
					
//					for(Permission p : groupPerms){
//						permissionDao.delPermissionById(p.getId());
//					}
					
				}
				
				// set a new full privilege permission for the group.				
				String permissionUuid = UUID.randomUUID().toString(); 
				Permission permission = new Permission(null, 
						permissionUuid, 
						"full privilege", 
						null, 
						groupId, 
						now, 
						null, 
						org.getId(),
						now);
				
				// for module
				if(moduleRoot!=null){
					PermissionedStuff stuff_mp = new PermissionedStuff(null, 
							PermissionedStuff.Category.moduledetail.name(), 
							Permission.Type.preview.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							moduleRoot.getModuleuuid(),
							moduleRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					//pstuffs.add(stuff_mp);
					permission.addPermissionedStuff(stuff_mp);
					PermissionedStuff stuff_mr = new PermissionedStuff(null, 
							PermissionedStuff.Category.moduledetail.name(), 
							Permission.Type.read.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							moduleRoot.getModuleuuid(), 
							moduleRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mr);
					PermissionedStuff stuff_mc = new PermissionedStuff(null, 
							PermissionedStuff.Category.moduledetail.name(), 
							Permission.Type.copy.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							moduleRoot.getModuleuuid(), 
							moduleRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mc);
					PermissionedStuff stuff_mm = new PermissionedStuff(null, 
							PermissionedStuff.Category.moduledetail.name(), 
							Permission.Type.modify.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							moduleRoot.getModuleuuid(), 
							moduleRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mm);
				}
				// for product
				if(entityRoot!=null){
					PermissionedStuff stuff_pp = new PermissionedStuff(null, 
							PermissionedStuff.Category.product.name(), 
							Permission.Type.preview.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							entityRoot.getEntityuuid(), 
							entityRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_pp);
					PermissionedStuff stuff_pr = new PermissionedStuff(null, 
							PermissionedStuff.Category.product.name(), 
							Permission.Type.read.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							entityRoot.getEntityuuid(), 
							entityRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_pr);
					PermissionedStuff stuff_pc = new PermissionedStuff(null, 
							PermissionedStuff.Category.product.name(), 
							Permission.Type.copy.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							entityRoot.getEntityuuid(), 
							entityRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_pc);
					PermissionedStuff stuff_pm = new PermissionedStuff(null, 
							PermissionedStuff.Category.product.name(), 
							Permission.Type.modify.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							entityRoot.getEntityuuid(), 
							entityRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_pm);
				}
				// for page
				if(desktopRoot!=null){
					PermissionedStuff stuff_desk_p = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.preview.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							desktopRoot.getPageuuid(), 
							desktopRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_desk_p);
					
					PermissionedStuff stuff_desk_r = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.read.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							desktopRoot.getPageuuid(), 
							desktopRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_desk_r);
					
					PermissionedStuff stuff_desk_c = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.copy.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							desktopRoot.getPageuuid(), 
							desktopRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_desk_c);
					
					PermissionedStuff stuff_desk_m = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.modify.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							desktopRoot.getPageuuid(), 
							desktopRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_desk_m);
					
				}
				if(mobileRoot!=null){
					PermissionedStuff stuff_mobile_p = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.preview.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mobileRoot.getPageuuid(), 
							mobileRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_mobile_p);
					
					PermissionedStuff stuff_mobile_r = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.read.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mobileRoot.getPageuuid(), 
							mobileRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_mobile_r);
					
					PermissionedStuff stuff_mobile_c = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.copy.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mobileRoot.getPageuuid(), 
							mobileRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_mobile_c);
					
					PermissionedStuff stuff_mobile_m = new PermissionedStuff(null, 
							PermissionedStuff.Category.page.name(), 
							Permission.Type.modify.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mobileRoot.getPageuuid(), 
							mobileRoot.getParentuuid(),
							permissionUuid, 
							org.getId());
					permission.addPermissionedStuff(stuff_mobile_m);
					
				}
				// for media
				if(mediaRoot!=null){
					PermissionedStuff stuff_mp = new PermissionedStuff(null, 
							PermissionedStuff.Category.media.name(), 
							Permission.Type.preview.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mediaRoot.getMediauuid(), 
							mediaRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mp);
					PermissionedStuff stuff_mr = new PermissionedStuff(null, 
							PermissionedStuff.Category.media.name(), 
							Permission.Type.read.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mediaRoot.getMediauuid(), 
							mediaRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mr);
					PermissionedStuff stuff_mc = new PermissionedStuff(null, 
							PermissionedStuff.Category.media.name(), 
							Permission.Type.copy.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mediaRoot.getMediauuid(), 
							mediaRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mc);
					PermissionedStuff stuff_mm = new PermissionedStuff(null, 
							PermissionedStuff.Category.media.name(), 
							Permission.Type.modify.name(), 
							PermissionedStuff.AllowDeny.allow.getCode(), 
							mediaRoot.getMediauuid(), 
							mediaRoot.getParentuuid(),
							permissionUuid,
							org.getId());
					permission.addPermissionedStuff(stuff_mm);
					
				}
				
				addGroupPermission(group.getId(), permission);
				
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public ApiResponse generateSqlConditionSegmentForProductList(Permission mergedPermission, String folderUuid) {
		ApiResponse apires = null;
		
		if(mergedPermission!=null && StringUtils.isNotBlank(folderUuid)){
			List<PermissionedStuff> permissionedStuffForProduct = mergedPermission.getPermissionedStuffsForCategory(PermissionedStuff.Category.product);
			if(permissionedStuffForProduct!=null){
				
				// check preview permission for folder:
				//     if folder is preview true -> find all permissionedStuffs (parentuuid is folder's uuid) which is preview false.
				//     if folder is preview false -> find all permissionedStuffs (parentuuid is folder's uuid) which is preview true.
				StringBuilder sqlSegment = null;
				boolean isPreviewPermissionForFolderAllow = isPermissionAllowed(mergedPermission, Permission.Type.preview, folderUuid);
				if(isPreviewPermissionForFolderAllow){
					apires = new ApiResponse();
					apires.setSuccess(true);
					
					List<PermissionedStuff> deniedStuffs = new ArrayList<PermissionedStuff>();
					for(PermissionedStuff s : permissionedStuffForProduct){
						if(StringUtils.equals(s.getParentuuid(), folderUuid) 
								&& StringUtils.equals(s.getAllowdeny(), PermissionedStuff.AllowDeny.deny.getCode())){
							deniedStuffs.add(s);
						}
					}
					if(deniedStuffs.size()>0){
						sqlSegment = new StringBuilder();
						sqlSegment.append("entityuuid not in (");
						int idx = 0;
						for(PermissionedStuff s : deniedStuffs){
							if(idx>0) {
								sqlSegment.append(",");
							}
							sqlSegment.append("'").append(s.getPointuuid()).append("'");
							idx++;
						}
						sqlSegment.append(")");
						apires.setResponse1(sqlSegment.toString());
					}
				}else{
					apires = new ApiResponse();
					apires.setSuccess(false);
					
					List<PermissionedStuff> allowedStuffs = new ArrayList<PermissionedStuff>();
					for(PermissionedStuff s : permissionedStuffForProduct){
						if(StringUtils.equals(s.getParentuuid(), folderUuid) 
								&& StringUtils.equals(s.getAllowdeny(), PermissionedStuff.AllowDeny.allow.getCode())){
							allowedStuffs.add(s);
						}
					}
					if(allowedStuffs.size()>0){
						sqlSegment = new StringBuilder();
						sqlSegment.append("entityuuid in (");
						int idx = 0;
						for(PermissionedStuff s : allowedStuffs){
							if(idx>0) {
								sqlSegment.append(",");
							}
							sqlSegment.append("'").append(s.getPointuuid()).append("'");
							idx++;
						}
						sqlSegment.append(")");
						apires.setResponse1(sqlSegment.toString());
					}
				}
				
			}
			
		}
		
		return apires;
	}

	
}
