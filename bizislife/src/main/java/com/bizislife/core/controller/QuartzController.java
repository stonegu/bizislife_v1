package com.bizislife.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bizislife.core.service.AccountService;

@Controller
public class QuartzController {

	private static final Logger logger = LoggerFactory.getLogger(QuartzController.class);
	
    @Autowired
    private AccountService accountService;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
	
	@RequestMapping(value="/schedule", method=RequestMethod.GET)
	public String schedule(
			HttpServletResponse response, HttpServletRequest request,
			ModelMap model
			){
		
		if(schedulerFactoryBean!=null){
			Scheduler sd = schedulerFactoryBean.getScheduler();
			List<Trigger> triggers = new ArrayList<Trigger>();
			Map<String, String> triggerStatusMap = new HashMap<String, String>();
			if(sd!=null){
				String[] triggerGroupNames = null;
				String[] triggerNames = null;
				try{
					triggerGroupNames = sd.getTriggerGroupNames();
					if(triggerGroupNames!=null){
						for(String gn : triggerGroupNames){
							triggerNames = sd.getTriggerNames(gn);
							if(triggerNames!=null){
								int triggerStatus = Trigger.STATE_NONE;
								for(String tn : triggerNames){
									//Trigger tg = sd.getTrigger(tn, gn);
									triggerStatus = sd.getTriggerState(tn, gn);
									switch (triggerStatus) {
									case 0:
										triggerStatusMap.put(tn, "Normal status");
										break;
									case 1:
										triggerStatusMap.put(tn, "Paused status");
										break;
									case 2:
										triggerStatusMap.put(tn, "Completed status");
										break;
									case 3:
										triggerStatusMap.put(tn, "Error status");
										break;
									case 4:
										triggerStatusMap.put(tn, "Blocked status");
										break;

									default:
										triggerStatusMap.put(tn, "No status");
										break;
									}
									triggers.add(sd.getTrigger(tn, gn));
								}
							}
							
						}
					}
					
				}catch (SchedulerException e) {
					// TODO: handle exception
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			model.put("triggers",triggers);
			model.put("triggerStatus", triggerStatusMap);
		}
		
		return "schedule";
	}
	
	@RequestMapping(value="/quartzController", method=RequestMethod.POST)
	public String quartzController(

			@RequestParam(value = "triggerGroupName", required = false) String triggerGroupName,
			@RequestParam(value = "triggerName", required = false) String triggerName,
			@RequestParam(value = "jobName", required = false) String jobName,
			@RequestParam(value = "cronExp", required = false) String cronExp,
			@RequestParam(value = "status", required = false) String status,
			
			HttpServletResponse response, HttpServletRequest request,
			ModelMap model
			){
		
		if(StringUtils.isNotBlank(status) 
				&& (StringUtils.isNotBlank(triggerName) || StringUtils.isNotBlank(jobName) || StringUtils.isNotBlank(cronExp)) 
				&& StringUtils.isNotBlank(triggerGroupName)){
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if(scheduler!=null){
				try{
					if(status.equals("start")){
						scheduler.resumeTrigger(triggerName, triggerGroupName);
					}else if(status.equals("pause")){
						scheduler.pauseTrigger(triggerName, triggerGroupName);
					}else if(status.equals("deleteJob")){
						scheduler.deleteJob(jobName, triggerGroupName);
					}else if(status.equals("changeExp")){
						
						Trigger quartzTrigger = scheduler.getTrigger(triggerName, triggerGroupName);
						if(quartzTrigger.getClass()==CronTrigger.class){
							((CronTrigger)quartzTrigger).setCronExpression(cronExp);
							
						}
						scheduler.rescheduleJob(triggerName, triggerGroupName, quartzTrigger);
					}
					
				}catch (SchedulerException e) {
					// TODO: handle exception
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
		return "redirect:schedule";
	}

}
