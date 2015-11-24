package com.bizislife.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.entity.Attribute;
import com.bizislife.core.entity.Entity;
import com.bizislife.core.entity.MetaData;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.Accountprofile;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleMeta;
import com.bizislife.core.hibernate.pojo.Msgbody;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.hibernate.pojo.Tree;
import com.bizislife.core.service.*;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute;
import com.bizislife.core.siteDesign.module.ModuleMoneyAttribute;
import com.bizislife.core.siteDesign.module.ModuleProductListAttribute;
import com.bizislife.core.siteDesign.module.Money;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.validation.ValidationSet;

@Controller
public class productController {
	private static final Logger logger = LoggerFactory.getLogger(productController.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private TreeService treeService;
    
	@Autowired
	MessageService messageService;

    @Autowired
    private PermissionService permissionService;
    
	@Autowired
	PaymentService paymentService;
    
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;

    
    @RequestMapping(value="/products", method=RequestMethod.GET)
    public String newsPage(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	model.put("currentPageId", new StringBuilder("products_").append(orguuid).toString());
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
    	
    	
        return "products";
    }
    
    @RequestMapping(value="/productNodeCreate", method=RequestMethod.GET)
    public @ResponseBody ApiResponse productNodeCreate(
    		@RequestParam(value = "newNodetype", required = true) String newNodetype,
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
        		EntityDetail.EntityType nodetype = EntityType.fromCode(newNodetype);
        		if(nodetype!=null){
        			
        			String newNodeUuid = null;
        			
        			if(StringUtils.isNotBlank(cloneFromUuid)){
        				res = productService.cloneProductNode(nodetype, parentNodeUuid, newNodeName, cloneFromUuid);
        				if(res.isSuccess()){
            				newNodeUuid = (String)res.getResponse1();
        				}
        			}else{
        				
           				AccountDto loginAccount = accountService.getCurrentAccount();
        				boolean isPermissionModifyAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, parentNodeUuid);
        				if(isPermissionModifyAllowed){
            				newNodeUuid = productService.newProductNode(nodetype, parentNodeUuid, newNodeName);
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
        		}
    			
    		}else{
    			res.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
    		}
    		
    	}else{
    		res.setResponse1("No enough information to process, try refresh the page!");
    	}
    	
    	return res;
    }

