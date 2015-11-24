package com.bizislife.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
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

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.OrgMeta;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.Organizationprofile;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.hibernate.pojo.PropertyInTable;
import com.bizislife.core.hibernate.pojo.VisibleOrg;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.service.activemq.QueueMsgSender;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.PasswordUtils;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.AttributeList;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.definition.EmailRelated;
import com.bizislife.util.validation.ValidationSet;

@Controller
public class OrgController {
	private static final Logger logger = LoggerFactory.getLogger(OrgController.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    private QueueMsgSender queueMsgSender;
    
    @Autowired
    private ActiveMQQueue emailQueue;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;
    
    @RequestMapping(value="/addOrganization", method=RequestMethod.GET)
    public String addOrganizationPage(
//			@RequestParam(required = false) String pageMsg,
//			@RequestParam(required = false) String pageErrorMsg, 
            ModelMap model) {
    	
    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "addOrganization");
    	
    	// put all industries into model
//    	model.put("industries", accountService.getAllIndustries());

        return "addOrganization";
    }
    
    @RequestMapping(value="/addOrganization", method=RequestMethod.POST)
    public String addOrganization(
    		Organization org,
    		Organizationprofile orgProfile,
    		Contactinfo orgContact,
    		
    		Account account,
    		Accountprofile accountProfile,
    		
			@RequestParam(value = "autocomplete", required = false) String autocomplete,
			@RequestParam(value = "personalEmail", required = false) String personalEmail,
			@RequestParam(value = "useEmailAsLoginName", required = false) Boolean useEmailAsLoginName,
			
			HttpServletRequest req,
            ModelMap model) {
    	
    	Date now = new Date();
    	boolean returnInputData = false;
    	Map<String, String> errorMsgs = new HashMap<String, String>();
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	
    	if(currentAccount!=null 
    			&& StringUtils.isNotBlank(org.getOrgname()) 
    			//&& StringUtils.isNotBlank(orgProfile.getIndustry()) && StringUtils.isNotBlank(orgProfile.getSubcategory())
    			&& StringUtils.isNotBlank(orgContact.getAddress()) && StringUtils.isNotBlank(orgContact.getCity()) && StringUtils.isNotBlank(orgContact.getState())
    			&& StringUtils.isNotBlank(orgContact.getCountry()) && StringUtils.isNotBlank(orgContact.getZip())
    			&& StringUtils.isNotBlank(personalEmail) && ValidationSet.isValidEmail(personalEmail.trim()) 
    			&& StringUtils.isNotBlank(account.getLoginname())
    			&& StringUtils.isNotBlank(accountProfile.getFirstname()) && StringUtils.isNotBlank(accountProfile.getLastname())){
    		
    		if(orgProfile==null) orgProfile = new Organizationprofile();
    		
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
    		
			boolean orgSameNameCheckpassed = true;
			List<Organization> orgsWithTheName = accountService.findOrgsByName(org.getOrgname());
			if(orgsWithTheName!=null && orgsWithTheName.size()>0) orgSameNameCheckpassed = false;
			
			boolean orgNameCheckPasssed = true;
			if(StringUtils.containsIgnoreCase(org.getOrgname(), "bizislife")){ // can't include 'bizislife'
				orgNameCheckPasssed = false;
			}
			if(org.getOrgname().length()>255){
				orgNameCheckPasssed = false;
			}
			if(!ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(org.getOrgname())){
				orgNameCheckPasssed = false;
			}
			
			
			if(loginNameCheckPassed && orgSameNameCheckpassed && orgNameCheckPasssed){
	    		// save org with profile and contactinfo
				Random random = new Random();
				org.setOrgsysname(new StringBuilder(org.getOrgname().replace(" ", "_").replaceAll("[\'\"]", "")).append("(").append(random.nextInt(1000)).append(")").toString());
	    		org.setCreatedate(now);
	    		org.setCreator_id(currentAccount.getId());
	    		org.setOrglevel(DatabaseRelatedCode.OrganizationRelated.orglevel_other.getCode());
	    		org.setOrguuid(UUID.randomUUID().toString());
	    		// set salt
	    		org.setSalt(PasswordUtils.makeSaltBase64());
	    		Long orgId = accountService.newOrg(org);
	    		Long orgProfileId = null;
	    		Long orgContactId = null;
	    		Long orgMetaId = null;
	    		
	    		Long accountId = null;
	    		Long accountProfileId = null;
	    		Long accountContactId = null;
	    		if(orgId!=null){
	    			orgProfile.setOrganization_id(orgId);
	    			orgProfile.setCreatedate(now);
	    			orgProfileId = accountService.newOrgProfile(orgProfile);

	    			orgContact.setOrganization_id(orgId);
	    			orgContact.setCreatedate(now);
	    			orgContactId = accountService.newContact(orgContact);
	    			
	    			// for orgmeta (freemium)
	    			OrgMeta orgMeta = new OrgMeta(null, 
	    					orgId, 
	    					org.getOrguuid(),
	    					null);
	    			orgMetaId = accountService.newOrgMeta(orgMeta);
	    			
	    			// for accountgroup
	    			OrgCanJoin orgCanJoin = new OrgCanJoin(null, orgId, 10000, UUID.randomUUID().toString());
	    			Accountgroup systemDefaultGroup = new Accountgroup(null, UUID.randomUUID().toString(), Accountgroup.GroupType.SystemDefault.name(), Accountgroup.GroupAccessLevel.Private.getCode(), 
	    					"System group with full privilege, you can't reset permission for the group!", now, now, null, orgId,
	    					Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
	    					Accountgroup.GroupType.SystemDefault.name(),
	    					null
	    					);
	    			systemDefaultGroup.addOrgCanJoin(orgCanJoin);
	    			List<Accountgroup> everyonegroups = accountService.findGroupByType(Accountgroup.GroupType.Everyone, null);
	    			Set<Accountgroup> groups = new HashSet<Accountgroup>();
	    			groups.add(systemDefaultGroup);
	    			if(everyonegroups!=null && everyonegroups.size()>0){
	    				groups.add(everyonegroups.get(0));
	    			}

	    			// save account with profile
	    			account.setAccountuuid(UUID.randomUUID().toString());
	    			account.setCreatedate(now);
	    			account.setCreator_id(currentAccount.getId());
	    			account.setOrganization_id(orgId);
	    			// set account salt
	    			account.setSalt(PasswordUtils.makeSaltBase64());
	    			
	    			if(groups.size()>0){
	    				for(Accountgroup g : groups){
	    					account.addAccountGroup(g);
	    				}
	    			}
	    			// generate password
	    			String pwd = RandomStringUtils.randomAlphanumeric(8);
	    			account.setPwd(WebUtil.securityPassword(pwd));
	    			StringBuilder accountName = new StringBuilder();
	    			if(accountProfile!=null){
	    				accountName.append(accountProfile.getFirstname()).append(" ").append(accountProfile.getLastname());
	    			}
	    			accountId = accountService.newAccount(account, personalEmail, accountName.toString());
	    			
	    			if(accountId!=null){
	    				accountProfile.setAccount_id(accountId);
	    				accountProfile.setCreatedate(now);
	    				accountProfileId = accountService.newAccountProfile(accountProfile);
	    				
	    				Contactinfo acctContact = new Contactinfo();
	    				acctContact.setAccount_id(accountId);
	    				acctContact.setCreatedate(now);
	    				acctContact.setEmail(personalEmail.trim());
	    				accountContactId = accountService.newContact(acctContact);
	    				
	    				// activate org
	    				accountService.activateOrg(orgId);
	    				
	    				// create org's product tree root
	    				String productTreeRootUuid = treeService.initialProductTree(orgId);
	    				
	    				// create org's media tree root
	    				String mediaTreeRootUuid = treeService.initialMeidaTree(orgId);
	    				
	    				// create org's desktop page tree root
	    				String desktopPageTreeRootUuid = treeService.initialPageTree(orgId, PageTreeLevelView.Type.Desktop);
	    				
	    				// create org's mobile page tree root
	    				//String mobilePageTreeRootUuid = treeService.initialPageTree(orgId, PageTreeLevelView.Type.Mobile);
	    				
	    				// create org's module tree root Modules 
	    				String moduleTreeRootUuid = treeService.initialModuleTree(orgId);
	    				
	    				// get the systemDefault group with full privilege
	    				Accountgroup existSystemDefaultGroup = accountService.getSystemDefaultGroupInOrg(orgId);
	    				if(existSystemDefaultGroup!=null){
	    					
	    					permissionService.setFullPermissionForGroup(existSystemDefaultGroup.getId());
	    					
	    				}
	    				return "redirect:addOrganization";
	    				
	    			}else{
	    				returnInputData = true;
	    				
	    				// remove org, orgprofile, orgContact from db
	    				accountService.delOrgById(orgId);
	    				if(orgProfileId!=null) accountService.delOrgProfileById(orgProfileId);
	    				if(orgContactId!=null) accountService.delContactById(orgContactId);
	    				
	    				PropertyInTable systemErrorMsg = accountService.getPropertyByKey("system.form.submit.notsave");
	    				errorMsgs.put("pageError", systemErrorMsg.getPvalue());
	    			}
	    		}else{
	    			returnInputData = true;
					PropertyInTable systemErrorMsg = accountService.getPropertyByKey("system.form.submit.notsave");
					errorMsgs.put("pageError", systemErrorMsg.getPvalue());
	    		}
	    		
				
			}else{
				
				returnInputData = true;
				
				if(!loginNameCheckPassed){
					if(!ValidationSet.isValidLoginName(account.getLoginname())){
						PropertyInTable inValidLoginName = accountService.getPropertyByKey("validateMsg.field.loginname");
						errorMsgs.put("loginname", inValidLoginName.getPvalue());
					}else{
						PropertyInTable sameLoginNameExist = accountService.getPropertyByKey("validateMsg.field.loginNameExist");
						errorMsgs.put("loginname", sameLoginNameExist.getPvalue());		
					}
				}
				
				if(!orgSameNameCheckpassed){
					PropertyInTable sameOrgNameExist = accountService.getPropertyByKey("validateMsg.field.orgNameExist");
					errorMsgs.put("orgname", sameOrgNameExist.getPvalue());		
				}
				
				if(!orgNameCheckPasssed){
					if(StringUtils.isNotBlank(errorMsgs.get("orgname"))){
						errorMsgs.put("orgname", errorMsgs.get("orgname")+
								" Also make sure that org's name don't include 'bizislife', and small than 255 characters, and only have english characters, numbers, underscores, dashs, spaces, dots.");
					}else{
						errorMsgs.put("orgname", "Make sure that org's name don't include 'bizislife', and small than 255 characters, and only have english characters, numbers, underscores, dashs, spaces, dots.");
					}
				}
				
			}
    		
    	}else{
    		
    		returnInputData = true;
    		
    		PropertyInTable notNullMsg = accountService.getPropertyByKey("validateMsg.field.notnull");
    		PropertyInTable selectMsg = accountService.getPropertyByKey("validateMsg.field.select");
    		PropertyInTable emailValiMsg = accountService.getPropertyByKey("validateMsg.field.email");
//    		PropertyInTable alphaNumUnsValiMsg = accountService.getPropertyByKey("validateMsg.field.alphaNumberUnderscore");
    		PropertyInTable sessionExpired = accountService.getPropertyByKey("system.session.expired");
    		
    		if(currentAccount==null) errorMsgs.put("pageError", sessionExpired.getPvalue());
    		if(StringUtils.isBlank(org.getOrgname())) errorMsgs.put("orgname", notNullMsg.getPvalue());
//    		if(StringUtils.isBlank(orgProfile.getIndustry())) errorMsgs.put("industry", selectMsg.getPvalue());
//    		if(StringUtils.isBlank(orgProfile.getSubcategory())) errorMsgs.put("subcategory", selectMsg.getPvalue());
    		if(StringUtils.isBlank(orgContact.getAddress())) errorMsgs.put("address", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(orgContact.getCity())) errorMsgs.put("city", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(orgContact.getState())) errorMsgs.put("state", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(orgContact.getCountry())) errorMsgs.put("country", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(orgContact.getZip())) errorMsgs.put("zip", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(personalEmail)) errorMsgs.put("personalEmail", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(account.getLoginname())) errorMsgs.put("loginname", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(accountProfile.getFirstname())) errorMsgs.put("firstname", notNullMsg.getPvalue());
    		if(StringUtils.isBlank(accountProfile.getLastname())) errorMsgs.put("lastname", notNullMsg.getPvalue());
    		
    		if(!ValidationSet.isValidEmail(personalEmail.trim())) errorMsgs.put("personalEmail", emailValiMsg.getPvalue());
    		//if(!ValidationSet.isAlphaNumUnderscoreOnly(account.getLoginname().trim())) errorMsgs.put("loginname", alphaNumUnsValiMsg.getPvalue());
    		
    	}
    	
    	// put error msg into view
    	model.put(AttributeList.RequestAttribute.errorMsgs.name(), errorMsgs);
    	
    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "addOrganization");
    	
