package com.bizislife.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FileExceedsFileSizeLimitHandler 
	implements org.springframework.web.servlet.HandlerExceptionResolver
{
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ModelAndView mv = new ModelAndView("exceptionFragment");
		
		System.out.println(" -- intoResolve Ex --");		
		if ( ex instanceof MaxUploadSizeExceededException ) {
			System.out.println(" -- File Size Exceeds --");
			
			mv.addObject("ex", ex.toString());
		}else {
			mv.addObject("ex", ex.toString());
		}
		return mv;
	}

}