package com.bizislife.util.AspectJ;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.util.annotation.JsonIgnoreNullField;

@Aspect
public class JsonModifyAspJ {
//	@After(value="@annotation(jsonIgnoreNullField)")
//	public void jsonIgnoreNullFieldAfter(JoinPoint joinPoint, JsonIgnoreNullField jsonIgnoreNullField) {
//		System.err.println("=================");
//		
//	}
	
	@Around(value="@annotation(jsonIgnoreNullField)")
	public void jsonIgnoreNullFieldAround(ProceedingJoinPoint joinPoint, JsonIgnoreNullField jsonIgnoreNullField) throws Throwable {
	 
		System.out.println("logAround() is running!");
		System.out.println("hijacked method : " + joinPoint.getSignature().getName());
		System.out.println("hijacked arguments : " + Arrays.toString(joinPoint.getArgs()));
		 
		System.out.println("Around before is running!");
		joinPoint.proceed(); //continue on the intercepted method
		System.out.println("Around after is running!");
		 
		System.out.println("******");
	 
	}	
	
	
	
	
	

}
