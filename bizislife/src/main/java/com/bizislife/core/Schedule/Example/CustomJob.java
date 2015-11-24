package com.bizislife.core.Schedule.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.bizislife.core.hibernate.dao.OrganizationDao;

public class CustomJob extends QuartzJobBean implements StatefulJob {
	 
	private static final Logger logger = LoggerFactory.getLogger(CustomJob.class);
	private static final String APPLICATION_CONTEXT_KEY = "applicationContext"; 
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private QuartzTask task;
	  
	@Override
	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
	 
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();

		// get spring's bean
		SchedulerContext schedulerContext = null;  
		try {  
			schedulerContext = ctx.getScheduler().getContext();  
		} catch(SchedulerException e) {  
			logger.error("Failure accessing schedulerContext");  
            throw new JobExecutionException("Failure accessing schedulerContext", e);  
		}  
		ApplicationContext appContext = (ApplicationContext)schedulerContext.get(APPLICATION_CONTEXT_KEY);
		OrganizationDao organizationDao = (OrganizationDao)appContext.getBean("organizationDao");
//		if(organizationDao!=null) System.err.println("current org is : " + organizationDao.getOrganizationById(1l));
   
		try {
			Date lastDateRun  = ctx.getPreviousFireTime();
			if (lastDateRun != null) {
				logger.info("----Last date run: " + sdf.format(lastDateRun));
				int refireCount = ctx.getRefireCount();
				if (refireCount > 0) {
					logger.info("----Total attempts: " + refireCount);
				}
			}
			else {
				logger.info("----Job is run for the first time");
			}
			logger.info("----Delegating work to worker");
			// todo: will use QuartzDao for all quartz's db access
			task.work(organizationDao);
			    
			String nextDateRun = sdf.format(ctx.getNextFireTime());
			logger.info("----Next date run: " + nextDateRun);
		 
		}catch (Exception e) {
			logger.error("----Unexpected exception" + e.toString());
			throw new JobExecutionException("Unexpected exception", e, true);
		}
	}
 
	public void setWorker(QuartzTask task) {
		this.task = task;
	}
 
 	 
}