package com.bizislife.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PropertyInTable;
import com.bizislife.core.hibernate.pojo.VisibleOrg;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.AttributeList;
import com.bizislife.util.validation.ValidationSet;

@Controller
public class GroupController {
	
	private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
	
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(value="/groups", method=RequestMethod.GET)
    public String groupsPage(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("groups_").append(orguuid).toString());
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(loginAccount!=null && org!=null){
    		model.put("loginAccount", loginAccount);
    		model.put("org", org);
    		
			// find all group access levels
			List<GeneralSelectionType> accessLevels = new ArrayList<GeneralSelectionType>();
			for(Accountgroup.GroupAccessLevel l : Accountgroup.GroupAccessLevel.values()){
				accessLevels.add(new GeneralSelectionType(l.getCode(), l.getDesc(), Boolean.FALSE));
			}
			model.put("groupAccessLevels", accessLevels);
			
    		// get all groups for org
    		List<Accountgroup> groups = accountService.findGlobalGroups(org.getOrguuid()); 
    		List<Accountgroup> privateGroups = accountService.findPrivateGroups(org.getOrguuid());
    		
    		if(privateGroups!=null && privateGroups.size()>0){
    			if(groups!=null) groups.addAll(privateGroups);
    			else{
    				groups = new ArrayList<Accountgroup>();
    				groups.addAll(privateGroups);
    			}
    		}
    		
    		// get everyone group for other org
    		if(!org.isBizOrg()){
        		Accountgroup everyoneGroup = accountService.getEveryoneGroup();
        		if(everyoneGroup!=null){
        			if(groups!=null) groups.add(everyoneGroup);
        		}else{
        			groups = new ArrayList<Accountgroup>();
        			groups.add(everyoneGroup);
        		}
    		}
    		
    		
    		model.put("groups", groups);
    		
    	}
        return "groups";
    }
    
    
    @RequestMapping(value="/addGroup", method=RequestMethod.GET)
    public String addGroupPage(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {

    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("groups_").append(orguuid).toString());
    	model.put(AttributeList.RequestAttribute.orguuid.name(), orguuid);
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();

    	if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && StringUtils.isNotBlank(orguuid)){
    		Organization org = accountService.getOrgByUuid(orguuid);
    		model.put("targetOrg", org);
    		if(org!=null){
    			
    			// find all group access levels
    			List<GeneralSelectionType> accessLevels = new ArrayList<GeneralSelectionType>();
    			for(Accountgroup.GroupAccessLevel l : Accountgroup.GroupAccessLevel.values()){
    				accessLevels.add(new GeneralSelectionType(l.getCode(), l.getDesc(), Boolean.FALSE));
    			}
    			model.put("groupAccessLevels", accessLevels);
    			
    		}
    	}
        return "addGroup";
    }
    
    @RequestMapping(value="/addGroup", method=RequestMethod.POST)
    public String addGroup(
			@RequestParam(value = "org", required = false) String orguuid,
			@RequestParam(value = "groupname", required = false) String groupname,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "accesslevel", required = false) String accesslevel,
			
			HttpServletRequest req,
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("groups_").append(orguuid).toString());
    	model.put(AttributeList.RequestAttribute.orguuid.name(), orguuid);

    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	Date now = new Date();
    	Map<String, String> errorMsgs = new HashMap<String, String>();
    	boolean returnInputData = false;
    	
    	if(currentAccount!=null && org!=null  
    		&& ((currentAccount.isBizAccount() && currentAccount.isSystemDefaultAccount()) //biz sys account can do
    			|| (currentAccount.isSystemDefaultAccount() && currentAccount.getOrganization_id().intValue()==org.getId().intValue()) // org's sys account can do
    			) 
    		&& StringUtils.isNotBlank(groupname) && StringUtils.isNotBlank(accesslevel)){

    		model.put("targetOrg", org);
    		
    		OrgCanJoin ocj = new OrgCanJoin(null, org.getId(), 10000, UUID.randomUUID().toString());
    		Accountgroup group = new Accountgroup(null, UUID.randomUUID().toString(), groupname, accesslevel, description, now, now, null, org.getId(), currentAccount.getId(), Accountgroup.GroupType.general.name(), null);
    		group.addOrgCanJoin(ocj);
    		Long groupId = accountService.newGroup(group);
    		
    	}else{
    		returnInputData = true;
    		
    		PropertyInTable sessionExpired = accountService.getPropertyByKey("system.session.expired");
    		PropertyInTable permissionDenied = accountService.getPropertyByKey("system.permission.denied");
    		PropertyInTable notNullMsg = accountService.getPropertyByKey("validateMsg.field.notnull");
    		PropertyInTable selectMsg = accountService.getPropertyByKey("validateMsg.field.select");
    		
    		if(currentAccount==null) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		else if(!((currentAccount.isBizAccount() && currentAccount.isSystemDefaultAccount()) || (currentAccount.isSystemDefaultAccount() && currentAccount.getOrganization_id().intValue()==org.getId().intValue()))) errorMsgs.put("pageError", permissionDenied.getPvalue());
    		
    		if(StringUtils.isBlank(orguuid)) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		else if(org==null) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		
    		if(StringUtils.isBlank(groupname)) errorMsgs.put("groupname", notNullMsg.getPvalue());
    		
    		if(StringUtils.isBlank(accesslevel)) errorMsgs.put("accesslevel", selectMsg.getPvalue());
    	}
    	
    	// put error msg into view
    	model.put(AttributeList.RequestAttribute.errorMsgs.name(), errorMsgs);
    	
    	// put all data inputs into request to the view if returnInputData=true
    	if(returnInputData){
//    		model.put("actionType", "addOrganization");
    		
    		model.put("groupname", groupname);
    		model.put("description", description);
    		model.put("accesslevel", accesslevel);
    	}
    	
		// find all group access levels
    	if(currentAccount!=null && currentAccount.isSystemDefaultAccount()){
    		List<GeneralSelectionType> accessLevels = new ArrayList<GeneralSelectionType>();
    		for(Accountgroup.GroupAccessLevel l : Accountgroup.GroupAccessLevel.values()){
				accessLevels.add(new GeneralSelectionType(l.getCode(), l.getDesc(), Boolean.FALSE));
    		}
    		model.put("groupAccessLevels", accessLevels);
    	}

    	return "addGroup";
    }
    	
    
    
    @RequestMapping(value="/editGroup", method=RequestMethod.POST)
    public String editGroup(
    		
			@RequestParam(value = "org", required = false) String orguuid,
			@RequestParam(value = "groupname", required = false) String groupname,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "groupid", required = false) Long groupid,
			
			HttpServletRequest req,
            ModelMap model) {
    	

		accountService.updateGroup(groupid, groupname, description);
    	
        return "redirect:groups?org="+orguuid;
    }
    	
    @RequestMapping(value="/delGroup", method=RequestMethod.POST)
    public String delGroup(
    		
			@RequestParam(value = "org", required = false) String orguuid,
			@RequestParam(value = "groupId", required = false) Long groupid,
			
			HttpServletRequest req,
            ModelMap model) {
    	
		accountService.delGroupById(groupid);
    	
        return "redirect:groups?org="+orguuid;
    }
    
    

    

}
