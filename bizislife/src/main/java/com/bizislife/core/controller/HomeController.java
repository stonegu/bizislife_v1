package com.bizislife.core.controller;

import java.io.StringWriter;
import java.net.InetAddress;
import java.util.*;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.Pagination;
import com.bizislife.core.controller.component.TopicTreeNode;
import com.bizislife.core.entity.converter.ModuleConverter;
import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.bizislife.core.entity.converter.ModuleAttrGroupConverter;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.service.*;
import com.bizislife.core.service.activemq.QueueMsgSender;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.ModuleIntegerAttribute;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleStringAttribute;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.PasswordUtils;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.AttributeList;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.definition.EmailRelated;
import com.bizislife.util.validation.ValidationSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;


@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    private PaymentService paymentService;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
    private QueueMsgSender queueMsgSender;
	
    @Autowired
    private ActiveMQQueue emailQueue;

    @Autowired
    protected ApplicationConfiguration applicationConfig;

    // ######################################################
    
    //TODO: need super star user name & password to do this
    @RequestMapping(value="/initialDb", method = RequestMethod.GET)
    public @ResponseBody String initialDb() {
    	
    	// check if the data already exist
    	List<Organization> orgs = accountService.findAllOrgs();
    	if(orgs==null || orgs.size()<1){
    		
        	Date now = new Date();
        	
//        	accountService.initialDb();
        	
    		// org for bizislife
    		Organization org = new Organization(
    				null, 
    				"bizislife", 
    				"bizislife",
    				UUID.randomUUID().toString(),
    				DatabaseRelatedCode.OrganizationRelated.orglevel_bizislife.getCode(), 
    				now, 
    				now, 
    				null,
    				null,
    				Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()), 
    				null);
    		//set salt for org:
    		org.setSalt(PasswordUtils.makeSaltBase64());
    		Long orgId = accountService.newOrg(org);
    		Long orgProfileId = null;
    		Long orgContactId = null;
    		Long orgMetaId = null;
    		
    		if(orgId!=null){
    			// set free payment info
    			PaymentPlan freemiumPaymentPlan = new PaymentPlan(null, 
    					"free plan", 
    					null, 
    					null, 
    					50l, 
    					4000l, 
    					6000l, 
    					4000l, 
    					2000l, 
    					PaymentPlan.Status.activate.getCode(), 
    					PaymentPlan.defaultPlan.yes.getCode());
    			paymentService.savePaymentPlan(freemiumPaymentPlan);
    			
    			Organizationprofile orgProfile = new Organizationprofile(
    					null, 
    					orgId, 
    					"tempImageLocation", // TODO: will replace with s3's location 
    					"Ind", // TODO: will replace with real industry code
    					"SubCat", // TODO: will replace with real sub category 
    					"web, business, online", 
    					null, 
    					"bizislife is the web content system to service the creation of the website", 
    					null, 
    					now);

    			orgProfileId = accountService.newOrgProfile(orgProfile);
    		
    			// org's contact info
    			Contactinfo orgContact = new Contactinfo(
    					null, 
    					null, 
    					orgId, 
    					"bizislife's main contact information", 
    					"contact@bizislife.com", 
    					null, 
    					null, 
    					null, 
    					null, 
    					null,
    					null,
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					null, 
    					now);
    			orgContactId = accountService.newContact(orgContact);
    			
    			// for orgMeta data
    			OrgMeta orgMeta = new OrgMeta(null, 
    					orgId,
    					org.getOrguuid(),
    					"localhost,bizislife.com");
    			orgMetaId = accountService.newOrgMeta(orgMeta);
    			

    			// add a everyonegroup for anybody can join
    			OrgCanJoin ocj_e = new OrgCanJoin(null, orgId, 10000, UUID.randomUUID().toString());
    			Accountgroup everyoneGroup = new Accountgroup(null, UUID.randomUUID().toString(), Accountgroup.GroupType.Everyone.name(), Accountgroup.GroupAccessLevel.Global.getCode(), 
    					Accountgroup.GroupAccessLevel.Global.getDesc(), now, now, null, orgId, 
    					Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()), 
    					Accountgroup.GroupType.Everyone.name(), null);
    			everyoneGroup.addOrgCanJoin(ocj_e);
    			Long everyoneGroupId = accountService.newGroup(everyoneGroup);

    			// add SystemDefault group
    			OrgCanJoin ocj_a = new OrgCanJoin(null, orgId, 10000, UUID.randomUUID().toString());
    			Accountgroup adminGroup = new Accountgroup(null, UUID.randomUUID().toString(), Accountgroup.GroupType.SystemDefault.name(), Accountgroup.GroupAccessLevel.Private.getCode(), 
    					"System group with full privilege, you can't reset permission for the group!", now, now, null, orgId,
    					Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
    					Accountgroup.GroupType.SystemDefault.name(),
    					null
    					);
    			adminGroup.addOrgCanJoin(ocj_a);
    			Set<Accountgroup> groups = new HashSet<Accountgroup>();
    			groups.add(adminGroup);
    			groups.add(everyoneGroup);
    			
    			// new admin account.
    			Account account = new Account(
    					null, 
    					"admin@bizislife", 
    					WebUtil.securityPassword("bizislife"), 
    					UUID.randomUUID().toString(),
    					now, 
    					now, 
    					null, 
    					orgId, 
    					Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()), 
    					groups, 
    					null);
    			// set salt
    			account.setSalt(PasswordUtils.makeSaltBase64());
    			
    			Long accountId = accountService.newAccount(account, null, "sysAdmin@Bizislife");
    			
    			if(accountId!=null){
    				// accountprofile
    				Accountprofile accountProfile = new Accountprofile(
    						null,
    						accountId,
    						"sysAdmin", "@", "Bizislife",
    						null,
    						null,
    						null,
    						null,
    						null,
    						now);

    				Long accountProfileId = accountService.newAccountProfile(accountProfile);
    				
    				// account contact info
    				Contactinfo acctContact = new Contactinfo();
    				acctContact.setAccount_id(accountId);
    				acctContact.setCreatedate(now);
    				acctContact.setEmail("contact@bizislife.com");
    				Long accountContactId = accountService.newContact(acctContact);

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
    				Accountgroup systemDefaultGroup = accountService.getSystemDefaultGroupInOrg(orgId);
    				if(systemDefaultGroup!=null){
    					
    					permissionService.setFullPermissionForGroup(systemDefaultGroup.getId());
    					
    					
    				}
    				
    			}

    		}
    	}
    	
        
        
        return "<html><body><h1>Done.</h1></body></html>";
    }
    
    // test mongo db 
	@RequestMapping(value="/mongotest", method=RequestMethod.GET)
	public @ResponseBody String mongotest() {
		
		ActivityLog re1 = new ActivityLog(1l, 100l, 200l, "mongotype", "mongodata", new Date()); 
		
		mongoTemplate.save(re1, "rootentities");
		
		ActivityLog activityLog = mongoTemplate.findOne(
				new Query(
		                    Criteria.where("type").is("mongotype")
		                ), ActivityLog.class,"rootentities"
		          );
		
		return "<html><body><h1>"+activityLog.getType()+"</h1></body></html>";
		
	}


    // ######################################################

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login(ModelMap model){
    	if(accountService.getCurrentAccount()!=null){
        	model.put(AttributeList.RequestAttribute.currentPageId.name(), "home");
    		return "forward:/";
    	}else{
        	model.put(AttributeList.RequestAttribute.currentPageId.name(), "login");
        	return "login";
    	}
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String doLogin(
			@RequestParam(value = "username", required = true) String username, 
			@RequestParam(value = "password", required = true) String password, 
			HttpServletRequest req) {
    	
    	Organization bizOrg = accountService.getBizOrg();
    	
    	if(bizOrg!=null){
    		// check login user's ip:
    		String ipAddress = req.getHeader("X-FORWARDED-FOR");  
    		if (ipAddress == null) {  
    			ipAddress = req.getRemoteAddr();  
    		}
    		// log the ipaddress:
    		// 1) log the activity
    		ActivityLogData activityLogData = new ActivityLogData();
    			Map<String, Object> dataMap = new HashMap<String, Object>();
    			dataMap.put("username", username);
    			dataMap.put("password", password);
    			dataMap.put("ipaddress", ipAddress);
    			activityLogData.setDataMap(dataMap);
    			String desc = "login log";
    			activityLogData.setDesc(desc);
    		Long activityLogId = messageService.newActivity(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()), bizOrg.getId(), ActivityType.userLogin, activityLogData);
    		
        	if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
        		username = username.trim();
        		password = password.trim();
                boolean success = accountService.accountCheck(username, password);
        		if (success) {
        			
        			// check for back url
        			String back = (String)req.getSession().getAttribute(AttributeList.SessionAttribute.back_url.name());
        			if (back==null || back.equals("/login")) {
        				back = "/"; // default value
        			} else if(back.equals("/")){
        				// default landing page
        				back = "/";
        			} else {
        				req.getSession().setAttribute(AttributeList.SessionAttribute.back_url.name(), null);
        			}
        			return "redirect:"+back;
        		}
        		
        	}
        	
    		
    	}
    	
		return "redirect:/login";
    }

    @RequestMapping(value="/logout")
    public String doLogout(HttpServletRequest req) {
        accountService.accountLogout();
        req.getSession(false).invalidate();
		return "redirect:/";
    }
    
    // ######################################################

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String display(
//			@RequestParam(required = false) String pageMsg,
//			@RequestParam(required = false) String pageErrorMsg, 
            ModelMap model) {

//        AccountDto account = accountService.getCurrentAccount();

//        model.put("families",accountService.getFamilies());
//        model.put("accounts", accountService.getAccounts());
//        model.put("currentAccount", accountDto);
//        model.put("newRuleset", new Ruleset());
//        model.put("newRule", new Rule());
//        model.put("rules", accountService.getRules());
//        model.put("rulesets", accountService.getRulesets());
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "home");

        return "home";
    }
    
    @RequestMapping(value="/news", method=RequestMethod.GET)
    public String newsPage(
			@RequestParam(value = "totalResultsNum", required = false) Integer totalResultsNumInPage, 
			@RequestParam(value = "pageIdx", required = false) Integer pageIdx, 
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "news");
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	if(currentAccount!=null){
    		
    		if(totalResultsNumInPage==null || totalResultsNumInPage.intValue()<0) totalResultsNumInPage = Integer.MAX_VALUE;
    		if(pageIdx==null || pageIdx<=1) pageIdx = 1;
    		
    		int totalMsgNumber = messageService.countMsg(currentAccount.getId(), Message.MsgType.topicPost);
    		if(totalMsgNumber>totalResultsNumInPage){
    			int numberOfPages = totalMsgNumber / totalResultsNumInPage;
    			int remainder = totalMsgNumber % totalResultsNumInPage;
    			if(remainder>0){
    				numberOfPages++;
    			}
    			
    			if(pageIdx>numberOfPages){
    				pageIdx = numberOfPages;
    			}
    		}
    		
    		// calcu offset
    		int offset = totalResultsNumInPage * (pageIdx - 1);
    		
    		List<Message> msgs = messageService.findMsgsByAccount(currentAccount.getId(), Message.MsgType.topicPost, totalResultsNumInPage, offset);
//    		Collections.sort(msgs, Message.createDateDescendComparator);
    		
    		model.put("msgs", msgs);
    		
    		
    		// for pagination
    		boolean hasPagination = false;
    		if(totalMsgNumber > totalResultsNumInPage.intValue()){
    			hasPagination = true;
    		}
    		if(hasPagination){
    			Pagination pagination = messageService.getPaginationForMessage(applicationConfig.getHostName(), currentAccount.getId(), Message.MsgType.topicPost, totalResultsNumInPage, pageIdx);
    			model.put("pagination", pagination);
    		}
    		
    	}

        return "news";
    }
    
    @RequestMapping(value="/delMsg", method=RequestMethod.GET)
    public String delMsg(
			@RequestParam(value = "msgId", required = true) Long msgId, 
            ModelMap model) {
    	
    	if(msgId!=null){
    		messageService.delMsg(msgId);
    	}
    	
        return "redirect:/news";
    }
    
    @RequestMapping(value="/subscribe", method=RequestMethod.GET)
    public String subscribePage(
            ModelMap model) {

    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "subscribe");
    	
    	// get current org and put into request
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	if(currentAccount!=null){
        	Organization currentOrg = accountService.getOrgById(currentAccount.getOrganization_id());
        	if(currentOrg!=null){
            	model.put("orgUuid", currentOrg.getOrguuid());
        	}
    	}
    	
