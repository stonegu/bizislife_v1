package com.bizislife.core.hibernate.dao;

import java.util.List;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;

public interface PaymentDao {
	
	public List<PaymentPlan> findFreePaymentPlans();
	
	public List<PaymentHistory> findOrgPaymentHistories(Long orgId);
	
	public PaymentPlan getDefaultPaymentPlan();
	
	public PaymentPlan getPaymentPlan(Long planId);
	
	public ApiResponse savePaymentPlan(PaymentPlan plan);
}
