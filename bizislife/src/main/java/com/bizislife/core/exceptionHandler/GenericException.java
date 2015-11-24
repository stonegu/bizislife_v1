package com.bizislife.core.exceptionHandler;

//http://www.mkyong.com/spring-mvc/spring-mvc-exception-handling-example/
public class GenericException extends RuntimeException{
	 
	private String customMsg;
 
	//getter and setter methods
 
	public GenericException(String customMsg) {
		this.customMsg = customMsg;
	}
 
}