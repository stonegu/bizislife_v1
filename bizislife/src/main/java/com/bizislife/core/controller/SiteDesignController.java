package com.bizislife.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ValueNode;
import org.dozer.Mapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scala.reflect.generic.Trees.ModuleDef;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.controller.helper.SwallowingJspRenderer;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.entity.MetaData;
import com.bizislife.core.hibernate.dao.SiteDesignDao;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.InstanceViewSchedule;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.ModuleDetail.Type;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.ModuleInstanceSchedule;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.ModuleTreeLevelView;
import com.bizislife.core.hibernate.pojo.ModuleXml;
import com.bizislife.core.hibernate.pojo.OrgMeta;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.hibernate.pojo.PageTreeLevelView;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.ScheduleInterface;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.MediaService;
import com.bizislife.core.service.MessageFromPropertiesService;
import com.bizislife.core.service.PaymentService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.ProductService;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttributeList;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleImageAttribute;
import com.bizislife.core.siteDesign.module.ModuleIntegerAttribute;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute;
import com.bizislife.core.siteDesign.module.ModuleMoneyAttribute;
import com.bizislife.core.siteDesign.module.ModuleNumberAttribute;
import com.bizislife.core.siteDesign.module.ModuleProductListAttribute;
import com.bizislife.core.siteDesign.module.ModuleStringAttribute;
import com.bizislife.core.siteDesign.module.ModuleTextAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.Money;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.core.view.dto.OrgPaymentProfile;
import com.bizislife.util.DateUtils;
import com.bizislife.util.PasswordUtils;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.AttributeList;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.validation.ValidationSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Controller
public class SiteDesignController {

	private static final Logger logger = LoggerFactory.getLogger(SiteDesignController.class);

	@Autowired 
	ServletContext servletContext;
	
	@Autowired
	MessageFromPropertiesService messageFromPropertiesService;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;
    
//    @Autowired
    //private SiteDesignDao siteDesignDao;
    
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    private TreeService treeService;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ProductService productService;
    
	@Autowired
	PermissionService permissionService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	MediaService mediaService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;
    
	@Autowired
    private Mapper mapper;

    @RequestMapping(value="/siteDesign_module_test", method = RequestMethod.GET)
    public ResponseEntity<String> siteDesign_module_test() {
    	
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	    
	    // create a sample module
	    Module module = new Module();
	    
	    ModuleStringAttribute string1 = new ModuleStringAttribute("111", "string1", "String #1", true, "this is the document for String #1", true, false, 0, 100, "default value 1");
	    ModuleIntegerAttribute int1 = new ModuleIntegerAttribute("222", "int1", "int #1", true, "document for integer", true, false, 0, 101, 0);

	    AttrGroup group1 = new AttrGroup();
	    group1.addAttr(string1);
	    group1.addAttr(int1);
	    group1.setArray(false);
	    group1.setGroupName("group1");
	    
	    module.addAttrGroup(group1);
	    
    	return new ResponseEntity<String>(SitedesignHelper.getXmlFromModule(module), responseHeaders, HttpStatus.CREATED);
    }
    
//    @Deprecated
//	@RequestMapping(value="/siteDesign_module_jsp_test", method=RequestMethod.GET)
//	public String siteDesign_module_jsp_test(ModelMap model){
//		
//		// get xml
//		ModuleXml moduleXml = siteDesignDao.getModuleXmlById(1l);
//		
//		if(moduleXml!=null){
//			Module module = SitedesignHelper.getModuleFromXml(moduleXml.getXml());
//			model.put("testModule1", module);
//		}
//		
//		return "/module/system/testModule1";
//	}
    
    @RequestMapping(value="/website", method=RequestMethod.GET)
    public String website(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(org!=null){
    		
    		AccountDto loginAccount = accountService.getCurrentAccount();
    		model.put("loginAccount", loginAccount);
    		
        	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("website_").append(orguuid).toString());
        	model.put("orgid", orguuid);
        	
    	}
    	
    	return "website";
    }
    
    @RequestMapping(value="/entity", method=RequestMethod.GET)
    public String entity(
			@RequestParam(value = "org", required = false) String orguuid,
            ModelMap model) {

    	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("entity_").append(orguuid).toString());
    	
