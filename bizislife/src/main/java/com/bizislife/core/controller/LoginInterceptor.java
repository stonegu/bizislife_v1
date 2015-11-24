package com.bizislife.core.controller;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.service.AccountService;
import com.bizislife.util.SpringAnnotationUtil;
import com.bizislife.util.WebUtil;
import com.bizislife.util.annotation.PublicPage;
import com.bizislife.util.definition.AttributeList;

public class LoginInterceptor extends HandlerInterceptorAdapter{

	@Autowired
	AccountService accountService;
	
	@Autowired
	ApplicationConfiguration applicationConfig;
	
	protected String loginURI;

	public void setLoginURI (String loginURI) {
		this.loginURI = loginURI;
	}

	protected List<String> outsidePages;

	public void setOutsidePages (List<String> outsidePages) {
		this.outsidePages = outsidePages;
	}

//	public static final String ATTR_SESSION_BACK_URI = "com.study4fun.backURI";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// if its a resource, skip the login handler
		if (handler instanceof org.springframework.web.servlet.resource.ResourceHttpRequestHandler) return true;

		// set the cache control for the content that handlers return
		response.addHeader("Cache-Control", "no-cache, no-store");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");

		String uri = request.getRequestURI();
		if (request.getQueryString()!=null && !request.getQueryString().isEmpty())
			uri+="?"+request.getQueryString();
		
		// **** check the host information in request:
		String hostName = request.getHeader("host");
		request.setAttribute("hostname", hostName);
		// is host the "bizislife"?
		
		String[] bizHostNamesRelated = applicationConfig.getBizislifeRelatedHostName();
		boolean isHostTheBizislife = WebUtil.isContainDomain(bizHostNamesRelated, hostName);
		// isHostTheBizislife == true -> continue
		// isHostTheBizislife == false -> only some special uri can reach.
		if(!isHostTheBizislife){
			/* 1. "www.abc.com" or "www.abc.com/index" -> ProxyController.indexPage
			 * 2. "www.abc.com/page/home -> ProxyController.generalPage
			 * 3. others -> ProxyController.proxyError
			 */ 
			// all domain except bizislife & localhost can reach /index
			if(request.getRequestURI().equals("/index")) return true;
			// all domain except bizislife & localhost can reach /page/xxx
			if(request.getRequestURI().startsWith("/page/")) return true;
			// all domain except bizislife & localhost can reach /proxyError
			if(request.getRequestURI().startsWith("/proxyError")) return true;
			// all domain can reach /getPage/org/
			if(request.getRequestURI().startsWith("/getPage/org/")) return true;
			
//			if(request.getRequestURI().startsWith("/getContainerModuleContent")) return true;
			
			if(request.getRequestURI().startsWith("/getphoto")) return true;
			if(request.getRequestURI().startsWith("/getTxt")) return true;
			if(request.getRequestURI().startsWith("/getPdfInText")) return true;
			// ...
//			if(request.getRequestURI().startsWith("/getContainerModuleContent")) return true;
			// for "/" : all domain except bizislife & localhost will goto "/index"
			if(request.getRequestURI().equals("/")){
				response.sendRedirect("/index");
				return false;
			}
			// for "/?xxx" : all domain except bizislife & localhost will goto "/index"
			if(request.getRequestURI().startsWith("/?")){
				response.sendRedirect("/index");
				return false;
			}
			// others -> ProxyController.proxyError
			response.sendRedirect("/proxyError");
			return false;
						
		}else{ // disable "/index" & "/page/xxx" & "/proxyError" for bizislife & localhost
			if(request.getRequestURI().equals("/index")){
				response.sendRedirect("/");
				return false;
			}
//			if(request.getRequestURI().startsWith("/page/")){
//				response.sendRedirect("/");
//				return false;
//			}
//			if(request.getRequestURI().startsWith("/proxyError")){
//				response.sendRedirect("/");
//				return false;
//			}
			
		}
		
		// **** end 

		// check for controllers marked with PublicPage annotation
		if (hasPublicPageAnnotation(request, handler)) return true;

		// check if its one the pages defined in the xml file
		if (outsidePages!=null) {
			for (String outside : outsidePages) {
				if (uri.startsWith(outside)) return true;
			}
		}

		if (accountService.getCurrentAccount()==null) {
			if (request.getRequestURI().startsWith(loginURI)) return true; // we're already on login

			// save the back uri
			request.getSession().setAttribute(AttributeList.SessionAttribute.back_url.name(), uri);
			response.sendRedirect(loginURI);
			return false;
		}
		return true;
	}
	
	static boolean hasPublicPageAnnotation (HttpServletRequest request, Object handler) {
		if (handler instanceof org.springframework.web.method.HandlerMethod) {
			handler = ((org.springframework.web.method.HandlerMethod)handler).getBean();
		}

		Class<?> handlerClass = handler.getClass();

		// see if the annotation has been declared on the handler (controller) class
		boolean controllerIsPublicAnnotation = SpringAnnotationUtil.isAnnotationInControllerClass(handlerClass, PublicPage.class);
		return controllerIsPublicAnnotation;

//		Method m = SpringAnnotationUtil.findReqestMappingMethodInController(handlerClass, request.getMethod(), request.getRequestURI());
//		if (m==null) return false;
//		return m.isAnnotationPresent(PublicPage.class);
	}
	
}
