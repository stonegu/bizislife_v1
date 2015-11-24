package com.bizislife.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.ProductService;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.util.annotation.PublicPage;

@PublicPage
@Controller
public class ApiController {
	
    @Autowired
    protected ApplicationConfiguration applicationConfig;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SiteDesignService siteDesignService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private TreeService treeService;

    // get category list
	@RequestMapping(value = "/categoryApi/list/org/{orgid}/category/{categoryid}", method = RequestMethod.GET)
	public @ResponseBody ApiResponse getCategoryList(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("categoryid") String categoryUuid
			){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);

    	// get everyone group from biz
    	Accountgroup everyoneGroup = accountService.getEveryoneGroup();

    	Organization org = accountService.getOrgByUuid(orgUuid);
    	EntityDetail categoryDetail = productService.getEntityDetailByUuid(categoryUuid);
    	
    	if(org!=null && everyoneGroup!=null && categoryDetail!=null && categoryDetail.getOrganization_id().longValue()==org.getId().longValue()){
    		if(categoryDetail.getType().equals(EntityDetail.EntityType.folder.getCode())){
    			
    			Permission mergedPermission = permissionService.getMergedPermissionForGroups(true, everyoneGroup.getId());
    			
    			// folders 
    			List<TreeNode> folderDetails = treeService.findTreeNodesByParentNodeUuid_v2(everyoneGroup.getUuid(), 
    					EntityDetail.class, categoryUuid, true, false, true);
    			if(folderDetails!=null){
    				for(TreeNode f : folderDetails){
    					treeService.updateEntityTreeNodeForProduct((EntityTreeNode)f, null, null, mergedPermission);
    				}
    			}
    			
    			// products
    			List<TreeNode> productDetails = treeService.findTreeNodesByParentNodeUuid_v2(everyoneGroup.getUuid(), 
    					EntityDetail.class, categoryUuid, false, true, true);
    			if(productDetails!=null){
    				for(TreeNode p : productDetails){
    					treeService.updateEntityTreeNodeForProduct((EntityTreeNode)p, null, null, mergedPermission);
    				}
    			}
    			
    			apires.setSuccess(true);
    			Map<String, Object> productDetailsMap = new HashMap<String, Object>();
    			productDetailsMap.put("Description", "products list");
    			productDetailsMap.put("List", productDetails);
    			apires.setResponse1(productDetailsMap);
    			Map<String, Object> folderDetailsMap = new HashMap<String, Object>();
    			folderDetailsMap.put("Description", "category list");
    			folderDetailsMap.put("List", folderDetails);
    			apires.setResponse2(folderDetailsMap);
    			
    		}else{
    			apires.setResponse1("Category selected is not a category: "+categoryUuid);
    		}
    		
    	}else{
    		apires.setResponse1("System don't have enough info to get product data.");
    	}
    	
