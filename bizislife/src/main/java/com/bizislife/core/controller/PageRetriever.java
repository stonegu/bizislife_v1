package com.bizislife.core.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ContainerTreeNode;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralTreeNode;
import com.bizislife.core.controller.component.Pagination;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.controller.helper.EntityHelp;
import com.bizislife.core.controller.helper.SwallowingJspRenderer;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.entity.converter.ContainerToPageDetailConvertor;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.MediaService;
import com.bizislife.core.service.ProductService;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleProductListAttribute;
import com.bizislife.util.WebUtil;
import com.bizislife.util.annotation.PublicPage;
import com.mongodb.util.Hash;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


@PublicPage
@Controller
public class PageRetriever {
	private static final Logger logger = LoggerFactory.getLogger(PageRetriever.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private MediaService mediaService;
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;

    @Autowired
	private SwallowingJspRenderer jspRenderer;
    
	
	//******************* begin test ******************************	
	@RequestMapping(value="/getPageInOrg", method=RequestMethod.GET)
	public String getPageInOrg (HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageId", required = false) String pageUuid,
			
			@RequestParam(value="locale", required=false) String requestedLocale
			) {
		
		
		// test page 
		return "module/system/testPage";
		
	}

	//******************* end test ******************************	
	
	
    /**
	 * Writes the report to the output stream
	 */
	public static void write(HttpServletResponse response, ByteArrayOutputStream bao) {
		
		//logger.debug("Writing report to the stream");
		try {
			// Retrieve the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			// Write to the output stream
			bao.writeTo(outputStream);
			// Flush the stream
			outputStream.flush();
			// Close the stream
			outputStream.close();

		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
		}
	}
	
	/**
	 * Writes the report to the output stream
	 */
	public static void write(HttpServletResponse response, byte[] byteArray) {
		
		logger.debug("Writing report to the stream");
		try {
			// Retrieve the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			// Write to the output stream
			outputStream.write(byteArray);
			// Flush the stream
			outputStream.flush();
			// Close the stream
			outputStream.close();
			
		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
		}
	}
	
	/**
	 * write the string to output stream.
	 * @param response
	 * @param str
	 */
	public static void write(HttpServletResponse response, String str) {
		try {
			response.getWriter().print(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
		}
	}
	
	public static void write (HttpServletResponse response, BufferedImage bufferedImg){
		try {
			ImageIO.write(bufferedImg, "png", response.getOutputStream());
		}catch (IOException ex) {
			ex.printStackTrace();
		}		
		
	}
	
//	@RequestMapping(value="/jspPreview", method=RequestMethod.GET)
//	public @ResponseBody String jspPreviewBlankPage () {
//		return "<html><body><h1>Done.</h1></body></html>";
//	}
	
    @RequestMapping(value="/jspPreview")
    public ModelAndView jspPreview(
    		@RequestParam(value = "type", required = false) String type, // moduledetail or instance
    		@RequestParam(value = "objuuid", required = false) String objuuid,
    		@RequestParam(value = "viewuuid", required = false) String viewuuid, // not null means that view from instance.
			@RequestParam(value = "entityid", required = false) String entityUuid, // for display product detail
			@RequestParam(value = "categoryid", required = false) String categoryid, // for display product list or ..
    		@RequestParam(value = "jspPreviewHead", required = false) String jspPreviewHead

		) {
    	ModelAndView mv = new ModelAndView("jspPreview");
    	
    	mv.addObject("categoryid", categoryid);
    	
    	// set contentInfoMap (modulecss, viewcss)
    	Map<String, String> contentInfoMap = new HashMap<String, String>();

    	StringBuilder viewName = new StringBuilder();
    	Long orgId = null;
    	
    	InstanceView view = siteDesignService.getInstanceViewByUuid(viewuuid);

		if(type.equals("moduledetail")){
			ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(objuuid);
			if(moduledetail!=null) {
				orgId = moduledetail.getOrganization_id();
				String moduleUuid = moduledetail.getModuleuuid();
				viewName.append("ModuleDetail (").append(moduledetail.getPrettyname()).append(")'s view");
				
				if(StringUtils.isNotBlank(moduledetail.getCss())){
			    	contentInfoMap.put("modulecss", "/org/"+orgId+"/"+moduleUuid+".css");
				}
		    	contentInfoMap.put("moduleuuid", moduleUuid);
		    	
			}
		}else if(type.equals("instance")){
			ModuleInstance instance = siteDesignService.getModuleInstanceByUuid(objuuid);
			if(instance!=null) {
				
				if(view == null){
					view = siteDesignService.getCurrentInstanceView(instance.getModuleinstanceuuid());
				}
				
				
				orgId = instance.getOrgid();
				viewName.append("Instance (").append(instance.getName()).append(")'s view");
				
				String moduleUuid = instance.getModuleuuid();
				ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
				
				String instanceuuid = instance.getModuleinstanceuuid();
				
				if(moduledetail!=null && StringUtils.isNotBlank(moduledetail.getCss())){
			    	contentInfoMap.put("modulecss", "/org/"+orgId+"/"+moduleUuid+".css");
				}
		    	contentInfoMap.put("moduleuuid", moduleUuid);
		    	contentInfoMap.put("instanceuuid", instanceuuid);
		    	
			} else {
				EntityDetail entitydetail = productService.getEntityDetailByUuid(objuuid);
				if(entitydetail!=null) {
					
					if(view == null){
						view = siteDesignService.getCurrentInstanceView(entitydetail.getEntityuuid());
					}
					
					
					mv.addObject("entityid", entitydetail.getEntityuuid());
					
					orgId = entitydetail.getOrganization_id();
					viewName.append("Product (").append(entitydetail.getName()).append(")'s view");
					
					String moduleUuid = entitydetail.getModuleuuid();
					ModuleDetail moduledetail = siteDesignService.getModuleDetailByUuid(moduleUuid);
					
					String instanceuuid = entitydetail.getEntityuuid();
					
					if(moduledetail!=null && StringUtils.isNotBlank(moduledetail.getCss())){
				    	contentInfoMap.put("modulecss", "/org/"+orgId+"/"+moduleUuid+".css");
					}
			    	contentInfoMap.put("moduleuuid", moduleUuid);
			    	contentInfoMap.put("instanceuuid", instanceuuid);
			    	
				}
			}
		}
		mv.addObject("previewTitle", viewName.toString());
    	
    	mv.addObject("pageHead", jspPreviewHead);
    	mv.addObject("bizhost", applicationConfig.getHostName());
    	
    	// for view:
    	if(view!=null){
    		contentInfoMap.put("viewuuid", view.getInstanceviewuuid());
    		// for view's css
			if(StringUtils.isNotBlank(view.getCss())){
				contentInfoMap.put("viewcss", "/org/"+view.getOrgid()+"/"+view.getInstanceviewuuid()+".css");
			}
    	}

    	mv.addObject("contentInfoMap", contentInfoMap);
    	
    	return mv;
    }

	
	@RequestMapping(value = "/getPage/org/{orgid}/pageurl/{url}", method = RequestMethod.GET)
	public String getResponsivePage(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("url") String url,

			@RequestParam(value = "entityid", required = false) String entityUuid, // for display product detail
			
			@RequestParam(value = "categoryid", required = false) String productFolderUuid, // for display category/product list
			@RequestParam(value = "pageidx", required = false) Integer pageIndex, // for display category/product list based on pageIndex (page#)
			
			HttpServletRequest req,
			
			ModelMap model) {
		
		List<String> errorMsgs = new ArrayList<String>();
		
		if(StringUtils.isNotBlank(orgUuid) && StringUtils.isNotBlank(url)){
			
			
//			String hostname = (String)req.getAttribute("hostname");
//			model.addAttribute("hostname", hostname);
			// get organization
			Organization org = accountService.getOrgByUuid(orgUuid);
			PageDetail pageDetail = null;
			PageMeta pageMeta = null;
			if(org!=null){
				model.addAttribute("org", org);
				pageDetail = siteDesignService.getOrgPageByTypeAndUrl(org.getId(), PageDetail.Type.Desktop, url);
				if(pageDetail!=null){
					pageMeta = siteDesignService.getPageMetaByPageUuid(pageDetail.getPageuuid());
				}
			}
			
			if(pageDetail!=null && pageMeta!=null){
				model.addAttribute("pageMeta", pageMeta);
//				model.addAttribute("pageid", pageDetail.getPageuuid());
				
				// for page head
				String pageHead = StringEscapeUtils.unescapeHtml(pageMeta.getHeadcontent());
				model.addAttribute("pageHead", pageHead);
				
				// get ContainerTreeNode from pageDetail
				if(pageDetail.getDetail()!=null){
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new ContainerToPageDetailConvertor());
					stream.processAnnotations(new Class[]{ContainerTreeNode.class});
					
					ContainerTreeNode container = (ContainerTreeNode)stream.fromXML(pageDetail.getDetail());
					if(container!=null){
						model.addAttribute("container", container);
						
						// get container schedule (module, instance, view) for tree's leaf(outest node)
						// map holds the relationship between containerUuid and it's content.
						// Map<ContainerUuid, 
						//						Map<
						//							type : ModuleDetail.Type.productModule || ModuleDetail.Type.module;
						//							modulecss : modulecss's location
						//							moduleuuid : uuid
						//							instanceuuid : uuid
						//							viewuuid : uuid
						//							viewcss : css location
						//							>
						Map<String, Map<String, String>> containerContentInfoMap = siteDesignService.generateContentInfoForContainerTreeLeaf(container, entityUuid);
						model.addAttribute("containerContentInfoMap", containerContentInfoMap);
						
						model.addAttribute("bizhost", applicationConfig.getHostName());
						
						
						// for purecss framework:
						// get pure unit class for each container
						if(StringUtils.equals(pageMeta.getDefaultcss(), PageMeta.DefaultCss.purecss.getCode())){
							Map<String, String> containerPureUnitMap = new HashMap<String, String>(); 
							siteDesignService.generateContainerPureUnitMap(container, containerPureUnitMap);
							model.addAttribute("containerPureUnitMap", containerPureUnitMap);
						}
						
						
					}
					
				}
				
				
				
			}else{
				errorMsgs.add("System can't find page based on provided information!");
			}
			
			
		}else{
			errorMsgs.add("No organization information or no page url information!");
		}
		
		model.addAttribute("errorMsgs", errorMsgs);
		
		if(StringUtils.isNotBlank(productFolderUuid)){
			model.addAttribute("categoryid", productFolderUuid);
		}
		
		if(pageIndex!=null){
			model.addAttribute("pageidx", pageIndex);
		}
		
		if(StringUtils.isNotBlank(entityUuid)){
			model.addAttribute("entityid", entityUuid);
		}
		
		return "page/desktopPageMain";
	}
	
	
	@Deprecated
	@RequestMapping(value = "/getMobilePage/org/{orgid}/pageurl/{url}", method = RequestMethod.GET)
	public String getMobilePage(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("url") String url,
			
			@RequestParam(value = "entityid", required = false) String entityUuid,// for display product detail
			
			@RequestParam(value = "categoryid", required = false) String productFolderUuid, // for display category/product list
			@RequestParam(value = "pageidx", required = false) Integer pageIndex, // for display category/product list based on pageIndex (page#)

			ModelMap model) {
		
		List<String> errorMsgs = new ArrayList<String>();
		
		if(StringUtils.isNotBlank(orgUuid) && StringUtils.isNotBlank(url)){
			// get organization
			Organization org = accountService.getOrgByUuid(orgUuid);
			PageDetail pageDetail = null;
			PageMeta pageMeta = null;
			if(org!=null){
				model.addAttribute("org", org);
				pageDetail = siteDesignService.getOrgPageByTypeAndUrl(org.getId(), PageDetail.Type.Mobile, url);
				if(pageDetail!=null){
					pageMeta = siteDesignService.getPageMetaByPageUuid(pageDetail.getPageuuid());
							
				}
			}
			
			if(pageDetail!=null && pageMeta!=null){
				model.addAttribute("pageMeta", pageMeta);
				
				// for page head
				String pageHead = StringEscapeUtils.unescapeHtml(pageMeta.getHeadcontent());
				model.addAttribute("pageHead", pageHead);
				
				// get ContainerTreeNode from pageDetail
				if(pageDetail.getDetail()!=null){
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new ContainerToPageDetailConvertor());
					stream.processAnnotations(new Class[]{ContainerTreeNode.class});
					
					ContainerTreeNode container = (ContainerTreeNode)stream.fromXML(pageDetail.getDetail());
					if(container!=null){
						model.addAttribute("container", container);
						
						// get container schedule (module, instance, view) for tree's leaf(outest node)
						// map holds the relationship between containerUuid and it's content.
						// Map<ContainerUuid, 
						//						Map<
						//							type : ModuleDetail.Type.productModule || ModuleDetail.Type.module;
						//							modulecss : modulecss's location
						//							moduleuuid : uuid
						//							instanceuuid : uuid
						//							viewuuid : uuid
						//							viewcss : css location
						//							>
						Map<String, Map<String, String>> containerContentInfoMap = siteDesignService.generateContentInfoForContainerTreeLeaf(container, entityUuid);
						model.addAttribute("containerContentInfoMap", containerContentInfoMap);
						
					}
					
				}
				
				
				
			}else{
				errorMsgs.add("System can't find page based on provided information!");
			}
			
			
		}else{
			errorMsgs.add("No organization information or no page url information!");
		}
		
		model.addAttribute("errorMsgs", errorMsgs);
		
		if(StringUtils.isNotBlank(productFolderUuid)){
			model.addAttribute("categoryid", productFolderUuid);
		}
		if(pageIndex!=null && pageIndex.intValue()>0){
			model.addAttribute("pageidx", pageIndex);
		}
		if(StringUtils.isNotBlank(entityUuid)){
			model.addAttribute("entityid", entityUuid);
		}
		
		return "page/mobilePageMain";
	}
	
	
	@RequestMapping(value="/getContainerModuleContent", method=RequestMethod.GET)
	public ResponseEntity<String> getContainerModuleContent (HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageid", required = false) String pageUuid,
			@RequestParam(value = "moduleid", required = false) String moduleuuid,
			@RequestParam(value = "instanceid", required = false) String instanceuuid,
			@RequestParam(value = "viewid", required = false) String viewuuid,
			
			@RequestParam(value = "categoryid", required = false) String productFolderUuid, // for product list
			@RequestParam(value = "pageidx", required = false) Integer pageIndex, // for display category/product list based on pageIndex (page#)
			
			@RequestParam(value = "entityid", required = false) String entityUuid,
			
			@RequestParam(value="locale", required=false) String requestedLocale,
			
			@RequestParam(value="hostname", required=false) String hostname
			) {

		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");	
	    
	    StringBuilder jspOutput = new StringBuilder();
	    
    	ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduleuuid);
    	
