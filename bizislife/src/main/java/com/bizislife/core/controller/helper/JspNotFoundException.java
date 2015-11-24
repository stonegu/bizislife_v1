package com.bizislife.core.controller.helper;

import java.io.IOException;

import com.bizislife.core.controller.component.ApiResponse;

public class JspNotFoundException extends IOException{

	private static final long serialVersionUID = 8048922047523149051L;
	
	private Exception exception;
	
	public JspNotFoundException(String msg){
		super(msg);
	}
	
	public JspNotFoundException(Exception e){
		//e.printStackTrace();
		exception = e;
	}
	
	public ApiResponse jspNotFoundMsg(){
		ApiResponse apires = new ApiResponse();
		
		apires.setSuccess(false);
		
		apires.setResponse1("test...");
		
		return apires;
		
	}

}