	@RequestMapping(value="/productTreeNodeDetail", method=RequestMethod.GET)
	public ModelAndView productTreeNodeDetail(
			
			@RequestParam(value = "nodeUuid", required = false) String nodeUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = null;
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
//		ModelAndView mv = new ModelAndView("productTreeNodeDetail");
		
		EntityDetail entityDetail = productService.getEntityDetailByUuid(nodeUuid);
		if(entityDetail!=null){

			// check the read permission for detail for loginAccount
			boolean readPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.read, entityDetail.getEntityuuid());
			
			if(readPermissionAllow){
				mv = new ModelAndView("productTreeNodeDetail");
				
				// add host info
				mv.addObject("bizhost", "http://"+applicationConfig.getHostName());
				
				
				// check the modify permission
				boolean modifyPermissionAllow = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, entityDetail.getEntityuuid());
				mv.addObject("modifyPermissionAllow", modifyPermissionAllow);
				
				
				// hold all targets' uuids
				Set<String> metaTargetUuids = new HashSet<String>();
				// hold all targetUuid with target's MetaData
				Map<String, MetaData> targetWithMetaDataMap = new HashMap<String, MetaData>();
				
				metaTargetUuids.add(entityDetail.getEntityuuid());
				
				mv.addObject("entityDetail", entityDetail);

				// get entity's org
				Organization org = accountService.getOrgById(entityDetail.getOrganization_id());
				mv.addObject("org", org);
				
				// get entity's module instance
				Module instance = SitedesignHelper.getModuleFromXml(entityDetail.getDetail());
				ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(entityDetail.getModuleuuid());
				if(instance!=null && moduleDetail!=null){
					metaTargetUuids.add(moduleDetail.getModuleuuid());
					Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
					SitedesignHelper.updateModuleInstanceByModule_v2(module, instance);
					mv.addObject("instance", instance);
					mv.addObject("moduleDetailForProduct", new GeneralSelectionType(moduleDetail.getModuleuuid(), moduleDetail.getPrettyname(), Boolean.TRUE));
					
					
					boolean hasModuleLinkAttr = false;
					boolean hasModuleProductListAttr = false;
					boolean hasModuleMoneyAttr = false;
					boolean hasModuleCatListAttr = false;
					if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
						for(AttrGroup g : instance.getAttrGroupList()){
							metaTargetUuids.add(g.getGroupUuid());
							metaTargetUuids.add(g.getModuleGroupUuid());
							if(g.getAttrList()!=null && g.getAttrList().size()>0){
								for(ModuleAttribute a : g.getAttrList()){
									
									if(!hasModuleLinkAttr && a.getClass().equals(ModuleLinkAttribute.class)){
										hasModuleLinkAttr = true;
									}
									if(!hasModuleMoneyAttr && a.getClass().equals(ModuleMoneyAttribute.class)){
										hasModuleMoneyAttr = true;
									}
									
									metaTargetUuids.add(a.getUuid());
									metaTargetUuids.add(a.getModuleAttrUuid());
								}
							}
						}
					}
					
					// ***** add some special param for attribute:
					if(hasModuleLinkAttr){
						mv.addObject("moduleLinkAttr_linkRels", ModuleLinkAttribute.linkRel.values());
						mv.addObject("moduleLinkAttr_linkTargets", ModuleLinkAttribute.linkTarget.values());
					}
					
					if(hasModuleMoneyAttr){
						mv.addObject("availableCurrencies", Money.getAvailableCurrencies());
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
				
				// find closest fold's module setup for product.
				if(entityDetail.getType().equals(EntityDetail.EntityType.entity.getCode())){
					// find if folder has setup module already!!
					EntityDetail folderHasModuleSetup = productService.getClosestFolderHasModuleDefined(entityDetail.getEntityuuid());
					if(folderHasModuleSetup!=null){
						ModuleDetail moduleDetailForFolder = siteDesignService.getModuleDetailByUuid(folderHasModuleSetup.getModuleuuid());
						if(moduleDetailForFolder!=null){
							mv.addObject("folderHasModuleSetup", new GeneralSelectionType(folderHasModuleSetup.getEntityuuid(), folderHasModuleSetup.getName(), Boolean.TRUE));
							mv.addObject("moduleDetailForFolder", new GeneralSelectionType(moduleDetailForFolder.getModuleuuid(), moduleDetailForFolder.getPrettyname(), Boolean.TRUE));
							
						}else{ // remove module setup for folder since the moduleDetail is not exist in DB
							folderHasModuleSetup.setModuleuuid(null);
							folderHasModuleSetup.setDetail(null);
							productService.saveEntityDetail(folderHasModuleSetup);
//							productService.removeModuleSetupforEntityDetail(folderHasModuleSetup.getId());
						}
					}
					
					// find default product page setup
					String defaultProductPageUuid_desk = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.product, PageDetail.Type.Desktop, null, null);
					PageDetail defaultProductPage_desk = siteDesignService.getPageDetailByUuid(defaultProductPageUuid_desk);
					String defaultProductPageUuid_mobile = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.product, PageDetail.Type.Mobile, null, null);
					PageDetail defaultProductPage_mobile = siteDesignService.getPageDetailByUuid(defaultProductPageUuid_mobile);
					
					if(defaultProductPage_desk!=null){
						mv.addObject("defaultProductPage_desk", new GeneralSelectionType(defaultProductPage_desk.getPageuuid(), defaultProductPage_desk.getPrettyname(), true));
					}
					if(defaultProductPage_mobile!=null){
						mv.addObject("defaultProductPage_mobile", new GeneralSelectionType(defaultProductPage_mobile.getPageuuid(), defaultProductPage_mobile.getPrettyname(), true));
					}
					
				}else if(entityDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
					// find default category & product page setup
					// find default category page setup
					String defaultCategoryPageUuid_desk = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.category, PageDetail.Type.Desktop, null, null);
					PageDetail defaultCategoryPage_desk = siteDesignService.getPageDetailByUuid(defaultCategoryPageUuid_desk);
					String defaultCategoryPageUuid_mobile = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.category, PageDetail.Type.Mobile, null, null);
					PageDetail defaultCategoryPage_mobile = siteDesignService.getPageDetailByUuid(defaultCategoryPageUuid_mobile);
					// find default product page setup
					String defaultProductPageUuid_desk = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.product, PageDetail.Type.Desktop, null, null);
					PageDetail defaultProductPage_desk = siteDesignService.getPageDetailByUuid(defaultProductPageUuid_desk);
					String defaultProductPageUuid_mobile = productService.getCategoryOrProductPageUuidForEntityNode(entityDetail.getEntityuuid(), PageDetail.Type.product, PageDetail.Type.Mobile, null, null);
					PageDetail defaultProductPage_mobile = siteDesignService.getPageDetailByUuid(defaultProductPageUuid_mobile);
					
					if(defaultCategoryPage_desk!=null){
						mv.addObject("defaultCategoryPage_desk", new GeneralSelectionType(defaultCategoryPage_desk.getPageuuid(), defaultCategoryPage_desk.getPrettyname(), true));
					}
					if(defaultCategoryPage_mobile!=null){
						mv.addObject("defaultCategoryPage_mobile", new GeneralSelectionType(defaultCategoryPage_mobile.getPageuuid(), defaultCategoryPage_mobile.getPrettyname(), true));
					}
					if(defaultProductPage_desk!=null){
						mv.addObject("defaultProductPage_desk", new GeneralSelectionType(defaultProductPage_desk.getPageuuid(), defaultProductPage_desk.getPrettyname(), true));
					}
					if(defaultProductPage_mobile!=null){
						mv.addObject("defaultProductPage_mobile", new GeneralSelectionType(defaultProductPage_mobile.getPageuuid(), defaultProductPage_mobile.getPrettyname(), true));
					}
				}
				
				// get entitydetail's charUsage info
				int entityUsageWith100Multipled = paymentService.countModuleInstanceUsage(entityDetail);
				mv.addObject("entityUsageWith100Multipled", entityUsageWith100Multipled);
				
				
			}else{
				mv = new ModelAndView("error_general");
				List<String> errorList = new ArrayList<String>();
				errorList.add("User "+loginAccount.getFirstname()+" doesn't have read permission for target: "+entityDetail.getEntityuuid());
				mv.addObject("errorList", errorList);
			}
			
		}
		
		return mv;

	}
    
	@RequestMapping(value="/newAttribute", method=RequestMethod.GET)
	public ModelAndView newAttribute(
			
			@RequestParam(value = "productid", required = false) String productUuid,
			@RequestParam(value = "attrType", required = false) String attrType,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("attributeDetail");
		mv.addObject("productUuid", productUuid);
		mv.addObject("attrType", attrType);
		
		return mv;

	}
	
	@RequestMapping(value="/delAttribute")
	public @ResponseBody ApiResponse delAttribute(
			
			@RequestParam(value = "productid", required = false) String productUuid,
			@RequestParam(value = "attrid", required = false) String attrUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ApiResponse res = null;
		if(StringUtils.isNotBlank(productUuid) && StringUtils.isNotBlank(attrUuid)){
			String delAttrid = productService.delAttribute(productUuid, attrUuid);
			if(StringUtils.isNotBlank(delAttrid)){
				res =  new ApiResponse();
				res.setSuccess(true);
				res.setResponse1(delAttrid);
			}
		}
		return res;
	}
	
	@RequestMapping(value="/updateAttribute")
	public @ResponseBody ApiResponse updateAttribute(
			
			@RequestParam(value = "productId", required = false) String productUuid,
			@RequestParam(value = "attrType", required = false) String attrType,
			@RequestParam(value = "attrName", required = false) String attrName,
			@RequestParam(value = "attrValue", required = false) String attrValue,
			@RequestParam(value = "attrId", required = false) String attrUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ApiResponse res = null;
		
		if(StringUtils.isNotBlank(productUuid) && StringUtils.isNotBlank(attrType) && StringUtils.isNotBlank(attrName)){
			
			String aUuid = null;
			if(StringUtils.isNotBlank(attrUuid)){
				aUuid = productService.updateAttribute(productUuid, attrType, attrName, attrValue, attrUuid);
			}
			res = new ApiResponse();
			if(aUuid!=null){
				res.setSuccess(true);
				res.setResponse1(aUuid);
			}else{
				res.setSuccess(false);
			}
		}
		return res;
	}
	
	@RequestMapping(value="/newAttribute", method=RequestMethod.POST)
	public ModelAndView newAttribute(
			
			@RequestParam(value = "productId", required = false) String productUuid,
			@RequestParam(value = "attrType", required = false) String attrType,
			@RequestParam(value = "attrName", required = false) String attrName,
			@RequestParam(value = "attrValue", required = false) String attrValue,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("attributeBrief");
		if(StringUtils.isNotBlank(productUuid) && StringUtils.isNotBlank(attrType) && StringUtils.isNotBlank(attrName)){
			String aUuid = productService.newAttribute(productUuid, attrType, attrName, attrValue);

			if(StringUtils.isNotBlank(aUuid)){
				Entity entity = productService.getEntityWithAttrsByNodeUuid(productUuid);
				if(entity!=null && entity.getAllAttributes()!=null && entity.getAllAttributes().size()>0){
					Attribute attribute = null;
					for(Attribute a : entity.getAllAttributes()){
						if(a.getUuid().equals(aUuid)){
							attribute = a;
							break;
						}
					}
					mv.addObject("attr", attribute);
				}
			}
			
			
		}
		
		return mv;
	}
    
	@RequestMapping(value="/attrDetailInfo", method=RequestMethod.GET)
	public ModelAndView attrDetailInfo(
			
			@RequestParam(value = "productid", required = false) String productUuid,
			@RequestParam(value = "attrid", required = false) String attrUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("attributeDetail");
		
		mv.addObject("productUuid", productUuid);
		mv.addObject("attrUuid", attrUuid);
		
		// get attribute
		Entity entity = productService.getEntityWithAttrsByNodeUuid(productUuid);
		if(entity!=null && entity.getAllAttributes()!=null && entity.getAllAttributes().size()>0){
			Attribute attr = null;
			for(Attribute a : entity.getAllAttributes()){
				if(a.getUuid().equals(attrUuid)){
					attr = a;
					break;
				}
			}
			mv.addObject("attribute", attr);
			if(attr!=null){
				mv.addObject("attrType", attr.getClass().getSimpleName());
			}
		}
		
		return mv;

	}
	
	@RequestMapping(value="/newProductInstance", method=RequestMethod.GET)
	public ModelAndView newProductInstance(
			
			@RequestParam(value = "moduledetailUuid", required = false) String moduledetailUuid,
			@RequestParam(value = "entityDetailUuid", required = false) String entityDetailUuid,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		ModelAndView mv = null;
		
		ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(moduledetailUuid);
		EntityDetail entityDetail = productService.getEntityDetailByUuid(entityDetailUuid);
		if(moduleDetail!=null && entityDetail!=null){
			mv = new ModelAndView("moduleInstance");
			
			String oldModuleUuid = entityDetail.getModuleuuid();
			
			Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
			Organization org = accountService.getOrgById(entityDetail.getOrganization_id());
			
			if(module!=null){
				Module instance = SitedesignHelper.getDefaultModuleInstance(module);
				entityDetail.setDetail(SitedesignHelper.getXmlFromModule(instance));
				entityDetail.setModuleuuid(moduleDetail.getModuleuuid());
				
				// del all entityDetail's instanceviews if module changed
				if(!StringUtils.equals(oldModuleUuid, moduleDetail.getModuleuuid())){
					List<InstanceView> views = siteDesignService.findInstanceViewsByInstanceUuid(entityDetailUuid);
					if(views!=null && views.size()>0){
						for(InstanceView v : views){
//							v.setModuleuuid(moduleDetail.getModuleuuid());
//							siteDesignService.saveInstanceView(v);
							siteDesignService.delInstanceViewByUuid(v.getInstanceviewuuid());
						}
					}
					
				}
				
				Long id = productService.saveEntityDetail(entityDetail);
				if(id!=null){
					SitedesignHelper.updateModuleInstanceByModule(module, instance);
					
					mv.addObject("orgUuid", org.getOrguuid());
					mv.addObject("moduleInstance", entityDetail);
					mv.addObject("instance", instance);

				}
				
			}
			
			
		}else{
			mv = new ModelAndView("error_general");
		}
		
		
		return mv;

	}
	
	
    @RequestMapping(value="/updateEntityDetailValue")
    public @ResponseBody ApiResponse updateEntityDetailValue(
    		@RequestParam(value = "entityId", required = false) String entityUuid,
    		@RequestParam(value = "valueName", required = false) String updateValueName,
    		@RequestParam(value = "updateValue", required = false) String updateValue
		){
    	
    	return productService.updateEntityDetailValue(entityUuid, updateValueName, updateValue);
    }
    	
    @RequestMapping(value="/entityNodeDelete")
    public @ResponseBody ApiResponse entityNodeDelete(
    		@RequestParam(value = "nodeId", required = false) String entityUuid
		){
    	
    	ApiResponse apires = treeService.delNodeDetail(EntityDetail.class, entityUuid);
    	
    	
    	return apires;
    }
    	
    @RequestMapping(value="/deleteModuleSelectionForEntity")
    public @ResponseBody ApiResponse deleteModuleSelectionForEntity(
    		@RequestParam(value = "entityUuid", required = false) String entityUuid
		){
    	
    	ApiResponse apires = productService.deleteModuleSelectionForEntity(entityUuid);
    	
    	
    	return apires;
    }
    
    @RequestMapping(value="/moveProductNode", method=RequestMethod.POST)
    public @ResponseBody ApiResponse moveProductNode(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.moveTreeNode_v2(EntityDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }
	
    @RequestMapping(value="/getEntityInfo", method=RequestMethod.GET)
    public ModelAndView getEntityInfo(
    		@RequestParam(value = "entityid", required = false) String entityUuid
		) {
    	ModelAndView mv = null;
    	
		mv = new ModelAndView("entityDetailInfo");

		
		return mv;
    }
    	
    	
	@RequestMapping(value="/getAllProductPageLinksForProduct")
	public @ResponseBody ApiResponse getAllProductPageLinksForProduct(
			
			@RequestParam(value = "productUuid", required = false) String productUuid,
			HttpServletResponse response, HttpServletRequest request) {
		
		ApiResponse res = null;
		
		EntityDetail entityDetail = productService.getEntityDetailByUuid(productUuid);
		if(entityDetail!=null){
			Organization org = accountService.getOrgById(entityDetail.getOrganization_id());
			if(org!=null){
				// get all product pages for org
				List<PageDetail> productPages = siteDesignService.findPagesByModuleTypeForOrg(org.getId(), ModuleDetail.Type.productModule);
				if(productPages!=null && productPages.size()>0){
					List<GeneralSelectionType> productPageUuids = new ArrayList<GeneralSelectionType>();
					res = new ApiResponse();
					res.setSuccess(true);
					
					for(PageDetail p : productPages){
						StringBuilder link = new StringBuilder();
						link.append("http://").append(applicationConfig.getHostName()).append("/getPage/").append("org/").append(org.getOrguuid()).append("/pageurl/").append(p.getUrl()).append("?entityid=").append(entityDetail.getEntityuuid());
						productPageUuids.add(
							new GeneralSelectionType(link.toString(), 
								new StringBuilder("[").append(PageDetail.Type.fromCode(p.getType()).name()).append("] ").append(p.getPrettyname()).toString(), 
								true));
					}
					
					Collections.sort(productPageUuids);
					
					res.setResponse1(productPageUuids);
					
				}
				
			}
		}
		
		return res;
	}

	@RequestMapping(value="/productInfoAnnouncement")
	public @ResponseBody ApiResponse productInfoAnnouncement(
			
			@RequestParam(value = "objuuid", required = false) String productUuid,
			@RequestParam(value = "messageBody", required = false) String messageBody,
			HttpServletResponse response, HttpServletRequest request) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		EntityDetail product = productService.getEntityDetailByUuid(productUuid);
		Date now = new Date();
		
		if(loginAccount!=null && product!=null){

			Accountprofile accountProfile = accountService.getAccountProfile(loginAccount.getId());
			StringBuilder accountName = new StringBuilder();
			if(accountProfile!=null){
				accountName.append(accountProfile.getFirstname()).append(" ").append(accountProfile.getLastname());
			}

			Organization belongToOrg = accountService.getOrgById(product.getOrganization_id());
			
			if(belongToOrg!=null){
				
				// 1) create message body, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed
				// 1) create message body
				Msgbody msgbody = new Msgbody();
				// testing private msg now
				msgbody.setBody("testing ...");
				
				// 2) create a topic
				Topic topic = new Topic(null,
						UUID.randomUUID().toString(),
						new StringBuilder("Product information is announced in ").append(belongToOrg.getOrgname()).toString(),
						new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(belongToOrg.getOrgsysname()).append(".").append("product.announcement").toString(),
						Topic.AccessLevel.privateTopic.getCode(),
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
						belongToOrg.getId(),
						"This topic is used when product information is announced",
						now,
						null,
						Topic.TopicType.SystemTopic.getCode(),
						null,
						null,
						null
						);
				Long topicId = messageService.newTopicWithAncestor(topic);
				
				// 3) create a tree node(s)
//				Long treeId = null;
//				if(topicId!=null){
//					treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//				}
				
				// 4) post to newsfeed
				if(msgbody!=null){
					
					// use ActivityLogData to pass data to messageService
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("operatorId", loginAccount.getId());
					dataMap.put("operatorName", accountName.toString());
					dataMap.put("orgId", belongToOrg.getId());
					dataMap.put("orgName", belongToOrg.getOrgname());
					dataMap.put("productUuid", product.getEntityuuid());
					dataMap.put("productName", product.getName());

					ActivityLogData activityLogData = new ActivityLogData();
					activityLogData.setDataMap(dataMap);
//					activityLogData.setDesc(desc);
					
					
					// do post ...
					messageService.postFeed(topicId, msgbody, ActivityType.announceProduct, activityLogData);
					apires.setSuccess(true);
				}
				
			}
			
		}
		
		return apires;
	}
	
	
    
}
