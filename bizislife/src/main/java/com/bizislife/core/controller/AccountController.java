package com.bizislife.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PropertyInTable;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.PasswordUtils;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.AttributeList;
import com.bizislife.util.validation.ValidationSet;

@Controller
public class AccountController {
	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
	
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(value="/accounts", method=RequestMethod.GET)
    public String accounts(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {

    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("accounts_").append(orguuid).toString());
    	
    	if(StringUtils.isNotBlank(orguuid)){
    		model.put("orguuid", orguuid);
    		
    		// get all accounts in org
    		List<AccountDto> accounts = accountService.findOrgAccountsByOrgUuid(orguuid.trim());
    		model.put("accounts", accounts);
    	}
    	
    	return "accounts";
    }

    @RequestMapping(value="/addAccount", method=RequestMethod.GET)
    public String addAccountPage(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {

    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentAction.name(), "addAccount");
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("accounts_").append(orguuid).toString());

    	if(StringUtils.isNotBlank(orguuid)){
    		Organization org = accountService.getOrgByUuid(orguuid);
    		
    		if(org!=null){
        		model.put("targetOrg", org);
        		
    			List<Accountgroup> groups = accountService.findGlobalGroups(org.getOrguuid());
    	    	List<Accountgroup> privateGroups = accountService.findPrivateGroups(org.getOrguuid());
    	    	if(privateGroups!=null && privateGroups.size()>0){
    	    		if(groups!=null) groups.addAll(privateGroups);
    	    		else{
    	    			groups = new ArrayList<Accountgroup>();
    	    			groups.addAll(privateGroups);
    	    		}
    	    	}
    	    	model.put("accountGroups", groups);
    	    	
    	    	// put orgIdNameMap into model
    	    	model.put("orgIdNameMap", accountService.getOrgIdNameMap());
    			
    		}
    	}
        return "addAccount";
    }
    
    @RequestMapping(value="/addAccount", method=RequestMethod.POST)
    public String addAccount(
    		Account account,
    		Accountprofile accountProfile,
    		
			@RequestParam(value = "org", required = false) String orguuid,
			@RequestParam(value = "personalEmail", required = false) String personalEmail,
			@RequestParam(value = "useEmailAsLoginName", required = false) Boolean useEmailAsLoginName,
			@RequestParam(value = "repwd", required = false) String repwd,
			@RequestParam(value = "accountGroup", required = false) Long[] groupids,
			
			HttpServletRequest req,
            ModelMap model) {
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	Date now = new Date();
    	Map<String, String> errorMsgs = new HashMap<String, String>();
    	boolean returnInputData = false;

    	// get org
		Organization targetOrg = accountService.getOrgByUuid(orguuid);
		
		// for selected groups
		Map<Long, Boolean> selectedGroups = new HashMap<Long, Boolean>();
		if(groupids!=null && groupids.length>0){
			for(Long gid : groupids){
				selectedGroups.put(gid, true);
			}
		}
		model.put("selectedGroups", selectedGroups);
    	
    	if(currentAccount!=null && currentAccount.isSystemDefaultAccount() &&
    			targetOrg!=null && StringUtils.isNotBlank(personalEmail) && ValidationSet.isValidEmail(personalEmail.trim()) 
    			&& StringUtils.isNotBlank(account.getLoginname()) 
    			&& StringUtils.isNotBlank(account.getPwd()) && StringUtils.isNotBlank(repwd)
    			&& ValidationSet.isAlphaNumOnly(account.getPwd()) && account.getPwd().equals(repwd)
    			&& StringUtils.isNotBlank(accountProfile.getFirstname()) && StringUtils.isNotBlank(accountProfile.getLastname())
    			){
    		
    		
    		// more validation for login name : can't duplicate for login name
    		boolean loginNameCheckPassed = true;
			if(useEmailAsLoginName!=null && useEmailAsLoginName){
				account.setLoginname(personalEmail.trim().toLowerCase());
			}else{
				loginNameCheckPassed = ValidationSet.isValidLoginName(account.getLoginname());
			}
			List<Account> accountsWithLoginName = accountService.findAccountsByLoginName(account.getLoginname());
			if(accountsWithLoginName!=null && accountsWithLoginName.size()>0){
				loginNameCheckPassed = false;
			}
    		
			if(loginNameCheckPassed && targetOrg!=null){
				
				List<Accountgroup> groups = new ArrayList<Accountgroup>();
				if(groupids!=null && groupids.length>0){
					groups = accountService.findGroupsByIds(groupids);
				}
				// add everyone group if no everyone group selected!
				boolean missEveryoneGroup = true;
				for(Accountgroup g : groups){
					if(g.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())){
						missEveryoneGroup = false;
					}
				}
				if(missEveryoneGroup){
					Accountgroup everyoneGroup = accountService.getEveryoneGroup();
					groups.add(everyoneGroup);
				}
				
    			// save account with profile
    			account.setAccountuuid(UUID.randomUUID().toString());
    			account.setCreatedate(now);
    			account.setCreator_id(currentAccount.getId());
    			account.setOrganization_id(targetOrg.getId());
    			// set salt
    			account.setSalt(PasswordUtils.makeSaltBase64());
    			if(groups.size()>0){
//    				Set<Accountgroup> aGroups = new HashSet<Accountgroup>(groups);
    				for(Accountgroup g : groups){
//            			account.setAccountGroups(aGroups);
    					account.addAccountGroup(g);
    				}
    			}
    			account.setPwd(WebUtil.securityPassword(account.getPwd()));
    			StringBuilder accountName = new StringBuilder();
    			if(accountProfile!=null){
    				accountName.append(accountProfile.getFirstname()).append(" ").append(accountProfile.getLastname());
    			}
    			
    			Long accountId = accountService.newAccount(account, personalEmail, accountName.toString());
    			
    			if(accountId!=null){
    				accountProfile.setAccount_id(accountId);
    				accountProfile.setCreatedate(now);
    				Long accountProfileId = accountService.newAccountProfile(accountProfile);
    				
    				Contactinfo acctContact = new Contactinfo();
    				acctContact.setAccount_id(accountId);
    				acctContact.setCreatedate(now);
    				acctContact.setEmail(personalEmail.trim());
    				Long accountContactId = accountService.newContact(acctContact);
    				
    				return "redirect:addAccount?org="+orguuid;
    			}
				
				
				
				
			}else{
				returnInputData = true;
				
				if(targetOrg==null){
		    		PropertyInTable sessionExpired = accountService.getPropertyByKey("system.session.expired");
		    		errorMsgs.put("pageError", sessionExpired.getPvalue());
				}
				
				if(!ValidationSet.isValidLoginName(account.getLoginname())){
					PropertyInTable inValidLoginName = accountService.getPropertyByKey("validateMsg.field.loginname");
					errorMsgs.put("loginname", inValidLoginName.getPvalue());
				}else{
					PropertyInTable sameLoginNameExist = accountService.getPropertyByKey("validateMsg.field.loginNameExist");
					errorMsgs.put("loginname", sameLoginNameExist.getPvalue());		
				}

			}
    		
    		
    	}else{
    		returnInputData = true;
    		
    		PropertyInTable sessionExpired = accountService.getPropertyByKey("system.session.expired");
    		PropertyInTable notNullMsg = accountService.getPropertyByKey("validateMsg.field.notnull");
    		PropertyInTable emailValiMsg = accountService.getPropertyByKey("validateMsg.field.email");
    		PropertyInTable confirmPwd = accountService.getPropertyByKey("validateMsg.field.confirmPwdNotSame");
    		PropertyInTable permissionDenied = accountService.getPropertyByKey("system.permission.denied");
		
    		if(currentAccount==null) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		else if(!currentAccount.isSystemDefaultAccount()) errorMsgs.put("pageError", permissionDenied.getPvalue());
    		
    		if(StringUtils.isBlank(orguuid)) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		
    		if(StringUtils.isBlank(personalEmail)) errorMsgs.put("personalEmail", notNullMsg.getPvalue());
    		if(!ValidationSet.isValidEmail(personalEmail.trim())) errorMsgs.put("personalEmail", emailValiMsg.getPvalue());
    		if(StringUtils.isBlank(account.getLoginname())) errorMsgs.put("loginname", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(accountProfile.getFirstname())) errorMsgs.put("firstname", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(accountProfile.getLastname())) errorMsgs.put("lastname", notNullMsg.getPvalue());
    		
    		if(StringUtils.isBlank(account.getPwd())) errorMsgs.put("pwd", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(repwd)) errorMsgs.put("repwd", notNullMsg.getPvalue());
    		if(!StringUtils.equals(account.getPwd(), repwd)) errorMsgs.put("repwd", confirmPwd.getPvalue());
    		
    	}
    	
    	model.put(AttributeList.RequestAttribute.currentAction.name(), "addAccount");
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("accounts_").append(orguuid).toString());
    	
    	model.put("targetOrg", targetOrg);
    	
    	// for groups
		List<Accountgroup> groups = accountService.findGlobalGroups(targetOrg.getOrguuid());
    	List<Accountgroup> privateGroups = accountService.findPrivateGroups(targetOrg.getOrguuid());
    	if(privateGroups!=null && privateGroups.size()>0){
    		if(groups!=null) groups.addAll(privateGroups);
    		else{
    			groups = new ArrayList<Accountgroup>();
    			groups.addAll(privateGroups);
    		}
    	}
    	model.put("accountGroups", groups);
    	
    	// put orgIdNameMap into model
    	model.put("orgIdNameMap", accountService.getOrgIdNameMap());
    	
    	
    	// put error msg into view
    	model.put(AttributeList.RequestAttribute.errorMsgs.name(), errorMsgs);
    	
    	// put all data inputs into request to the view if returnInputData=true
    	if(returnInputData){
//    		model.put("actionType", "addOrganization");
    		
    		if(useEmailAsLoginName!=null && useEmailAsLoginName) account.setLoginname(personalEmail.trim());
    		model.put("account", account);
    		model.put("accountProfile", accountProfile);
    		
    		model.put("personalEmail", personalEmail);
    		model.put("useEmailAsLoginName", useEmailAsLoginName);
    	}
    	
    	

    	return "addAccount";
    }

    
    
    @RequestMapping(value="/getAcctInfo", method=RequestMethod.GET)
    public String getOrgInfo(
			@RequestParam(value = "acct", required = false) String acctuuid,
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentAction.name(), "accountInfo");
    	
    	if(StringUtils.isNotBlank(acctuuid)){
    		AccountDto currentAccount = accountService.getAccountByUuid(acctuuid);
    		
    		AccountDto loginAccount = accountService.getCurrentAccount();
    		
    		if(currentAccount!=null && loginAccount!=null){
    			// get orginfo
    			Organization org = accountService.getOrgById(currentAccount.getOrganization_id());
    			if(org!=null) {
    				model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("accounts_").append(org.getOrguuid()).toString());
    				model.put(AttributeList.RequestAttribute.currentAccountPageId.name(), new StringBuilder("accountSettings_").append(acctuuid));
            		model.put("account", currentAccount);
            		model.put("loginAccount", loginAccount);
            		// get account profile
            		Accountprofile profile = accountService.getAccountProfile(currentAccount.getId());
            		model.put("accountProfile", profile);
            		
            		// get account contact infos: note : support one contactInfo for one account now
            		List<Contactinfo> contactInfos = accountService.findContactInfosForAccount(currentAccount.getId());
            		model.put("contactInfo", (contactInfos!=null && contactInfos.size()>0)?contactInfos.get(0):null);
    				
            		// get groups (global and org's)
        			List<Accountgroup> groups = accountService.findGlobalGroups(org.getOrguuid());
        	    	List<Accountgroup> privateGroups = accountService.findPrivateGroups(org.getOrguuid());
        	    	if(privateGroups!=null && privateGroups.size()>0){
        	    		if(groups!=null) groups.addAll(privateGroups);
        	    		else{
        	    			groups = new ArrayList<Accountgroup>();
        	    			groups.addAll(privateGroups);
        	    		}
        	    	}
        	    	model.put("accountGroups", groups);
        	    	
        	    	// get selected groups
        			Map<Long, String> selectedGroupsMap = new HashMap<Long, String>(); // *******
        			// hold selected groups belonging to self org
        			List<Accountgroup> selectedGroups_self = new ArrayList<Accountgroup>();
        			// hold selected groups belonging to other org
        			List<Accountgroup> selectedGroups_other = new ArrayList<Accountgroup>();
        			
        			List<Accountgroup> accountgroups = accountService.findGroupsForAccount(currentAccount.getId());
        			
        			if(accountgroups!=null && accountgroups.size()>0){
        				for(Accountgroup g : accountgroups){
        					// add orgid with name  to groupOrgIdWithNameMap if not found:
        					selectedGroupsMap.put(g.getId(), g.getGroupname());
        					
        					if(g.getOrganization_id().intValue()==currentAccount.getOrganization_id().intValue()){
        						selectedGroups_self.add(g);
        					}else{
        						selectedGroups_other.add(g);
        					}
        				}
        			}
        			
        			model.put("selectedGroupsMap", selectedGroupsMap);
        			model.put("selectedGroups_self", selectedGroups_self);
        			model.put("selectedGroups_other", selectedGroups_other);
        	    	
        	    	// put orgIdNameMap into model
        	    	model.put("orgIdNameMap", accountService.getOrgIdNameMap());
        	    	
        	    	// put timezone into model
        			String[] timezoneIds = TimeZone.getAvailableIDs();
//        			List<GeneralSelectionType> timezoneWithNullIds = new ArrayList<GeneralSelectionType>();
//        			timezoneWithNullIds.add(new GeneralSelectionType(null, " select...", false));
//        			for(String tzid : timezoneIds){
//        				int timezoneOffset = TimeZone.getTimeZone(tzid).getOffset(new Date().getTime());
//        				String offset = String.format("%02d:%02d", Math.abs(timezoneOffset / 3600000), Math.abs((timezoneOffset / 60000) % 60));
//        			    offset = (timezoneOffset >= 0 ? "+" : "-") + offset;					
//        				timezoneWithNullIds.add(new GeneralSelectionType(tzid, "("+offset+")"+tzid, false));
//        			}
//        			Collections.sort(timezoneWithNullIds);
        			
        			List<GeneralSelectionType> timezoneIdNameWithOffsets = new ArrayList<GeneralSelectionType>();
        			timezoneIdNameWithOffsets.add(new GeneralSelectionType(null, " select...", false));
        			
        			Map<String, Boolean> timezoneNameExist = new HashMap<String, Boolean>();
        			for(String tzid : timezoneIds){
        				
        				int timezoneOffset = TimeZone.getTimeZone(tzid).getOffset(new Date().getTime());
        				
        				String offset = String.format("%02d:%02d", Math.abs(timezoneOffset / 3600000), Math.abs((timezoneOffset / 60000) % 60));
        			    offset = (timezoneOffset >= 0 ? "+" : "-") + offset;					
        				
//        				timezoneWithNullIds.add(new GeneralType(tzid, "("+offset+")"+tzid, false));
        			    
        			    String timezoneName = "("+offset+")"+TimeZone.getTimeZone(tzid).getDisplayName();
        			    if(timezoneNameExist.get(timezoneName)==null){
        			    	timezoneIdNameWithOffsets.add(new GeneralSelectionType(tzid, timezoneName, false));
        				    timezoneNameExist.put(timezoneName, true);
        			    }

        			}
        			Collections.sort(timezoneIdNameWithOffsets);
        			
        			model.put("timezones", timezoneIdNameWithOffsets);
            		
    			}
    		}
    		
    	}else{
    		return "error_general";
    	}
        return "accountInfo";
    }
    
    /**
     * This method will activate account (verify account) by self
     * 
     * @param acctuuid
     * @param model
     * @return
     */
    @RequestMapping(value="/accountVerify", method=RequestMethod.GET)
    public String accountVerify(
			@RequestParam(value = "hash", required = false) String acctuuid,
            ModelMap model) {
    	
    	Long accountId = accountService.activateAccount(acctuuid);
    	if(accountId!=null){
        	model.addAttribute("resultInfo", "Your account is verified, you can login now!");
    	}else{
        	model.addAttribute("resultInfo", "Your account("+acctuuid+") can't be verified, send us a message here and we can help you out.");
    	}
    	
    	return "accountVerifyPage";
    }

    /**
     * this method will activate account by admin in org or in bizislife
     * 
     * @param acctuuid
     * @param model
     * @return
     */
    @RequestMapping(value="/accountActivate", method=RequestMethod.GET)
    public String accountActivate(
			@RequestParam(value = "acct", required = false) String acctuuid,
            ModelMap model) {
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	StringBuilder redirectUrl = new StringBuilder("redirect:accounts");
    	if(StringUtils.isNotBlank(acctuuid)){
    		AccountDto account = accountService.getAccountByUuid(acctuuid);
    		if(loginAccount!=null && account!=null 
        			&& ((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz system account can do anything 
        					|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==account.getOrganization_id().intValue())  ) // org's admin can do
        		){
    			if(account.getActivatedate()==null){
    	    		accountService.activateAccount(acctuuid);
    			}
        		
        		Organization org = accountService.getOrgById(account.getOrganization_id());
        		if(org!=null) redirectUrl.append("?org=").append(org.getOrguuid());
    		}
    		
    	}
    	return redirectUrl.toString();
    	
    }

    @RequestMapping(value="/accountDeactivate", method=RequestMethod.GET)
    public String accountDeactivate(
			@RequestParam(value = "acct", required = false) String acctuuid,
            ModelMap model) {
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	StringBuilder redirectUrl = new StringBuilder("redirect:accounts");
    	if(StringUtils.isNotBlank(acctuuid)){
    		AccountDto account = accountService.getAccountByUuid(acctuuid);
    		
    		if(loginAccount!=null && account!=null 
    			&& ((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz system account can do anything 
    					|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==account.getOrganization_id().intValue())  ) // org's admin can do
    		){
    			if(account.getActivatedate()!=null){
    	    		accountService.deActivateAccount(acctuuid);
    			}
    			Organization org = accountService.getOrgById(account.getOrganization_id());
        		if(org!=null) redirectUrl.append("?org=").append(org.getOrguuid());
    		}
    	}
    	return redirectUrl.toString();
    	
    }

    @RequestMapping(value="/updateAccountProfile")
    public @ResponseBody ApiResponse updateAccountProfile(
    		@RequestParam(value = "accountid", required = false) String accountuuid,
    		@RequestParam(value = "updatedName", required = false) String updatedName,
    		@RequestParam(value = "updatedValue", required = false) String updatedValue
		){
    	
    	ApiResponse apires = accountService.updateAccountByFieldnameValue(accountuuid, updatedName, updatedValue);
    	
    	return apires;
    }
    
    @RequestMapping(value="/updateAccountContact")
    public @ResponseBody ApiResponse updateAccountContact(
    		@RequestParam(value = "accountid", required = false) String accountuuid,
    		@RequestParam(value = "contactid", required = false) Long contactid,
    		
    		@RequestParam(value = "updatedName", required = false) String updatedName,
    		@RequestParam(value = "updatedValue", required = false) String updatedValue
		){
    	
    	ApiResponse apires = accountService.updateContactByFieldnameValue(null, accountuuid, contactid, updatedName, updatedValue);
    	
    	return apires;
    }
    
