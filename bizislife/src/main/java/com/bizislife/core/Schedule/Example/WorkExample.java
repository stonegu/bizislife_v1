package com.bizislife.core.Schedule.Example;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("workExample")
public class WorkExample implements Serializable, QuartzTask{
	
	private static final long serialVersionUID = -9014599884955096250L;

	private static final Logger logger = LoggerFactory.getLogger(WorkExample.class);

	 @Override
	 public void work(Object o) {
		 
	  String threadName = Thread.currentThread().getName();
	  //logger.debug("   " + threadName + " has began working.");
	  System.out.println("   " + threadName + " has began working.");
	        try {
//	         logger.debug("working...");
	        	System.out.println("working ...");
	            Thread.sleep(5000); // simulates work
	        }
	        catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	        
//	        logger.debug("   " + threadName + " has completed work.");
	        System.out.println("   " + threadName + " has completed work.");
	 }
}