//    	if(StringUtils.isNotBlank(orguuid)){
//    		model.put("orguuid", orguuid);
//    		
//    	}
    	
    	return "entity";
    }
    
    @RequestMapping(value="/websitePages", method=RequestMethod.GET)
    public String websitePages(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	if(org!=null){
        	model.put("currentPageId", new StringBuilder("responsivePages_").append(orguuid).toString());
        	model.put("orgUuid", orguuid);
        	model.put("pageType", "desktop");
        	
        	// find all desktop container with it's hexcolor
        	List<PageDetail> pageDetails = siteDesignService.findOrgPagesByType(orguuid, PageDetail.Type.Desktop);
        	if(pageDetails!=null && pageDetails.size()>0){
        		List<ContainerDetail> containers = new ArrayList<ContainerDetail>();
        		for(PageDetail p : pageDetails){
        			List<ContainerDetail> pageContainers = siteDesignService.findPageContainers(p.getPageuuid());
        			if(pageContainers!=null && pageContainers.size()>0){
        				containers.addAll(pageContainers);
        			}
        		}
        		
        		if(containers.size()>0){
//        			Map<String, String> containerHexcolorMap = new HashMap<String, String>();
        			Set<String> containerHexcolors = new HashSet<String>();
        			
        			for(ContainerDetail c : containers){
        				//containerHexcolorMap.put(c.getContaineruuid(), c.getHexColor());
        				containerHexcolors.add(c.getHexColor());
        			}
        			
        			if(containerHexcolors.size()>0){
        				model.put("containerHexcolors", containerHexcolors);
        			}
        		}
        	}
        	
        	// find totals page created & maximum number of pages can create
//        	int pagesAlreadyHave = siteDesignService.countAllPagesForOrg(org.getId(), PageDetail.Type.Page);
        	
//			OrgPaymentProfile orgPaymentProfile = accountService.getCurrentOrgPaymentProfile();
//			OrgPaymentProfile orgPaymentProfile = accountService.getOrgPaymentProfile(org.getId());
//			if(orgPaymentProfile!=null && orgPaymentProfile.getPagesCanHave()!=null){
//				if(orgPaymentProfile.getPagesCanHave().longValue()>pagesCanHave){
//					pagesCanHave = orgPaymentProfile.getPagesCanHave().longValue();
//				}
//			}
			
//			model.put("pagesAlreadyHave", pagesAlreadyHave);
//			model.put("pagesCanHave", pagesCanHave);
        	
        	
    		
    	}
    	
    	
        return "generalPageManage";
    }
    
    
    @RequestMapping(value="/websiteConfig", method=RequestMethod.GET)
    public String websiteConfig(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	
    	if(org!=null){
    		
    		AccountDto loginAccount = accountService.getCurrentAccount();
    		model.put("loginAccount", loginAccount);
    		
        	model.put(AttributeList.RequestAttribute.currentPageId.name(), new StringBuilder("websiteConfig_").append(orguuid).toString());
        	model.put("orgid", orguuid);
        	
        	// get all domain names with status
        	OrgMeta orgMeta = accountService.getOrgMetaByOrgUuid(orguuid);
        	List<GeneralSelectionType> domainNameListWithStatus = new ArrayList<GeneralSelectionType>();
        	if(orgMeta!=null && StringUtils.isNotBlank(orgMeta.getDomains())){
        		String[] domainnamesWithStatus = orgMeta.getDomains().split(",");
//        		int count = 0;
        		for(String dn : domainnamesWithStatus){
//        			count++;
        			if(dn.indexOf("[")>-1){
        				String dnWithoutStatus = dn.substring(0, dn.indexOf("["));
        				
        				// create a validation txt key for domain
        				String validationKey = PasswordUtils.hashPasswordBase64(dnWithoutStatus, org.getSalt());
        				
        				domainNameListWithStatus.add(new GeneralSelectionType(validationKey, 
        						dnWithoutStatus, 
        						Boolean.FALSE));
        			}else{
        				// create a validation txt key for domain
        				String validationKey = PasswordUtils.hashPasswordBase64(dn, org.getSalt());
        				
        				domainNameListWithStatus.add(new GeneralSelectionType(validationKey, dn, Boolean.TRUE));
        			}
        		}
        	}
        	
        	model.put("orgDomains", domainNameListWithStatus);
        	
        	model.put("proxyId", applicationConfig.getProxyId());
        	
//        	if(StringUtils.isNotBlank(orguuid)){
//        		model.put("orguuid", orguuid);
//        		
//        	}
    	}
    	
    	return "websiteConfig";
    }

    @RequestMapping(value="/websiteMobile", method=RequestMethod.GET)
    public String websiteMobile(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	Organization org = accountService.getOrgByUuid(orguuid);
    	if(org!=null){
        	model.put("currentPageId", new StringBuilder("mobile_").append(orguuid).toString());
        	model.put("orgUuid", orguuid);
        	model.put("pageType", "mobile");
        	
        	// find all mobile container with it's hexcolor
        	List<PageDetail> pageDetails = siteDesignService.findOrgPagesByType(orguuid, PageDetail.Type.Mobile);
        	if(pageDetails!=null && pageDetails.size()>0){
        		List<ContainerDetail> containers = new ArrayList<ContainerDetail>();
        		for(PageDetail p : pageDetails){
        			List<ContainerDetail> pageContainers = siteDesignService.findPageContainers(p.getPageuuid());
        			if(pageContainers!=null && pageContainers.size()>0){
        				containers.addAll(pageContainers);
        			}
        		}
        		
        		if(containers.size()>0){
//        			Map<String, String> containerHexcolorMap = new HashMap<String, String>();
        			Set<String> containerHexcolors = new HashSet<String>();
        			
        			for(ContainerDetail c : containers){
        				//containerHexcolorMap.put(c.getContaineruuid(), c.getHexColor());
        				containerHexcolors.add(c.getHexColor());
        			}
        			
        			if(containerHexcolors.size()>0){
        				model.put("containerHexcolors", containerHexcolors);
        			}
        		}
        	}

        	// find totals page created & maximum number of pages can create
//        	int pagesAlreadyHave = siteDesignService.countAllPagesForOrg(org.getId(), PageDetail.Type.Page);
        	
//			long pagesCanHave = applicationConfig.getFreemiumPagesCanHave();
//			OrgPaymentProfile orgPaymentProfile = accountService.getCurrentOrgPaymentProfile();
//			OrgPaymentProfile orgPaymentProfile = accountService.getOrgPaymentProfile(org.getId());
//			if(orgPaymentProfile!=null && orgPaymentProfile.getPagesCanHave()!=null){
//				if(orgPaymentProfile.getPagesCanHave().longValue()>pagesCanHave){
//					pagesCanHave = orgPaymentProfile.getPagesCanHave().longValue();
//				}
//			}
//			
//			model.put("pagesAlreadyHave", pagesAlreadyHave);
//			model.put("pagesCanHave", pagesCanHave);
        	
    	}

    	return "generalPageManage";
    	
    }

    @RequestMapping(value="/pageNodeCreate")
    public @ResponseBody ApiResponse pageNodeCreate(
    		@RequestParam(value = "newNodetype", required = true) String newNodetype, // all types are defined in PageDetail.Type
    		@RequestParam(value = "parentNodeUuid", required = true) String parentNodeUuid,
    		@RequestParam(value = "nodeName", required = true) String newNodeName,
    		@RequestParam(value = "pageTreeLevelViewType", required = true) String pageTreeLevelViewType, // all types are defined in PageTreeLevelView.Type
    		ModelMap model){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
		PageDetail parentPageDetail = siteDesignService.getPageDetailByUuid(parentNodeUuid.trim());
    	
    	if(StringUtils.isNotBlank(newNodetype) && parentPageDetail!=null && StringUtils.isNotBlank(newNodeName) && StringUtils.isNotBlank(pageTreeLevelViewType)){
    		
    		// name validation
    		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(newNodeName);
    		if(nameVali){
        		
        		PageDetail.Type nodetype = PageDetail.Type.fromCode(newNodetype);
        		PageTreeLevelView.Type viewType = PageTreeLevelView.Type.fromCode(pageTreeLevelViewType);
        		if(nodetype!=null && viewType!=null){
        			
        			// check orgMeta.pagesCanHave
//        			long pagesCanHave = applicationConfig.getFreemiumPagesCanHave();
//        			OrgPaymentProfile orgPaymentProfile = accountService.getCurrentOrgPaymentProfile();
//        			OrgPaymentProfile orgPaymentProfile = accountService.getOrgPaymentProfile(parentPageDetail.getOrganization_id());
//        			if(orgPaymentProfile!=null && orgPaymentProfile.getPagesCanHave()!=null){
//        				if(orgPaymentProfile.getPagesCanHave().longValue()>pagesCanHave){
//        					pagesCanHave = orgPaymentProfile.getPagesCanHave().longValue();
//        				}
//        			}
//        			int pagesAlreadyHave = siteDesignService.countAllPagesForOrg(parentPageDetail.getOrganization_id(), PageDetail.Type.Page);
//        			if(pagesAlreadyHave<pagesCanHave){
//        				
//        			}else{
//        				StringBuilder errMsg = new StringBuilder("The total pages your organization can have is ").append(pagesCanHave)
//        						.append(", you already have ").append(pagesAlreadyHave).append(" pages created.");
//        				res.setResponse1(errMsg.toString());
//        			}
        			
            		String newNodeUuid = siteDesignService.newPageNode(nodetype, parentNodeUuid, newNodeName, viewType);
            		if(StringUtils.isNotBlank(newNodeUuid)){
            			res.setSuccess(true);
            			res.setResponse1(newNodeUuid);
            			
            			// get root container for the new page
            			ContainerDetail rootContainer = null;
            			List<ContainerDetail> containers = siteDesignService.findPageContainers(newNodeUuid);
            			if(containers!=null && containers.size()>0){
            				for(ContainerDetail c : containers){
            					if(StringUtils.isBlank(c.getParentuuid())){
            						rootContainer = c;
            						break;
            					}
            				}
            			}
            			
            			if(rootContainer!=null){
            				res.setResponse2(rootContainer);
            			}
            			
            		}else{
        				res.setResponse1("No node is created, please try again!");
            		}
        			
        		}else{
    				res.setResponse1("Not find the node type, please refresh the page, and try again!");
        		}
    			
    		}else{
    			res.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    	}else{
			res.setResponse1("Missing node creation info, please refresh the page, and try again!");
    	}
    	
    	return res;
    }
	
    
	@RequestMapping(value="/pageTreeNodeDetail", method=RequestMethod.GET)
	public ModelAndView pageTreeNodeDetail(
			
			@RequestParam(value = "nodeUuid", required = false) String pageUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("pageTreeNodeDetail");
		AccountDto loginaccount = accountService.getCurrentAccount();
		
		PageDetail pageDetail = siteDesignService.getPageDetailByUuid(pageUuid);
		
		if(pageDetail!=null){
			
			// get page modify permission
			boolean pageModifyPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, pageDetail.getPageuuid());
			mv.addObject("pageModifyPermission", pageModifyPermission);
			
			mv.addObject("pageuuid", pageUuid);
			mv.addObject("pageUrl", pageDetail.getUrl());
			//AccountDto currentAccount = accountService.getCurrentAccount();
			
			// get page meta 
			PageMeta pageMeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
			if(pageMeta!=null && StringUtils.isNotBlank(pageMeta.getChangelist())){
				XStream stream = new XStream(new DomDriver());
				Map<Long, String> changelist = (Map<Long, String>)stream.fromXML(pageMeta.getChangelist());
				if(changelist!=null && changelist.size()>0){
					mv.addObject("changeList", changelist);
				}
			}
			
			//mv.addObject("pageCss", pageMeta.getCss());
			
			// put pagebuild design area sizes into mv
			mv.addObject("pageDesignArea", applicationConfig.getPageDesignArea());
			
			// get ContainerForPageBuilder with all subcontainers
			mv.addObject("pageContainer", treeService.getContaienrTreeRootWithSubNodes(pageUuid));
			
			// get all scheduled containerIds for the page
			mv.addObject("isContainerScheduled", siteDesignService.isPageContainersScheduledMap(pageUuid));
			
		}
		
		return mv;

	}

    @RequestMapping(value="/newPageBuilderElement", method=RequestMethod.GET)
    public @ResponseBody ApiResponse newPageBuilderElement(
    		@RequestParam(value = "type", required = true) String type,
    		@RequestParam(value = "parentId", required = true) String parentId,
    		@RequestParam(value = "x", required = true) Integer x,
    		@RequestParam(value = "y", required = true) Integer y,
    		ModelMap model){
    	ApiResponse res = new ApiResponse();
    	
    	if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(parentId) && x!=null && y!=null){
    		// get the parent container
    		parentId = parentId.substring(3);// remove first 3 characters "ct_", which is added in containerDetail.jsp to avoid id duplication with container tree
    		ContainerDetail parentContainer = siteDesignService.getContainerByUuid(parentId);
    		
    		if(parentContainer!=null){
    			AccountDto currentAccount = accountService.getCurrentAccount();
    			
    			Date now = new Date();
    			
    			// create a new containerDetail
    			String newContainerUuid = UUID.randomUUID().toString();
    			String newContainerPrettyname = new StringBuilder("c_").append((int)(Math.random()*888)).toString(); // random prettyname
    			String newContainerPath = StringUtils.isNotBlank(parentContainer.getPath())?new StringBuilder(parentContainer.getPath()).append("/").append(parentContainer.getContaineruuid()).toString():parentContainer.getContaineruuid();
    			int newTopPosition = -1;
    			int newLeftPosition = -1;
    			int newWidth = -1;
    			int newHeight = -1;
    			if(parentContainer.getDirection().trim().equals("1")){
    				newTopPosition = y-parentContainer.getTopposition();
    				newLeftPosition = 0;
    				newWidth = parentContainer.getWidth();
    				newHeight = applicationConfig.getContainerDefaultSize().get("height");
    			}else if(parentContainer.getDirection().trim().equals("0")){
    				newTopPosition = 0;
    				newLeftPosition = x-parentContainer.getLeftposition();
    				newWidth = applicationConfig.getContainerDefaultSize().get("width");
    				newHeight = parentContainer.getHeight();
    			}
    			String hexColor = WebUtil.hexColorRandomGenerator();
    			ContainerDetail newContainer = new ContainerDetail(null, 
    					newContainerUuid, 
    					newContainerPrettyname, 
    					parentContainer.getPageuuid(), 
    					parentId.trim(), 
    					newContainerPath, 
    					parentContainer.getDirection().trim().equals("1")?"0":"1", 
    					newTopPosition, newLeftPosition, newWidth, newHeight, hexColor, now, parentContainer.getOrganization_id(), currentAccount.getId(), ContainerDetail.BOOLEAN.enable.getCode(), ContainerDetail.BOOLEAN.enable.getCode());
    			

    			Long newContainerId = siteDesignService.newContainerDetail(newContainer);
    			if(newContainerId!=null){
    				res.setSuccess(true);
    				res.setResponse1(newContainer);
    				res.setResponse2(newContainer.getPageuuid());
    			}else{
    				res.setSuccess(false);
    				res.setResponse1(messageFromPropertiesService.getMessageSource().getMessage("container.create.nospace", null, Locale.US));
    			}
    		}else{
        		res.setSuccess(false);
        		res.setResponse1("System can't find parent container.");
    		}
    	}else{
    		res.setSuccess(false);
    		res.setResponse1("Missing type, parentId, x, y information.");
    	}
    	
    	return res;
    }
    
    
    
    @RequestMapping(value="/resizeContaier", method=RequestMethod.GET)
    public @ResponseBody ApiResponse resizeContaier(
    		@RequestParam(value = "containerId", required = true) String containerId,
    		@RequestParam(value = "leftPosition", required = true) Integer leftPosition,
    		@RequestParam(value = "topPosition", required = true) Integer topPosition,
    		@RequestParam(value = "width", required = true) Integer width,
    		@RequestParam(value = "height", required = true) Integer height,
    		ModelMap model){

    	ApiResponse res = new ApiResponse();
    	
    	if(StringUtils.isNotBlank(containerId)){
    		// remove "ct_" from container id
    		containerId = containerId.substring(3);
    		res = siteDesignService.resizeContainer(containerId, leftPosition, topPosition, width, height);
    	}
    	
    	return res;
    }
    
    
    @RequestMapping(value="/modules", method=RequestMethod.GET)
    public String modules(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	model.put("currentPageId", new StringBuilder("modules_").append(orguuid).toString());
    	model.put("orgUuid", orguuid);
    	
    	// get payment info, created modules info
    	Organization org = accountService.getOrgByUuid(orguuid);
    	if(org!=null){
    		model.put("orgName", org.getOrgname());
    		
    		// get paymentPlan
    		PaymentPlan paymentPlan = paymentService.getPaymentPlanAtPointOfDate(org.getId(), new Date());
    		// get org can have ...
    		if(paymentPlan!=null){
    			model.put("maxiModuleAndInstanceCanHave", paymentPlan.getMaxinstance());
    		}
    		
    		// get org alread have ...
    		int totalModuleDetailsAlreadyHave = siteDesignService.countAllModuleDetailInOrg(org.getId());
    		int totalModuleInstancesAlreadyHave = siteDesignService.countAllModuleInstanceInOrg(org.getId());
    		int totalProductsAlreadyHave = siteDesignService.countAllProductInOrgInOrg(org.getId());
    		model.put("totalModuleAndInstanceHave", totalModuleDetailsAlreadyHave+totalModuleInstancesAlreadyHave+totalProductsAlreadyHave);
    		StringBuilder detailForTotalModuleAndInstanceHave = new StringBuilder();
    		detailForTotalModuleAndInstanceHave.append("ModuleDetails created: ").append(totalModuleDetailsAlreadyHave).append("<br/>");
    		detailForTotalModuleAndInstanceHave.append("ModuleInstances created: ").append(totalModuleInstancesAlreadyHave).append("<br/>");
    		detailForTotalModuleAndInstanceHave.append("Products created: ").append(totalProductsAlreadyHave).append("<br/>");
    		if(paymentPlan.getMaxinstance().longValue()<(totalModuleDetailsAlreadyHave+totalModuleInstancesAlreadyHave+totalProductsAlreadyHave)){
    			detailForTotalModuleAndInstanceHave.append("<span>Your organization has more modules and products than you can have, which can cause some moduledetail or instance or product to display incorrectly.</span>");
    		}
    		model.put("detailForTotalModuleAndInstanceHave", detailForTotalModuleAndInstanceHave.toString());
    		
    	}
    	
        return "modules";
    }

    @RequestMapping(value="/moduleNodeCreate", method=RequestMethod.GET)
    public @ResponseBody ApiResponse moduleNodeCreate(
    		@RequestParam(value = "newNodetype", required = true) String newNodetype, // all types are defined in PageDetail.Type
    		@RequestParam(value = "parentNodeUuid", required = true) String parentNodeUuid,
    		@RequestParam(value = "nodeName", required = true) String newNodeName,
    		@RequestParam(value = "cloneFrom", required = false) String cloneFromUuid,
    		ModelMap model){
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(newNodetype) && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(newNodeName)){
    		
    		// validate the node's name
    		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(newNodeName);
    		if(nameVali){
        		ModuleDetail.Type nodetype = ModuleDetail.Type.fromCode(newNodetype);
        		if(nodetype!=null){
        			
        			
        			String newNodeUuid = null;
        			
        			if(StringUtils.isNotBlank(cloneFromUuid)){
        				
        				// permission check is inside siteDesignService.cloneModuleNode already! 
        				res = siteDesignService.cloneModuleNode(nodetype, parentNodeUuid, newNodeName, cloneFromUuid);
        				if(res.isSuccess()){
            				newNodeUuid = (String)res.getResponse1();
        				}
        			}else{
        				
        				AccountDto loginAccount = accountService.getCurrentAccount();
        				boolean isPermissionModifyAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, parentNodeUuid);
        				if(isPermissionModifyAllowed){
            				newNodeUuid = siteDesignService.newModuleNode(nodetype, parentNodeUuid, newNodeName);
        				}else{
        					res.setResponse1("User "+loginAccount.getFirstname()+" doesn't have create permission for node: "+parentNodeUuid);
        				}
        			}
        			
        			if(StringUtils.isNotBlank(newNodeUuid)){
        				res.setSuccess(true);
        				res.setResponse1(newNodeUuid);
        			}else{
        				StringBuilder errormsg = new StringBuilder("No node is created, this could because: \n").append(res.getResponse1()).append("\nPlease refresh the page and try again!");
        				res.setResponse1(errormsg.toString());
        			}
        		}else{
    				res.setResponse1("Not find the node type, please refresh the page, and try again!");
        		}
    		}else{
    			res.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    		
    		
    	}else{
			res.setResponse1("Missing node creation info, please refresh the page, and try again!");
    	}
    	
    	return res;
    }
    
    @RequestMapping(value="/moduleConfig", method=RequestMethod.POST)
    public String moduleConfig(
			@RequestParam(value = "orgId", required = false) String orgUuid, 
			@RequestParam(value = "moduleId", required = false) String moduleUuid, 
			@RequestParam(value = "desc", required = false) String desc, 
			@RequestParam(value = "xml", required = false) String xml, 
			@RequestParam(value = "jsp", required = false) String jsp, 
			@RequestParam(value = "css", required = false) String css,
            ModelMap model) {
    	
    	if(StringUtils.isNotBlank(orgUuid)){
    		if(StringUtils.isNotBlank(moduleUuid)){
    			// update moduleDetail by all info
    			Long moduleId = siteDesignService.updateModuleDetailForXmlJspDesc(moduleUuid, desc, xml, jsp, css);
    			
    			if(moduleId!=null){
        			siteDesignService.writeModuleJspToFile(moduleId);
        			siteDesignService.writeModuleCssToFile(moduleId);
    			}
    			
    			return "redirect:/modules?org="+orgUuid+"&moduleId="+moduleUuid;
    			
    		}else{
    			return "redirect:/modules?org="+orgUuid;
    		}
    	}
    	
    	return "redirect:/";
    }
    
    @RequestMapping(value="/getModuleConfigFrag", method=RequestMethod.POST)
    public ModelAndView getModuleConfigFrag(
			@RequestParam(value = "moduleId", required = false) String moduleUuid) {
		ModelAndView mv = null;
		
		AccountDto loginAccount = accountService.getCurrentAccount();

		// get moduledetail
		if(StringUtils.isNotBlank(moduleUuid)){
			ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
			Organization org = accountService.getOrgById(detail.getOrganization_id());
			if(detail!=null && org!=null){
				
				// check read permission
				Permission mergedPermission = permissionService.getMergedPermissionForAccount(loginAccount.getId(), true);
				// check the read permission for detail for loginAccount
				boolean readPermissionAllow = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.read, detail.getModuleuuid());
				
				if(readPermissionAllow){
					
					// hold all targets' uuids
					Set<String> metaTargetUuids = new HashSet<String>();
					// hold all targetUuid with target's MetaData
					Map<String, MetaData> targetWithMetaDataMap = new HashMap<String, MetaData>();
					
					metaTargetUuids.add(detail.getModuleuuid());
					
					mv = new ModelAndView("moduleConfigFragment");
					mv.addObject("moduleDetail", detail);
					mv.addObject("org", org);
					
					boolean modifyPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.modify, detail.getModuleuuid());
					mv.addObject("modifyPermissionAllowed", modifyPermissionAllowed);
					
					mv.addObject("isDisplayOnly", false);
					
					// add module attributes list into the view
					List<ModuleAttributeList> moduleAttributeList = new ArrayList<ModuleAttributeList>();
					for(ModuleAttributeList al : ModuleAttributeList.values()){
						if(al.getUsedFor().equals(ModuleDetail.Type.all) || al.getUsedFor().getCode().equals(detail.getType())){
							moduleAttributeList.add(al);
						}
					}
					mv.addObject("moduleAttrList", moduleAttributeList);
					
					// add module with all the attgroups
					if(StringUtils.isNotBlank(detail.getXml())){
						Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
						mv.addObject("module", module);
						
						boolean hasModuleLinkAttr = false;
						boolean hasModuleProductListAttr = false;
						boolean hasModuleMoneyAttr = false;
						boolean hasModuleCatListAttr = false;
						if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
							for(AttrGroup g : module.getAttrGroupList()){
								metaTargetUuids.add(g.getGroupUuid());
								if(g.getAttrList()!=null && g.getAttrList().size()>0){
									for(ModuleAttribute a : g.getAttrList()){
										if(!hasModuleLinkAttr && a.getClass().equals(ModuleLinkAttribute.class)){
											hasModuleLinkAttr = true;
										}
										if(!hasModuleProductListAttr && a.getClass().equals(ModuleProductListAttribute.class)){
											hasModuleProductListAttr = true;
										}
										if(!hasModuleMoneyAttr && a.getClass().equals(ModuleMoneyAttribute.class)){
											hasModuleMoneyAttr = true;
										}
										if(!hasModuleCatListAttr && a.getClass().equals(ModuleEntityCategoryListAttribute.class)){
											hasModuleCatListAttr = true;
										}
										metaTargetUuids.add(a.getUuid());
									}
								}
							}
						}
						
						// ***** add some special param for attribute:
						if(hasModuleLinkAttr){
							mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
							mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
						}
						// for page list
						if(hasModuleProductListAttr){
							List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
							List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
							List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
							List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
							if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
								for(PageDetail d : orgDeskPageDetails){
									orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
								}
							}
							if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
								for(PageDetail d : orgMobilePageDetails){
									orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
								}
							}
							mv.addObject("orgDeskPages", orgDeskPages);
							mv.addObject("orgMobilePages", orgMobilePages);
						}
						if(hasModuleMoneyAttr){
							mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
						}
						if(hasModuleCatListAttr){
							mv.addObject("entityCatTypeList", ModuleEntityCategoryListAttribute.Type.values());
							mv.addObject("entityCatSoryBy", ModuleEntityCategoryListAttribute.SortType.values());
						}
					}
					
					// find all metaData if metaTargetUuids not empty
					if(metaTargetUuids.size()>0){
						for(String targetUuid : metaTargetUuids){
							ModuleMeta moduleMeta = siteDesignService.getModuleMetaByTargetUuid(targetUuid);
							if(moduleMeta!=null){
								MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
								if(metaData!=null){
									targetWithMetaDataMap.put(targetUuid, metaData);
								}
							}
						}
						
						if(targetWithMetaDataMap.size()>0){
							mv.addObject("targetWithMetaDataMap", targetWithMetaDataMap);
						}
					}
					
					// find module space usage:
					int usedSpaceWith100Multipled = paymentService.countModuleDetailUsage(detail);
					mv.addObject("usedSpaceWith100Multipled", usedSpaceWith100Multipled);
					
					// find module's jsp usage: 
					int usedJspSpaceWith100Multipled = paymentService.countModuleDetailJspUsage(detail);
					mv.addObject("usedJspSpaceWith100Multipled", usedJspSpaceWith100Multipled);;
					
					// find module's css usage: 
					int usedCssSpaceWith100Multipled = paymentService.countModuleDetailCssUsage(detail);
					mv.addObject("usedCssSpaceWith100Multipled", usedCssSpaceWith100Multipled);;

				}else{
					mv = new ModelAndView("error_general");
					List<String> errorList = new ArrayList<String>();
					errorList.add("User "+loginAccount.getFirstname()+" doesn't have read permission for target: "+detail.getModuleuuid());
					mv.addObject("errorList", errorList);
				}
				
			}else{
				mv = new ModelAndView("error_general");
			}
			
		}else{ // return error
			mv = new ModelAndView("error_general");
		}
		
		return mv;
    }
    
    @RequestMapping(value="/getModuleForContainerSchedule", method=RequestMethod.GET)
    public ModelAndView getModuleForContainerSchedule(
			@RequestParam(value = "moduleid", required = false) String moduleUuid) {
		ModelAndView mv = null;

		// get moduledetail
		if(StringUtils.isNotBlank(moduleUuid)){
			ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
			Organization org = accountService.getOrgById(detail.getOrganization_id());
			if(detail!=null && org!=null){
				mv = new ModelAndView("moduleDisplayFragment");
				mv.addObject("moduleDetail", detail);
				mv.addObject("org", org);
				
				// add module with all the attgroups
				if(StringUtils.isNotBlank(detail.getXml())){
					Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
					mv.addObject("module", module);
				}
				
			}else{
				mv = new ModelAndView("error_general");
			}
			
		}else{ // return error
			mv = new ModelAndView("error_general");
		}
		
		return mv;
    }
    
    
    @RequestMapping(value="/moduleInstancePreview", method=RequestMethod.GET)
    public ModelAndView moduleInstancePreviewForProduct(
    		@RequestParam(value = "previewType", required = false) String previewType,
			@RequestParam(value = "moduleId", required = false) String moduleUuid,
			@RequestParam(value = "moduleInstanceUuid", required = false) String moduleInstanceUuid
    ) {
		ModelAndView mv = null;
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(previewType)){ // for display module instance sample only!!! get moduleInstance from moduleUuid, this will generate a new instance for module
			
			if(previewType.equals(ModuleDetail.Type.productModule.name())){
				EntityDetail entityDetail = productService.getEntityDetailByUuid(moduleInstanceUuid);
				
				if(entityDetail!=null){
					
					ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
					if(detail!=null){
						
						mv = new ModelAndView("moduleInstancePreview");
						mv.addObject("previewType", previewType);

						mv.addObject("moduledetailUuid", detail.getModuleuuid());
						// xml to obj
						Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
						// update module to instance
						Module instance = SitedesignHelper.getDefaultModuleInstance(module);
						
						SitedesignHelper.updateModuleInstanceByModule(module, instance);
						
						mv.addObject("instance", instance);
						
						// send back more info for module preview in json structure
						JSONObject domvalue = new JSONObject();
						domvalue.put("moduleDetailUuid", detail.getModuleuuid());
						if(StringUtils.isBlank(entityDetail.getModuleuuid())){
							domvalue.put("warning", "no");
						}else{
							domvalue.put("warning", "yes");
							domvalue.put("warningInfo", "The category or product is already selected one module as template.Do you like to replace the old templage with your new selection?\nNote: the replacement will delete all views for the category or product!");
						}
						
						mv.addObject("domvalue", domvalue.toJSONString());
						

					}else{
						mv = new ModelAndView("error_general");
					}
				}
			}else if(previewType.equals(ModuleDetail.Type.module.name())){
				ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
				ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(moduleInstanceUuid);
				if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml()) && moduleInstance!=null 
						&& StringUtils.isNotBlank(moduleInstance.getInstance())){
					mv = new ModelAndView("moduleInstancePreview");
					
					mv.addObject("previewType", previewType);
					mv.addObject("moduleInstance", moduleInstance);

					// for instance
					Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
					Module instance = SitedesignHelper.getModuleFromXml(moduleInstance.getInstance());
					// update instance by module
					SitedesignHelper.updateModuleInstanceByModule(module, instance);
					mv.addObject("instance", instance);
					
				}else{
					mv = new ModelAndView("error_general");
				}
				
				
			}
			
		}
		return mv;
    }
    
    @RequestMapping(value="/moduleInstance", method=RequestMethod.GET)
    public ModelAndView moduleInstance(
//			@RequestParam(value = "moduleId", required = false) String moduleUuid,
			@RequestParam(value = "moduleInstanceId", required = false) String moduleInstanceUuid
    ) {
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
		ModelAndView mv = null;

		if(StringUtils.isNotBlank(moduleInstanceUuid)){ // get moduleinstance
			ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(moduleInstanceUuid);
			if(loginAccount!=null && moduleInstance!=null){
				
				// hold all targets' uuids
				Set<String> metaTargetUuids = new HashSet<String>();
				// hold all targetUuid with target's MetaData
				Map<String, MetaData> targetWithMetaDataMap = new HashMap<String, MetaData>();
				
				metaTargetUuids.add(moduleInstanceUuid);
				
				ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleInstance.getModuleuuid());
				if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
					
					metaTargetUuids.add(moduleDetail.getModuleuuid());
					
					Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
					
					if(module!=null){
						
						
						boolean hasModuleLinkAttr = false;
						boolean hasModuleProductListAttr = false;
						boolean hasModuleMoneyAttr = false;
						boolean hasModuleCatListAttr = false;
						
						// add module's group and attr's uuid into the list
						if(module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
							for(AttrGroup g : module.getAttrGroupList()){
								metaTargetUuids.add(g.getGroupUuid());
								if(g.getAttrList()!=null && g.getAttrList().size()>0){
									for(ModuleAttribute a : g.getAttrList()){
										
										if(!hasModuleLinkAttr && a.getClass().equals(ModuleLinkAttribute.class)){
											hasModuleLinkAttr = true;
										}
										if(!hasModuleProductListAttr && a.getClass().equals(ModuleProductListAttribute.class)){
											hasModuleProductListAttr = true;
										}
										if(!hasModuleMoneyAttr && a.getClass().equals(ModuleMoneyAttribute.class)){
											hasModuleMoneyAttr = true;
										}
										if(!hasModuleCatListAttr && a.getClass().equals(ModuleEntityCategoryListAttribute.class)){
											hasModuleCatListAttr = true;
										}
										metaTargetUuids.add(a.getUuid());
									}
								}
							}
						}
						
						Organization org = accountService.getOrgById(moduleInstance.getOrgid());
						
						mv = new ModelAndView("moduleInstance");
						mv.addObject("moduleDetail", moduleDetail);
						mv.addObject("orgUuid", org.getOrguuid());
//						mv.addObject("moduleUuid", moduleInstance.getModuleuuid());
						mv.addObject("moduleInstance", moduleInstance);
//						mv.addObject("instanceName", moduleInstance.getName());
//						mv.addObject("readyToUse", moduleInstance.getVisibility());
						
						boolean modifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, moduleInstance.getModuleuuid());
						mv.addObject("isPreview", !modifyPermission);
						
						
						// xml to obj
						Module instance = SitedesignHelper.getModuleFromXml(moduleInstance.getInstance());
						// update instance by module
						SitedesignHelper.updateModuleInstanceByModule_v2(module, instance);
						mv.addObject("instance", instance);
						
						
						// ***** add some special param for attribute:
						if(hasModuleLinkAttr){
							mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
							mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
						}
						// for page list
						if(hasModuleProductListAttr){
							List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
							List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
							List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
							List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
							if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
								for(PageDetail d : orgDeskPageDetails){
									orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
								}
							}
							if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
								for(PageDetail d : orgMobilePageDetails){
									orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
								}
							}
							mv.addObject("orgDeskPages", orgDeskPages);
							mv.addObject("orgMobilePages", orgMobilePages);
						}
						if(hasModuleMoneyAttr){
							mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
						}
						if(hasModuleCatListAttr){
							mv.addObject("entityCatTypeList", ModuleEntityCategoryListAttribute.Type.values());
							mv.addObject("entityCatSoryBy", ModuleEntityCategoryListAttribute.SortType.values());
							
						}
						
						// attributeUuidWithValidationInfos: map to hold attribute's uuid with list of validation informations, ie, required, maxlength, ...
						Map<String, List<String>> attributeUuidWithValidationInfos = new HashMap<String, List<String>>();
						if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
							for(AttrGroup g : instance.getAttrGroupList()){
								metaTargetUuids.add(g.getGroupUuid());
								if(g.getAttrList()!=null && g.getAttrList().size()>0){
									for(ModuleAttribute a : g.getAttrList()){
										metaTargetUuids.add(a.getUuid());
										
										// *** add attribute's validation's info here
										List<String> validationInfos = SitedesignHelper.moduleAttributeValidation(a);
										if(validationInfos!=null && validationInfos.size()>0){
											attributeUuidWithValidationInfos.put(a.getUuid(), validationInfos);
										}
									}
								}
							}
						}
						
						mv.addObject("attributeUuidWithValidationInfos", attributeUuidWithValidationInfos);
					}
					
					
				}
				
				// find all metaData if metaTargetUuids not empty
				if(metaTargetUuids.size()>0){
					for(String targetUuid : metaTargetUuids){
						ModuleMeta moduleMeta = siteDesignService.getModuleMetaByTargetUuid(targetUuid);
						if(moduleMeta!=null){
							MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
							if(metaData!=null){
								targetWithMetaDataMap.put(targetUuid, metaData);
							}
						}
					}
					
					if(targetWithMetaDataMap.size()>0){
						mv.addObject("targetWithMetaDataMap", targetWithMetaDataMap);
					}
				}
				
				// find moduleInstance usage info
				int moduleInstanceUsageWith100Mutipled = paymentService.countModuleInstanceUsage(moduleInstance);
				mv.addObject("moduleInstanceUsageWith100Mutipled", moduleInstanceUsageWith100Mutipled);
				
			}
			
		}else{ // return error
			mv = new ModelAndView("error_general");
		}
		
		return mv;
    }
    
    @RequestMapping(value="/newModuleInstance", method=RequestMethod.GET)
    public @ResponseBody ApiResponse newModuleInstance(
    		@RequestParam(value = "newNodetype", required = true) String newNodetype, // all types are defined in ModuleInstance.Type
    		@RequestParam(value = "parentNodeUuid", required = true) String moduleUuid,
    		@RequestParam(value = "nodeName", required = true) String newNodeName,
    		HttpServletResponse response, HttpServletRequest request
    		) {
    	
    	ApiResponse res = new ApiResponse();
    	res.setSuccess(false);
    	
    	AccountDto currentAccount = accountService.getCurrentAccount();
    	
    	ModuleDetail.Type nodeType = ModuleDetail.Type.fromCode(newNodetype);
    	if(currentAccount!=null && nodeType!=null && nodeType.equals(ModuleDetail.Type.instance) && StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(newNodeName)){
    		
    		// name validation
    		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(newNodeName);
    		if(nameVali){
        		ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
        		if(moduledetail!=null && moduledetail.getType().equals(ModuleDetail.Type.module.getCode())){
        			
        			// permission check
        			boolean isCreatePermissionAllowed = permissionService.isPermissionAllowed(currentAccount.getId(), Permission.Type.modify, moduleUuid);
        			if(isCreatePermissionAllowed){
            			
            			if(StringUtils.isNotBlank(moduledetail.getXml())){
                			Date now = new Date();
                			String moduleinstanceuuid = UUID.randomUUID().toString();
                			
                			ModuleInstance instance = new ModuleInstance(null, 
                					moduleinstanceuuid, 
                					newNodeName.trim(), 
                					moduledetail.getModuleuuid(), 
                					nodeType.getCode(), 
                					null, 
                					moduledetail.getOrganization_id(), 
                					ModuleInstance.Visibility.hide.getCode(), 
                					ModuleInstance.IsDefault.no.getCode(), 
                					now, 
                					currentAccount.getId());
                			
                			// create default modulexml from module
                			Module module = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
                			Module defaultModuleInstance = SitedesignHelper.getDefaultModuleInstance(module);
                			if(defaultModuleInstance!=null){
                    			String defaultModuleXml = SitedesignHelper.getXmlFromModule(defaultModuleInstance);
                    			instance.setInstance(defaultModuleXml);
                			}
                			
                			// inherit moduleMeta from moduledetail
                			// note: don't need to inherit moduleMeta from moduledetail, since instance's detail has all reference from moduleDetail.
                			
                			Long id = siteDesignService.newModuleInstance(instance);
                			if(id!=null){
                				res.setSuccess(true);
                				res.setResponse1(instance.getModuleinstanceuuid());
                			}
            				
            			}else{
            				res.setResponse1("You haven't designed module yet!");
            			}
        				
        			}else{
        				res.setResponse1("User "+currentAccount.getFirstname()+" doesn't have permission to create a instance for node : "+moduledetail.getPrettyname());
        			}
        			
        		}else{
        			res.setResponse1("System can't get Module by moduleUuid: "+moduleUuid+" or module type isn't module.");
        		}
    			
    		}else{
    			res.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    		
    	}else{
    		//currentAccount!=null && nodeType!=null && nodeType.equals(ModuleDetail.Type.instance) && StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(newNodeName)
    		res.setResponse1("System can't create a new instance, please try to refresh the page and do it again!");
    	}
    	return res;
    	
    }
    
    
    @Deprecated
    @RequestMapping(value="/moduleInstance", method=RequestMethod.POST)
    public String moduleInstance_post(
			@RequestParam(value = "orgId", required = false) String orgUuid, 
			@RequestParam(value = "moduleId", required = false) String moduleUuid, 
			@RequestParam(value = "instanceId", required = false) String instanceUuid,
			@RequestParam(value = "visibility", required = false) String visibility,
			@RequestParam(value = "instancetype", required = false) String instancetype,
			@RequestParam(value = "instancename", required = false) String instancename,
    		HttpServletResponse response, HttpServletRequest request
    		) {
    	
    	if(StringUtils.isNotBlank(orgUuid) && StringUtils.isNotBlank(instancetype) && StringUtils.isNotBlank(instancename)){
    		
    		if(StringUtils.isBlank(instanceUuid) && StringUtils.isNotBlank(moduleUuid)){ // new instance
        		ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
        		AccountDto currentAccount = accountService.getCurrentAccount();
        		Organization org = accountService.getOrgByUuid(orgUuid);
        		if(org!=null && currentAccount!=null && detail!=null){
        			Date now = new Date();
        			Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
        			if(module!=null){
        				Module mis = SitedesignHelper.createInstanceFromHttprequest(request, module);
        				
        				// save instance
        				if(mis!=null){
        					ModuleDetail.Type moduletype = ModuleDetail.Type.fromCode(instancetype);
        					if(instancetype!=null){
        						
        						String moduleinstanceuuid = UUID.randomUUID().toString();
        						ModuleInstance instance = new ModuleInstance(null, 
        								moduleinstanceuuid, 
        								instancename.trim(), 
        								detail.getModuleuuid(), 
        								moduletype.getCode(), 
        								SitedesignHelper.getXmlFromModule(mis), 
        								org.getId(), 
        								visibility.trim(), 
        								ModuleInstance.IsDefault.no.getCode(),
        								now, 
        								currentAccount.getId());
        						Long instanceId = siteDesignService.newModuleInstance(instance);
        					}
        					
        				}
        				
        			}
        		}
        		
            	return "redirect:/modules?org="+orgUuid+"&moduleId="+moduleUuid;
    			
    		}else if(StringUtils.isNotBlank(instanceUuid)){ // update instance
    			// get instance
    			ModuleInstance instance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
    			if(instance!=null){
    				// get moduledetail
    				ModuleDetail detail = siteDesignService.getModuleDetailByUuid(instance.getModuleuuid());
    				if(detail!=null){
    					Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
    					if(module!=null){
    						Module mis = SitedesignHelper.createInstanceFromHttprequest(request, module);
    						
    						// update instance
    						if(mis!=null){
    							instance.setInstance(SitedesignHelper.getXmlFromModule(mis));
    							instance.setName(instancename.trim());
    							instance.setVisibility(visibility.trim());
    							Long instanceId = siteDesignService.saveModuleInstance(instance);
    						}
    					}
    				}
    			}
    			
    			return "redirect:/modules?org="+orgUuid+"&moduleId="+moduleUuid+"&instanceId"+instanceUuid;
    			
    		}
    		
    	}
    	
    	return "redirect:/";
    	
    }

        
    @RequestMapping(value="/dupliModuleElemt", method=RequestMethod.GET)
    public ModelAndView dupliModuleElemt(
			@RequestParam(value = "moduleId", required = false) String moduleUuid,
			@RequestParam(value = "elementPath", required = false) String elementPath,
			@RequestParam(value = "paramGroupIndex", required = false) String paramGroupIndex,
			@RequestParam(value = "totalModuleInstanceGroupInAjax", required = false) Integer totalModuleInstanceGroupInAjax
		) {
		ModelAndView mv = null;
		if(StringUtils.isNotBlank(moduleUuid) && StringUtils.isNotBlank(elementPath)){
			String[] path = elementPath.trim().split("_"); // groupname_paramname
			ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
			if(detail!=null){
				Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
				if(module!=null){
					if(path.length>1){ // param duplicate
						mv = new ModelAndView("moduleInstance_param");
						mv.addObject("isRemove", true);
						mv.addObject("groupIndex", paramGroupIndex);
						for(AttrGroup g : module.getAttrGroupList()){
							if(g.getGroupName().trim().equalsIgnoreCase(path[0].trim())){
								mv.addObject("groupName", g.getGroupName());
								for(ModuleAttribute p : g.getAttrList()){
									if(p.getName().trim().equalsIgnoreCase(path[1].trim())){
										mv.addObject("moduleParam", p);
										break;
									}
								}
							}
						}
						
					}else{ // group duplicate
						mv = new ModelAndView("moduleInstance_paramGroup");
						mv.addObject("groupIndex", totalModuleInstanceGroupInAjax);
//						mv.addObject("isRemove", true);
						for(AttrGroup g : module.getAttrGroupList()){
							if(g.getGroupName().trim().equalsIgnoreCase(path[0].trim())){
								mv.addObject("paramGroup", g);
								break;
							}
						}
						
					}
					
				}
			}
		}

		return mv;
    }
    
    @RequestMapping(value="/containerModuleSchedule", method=RequestMethod.GET)
    public ModelAndView containerModuleSchedule(
			@RequestParam(value = "containerId", required = false) String containerUuid
		) {
		ModelAndView mv = new ModelAndView("containerModuleSchedule");
		// get container container and container's schedules by containerUuid
		containerUuid = containerUuid.substring(containerUuid.indexOf("ct_")+3);
		ContainerDetail container = siteDesignService.getContainerByUuid(containerUuid);
		if(container!=null){
			mv.addObject("container", container);
			
			// get containerModuleSchedules
			List<ContainerModuleSchedule> schds = siteDesignService.findContainerModuleSchedulesByContainerUuid(containerUuid);
			if(schds!=null && schds.size()>0){
				mv.addObject("schedules", schds);

				// get moduleinstanceschedule exist or not information for each containerModuleSchedule.
				// moduleinstanceschedule must exist for each containerModuleSchedule!!!
				Map<String, Boolean> instanceExistInfoForContainerModuleMap = new HashMap<String, Boolean>();
				for(ContainerModuleSchedule cms : schds){
					List<ModuleInstanceSchedule> miSchds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(cms.getUuid());
					if(miSchds!=null && miSchds.size()>0){
						instanceExistInfoForContainerModuleMap.put(cms.getUuid(), Boolean.TRUE);
					}else{
						instanceExistInfoForContainerModuleMap.put(cms.getUuid(), Boolean.FALSE);
					}
				}
				mv.addObject("instanceExistInfoForContainerModuleMap", instanceExistInfoForContainerModuleMap);
			}
			
			
			// get modules
			List<ModuleDetail> moduleDetails = siteDesignService.findOrgModulesWithProModulesAggregate(container.getOrganization_id());
			if(moduleDetails!=null && moduleDetails.size()>0){
				List<GeneralSelectionType> modules = new ArrayList<GeneralSelectionType>();
//				boolean hasProductModule = false;
				for(ModuleDetail m : moduleDetails){
					modules.add(new GeneralSelectionType(m.getModuleuuid(), m.getPrettyname(), false));
				}
//				if(hasProductModule){
//					modules.add(new GeneralSelectionType(ModuleDetail.Type.productModule.name(), ModuleDetail.Type.productModule.getDesc(), false));
//				}
				// sort modules
				Collections.sort(modules);
				
				mv.addObject("modules", modules);
			}
			
			// get PriorityLevels
			mv.addObject("priorityLevels", ContainerModuleSchedule.PriorityLevel.values());
		}
		
		

		return mv;
    }

    @RequestMapping(value="/newContainerModuleSchedule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse newContainerModuleSchedule(
    		@RequestParam(value = "containerUuid", required = false) String containerUuid,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid,
    		@RequestParam(value = "startdate", required = false) String fromDate,
    		@RequestParam(value = "enddate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
    	Date now = new Date();
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ContainerDetail containerdetail = siteDesignService.getContainerByUuid(containerUuid);
    	ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	if(containerdetail!=null && moduleDetail!=null && StringUtils.isNotBlank(scheduleName) && priority!=null){
    		
    		
    		// schedule name validation
    		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(scheduleName);
    		if(nameVali ){
        		
    			ContainerModuleSchedule.PriorityLevel priorityLevel = ContainerModuleSchedule.PriorityLevel.fromCode(priority.intValue());
        		
//    			// translate string date to Date
    			String datePattern = "yyyy-MM-dd";
    			SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
    			Date startdate = null;
    			Date enddate = null;
    			try {
    				startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
    				enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
    			// if startdate after enddate, switch dates
    			if(startdate!=null && enddate!=null && startdate.after(enddate)){
    				Date tempDate = new Date(startdate.getTime());
    				startdate = enddate;
    				enddate = tempDate;
    			}
    			
    			if(priorityLevel!=null){
    				
    				String schUuid = UUID.randomUUID().toString();
    				ContainerModuleSchedule sch = new ContainerModuleSchedule(null, scheduleName,
    						schUuid, 
    						containerdetail.getContaineruuid(), 
    						moduleDetail.getModuleuuid(), 
    						startdate, 
    						enddate!=null?DateUtils.getEnd(enddate):null, 
    						now, 
    						priority);
    				
    				// save ContainerModuleSchedule
    				Long schId = siteDesignService.saveContainerModuleSchedule(sch);
    				if(schId!=null){
    					apires.setSuccess(true);
    					apires.setResponse1(sch);
    					
    					// set pageUuid and module type into response2
    					JSONObject resp2 = new JSONObject();
    					resp2.put("pageuuid", containerdetail.getPageuuid());
    					resp2.put("moduletype", moduleDetail.getType());
    					apires.setResponse2(resp2);
    				}
    				
    				
    			}else{
    				apires.setResponse1("No enough information to create a new ContainerModuleSchedule. (System can't generate the ContainerModuleSchedule.PriorityLevel from priority code: "+priority+")");
    			}
    			
    		}else{
    			apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    		
    		
    	}else{
    		apires.setResponse1("No enough information to create a new ContainerModuleSchedule. (containerUuid: "+containerUuid+", moduleUuid: "+moduleUuid+", scheduleName: "+scheduleName+", priority: "+priority+")");
    	}
    	
    	return apires;
    	
    }
    
    @RequestMapping(value="/editContainerModuleSchedule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse  editContainerModuleSchedule(
    		@RequestParam(value = "updatetype", required = false) String updatetype, // rename, general

    		@RequestParam(value = "schedUuid", required = false) String scheduleUuidd,
    		
//    		@RequestParam(value = "containerUuid", required = false) String containerUuid,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid,
    		@RequestParam(value = "startdate", required = false) String fromDate,
    		@RequestParam(value = "enddate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
    		
		) {
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ContainerModuleSchedule sched = siteDesignService.getContainerModuleScheduleByUuid(scheduleUuidd);
    	if(sched!=null){
    		if(StringUtils.isNotBlank(updatetype)){
    			if("rename".equals(updatetype)){
    				if(StringUtils.isNotBlank(scheduleName)){
    					
    					boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(scheduleName);
    					if(nameVali){
        					sched.setSchedulename(scheduleName.trim());
        					Long id = siteDesignService.saveContainerModuleSchedule(sched);
        					if(id!=null){
        						apires.setSuccess(true);
        						apires.setResponse1(sched);
        					}
    						
    					}else{
    		    			apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    					}
    					
    				}else{
    					apires.setResponse1("You need to provide new schedule name to rename the schedule.");
    				}
    			}else if("general".equals(updatetype)){
    				
    				ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    				ContainerModuleSchedule.PriorityLevel priorityLevel = ContainerModuleSchedule.PriorityLevel.fromCode(priority.intValue());

    				if(moduledetail!=null && priorityLevel!=null){
    					
    					// remove all moduleInstanceSchedules if old module is not the new module
    					if(!moduledetail.getModuleuuid().equals(sched.getModuleuuid())){
    						List<ModuleInstanceSchedule> moduleInstanceScheds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(sched.getUuid());
    						if(moduleInstanceScheds!=null && moduleInstanceScheds.size()>0){
    							for(ModuleInstanceSchedule s : moduleInstanceScheds){
    								siteDesignService.delModuleInstanceScheduleByUuid(s.getUuid());
    							}
    						}
    					}
    					
    					// translate string date to Date
    					String datePattern = "yyyy-MM-dd";
    					SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
    					Date startdate = null;
    					Date enddate = null;
    					try {
    						startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
    						enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
    					} catch (ParseException e) {
    						e.printStackTrace();
    					}
    					// if startdate after enddate, switch dates
    					if(startdate!=null && enddate!=null && startdate.after(enddate)){
    						Date tempDate = new Date(startdate.getTime());
    						startdate = enddate;
    						enddate = tempDate;
    					}
    					
    					// add hours mins seconds to enddate to make enddate be the end of the day
    					enddate = DateUtils.getEnd(enddate);
    					
    					sched.setEnddate(enddate);
    					sched.setModuleuuid(moduledetail.getModuleuuid());
    					sched.setPriority(priority);
    					sched.setStartdate(startdate);
    					Long id = siteDesignService.saveContainerModuleSchedule(sched);
    					
    					if(id!=null){
    						apires.setSuccess(true);
    						apires.setResponse1(sched);
    					}
    					
    				}else{
    					apires.setResponse1("You must select a module for the schedule and you must have a priority selected! (moduleUuid: "+moduleUuid+", priority: "+priority+")");
    				}
    				
    			}
    		}else{
    			apires.setResponse1("System can't get updatetype! You can refresh the page and try it again.");
    		}
    	}else{
    		apires.setResponse1("System can't find containerModuleSchedule by scheduleUuid: "+scheduleUuidd);
    	}
    	
    	
    	
    	return apires;
    	
    	
    }
    
    @RequestMapping(value="/delContainerModuleSchedule", method=RequestMethod.POST)
    public ModelAndView delContainerModuleSchedule(
    		@RequestParam(value = "containerId", required = false) String containerUuid,
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuid,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "moduleId", required = false) String moduleUuid,
    		@RequestParam(value = "fromDate", required = false) String fromDate,
    		@RequestParam(value = "toDate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
    	
		ModelAndView mv = new ModelAndView("containerModuleSchedule");
		
		if(StringUtils.isNotBlank(containerUuid)){
			ContainerDetail container = siteDesignService.getContainerByUuid(containerUuid);
			
			if(StringUtils.isNotBlank(scheduleUuid)){
				siteDesignService.delContainerModuleScheduleByUuid(scheduleUuid);
			}
			
			// objs to return
			if(container!=null){
				mv.addObject("container", container);
				
				// get schedules
				List<ContainerModuleSchedule> schds = siteDesignService.findContainerModuleSchedulesByContainerUuid(container.getContaineruuid());
				if(schds!=null && schds.size()>0){
					mv.addObject("schedules", schds);
				}
				
				// get modules
				List<ModuleDetail> moduleDetails = siteDesignService.findOrgModules(container.getOrganization_id());
				if(moduleDetails!=null && moduleDetails.size()>0){
					List<GeneralSelectionType> modules = new ArrayList<GeneralSelectionType>();
					for(ModuleDetail m : moduleDetails){
						modules.add(new GeneralSelectionType(m.getModuleuuid(), m.getPrettyname(), false));
					}
					mv.addObject("modules", modules);
				}
				
				// get PriorityLevels
				mv.addObject("priorityLevels", ContainerModuleSchedule.PriorityLevel.values());
			}
		}else{
			List<String> errorList = new ArrayList<String>();
			errorList.add("You need refresh the page to try again.");
			mv.addObject("errors", errorList);

		}
		
		return mv;
    	
    }
    
    @RequestMapping(value="/moduleInstanceSchedule", method=RequestMethod.GET)
    public ModelAndView moduleInstanceSchedule(
			@RequestParam(value = "containerModuleScheduleId", required = false) String containerModuleScheduleUuid
		) {
		ModelAndView mv = new ModelAndView("moduleInstanceSchedule");
		
		if(StringUtils.isNotBlank(containerModuleScheduleUuid)){
			// get containerModuleSchedule
			ContainerModuleSchedule cmSchedule = siteDesignService.getContainerModuleScheduleByUuid(containerModuleScheduleUuid);
			if(cmSchedule!=null){
				mv.addObject("cmSchedule", cmSchedule);
				// get module in GeneralSelecttype
				ModuleDetail module = siteDesignService.getModuleDetailByUuid(cmSchedule.getModuleuuid());
				if(module!=null){
					GeneralSelectionType md = new GeneralSelectionType(module.getModuleuuid(), module.getPrettyname(), Boolean.TRUE);
					mv.addObject("module", md);
				}
				// get container
				ContainerDetail container = siteDesignService.getContainerByUuid(cmSchedule.getContaineruuid());
				mv.addObject("container", container);
				
				// get list moduleInstanceSchedules
				List<ModuleInstanceSchedule> miSchedules = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(cmSchedule.getUuid());
				mv.addObject("miSchedules", miSchedules);
				
				// get list of instance for the module
				if(module!=null){
					List<ModuleInstance> instances = siteDesignService.findModuleInstancesByModuleUuid(module.getModuleuuid());
					List<GeneralSelectionType> ins = new ArrayList<GeneralSelectionType>();
					if(instances!=null && instances.size()>0){
						for(ModuleInstance i : instances){
							ins.add(new GeneralSelectionType(i.getModuleinstanceuuid(), i.getName(), Boolean.FALSE));
						}
					}
					mv.addObject("instances", ins);
				}
				
				// get list of schedule level
				mv.addObject("priorityLevels", ModuleInstanceSchedule.PriorityLevel.values());
				
				
			}
			
		}
		
		
		
		return mv;
    }
    
    
    
    
    
    @RequestMapping(value="/instanceViewManage", method=RequestMethod.GET)
    public ModelAndView instanceViewManage(
    		@RequestParam(value = "moduleInstanceId", required = false) String moduleInstanceUuid
//    		@RequestParam(value = "moduleId", required = false) String moduleUuid
		) {
    	
		ModelAndView mv = new ModelAndView("instanceViewManage");
		
		// get instance
		ModuleInstance instance = siteDesignService.getModuleInstanceByUuid(moduleInstanceUuid);
		mv.addObject("instance", instance);
		
		// get instance views by instance uuid
		List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(moduleInstanceUuid);
		mv.addObject("views", views);
		
		// get viewScheduleMap
		if(views!=null && views.size()>0){
			Map<String, List<InstanceViewSchedule>> viewSchedsMap = new HashMap<String, List<InstanceViewSchedule>>();
			for(InstanceView v : views){
				List<InstanceViewSchedule> scheds = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(v.getInstanceviewuuid());
				if(scheds!=null && scheds.size()>0){
					viewSchedsMap.put(v.getInstanceviewuuid(), scheds);
				}
			}
			mv.addObject("viewSchedsMap", viewSchedsMap);
		}
		
		// map to hold all activated views and schedules for today - uuid : InstanceView/InstanceViewSchedule
		List<String> instanceUuids = new ArrayList<String>();
		instanceUuids.add(moduleInstanceUuid);
		Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
		siteDesignService.findAllActivatedViewsAndScheds(instanceUuids, activatedViewsAndScheds);
		mv.addObject("activatedViewsAndScheds", activatedViewsAndScheds);
		
		return mv;
    }
    	
    @RequestMapping(value="/instanceViewManageForEntity", method=RequestMethod.GET)
    public ModelAndView instanceViewManageForEntity(
    		@RequestParam(value = "entityUuid", required = false) String entityUuid
		) {
    	
		ModelAndView mv = new ModelAndView("instanceViewManageForEntity");
		
		// get instance (entity) tree : from root to itself
		EntityDetail instance = productService.getEntityDetailByUuid(entityUuid);
		if(instance!=null){
			mv.addObject("instance", instance);
			String[] paths = null;
			
			EntityTreeNode entityRoot = null;
			EntityTreeNode currentEntity = entityRoot;

			if(StringUtils.isNotBlank(instance.getPath())){
				paths = instance.getPath().split("/");

				// create a treeNode from root to itself
				for(String path: paths){
					EntityDetail fd = productService.getEntityDetailByUuid(path);
					if(fd!=null){
						if(currentEntity==null){ // first folder (root)
							entityRoot = new EntityTreeNode();
							entityRoot.setPrettyName(fd.getName());
							entityRoot.setSystemName(fd.getEntityuuid());
							entityRoot.setType(EntityType.folder);
//							entityRoot.setCssClassInfos("newView");
							
							currentEntity = entityRoot;
						}else{
							EntityTreeNode node = new EntityTreeNode();
							node.setPrettyName(fd.getName());
							node.setSystemName(fd.getEntityuuid());
							node.setType(EntityType.folder);
//							node.setCssClassInfos("newView");
							
							currentEntity.addSubnode(node);
							currentEntity = node;
							
						}
					}else{
						
						String errorInfo = "entity '"+instance.getEntityuuid()+"' has path problem. Path '"+path+"' can't find entityDetail.";
						logger.error(errorInfo);
						
						mv.addObject("errorInfo", errorInfo);
						
						break;
					}
				}
			}
			
			EntityTreeNode node = new EntityTreeNode();
			node.setPrettyName(instance.getName());
			node.setSystemName(instance.getEntityuuid());
			node.setType(EntityType.fromCode(instance.getType()));
			node.setCssClassInfos("newView");
			
			if(currentEntity!=null){
				currentEntity.addSubnode(node);
			}else{
				entityRoot = node;
			}
			
			mv.addObject("entityRoot", entityRoot);
			
			
			// find all views for entity, and put into a map - entityUuid:list of views
			List<String> entityUuids = new ArrayList<String>();
			if(paths!=null){
				for(String uuid : paths){
					entityUuids.add(uuid);
				}
			}
			entityUuids.add(instance.getEntityuuid());
			
			Map<String, List<InstanceView>> entityWithViewsMap = new HashMap<String, List<InstanceView>>();
			for(String uuid: entityUuids){
				List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(uuid);
				if(views!=null && views.size()>0){
					entityWithViewsMap.put(uuid, views);
				}
			}
			mv.addObject("entityWithViewsMap", entityWithViewsMap);

			// find all instanceviewschedules for views
			if(entityWithViewsMap.size()>0){
				Map<String, List<InstanceViewSchedule>> viewWithSchedsMap = new HashMap<String, List<InstanceViewSchedule>>();
				for(Map.Entry<String, List<InstanceView>> entry : entityWithViewsMap.entrySet()){
					if(entry.getValue()!=null && entry.getValue().size()>0){
						for(InstanceView v : entry.getValue()){
							List<InstanceViewSchedule> scheds = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(v.getInstanceviewuuid());
							if(scheds!=null && scheds.size()>0){
								viewWithSchedsMap.put(v.getInstanceviewuuid(), scheds);
							}
						}
					}
				}
				mv.addObject("viewWithSchedsMap", viewWithSchedsMap);
			}
			
			// map to hold all activated views and schedules for today - uuid : InstanceView/InstanceViewSchedule
			Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
			siteDesignService.findAllActivatedViewsAndScheds(entityUuids, activatedViewsAndScheds);
			mv.addObject("activatedViewsAndScheds", activatedViewsAndScheds);
			
		}
		
		
		
		
//		
//		mv.addObject("instance", instance);
//		
//		// get instance views by instance uuid
//		List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(entityUuid);
//		mv.addObject("views", views);
//		
//		// get viewScheduleMap
//		if(views!=null && views.size()>0){
//			Map<String, List<InstanceViewSchedule>> viewSchedsMap = new HashMap<String, List<InstanceViewSchedule>>();
//			for(InstanceView v : views){
//				List<InstanceViewSchedule> scheds = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(v.getInstanceviewuuid());
//				if(scheds!=null && scheds.size()>0){
//					viewSchedsMap.put(v.getInstanceviewuuid(), scheds);
//				}
//			}
//			mv.addObject("viewSchedsMap", viewSchedsMap);
//		}
		
		return mv;
    }
    	
    @RequestMapping(value="/instanceViewConfig", method=RequestMethod.POST)
    public @ResponseBody ApiResponse instanceViewCreate_post(
    		@RequestParam(value = "instanceId", required = false) String instanceUuid,
    		@RequestParam(value = "instanceViewType", required = false) String instanceViewType,
    		@RequestParam(value = "instanceViewId", required = false) String instanceViewUuid,
    		@RequestParam(value = "viewname", required = false) String viewname,
    		@RequestParam(value = "desc", required = false) String desc,
    		@RequestParam(value = "jsp", required = false) String jsp,
    		@RequestParam(value = "css", required = false) String css,
    		@RequestParam(value = "isDefaultView", required = false) String isDefaultView
    		
		) {
    	
    	AccountDto loginaccount = accountService.getCurrentAccount();
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
		InstanceView.Type iviewType = InstanceView.Type.fromCode(instanceViewType.trim());
		// get instance
		ModuleInstanceInterface instance = null;
		if(iviewType!=null){
			if(iviewType.equals(InstanceView.Type.NormalInstanceView)){
				instance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
			}else if(iviewType.equals(InstanceView.Type.ProductInstanceView)){
				instance = productService.getEntityDetailByUuid(instanceUuid);
			}
		}

    	if(instance!=null && StringUtils.isNotBlank(instanceViewUuid) && StringUtils.isNotBlank(viewname)){
    		
			// permission check: moduledetail's edit permission Or entityDetail's edit permission
    		boolean editPermissionAllow = false;
    		if(iviewType.equals(InstanceView.Type.NormalInstanceView)){
    			editPermissionAllow = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, ((ModuleInstance)instance).getModuleuuid());
    		}else if(iviewType.equals(InstanceView.Type.ProductInstanceView)){
    			editPermissionAllow = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, ((EntityDetail)instance).getEntityuuid());
    		}
			
    		if(editPermissionAllow){
        		// view name validation
        		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(viewname);
        		
        		if(nameVali){
        			
                	InstanceView view = siteDesignService.getInstanceViewByUuid(instanceViewUuid);
                	
                	Long viewId = null;
                	String viewUuid = null;
                	if(view!=null){ // update view
                		viewUuid = view.getInstanceviewuuid();
                		view.setCss(css);
                		view.setDescription(desc);
                		view.setViewname(viewname);
                		// escape jsp 
                		view.setJsp(StringEscapeUtils.escapeHtml(jsp));
                		view.setCss(css);
                		viewId = siteDesignService.saveInstanceView(view);
                		if(viewId!=null){
                			apires.setSuccess(true);
                			apires.setResponse1(viewname+" is updated!");
                		}
                		
                	}else if("newView".equalsIgnoreCase(instanceViewUuid)){ // new instance view
                		
                		
                		if(instance!=null){
                			viewUuid = UUID.randomUUID().toString(); 
                			if(iviewType.equals(InstanceView.Type.NormalInstanceView)){
                        		view = new InstanceView(null, 
                        				viewUuid, 
                        				((ModuleInstance)instance).getModuleuuid(), 
                        				((ModuleInstance)instance).getModuleinstanceuuid(), 
                        				viewname, 
                        				desc, 
                        				StringEscapeUtils.escapeHtml(jsp), 
                        				css, 
                        				ModuleInstance.IsDefault.no.getCode(),
                        				InstanceView.Type.NormalInstanceView.getCode(),
                        				((ModuleInstance)instance).getOrgid(), 
                        				new Date(), 
                        				accountService.getCurrentAccount().getId());
                				
                			}else if(iviewType.equals(InstanceView.Type.ProductInstanceView)){
                				// get org's product module
                				//ModuleDetail productModule = siteDesignService.getOrgProductModule(((EntityDetail)instance).getOrganization_id());
                				
                				view = new InstanceView(null, 
                						viewUuid, 
                						((EntityDetail)instance).getModuleuuid(), 
                						((EntityDetail)instance).getEntityuuid(), 
                						viewname, 
                						desc, 
                						StringEscapeUtils.escapeHtml(jsp),
                						css, 
                						ModuleInstance.IsDefault.no.getCode(),
                						InstanceView.Type.ProductInstanceView.getCode(), 
                						((EntityDetail)instance).getOrganization_id(), 
                						new Date(), 
                						accountService.getCurrentAccount().getId());
                			}
                    		
                    		viewId = siteDesignService.saveInstanceView(view);
                    		if(viewId!=null){
                    			apires.setSuccess(true);
                    			apires.setResponse1(viewname+" is created!");
                    			apires.setResponse2(new GeneralSelectionType(view.getInstanceviewuuid(), view.getViewname(), Boolean.FALSE));
                    		}
                    		
                    		
                		}
                		
                	}
                	
                	// default view setup
                	if(StringUtils.isBlank(isDefaultView)){
                		isDefaultView = InstanceView.IsDefault.no.getCode();
                	}
                	InstanceView.IsDefault isdefault = InstanceView.IsDefault.fromCode(isDefaultView.trim());
                	if(StringUtils.isNotBlank(viewUuid) && isdefault!=null){
                		siteDesignService.setDefaultInstanceView(viewUuid, isdefault);
                	}
                	
                	if(view!=null){
            			siteDesignService.writeInstanceViewJspToFile(view.getId());
            			siteDesignService.writeInstanceViewCssToFile(view.getId());
                		
                	}
        			
        		}else{
        			apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
        		}
    			
    		}else{
    			apires.setResponse1("You don't have permission for the action");
    		}
			
			
    		
    	}else{
    		apires.setResponse1("instance id or instanceView id or view name is null!");
    	}
    	
    	if(apires==null){
    		apires.setResponse1("no view created!");
    	}
    	
    	return apires;
    	
    }
    	
    @RequestMapping(value="/getInstanceViewForm", method=RequestMethod.GET)
    public ModelAndView getInstanceViewForm(
    		@RequestParam(value = "instanceViewUuid", required = false) String instanceViewUuid
		) {
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
		ModelAndView mv = new ModelAndView("instanceViewForm");
		// get view
		InstanceView view = siteDesignService.getInstanceViewByUuid(instanceViewUuid);
		
		if(view!=null){
			
			// find instance is entityDetail or moduleDetail's instance
			EntityDetail entityDetail = productService.getEntityDetailByUuid(view.getModuleinstanceuuid());
			ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(view.getModuleinstanceuuid());
			
			String pointUuid = null; // uuid for entitydetail or moduledetail
			if(entityDetail!=null){
				pointUuid = entityDetail.getEntityuuid();
			}else if(moduleInstance!=null){
				pointUuid = moduleInstance.getModuleuuid();
			}
			
			boolean modifyPermissionAllow = false;
			if(pointUuid!=null){
				modifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, pointUuid);
			}
			mv.addObject("modifyPermissionAllow", modifyPermissionAllow);
			
			mv.addObject("view", view);
			
			// set instanceviewuuid ("" if newView)
			mv.addObject("instanceviewuuid", instanceViewUuid);
			
			// set instanceUuid
			mv.addObject("instanceUuid", view.getModuleinstanceuuid());
			
			// set instanceViewType
			mv.addObject("instanceViewType", view.getViewtype());
			
			// find module's jsp usage: 
			int usedJspSpaceWith100Multipled = paymentService.countInstanceViewJspUsage(view);
			mv.addObject("viewUsedJspSpaceWith100Multipled", usedJspSpaceWith100Multipled);;
			
			// find module's css usage: 
			int usedCssSpaceWith100Multipled = paymentService.countInstanceViewCssUsage(view);
			mv.addObject("viewUsedCssSpaceWith100Multipled", usedCssSpaceWith100Multipled);;
			
		}
		
		return mv;
    }
    
    @RequestMapping(value="/newModuleInstanceSchedule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse newModuleInstanceSchedule(
    		@RequestParam(value = "containerModuleSchedUuid", required = false) String containerModuleScheduleUuid,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "moduleInstanceUuid", required = false) String instanceUuid,
    		@RequestParam(value = "startdate", required = false) String fromDate,
    		@RequestParam(value = "enddate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ContainerModuleSchedule containerModuleSchedule = siteDesignService.getContainerModuleScheduleByUuid(containerModuleScheduleUuid);
    	ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
    	ModuleInstanceSchedule.PriorityLevel priorityLevel = ModuleInstanceSchedule.PriorityLevel.fromCode(priority!=null?priority.intValue():-1);
    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerModuleSchedule.getContaineruuid());
    	
    	if(containerModuleSchedule!=null && moduleInstance!=null && priorityLevel!=null){
    		if(StringUtils.isNotBlank(scheduleName)){
    			
    			// name validation
    			boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(scheduleName);
    			if(nameVali){
    				// translate string date to Date
    				String datePattern = "yyyy-MM-dd";
    				SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
    				Date startdate = null;
    				Date enddate = null;
    				try {
    					startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
    					enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
    				} catch (ParseException e) {
    					e.printStackTrace();
    				}
    				// if startdate after enddate, switch dates
    				if(startdate!=null && enddate!=null && startdate.after(enddate)){
    					Date tempDate = new Date(startdate.getTime());
    					startdate = enddate;
    					enddate = tempDate;
    				}
    				
    				// new ModuleInstanceSchedule
    				String miScheUuid = UUID.randomUUID().toString();
    				ModuleInstanceSchedule miSche = new ModuleInstanceSchedule(null, 
    						scheduleName, 
    						miScheUuid, 
    						containerModuleSchedule.getUuid(),
    						containerModuleSchedule.getContaineruuid(),
    						containerModuleSchedule.getModuleuuid(), 
    						moduleInstance.getModuleinstanceuuid(), 
    						startdate, 
    						enddate!=null?DateUtils.getEnd(enddate):null, 
    						new Date(), 
    						priority);
    				// save
    				Long miScheId = siteDesignService.saveModuleInstanceSchedule(miSche);
        			
    				if(miScheId!=null){
    					apires.setSuccess(true);
    					apires.setResponse1(miSche);
    					apires.setResponse2(containerDetail);
    				}
    			}else{
    				apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    			}
    			
    		}else{
    			apires.setResponse1("scheduleName can't be empty for newModuleInstanceSchedule.");
    		}
    	}else{
    		apires.setResponse1("System can't find ContainerModuleSchedule or ModuleInstance or ModuleInstanceSchedule.PriorityLevel by containerModuleScheduleUuid="+containerModuleScheduleUuid+", instanceUuid="+instanceUuid+", priority="+priority);
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/editModuleInstanceSchedule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse editModuleInstanceSchedule(
    		@RequestParam(value = "updatetype", required = false) String updatetype,
    		@RequestParam(value = "schedUuid", required = false) String scheduleUuidd,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "startdate", required = false) String fromDate,
    		@RequestParam(value = "enddate", required = false) String toDate,
    		@RequestParam(value = "moduleInstanceUuid", required = false) String instanceUuid,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
    	
    	
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ModuleInstanceSchedule sched = siteDesignService.getModuleInstanceScheduleByUuid(scheduleUuidd);
    	if(sched!=null){
    		if(StringUtils.isNotBlank(updatetype)){
    			if("rename".equals(updatetype)){
    				if(StringUtils.isNotBlank(scheduleName)){
    					// name vali
    					boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(scheduleName);
    					if(nameVali){
    						
        					sched.setSchedulename(scheduleName.trim());
        					Long id = siteDesignService.saveModuleInstanceSchedule(sched);
        					if(id!=null){
        						apires.setSuccess(true);
        						apires.setResponse1(sched);
        					}
        					
    					}else{
    	    				apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    					}
    					
    				}else{
    					apires.setResponse1("You need to provide new schedule name to rename the schedule.");
    				}
    			}else if("general".equals(updatetype)){
    				
    				ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
    				ModuleInstanceSchedule.PriorityLevel priorityLevel = ModuleInstanceSchedule.PriorityLevel.fromCode(priority.intValue());

    				if(moduleInstance!=null && priorityLevel!=null){
    					
    					// translate string date to Date
    					String datePattern = "yyyy-MM-dd";
    					SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
    					Date startdate = null;
    					Date enddate = null;
    					try {
    						startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
    						enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
    					} catch (ParseException e) {
    						e.printStackTrace();
    					}
    					// if startdate after enddate, switch dates
    					if(startdate!=null && enddate!=null && startdate.after(enddate)){
    						Date tempDate = new Date(startdate.getTime());
    						startdate = enddate;
    						enddate = tempDate;
    					}
    					
    					enddate = DateUtils.getEnd(enddate);

    					sched.setEnddate(enddate);
    					sched.setModuleinstanceuuid(instanceUuid);
    					sched.setPriority(priority);
    					sched.setStartdate(startdate);
    					Long id = siteDesignService.saveModuleInstanceSchedule(sched);
    					
    					if(id!=null){
    						apires.setSuccess(true);
    						apires.setResponse1(sched);
    					}
    					
    				}else{
    					apires.setResponse1("You must select a instance for the schedule and you must have a priority selected! (instanceuuid: "+instanceUuid+", priority: "+priority+")");
    				}
    				
    			}
    		}else{
    			apires.setResponse1("System can't get updatetype! You can refresh the page and try it again.");
    		}
    	}else{
    		apires.setResponse1("System can't find moduleInstanceSchedule by scheduleUuid: "+scheduleUuidd);
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/delModuleInstanceSchedule", method=RequestMethod.POST)
    public ModelAndView delModuleInstanceSchedule(
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuidd,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "fromDate", required = false) String fromDate,
    		@RequestParam(value = "toDate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
		ModelAndView mv = new ModelAndView("moduleInstanceSchedule");
		if(StringUtils.isNotBlank(scheduleUuidd)){
			ModuleInstanceSchedule mische = siteDesignService.getModuleInstanceScheduleByUuid(scheduleUuidd);
			String containerModuleScheduleUuid = mische.getContainermodulescheduleuuid();
			if(mische!=null){
				siteDesignService.delModuleInstanceScheduleByUuid(scheduleUuidd);
			}
			// get obj to return
			if(StringUtils.isNotBlank(containerModuleScheduleUuid)){
				
				// get containerModuleSchedule 
				ContainerModuleSchedule cmSche = siteDesignService.getContainerModuleScheduleByUuid(containerModuleScheduleUuid);
				
				if(cmSche!=null){
					mv.addObject("cmSchedule", cmSche);
					// get module in GeneralSelecttype
					ModuleDetail module = siteDesignService.getModuleDetailByUuid(cmSche.getModuleuuid());
					if(module!=null){
						GeneralSelectionType md = new GeneralSelectionType(module.getModuleuuid(), module.getPrettyname(), Boolean.TRUE);
						mv.addObject("module", md);
					}
					// get container
					ContainerDetail container = siteDesignService.getContainerByUuid(cmSche.getContaineruuid());
					mv.addObject("container", container);
					
					// get list moduleInstanceSchedules
					List<ModuleInstanceSchedule> miSchedules = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(cmSche.getUuid());
					mv.addObject("miSchedules", miSchedules);
					
					// get list of instance for the module
					if(module!=null){
						List<ModuleInstance> instances = siteDesignService.findModuleInstancesByModuleUuid(module.getModuleuuid());
						List<GeneralSelectionType> ins = new ArrayList<GeneralSelectionType>();
						if(instances!=null && instances.size()>0){
							for(ModuleInstance i : instances){
								ins.add(new GeneralSelectionType(i.getModuleinstanceuuid(), i.getName(), Boolean.FALSE));
							}
						}
						mv.addObject("instances", ins);
					}
					
					// get list of schedule level
					mv.addObject("priorityLevels", ModuleInstanceSchedule.PriorityLevel.values());
				}
			}
			
			
			
			
		}else{
			List<String> errorList = new ArrayList<String>();
			errorList.add("You need refresh the page to try again.");
			mv.addObject("errors", errorList);
		}
		
		return mv;
    }
    	
    
    
    
    
    @RequestMapping(value="/instanceViewSchedule", method=RequestMethod.GET)
    public ModelAndView instanceViewSchedule(
    		@RequestParam(value = "schedId", required = false) String schedUuid
		) {
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
		ModelAndView mv = new ModelAndView("instanceViewSchedule");
		
		InstanceViewSchedule viewSched = siteDesignService.getInstanceViewScheduleByUuid(schedUuid);
		if(viewSched!=null){
			mv.addObject("sch", viewSched);
			
			// get instance 
			ModuleInstanceInterface instance = siteDesignService.getModuleInstanceByUuid(viewSched.getModuleinstanceuuid());
			if(instance==null){
				instance = productService.getEntityDetailByUuid(viewSched.getModuleinstanceuuid());
			}
			if(instance!=null){
				boolean modifyPermissionAllow = false;
				
				//GeneralSelectionType ins = new GeneralSelectionType(instance.getModuleinstanceuuid(), instance.getName(), Boolean.FALSE);
				GeneralSelectionType ins = new GeneralSelectionType();
				if(instance.getClass().equals(ModuleInstance.class)){
					ins.setKey(((ModuleInstance)instance).getModuleinstanceuuid());
					ins.setValue(((ModuleInstance)instance).getName());
					
					modifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((ModuleInstance)instance).getModuleuuid());
					
					
				}else if(instance.getClass().equals(EntityDetail.class)){
					ins.setKey(((EntityDetail)instance).getEntityuuid());
					ins.setValue(((EntityDetail)instance).getName());
					
					modifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((EntityDetail)instance).getEntityuuid());
					
				}
				mv.addObject("instance", ins);
				
				mv.addObject("modifyPermissionAllow", modifyPermissionAllow);
			}
			
			// get PriorityLevels
			mv.addObject("priorityLevels", InstanceViewSchedule.PriorityLevel.values());
		}
		
		return mv;
    	
    }
    
    
    @RequestMapping(value="/newInstanceViewSchedule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse newInstanceViewSchedule(
//    		@RequestParam(value = "instanceId", required = false) String moduleInstanceUuid,
    		@RequestParam(value="scheduleName", required = false) String scheduleName,
    		@RequestParam(value = "instanceViewId", required = false) String instanceViewUuid,
    		@RequestParam(value = "fromDate", required = false) String fromDate,
    		@RequestParam(value = "toDate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
    	AccountDto loginaccount = accountService.getCurrentAccount();
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	Date now = new Date();
    	
		// get instance view
		InstanceView instanceView = siteDesignService.getInstanceViewByUuid(instanceViewUuid);

		ContainerModuleSchedule.PriorityLevel priorityLevel = ContainerModuleSchedule.PriorityLevel.fromCode(priority!=null?priority.intValue():-1);

		if(instanceView!=null && priorityLevel!=null && StringUtils.isNotBlank(scheduleName)){
			
			
			boolean editPermissionAllow = false;
			if(InstanceView.Type.NormalInstanceView.getCode().equals(instanceView.getViewtype())){
				editPermissionAllow = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleuuid());
			}else if(InstanceView.Type.ProductInstanceView.getCode().equals(instanceView.getViewtype())){
				editPermissionAllow = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, instanceView.getModuleinstanceuuid());
			}
			
			if(editPermissionAllow){
				
				// name validation
				boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(scheduleName);
				if(nameVali){
		    		
					// translate string date to Date
					String datePattern = "yyyy-MM-dd";
					SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
					Date startdate = null;
					Date enddate = null;
					try {
						startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
						enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
					} catch (ParseException e) {
						e.printStackTrace();
					}
					// if startdate after enddate, switch dates
					if(startdate!=null && enddate!=null && startdate.after(enddate)){
						Date tempDate = new Date(startdate.getTime());
						startdate = enddate;
						enddate = tempDate;
					}
					
					String scheduleUuid = UUID.randomUUID().toString();
					
					String instanceUuid = instanceView.getModuleinstanceuuid();
					
					InstanceViewSchedule ivSchedule = new InstanceViewSchedule(null, 
							scheduleName, 
							scheduleUuid, 
							instanceUuid, 
							instanceView.getInstanceviewuuid(), 
							startdate, 
							enddate!=null?DateUtils.getEnd(enddate):null, 
							now, 
							priority);
					Long ivScheduleId = siteDesignService.saveInstanceViewSchedule(ivSchedule);
					
					if(ivScheduleId!=null){
						apires.setSuccess(true);
						apires.setResponse1(scheduleUuid);
					}else{
						apires.setResponse1("No schedule is created! This could be the system issue - instanceviewUuid: "+instanceViewUuid + ", scheduleName: "+scheduleName
		    				+ ", priority: " + priority);
					}
					
				}else{
					apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
				}
				
			}else{
				apires.setResponse1("You don't have permission for this action.");
			}
    		
    	}else{
    		apires.setResponse1("No enough information to create a schedule - instanceviewUuid: "+instanceViewUuid + ", scheduleName: "+scheduleName
    				+ ", priority: " + priority);
    		
    	}
    	
    	return apires;
    	
    }
    
    @RequestMapping(value="/renameInstanceViewSchedule", method=RequestMethod.GET)
    public @ResponseBody ApiResponse renameInstanceViewSchedule(
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuid,
    		@RequestParam(value = "newName", required = false) String newName
		) {
    	
    	ApiResponse apires = siteDesignService.updateInstanceViewScheduleByFieldnameValue(scheduleUuid, "name", newName);
    	
    	return apires;
    }

    
    
    @RequestMapping(value="/editInstanceViewSchedule", method=RequestMethod.POST)
    public ModelAndView editInstanceViewSchedule(
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuid,
    		@RequestParam(value = "fromDate", required = false) String fromDate,
    		@RequestParam(value = "toDate", required = false) String toDate,
    		@RequestParam(value = "priority", required = false) Integer priority
		) {
		ModelAndView mv = new ModelAndView("instanceViewSchedule");
		
		// get schedule
		InstanceViewSchedule ivSchedule = siteDesignService.getInstanceViewScheduleByUuid(scheduleUuid);
		
		InstanceViewSchedule.PriorityLevel priorityLevel = InstanceViewSchedule.PriorityLevel.fromCode(priority.intValue());

		if(ivSchedule!=null && priorityLevel!=null){
				
			// translate string date to Date
			String datePattern = "yyyy-MM-dd";
			SimpleDateFormat dateformat = new SimpleDateFormat(datePattern);
			Date startdate = null;
			Date enddate = null;
			try {
				startdate = StringUtils.isNotBlank(fromDate)?dateformat.parse(fromDate):null;
				enddate = StringUtils.isNotBlank(toDate)?dateformat.parse(toDate):null;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// if startdate after enddate, switch dates
			if(startdate!=null && enddate!=null && startdate.after(enddate)){
				Date tempDate = new Date(startdate.getTime());
				startdate = enddate;
				enddate = tempDate;
			}
			
			ivSchedule.setEnddate(enddate!=null?DateUtils.getEnd(enddate):null);
			ivSchedule.setPriority(priority);
			ivSchedule.setStartdate(startdate);
			Long scheId = siteDesignService.saveInstanceViewSchedule(ivSchedule);
			
			
			// return back to view
			ModuleInstanceInterface moduleInstance = siteDesignService.getModuleInstanceByUuid(ivSchedule.getModuleinstanceuuid());
			if(moduleInstance==null){
				moduleInstance = productService.getEntityDetailByUuid(ivSchedule.getModuleinstanceuuid());
			}
			if(moduleInstance!=null){
//				GeneralSelectionType ins = new GeneralSelectionType(moduleInstance.getModuleinstanceuuid(), moduleInstance.getName(), Boolean.FALSE);
				
				GeneralSelectionType ins = new GeneralSelectionType();
				if(moduleInstance.getClass().equals(ModuleInstance.class)){
					ins.setKey(((ModuleInstance)moduleInstance).getModuleinstanceuuid());
					ins.setValue(((ModuleInstance)moduleInstance).getName());
				}else if(moduleInstance.getClass().equals(EntityDetail.class)){
					ins.setKey(((EntityDetail)moduleInstance).getEntityuuid());
					ins.setValue(((EntityDetail)moduleInstance).getName());
				}
				
				mv.addObject("instance", ins);
				
				// get PriorityLevels
				mv.addObject("priorityLevels", InstanceViewSchedule.PriorityLevel.values());
				
				// get schedule
				mv.addObject("sch", ivSchedule);
			}
				
			
			
		}else{
			List<String> errorList = new ArrayList<String>();
			errorList.add("No enough information to process the update, you may need to refresh the page. (scheduleUuid: "+scheduleUuid+" priority: "+priority+")");
			mv.addObject("errors", errorList);
			
		}
		return mv;
    }
    	
    @RequestMapping(value="/delInstanceViewSchedule", method=RequestMethod.POST)
    public ModelAndView delInstanceViewSchedule(
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuid
//    		@RequestParam(value="scheduleName", required = false) String scheduleName,
//    		@RequestParam(value = "fromDate", required = false) String fromDate,
//    		@RequestParam(value = "toDate", required = false) String toDate,
//    		@RequestParam(value = "priority", required = false) Integer priority
		) {
		ModelAndView mv = new ModelAndView("instanceViewSchedule");
		
		if(StringUtils.isNotBlank(scheduleUuid)){
			// get sche
			InstanceViewSchedule ivSchedule = siteDesignService.getInstanceViewScheduleByUuid(scheduleUuid);
			if(ivSchedule!=null){
				
				siteDesignService.delInstanceViewScheduleByUuid(scheduleUuid);
				
				// return back to view
				ModuleInstanceInterface moduleInstance = siteDesignService.getModuleInstanceByUuid(ivSchedule.getModuleinstanceuuid());
				if(moduleInstance==null){
					moduleInstance = productService.getEntityDetailByUuid(ivSchedule.getModuleinstanceuuid());
				}
				if(moduleInstance!=null){
//					GeneralSelectionType ins = new GeneralSelectionType(moduleInstance.getModuleinstanceuuid(), moduleInstance.getName(), Boolean.FALSE);
					
					GeneralSelectionType ins = new GeneralSelectionType();
					if(moduleInstance.getClass().equals(ModuleInstance.class)){
						ins.setKey(((ModuleInstance)moduleInstance).getModuleinstanceuuid());
						ins.setValue(((ModuleInstance)moduleInstance).getName());
					}else if(moduleInstance.getClass().equals(EntityDetail.class)){
						ins.setKey(((EntityDetail)moduleInstance).getEntityuuid());
						ins.setValue(((EntityDetail)moduleInstance).getName());
					}
					mv.addObject("instance", ins);
					
//					// get instanceviews
//					List<InstanceView> instanceViews = siteDesignService.findInstanceViewsByInstanceUuid(moduleInstance.getModuleinstanceuuid());
//					if(instanceViews!=null && instanceViews.size()>0){
//						List<GeneralSelectionType> views = new ArrayList<GeneralSelectionType>();
//						for(InstanceView v : instanceViews){
//							views.add(new GeneralSelectionType(v.getInstanceviewuuid(), v.getViewname(), Boolean.FALSE));
//						}
//						mv.addObject("instanceViews", views);
//					}
					
					// get PriorityLevels
					mv.addObject("priorityLevels", InstanceViewSchedule.PriorityLevel.values());
					
					// get schedules
//					List<InstanceViewSchedule> ivScheds = siteDesignService.findInstanceViewSchedulesByInstanceUuid(moduleInstance.getModuleinstanceuuid());
//					mv.addObject("schedules", ivScheds);
				}
				
				
				
			}
		}
    	
		return mv;
    }
    

    
    @RequestMapping(value="/getPageChangedInfo", method=RequestMethod.GET)
    public ModelAndView getPageChangedInfo(
    		@RequestParam(value = "pageUuid", required = false) String pageUuid
		) {
    	
		ModelAndView mv = new ModelAndView("pageChangedInfo");
		mv.addObject("pageUuid", pageUuid);
		if(StringUtils.isNotBlank(pageUuid)){
			PageMeta pageMeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
			if(pageMeta!=null && StringUtils.isNotBlank(pageMeta.getChangelist())){
				XStream stream = new XStream(new DomDriver());
				Map<Long, String> changelist = (Map<Long, String>)stream.fromXML(pageMeta.getChangelist());
				mv.addObject("changeList", changelist);
			}
		}
		return mv;
    	
    }
    
    
    /**
     * this method will need to be called every time when you add a new container, del a container, resize a container, move a container.<br/>
     * this method will calculate the margin-top and margin-left for each container for pageRetriever to use.
     * 
     * @param pageUuid
     * @param model
     * @return
     */
    @RequestMapping(value="/pagepublish", method=RequestMethod.POST)
    public @ResponseBody ApiResponse pagepublish(
    		@RequestParam(value = "pageUuid", required = true) String pageUuid,
    		ModelMap model){
    	ApiResponse res = null;
    	
    	ContainerTreeNode containerTreeNode = treeService.getContaienrTreeRootWithSubNodes(pageUuid);
    	if(containerTreeNode!=null){
    		
    		// assume containerTreeNode and its subnodes are sorted by ContainerTreeNode.positionSort first!!!
    		siteDesignService.containerTreeNodeMarginSpaceGenerator(null, null, containerTreeNode);
    		
    		siteDesignService.containerTreeNodeRelativeWidthHeightGenerator(null, containerTreeNode);
    		
    		ApiResponse apires =  siteDesignService.pageDetailGenerator(pageUuid, containerTreeNode);

    		return apires;
    		
    	}
    	
    	return res;
    }
    
    @RequestMapping(value="/getPageDetail", method=RequestMethod.GET)
    public ModelAndView getPageDetail(
    		@RequestParam(value = "pageUuid", required = false) String pageUuid
		) {
    	
		ModelAndView mv = new ModelAndView("pageDetailInfo");
		
		if(StringUtils.isNotBlank(pageUuid)){
			PageDetail pageDetail = siteDesignService.getPageDetailByUuid(pageUuid);
			PageMeta pageMeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
			mv.addObject("pageDetail", pageDetail);
			mv.addObject("pageMeta", pageMeta);
		}
		
		
		return mv;
    	
    }
    
    @RequestMapping(value="/pageDetailUpdate", method=RequestMethod.POST)
    public String pageDetailUpdate(
    		@RequestParam(value = "pageId", required = false) String pageUuid,
    		@RequestParam(value = "url", required = false) String url,
    		@RequestParam(value = "css", required = false) String css
    		){
    	
    	if(StringUtils.isNotBlank(pageUuid)){
    		PageDetail pageDetail = siteDesignService.getPageDetailByUuid(pageUuid);
    		if(pageDetail!=null){
        		siteDesignService.setPageUrl(pageUuid, url);
        		siteDesignService.setPageCss(pageUuid, css);
        		
        		siteDesignService.writePageCssToFile(pageDetail.getId());
        		
        		Organization org = accountService.getOrgById(pageDetail.getOrganization_id());
        		return "redirect:/websiteMobile?org="+org.getOrguuid()+"&pageUuid="+pageUuid;
    		}
    		
    	}
    	
    	return "redirect:/";
    }

    @RequestMapping(value="/moduleNewAttr", method=RequestMethod.POST)
    public ModelAndView moduleNewAttr(
    		@RequestParam(value = "moduleId", required = false) String moduleUuid,
    		@RequestParam(value = "attrClassName", required = false) String attrClassName,
    		@RequestParam(value = "attrJspName", required = false) String attrJspName,
    		@RequestParam(value = "attrsGroupId", required = false) String attrsGroupUuid
		) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	
		ModelAndView mv = new ModelAndView("exceptionFragment");
		
		ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
		
		if(moduledetail!=null && StringUtils.isNotBlank(attrClassName) && StringUtils.isNotBlank(attrJspName) && StringUtils.isNotBlank(attrsGroupUuid)){
			
			Organization org = accountService.getOrgById(moduledetail.getOrganization_id());

//			String newModuleAttrUuid = siteDesignService.newModuleAttr(moduleUuid, attrsGroupUuid, attrClassName);
			
			ApiResponse apires = siteDesignService.newModuleAttr(moduleUuid, attrsGroupUuid, attrClassName);
			
			
			if(apires.isSuccess()){
				AttrGroup attrGroup = siteDesignService.getModuleAttrGroupByUuid(moduleUuid, attrsGroupUuid);
				ModuleAttribute moduleAttr = siteDesignService.getModuleAttrByUuid(moduleUuid, attrsGroupUuid, (String)apires.getResponse1());
				if(moduleAttr!=null){
					mv = new ModelAndView("module/"+attrJspName);
					//mv.addObject("moduleAttrName", moduleAttr.getClass().getSimpleName());
					mv.addObject("moduleAttr", moduleAttr);
					mv.addObject("attrGroup", attrGroup);
					mv.addObject("moduleDetail", moduledetail);
					
					mv.addObject("isDisplayOnly", false);
					mv.addObject("modifyPermissionAllowed", true);
					
					// some extra param for jsp
					if(moduleAttr.getClass().equals(ModuleLinkAttribute.class)){
						mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
						mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
					}else if(moduleAttr.getClass().equals(ModuleProductListAttribute.class)){
						
						List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
						List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
						List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
						List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
						if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
							for(PageDetail d : orgDeskPageDetails){
								orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
							}
						}
						if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
							for(PageDetail d : orgMobilePageDetails){
								orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
							}
						}
						mv.addObject("orgDeskPages", orgDeskPages);
						mv.addObject("orgMobilePages", orgMobilePages);
						
					}else if(moduleAttr.getClass().equals(ModuleMoneyAttribute.class)){
						mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
					}else if(moduleAttr.getClass().equals(ModuleEntityCategoryListAttribute.class)){
						mv.addObject("entityCatTypeList", ModuleEntityCategoryListAttribute.Type.values());
						mv.addObject("entityCatSoryBy", ModuleEntityCategoryListAttribute.SortType.values());
					}
					
					// add moduleCharUsage info
					mv.addObject("moduleDetailUsageWith100Multipled_newAttr", apires.getResponse3());
					
				}else{
					mv.addObject("ex", "System can't find moduleAttr by moduleUuid: "+ moduleUuid +", attrsGroupUuid: " + attrsGroupUuid + ", new AttriUuid: " + apires.getResponse1());
				}
			}else{
				mv.addObject("ex", apires.getResponse1());
			}
			
		}else{
			mv.addObject("ex", "No module id and other information found!");
		}
		
		return mv;
    	
    }
    
    @RequestMapping(value="/newAttrGroupSet", method=RequestMethod.GET)
    public ModelAndView newAttrGroupSet(
    		@RequestParam(value = "moduleId", required = false) String moduleUuid
		){
    	ModelAndView mv;
    	
    	if(StringUtils.isNotBlank(moduleUuid)){
    		
    		ApiResponse apires = siteDesignService.newAttrGroupSet(moduleUuid);
    		if(apires.isSuccess()){
    			AttrGroup attrGroup = siteDesignService.getModuleAttrGroupByUuid(moduleUuid, (String)apires.getResponse1());
        		mv = new ModelAndView("module/moduleAttrGroupSetTemplate");
        		mv.addObject("attrGroup", attrGroup);
        		
        		mv.addObject("isDisplayOnly", false);
        		mv.addObject("modifyPermissionAllowed", true);
        		
        		mv.addObject("moduleDetailUsageWith100Multipled_newAttrGroupSet", apires.getResponse3());
        		
    		}else{
        		mv = new ModelAndView("exceptionFragment");
        		mv.addObject("ex", (String)apires.getResponse1());
    		}
    	}else{
    		mv = new ModelAndView("exceptionFragment");
    		mv.addObject("ex", "No module's Id passed! Try to refress the page.");
    	}
		
		
		return mv;
    	
    }
    
    @RequestMapping(value="/deleteAttrGroupSet", method=RequestMethod.GET)
    public @ResponseBody ApiResponse deleteAttrGroupSet(
    		@RequestParam(value = "moduleid", required = false) String moduleUuid,
    		@RequestParam(value = "attrGroupId", required = false) String attrGroupUuid
		){
    	
    	ApiResponse apires = siteDesignService.delAttrGroupSet(moduleUuid, attrGroupUuid);
    	return apires;
    }
    
    @RequestMapping(value="/deleteInstanceAttrGroupSet", method=RequestMethod.GET)
    public @ResponseBody ApiResponse deleteInstanceAttrGroupSet(
    		@RequestParam(value = "instanceUuid", required = false) String instanceUuid,
    		@RequestParam(value = "attrGroupUuid", required = false) String attrGroupUuid
		){
    	
    	ApiResponse apires = null;
    	
    	apires = siteDesignService.delInstanceAttrGroupSet(instanceUuid, attrGroupUuid);
    	
    	return apires;
    }
    
    
    @RequestMapping(value="/deleteInstanceAttr", method=RequestMethod.GET)
    public @ResponseBody ApiResponse deleteInstanceAttr(
    		@RequestParam(value = "instanceId", required = false) String instanceUuid,
    		@RequestParam(value = "attrGroupId", required = false) String attrGroupUuid,
    		@RequestParam(value = "attrId", required = false) String attrUuid
		){
    	
    	ApiResponse apires = null;
    	
    	apires = siteDesignService.delInstanceAttr(instanceUuid, attrGroupUuid, attrUuid);
    	
//    	siteDesignService.delModuleAttr(moduleUuid, attrGroupUuid, attrUuid);
//    	
//    	apires = new ApiResponse();
//    	apires.setSuccess(true);
//    	apires.setResponse1(attrUuid);
    	
    	return apires;
    }
    
    
    @RequestMapping(value="/deleteModuleAttr", method=RequestMethod.GET)
    public @ResponseBody ApiResponse deleteModuleAttr(
    		@RequestParam(value = "moduleId", required = false) String moduleUuid,
    		@RequestParam(value = "attrGroupId", required = false) String attrGroupUuid,
    		@RequestParam(value = "attrId", required = false) String attrUuid
		){
    	
    	ApiResponse apires = siteDesignService.delModuleAttr(moduleUuid, attrGroupUuid, attrUuid);
    	apires.setResponse1(attrUuid);
    	
    	return apires;
    }
    
    @RequestMapping(value="/updateModuleValue")
    public @ResponseBody ApiResponse updateModuleValue(
    		@RequestParam(value = "moduleId", required = false) String moduleUuid,
    		@RequestParam(value = "groupUuid", required = false) String attrGroupUuid,
    		@RequestParam(value = "attrUuid", required = false) String attrUuid,
    		@RequestParam(value = "updateType", required = false) String updateType, // only three update type: moduledetailValue & groupValue & attrValue
    		@RequestParam(value = "valueName", required = false) String updateValueName, // the input or select name, which is same as the name defined in AttrGroup or ModuleAttribute
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = new ApiResponse();
    	
    	if(StringUtils.isNotBlank(moduleUuid) 
    			&& StringUtils.isNotBlank(updateType) 
    			&& StringUtils.isNotBlank(updateValueName)
    			&& (updateType.equals("groupValue") || updateType.equals("attrValue") || updateType.equals("moduledetailValue"))){
    		if(updateType.equals("groupValue")){ // for group
    			
    			apires = siteDesignService.updateModuleGroupValue(moduleUuid, attrGroupUuid, updateValueName, updateValue);
    			
    		}else if(updateType.equals("attrValue")){ // for attr
    			if(StringUtils.isNotBlank(attrUuid)){

    				apires = siteDesignService.updateModuleAttrValue(moduleUuid, attrGroupUuid, attrUuid, updateValueName, updateValue);
    				// update apires.response1 with other data
    				if(apires.isSuccess()){
    					if(updateValueName.equals("desktopProductPageUuid") 
    							|| updateValueName.equals("mobileProductPageuuid") 
    							|| updateValueName.equals("deskCatListPageuuid")
    							|| updateValueName.equals("mobileCatListPageuuid")){
    						PageDetail pageDetail = siteDesignService.getPageDetailByUuid((String)apires.getResponse1());
    						if(pageDetail!=null){
    							apires.setResponse1(pageDetail.getPrettyname());
    						}
    					}
    				}
    				
    				
    				
    			}else{
    	    		apires.setSuccess(false);
    	    		apires.setResponse1("Not enough information is provided to update module value.");
    			}
    		}else if(updateType.equals("moduledetailValue")){ // for moduleDetail
    			ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    			if(moduledetail!=null){
        			apires = siteDesignService.updateModuleDetailByFieldnameValue(moduleUuid, updateValueName, updateValue);
        			
        			if(apires.isSuccess()){
            			if(updateValueName.equals("jsp")){
                			siteDesignService.writeModuleJspToFile(moduledetail.getId());
            			}else if(updateValueName.equals("css")){
                			siteDesignService.writeModuleCssToFile(moduledetail.getId());
            			}
        			}
    			}
    		}
    		
    	}else{
    		apires.setSuccess(false);
    		apires.setResponse1("Not enough information is provided to update module value.");
    	}
    	
    	return apires;
    }
    
    
    
    @RequestMapping(value="/updateModuleMetaValue")
    public @ResponseBody ApiResponse updateModuleMetaValue(
    		@RequestParam(value = "targetId", required = false) String targetId,
    		@RequestParam(value = "valueName", required = false) String updateValueName, // the input or select name, which is same as the name defined in AttrGroup or ModuleAttribute
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(targetId) && StringUtils.isNotBlank(updateValueName)){
    		// get the real targetId
    		targetId = targetId.substring(targetId.indexOf("_")+1);
    		return siteDesignService.updateModuleMetaValue(targetId, updateValueName, updateValue);
    		
    	}else{
    		apires.setResponse1("targetId and updateValue can't be empty!");
    	}
    	
    	return apires;
    }
    	
    @RequestMapping(value="/removeModuleMetaValue")
    public @ResponseBody ApiResponse removeModuleMetaValue(
    		@RequestParam(value = "targetId", required = false) String targetId,
    		@RequestParam(value = "type", required = false) String type
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(targetId) && StringUtils.isNotBlank(type)){
    		// get the real targetId
    		targetId = targetId.substring(targetId.indexOf("_")+1);
    		
    		return siteDesignService.removeModuleMetaValue(targetId, type);
    		
    	}else{
    		apires.setResponse1("targetId and updateValue can't be empty!");
    	}
    	
    	return apires;
    }
    	
    
    
    
    
    
    @RequestMapping(value="/updateInstanceValue")
    public @ResponseBody ApiResponse updateInstanceValue(
    		@RequestParam(value = "instanceId", required = false) String instanceUuid,
    		@RequestParam(value = "groupUuid", required = false) String attrGroupUuid,
    		@RequestParam(value = "attrUuid", required = false) String attrUuid,
    		@RequestParam(value = "updateType", required = false) String updateType, // only two update type: moduleInstanceValue & attrValue
    		@RequestParam(value = "valueName", required = false) String updateValueName, // the input or select name, which is same as the name defined in AttrGroup or ModuleAttribute
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(instanceUuid) 
    			&& StringUtils.isNotBlank(updateType) 
    			&& StringUtils.isNotBlank(updateValueName)
    			&& (updateType.equals("groupValue") || updateType.equals("attrValue") || updateType.equals("moduleInstanceValue"))){
    		if(updateType.equals("groupValue")){ // for group
    			
//    			apires = siteDesignService.updateModuleGroupValue(moduleUuid, attrGroupUuid, updateValueName, updateValue);
    			
    		}else if(updateType.equals("attrValue")){ // for attr
    			if(StringUtils.isNotBlank(attrUuid)){

    				apires = siteDesignService.updateModuleInstanceAttrValue(instanceUuid, attrGroupUuid, attrUuid, updateValueName, updateValue);
    				
    				// update apires.response1 with other data
    				if(apires.isSuccess()){
    					if(updateValueName.equals("desktopProductPageUuid") || updateValueName.equals("mobileProductPageuuid")){
    						PageDetail pageDetail = siteDesignService.getPageDetailByUuid((String)apires.getResponse1());
    						if(pageDetail!=null){
    							apires.setResponse1(pageDetail.getPrettyname());
    						}
    					}
    				}
    				
    				
    			}else{
    	    		apires.setResponse1("Not enough information is provided to update module's instance value.");
    			}
    		}else if(updateType.equals("moduleInstanceValue")){ // for ModuleInstance
    			
    			apires = siteDesignService.updateModuleInstanceValue(instanceUuid, updateValueName, updateValue);
    			
    		}
    		
    	}else{
    		apires.setResponse1("Not enough information is provided to update module value.");
    	}
    	
    	return apires;
    }

    @RequestMapping(value="/updateViewValue")
    public @ResponseBody ApiResponse updateViewValue(
    		@RequestParam(value = "viewId", required = false) String viewUuid,
    		@RequestParam(value = "updateType", required = false) String updateType, // only # update type: instanceViewValue
    		@RequestParam(value = "valueName", required = false) String updateValueName,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = new ApiResponse();
    	
    	if(StringUtils.isNotBlank(viewUuid) 
    			&& StringUtils.isNotBlank(updateType) 
    			&& StringUtils.isNotBlank(updateValueName)
    			&& (updateType.equals("instanceViewValue"))){
    		if(updateType.equals("instanceViewValue")){ // for instanceView
    			
    			InstanceView instanceView = siteDesignService.getInstanceViewByUuid(viewUuid);
    			if(instanceView!=null){
    				
        			apires = siteDesignService.updateInstanceViewByFieldnameValue(viewUuid, updateValueName, updateValue);
        			
        			if(apires.isSuccess()){
            			if(updateValueName.equals("jsp")){
            				siteDesignService.writeInstanceViewJspToFile(instanceView.getId());
            			}else if(updateValueName.equals("css")){
                			siteDesignService.writeInstanceViewCssToFile(instanceView.getId());
            			}
        			}
    			}
    		}
    		
    	}else{
    		apires.setSuccess(false);
    		apires.setResponse1("Not enough information is provided to update view value.");
    	}
    	
    	return apires;
    }
    
    
    @RequestMapping(value="/updatePageDetailValue")
    public @ResponseBody ApiResponse updatePageDetailValue(
    		@RequestParam(value = "pageId", required = false) String pageUuid,
    		@RequestParam(value = "valueName", required = false) String updateValueName,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = siteDesignService.updatePageDetailByFieldnameValue(pageUuid, updateValueName, updateValue);
    	
    	return apires;
    }
    
    
    /**
     * @param containerUuid support container's uuid and "ct_"+uuid
     * @param updateValueName
     * @param updateValue
     * @return
     */
    @RequestMapping(value="/updateContainerDetailValue")
    public @ResponseBody ApiResponse updateContainerDetailValue(
    		@RequestParam(value = "targetId", required = false) String containerUuid,
    		@RequestParam(value = "valueName", required = false) String updateValueName,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	if(containerUuid!=null && containerUuid.startsWith("ct_")){
    		containerUuid = containerUuid.substring(3);
    	}
    	
    	ApiResponse apires = siteDesignService.updateContainerDetailByFieldnameValue(containerUuid, updateValueName, updateValue);
    	
    	return apires;
    }
    
    @RequestMapping(value="/updateViewSchedValue")
    public @ResponseBody ApiResponse updateViewSchedValue(
    		@RequestParam(value = "scheduleId", required = false) String schedUuid,
    		@RequestParam(value = "updateType", required = false) String updateType, // only # update type: instanceViewValue
    		@RequestParam(value = "valueName", required = false) String updateValueName,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = new ApiResponse();
    	
    	if(StringUtils.isNotBlank(schedUuid) 
    			&& StringUtils.isNotBlank(updateType) 
    			&& StringUtils.isNotBlank(updateValueName)
    			&& (updateType.equals("schedValue"))){
    		if(updateType.equals("schedValue")){ // for instanceView
    			
    			InstanceViewSchedule instanceViewSchedule = siteDesignService.getInstanceViewScheduleByUuid(schedUuid);
    			if(instanceViewSchedule!=null){
        			apires = siteDesignService.updateInstanceViewScheduleByFieldnameValue(schedUuid, updateValueName, updateValue);
    			}
    		}
    		
    	}else{
    		apires.setSuccess(false);
    		apires.setResponse1("Not enough information is provided to update view value.");
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/delModuleInstance", method=RequestMethod.GET)
    public @ResponseBody ApiResponse delModuleInstance(
//    		@RequestParam(value = "parentid", required = false) String parentid,
    		@RequestParam(value = "currentNodeId", required = false) String currentNodeId
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	if(StringUtils.isNotBlank(currentNodeId)){
    		apires = siteDesignService.delModuleInstanceByUuid(currentNodeId);
    	}
    	
    	return apires;
    }

    @RequestMapping(value="/instanceViewScheduleDelete", method=RequestMethod.GET)
    public @ResponseBody ApiResponse instanceViewScheduleDelete(
    		@RequestParam(value = "scheduleId", required = false) String scheduleUuid
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	InstanceViewSchedule schedule = siteDesignService.getInstanceViewScheduleByUuid(scheduleUuid);
    	
    	if(schedule!=null){
    		String instanceUuid = schedule.getInstanceviewuuid();
    		
    		apires = siteDesignService.delInstanceViewScheduleByUuid(scheduleUuid);
//    		apires.setSuccess(true);
    		
    		// re-find all activated views and viewSchedules
    		InstanceView view = siteDesignService.getInstanceViewByUuid(instanceUuid);
    		if(view!=null){
    			List<String> instanceUuids = siteDesignService.findInstanceChainFromView(view.getInstanceviewuuid());
    			
    			if(instanceUuids.size()>0){
        			Map<String, String> activatedViewsAndScheds = new HashMap<String, String>();
        			siteDesignService.findAllActivatedViewsAndScheds(instanceUuids, activatedViewsAndScheds);
//        			mv.addObject("activatedViewsAndScheds", activatedViewsAndScheds);
        			
        			if(activatedViewsAndScheds.size()>0){
        				List<String> activatedUuids = new ArrayList<String>();
        				for(Map.Entry<String, String> entry : activatedViewsAndScheds.entrySet()){
        					activatedUuids.add(entry.getKey());
        				}
            			apires.setResponse1(activatedUuids);
        			}
    				
    			}
    		}
    	}
    	
    	return apires;
    }

    
    
    @RequestMapping(value="/moduleNodeDelete", method=RequestMethod.GET)
    public @ResponseBody ApiResponse moduleNodeDelete(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.delNodeDetail(ModuleDetail.class, nodeUuid);
    	
    	return apires;
    }
    
    @RequestMapping(value="/instanceViewDelete", method=RequestMethod.GET)
    public @ResponseBody ApiResponse instanceViewDelete(
    		@RequestParam(value = "nodeId", required = false) String viewUuid
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = siteDesignService.delInstanceViewByUuid(viewUuid);
    	
    	//apires = treeService.delNodeDetail(ModuleDetail.class, nodeUuid);
    	
    	return apires;
    }
    
    
    
    @RequestMapping(value="/duplicateAttrGroupSet", method=RequestMethod.GET)
    public ModelAndView duplicateAttrGroupSet(
    		@RequestParam(value = "instanceUuid", required = false) String instanceUuid,
    		@RequestParam(value = "groupUuid", required = false) String groupUuid
		){
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = null;
    	
    	ModuleInstanceInterface moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
    	if(moduleInstance==null){
    		moduleInstance = productService.getEntityDetailByUuid(instanceUuid);
    	}
    	
    	
    	if(loginAccount!=null && moduleInstance!=null && StringUtils.isNotBlank(groupUuid)){
    		
    		boolean modifyPermission = false;
    		// check modifyPermission for ModuleInstance or EntityDetail
    		if(moduleInstance.getClass().equals(ModuleInstance.class)){
    			// if ModuleInstance's ModuleDetail can modify, ModuleInstance can modify too.
    			modifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((ModuleInstance)moduleInstance).getModuleuuid());
    			
    		}else if(moduleInstance.getClass().equals(EntityDetail.class)){
    			modifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((EntityDetail)moduleInstance).getEntityuuid());
    		}
    		
    		if(modifyPermission){
    			
				// hold all targetUuid with target's MetaData
				Map<String, MetaData> targetWithMetaDataMap = new HashMap<String, MetaData>();

				// hold all targets' uuids
				Set<String> metaTargetUuids = new HashSet<String>();
    			
        		Module instance = null;
        		String moduleDetailUuid = null;
        		Organization org = null;
        		if(moduleInstance.getClass().equals(ModuleInstance.class)){
        			instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
        			moduleDetailUuid = ((ModuleInstance)moduleInstance).getModuleuuid();
        			org = accountService.getOrgById(((ModuleInstance)moduleInstance).getOrgid());
        		}else if(moduleInstance.getClass().equals(EntityDetail.class)){
        			instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
        			moduleDetailUuid = ((EntityDetail)moduleInstance).getModuleuuid();
        			org = accountService.getOrgById(((EntityDetail)moduleInstance).getOrganization_id());
        		}
        		
				// get module's group based on g
	    		ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleDetailUuid);
	    		Module module = null;
	    		if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
	    			metaTargetUuids.add(moduleDetailUuid);
	    			module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
	    		}
	    		
        		if(org!=null && instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
        			// find group set by groupUuid
        			for(AttrGroup g : instance.getAttrGroupList()){
        				if(g.getGroupUuid().equals(groupUuid)){
        					
    			    		AttrGroup moduleGroup = null;
			    			if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
			    				for(AttrGroup mg : module.getAttrGroupList()){
			    					if(mg.getGroupUuid().equals(g.getModuleGroupUuid())){
			    						metaTargetUuids.add(mg.getGroupUuid());
			    						
			    						moduleGroup = mg;
			    						break;
			    					}
			    				}
			    			}
    			    		
    			    		if(moduleGroup!=null){
    			    			
    			    			// generate a duplicatedgroup based on moduleGroup
    			    			AttrGroup duplicatedGroup = mapper.map(moduleGroup, AttrGroup.class);
    			    			// reset uuid
    			    			duplicatedGroup.setModuleGroupUuid(duplicatedGroup.getGroupUuid());
    			    			duplicatedGroup.setGroupUuid(UUID.randomUUID().toString());
    			    			// remove all unnecessary info for instance
    			    			duplicatedGroup.setArray(null);
    			    			duplicatedGroup.setGroupName(null);
    			    			
    			    			boolean hasModuleLinkAttr = false;
    			    			boolean hasModuleProductListAttr = false;
    			    			boolean hasModuleMoneyAttr = false;
    			    			// duplicate all attr
    			    			if(moduleGroup.getAttrList()!=null && moduleGroup.getAttrList().size()>0){
    			    				for(ModuleAttribute ma : moduleGroup.getAttrList()){
    			    					
    			    					metaTargetUuids.add(ma.getUuid());
    			    					
    									if(!hasModuleLinkAttr && ma.getClass().equals(ModuleLinkAttribute.class)){
    										hasModuleLinkAttr = true;
    									}
    									if(!hasModuleProductListAttr && ma.getClass().equals(ModuleProductListAttribute.class)){
    										hasModuleProductListAttr = true;
    									}
    									if(!hasModuleMoneyAttr && ma.getClass().equals(ModuleMoneyAttribute.class)){
    										hasModuleMoneyAttr = true;
    									}
    			    					
    			    					ModuleAttribute duplicatedAttr = SitedesignHelper.getDefaultModuleAttr(ma);
    			    					if(duplicatedAttr!=null) duplicatedGroup.addAttr(duplicatedAttr);
    			    				}
    			    			}
    			    			
    	    					// add duplicated group into instance
    	    					instance.addAttrGroup(duplicatedGroup);
    	    					
    	    					String updatedXml = SitedesignHelper.getXmlFromModule(instance);
    	    					
    	    					Long id = null;
    	    					
		    					int instanceCharUsageWith100Multipled = 0;
		    					PaymentPlan paymentplan = paymentService.getPaymentPlanAtPointOfDate(org.getId(), new Date());
		    					if(paymentplan!=null){
		    						int usedXmlChar = updatedXml!=null?updatedXml.length():0;
		    						instanceCharUsageWith100Multipled = paymentService.countCharsUsage(usedXmlChar, paymentplan.getMaxcharsperinstance());
		    					}
    	    					
		    					List<String> errorMsgs = new ArrayList<String>();
		    					
		    					if(instanceCharUsageWith100Multipled>100){
//		    						mv = new ModelAndView("exceptionFragment");
		    						errorMsgs.add("You reach the maximum storage usage for moduleInstance with the new value.");
		    					}else{
		    						
	    	    					if(moduleInstance.getClass().equals(ModuleInstance.class)){
	    		    					((ModuleInstance)moduleInstance).setInstance(updatedXml);
	    		    					id = siteDesignService.saveModuleInstance((ModuleInstance)moduleInstance);
	    	    						
	    	    					}else if(moduleInstance.getClass().equals(EntityDetail.class)){
	    	    						((EntityDetail)moduleInstance).setDetail(updatedXml);
	    	    						id = productService.saveEntityDetail((EntityDetail)moduleInstance);
	    	    					}
		    						
			    					if(id==null){
			    						errorMsgs.add("System can't save the updated instance, try to refresh the page.");
			    					}
		    						
		    					}
		    					
    	    					if(id!=null){
    	    						mv = new ModelAndView("module/instanceAttrGroupSetTemplate");
    	    						
		    						mv.addObject("moduleInstance", moduleInstance);

    	    						
		    						mv.addObject("instanceCharUsageWith100Multipled_attrGroupDupli", instanceCharUsageWith100Multipled);
    	    						
    	        					// add all other informations
    	        					SitedesignHelper.updateAttrGroup(duplicatedGroup, moduleGroup);

    	        					mv.addObject("attrGroup", duplicatedGroup);
    	    						mv.addObject("attrGroupIdx", instance.getAttrGroupList().size()-1);
    	    						
    	    						
    	    						
    	    						// ***** add some special param for attribute:
    	    						if(hasModuleLinkAttr){
    	    							mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
    	    							mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
    	    						}
    	    						// for page list
    	    						if(hasModuleProductListAttr){
    	    							List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
    	    							List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
    	    							List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
    	    							List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
    	    							if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
    	    								for(PageDetail d : orgDeskPageDetails){
    	    									orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
    	    								}
    	    							}
    	    							if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
    	    								for(PageDetail d : orgMobilePageDetails){
    	    									orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
    	    								}
    	    							}
    	    							mv.addObject("orgDeskPages", orgDeskPages);
    	    							mv.addObject("orgMobilePages", orgMobilePages);
    	    						}
    	    						if(hasModuleMoneyAttr){
    	    							mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
    	    						}
    	    					}else{
		    						mv = new ModelAndView("exceptionFragment");
		    						StringBuilder errorMsgsInString = new StringBuilder();
		    						if(errorMsgs.size()>0){
		    							for(String e : errorMsgs){
		    								errorMsgsInString.append(e).append("<br/>");
		    							}
		    						}
		    						mv.addObject("ex", errorMsgsInString);
    	    					}
    			    		}
        					
        					break;
        				}
        			}
        			
    				// find all metaData if metaTargetUuids not empty
    				if(metaTargetUuids.size()>0){
    					for(String targetUuid : metaTargetUuids){
    						ModuleMeta moduleMeta = siteDesignService.getModuleMetaByTargetUuid(targetUuid);
    						if(moduleMeta!=null){
    							MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
    							if(metaData!=null){
    								targetWithMetaDataMap.put(targetUuid, metaData);
    							}
    						}
    					}
    					
    					if(targetWithMetaDataMap.size()>0){
    						mv.addObject("targetWithMetaDataMap", targetWithMetaDataMap);
    					}
    				}
        			
        		}
    			
    		}
    		
    	}
    	
    	
    	return mv;
    }
    
    
    @RequestMapping(value="/duplicateInstanceAttr", method=RequestMethod.GET)
    public ModelAndView duplicateInstanceAttr(
    		@RequestParam(value = "instanceUuid", required = false) String instanceUuid,
    		@RequestParam(value = "groupUuid", required = false) String groupUuid,
    		@RequestParam(value = "attrUuid", required = false) String attrUuid
		){
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = null;
    	
    	ModuleInstanceInterface moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
    	if(moduleInstance==null){
    		moduleInstance = productService.getEntityDetailByUuid(instanceUuid);
    	}
    	
    	if(loginAccount!=null && moduleInstance!=null && StringUtils.isNotBlank(groupUuid)){
    		
			// hold all targetUuid with target's MetaData
			Map<String, MetaData> targetWithMetaDataMap = new HashMap<String, MetaData>();

			// hold all targets' uuids
			Set<String> metaTargetUuids = new HashSet<String>();
    		
    		boolean modifyPermission = false;
    		// check modifyPermission for ModuleInstance or EntityDetail
    		if(moduleInstance.getClass().equals(ModuleInstance.class)){
    			// if ModuleInstance's ModuleDetail can modify, ModuleInstance can modify too.
    			modifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((ModuleInstance)moduleInstance).getModuleuuid());
    			
    		}else if(moduleInstance.getClass().equals(EntityDetail.class)){
    			modifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, ((EntityDetail)moduleInstance).getEntityuuid());
    		}
    		
    		
    		if(modifyPermission){
        		Module instance = null;
        		String moduleDetailUuid = null;
        		Organization org = null;
        		if(moduleInstance.getClass().equals(ModuleInstance.class)){
        			instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
        			moduleDetailUuid = ((ModuleInstance)moduleInstance).getModuleuuid();
        			org = accountService.getOrgById(((ModuleInstance)moduleInstance).getOrgid());
        		}else if(moduleInstance.getClass().equals(EntityDetail.class)){
        			instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
        			moduleDetailUuid = ((EntityDetail)moduleInstance).getModuleuuid();
        			org = accountService.getOrgById(((EntityDetail)moduleInstance).getOrganization_id());
        		}
        		
	    		ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleDetailUuid);
	    		Module module = null;
	    		if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
	    			module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
	    			
	    			metaTargetUuids.add(moduleDetailUuid);
	    		}

        		
        		if(org!=null && instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
        			// find group set by groupUuid
        			for(AttrGroup g : instance.getAttrGroupList()){
        				if(g.getGroupUuid().equals(groupUuid)){
        					
        					if(g.getAttrList()!=null && g.getAttrList().size()>0){
        						for(ModuleAttribute a : g.getAttrList()){
        							if(a.getUuid().equals(attrUuid)){
        								// get module's attr based on a
        					    		ModuleAttribute moduleAttr = null;
    					    			if(module!=null && module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
    					    				for(AttrGroup mg : module.getAttrGroupList()){
    					    					if(mg.getGroupUuid().equals(g.getModuleGroupUuid())){
    					    						
    					    						metaTargetUuids.add(mg.getGroupUuid());
    					    						
    					    						if(mg.getAttrList()!=null && mg.getAttrList().size()>0){
    					    							for(ModuleAttribute ma : mg.getAttrList()){
    					    								if(ma.getUuid().equals(a.getModuleAttrUuid())){
    					    									metaTargetUuids.add(ma.getUuid());
    					    									
    					    									moduleAttr = ma;
    					    									
    					    									break;
    					    								}
    					    							}
    					    						}
    					    						
    					    						break;
    					    					}
    					    				}
    					    			}
        					    		
        					    		if(moduleAttr!=null){
        					    			
        					    			// generate a duplicatedAttr based on moduleAttr
        					    			ModuleAttribute duplicatedAttr = SitedesignHelper.getDefaultModuleAttr(moduleAttr);
        					    			g.addAttr(duplicatedAttr);
        			    					
        			    					String updatedXml = SitedesignHelper.getXmlFromModule(instance);
        			    					
        			    					Long id = null;
        			    					
        			    					int instanceCharUsageWith100Multipled = 0;
        			    					PaymentPlan paymentplan = paymentService.getPaymentPlanAtPointOfDate(org.getId(), new Date());
        			    					if(paymentplan!=null){
        			    						int usedXmlChar = updatedXml!=null?updatedXml.length():0;
        			    						instanceCharUsageWith100Multipled = paymentService.countCharsUsage(usedXmlChar, paymentplan.getMaxcharsperinstance());
        			    					}
        			    					
        			    					List<String> errorMsgs = new ArrayList<String>();
        			    					
        			    					if(instanceCharUsageWith100Multipled>100){
//        			    						mv = new ModelAndView("exceptionFragment");
        			    						errorMsgs.add("You reach the maximum storage usage for moduleInstance with the new value.");
        			    					}else{
            			    					
            			    					if(moduleInstance.getClass().equals(ModuleInstance.class)){
                			    					((ModuleInstance)moduleInstance).setInstance(updatedXml);
            			    						id = siteDesignService.saveModuleInstance((ModuleInstance)moduleInstance); 
            			    					}else if(moduleInstance.getClass().equals(EntityDetail.class)){
            			    						((EntityDetail)moduleInstance).setDetail(updatedXml);
            			    						id = productService.saveEntityDetail((EntityDetail)moduleInstance);
            			    					}
            			    					if(id==null){
            			    						errorMsgs.add("System can't save the updated instance, try to refresh the page.");
            			    					}
            			    					
        			    					}
        			    					
        			    					
        			    					if(id!=null){
        			    						mv = new ModelAndView("module/InstanceAttrGeneral");
        			    						
        			    						mv.addObject("moduleInstance", moduleInstance);
        			    						
        			    						mv.addObject("instanceCharUsageWith100Multipled_attrDupli", instanceCharUsageWith100Multipled);
        			    						
        			        					// add all other informations
        			        					SitedesignHelper.updateAttr(duplicatedAttr, moduleAttr);

        			        					mv.addObject("moduleAttr", duplicatedAttr);
        			        					mv.addObject("attrGroup", g);
        			    						mv.addObject("attrIdx", g.getAttrList().size()-1);
        			    						
        			    						
        			    						
        			    						
        			    						
        			    						// ***** add some special param for attribute:
        			    						if(moduleAttr.getClass().equals(ModuleLinkAttribute.class)){
        			    							mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
        			    							mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
        			    						}
        			    						// for page list
        			    						if(moduleAttr.getClass().equals(ModuleProductListAttribute.class)){
        			    							List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
        			    							List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
        			    							List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
        			    							List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
        			    							if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
        			    								for(PageDetail d : orgDeskPageDetails){
        			    									orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
        			    								}
        			    							}
        			    							if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
        			    								for(PageDetail d : orgMobilePageDetails){
        			    									orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
        			    								}
        			    							}
        			    							mv.addObject("orgDeskPages", orgDeskPages);
        			    							mv.addObject("orgMobilePages", orgMobilePages);
        			    						}
        			    						if(moduleAttr.getClass().equals(ModuleMoneyAttribute.class)){
        			    							mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
        			    						}
        			    						
        			    					}else{
        			    						mv = new ModelAndView("exceptionFragment");
        			    						StringBuilder errorMsgsInString = new StringBuilder();
        			    						if(errorMsgs.size()>0){
        			    							for(String e : errorMsgs){
        			    								errorMsgsInString.append(e).append("<br/>");
        			    							}
        			    						}
        			    						mv.addObject("ex", errorMsgsInString);
        			    						
        			    					}
        					    		}
        								
        								break;
        							}
        						}
        					}
        					
        					break;
        				}
        			}
        			
    				// find all metaData if metaTargetUuids not empty
    				if(metaTargetUuids.size()>0){
    					for(String targetUuid : metaTargetUuids){
    						ModuleMeta moduleMeta = siteDesignService.getModuleMetaByTargetUuid(targetUuid);
    						if(moduleMeta!=null){
    							MetaData metaData = SitedesignHelper.getMetaDataFromXml(moduleMeta.getMetadata());
    							if(metaData!=null){
    								targetWithMetaDataMap.put(targetUuid, metaData);
    							}
    						}
    					}
    					
    					if(targetWithMetaDataMap.size()>0){
    						mv.addObject("targetWithMetaDataMap", targetWithMetaDataMap);
    					}
    				}
        			
        			
        		}
    		}
    		
    	}
    	
    	
    	return mv;
    }
        
    @RequestMapping(value="/getContainerModuleScheduleConfig", method=RequestMethod.GET)
    public ModelAndView getContainerModuleScheduleConfig(
    		@RequestParam(value = "org", required = false) String orgUuid,
    		@RequestParam(value = "containerId", required = false) String containerUuid,
    		@RequestParam(value = "schedId", required = false) String schedUuid
		) {
    	AccountDto loginaccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = null;
    	
    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerUuid);
    	
		if(containerDetail!=null && StringUtils.isNotBlank(schedUuid)){
	    	mv = new ModelAndView("containerModuleScheduleConfig");
	    	
	    	// find page's modify permission
	    	boolean pageModifyPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
	    	mv.addObject("pageModifyPermission", pageModifyPermission);
	    	
			mv.addObject("priorityLevels", ContainerModuleSchedule.PriorityLevel.values());
			
			mv.addObject("container", containerDetail);
			
			// for existing sched
			ContainerModuleSchedule sched = siteDesignService.getContainerModuleScheduleByUuid(schedUuid);
			mv.addObject("sched", sched);
			
			if(sched!=null){
				ModuleDetail detail = siteDesignService.getModuleDetailByUuid(sched.getModuleuuid());
				if(detail!=null){
					mv.addObject("moduleDetail", detail);
					
					if(StringUtils.isNotBlank(detail.getXml())){
						Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
						mv.addObject("module", module);
					}
				}
			}
			
			
			
		}else{
			mv = new ModelAndView("error_general");
		}
		
		return mv;
    	
    }
    
    
    
    @RequestMapping(value="/editContainerDefaultModule", method=RequestMethod.POST)
    public @ResponseBody ApiResponse editContainerDefaultModule(
    		@RequestParam(value = "containerUuid", required = false) String containerUuid,
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	
    	
    	apires = siteDesignService.updateContainerDetailByFieldnameValue(containerUuid, "moduleuuid", moduleUuid);
    	
    	
    	
    	
    	return apires;
    }
    
    
    @RequestMapping(value="/getContainerDefaultModuleSetup", method=RequestMethod.GET)
    public ModelAndView getContainerDefaultModuleSetup(
    		@RequestParam(value = "containerId", required = false) String containerUuid
		) {
    	ModelAndView mv = null;
    	
    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerUuid);
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	if(containerDetail!=null && loginAccount!=null){
	    	mv = new ModelAndView("containerDefaultModuleSetup");
	    	
	    	// find page permission
	    	boolean pageModifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
	    	mv.addObject("pageModifyPermissionAllow", pageModifyPermissionAllow);
	    	
	    	mv.addObject("container", containerDetail);
	    	
	    	if(StringUtils.isNotBlank(containerDetail.getModuleuuid())){
	    		ModuleDetail detail = siteDesignService.getModuleDetailByUuid(containerDetail.getModuleuuid());
	    		
	    		if(detail!=null){
	    			mv.addObject("moduleDetail", detail);
	    			
	    			Module module = SitedesignHelper.getModuleFromXml(detail.getXml());
	    			mv.addObject("module", module);
	    		}
	    		
	    	}

    	}else{
    		mv = new ModelAndView("error_general");
    	}
    	
    	
    	return mv;
    }
    	
