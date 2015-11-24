package com.bizislife.core.controller;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.util.WebUtil;
import com.bizislife.util.annotation.PublicPage;

@PublicPage
@Controller
public class ProxyController {
	
    @Autowired
    protected ApplicationConfiguration applicationConfig;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SiteDesignService siteDesignService;


	@RequestMapping(value = "/proxyError", method = RequestMethod.GET)
	public ResponseEntity<String> proxyController(
			HttpServletResponse response, HttpServletRequest request) {

		
		String errorInfo = (String)request.getAttribute("errorInfo");
		
		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");	
	    
	    StringBuilder jspOutput = new StringBuilder();
	    
	    if(StringUtils.isNotBlank(errorInfo)){
	    	jspOutput.append(errorInfo);
	    }else{
		    jspOutput.append("This could because your URI is not allowed!<br/>");
		    jspOutput.append("There have two URLs allow: 'index' & 'page/YourPageName'");
	    }
	    
		
		return new ResponseEntity<String>(jspOutput.toString(), responseHeaders, HttpStatus.CREATED);

	}
	
    @RequestMapping(value="/index", method=RequestMethod.GET)
    public String indexPage(
			HttpServletRequest req,
    		ModelMap model){
    	
//    	String hostName = req.getHeader("host");
    	String hostName = (String)req.getAttribute("hostname");

		// find orgUuid by hostname
		Organization org = accountService.getOrgByHostName(hostName);
		if(org!=null){
			PageDetail pageDetail = siteDesignService.getOrgPageByTypeAndUrl(org.getId(), PageDetail.Type.Desktop, "index");
			if(pageDetail!=null){
				// /getPage/org/{orgid}/pageurl/{url}
				StringBuilder pageUri = new StringBuilder("/getPage/org/").append(org.getOrguuid()).append("/pageurl/index");
//				pageUri.append("?categoryid=").append(productFolderUuid!=null?productFolderUuid:"");
//				pageUri.append("&pageidx=").append(pageIndex!=null?pageIndex:"");
//				pageUri.append("&entityid=").append(entityUuid!=null?entityUuid:"");
				
				return "forward:"+pageUri.toString();
//				return "redirect:http://"+applicationConfig.getHostName()+pageUri.toString();
			}else{
	    		StringBuilder errorInfo = new StringBuilder("System can't get page by pagename: index.");
	    		req.setAttribute("errorInfo", errorInfo.toString());
			}
			
		}else{
			StringBuilder errorInfo = new StringBuilder("System can't find organization with hostname \"").append(hostName).append("\" defined!");
			req.setAttribute("errorInfo", errorInfo.toString());
		}
		
    	return "forward:/proxyError";
    	
    }

    @RequestMapping(value="/page/{pagename}", method=RequestMethod.GET)
    public String generalPage(
			@PathVariable("pagename") String pagename, 

			@RequestParam(value = "categoryid", required = false) String productFolderUuid, // for product list
			@RequestParam(value = "pageidx", required = false) Integer pageIndex, // for display category/product list based on pageIndex (page#)
			@RequestParam(value = "entityid", required = false) String entityUuid,
//			@RequestParam(value = "hostname", required = false) String hostName,
			
			HttpServletRequest req,
    		ModelMap model){
    	
//    	String hostName = req.getHeader("host");
//    	model.addAttribute("hostname", hostName);
    	// check if hostname is not in ApplicationConfiguration.getBizislifeRelatedHostName()
//		String[] bizHostNamesRelated = applicationConfig.getBizislifeRelatedHostName();
//		boolean isHostTheBizislife = WebUtil.isContainDomain(bizHostNamesRelated, hostName);
//		if(!isHostTheBizislife){
//		}else{ // set extra info to request for future process
//			StringBuilder errorInfo = new StringBuilder("The current URI is /page/")
//				.append(pagename).append(", which is not allowed to access by domain \"bizislife.com\"");
//			req.setAttribute("errorInfo", errorInfo.toString());
//		}
    	
    	String hostName = (String)req.getAttribute("hostname");
		// find orgUuid by hostname
		Organization org = accountService.getOrgByHostName(hostName);
		if(org!=null){
			PageDetail pageDetail = siteDesignService.getOrgPageByTypeAndUrl(org.getId(), PageDetail.Type.Desktop, pagename);
			if(pageDetail!=null){
				// /getPage/org/{orgid}/pageurl/{url}
				StringBuilder pageUri = new StringBuilder("/getPage/org/").append(org.getOrguuid()).append("/pageurl/").append(pagename);
//				pageUri.append("?categoryid=").append(productFolderUuid!=null?productFolderUuid:"");
//				pageUri.append("&pageidx=").append(pageIndex!=null?pageIndex:"");
//				pageUri.append("&entityid=").append(entityUuid!=null?entityUuid:"");
				
				return "forward:"+pageUri.toString();
//				return "redirect:http://"+applicationConfig.getHostName()+pageUri.toString();
			}else{
	    		StringBuilder errorInfo = new StringBuilder("System can't get page by pagename: ").append(pagename).append(".");
	    		req.setAttribute("errorInfo", errorInfo.toString());
			}
			
		}else{
			StringBuilder errorInfo = new StringBuilder("System can't find organization with hostname \"").append(hostName).append("\" defined!");
			req.setAttribute("errorInfo", errorInfo.toString());
		}
		
    	return "forward:/proxyError";
    }
    
    

}