    	Accountgroup everyonegroup = accountService.getEveryoneGroup();

    	if(moduleDetail!=null){
    	    
	    	Organization org = accountService.getOrgById(moduleDetail.getOrganization_id());
	    	
    	    PageDetail targetPageDetail = null;
    	    
    	    if(StringUtils.equals(pageUuid, PageDetail.FAKEPAGEUUID)){
    	    	targetPageDetail = SitedesignHelper.createFakePage(org);
    	    }else{
    	    	targetPageDetail = siteDesignService.getPageDetailByUuid(pageUuid);
    	    }
    	    
    	    if(targetPageDetail!=null){
    	    	
    	    	PageDetail.Type sitetype = null;
    	    	if(targetPageDetail.getType().equals(PageDetail.Type.Desktop.getCode())){
    	    		sitetype = PageDetail.Type.Desktop;
    	    	}else if(targetPageDetail.getType().equals(PageDetail.Type.Mobile.getCode())){
    	    		sitetype = PageDetail.Type.Mobile;
    	    	}
    	    	
    	    	// only if module is product module, then we need to check the entityId. 
    	    	if(moduleDetail.getType().equals(ModuleDetail.Type.productModule.getCode())){
    	    		Module module = null;
    	    		
    	    		if(StringUtils.isNotBlank(entityUuid)){
    	    			EntityDetail entitydetail = productService.getEntityDetailByUuid(entityUuid);
    	    			if(entitydetail!=null){
    	    				if(StringUtils.isNotBlank(entitydetail.getModuleuuid())){
    	    					// switch moduledetail to entityDetail's moduledetail.
    	    					moduleDetail = siteDesignService.getModuleDetailByUuid(entitydetail.getModuleuuid());
    	    					
    	    					if(moduleDetail!=null){
    			    				// get module from entityDetail
    				    			module = SitedesignHelper.getModuleFromInstance(entitydetail);
    				    			// add extra module info to instance
    					    		Module moduleFromModule = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
    					    		SitedesignHelper.updateModuleInstanceByModule(moduleFromModule, module);
    	    						
    	    					}else{
    		    					jspOutput.append("<div class='warnInfo' style='color: red;'>The template used for entity \""+entitydetail.getName()+"\" doesn't exist!");
    			    				jspOutput.append("The template:  \""+entitydetail.getModuleuuid()+"\" could be deleted!!</div>");
    	    						
    	    					}
    	    					
    	    				}else{
    	    					jspOutput.append("<div class='warnInfo' style='color: red;'>The system can't find templated selected for the product \""+entitydetail.getName()+"\", ");
    	    					jspOutput.append("The content displayed in the container will be the default content from the template \""+moduleDetail.getPrettyname()+"\".</div>");
    	    				}
    	    			}else{
    	    				jspOutput.append("<div class='warnInfo' style='color: red;'>The system can't find entity based on entityId: "+entityUuid+". ");
    	    				jspOutput.append("The content displayed in the container will be the default content from the template \""+moduleDetail.getPrettyname()+"\".</div>");
    	    			}
    	    		}else{
    	    			jspOutput.append("<div class='warnInfo' style='color: red;'>The template \""+moduleDetail.getPrettyname()+"\" selected in this container is entityTemplate, but the url doesn't include entityid in param! ");
    	    			jspOutput.append("The content displayed in the container will be the default content from the template.</div>");
    	    		}
    	    		// get module from moduleDetail if module can't get from entityDetail
    	    		if(module==null && moduleDetail!=null){
    	    			module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
    	    		}
    	    		
    	    		if(module!=null){
    		    		Map<String, Object> jspMap = new HashMap<String, Object>();
    		    		jspMap.put("module", module);
    		    		
    	    			Locale locale = Locale.getDefault();
    	    			
    	    			try{
    	    	    		if(StringUtils.isNotBlank(viewuuid)){
    	    	    			InstanceView instanceview = siteDesignService.getInstanceViewByUuid(viewuuid);
    	    	    			if(instanceview!=null){
    	    						jspOutput.append(jspRenderer.render("module/org/"+instanceview.getOrgid()+"/"+instanceview.getInstanceviewuuid(), jspMap, locale));
    	    	    			}else{
    	    	    				jspOutput.append(jspRenderer.render("module/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid(), jspMap, locale));
    	    	    			}
    	    	    		}else{
    	    	    			jspOutput.append(jspRenderer.render("module/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid(), jspMap, locale));
    	    	    		}
    	    				
    	    			}catch (IOException e) {
    	    				
    	    				
    	    				StringBuilder errorOutput = new StringBuilder();
    	    				errorOutput.append("<div class='sysErrorMsg pageNotFound' style='color: red;'>");
    	    				
    	    				InstanceView instanceview = siteDesignService.getInstanceViewByUuid(viewuuid);
    	    				if(instanceview!=null){
    	    					errorOutput.append("System can't find view's jsp file for view: ").append(instanceview.getViewname()).append("(").append(viewuuid).append(")");
    	    				}else {
    	    					errorOutput.append("System can't find moduledetail's jsp file for moduledetail: ").append(moduleDetail.getPrettyname()).append("(").append(moduleDetail.getModuleuuid()).append(")");
    	    				}
    	    				
    	    				errorOutput.append("<br/> Or, could be <br/>");
    	    				errorOutput.append(e.toString());
    	    				
    	    				
    	    				errorOutput.append("</div>");
    	    				
    	    				jspOutput.append(errorOutput);
    	    				
    					}
    	    			
    	    		}
    	    		
    	    	}else{ //end product module, begin no product module
    		    	Module module = null;
    		    	ModuleInstance moduleInstance = siteDesignService.getModuleInstanceByUuid(instanceuuid);
    		    	if(moduleInstance!=null){
    		    		module = SitedesignHelper.getModuleFromInstance(moduleInstance);
    		    		// add extra module info to instance
    		    		Module moduleFromModuleDetail = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
    		    		SitedesignHelper.updateModuleInstanceByModule(moduleFromModuleDetail, module);
    		    		
    		    	}else{
    		    		
    		    		module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
    		    	}
    		    	
    		    	if(module!=null){
    		    		Map<String, Object> jspMap = new HashMap<String, Object>();
    		    		jspMap.put("module", module);
    		    		
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
    		    		
    		    		
    		    		//******* add special params in to jspMap *************************
    		    		
    		    		// if module has ModuleProductListAttr:
    		    		if(moduleProductListAttr!=null){
    		    			
    		    			//get the productFolderUuid: if productFolderUuid is null, productFolderUuid is productRootUuid!!
    		    			if(StringUtils.isBlank(productFolderUuid)){
    		    				EntityTreeNode entityRoot = treeService.getProductTreeRoot(org.getId());
    		    				productFolderUuid = entityRoot.getSystemName(); 
    		    			}
    						
        					// for pagination
        					boolean hasPagination = moduleProductListAttr.getHasPagination()!=null?moduleProductListAttr.getHasPagination():false;
        					
        					int totalNumProductsInPage = Integer.MAX_VALUE;
    						int pageIdxNum = 1;
        					
        					if(hasPagination){
        						
        						if(moduleProductListAttr.getTotalNumProductsInPage()!=null && moduleProductListAttr.getTotalNumProductsInPage().intValue()>0){
        							totalNumProductsInPage = moduleProductListAttr.getTotalNumProductsInPage().intValue();
        						}
        						
        						if(pageIndex!=null){
        							if(pageIndex.intValue()>1) pageIdxNum = pageIndex.intValue();
        							else if(pageIndex.intValue()<1) totalNumProductsInPage = Integer.MAX_VALUE;
        						}
        						

        						if(StringUtils.isNotBlank(productFolderUuid)){
            						Pagination pagination = siteDesignService.getPaginationForNodeDetail(everyonegroup.getUuid(), hostname, EntityDetail.class, productFolderUuid, totalNumProductsInPage, pageIdxNum, pageUuid);
            						
            						if(pagination!=null){
            							jspMap.put("pagination", pagination);
            						}
        						}
        					}
    		    			
    		    			
    		    			// get category or product list:
    		    			if(StringUtils.isNotBlank(productFolderUuid)){ // return all subfolders under this folder, and all products direct under this folder
    		    				EntityDetail folderDetail = productService.getEntityDetailByUuid(productFolderUuid);
    		    				if(folderDetail!=null && folderDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
    		    					EntityTreeNode folderNode = new EntityTreeNode();
    		    					folderNode.setPrettyName(folderDetail.getName());
    		    					folderNode.setSystemName(folderDetail.getEntityuuid());
    		    					treeService.updateEntityTreeNodeForProduct(folderNode, null, null, everyonegroup.getUuid());
    		    					
    		    					// find categorypage and productPage for current node:
    		    					String categoryOrProductPageUuid = productService.getCategoryOrProductPageUuidForEntityNode(folderNode.getSystemName(), PageDetail.Type.category, sitetype, null, null);
    		    					PageDetail categoryOrProductPage = siteDesignService.getPageDetailByUuid(categoryOrProductPageUuid);
    		    					if(!StringUtils.equals(pageUuid, PageDetail.FAKEPAGEUUID)){
        		    					String entityUrl = null;
        		    					if(categoryOrProductPage!=null){
        			    					entityUrl = productService.getEntityUrl(hostname, folderNode, sitetype, org.getOrguuid(), categoryOrProductPage.getUrl(), null);
        		    					}
        		    					folderNode.setUrl(entityUrl);
    		    						
    		    					}else{
    		    						folderNode.setUrl("http://"+applicationConfig.getHostName());
    		    					}
    		    					
    		    					
    		    					// fill folderNode with all subFolders
    		    					List<TreeNode> subFolders = new ArrayList<TreeNode>();
    			    				treeService.findSubFoldersUnderFolder(everyonegroup.getUuid(), subFolders, EntityTreeNode.class, folderNode.getSystemName(), false, true);
    			    				if(subFolders!=null && subFolders.size()>0){
    			    					for(TreeNode f : subFolders){
    			    						
    			    						// add default photo:
    			    						treeService.updateEntityTreeNodeForProduct((EntityTreeNode)f, null, null, everyonegroup.getUuid());
    			    						
    			    						
    			    						
    			    						if(!StringUtils.equals(pageUuid, PageDetail.FAKEPAGEUUID)){
        			    						String entityUrl = null;
        			    						
        			    						String subCategoryOrProductPageUuid = productService.getCategoryOrProductPageUuidForEntityNode(f.getSystemName(), PageDetail.Type.category, sitetype, categoryOrProductPageUuid, null);
        			    						PageDetail subCategoryOrProductPage = siteDesignService.getPageDetailByUuid(subCategoryOrProductPageUuid);
        			    						if(subCategoryOrProductPage!=null){
        				    						entityUrl = productService.getEntityUrl(hostname, (EntityTreeNode)f, sitetype, org.getOrguuid(), subCategoryOrProductPage.getUrl(), null);
        			    						}
        			    						((EntityTreeNode)f).setUrl(entityUrl);
    			    						}else{
    			    							((EntityTreeNode)f).setUrl("http://"+applicationConfig.getHostName());
    			    						}
    			    						
    			    						
    			    						folderNode.addSubnode((EntityTreeNode)f);
    			    					}
    			    				}
    			    				
    		    					
    		    					// *************** total returned children product (not folders) should based on "moduleProductListAttr"!!!
    		    					if(totalNumProductsInPage>0){
    		    						
    		    						// find products within totalNumProductsInPage
    	    							List<NodeDetail> foundProducts = productService.findProductsInfolderForPreview(everyonegroup.getUuid(), folderNode.getSystemName(), totalNumProductsInPage, pageIdxNum);
    	    							if(foundProducts!=null && foundProducts.size()>0){
    	    								
    	    								// find default or schedule productPage in category first
    	    								String defaultProductPageDefinedInCat = productService.getCategoryOrProductPageUuidForEntityNode(folderNode.getSystemName(), PageDetail.Type.product, sitetype, null, null);
    	    								
    		    							List<EntityTreeNode> productsForPage = new ArrayList<EntityTreeNode>();
    		    							for(NodeDetail d : foundProducts){
    		    								// transfer entitydetail to entityTreeNode
    		    								EntityTreeNode tn = new EntityTreeNode();
    		    								tn.setPrettyName(((EntityDetail)d).getName());
    		    								tn.setSystemName(((EntityDetail)d).getEntityuuid());
    		    								
    		    								treeService.updateEntityTreeNodeForProduct(tn, null, null, everyonegroup.getUuid());
    		    								
    		    								if(!StringUtils.equals(pageUuid, PageDetail.FAKEPAGEUUID)){
        		    								String entityUrl = null;
        		    								String subCategoryOrProductPageUuid = productService.getCategoryOrProductPageUuidForEntityNode(((EntityDetail)d).getEntityuuid(), PageDetail.Type.product, sitetype, null, defaultProductPageDefinedInCat);
        		    								PageDetail subCategoryOrProductPage = siteDesignService.getPageDetailByUuid(subCategoryOrProductPageUuid);
        		    								if(subCategoryOrProductPage!=null){
        			    								entityUrl = productService.getEntityUrl(hostname, tn, sitetype, org.getOrguuid(), null, subCategoryOrProductPage.getUrl());
        		    								}
        		    								tn.setUrl(entityUrl);
    		    									
    		    								}else {
    		    									tn.setUrl("http://"+applicationConfig.getHostName());
    		    								}
    		    								
    		    								productsForPage.add(tn);
    		    							}
    		    							
    		    							folderNode.addSubnodes(productsForPage);
    	    							}
    		    						
    		    						
    		    					}else{ // display all
    			    					
    			    					// fill folderNode with all direct products
    				    				List<NodeDetail> subProducts = productService.findProductsInfolder(everyonegroup.getUuid(), folderNode.getSystemName());
    				    				if(subProducts!=null && subProducts.size()>0){
    	    								// find default or schedule productPage in category first
    	    								String defaultProductPageDefinedInCat = productService.getCategoryOrProductPageUuidForEntityNode(folderNode.getSystemName(), PageDetail.Type.product, sitetype, null, null);
    				    					
    				    					for(NodeDetail ed : subProducts){
    				    						
    				    						EntityTreeNode p = new EntityTreeNode();
    				    						p.setPrettyName(((EntityDetail)ed).getName());
    				    						p.setSystemName(((EntityDetail)ed).getEntityuuid());
    		    								treeService.updateEntityTreeNodeForProduct(p, null, null, everyonegroup.getUuid());
    				    						
    				    						if(!StringUtils.equals(pageUuid, PageDetail.FAKEPAGEUUID)){
        				    						String entityUrl = null;
        		    								String subCategoryOrProductPageUuid = productService.getCategoryOrProductPageUuidForEntityNode(p.getSystemName(), PageDetail.Type.product, sitetype, null, defaultProductPageDefinedInCat);
        		    								PageDetail subCategoryOrProductPage = siteDesignService.getPageDetailByUuid(subCategoryOrProductPageUuid);
        		    								if(subCategoryOrProductPage!=null){
        			    								entityUrl = productService.getEntityUrl(hostname, p, sitetype, org.getOrguuid(), null, subCategoryOrProductPage.getUrl());
        		    								}
        				    						p.setUrl(entityUrl);
    				    							
    				    						}else{
    				    							p.setUrl("http://"+applicationConfig.getHostName());
    				    						}
    				    						
    				    						folderNode.addSubnode(p);
    				    					}
    				    				}
    		    						
    		    					}
    			    				
    			    				jspMap.put("productTree", folderNode);
    		    					
    		    				}
    		    				
    		    				
    		    			}
    		    			
    		    			jspMap.put("org", org);
    		    			jspMap.put("pageid", pageUuid);
    		    			
    		    			
    		    		}
    		    		
    		    		// if module hase ModuleEntityCategoryListAttribute
    		    		if(moduleEntityCategoryListAttr!=null){
    		    			
    		    			// determine what the cattype first:
    		    			ModuleEntityCategoryListAttribute.Type theCatType = ModuleEntityCategoryListAttribute.Type.Product; // default is product
    		    			if(StringUtils.isNotBlank(moduleEntityCategoryListAttr.getCatType()) 
    		    					&& !moduleEntityCategoryListAttr.getCatType().equals(ModuleEntityCategoryListAttribute.Type.Product)){
    		    				theCatType = ModuleEntityCategoryListAttribute.Type.valueOf(moduleEntityCategoryListAttr.getCatType());
    		    			}
    		    			
    		    			// level of category
    		    			int levelOfCategory = 3; // from root to sub to subsub
    		    			if(moduleEntityCategoryListAttr.getLevelOfCategory()>=0){
    		    				levelOfCategory = moduleEntityCategoryListAttr.getLevelOfCategory();
    		    			}
    		    			
    		    			// sort type
    		    			ModuleEntityCategoryListAttribute.SortType theSortType = ModuleEntityCategoryListAttribute.SortType.NotSort;
    		    			if(StringUtils.isNotBlank(moduleEntityCategoryListAttr.getSortType()) 
    		    					&& !moduleEntityCategoryListAttr.getSortType().equals(ModuleEntityCategoryListAttribute.SortType.NotSort)){
    		    				theSortType = ModuleEntityCategoryListAttribute.SortType.valueOf(moduleEntityCategoryListAttr.getSortType());
    		    			}
    		    			
    		    			// for product 
    		    			if(theCatType!=null && theCatType.equals(ModuleEntityCategoryListAttribute.Type.Product)){
    		    				GeneralTreeNode	productCatTree = treeService.generateTree(EntityTreeNode.class, true, org.getId(), levelOfCategory);
    		    				
    		    				// for sort
    		    				if(!theSortType.equals(ModuleEntityCategoryListAttribute.SortType.NotSort)){
    		    					TreeHelp.sortGeneralTree(productCatTree, theSortType);
    		    				}
    		    				
    		    				// ****   for extra info setup: url, isSelected ...
    		    				String currentSelectedEntityUuid = null;
    		    				if(StringUtils.isNotBlank(entityUuid)){
    		    					currentSelectedEntityUuid = entityUuid;
    		    				}else {
    		    					currentSelectedEntityUuid = productFolderUuid;
    		    				}
    		    				// 
    		    				EntityDetail currentSelectedEntity = productService.getEntityDetailByUuid(currentSelectedEntityUuid);
        						List<String> allSelectedNodesUuids = new ArrayList<String>();
    		    				if(currentSelectedEntity!=null){
    	    						String[] selectedPaths = StringUtils.isNotBlank(currentSelectedEntity.getPath())?currentSelectedEntity.getPath().split("/"):null;
    	    						if(selectedPaths!=null && selectedPaths.length>0){
    	    							for(String path : selectedPaths){
    	    								allSelectedNodesUuids.add(path);
    	    							}
    	    						}
    	    						allSelectedNodesUuids.add(currentSelectedEntity.getEntityuuid());
    		    				}
    		    				treeService.productTreeFurtherProcess(hostname, productCatTree, pageUuid, allSelectedNodesUuids, org.getOrguuid(), null, null);
    		    				
    		    				jspMap.put("categoryTree", productCatTree);
    		    				
    		    			}
    		    			
    		    		}
    		    		
    	    			Locale locale = Locale.getDefault();
    	    			try{
    	    	    		if(StringUtils.isNotBlank(viewuuid)){
    	    	    			InstanceView instanceview = siteDesignService.getInstanceViewByUuid(viewuuid);
    	    	    			if(instanceview!=null){
    	    						jspOutput.append(jspRenderer.render("module/org/"+instanceview.getOrgid()+"/"+instanceview.getInstanceviewuuid(), jspMap, locale));
    	    	    			}else{
    	    	    				jspOutput.append(jspRenderer.render("module/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid(), jspMap, locale));
    	    	    			}
    	    	    		}else{
    	    	    			jspOutput.append(jspRenderer.render("module/org/"+moduleDetail.getOrganization_id()+"/"+moduleDetail.getModuleuuid(), jspMap, locale));
    	    	    		}
    	    				
    	    			}catch (IOException e) {
    	    				
    	    				StringBuilder errorOutput = new StringBuilder();
    	    				errorOutput.append("<div class='sysErrorMsg pageNotFound' style='color: red;'>");
    	    				
    	    				InstanceView instanceview = siteDesignService.getInstanceViewByUuid(viewuuid);
    	    				if(instanceview!=null){
    	    					errorOutput.append("System can't find view's jsp file for view: ").append(instanceview.getViewname()).append("(").append(viewuuid).append(")");
    	    				}else {
    	    					errorOutput.append("System can't find moduledetail's jsp file for moduledetail: ").append(moduleDetail.getPrettyname()).append("(").append(moduleuuid).append(")");
    	    				}
    	    				
    	    				
    	    				errorOutput.append("</div>");
    	    				
    	    				jspOutput.append(errorOutput);
    					}
    	    			
    		    	}
    		    			
    		    	
    // the reason for not deleting the next example is there has locale sample I could use in the future!!!	    	
//    		    	// get module id : c7aa6422-c458-4da5-8ee2-0757188b6fb9
//    		    	
//    		    	// get instance 
//    		    	List<ModuleInstance> ins = siteDesignService.findModuleInstancesByModuleUuid("c7aa6422-c458-4da5-8ee2-0757188b6fb9");
//    		    	if(ins!=null && ins.size()>0){
//    		    		Module mo = SitedesignHelper.getModuleFromXml(ins.get(0).getInstance());
//    		    		if(mo!=null){
//    		    			Locale locale = Locale.getDefault();
//    		    			if(requestedLocale != null && org.springframework.util.StringUtils.hasText(requestedLocale)) {
//    							if(requestedLocale.contains("_")) {
//    								String[] parts = requestedLocale.split("_",2);
//    								locale = new Locale(parts[0], parts[1]);
//    							} else {
//    								locale = new Locale(requestedLocale);
//    							}
//    		    			}
//    		    			
//    		    			Map<String,Object> jspMap = new HashMap<String,Object>();
//    		    			jspMap.put("module", mo);
////    		    			jspMap.put("localeUsed", locale);
////    		    			jspMap.put("jspMessage", "This is the value of jsp message");
////    		    			jspMap.put("costMessage", 4567.89);			
//    		    			try {
//    							jspOutput = jspRenderer.render("module/org/1/c7aa6422-c458-4da5-8ee2-0757188b6fb9", jspMap, locale);
//    						} catch (IOException e) {
//    							logger.error(e.toString());
//    							e.printStackTrace();
//    							jspOutput = e.toString();
//    						}
//    		    			
//    		    		}
//    		    	}
    		    	
    	    		
    	    	}
    	    	
    	    }
    		
    	}
	    
	    // temple test solution for debug interface:
//	    if(jspOutput.length()<1){
//	    	jspOutput.append("no module");
//	    }
	    
		return new ResponseEntity<String>(jspOutput.toString(), responseHeaders, HttpStatus.CREATED);

	}
	
	// nothing just test.
	@RequestMapping(value="/getProductList", method=RequestMethod.GET)
	public ResponseEntity<String> getProductList (HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "totalNumProductsInPage", required = false) String totalNumProductsInPage,
			@RequestParam(value = "hasPagination", required = false) boolean hasPagination,
			@RequestParam(value = "desktopProductPage", required = false) String desktopProductPageUuid,
			@RequestParam(value = "mobileProductPage", required = false) String mobileProductPageuuid,
			
			@RequestParam(value = "category", required = false) String productFolderUuid,
			
			@RequestParam(value="locale", required=false) String requestedLocale
			) {
		
		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");	
	    
	    StringBuilder jspOutput = new StringBuilder();

	    jspOutput.append("this is product list page!!!");
		
		return new ResponseEntity<String>(jspOutput.toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/getphoto", method = RequestMethod.GET)
	public void getPhoto(
			@RequestParam(value = "id", required = true) String id, 
			@RequestParam(value = "size", required = false) Integer size,
			HttpServletResponse response, HttpServletRequest request
			) {
		
		// Prepare acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.IMAGE_JPEG);
		acceptableMediaTypes.add(MediaType.IMAGE_GIF);
		acceptableMediaTypes.add(MediaType.IMAGE_PNG);
		
		// Prepare header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<String> entity = new HttpEntity<String>(headers);	
		
		// Send the request as GET
		ResponseEntity<byte[]> result = restTemplate.exchange(
				"http://"+applicationConfig.getFileServerLocation()+"/web/getimage/id/{id}/size/{size}",
				HttpMethod.GET, 
				entity, 
				byte[].class, 
				id, size);
		
		// Display the image
		if(result.getBody()==null){
			BufferedImage bi = WebUtil.getImageFromString("File not found!");
			if(bi!=null){
				write(response, bi);
			}
			
		}else{
			write(response, result.getBody());
		}
	}
    
    @RequestMapping(value="/getTxt", method=RequestMethod.GET)
    public void getTxt(
			@RequestParam(value = "id", required = true) String id, 
			@RequestParam(value = "characters", required = false) Integer characters,
			HttpServletResponse response, HttpServletRequest request
    		
    		){
		MediaDetail mediaDetail = mediaService.getMediaDetailByUuid(id);
		
		if(mediaDetail!=null){
			
			// Prepare acceptable media type
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
//			MediaType TEXT_PLAIN_UTF8 = new MediaType("text", "html", Charset.forName("UTF-8"));
			
			// Prepare header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
//			headers.setContentType(TEXT_PLAIN_UTF8);
//			headers.add("Content-Type", "text/html; charset=utf-8");
			HttpEntity<String> entity = new HttpEntity<String>(headers);	
			
			// Send the request as GET
			ResponseEntity<String> result = restTemplate.exchange(
					"http://"+applicationConfig.getFileServerLocation()+"/web/getTxt/id/{id}",
					HttpMethod.GET, 
					entity, 
					String.class, 
					id);
			
			if(result.getBody()!=null){
				
//		        File someFile = new File(mediaDetail.getPrettyname());
//		        try {
//			        FileOutputStream fos = new FileOutputStream(someFile);
//					fos.write(result.getBody());
//			        fos.flush();
//			        fos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				write(response, result.getBody());
				
				
			}else{
				
			}
	    	
		}
    }
    
    @RequestMapping(value="/getPdfInText", method=RequestMethod.GET)
    public void getPdfInText(
			@RequestParam(value = "id", required = true) String id, 
			HttpServletResponse response, HttpServletRequest request
    		){
    	
		MediaDetail mediaDetail = mediaService.getMediaDetailByUuid(id);
		
		if(mediaDetail!=null){
			
			// Prepare acceptable media type
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
			
			// Prepare header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<String> entity = new HttpEntity<String>(headers);	
			
			// Send the request as GET
			ResponseEntity<String> result = restTemplate.exchange(
					"http://"+applicationConfig.getFileServerLocation()+"/web/getPdfInString/id/{id}/pages/{pages}",
					HttpMethod.GET, 
					entity, 
					String.class, 
					id, 2);
			
			if(result.getBody()!=null){
				
//		        File someFile = new File(mediaDetail.getPrettyname());
//		        try {
//			        FileOutputStream fos = new FileOutputStream(someFile);
//					fos.write(result.getBody());
//			        fos.flush();
//			        fos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				write(response, result.getBody());
				
				
			}else{
				
			}
	    	
		}
    	
    	
    }

	

}
