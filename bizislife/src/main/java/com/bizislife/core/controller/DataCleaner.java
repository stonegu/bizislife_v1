package com.bizislife.core.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.PageMeta;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.core.view.dto.AccountDto;

/**
 * 
 * this controller is used to clean project data. all methods can run multiple times without cause any problems.
 *
 */

@Controller
public class DataCleaner {
	private static final Logger logger = LoggerFactory.getLogger(DataCleaner.class);
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	PermissionService permissionService;

	@Autowired
	SiteDesignService siteDesignService;
	
	/**
	 * using jspCssGenerator instead!!
	 * 
	 * write all organization's module's jsp to tomcat server location. 
	 * This method need to run after re-deploy the project with old data.
	 * 
	 * @return
	 */
	@Deprecated
	@RequestMapping(value="/moduleJspCssGenerator", method = RequestMethod.GET)
    public @ResponseBody String moduleJspGenerator() {
    	
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount.isSystemDefaultAccount() && currentAccount.isBizAccount()){
	    	// get all orgs
	    	List<Organization> orgs = accountService.findAllOrgs();
	    	if(orgs!=null && orgs.size()>0){
	    		for(Organization o : orgs){
	    			List<ModuleDetail> details = siteDesignService.findOrgModules(o.getId());
	    			if(details!=null && details.size()>0){
	    				for(ModuleDetail m : details){
	    					siteDesignService.writeModuleJspToFile(m.getId());
	    					siteDesignService.writeModuleCssToFile(m.getId());
	    				}
	    			}
	    		}
	    	}
		}
    	
        return "<html><body><h1>Done.</h1></body></html>";

    }
	
	/**
	 * using jspCssGenerator instead!!
	 * 
	 * @return
	 */
	@Deprecated
	@RequestMapping(value="/instanceViewJspCssGenerator", method = RequestMethod.GET)
	public @ResponseBody String instanceViewJspCssGenerator(){
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount.isSystemDefaultAccount() && currentAccount.isBizAccount()){
			// get all orgs
			List<Organization> orgs = accountService.findAllOrgs();
			if(orgs!=null && orgs.size()>0){
				for(Organization o : orgs){
					List<InstanceView> views = siteDesignService.findOrgInstanceViews(o.getId());
					if(views!=null && views.size()>0){
						for(InstanceView v : views){
							siteDesignService.writeInstanceViewCssToFile(v.getId());
							siteDesignService.writeInstanceViewJspToFile(v.getId());
						}
					}
					
				}
			}
		}
		
        return "<html><body><h1>Done.</h1></body></html>";
	}

	@RequestMapping(value="/jspCssGenerator", method = RequestMethod.GET)
	public @ResponseBody String jspCssGenerator(){
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount.isSystemDefaultAccount() && currentAccount.isBizAccount()){
			
			// get all orgs
			List<Organization> orgs = accountService.findAllOrgs();
			
			if(orgs!=null && orgs.size()>0){
				for(Organization o : orgs){
					
					// for moduledetail
	    			List<ModuleDetail> details = siteDesignService.findOrgModules(o.getId());
	    			if(details!=null && details.size()>0){
	    				for(ModuleDetail m : details){
	    					siteDesignService.writeModuleJspToFile(m.getId());
	    					siteDesignService.writeModuleCssToFile(m.getId());
	    				}
	    			}
					
	    			// for instanceView
					List<InstanceView> views = siteDesignService.findOrgInstanceViews(o.getId());
					if(views!=null && views.size()>0){
						for(InstanceView v : views){
							siteDesignService.writeInstanceViewCssToFile(v.getId());
							siteDesignService.writeInstanceViewJspToFile(v.getId());
						}
					}
					
					// for page
					List<PageDetail> pagedetails_mobile = siteDesignService.findOrgPagesByType(o.getOrguuid(), PageDetail.Type.Mobile);
					List<PageDetail> pagedetails_desktop = siteDesignService.findOrgPagesByType(o.getOrguuid(), PageDetail.Type.Desktop);
					if(pagedetails_desktop!=null && pagedetails_desktop.size()>0){
						for(PageDetail p : pagedetails_desktop){
							siteDesignService.writePageCssToFile(p.getId());
						}
					}
					if(pagedetails_mobile!=null && pagedetails_mobile.size()>0){
						for(PageDetail p : pagedetails_mobile){
							siteDesignService.writePageCssToFile(p.getId());
						}
					}
					
					
				}
			}
		}
		
		
        return "<html><body><h1>Done.</h1></body></html>";
	}

	
	@RequestMapping(value="/setFullPermissionForSystemDefaultGroup", method = RequestMethod.GET)
	public @ResponseBody String setFullPermissionForSystemDefaultGroup(){
		
		List<Organization> orgs = accountService.findAllOrgs();
		if(orgs!=null && orgs.size()>0){
			for(Organization o : orgs){
				List<Accountgroup> systemGroups = accountService.findGroupByType(Accountgroup.GroupType.SystemDefault, o.getId());
				
				if(systemGroups!=null && systemGroups.size()>0){
					for(Accountgroup g : systemGroups){
						permissionService.setFullPermissionForGroup(g.getId());
					}
				}
			}
		}
		
        return "<html><body><h1>Done.</h1></body></html>";
	}

}
