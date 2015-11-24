package com.bizislife.core.controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.controller.helper.SwallowingJspRenderer;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.service.SiteDesignService;
import com.bizislife.core.siteDesign.module.Module;

@Controller
public class OrgPageController {
	private static final Logger logger = LoggerFactory.getLogger(OrgPageController.class);

    @Autowired
    private SiteDesignService siteDesignService;
    
	@Autowired
	private SwallowingJspRenderer jspRenderer;

    @RequestMapping(value="/getDocumentation")
    public ModelAndView getDocumentation(
    		@RequestParam(value="targetId", required=false) String targetid
    		){
    	ModelAndView mv = new ModelAndView("doc/"+targetid);
    	
    	return mv;
    	
    }
    	

	
	

    
	

}