//    @RequestMapping(value="/updateAccountGroup")
//    public @ResponseBody ApiResponse updateAccountGroup(
//    		@RequestParam(value = "accountid", required = false) String accountuuid,
//    		@RequestParam(value = "accountGroup", required = false) Long[] groupids
//		){
//    	
//    	ApiResponse apires = accountService.updateAccountGroups(accountuuid, groupids);
//    	
//    	return apires;
//    }
    
    @RequestMapping(value="/updateAccountPwd")
    public @ResponseBody ApiResponse updateAccountPwd(
    		@RequestParam(value = "accountid", required = false) String accountuuid,
    		@RequestParam(value = "oldPwd", required = false) String oldPwd,
    		@RequestParam(value = "newPwd", required = false) String newPwd,
    		@RequestParam(value = "confirmNewPwd", required = false) String confirmNewPwd
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	AccountDto account = accountService.getAccountByUuid(accountuuid);
    	if(account!=null){
        	if(StringUtils.isNotBlank(oldPwd) && StringUtils.isNotBlank(newPwd) && StringUtils.isNotBlank(confirmNewPwd)){
        		// check the old password;
        		String securedOldPwd = WebUtil.securityPassword(oldPwd);
        		if(securedOldPwd.equals(account.getPwd())){
        			if(newPwd.trim().equals(confirmNewPwd.trim())){
        				apires = accountService.updateAccountByFieldnameValue(account.getAccountuuid(), "password", newPwd);
        			}else{
        				apires.setResponse1("The New Password doesn't matched the Confirm New Password!");
        			}
        		}else{
        			apires.setResponse1("The existing password for account doesn't match the password (Old Password) you gave!");
        		}
        	}else{
        		apires.setResponse1("\"Old Password\", \"New Password\" and \"Confirm New Password\" can't be empty!");
        	}
    		
    	}else{
    		apires.setResponse1("System can't find account for accountid: "+accountuuid+", you may need to refresh the page and try again!");
    	}
    	
    	
//    	apires = accountService.updateAccountByFieldnameValue(accountuuid, updatedName, updatedValue);
    	
    	return apires;
    }
    
    
    
    // "type="+jsonObj.type+"&account="+jsonObj.account+"&group="+jsonObj.group
    @RequestMapping(value="/toggleGroupJoin")
    public @ResponseBody ApiResponse toggleGroupJoin(
    		@RequestParam(value = "type", required = false) String type, //unjoinGroup_outside, joinGroup, unjoinGroup
    		@RequestParam(value = "account", required = false) String accountUuid,
    		@RequestParam(value = "group", required = false) Long groupId
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	AccountDto targetAccount = accountService.getAccountByUuid(accountUuid);
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	Accountgroup group = accountService.getGroup(groupId);
    	
    	if(loginAccount!=null && targetAccount!=null && group!=null && StringUtils.isNotBlank(type)){
    		
    		if(loginAccount.isSystemDefaultAccount() 
    			&& (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==targetAccount.getOrganization_id().intValue()) 
    		){
    			
        		if(type.equals("joinGroup")){
        			apires = accountService.joinGroup(groupId, targetAccount.getId(), null);
        		}else if(type.equals("unjoinGroup")){
        			apires = accountService.unJoinGroup(groupId, targetAccount.getId());
        		}else if(type.equals("unjoinGroup_outside")){
        			apires = accountService.unJoinGroup(groupId, targetAccount.getId());
        		}
        		
    		}else{
    			apires.setResponse1("You don't have permission to do group join action.");
    		}
    		
    	}else{
    		apires.setResponse1("System can find loginAccount and/or targetAccount("+accountUuid+") and/or group("+groupId+") and or type ("+type+")");
    	}
    	
    	return apires;
    }
    
    
    
    //
    @RequestMapping(value="/getAccountGroupInfo", method=RequestMethod.GET)
    public ModelAndView getAccountGroupInfo(
			@RequestParam(value = "account", required = false) String accountUuid
		) {
		ModelAndView mv = new ModelAndView("accountInfoFragment_group_value");
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		AccountDto targetAccount = accountService.getAccountByUuid(accountUuid);
		
		if(loginAccount!=null && targetAccount!=null
			&& (loginAccount.getId().intValue()==targetAccount.getId().intValue()
				|| (loginAccount.isSystemDefaultAccount() && (loginAccount.isBizAccount() || loginAccount.getOrganization_id().intValue()==targetAccount.getOrganization_id().intValue()))
			)
		){
			// hold selected groups belonging to self org
			List<Accountgroup> selectedGroups_self = new ArrayList<Accountgroup>();
			// hold selected groups belonging to other org
			List<Accountgroup> selectedGroups_other = new ArrayList<Accountgroup>();
			
			List<Accountgroup> accountGroups = accountService.findGroupsForAccount(targetAccount.getId());
			if(accountGroups!=null && accountGroups.size()>0){
				for(Accountgroup g : accountGroups){
					if(g.getOrganization_id().intValue()==targetAccount.getOrganization_id().intValue()){
						selectedGroups_self.add(g);
					}else{
						selectedGroups_other.add(g);
					}
				}
			}
			
			mv.addObject("selectedGroups_self", selectedGroups_self);
			mv.addObject("selectedGroups_other", selectedGroups_other);
			
			mv.addObject("orgIdNameMap", accountService.getOrgIdNameMap());
		}
		
		return mv;
    	
    }
    
    @RequestMapping(value="/delAccount", method=RequestMethod.POST)
    public @ResponseBody ApiResponse delAccount(
			@RequestParam(value = "acctId", required = false) String acctuuid,
            ModelMap model) {
    	
    	ApiResponse apires = accountService.suspendAccount(acctuuid);
    	
    	return apires;
    }
    

}