    	return apires;
		
	}
    
    
    
    // get product's group
	@RequestMapping(value = "/productApi/org/{orgid}/product/{productid}", method = RequestMethod.GET)
	public @ResponseBody ApiResponse getProduct(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("productid") String productUuid
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);

    	Organization org = accountService.getOrgByUuid(orgUuid);
    	EntityDetail entityDetail = productService.getEntityDetailByUuid(productUuid);
    	
    	// get everyone group from biz
    	Accountgroup everyoneGroup = accountService.getEveryoneGroup();
    	// check permission
    	boolean isPreviewPermissionAllowed = permissionService.isPermissionAllowedForGroup(everyoneGroup!=null?everyoneGroup.getId():null, Permission.Type.preview, productUuid);
    	
    	if(isPreviewPermissionAllowed && org!=null && entityDetail!=null && entityDetail.getOrganization_id().longValue()==org.getId().longValue()){
			Module instance = SitedesignHelper.getModuleFromInstance(entityDetail);
			ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(entityDetail.getModuleuuid());
			if(instance!=null && moduleDetail!=null){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				SitedesignHelper.updateModuleInstanceByModule(module, instance);
				
				apires.setSuccess(true);
				apires.setResponse1(instance);
			}
    	}else{
    		if(!isPreviewPermissionAllowed){
    			apires.setResponse1("This product don't have preview permission set for everyone group.");
    		}else{
    			apires.setResponse1("System don't have enough info to get product data.");
    		}
    	}
    	
    	return apires;
	}
    
    // get product's group
	@RequestMapping(value = "/productApi/org/{orgid}/product/{productid}/moduleGroup/{groupName}", method = RequestMethod.GET)
	public @ResponseBody ApiResponse getProductGroup(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("productid") String productUuid,
			@PathVariable("groupName") String groupName
		){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	// get everyone group from biz
    	Accountgroup everyoneGroup = accountService.getEveryoneGroup();
    	// check permission
    	boolean isPreviewPermissionAllowed = permissionService.isPermissionAllowedForGroup(everyoneGroup!=null?everyoneGroup.getId():null, Permission.Type.preview, productUuid);
    	
    	Organization org = accountService.getOrgByUuid(orgUuid);
    	EntityDetail entityDetail = productService.getEntityDetailByUuid(productUuid);
    	if(isPreviewPermissionAllowed && org!=null && entityDetail!=null && entityDetail.getOrganization_id().longValue()==org.getId().longValue()){
			Module instance = SitedesignHelper.getModuleFromXml(entityDetail.getDetail());
			ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(entityDetail.getModuleuuid());
			if(instance!=null && moduleDetail!=null){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				SitedesignHelper.updateModuleInstanceByModule(module, instance);
				Map<String, List<AttrGroup>> nameGroupsMap = instance.getGroups();
				if(nameGroupsMap!=null){
					List<AttrGroup> groups = nameGroupsMap.get(groupName);
					if(groups!=null){
						apires.setSuccess(true);
						apires.setResponse1(groups);
					}
				}
			}
    		
    	}
    	
    	return apires;
	}
    	
    
    // get product's attribute
	@RequestMapping(value = "/productApi/org/{orgid}/product/{productid}/moduleGroup/{groupName}/moduleAttr/{attributeName}", method = RequestMethod.GET)
	public @ResponseBody ApiResponse getProductGroupAttrs(
			@PathVariable("orgid") String orgUuid, 
			@PathVariable("productid") String productUuid,
			@PathVariable("groupName") String groupName,
			@PathVariable("attributeName") String attributeName
		){
    	
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	// get everyone group from biz
    	Accountgroup everyoneGroup = accountService.getEveryoneGroup();
    	// check permission
    	boolean isPreviewPermissionAllowed = permissionService.isPermissionAllowedForGroup(everyoneGroup!=null?everyoneGroup.getId():null, Permission.Type.preview, productUuid);
    	
    	Organization org = accountService.getOrgByUuid(orgUuid);
    	EntityDetail entityDetail = productService.getEntityDetailByUuid(productUuid);
    	if(isPreviewPermissionAllowed && org!=null && entityDetail!=null && entityDetail.getOrganization_id().longValue()==org.getId().longValue()){
			Module instance = SitedesignHelper.getModuleFromXml(entityDetail.getDetail());
			ModuleDetail moduleDetail = siteDesignService.getModuleDetailByUuid(entityDetail.getModuleuuid());
			if(instance!=null && moduleDetail!=null){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				SitedesignHelper.updateModuleInstanceByModule(module, instance);
				
				Map<String, List<AttrGroup>> nameGroupsMap = instance.getGroups();
				if(nameGroupsMap!=null){
					List<AttrGroup> groups = nameGroupsMap.get(groupName);
					if(groups!=null){
						Map<String, List<ModuleAttribute>> groupAttrsMap = new HashMap<String, List<ModuleAttribute>>();
						for(AttrGroup g : groups){
							Map<String, List<ModuleAttribute>> nameAttrsMap =  g.getAttrs();
							if(nameAttrsMap!=null){
								List<ModuleAttribute> attrs = nameAttrsMap.get(attributeName);
								if(attrs!=null && attrs.size()>0){
									groupAttrsMap.put(g.getGroupUuid(), attrs);
								}
							}
						}
						if(groupAttrsMap.size()>0){
							apires.setSuccess(true);
							apires.setResponse1(groupAttrsMap);
						}
					}
				}
				
			}
    		
    	}
    	
    	
    	return apires;
    }
    	
    	
    
    
    

}