//    	Tree topicTree = treeService.getTree(Tree.TreeCategory.Topic);

        return "subscribe";
    }
    
    @RequestMapping(value="/saveSubscribes", method=RequestMethod.GET)
    public @ResponseBody ApiResponse saveSubscribes(
			@RequestParam(value = "topicUuids", required = true) String[] topicUuids
			) {
    	
    	ApiResponse res = null;
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	if(topicUuids!=null && topicUuids.length>0 && currentAccount!=null){
    		Map<String, List<String>> newSavedSubscribes = messageService.saveSubscribes(topicUuids, currentAccount.getId());
    		if(newSavedSubscribes!=null && newSavedSubscribes.size()>0){
    			res = new ApiResponse();
    			res.setSuccess(true);
    			res.setResponse1(newSavedSubscribes);
    		}
    	}

        return res;
    }
    
    @RequestMapping(value="/saveSubscribe", method=RequestMethod.GET)
    public @ResponseBody ApiResponse saveSubscribes(
			@RequestParam(value = "topicUuid", required = true) String topicUuid,
			@RequestParam(value = "type", required = true) String type
			
			) {
    	
    	ApiResponse res = null;
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	
    	if(currentAccount!=null && StringUtils.isNotBlank(topicUuid) 
    			&& (StringUtils.equals(type, Subscribe.type.subscribe.name()) || StringUtils.equals(type, Subscribe.type.unsubscribe.name()))){

    		Long subscribeId = messageService.saveSubscribe(topicUuid, type, currentAccount.getId()); 
    		if(subscribeId!=null){
    			res = new ApiResponse();
    			res.setSuccess(true);
    			res.setResponse1(subscribeId);
    		}
    	}
    	
        return res;
    }
    
    @RequestMapping(value="/getTopicInfo")
    public ModelAndView getTopicInfo(
    		@RequestParam(value="targetId", required=false) String targetid
    		){
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = new ModelAndView("topicInfoFragment");
    	
    	
    	//???????????
//    	if(type!=null) mv.addObject("viewType", type);
//    	mv.addObject("targetId", targetid);
    	//????????????
    	
    	
    	
    	
		Topic topic = messageService.getTopicByUuid(targetid);
    	if(loginAccount!=null && topic!=null){
    		
    		
    		// get topic based on this topic uuid
    		if(topic!=null) mv.addObject("topic", topic);
    		
    		// if currentAccount is admin and topic belongs to current account's org, then editable
    		boolean editable = false;
    		if(loginAccount!=null && loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().equals(topic.getOrg_id())) editable = true;
    		mv.addObject("editable", editable);
    		
    		// add org info into modelandview
    		if(topic!=null){
    			Organization org = accountService.getOrgById(topic.getOrg_id());
    			mv.addObject("org", org);
    		}
    		
    		// add all visible orgs, groups, accounts info view for selection
    		if(topic!=null){
    			// all orgs with selected info
    			List<GeneralSelectionType> allOrgs = messageService.findTopicVisibleOrgs(topic.getTopicuuid());
    			mv.addObject("allOrgs", allOrgs);
    			// all org's groups with selected info
    			List<GeneralSelectionType> allGroups = messageService.findTopicVisibleGroupsInOrg(topic.getTopicuuid(), topic.getOrg_id());
    			mv.addObject("allGroup", allGroups);
    			// all org's accounts with selected info
    			List<GeneralSelectionType> allAccts = messageService.findTopicVisibleAccountsInOrg(topic.getTopicuuid(), topic.getOrg_id());
    			mv.addObject("allAccounts", allAccts);
    		}
    		
    	}    	
    	
    	return mv;
    }
    
    @RequestMapping(value="/topicPropsModify")
    public String topicPropsModify(
    		@RequestParam(value="topicuuid", required=false) String topicuuid,
    		@RequestParam(value="accesslevel", required=false) String accesslevel,
    		@RequestParam(value="visibleOrg", required=false) String[] visibleOrgs,
    		@RequestParam(value="visibleGroup", required=false) String[] visibleGroups,
    		@RequestParam(value="visibleAccount", required=false) String[] visibleAccounts,
            ModelMap model) {
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && StringUtils.isNotBlank(topicuuid)){
    		// get topic
    		Topic topic = messageService.getTopicByUuid(topicuuid);
    		
    		if(topic.getOrg_id().equals(currentAccount.getOrganization_id())){ // only modify by org's admin
    			Topic.AccessLevel xlevel = null;
    			if(accesslevel!=null){
    				xlevel = Topic.AccessLevel.fromCode(accesslevel);
    			}
    			messageService.updateTopicWithVisibles(topicuuid, xlevel, visibleOrgs, visibleGroups, visibleAccounts);
    		}
    		
    	}

        return "redirect:/subscribe";
    }
    
    
    @RequestMapping(value="/todo", method=RequestMethod.GET)
    public String todo(
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "todo");
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
//    	if(loginAccount!=null){
//    	}

        return "todo";
    }
    
    @RequestMapping(value="/dashboard", method=RequestMethod.GET)
    public String dashboard(
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "dashboard");
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
//    	if(loginAccount!=null){
//    	}

        return "dashboard";
    }
    
    @RequestMapping(value="/report", method=RequestMethod.GET)
    public String report(
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "report");
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
//    	if(loginAccount!=null){
//    	}

        return "report";
    }
    
    @RequestMapping(value="/messages", method=RequestMethod.GET)
    public String messages(
			@RequestParam(value = "totalResultsNum", required = false) Integer totalResultsNumInPage, 
			@RequestParam(value = "pageIdx", required = false) Integer pageIdx, 
            ModelMap model) {
    	
    	model.put(AttributeList.RequestAttribute.currentPageId.name(), "messages");
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	if(currentAccount!=null){
    		
    		if(totalResultsNumInPage==null || totalResultsNumInPage.intValue()<0) totalResultsNumInPage = Integer.MAX_VALUE;
    		if(pageIdx==null || pageIdx<=1) pageIdx = 1;
    		
    		int totalMsgNumber = messageService.countMsg(currentAccount.getId(), Message.MsgType.memberSend);
    		if(totalMsgNumber>totalResultsNumInPage){
    			int numberOfPages = totalMsgNumber / totalResultsNumInPage;
    			int remainder = totalMsgNumber % totalResultsNumInPage;
    			if(remainder>0){
    				numberOfPages++;
    			}
    			
    			if(pageIdx>numberOfPages){
    				pageIdx = numberOfPages;
    			}
    		}
    		
    		// calcu offset
    		int offset = totalResultsNumInPage * (pageIdx - 1);
    		
    		List<Message> msgs = messageService.findMsgsByAccount(currentAccount.getId(), Message.MsgType.memberSend, totalResultsNumInPage, offset);
//    		Collections.sort(msgs, Message.createDateDescendComparator);
    		
    		model.put("msgs", msgs);
    		
    		
    		// for pagination
    		boolean hasPagination = false;
    		if(totalMsgNumber > totalResultsNumInPage.intValue()){
    			hasPagination = true;
    		}
    		if(hasPagination){
    			Pagination pagination = messageService.getPaginationForMessage(applicationConfig.getHostName(), currentAccount.getId(), Message.MsgType.memberSend, totalResultsNumInPage, pageIdx);
    			model.put("pagination", pagination);
    		}
    		
    		
    		
    	}

        return "messages";
    }
    
    
    @RequestMapping(value="/bugReportSubmit", method=RequestMethod.POST)
    public @ResponseBody ApiResponse bugReportSubmit(
			@RequestParam(value = "pageName", required = false) String pageName,
			@RequestParam(value = "issue", required = false) String issue,
			HttpServletResponse response, HttpServletRequest request
			) {
    	
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	StringBuilder loginAccountInfo = new StringBuilder();
    	if(loginAccount!=null){
    		loginAccountInfo.append(loginAccount.getLastname()).append(" ").append(loginAccount.getFirstname());
    		loginAccountInfo.append("(uuid: ").append(loginAccount.getAccountuuid()).append(", loginName: ").append(loginAccount.getLoginname()).append(")");
    	}
    	
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();  
		}		
    	
		// set all args to email template
		Map<String, Map<String, String>> emailReceiversWithArgs = new HashMap<String, Map<String,String>>();
		Map<String, String> args = new HashMap<String, String>();
		args.put("ipAddress", ipAddress);
		args.put("fromUser", loginAccountInfo.length()>0?loginAccountInfo.toString():"");
		args.put("pagename", pageName);
		args.put("bugContent", issue);
		emailReceiversWithArgs.put(applicationConfig.getBugReceiver(), args);
		ActiveMQMapMessage msg = new ActiveMQMapMessage();
		try {
			msg.setObject(QueueMsgSender.mapMsgName.emails.name(), emailReceiversWithArgs);
			msg.setString(QueueMsgSender.mapMsgName.emailType.name(), EmailRelated.EmailType.bugReport.name());
		} catch (JMSException e) {
			e.printStackTrace();
			logger.debug(e.toString());
			res.setResponse1(e.toString());
		}
		queueMsgSender.send(emailQueue, msg);
		
		res.setSuccess(true);
        return res;
    }
    
    @RequestMapping(value="/applicationSubmit", method=RequestMethod.POST)
    public @ResponseBody ApiResponse applicationSubmit(
			@RequestParam(value = "contactEmail", required = false) String contactEmail,
			HttpServletResponse response, HttpServletRequest request
			) {
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	
    	// email validation
    	boolean isEmail = ValidationSet.isValidEmail(contactEmail);
    	if(isEmail){

    		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
    		if (ipAddress == null) {
    			ipAddress = request.getRemoteAddr();  
    		}		
        	
    		// set all args to email template
    		Map<String, Map<String, String>> emailReceiversWithArgs = new HashMap<String, Map<String,String>>();
    		Map<String, String> args = new HashMap<String, String>();
    		args.put("ipAddress", ipAddress);
    		args.put("fromEmail", contactEmail);
    		emailReceiversWithArgs.put(applicationConfig.getApplicationReceiver(), args);
    		ActiveMQMapMessage msg = new ActiveMQMapMessage();
    		try {
    			msg.setObject(QueueMsgSender.mapMsgName.emails.name(), emailReceiversWithArgs);
    			msg.setString(QueueMsgSender.mapMsgName.emailType.name(), EmailRelated.EmailType.applicationSubmit.name());
    		} catch (JMSException e) {
    			e.printStackTrace();
    			logger.debug(e.toString());
    			res.setResponse1(e.toString());
    		}
    		queueMsgSender.send(emailQueue, msg);
    		
    		res.setSuccess(true);
    		
    	}else{
    		res.setResponse1("Please input email address");
    	}
    	
    	return res;
    }
    	
    @RequestMapping(value="/contactInfoSubmit", method=RequestMethod.POST)
    public @ResponseBody ApiResponse contactInfoSubmit(
			@RequestParam(value = "contactInfo", required = false) String contactInfo,
			HttpServletResponse response, HttpServletRequest request
			) {
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();  
		}		
    	
		// set all args to email template
		Map<String, Map<String, String>> emailReceiversWithArgs = new HashMap<String, Map<String,String>>();
		Map<String, String> args = new HashMap<String, String>();
		args.put("ipAddress", ipAddress);
		args.put("contactinfo", contactInfo);
		emailReceiversWithArgs.put(applicationConfig.getContactInfoReceiver(), args);
		ActiveMQMapMessage msg = new ActiveMQMapMessage();
		try {
			msg.setObject(QueueMsgSender.mapMsgName.emails.name(), emailReceiversWithArgs);
			msg.setString(QueueMsgSender.mapMsgName.emailType.name(), EmailRelated.EmailType.contactInfo.name());
		} catch (JMSException e) {
			e.printStackTrace();
			logger.debug(e.toString());
			res.setResponse1(e.toString());
		}
		queueMsgSender.send(emailQueue, msg);
		
		res.setSuccess(true);
    	
    	return res;
    }

}