//    containerDetailSetup
    @RequestMapping(value="/containerDetailSetup", method=RequestMethod.GET)
    public ModelAndView containerDetailSetup(
    		@RequestParam(value = "containerId", required = false) String containerUuid
		) {
    	ModelAndView mv = new ModelAndView("containerDetailSetup");

    	
    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerUuid);
    	if(containerDetail!=null){
    		mv.addObject("containerDetail", containerDetail);
    		// get default module info
    		if(StringUtils.isNotBlank(containerDetail.getModuleuuid())){
    			ModuleDetail defaultModuleDetail = siteDesignService.getModuleDetailByUuid(containerDetail.getModuleuuid());
    			if(defaultModuleDetail!=null){
    				mv.addObject("defaultModuleDetailName", defaultModuleDetail.getPrettyname());
    			}
    		}
    		
    		
    	}
    	
    	return mv;
    }
    
    
    @RequestMapping(value="/getModuleInstanceScheduleConfig", method=RequestMethod.GET)
    public ModelAndView getModuleInstanceScheduleConfig(
    		@RequestParam(value = "org", required = false) String orgUuid,
    		@RequestParam(value = "containerModuleSchedId", required = false) String containerModuleSchedUuid,
    		@RequestParam(value = "schedId", required = false) String schedUuid
		) {
    	AccountDto loginaccount = accountService.getCurrentAccount();
    
    	ModelAndView mv = null;
    	
    	ContainerModuleSchedule containerModuleSchedule = siteDesignService.getContainerModuleScheduleByUuid(containerModuleSchedUuid);
    	
		if(containerModuleSchedule!=null && StringUtils.isNotBlank(schedUuid)){
			
			// check the module has instances?
			ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(containerModuleSchedule.getModuleuuid());
			List<ModuleInstance> moduleInstances = siteDesignService.findModuleInstancesByModuleUuid(containerModuleSchedule.getModuleuuid());
			if(moduleInstances!=null && moduleInstances.size()>0){
		    	mv = new ModelAndView("moduleInstanceScheduleConfig");
		    	
		    	
		    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerModuleSchedule.getContaineruuid());
		    	
		    	if(containerDetail!=null){
		    		
			    	// add page modify permission to the mv
		    		boolean pageModifyPermission = permissionService.isPermissionAllowed(loginaccount.getId(), Permission.Type.modify, containerDetail.getPageuuid());
		    		mv.addObject("pageModifyPermission", pageModifyPermission);
			    	
			    	// for the tree
			    	mv.addObject("moduleDetail", moduleDetail);
			    	mv.addObject("moduleInstances", moduleInstances);
			    	
			    	// for the form: priority
					mv.addObject("priorityLevels", ContainerModuleSchedule.PriorityLevel.values());
					
					// new schedule is for this containerModuleSchedule
					mv.addObject("containerModuleSchedule", containerModuleSchedule);
					
					// for existing sched
					ModuleInstanceSchedule sched = siteDesignService.getModuleInstanceScheduleByUuid(schedUuid);
					mv.addObject("sched", sched);
					
					if(sched!=null){
						ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(sched.getModuleinstanceuuid());
						if(moduleInstance!=null && moduleDetail!=null){
							mv.addObject("moduleInstance", moduleInstance);
							
							Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
							Module instance = SitedesignHelper.getModuleFromXml(moduleInstance.getInstance());
							
							if(module!=null && instance!=null){
								SitedesignHelper.updateModuleInstanceByModule(module, instance);
								mv.addObject("instance", instance);
							}
						}
						
					}
					
					// find all instances are using or are scheduled to use
					Map<String, Integer> instanceUsageMap = new HashMap<String, Integer>();
					for(ModuleInstance mi : moduleInstances){
						List<ModuleInstanceSchedule> moduleInstanceSchedules = siteDesignService.findModuleInstanceSchedulesByInstanceUuid(mi.getModuleinstanceuuid());
						if(moduleInstanceSchedules!=null && moduleInstanceSchedules.size()>0){
							instanceUsageMap.put(mi.getModuleinstanceuuid(), moduleInstanceSchedules.size());
						}else{
							instanceUsageMap.put(mi.getModuleinstanceuuid(), 0);
						}
					}
					mv.addObject("instanceUsageMap", instanceUsageMap);
					
		    	}
				
			}else{
				mv = new ModelAndView("error_general");
				List<String> errorList = new ArrayList<String>();
				errorList.add("Module (name: "+moduleDetail.getPrettyname()+", id: "+moduleDetail.getModuleuuid()+") doesn't have any instance defined! You can goto module section to add instance(s) for the module.");
				mv.addObject("errorList", errorList);
			}
			
			
		}else{
			mv = new ModelAndView("error_general");
			List<String> errorList = new ArrayList<String>();
			errorList.add("System can't find containerModuleSchedule by containerModuleScheduleId: "+containerModuleSchedUuid+" or schedUuid ("+schedUuid+") has problem.");
			mv.addObject("errorList", errorList);
		}
		
		return mv;
    	
    }
    
    @RequestMapping(value="/pageNodeDelete", method=RequestMethod.GET)
    public @ResponseBody ApiResponse pageNodeDelete(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "relType", required = false) String relType // all defined in JsTreeNode.NodeType
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	JsTreeNode.NodeType nodetype = JsTreeNode.NodeType.fromSysName(relType);
    	if(StringUtils.isNotBlank(nodeUuid) && nodetype!=null){
    		
    		if(nodetype.equals(JsTreeNode.NodeType.moduleInstanceSchedule)){ // for miSched
    			siteDesignService.delModuleInstanceScheduleByUuid(nodeUuid);
    			apires.setResponse1(nodeUuid);
    			apires.setSuccess(true);
    		}else if(nodetype.equals(JsTreeNode.NodeType.containerModuleSchedule)){ // for cmSched
    			ContainerModuleSchedule cmSched = siteDesignService.getContainerModuleScheduleByUuid(nodeUuid);
    			
    			// find all moduleInstanceSchedules for this containerModuleSchedule
    			List<ModuleInstanceSchedule> miScheds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(nodeUuid);
    			if(miScheds!=null && miScheds.size()>0){
    				for(ModuleInstanceSchedule mis : miScheds){
    					siteDesignService.delModuleInstanceScheduleByUuid(mis.getUuid());
    				}
    			}
    			
    			siteDesignService.delContainerModuleScheduleByUuid(nodeUuid);
    			apires.setResponse1(nodeUuid);
    			apires.setSuccess(true);
    			// extra container info will be send back
    			if(cmSched!=null){
        			apires.setResponse2(cmSched.getContaineruuid());
    			}
    		}else if(nodetype.equals(JsTreeNode.NodeType.container)){ // for container
    			return treeService.delNodeDetail(ContainerDetail.class, nodeUuid);
    		}else if(nodetype.equals(JsTreeNode.NodeType.leafPage)){ // for page
    			return treeService.delNodeDetail(PageDetail.class, nodeUuid);
    			
    		}else if(nodetype.equals(JsTreeNode.NodeType.folder)){ // for folder
    			return treeService.delNodeDetail(PageDetail.class, nodeUuid);
    		}
    	}else{
    		apires.setResponse1("nodeId("+nodeUuid+") and relType("+relType+") can't be empty, and relType should be defined in JsTreeNode.NodeType.");
    	}
    	
