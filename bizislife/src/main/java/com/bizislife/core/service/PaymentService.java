package com.bizislife.core.service;

import java.util.Date;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;

public interface PaymentService {
	
	public int countCharsUsage(int usedChars, long maxCharsInPlan);
	
	public int countInstanceViewCssUsage(InstanceView view);
	
	public int countInstanceViewJspUsage(InstanceView view);
	
	public int countModuleDetailCssUsage(ModuleDetail detail);

	public int countModuleDetailJspUsage(ModuleDetail detail);
	
	
	/**
	 * 1. find current org paymentPlan used<br/>
	 * 2. find moduleDetail's xml string's length.<br/>
	 * 3. find % used<br/>
	 * 
	 * @param detail
	 * @return % used (100 multipled already)
	 */
	public int countModuleDetailUsage(ModuleDetail detail);
	
	public int countModuleInstanceUsage(ModuleInstanceInterface instance);
	
	public PaymentPlan getDefaultPaymentPlan();
	
	public PaymentPlan getFreePaymentPlan();
	
	public PaymentHistory getPaymentHistory(Long orgId, Date date);
	
	public PaymentPlan getPaymentPlan(Long planId);
	
	public PaymentPlan getPaymentPlanAtPointOfDate(Long orgId, Date pointOfDate);

	public ApiResponse savePaymentPlan(PaymentPlan plan);
	
}
