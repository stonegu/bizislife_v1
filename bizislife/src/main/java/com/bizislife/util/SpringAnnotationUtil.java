package com.bizislife.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumSet;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class SpringAnnotationUtil {

	public static <E extends Annotation> boolean isAnnotationInControllerClass (Class<?> handlerClass, Class<E> annotationClass) {
		while (handlerClass!=null) {
//			E annotation = handlerClass.getAnnotation(annotationClass);
			boolean hasAnnotation = handlerClass.isAnnotationPresent(annotationClass);
			if (hasAnnotation) return true;
			handlerClass = handlerClass.getSuperclass();
		}
		return false;
	}

	/**
	 * Note: this method is not work when @PathVariable used.
	 * 
	 * Finds the method marked with the @RequestMapping specified by the given requestMethod and requestURI. 
	 * This method will also search through superclasses of the handlerClass, which handles the AOP proxy wrapping
	 * which do not propagate annotations.
	 *
	 * @param handlerClass		controller class to inspect
	 * @param requestMethod		request method to look for
	 * @param requestURI		request uri to look for
	 * @return Method marked with @RequestMapping with the specified parameters, or null if none of the methods match
	 */
	@Deprecated
	public static Method findReqestMappingMethodInController (Class<?> handlerClass, String requestMethod, String requestURI) {
		// fix up requestURI format
		if (!requestURI.startsWith("/")) requestURI = "/"+requestURI;

		// find if there's a method in the handler that handles this request
		// if so, see if the method is marked with JoinConversation annotation

		while (handlerClass!=null) {
			for (Method m : handlerClass.getMethods()) {
	
				// check only methods marked with RequestMapping
				RequestMapping reqMapping = m.getAnnotation(RequestMapping.class);
				if (reqMapping==null) continue;
	
				// check the request method of the RequestMapping
				EnumSet<RequestMethod> reqMappings = EnumSet.noneOf(RequestMethod.class);
				for (RequestMethod r : reqMapping.method()) reqMappings.add(r);
				if (reqMappings.isEmpty()) {
					reqMappings.add(RequestMethod.GET);
					reqMappings.add(RequestMethod.POST);
				}
	
				boolean foundit = false;
				if (requestMethod.equalsIgnoreCase("GET")) foundit = reqMappings.contains(RequestMethod.GET);
				else if (requestMethod.equalsIgnoreCase("POST")) foundit = reqMappings.contains(RequestMethod.POST);
				else if (requestMethod.equalsIgnoreCase("PUT")) foundit = reqMappings.contains(RequestMethod.PUT);
				else if (requestMethod.equalsIgnoreCase("HEAD")) foundit = reqMappings.contains(RequestMethod.HEAD);
				else if (requestMethod.equalsIgnoreCase("DELETE")) foundit = reqMappings.contains(RequestMethod.DELETE);
				else if (requestMethod.equalsIgnoreCase("OPTIONS")) foundit = reqMappings.contains(RequestMethod.OPTIONS);
				else if (requestMethod.equalsIgnoreCase("TRACE")) foundit = reqMappings.contains(RequestMethod.TRACE);
				if (!foundit) continue;
	
				// check the URI of the RequestMapping
				boolean founduri = false;
				for (String uri : reqMapping.value()) {
					String mappedURI = uri;
					if (!mappedURI.startsWith("/")) mappedURI = "/"+mappedURI;
					if (mappedURI.equalsIgnoreCase(requestURI)) { founduri = true; break; }
				}
	
				if (!founduri) continue;
	
				return m;
			} // for methods in class

			// also check superclasses
			handlerClass = handlerClass.getSuperclass();
		} 
		return null;
	} 

}