    	// put all data inputs into request to the view if returnInputData=true
    	if(returnInputData){
//    		model.put("actionType", "addOrganization");
    		
    		model.put("org", org);
    		model.put("orgProfile", orgProfile);
    		model.put("orgContact", orgContact);
    		
    		if(useEmailAsLoginName!=null && useEmailAsLoginName) account.setLoginname(personalEmail.trim());
    		model.put("account", account);
    		model.put("accountProfile", accountProfile);
    		
    		model.put("personalEmail", personalEmail);
    		model.put("useEmailAsLoginName", useEmailAsLoginName);
    	}
    	
    	// put all industries into model
//    	List<GeneralSelectionType> allIndustries = accountService.getAllIndustries();
//    	if(allIndustries!=null && allIndustries.size()>0 && StringUtils.isNotBlank(orgProfile.getIndustry())){
//        	for(GeneralSelectionType i : allIndustries){
//        		if(i.getKey().equals(orgProfile.getIndustry().trim())){
//        			i.setSelected(Boolean.TRUE);
//        			break;
//        		}
//        	}
//        	
//        	// get all subcategories and put into model
//        	List<GeneralSelectionType> allSubcats = accountService.findSubCatsByIndustryId(orgProfile.getIndustry().trim());
//        	if(allSubcats!=null && allSubcats.size()>0 && StringUtils.isNotBlank(orgProfile.getSubcategory())){
//        		for(GeneralSelectionType s : allSubcats){
//        			if(s.getKey().equals(orgProfile.getSubcategory().trim())){
//        				s.setSelected(Boolean.TRUE);
//        				break;
//        			}
//        		}
//        	}
//        	model.put("subCats", allSubcats);
//        	
//    	}
//    	model.put("industries", allIndustries);