//    	apires = treeService.delNodeDetail(ModuleDetail.class, nodeUuid);
    	
    	return apires;
    }
    
    
    //?pageid=${pageuuid}&fieldName=css
    
    
    @RequestMapping(value="/ajaxPageDetailFieldValue", method=RequestMethod.GET)
    public ModelAndView ajaxPageDetail(
    		@RequestParam(value = "pageid", required = false) String pageUuid,
    		@RequestParam(value = "fieldName", required = false) String fname
		) {
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	ModelAndView mv = null;
    	PageDetail pagedetail = siteDesignService.getPageDetailByUuid(pageUuid);
    	if(pagedetail!=null && StringUtils.isNotBlank(fname)){
    		
    		Organization org = accountService.getOrgById(pagedetail.getOrganization_id());
    		OrgMeta orgMeta = accountService.getOrgMetaByOrgUuid(org.getOrguuid());
    		
    		mv = new ModelAndView("fieldValueHtml");
    		mv.addObject("uuid", pagedetail.getPageuuid());
    		
    		// find page's modify permission
//    		boolean pageReadPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, pagedetail.getPageuuid());
    		boolean pageModifyPermission = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, pagedetail.getPageuuid());
    		mv.addObject("pageModifyPermission", pageModifyPermission);
    		
    		
    		String fieldType = null;
    		String fieldName = fname;
    		String fieldTitle = null;
    		String fieldValue = null;
    		String jsDomAction = null;
    		
    		if(fname.equalsIgnoreCase("url")){
    			fieldType = "input";
    			fieldValue = pagedetail.getUrl();
    			fieldTitle = "URL";
    			jsDomAction = "domReady_savePageValue";
    			
    			// page location:
    			Map<String, String> links = new HashMap<String, String>();
    			
    			// get long link
    			if(pagedetail.getType().equals(PageDetail.Type.Desktop.getCode())){
    				String link = "http://"+applicationConfig.getHostName().trim()+"/getPage/org/"+org.getOrguuid()+"/pageurl/"+pagedetail.getUrl();
    				links.put(applicationConfig.getHostName(), link);
    			}
//    			else if(pagedetail.getType().equals(PageDetail.Type.Mobile.getCode())){
//    				link = "/getMobilePage/org/"+org.getOrguuid()+"/pageurl/"+pagedetail.getUrl();
//    			}
    			
    			// get short links
    			if(StringUtils.isNotBlank(orgMeta.getDomains())){
                	List<String> valifiedDomains = new ArrayList<String>();
            		String[] domainnamesWithStatus = orgMeta.getDomains().split(",");
//            		int count = 0;
            		for(String dn : domainnamesWithStatus){
//            			count++;
            			if(dn.indexOf("[")<0){
            				valifiedDomains.add(dn);
            			}
            		}
            		if(valifiedDomains.size()>0){
            			for(String vd : valifiedDomains){
            				String[] domainSegments = vd.split("\\.");
            				StringBuilder link = new StringBuilder();
            				link.append("http://");
            				StringBuilder linkDomain = new StringBuilder();
            				if(domainSegments.length==2){ // like "abc.com" -> need to add "www."
            					linkDomain.append("www.");
            				}
            				linkDomain.append(vd);
            				link.append(linkDomain).append("/page/").append(pagedetail.getUrl());
            				links.put(linkDomain.toString(), link.toString());
            			}
            		}
    			}
    			
    			mv.addObject("pagelinks", links);
    		}else if(fname.equalsIgnoreCase("css")){
    			PageMeta pagemeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
    			if(pagemeta!=null){
        			fieldType = "csstextarea";
        			fieldValue = pagemeta.getCss();
        			fieldTitle = "Page Css";
        			jsDomAction = "savePageCssHead";
    			}
    		}else if(fname.equalsIgnoreCase("pagetitle")){
    			PageMeta pagemeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
    			if(pagemeta!=null){
        			fieldType = "input";
        			fieldValue = pagemeta.getTitle();
        			fieldTitle = "Page Title";
        			jsDomAction = "domReady_savePageValue";
    			}
    		}else if(fname.equalsIgnoreCase("defaultcss")){
    			
    			// get default css choose:
    			mv.addObject("defaultCssSelections", PageMeta.DefaultCss.values());
    			
    			PageMeta pagemeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
    			fieldType = "defaultcss";
    			fieldValue = PageMeta.DefaultCss.previewMode.name();
    			if(pagemeta!=null && StringUtils.isNotBlank(pagemeta.getDefaultcss())){
    				PageMeta.DefaultCss defaultCss = PageMeta.DefaultCss.getDefaultCssForCode(pagemeta.getDefaultcss());
    				if(defaultCss!=null){
    					fieldValue = defaultCss.name();
    				}
    			}
    			
    			fieldTitle = "Default Css";
    			jsDomAction = "domReady_savePageValue";
    		}else if(fname.equalsIgnoreCase("pagehead")){
    			PageMeta pagemeta = siteDesignService.getPageMetaByPageUuid(pageUuid);
    			if(pagemeta!=null){
    				fieldType = "pagehead";
    				fieldValue = pagemeta.getHeadcontent();
    				fieldTitle = "Html Head";
    				jsDomAction = "savePageCssHead";
    			}
    			
    		}
    		
    		
    		mv.addObject("fieldType", fieldType);
    		mv.addObject("fieldName", fieldName); //
    		mv.addObject("fieldTitle", fieldTitle); // page display title
    		mv.addObject("fieldValue", fieldValue);
    		mv.addObject("jsDomAction", jsDomAction);
    		
    		
    	}
    	
    	return mv;
    }
    
    /**
     * used for highlighting the current instanceview and current viewschedule for instance
     * @param instanceUuid
     * @return
     */
    @RequestMapping(value="/getCurrentViewForInstance", method=RequestMethod.GET)
    public @ResponseBody ApiResponse getCurrentViewForInstance(
    		@RequestParam(value = "instanceId", required = false) String instanceUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	InstanceView currentInstanceView = siteDesignService.getCurrentInstanceView(instanceUuid);
    	if(currentInstanceView!=null){

    		apires.setSuccess(true);
    		apires.setResponse1(currentInstanceView);
    		
    		// get all schedules for the view
    		List<InstanceViewSchedule> schedules = siteDesignService.findInstanceViewSchedulesByInstanceViewUuid(currentInstanceView.getInstanceviewuuid());
    		if(schedules!=null && schedules.size()>0){
    			InstanceViewSchedule currentSchedule = (InstanceViewSchedule)siteDesignService.getCurrentSchedule(schedules, new Date());
    			if(currentSchedule!=null){
    				apires.setResponse2(currentSchedule);
    			}
    		}
    		
    	}else{
    		apires.setResponse1("No current view is found for instance: "+instanceUuid);
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/getCurrentScheduleForContainer", method=RequestMethod.GET)
    public @ResponseBody ApiResponse getCurrentScheduleForContainer(
    		@RequestParam(value = "containerId", required = false) String containerUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);

    	Date now = new Date();
    	
    	ContainerDetail containerDetail = siteDesignService.getContainerByUuid(containerUuid);
    	List<ContainerModuleSchedule> containerModuleScheds = siteDesignService.findContainerModuleSchedulesByContainerUuid(containerUuid);
    	if(containerDetail!=null && containerModuleScheds!=null && containerModuleScheds.size()>0){

    		ContainerModuleSchedule currentContainerModuleSchedule = null;
        	ModuleInstanceSchedule currentModuleInstanceSchedule = null;
        	
    		currentContainerModuleSchedule = (ContainerModuleSchedule)siteDesignService.getCurrentSchedule(containerModuleScheds, now);
    		
    		if(currentContainerModuleSchedule!=null){
    			List<ModuleInstanceSchedule> moduleInstanceScheds = siteDesignService.findModuleInstanceSchedulesByContainerModuleScheduleUuid(currentContainerModuleSchedule.getUuid());
    			if(moduleInstanceScheds!=null && moduleInstanceScheds.size()>0){
    				currentModuleInstanceSchedule = (ModuleInstanceSchedule)siteDesignService.getCurrentSchedule(moduleInstanceScheds, now);
    			}
    		}
    		
    		if(currentContainerModuleSchedule!=null || currentModuleInstanceSchedule!=null){
    			apires.setSuccess(true);
    			
				JSONObject resp1 = new JSONObject();
    			if(currentContainerModuleSchedule!=null){
    				resp1.put("currentContainerModuleSchedule", currentContainerModuleSchedule.getUuid());
    			}
    			if(currentModuleInstanceSchedule!=null){
    				resp1.put("currentModuleInstanceSchedule", currentModuleInstanceSchedule.getUuid());
    			}
    			apires.setResponse1(resp1);
    			
    			
				String info = messageFromPropertiesService.getMessageSource().getMessage("currentUsedSched4container", 
						new Object[] {containerDetail.getPrettyname(),
							currentContainerModuleSchedule!=null?currentContainerModuleSchedule.getSchedulename():"",
							currentModuleInstanceSchedule!=null?currentModuleInstanceSchedule.getSchedulename():""}, Locale.US);
    			apires.setResponse2(info);
    			
    		}else{
    			apires.setResponse1("No any current runing schedule found for this container at "+now.toString());
    		}
    	}else{
    		apires.setResponse1("No containerModuleSchedule found for this container.");
    	}
    	
    	return apires;
    }
    	
    @RequestMapping(value="/getModuleDetailInfo", method=RequestMethod.GET)
    public ModelAndView getModuleDetailInfo(
    		@RequestParam(value = "moduleid", required = false) String moduleUuid
		) {
    	
    	Date now = new Date();
    	ModelAndView mv = null;
    	
		mv = new ModelAndView("moduleDetailInfo");
		
		List<ContainerModuleSchedule> containerModuleSchedules = siteDesignService.findContainerModuleSchedulesByModuleUuid(moduleUuid);
		
		if(containerModuleSchedules!=null && containerModuleSchedules.size()>0){
			mv.addObject("totalSchedUsed", containerModuleSchedules.size());
			
			// the map to hold key: page+container, value: list of schedules
			Map<String, List<ContainerModuleSchedule>> containerWithSchedulesMap = new HashMap<String, List<ContainerModuleSchedule>>();
			Set<String> keys = new HashSet<String>(); // the list including all keys for sorting
			for(ContainerModuleSchedule cms : containerModuleSchedules){
				ContainerDetail containerDetail = siteDesignService.getContainerByUuid(cms.getContaineruuid());
				PageDetail pageDetail = siteDesignService.getPageDetailByUuid(containerDetail.getPageuuid());
				
				String key = "page \""+pageDetail.getPrettyname()+"\" in container \""+containerDetail.getPrettyname()+"\"<span class='displaynone'>"+containerDetail.getContaineruuid()+"</span>";
				keys.add(key);
				
				if(containerWithSchedulesMap.get(key)!=null){
					containerWithSchedulesMap.get(key).add(cms);
				}else{
					List<ContainerModuleSchedule> cmslist = new ArrayList<ContainerModuleSchedule>();
					cmslist.add(cms);
					containerWithSchedulesMap.put(key, cmslist);
				}
				
			}
			
			// sorted keys
			List<String> keylist = new ArrayList<String>(keys);
			Collections.sort(keylist);
			mv.addObject("sortedKeys", keylist);
			mv.addObject("containerWithSchedulesMap", containerWithSchedulesMap);
			
			// get the current schedule and outdated schedule for each container
			Map<String, Boolean> scheduleStatusMap = new HashMap<String, Boolean>();// hold scheduleuuid with it's status: currentUsing-true, outdated-false, other-null
			if(containerWithSchedulesMap.size()>0){
				for(Map.Entry<String, List<ContainerModuleSchedule>> en : containerWithSchedulesMap.entrySet()){
					if(en.getValue()!=null && en.getValue().size()>0){
						
						// get container's moduleSchedules
						List<ContainerModuleSchedule> containerModuleScheds = siteDesignService.findContainerModuleSchedulesByContainerUuid(en.getValue().get(0).getContaineruuid());
						
						
						// get current schedule for container
						ContainerModuleSchedule currentSchedule = (ContainerModuleSchedule)siteDesignService.getCurrentSchedule(containerModuleScheds, now);
						// get outdated schedules for container:
						List<ScheduleInterface> outdatedSchedules = siteDesignService.getOutDatedSchedule(containerModuleScheds, now);
						
						if(currentSchedule!=null){
							scheduleStatusMap.put(currentSchedule.getUuid(), Boolean.TRUE);
						}
						
						if(outdatedSchedules!=null && outdatedSchedules.size()>0){
							for(ScheduleInterface cms : outdatedSchedules){
								scheduleStatusMap.put(cms.getUuid(), Boolean.FALSE);
							}
						}
					}
				}
			}
			mv.addObject("scheduleStatusMap", scheduleStatusMap);
			
		}
    	return mv;
    }

    @RequestMapping(value="/getModuleInstanceInfo", method=RequestMethod.GET)
    public ModelAndView getModuleInstanceInfo(
    		@RequestParam(value = "instanceid", required = false) String instanceUuid
		) {
    	ModelAndView mv = null;
    	
    	Date now = new Date();
    	
		mv = new ModelAndView("moduleInstanceInfo");
		List<ModuleInstanceSchedule> moduleInstanceSchedules = siteDesignService.findModuleInstanceSchedulesByInstanceUuid(instanceUuid);
		
//		List<ModuleInstanceSchedule> moduleInstanceSchedules = siteDesignDao.findModuleInstanceSchedulesByInstanceUuid(instanceUuid);
		
		if(moduleInstanceSchedules!=null && moduleInstanceSchedules.size()>0){
			mv.addObject("totalSchedUsed", moduleInstanceSchedules.size());
			
			
			// the map to hold key: page+container, value: list of schedules
			Map<String, List<ModuleInstanceSchedule>> containerWithSchedulesMap = new HashMap<String, List<ModuleInstanceSchedule>>();
			Set<String> keys = new HashSet<String>(); // the list including all keys for sorting
			for(ModuleInstanceSchedule cms : moduleInstanceSchedules){
				ContainerDetail containerDetail = siteDesignService.getContainerByUuid(cms.getContaineruuid());
				PageDetail pageDetail = siteDesignService.getPageDetailByUuid(containerDetail.getPageuuid());
				
				String key = "page \""+pageDetail.getPrettyname()+"\" in container \""+containerDetail.getPrettyname()+"\"<span class='displaynone'>"+containerDetail.getContaineruuid()+"</span>";
				keys.add(key);
				
				if(containerWithSchedulesMap.get(key)!=null){
					containerWithSchedulesMap.get(key).add(cms);
				}else{
					List<ModuleInstanceSchedule> cmslist = new ArrayList<ModuleInstanceSchedule>();
					cmslist.add(cms);
					containerWithSchedulesMap.put(key, cmslist);
				}
				
			}
			
			// sorted keys
			List<String> keylist = new ArrayList<String>(keys);
			Collections.sort(keylist);
			mv.addObject("sortedKeys", keylist);
			mv.addObject("containerWithSchedulesMap", containerWithSchedulesMap);
			
			// get the current schedule and outdated schedule for each container
			Map<String, Boolean> scheduleStatusMap = new HashMap<String, Boolean>();// hold scheduleuuid with it's status: currentUsing-true, outdated-false, other-null
			if(containerWithSchedulesMap.size()>0){
				for(Map.Entry<String, List<ModuleInstanceSchedule>> en : containerWithSchedulesMap.entrySet()){
					if(en.getValue()!=null && en.getValue().size()>0){
						
						// get current schedule for container
						ModuleInstanceSchedule currentSchedule = (ModuleInstanceSchedule)siteDesignService.getCurrentSchedule(en.getValue(), now);
						// get outdated schedules for container:
						List<ScheduleInterface> outdatedSchedules = siteDesignService.getOutDatedSchedule(en.getValue(), now);
						
						if(currentSchedule!=null){
							scheduleStatusMap.put(currentSchedule.getUuid(), Boolean.TRUE);
						}
						
						if(outdatedSchedules!=null && outdatedSchedules.size()>0){
							for(ScheduleInterface cms : outdatedSchedules){
								scheduleStatusMap.put(cms.getUuid(), Boolean.FALSE);
							}
						}
					}
				}
			}
			mv.addObject("scheduleStatusMap", scheduleStatusMap);
			
		}
    	
    	return mv;
    }
    
    
    
    @RequestMapping(value="/howToGetValueInView", method=RequestMethod.GET)
    public ModelAndView howToGetValueInView(
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid,
    		@RequestParam(value = "moduleAttrGroupUuid", required = false) String moduleAttrGroupUuid,
    		@RequestParam(value = "moduleAttrUuid", required = false) String moduleAttrUuid
		) {
    	ModelAndView mv = new ModelAndView("howToGetValueInView");
    	
    	ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	if(moduleDetail!=null){
        	Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
        	if(module!=null){
            	AttrGroup attrGroup = module.getGroupByUuid(moduleAttrGroupUuid);
            	if(attrGroup!=null){
            		ModuleAttribute attr = attrGroup.getModuleAttributeByUuid(moduleAttrUuid);
            		if(attr!=null){
            			mv.addObject("attrGroup", attrGroup);
            			mv.addObject("attr", attr);
            			mv.addObject("moduleDetail", moduleDetail);
            		}
            		
            	}
        	}
    	}
    	
    	return mv;
    }
    
	@RequestMapping(value="/howToGetValueInView_v2", method = RequestMethod.GET)
    public @ResponseBody String howToGetValueInView_v2(
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid,
    		@RequestParam(value = "moduleAttrGroupUuid", required = false) String moduleAttrGroupUuid,
    		@RequestParam(value = "moduleAttrUuid", required = false) String moduleAttrUuid,
    		@RequestParam(value = "howToDocName", required = false) String howToDocName
    	) {
		
		
    	ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	if(moduleDetail!=null){
        	Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
        	if(module!=null){
            	AttrGroup attrGroup = module.getGroupByUuid(moduleAttrGroupUuid);
            	if(attrGroup!=null){
            		ModuleAttribute attr = attrGroup.getModuleAttributeByUuid(moduleAttrUuid);
            		
            		if(attr!=null){
            			StringBuilder sampleCodeTitle = new StringBuilder();
            			if(attr.getClass().equals(ModuleTextAttribute.class)){
            				sampleCodeTitle.append(((ModuleTextAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleEntityCategoryListAttribute.class)){
            				sampleCodeTitle.append(((ModuleEntityCategoryListAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleLinkAttribute.class)){
            				sampleCodeTitle.append(((ModuleLinkAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleMoneyAttribute.class)){
            				sampleCodeTitle.append(((ModuleMoneyAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleNumberAttribute.class)){
            				sampleCodeTitle.append(((ModuleNumberAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleImageAttribute.class)){
            				sampleCodeTitle.append(((ModuleImageAttribute)attr).MODULEATTRIBUTEDESC);
            			}else if(attr.getClass().equals(ModuleProductListAttribute.class)){
            				sampleCodeTitle.append(((ModuleProductListAttribute)attr).MODULEATTRIBUTEDESC);
            			}
            			
            			String filePathToHowTo = servletContext.getRealPath("/WEB-INF/view/howto/");
            			
            			// ******* note: there have 5 howto.jsp files for each type of moduleAttr, take 'ModuleImageAttribute' for example:
            			// *******       howToImageAttrValue.jsp - hold some general information for how to get image value,
            			// *******       howToImageAttrValue_gf_af.jsp - hold information for how to get image value when group's array is false and attr's array is false,
            			// *******       howToImageAttrValue_gt_at.jsp - hold information for how to get image value when group's array is true and attr's array is true,
            			// *******       howToImageAttrValue_gf_at.jsp - hold information for how to get image value when group's array is false and attr's array is true,
            			// *******       howToImageAttrValue_gt_af.jsp - hold information for how to get image value when group's array is true and attr's array is false,
            			// the path for general howto info:
            			StringBuilder generalFilePathToHowTo = new StringBuilder(filePathToHowTo);
            			if(filePathToHowTo.indexOf("/")>-1){
            				generalFilePathToHowTo.append("/").append(howToDocName).append(".jsp");
            			}else{
            				generalFilePathToHowTo.append("\\").append(howToDocName).append(".jsp");
            			}
            			
            			// the path for specific howto info:
            			StringBuilder specificFilePathToHowTo = new StringBuilder(filePathToHowTo);
            			if(!attrGroup.getArray() && !attr.getArray()){
                			if(filePathToHowTo.indexOf("/")>-1){
                				specificFilePathToHowTo.append("/").append(howToDocName).append("_gf_af").append(".jsp");
                			}else{
                				specificFilePathToHowTo.append("\\").append(howToDocName).append("_gf_af").append(".jsp");
                			}
                			sampleCodeTitle.append(" (group's array: false, attribute's array: false)");
            			}else if(attrGroup.getArray() && !attr.getArray()){
                			if(filePathToHowTo.indexOf("/")>-1){
                				specificFilePathToHowTo.append("/").append(howToDocName).append("_gt_af").append(".jsp");
                			}else{
                				specificFilePathToHowTo.append("\\").append(howToDocName).append("_gt_af").append(".jsp");
                			}
                			sampleCodeTitle.append(" (group's array: true, attribute's array: false)");
            			}else if(attrGroup.getArray() && attr.getArray()){
                			if(filePathToHowTo.indexOf("/")>-1){
                				specificFilePathToHowTo.append("/").append(howToDocName).append("_gt_at").append(".jsp");
                			}else{
                				specificFilePathToHowTo.append("\\").append(howToDocName).append("_gt_at").append(".jsp");
                			}
                			sampleCodeTitle.append(" (group's array: true, attribute's array: true)");
            			}else if(!attrGroup.getArray() && attr.getArray()){
                			if(filePathToHowTo.indexOf("/")>-1){
                				specificFilePathToHowTo.append("/").append(howToDocName).append("_gf_at").append(".jsp");
                			}else{
                				specificFilePathToHowTo.append("\\").append(howToDocName).append("_gf_at").append(".jsp");
                			}
                			sampleCodeTitle.append(" (group's array: false, attribute's array: true)");
            			}
            			
            			//File destinationFile = new File(filePathToHowTo.toString());
            			StringBuilder combinedString = new StringBuilder();
            			File generalFile = new File(generalFilePathToHowTo.toString());
            			if(generalFile.exists()){
            				FileInputStream fisTargetFile;
            				try {
            					fisTargetFile = new FileInputStream(generalFile);
            					String howToString = IOUtils.toString(fisTargetFile, "UTF-8");
            					//combinedString.append(SitedesignHelper.transferStringIntoHtmlFormat(howToString));
            					combinedString.append(howToString);
            				} catch (FileNotFoundException e) {
            					e.printStackTrace();
            					logger.error(e.toString());
            				} catch (IOException e) {
            					e.printStackTrace();
            					logger.error(e.toString());
            				}
            			}
            			
            			File specificFile = new File(specificFilePathToHowTo.toString());
            			if(specificFile.exists()){
            				FileInputStream fisTargetFile;
            				try {
            					fisTargetFile = new FileInputStream(specificFile);
            					String howToString = IOUtils.toString(fisTargetFile, "UTF-8");
            					// replace {{groupname}} & {{attrname}}
            					//StringUtils.repl
            					
            					howToString = howToString.replaceAll("<<groupname>>", attrGroup.getGroupName());
            					howToString = howToString.replaceAll("<<attrname>>", attr.getName());
            					howToString = howToString.replaceAll("<<hostname>>", "http://"+applicationConfig.getHostName().trim());
            					
            					combinedString.append("<div class='sampleCode' style='border:1px solid; margin-top: 6px;'>")
            						.append("<h3 style='margin: 3px;'>Sample Code</h3><div class='code' style='border: 1px solid; margin: 3px;'>")
            						.append("<div class='sampleHead' style='background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;'><h4>").append(sampleCodeTitle).append("</h4></div>")
            						.append("<div style='padding: 2px 6px; color: #4183c4; font-size: 15px; overflow-x: auto;'>");
            					combinedString.append(SitedesignHelper.transferStringIntoHtmlFormat(howToString));
            					combinedString.append("</div></div></div>");
            				} catch (FileNotFoundException e) {
            					e.printStackTrace();
            					logger.error(e.toString());
            				} catch (IOException e) {
            					e.printStackTrace();
            					logger.error(e.toString());
            				}
            			}
            			
            			return combinedString.toString();
            			
            		}
            	}
        	}
    	}
		
		return "<div>System cannot find file: "+howToDocName+"</div>";
	}
    
    

    @RequestMapping(value="/entityHasModuleSelected", method=RequestMethod.GET)
    public @ResponseBody ApiResponse entityHasModuleSelected(
    		@RequestParam(value = "entityUuid", required = false) String entityUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	EntityDetail entity = productService.getEntityDetailByUuid(entityUuid);
    	if(entity!=null && StringUtils.isNotBlank(entity.getModuleuuid())){
    		apires.setSuccess(true);
    	}
    	return apires;
    }
    
    @RequestMapping(value="/hasModuleDesigned", method=RequestMethod.GET)
    public @ResponseBody ApiResponse hasModuleDesigned(
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	if(moduledetail!=null && StringUtils.isNotBlank(moduledetail.getXml())){
    		apires.setSuccess(true);
    	}
    	return apires;
    }
    	
    @RequestMapping(value="/movePageNode", method=RequestMethod.POST)
    public @ResponseBody ApiResponse movePageNode(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.moveTreeNode_v2(PageDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }

    @RequestMapping(value="/moveModuleNode", method=RequestMethod.POST)
    public @ResponseBody ApiResponse moveModuleNode(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.moveTreeNode_v2(ModuleDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }
    
    @RequestMapping(value="/copyModuleNodeToAnotherTree", method=RequestMethod.POST)
    public @ResponseBody ApiResponse copyModuleNodeToAnotherTree(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.copyTreeNodeToAnotherTree(ModuleDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }
    
    @RequestMapping(value="/copyProductNodeToAnotherTree", method=RequestMethod.POST)
    public @ResponseBody ApiResponse copyProductNodeToAnotherTree(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.copyTreeNodeToAnotherTree(EntityDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }
    
    @RequestMapping(value="/copyPageNodeToAnotherTree", method=RequestMethod.POST)
    public @ResponseBody ApiResponse copyPageNodeToAnotherTree(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	PageDetail pagedetail_source = siteDesignService.getPageDetailByUuid(nodeUuid);
    	
    	if(pagedetail_source!=null){
        	
        	apires = treeService.copyTreeNodeToAnotherTree(PageDetail.class, nodeUuid, targetUuid);
        	
        	// get list containers' hexcolors
    		Set<String> containerHexcolors = new HashSet<String>();
    		List<String> containerUuids = SitedesignHelper.findAllContainerUuidsFromPageDetail(pagedetail_source.getDetail());
    		if(containerUuids!=null && containerUuids.size()>0){
    			for(String cuuid : containerUuids){
    				ContainerDetail containerDetail = siteDesignService.getContainerByUuid(cuuid);
    				if(containerDetail!=null && StringUtils.isNotBlank(containerDetail.getHexColor())){
    					containerHexcolors.add(containerDetail.getHexColor());
    				}
    			}
    		}
    		
    		if(containerHexcolors.size()>0){
    			apires.setResponse2(containerHexcolors);
    		}
    	}
    	
    	
    	return apires;
    }

    
    @RequestMapping(value="/getAttributeValidation", method=RequestMethod.GET)
    public @ResponseBody ApiResponse getAttributeValidation(
    		@RequestParam(value = "instanceId", required = false) String instanceUuid,
    		@RequestParam(value = "groupUuid", required = false) String groupUuid,
    		@RequestParam(value = "attrUuid", required = false) String attrUuid,
    		
    		ModelMap model){

    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(true);
    	
		ModuleInstanceInterface moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceUuid);
		if(moduleInstance==null){
			moduleInstance = productService.getEntityDetailByUuid(instanceUuid);
		}
		if(moduleInstance!=null){
			Module instance = null;
			String moduleDetailUuid = null;
			if(moduleInstance.getClass().equals(ModuleInstance.class)){
				instance = SitedesignHelper.getModuleFromXml(((ModuleInstance)moduleInstance).getInstance());
				moduleDetailUuid = ((ModuleInstance)moduleInstance).getModuleuuid();
			}else if(moduleInstance.getClass().equals(EntityDetail.class)){
				instance = SitedesignHelper.getModuleFromXml(((EntityDetail)moduleInstance).getDetail());
				moduleDetailUuid = ((EntityDetail)moduleInstance).getModuleuuid();
			}
			
			if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
				for(AttrGroup g : instance.getAttrGroupList()){
					if(g.getGroupUuid().equals(groupUuid)){
						if(g.getAttrList()!=null && g.getAttrList().size()>0){
							for(ModuleAttribute a : g.getAttrList()){
								if(a.getUuid().equals(attrUuid)){
									
									// filled all extra info for instance's attr, like minlength, ...
									ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleDetailUuid);
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
									
									List<String> validationErrorInfos = SitedesignHelper.moduleAttributeValidation(a);
									if(validationErrorInfos!=null && validationErrorInfos.size()>0){
										StringBuilder errorInfos = new StringBuilder();
										int idx = 0;
										for(String info : validationErrorInfos){
											if(idx>0){
												errorInfos.append("<br/>");
											}
											
											errorInfos.append(info);
											idx++;
										}
										apires.setSuccess(false);
										apires.setResponse1(errorInfos.toString());
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
    	
    	return apires;
    }
    
    @RequestMapping(value="/getPageScheduleForEntitySetup", method=RequestMethod.GET)
    public ModelAndView getPageScheduleForEntitySetup(
    		@RequestParam(value = "entityid", required = false) String entityUuid,
    		@RequestParam(value = "pagetype", required = false) String pagetype // categoryPage | productPage
		) {
    	ModelAndView mv = null;
    	
    	mv = new ModelAndView("pageScheduleForEntitySetup");
    	
    	EntityDetail entityDetail = productService.getEntityDetailByUuid(entityUuid);
    	if(entityDetail!=null && StringUtils.isNotBlank(pagetype)){
    		
    		mv.addObject("entityUuid", entityDetail.getEntityuuid());
    		
    		Organization org = accountService.getOrgById(entityDetail.getOrganization_id());
    		
    		List<PageDetail> orgDeskPageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Desktop);
    		List<PageDetail> orgMobilePageDetails = siteDesignService.findOrgPagesByType(org.getOrguuid(), PageDetail.Type.Mobile);
    		List<GeneralSelectionType> orgDeskPages = new ArrayList<GeneralSelectionType>();
    		List<GeneralSelectionType> orgMobilePages = new ArrayList<GeneralSelectionType>();
    		if(orgDeskPageDetails!=null && orgDeskPageDetails.size()>0){
    			for(PageDetail d : orgDeskPageDetails){
    				orgDeskPages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
    			}
    		}
    		if(orgMobilePageDetails!=null && orgMobilePageDetails.size()>0){
    			for(PageDetail d : orgMobilePageDetails){
    				orgMobilePages.add(new GeneralSelectionType(d.getPageuuid(), d.getPrettyname(), false));
    			}
    		}
    		mv.addObject("orgDeskPages", orgDeskPages);
    		mv.addObject("orgMobilePages", orgMobilePages);
        	
        	mv.addObject("pagetype", pagetype);
        	
        	// find default pages for desktop and mobile
//        	PageDetail.Type thePagetype = null;
//        	if(StringUtils.isNotBlank(pagetype)){
//        		if(pagetype.equals("categoryPage")){
//        			thePagetype = PageDetail.Type.category;
//        		}else if(pagetype.equals("productPage")){
//        			thePagetype = PageDetail.Type.product;
//        		}
//        	}
//        	String categoryOrProductPageUuid_desk = productService.getCategoryOrProductPageUuidForEntityNode(entityUuid, thePagetype, PageDetail.Type.Desktop, null, null);
//        	String categoryOrProductPageUuid_mobile = productService.getCategoryOrProductPageUuidForEntityNode(entityUuid, thePagetype, PageDetail.Type.Desktop, null, null);
        	mv.addObject("defaultCategoryPage_desk", entityDetail.getCatpageuuid_desktop());
        	mv.addObject("defaultCategoryPage_mobile", entityDetail.getCatpageuuid_mobile());
        	mv.addObject("defaultProductPage_desk", entityDetail.getProdpageuuid_desktop());
        	mv.addObject("defaultProductPage_mobile", entityDetail.getProdpageuuid_mobile());
        	
        	
    		
    	}
    	
    	return mv;
    }

    
    @RequestMapping(value="/updateEntityDefaultPage", method=RequestMethod.GET)
    public @ResponseBody ApiResponse updateEntityDefaultPage(
    		@RequestParam(value = "entityId", required = false) String entityUuid,
    		@RequestParam(value = "pagetype", required = false) String pagetype, //category , product
    		@RequestParam(value = "pageid", required = false) String pageUuid,
    		@RequestParam(value = "sitetype", required = false) String sitetype, // desktop , mobile
    		
    		ModelMap model){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	
    	
    	if(StringUtils.isNotBlank(entityUuid) 
//    			&& StringUtils.isNotBlank(pageUuid) 
    			&& StringUtils.isNotBlank(pagetype)
    			&& StringUtils.isNotBlank(sitetype)){
    		PageDetail.Type thePageType = null;
    		if(pagetype.equals("category")){
    			thePageType = PageDetail.Type.category;
    		}else if(pagetype.equals("product")){
    			thePageType = PageDetail.Type.product;
    		}
    		
    		PageDetail.Type theSiteType = null;
    		if(sitetype.equals("desktop")){
    			theSiteType = PageDetail.Type.Desktop;
    		}else if(sitetype.equals("mobile")){
    			theSiteType = PageDetail.Type.Mobile;
    		}
        	
        	apires = productService.setDefaultPageForEntity(entityUuid, pageUuid, thePageType, theSiteType);
    		
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/charSizeValiJson")
    public @ResponseBody ApiResponse charSizeValiJson(
    		@RequestParam(value = "moduleId", required = false) String moduleUuid,
    		@RequestParam(value = "type", required = false) String type,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	ModuleDetail detail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	if(detail!=null){
//    		int extraStringLength = 0;
//    		if(StringUtils.isNotBlank(updateValue)){
//    			extraStringLength = updateValue.length();
//    		}
        	int usedPercent = paymentService.countModuleDetailUsage(detail);
        	
        	if(usedPercent>100){
        		apires.setResponse1("You reach the maximum storage usage for ModuleDetail with the new value.");
        	}else{
        		apires.setSuccess(true);
        		apires.setResponse1(usedPercent);
        	}
    		
    	}else{
    		apires.setResponse1("System can find ModuleDetail by "+moduleUuid);
    	}
    	
    	return apires;
    }
    
    @RequestMapping(value="/moduleDetailAttributesSort")
    public @ResponseBody ApiResponse moduleDetailAttributesSort(
    		@RequestParam(value = "moduleDetailId", required = false) String moduleDetailUuid,
    		@RequestParam(value = "groupId", required = false) String groupUuid,
    		@RequestParam(value = "attrId", required = false) String attriUuid,
    		@RequestParam(value = "position", required = false) Integer position
		){
    	ApiResponse apires = siteDesignService.moduleDetailAttributesSort(moduleDetailUuid, groupUuid, attriUuid, position);
    	return apires;
    }
    	
    
    @RequestMapping(value="/instanceNewAttr", method=RequestMethod.POST)
    public @ResponseBody ApiResponse instanceNewAttr(
    		@RequestParam(value = "moduleUuid", required = false) String moduleUuid,
    		@RequestParam(value = "moduleGroupUuid", required = false) String moduleGroupUuid,
    		@RequestParam(value = "moduleAttrUuid", required = false) String moduleAttrUuid,
    		
    		@RequestParam(value = "instanceUuid", required = false) String instanceUuid
    		
		){
    	ApiResponse apires = siteDesignService.newInstanceAttr(moduleUuid, moduleGroupUuid, moduleAttrUuid, instanceUuid);
    	
    	return apires;
    }
    
    
    @RequestMapping(value="/jspReviewPreset", method=RequestMethod.GET)
    public ModelAndView jspReviewPreset(
    		@RequestParam(value = "type", required = false) String type, // moduledetail or instance
    		@RequestParam(value = "objuuid", required = false) String objuuid, // moduleDetail's uuid or instance's uuid
    		@RequestParam(value = "viewuuid", required = false) String viewuuid // view's uuid if have
		) {
    	ModelAndView mv = new ModelAndView("jspReviewPreset");
    	
    	AccountDto loginAccount = accountService.getCurrentAccount();
    	
    	if(loginAccount!=null && StringUtils.isNotBlank(type)){
    		mv.addObject("type", type);
    		mv.addObject("objuuid", objuuid);
    		mv.addObject("viewuuid", viewuuid);
    		
    		Long orgid = null;
    		Module module = null;
    		
    		if(type.equals("moduledetail")){
    			ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(objuuid);
    			if(moduledetail!=null) {
    				orgid = moduledetail.getOrganization_id();
    				
    				// get module
    				module = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
    			}
    		}else if(type.equals("instance")){
    			ModuleInstance instance = siteDesignService.getModuleInstanceByUuid(objuuid);
    			if(instance!=null) {
    				orgid = instance.getOrgid();
    				
    		    	// get updated module from instance
    				String moduleUuid = instance.getModuleuuid();
    				ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
    	    		module = SitedesignHelper.getModuleFromXml(instance.getInstance());
    	    		// add extra module info to instance
    	    		if(moduledetail!=null){
        	    		Module moduleFromModuleDetail = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
        	    		SitedesignHelper.updateModuleInstanceByModule(moduleFromModuleDetail, module);
    	    		}
    				
    			} else {
    				EntityDetail entitydetail = productService.getEntityDetailByUuid(objuuid);
    				if(entitydetail!=null) {
    					orgid = entitydetail.getOrganization_id();
    					
        		    	// get updated module from instance
        				String moduleUuid = entitydetail.getModuleuuid();
        				ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
        	    		module = SitedesignHelper.getModuleFromXml(entitydetail.getDetail());
        	    		// add extra module info to instance
        	    		if(moduledetail!=null){
            	    		Module moduleFromModuleDetail = SitedesignHelper.getModuleFromXml(moduledetail.getXml());
            	    		SitedesignHelper.updateModuleInstanceByModule(moduleFromModuleDetail, module);
        	    		}
    					
    				}
    			}
    		}
    		
    		if(orgid!=null){
    	    	// find all js and css uploaded!
    			List<MediaDetail> jsDetails = mediaService.findMediasInOrg(loginAccount.getAccountuuid(), MediaType.javascript, orgid);
    			List<MediaDetail> cssDetails = mediaService.findMediasInOrg(loginAccount.getAccountuuid(), MediaType.css, orgid);
    			mv.addObject("jsDetails", jsDetails);
    			mv.addObject("cssDetails", cssDetails);
    		}
    		
    		
    		// some extra operation for ModuleProductListAttribute & ModuleEntityCategoryListAttribute
        	if(module!=null){
	    		// find if module has ModuleProductListAttribute, ModuleEntityCategoryListAttribute
	    		ModuleProductListAttribute moduleProductListAttr = null;
	    		ModuleEntityCategoryListAttribute moduleEntityCategoryListAttr = null;
	    		if(module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
	    			for(AttrGroup g : module.getAttrGroupList()){
	    				//if(moduleProductListAttr!=null) break;
	    				if(g.getAttrList()!=null && g.getAttrList().size()>0){
	    					for(ModuleAttribute a : g.getAttrList()){
	    						
	    						if(a.getClass().equals(ModuleProductListAttribute.class)){
	    							moduleProductListAttr = (ModuleProductListAttribute)a;
	    						}else if(a.getClass().equals(ModuleEntityCategoryListAttribute.class)){
	    							moduleEntityCategoryListAttr = (ModuleEntityCategoryListAttribute)a;
	    						}
	    					}
	    				}
	    			}
	    		}
	    		
	    		// add category list for preview to select
	    		if(moduleProductListAttr!=null){
	    			if(orgid!=null){
	    				EntityTreeNode productRoot = treeService.getProductTreeRoot(orgid);
	    				if(productRoot!=null){
	    	    			List<TreeNode> childFolders = new ArrayList<TreeNode>();
	    	    			treeService.findSubFoldersUnderFolder(loginAccount.getAccountuuid(), childFolders, EntityTreeNode.class, productRoot.getSystemName(), true, true);
	    	    			if(childFolders!=null && childFolders.size()>0){
	    	    				List<TreeNode> selectableFolders = new ArrayList<TreeNode>();
	    	    				selectableFolders.add(productRoot);
	    	    				for(TreeNode n : childFolders){
	    	    					boolean isPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.preview, n.getSystemName());
	    	    					if(isPermissionAllowed){
	    	    						selectableFolders.add(n);
	    	    					}
	    	    				}
	    	    				if(selectableFolders.size()>0){
	    	    					mv.addObject("selectableProductFolders", selectableFolders);
	    	    				}
	    	    			}
	    					
	    				}
	    				
	    			}
	    			
	    			
	    		}
	    		
	    	}
    		
    	}
    	
    	// get host address
    	String hostAddress = "http://"+applicationConfig.getHostName().trim();
    	mv.addObject("hostAddress", hostAddress);
    	
    	return mv;
    	
    }
    
    
    
}
