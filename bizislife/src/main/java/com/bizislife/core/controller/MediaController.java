package com.bizislife.core.controller;

import java.util.Collections;
import java.util.List;

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

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.MediaService;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.service.TreeService;
import com.bizislife.core.view.dto.AccountDto;

@Controller
public class MediaController {
	private static final Logger logger = LoggerFactory.getLogger(MediaController.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private MediaService mediaService;

    @Autowired
    private TreeService treeService;
    
    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value="/medias", method=RequestMethod.GET)
    public String newsPage(
			@RequestParam(value = "org", required = true) String orguuid, 
            ModelMap model) {
    	
    	model.put("currentPageId", new StringBuilder("medias_").append(orguuid).toString());
    	model.put("orgUuid", orguuid);
    	
        return "medias";
    }
    
    @RequestMapping(value="/mediaNodeCreate", method=RequestMethod.GET)
    public @ResponseBody ApiResponse mediaNodeCreate(
    		@RequestParam(value = "newNodetype", required = true) String newNodetype,
    		@RequestParam(value = "parentNodeUuid", required = true) String parentNodeUuid,
    		@RequestParam(value = "nodeName", required = true) String newNodeName,
    		ModelMap model){
    	ApiResponse res = null;
    	if(StringUtils.isNotBlank(newNodetype) && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(newNodeName)){
    		MediaDetail.MediaType nodetype = MediaType.fromCode(newNodetype);
    		if(nodetype!=null){
    			
				AccountDto loginAccount = accountService.getCurrentAccount();
				boolean isPermissionModifyAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, parentNodeUuid);

				if(isPermissionModifyAllowed){
	        		String newNodeUuid = mediaService.newMediaNode(nodetype, parentNodeUuid, newNodeName, null);
	        		if(StringUtils.isNotBlank(newNodeUuid)){
	        			res = new ApiResponse();
	        			res.setSuccess(true);
	        			res.setResponse1(newNodeUuid);
	        		}
				}else{
					res = new ApiResponse();
					res.setSuccess(false);
					res.setResponse1("User "+loginAccount.getFirstname()+" doesn't have create permission for node: "+parentNodeUuid);					
				}
				
    		}
    	}else{
    		res = new ApiResponse();
    		res.setSuccess(false);
    	}
    	
    	return res;
    }

    @RequestMapping(value="/mediaNodeDelete", method=RequestMethod.GET)
    public @ResponseBody ApiResponse mediaNodeDelete(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.delNodeDetail(MediaDetail.class, nodeUuid);
    	
    	return apires;
    }

    @RequestMapping(value="/moveMediaNode", method=RequestMethod.POST)
    public @ResponseBody ApiResponse moveMediaNode(
    		@RequestParam(value = "nodeId", required = false) String nodeUuid,
    		@RequestParam(value = "targetUuid", required = false) String targetUuid,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = treeService.moveTreeNode_v2(MediaDetail.class, nodeUuid, targetUuid);
    	
    	return apires;
    }

    @RequestMapping(value="/updateMediaDetailValue")
    public @ResponseBody ApiResponse updateMediaDetailValue(
    		@RequestParam(value = "mediaId", required = false) String mediaUuid,
    		@RequestParam(value = "updateValue", required = false) String updateValue,
    		@RequestParam(value = "valueName", required = false) String valueName,
    		
    		ModelMap model){
    	ApiResponse apires = new ApiResponse();
    	apires.setSuccess(false);
    	
    	apires = mediaService.updateMediaDetailByFieldnameValue(mediaUuid, valueName, updateValue);
    	
    	return apires;
    }
    
    
	@RequestMapping(value="/getMediaListForFold", method=RequestMethod.GET)
	public ModelAndView getMediaListForFold(
			
			@RequestParam(value = "mediaType", required = false) String mediaType,
			@RequestParam(value = "folderId", required = false) String folderId,
			
			HttpServletResponse response, HttpServletRequest request) {
		
		AccountDto loginAccount = accountService.getCurrentAccount();
		
		ModelAndView mv = new ModelAndView("mediaListForFolder");
		
		MediaType mtype = MediaType.fromCode(mediaType);
		if(loginAccount!=null && StringUtils.isNotBlank(folderId)){
			List<MediaDetail> medias = mediaService.findMediasInFolder(loginAccount.getAccountuuid(), mtype, folderId);
			if(medias!=null && medias.size()>0){
				Collections.sort(medias, MediaDetail.prettyNameComparator);
			}
			
			mv.addObject("folderId", folderId);
			mv.addObject("medias", medias);
		}
		
		return mv;
	}
		


    
}