        return "addOrganization";
    }
    
	@RequestMapping(value="/getSubCatsByIndustryIdAjax", method=RequestMethod.GET)
	public ModelAndView getSubCatsByIndustryIdAjax(
			@RequestParam(value = "naicsCode", required = false) String naicsCode,
			HttpServletResponse response, HttpServletRequest request
			){

		ModelAndView mv = new ModelAndView("selectionOptions");
		
		if(StringUtils.isNotBlank(naicsCode)){
			List<GeneralSelectionType> subcats = accountService.findSubCatsByIndustryId(naicsCode);
			List<GeneralSelectionType> subcatList = new ArrayList<GeneralSelectionType>();
			subcatList.add(new GeneralSelectionType("", "please select ...", false));
			if(subcats!=null && subcats.size()>0){
				subcatList.addAll(subcats);
			}
			Collections.sort(subcatList);
			mv.addObject("options", subcatList);			
		}
		return mv;
		
	}
	
    @RequestMapping(value="/getOrgInfo", method=RequestMethod.GET)
    public String getOrgInfo(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {
    	
    	model.put("currentPageId", orguuid);
    	
    	if(StringUtils.isNotBlank(orguuid)){
    		// find org by uuid
    		Organization org = accountService.getOrgByUuid(orguuid);
    		// escape org's name
    		org.setOrgname(StringEscapeUtils.escapeHtml(org.getOrgname()));
    		model.put("org", org);
    		
    		if(org!=null){
        		// find org's profile
        		Organizationprofile orgProfile = accountService.getOrgProfileByOrgId(org.getId());
        		
        		// find industries
//        		List<GeneralSelectionType> industries = accountService.getAllIndustries();
//        		if(orgProfile!=null){
//            		List<GeneralSelectionType> allSubcats = accountService.findSubCatsByIndustryId(orgProfile.getIndustry());
//            		// set selected info
//            		for(GeneralSelectionType i : industries){
//            			if(i.getKey().equals(orgProfile.getIndustry())){
//            				i.setSelected(Boolean.TRUE);
//            				break;
//            			}
//            		}
//            		if(allSubcats!=null && allSubcats.size()>0){
//                		for(GeneralSelectionType c : allSubcats){
//                			if(c.getKey().equals(orgProfile.getSubcategory())){
//                				c.setSelected(Boolean.TRUE);
//                				break;
//                			}
//                		}
//                		model.put("subCats", allSubcats);
//            		}
//        		}
//        		model.put("industries", industries);
        		
        		// find contact info : note: support one contact for one org now.
        		List<Contactinfo> orgContacts = accountService.findOrgContactsByOrgId(org.getId());
        		model.put("orgContact", (orgContacts!=null && orgContacts.size()>0)?orgContacts.get(0):null);
    			model.put("orgProfile", orgProfile);
    		}
    		
    		
    	}else{
    		return "error_general";
    	}
        return "organizationInfo";
    }
    
	
    @RequestMapping(value="/organizations", method=RequestMethod.GET)
    public String organizationsPage(
            ModelMap model) {
    	
    	// put the page name into request, and tree can hightlight the node
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "organizations");
    	
    	// put org list into the map
    	List<Organization> orgs = accountService.findAllOrgs();
    	model.put("orgs",orgs);

        return "organizations";
    }
    
    @RequestMapping(value="/domainSubmit", method=RequestMethod.POST)
    public String domainSubmit(
    		@RequestParam(value = "orgid", required = true) String orguuid,
    		@RequestParam(value = "domainName", required = true) String domainName,
    		ModelMap model){
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(org!=null && StringUtils.isNotBlank(domainName)){
    		accountService.saveDomainNameForOrg(org.getId(), domainName);
    	}
    	
    	return "redirect:/websiteConfig?org="+orguuid;
    	
    }
    
    @RequestMapping(value="/domainRemoveSubmit", method=RequestMethod.POST)
    public String domainRemoveSubmit(
    		@RequestParam(value = "orgid", required = true) String orguuid,
    		@RequestParam(value = "domainName", required = true) String domainName,
    		ModelMap model){
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(org!=null && StringUtils.isNotBlank(domainName)){
    		accountService.removeDomainNameForOrg(org.getId(), domainName);
    	}
    	
    	return "redirect:/websiteConfig?org="+orguuid;
    	
    }
    
    
    @RequestMapping(value="/domainValidateForOrg", method=RequestMethod.POST)
    public @ResponseBody ApiResponse domainValidateForOrg(
    		@RequestParam(value = "orgid", required = true) String orguuid,
    		@RequestParam(value = "domainName", required = true) String domainName,
    		ModelMap model){
    	
    	return accountService.domainValidateForOrg(orguuid, domainName);
    }
    
    @RequestMapping(value="/updateOrgInfo", method=RequestMethod.POST)
    public @ResponseBody ApiResponse updateOrgInfo(
    		@RequestParam(value = "updatedName", required = true) String updatedName,
    		@RequestParam(value = "orguuid", required = true) String orguuid,
    		HttpServletRequest request,
    		ModelMap model){
    	
    	
		Map<String, String> nameValueMap = new HashMap<String, String>();

		Map<String, String[]> keynameFromRequest = request.getParameterMap();

		for(Map.Entry<String, String[]> entry : keynameFromRequest.entrySet()){
			if(entry.getKey().indexOf("updatedValue_")>-1){
				nameValueMap.put(entry.getKey().substring(13), entry.getValue()[0]);
			}
		}
    	
    	return accountService.updateOrgInfo(orguuid, updatedName, nameValueMap);
		
//		return null;
    }
    
    
    

}
