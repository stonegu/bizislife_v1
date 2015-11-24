package com.bizislife.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.validation.ValidationSet;

@Controller
public class PermissionController {
	private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
	
    @Autowired
    private AccountService accountService;

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;


    @RequestMapping(value="/getWhoCanJoinGroupSegment", method=RequestMethod.GET)
    public ModelAndView getWhoCanJoinGroupSegment(
			@RequestParam(value = "targetUuid", required = false) Long targeGrouptId,
			@RequestParam(value = "targetOrg", required = false) Long targetOrgId
		) {
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
		ModelAndView mv = new ModelAndView("whoCanJoinGroupSegment");
		
		Accountgroup group = accountService.getGroup(targeGrouptId);
		Organization joinTargetOrg = accountService.getOrgById(targetOrgId);
		
		if(group!=null && joinTargetOrg!=null && group.getOrganization_id().intValue()==joinTargetOrg.getId().intValue()){
			if(loginAccount!=null && loginAccount.isSystemDefaultAccount()
				&& (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue())
			){
				
				if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode()) // only global group can let other org's accounts to join 
						&& group.getGrouptype().equals(Accountgroup.GroupType.general.name())){
					
					mv.addObject("group", group);
					
					List<Organization> allOrgs = accountService.findAllOrgs();
					if(allOrgs!=null && allOrgs.size()>0){
						List<Organization> orgsCanJoin = new ArrayList<Organization>();
						for(Organization o : allOrgs){
							if(o.getId().intValue()!=joinTargetOrg.getId().intValue()
								&& o.getActivatedate()!=null && o.getDeletedate()==null
							){
								orgsCanJoin.add(o);
							}
						}
						mv.addObject("orgsCanJoin", orgsCanJoin);

						
						List<OrgCanJoin> ocjs = accountService.findOrgsCanJoin(group.getId());
						
						Map<Long, OrgCanJoin> joinedOrgIdWithNum = new HashMap<Long, OrgCanJoin>();
						if(ocjs!=null && ocjs.size()>0){
							for(OrgCanJoin ocj : ocjs){
								joinedOrgIdWithNum.put(ocj.getOrganization_id(), ocj);
							}
						}
						mv.addObject("joinedOrgIdWithNum", joinedOrgIdWithNum);
						
					}
				}
			}
		}
		
		return mv;
    }
    
    
    
    @RequestMapping(value="/toggleJoinAllowDeny", method=RequestMethod.GET)
    public ModelAndView toggleJoinAllowDeny(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "targetOrg", required = false) String targetOrgUuid,
			@RequestParam(value = "group", required = false) Long groupId
			
		) {
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = null;
    	
		Accountgroup group = accountService.getGroup(groupId);
		Organization targetOrg = accountService.getOrgByUuid(targetOrgUuid);
		
		if(loginAccount!=null && group!=null && targetOrg!=null && StringUtils.isNotBlank(type)){
			if(loginAccount!=null && loginAccount.isSystemDefaultAccount()
					&& (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue())
				){
					mv = new ModelAndView("whoCanJoinGroupSegment_tr");
					OrgCanJoin orgCanJoin = permissionService.toggleOrgJoinGroupAllowDeny(type, targetOrgUuid, groupId);
					
					mv.addObject("org", targetOrg);
					mv.addObject("group", group);
					mv.addObject("orgCanJoin", orgCanJoin);
				}else {
					mv = new ModelAndView("error_general");
					List<String> errorList = new ArrayList<String>();
					errorList.add("You don't have permission to do this action");
					mv.addObject("errorList", errorList);
				}
			
		}else{
			mv = new ModelAndView("error_general");
			List<String> errorList = new ArrayList<String>();
			errorList.add("System cannot find type or/and group by id: "+groupId+" or/and organization by id: "+targetOrgUuid+" or/and user login session is expired!");
			mv.addObject("errorList", errorList);
		}
		
		return mv;
    	
    }
    
    @RequestMapping(value="/orgCanJoinModify", method=RequestMethod.GET)
    public @ResponseBody ApiResponse orgCanJoinModify(
    		@RequestParam(value = "type", required = true) String type,
    		@RequestParam(value = "group", required = true) Long groupId,
			@RequestParam(value = "targetOrg", required = false) String targetOrgUuid,
			@RequestParam(value = "valueName", required = false) String valueName,
			@RequestParam(value = "value", required = false) String value
    ){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	Accountgroup group = accountService.getGroup(groupId);
    	Organization org = accountService.getOrgByUuid(targetOrgUuid);
    	
    	if(loginAccount!=null && group!=null && org!=null && StringUtils.isNotBlank(type)){
        	// check the permission
        	if(loginAccount!=null && loginAccount.isSystemDefaultAccount()
        		&& (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue())
        	){
        		
        		res = permissionService.orgCanJoinModify(type, groupId, targetOrgUuid, valueName, value);
        		
        		
        	}else{
        		res.setResponse1("user: "+loginAccount.getLastname()+"(id: "+loginAccount.getAccountuuid()+") has no permission to modify the data");
        	}
    		
    	}else{
    		res.setResponse1("System cannot find type or/and group by id: "+groupId+" or/and organization by id: "+targetOrgUuid+" or/and user login session is expired!");
    	}
    	
    	
    	return res;
    }

    
    //joinGroupLinkGenerator
    @RequestMapping(value="/joinGroupLinkGenerator", method=RequestMethod.GET)
    public ModelAndView joinGroupLinkGenerator(
    		@RequestParam(value = "group", required = false) Long groupId,
    		@RequestParam(value = "targetOrg", required = false) String orgUuid
		) {
    	ModelAndView mv = new ModelAndView("joinGroupLinkWithHowTo");
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	Accountgroup group = accountService.getGroup(groupId);
    	Organization org = accountService.getOrgByUuid(orgUuid);
    	
    	if(loginAccount!=null && group!=null && org!=null
    		&& loginAccount.isSystemDefaultAccount() && (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue())
    	){
    		List<OrgCanJoin> ocjs = accountService.findOrgsCanJoin(group.getId());
    		if(ocjs!=null && ocjs.size()>0){
    			OrgCanJoin ocj = null;
    			for(OrgCanJoin j : ocjs){
    				if(j.getOrganization_id().intValue()==org.getId().intValue()){
    					ocj = j;
    					break;
    				}
    			}
    			if(ocj!=null){
    				mv.addObject("url", applicationConfig.getHostName());
    				
    				mv.addObject("groupid", group.getId());
    				mv.addObject("org", org);
    				mv.addObject("joinkey", ocj.getJoinkey());
    			}
    			
    		}
    	}
    	
    	return mv;
    	
    }
    
    @RequestMapping(value="/joinGroup/g/{groupid}/to/{orguuid}/jk/{joinkey}", method=RequestMethod.GET)
    public String joinGroup(
			@PathVariable("groupid") Long groupid, 
			@PathVariable("orguuid") String orguuid, 
			@PathVariable("joinkey") String joinkey, 
    		
            ModelMap model) {
    	
    	
//    	List<String> errorList = new ArrayList<String>();
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	Accountgroup group = accountService.getGroup(groupid);
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(loginAccount!=null && group!=null && org!=null // must login & all other info are not null
    		&& loginAccount.getOrganization_id().intValue()==org.getId().intValue() // join where user can join
    	){
    		
    		
//			ApiResponse res = accountService.joinGroup(group.getId(), loginAccount.getId(), joinkey);
//			model.put("joinRes", res);
			
    		Organization groupOrg = accountService.getOrgById(group.getOrganization_id());
    		model.put("groupOrg", groupOrg);
    		
    		model.put("group", group);
    		model.put("joinkey", joinkey);
    		model.put("loginAccount", loginAccount);
			
    	}
    	
//    	model.put("errorList", errorList);
    	
    	
    	return "joinGroupConfirmation";
    }
    
    
    @RequestMapping(value="/groupJoinFinalConfirm", method=RequestMethod.GET)
    public @ResponseBody ApiResponse groupJoinFinalConfirm(
    		@RequestParam(value = "groupToJoin", required = true) Long groupId,
    		@RequestParam(value = "joinkey", required = true) String joinkey
    ){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	Accountgroup group = accountService.getGroup(groupId);
    	
    	if(loginAccount!=null && group!=null){
    		
    		res = accountService.joinGroup(groupId, loginAccount.getId(), joinkey);
    		
    	}else{
    		res.setResponse1("System can't find login account and/or group that need to join by groupId: "+groupId);
    	}
    
    	return res;
    }
    
    
    
    @RequestMapping(value="/getPermissionSetSegment", method=RequestMethod.GET)
    public ModelAndView getPermissionSetSegment(
			@RequestParam(value = "target", required = false) String target, // account or group
			@RequestParam(value = "targetUuid", required = false) String targetId,
			@RequestParam(value = "targetOrg", required = false) Long targetOrgId
		) {
		ModelAndView mv = new ModelAndView("permissionSetSegment");
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		mv.addObject("loginAccount", loginAccount);
		
		mv.addObject("target", target);
		mv.addObject("targetId", targetId);
		
		if(StringUtils.equals(target, "account")){
			AccountDto account = accountService.getAccountByUuid(targetId);
			if(account!=null){
				Accountprofile accountProfile = accountService.getAccountProfile(account.getId());
				mv.addObject("targetAccount", accountProfile);
				Organization targetOrg = accountService.getOrgById(targetOrgId);
				mv.addObject("targetOrg", targetOrg);
				
				List<Permission> permissions = permissionService.findAccountPermissions(account.getId());
				// sort permissions
				if(permissions!=null && permissions.size()>1){
					Collections.sort(permissions);
				}
				mv.addObject("permissions", permissions);
			}
		}else if(StringUtils.equals(target, "group")){
			Long groupId = Long.valueOf(targetId);
			Accountgroup group = accountService.getGroup(groupId);
			if(group!=null){
				mv.addObject("targetGroup", group);
				Organization targetOrg = accountService.getOrgById(targetOrgId);
				mv.addObject("targetOrg", targetOrg);
				
				List<Permission> permissions = permissionService.findGroupPermissions(group.getId());
				if(permissions!=null && permissions.size()>0){
					// filter out the permissions not belong to targetOrg : this is for everyone group, which can be setup the permission for all orgs
					if(group.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())){
						List<Permission> removePermissions = new ArrayList<Permission>();
						for(Permission p : permissions){
							if(p.getTargetorg().intValue()!=targetOrgId.intValue()){
								removePermissions.add(p);
							}
						}
						if(removePermissions.size()>0){
							for(Permission p : removePermissions){
								permissions.remove(p);
							}
						}
					}
					
					Collections.sort(permissions);
				}
				mv.addObject("permissions", permissions);
			}
			
		}
		
		return mv;
    }
    
    @RequestMapping(value="/permissionSetEntityTree", method=RequestMethod.GET)
    public ModelAndView permissionSetEntityTree(
			@RequestParam(value = "permissionId", required = false) String permissionUuid
		) {
		ModelAndView mv = new ModelAndView("permissionSetEntitySegment");
		Permission permission = permissionService.getPermissionByUuid(permissionUuid);
		
		if(permission.getGroup_id()!=null){
			mv.addObject("targetGroup", accountService.getGroup(permission.getGroup_id()));
		}
		
		mv.addObject("permission", permission);
		
		return mv;
    }
    
    
    @RequestMapping(value="/permissionNodeCreate")
    public @ResponseBody ApiResponse permissionNodeCreate(
    		@RequestParam(value = "target", required = true) String target,
    		@RequestParam(value = "targetId", required = true) String targetId,
    		@RequestParam(value = "nodeName", required = true) String newNodeName,
			@RequestParam(value = "targetOrg", required = false) String targetOrgUuid
    ){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(target) && StringUtils.isNotBlank(targetId) && StringUtils.isNotBlank(newNodeName)){
    		
    		// validate the node's name
    		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(newNodeName);
    		if(nameVali){
    			res = permissionService.newPermissionNode(target, targetId, newNodeName, targetOrgUuid);
    			
    		}else{
    			res.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    		
    		
    	}else{
			res.setResponse1("Missing node creation info, please refresh the page, and try again!");
    	}
    	
    	return res;
    }

    
    @RequestMapping(value="/permissionStuffUpdate", method=RequestMethod.GET)
    public @ResponseBody ApiResponse permissionStuffUpdate(
    		@RequestParam(value = "permissionUuid", required = true) String permissionUuid,
    		@RequestParam(value = "permissionType", required = true) String permissionTypeString,
    		@RequestParam(value = "permissionValue", required = true) String permissionValue,
    		@RequestParam(value = "stuffUuid", required = true) String stuffUuid
    		
    ){

    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	// determine the permission type:
    	Permission.Type permissionType = null;
    	if(StringUtils.isNotBlank(permissionTypeString)){
    		if(permissionTypeString.equals("permissionBtn_p")){
    			permissionType = Permission.Type.preview;
    		}else if(permissionTypeString.equals("permissionBtn_r")){
    			permissionType = Permission.Type.read;
    		}else if(permissionTypeString.equals("permissionBtn_c")){
    			permissionType = Permission.Type.copy;
    		}else if(permissionTypeString.equals("permissionBtn_m")){
    			permissionType = Permission.Type.modify;
    		}
    	}
    	
    	res = permissionService.updatePermissionStuff(permissionUuid, stuffUuid, permissionType, permissionValue);
    	
    	return res;
    }

    @RequestMapping(value="/extraInfoForPermissionStuffUpdate", method=RequestMethod.GET)
    public @ResponseBody ApiResponse extraInfoForPermissionStuffUpdate(
    		@RequestParam(value = "permissionUuid", required = true) String permissionUuid,
    		@RequestParam(value = "permissionType", required = true) String permissionTypeString,
    		@RequestParam(value = "permissionValue", required = true) String permissionValue,
    		@RequestParam(value = "stuffUuid", required = true) String stuffUuid
    		
    ){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	// determine the permission type:
    	Permission.Type permissionType = null;
    	if(StringUtils.isNotBlank(permissionTypeString)){
    		if(permissionTypeString.equals("permissionBtn_p")){
    			permissionType = Permission.Type.preview;
    		}else if(permissionTypeString.equals("permissionBtn_r")){
    			permissionType = Permission.Type.read;
    		}else if(permissionTypeString.equals("permissionBtn_c")){
    			permissionType = Permission.Type.copy;
    		}else if(permissionTypeString.equals("permissionBtn_m")){
    			permissionType = Permission.Type.modify;
    		}
    	}
    	res = permissionService.getExtraInfoForPermissionStuffUpdate(permissionUuid, stuffUuid, permissionType);
    	
    	return res;
    }
    	
    	
    @RequestMapping(value="/updatePermissionValue")
    public @ResponseBody ApiResponse updatePermissionValue(
    		@RequestParam(value = "permissionUuid", required = true) String permissionUuid,
    		@RequestParam(value = "updateValue", required = true) String updateValue,
    		@RequestParam(value = "valueName", required = true) String valueName
    		
    ){
    	
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	res = permissionService.updatePermissionValue(permissionUuid, valueName, updateValue);
    	
    	return res;
    }
    
    @RequestMapping(value="/permissionNodeDelete", method=RequestMethod.POST)
    public @ResponseBody ApiResponse permissionNodeDelete(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.delNodeDetail(Permission.class, nodeUuid);
    	
    	return apires;
    }
    
    

}
