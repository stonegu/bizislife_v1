package com.bizislife.core.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.dao.AccountDao;
import com.bizislife.core.hibernate.dao.PaymentDao;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.InstanceView;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;

@Service
public class PaymentServiceImpl implements PaymentService{
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Autowired
	AccountDao accountDao;

	@Autowired
	PaymentDao paymentDao;

	@Override
	@Transactional
	public ApiResponse savePaymentPlan(PaymentPlan plan) {
		return paymentDao.savePaymentPlan(plan);
	}

	@Override
	@Transactional(readOnly=true)
	public PaymentHistory getPaymentHistory(Long orgId, Date date) {
		if(orgId!=null && date!=null){
			
			List<PaymentHistory> orgPaymentHistories = paymentDao.findOrgPaymentHistories(orgId);
			if(orgPaymentHistories!=null && orgPaymentHistories.size()>0){
				for(PaymentHistory his : orgPaymentHistories){
					if((his.getFromdate().before(date) && his.getTodate().after(date)) // between 
							|| his.getFromdate().equals(date) // exact start time
							|| his.getTodate().equals(date)) // exact end time
					{ 
						return his;
					}
				}
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public PaymentPlan getDefaultPaymentPlan() {
		return paymentDao.getDefaultPaymentPlan();
	}

	@Override
	@Transactional(readOnly=true)
	public PaymentPlan getFreePaymentPlan() {
		List<PaymentPlan> freePlans = paymentDao.findFreePaymentPlans();
		if(freePlans!=null && freePlans.size()>0) return freePlans.get(0);
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public PaymentPlan getPaymentPlan(Long planId) {
		return paymentDao.getPaymentPlan(planId);
	}

	@Override
	public PaymentPlan getPaymentPlanAtPointOfDate(Long orgId, Date pointOfDate) {
		PaymentPlan paymentPlan = null;
		PaymentHistory paymentHistory = getPaymentHistory(orgId, pointOfDate);
		if(paymentHistory!=null){
			paymentPlan = getPaymentPlan(paymentHistory.getPlanid());
		}else{
			paymentPlan = getFreePaymentPlan();
		}
		return paymentPlan;
	}
	
	@Override
	@Transactional(readOnly=true)
	public int countModuleDetailUsage(ModuleDetail detail) {
		if(detail!=null){
			PaymentPlan orgPaymentPlan = getPaymentPlanAtPointOfDate(detail.getOrganization_id(), new Date());
			if(orgPaymentPlan!=null){
				long maxcharspermoduledetail = orgPaymentPlan.getMaxcharspermoduledetail();
				int usedCharsInModuleDetail = detail.getXml()!=null?detail.getXml().length():0;
				return countCharsUsage(usedCharsInModuleDetail, maxcharspermoduledetail);
			}
		}
		return 0;
	}
	
	@Override
	public int countCharsUsage(int usedChars, long maxCharsInPlan){
		return (int)(((double)usedChars)/((double)maxCharsInPlan)*100);
	}

	@Override
	@Transactional(readOnly=true)
	public int countModuleDetailCssUsage(ModuleDetail detail) {
		if(detail!=null){
			PaymentPlan orgPaymentPlan = getPaymentPlanAtPointOfDate(detail.getOrganization_id(), new Date());
			if(orgPaymentPlan!=null){
				long maxCssCharsPerModuleDetail = orgPaymentPlan.getMaxcharspercss();
				int usedCssCharsInModuleDetail = detail.getCss()!=null?detail.getCss().length():0;
				return countCharsUsage(usedCssCharsInModuleDetail, maxCssCharsPerModuleDetail);
			}
		}
		return 0;
	}

	@Override
	@Transactional(readOnly=true)
	public int countModuleDetailJspUsage(ModuleDetail detail) {
		if(detail!=null){
			PaymentPlan orgPaymentPlan = getPaymentPlanAtPointOfDate(detail.getOrganization_id(), new Date());
			if(orgPaymentPlan!=null){
				long maxJspCharsPerModuleDetail = orgPaymentPlan.getMaxcharsperjsp();
				int usedJspCharsInModuleDetail = detail.getJsp()!=null?detail.getJsp().length():0;
				return countCharsUsage(usedJspCharsInModuleDetail, maxJspCharsPerModuleDetail);
			}
		}
		return 0;
	}

	@Override
	@Transactional(readOnly=true)
	public int countInstanceViewCssUsage(InstanceView view) {
		if(view!=null){
			PaymentPlan paymentplan = getPaymentPlanAtPointOfDate(view.getOrgid(), new Date());
			if(paymentplan!=null){
				long maxCssCharsPerView = paymentplan.getMaxcharspercss();
				int usedCssCharsInView = view.getCss()!=null?view.getCss().length():0;
				return countCharsUsage(usedCssCharsInView, maxCssCharsPerView);
			}
		}
		return 0;
	}

	@Override
	@Transactional(readOnly=true)
	public int countInstanceViewJspUsage(InstanceView view) {
		if(view!=null){
			PaymentPlan paymentplan = getPaymentPlanAtPointOfDate(view.getOrgid(), new Date());
			if(paymentplan!=null){
				long maxJspCharsPerView = paymentplan.getMaxcharsperjsp();
				int usedJspCharsInView = view.getJsp()!=null?view.getJsp().length():0;
				return countCharsUsage(usedJspCharsInView, maxJspCharsPerView);
			}
		}
		return 0;
	}

	@Override
	@Transactional(readOnly=true)
	public int countModuleInstanceUsage(ModuleInstanceInterface instance) {
		if(instance!=null){
			Long orgId = null;
			String instanceXml = null;
			if(instance.getClass().equals(ModuleInstance.class)){
				orgId = ((ModuleInstance)instance).getOrgid();
				instanceXml = ((ModuleInstance)instance).getInstance();
			}else if(instance.getClass().equals(EntityDetail.class)){
				orgId = ((EntityDetail)instance).getOrganization_id();
				instanceXml = ((EntityDetail)instance).getDetail();
			}
			if(orgId!=null){
				PaymentPlan orgPaymentPlan = getPaymentPlanAtPointOfDate(orgId, new Date());
				if(orgPaymentPlan!=null){
					long maxCharsInstanceCanHave = orgPaymentPlan.getMaxcharsperinstance();
					int usedCharsInInstance = instanceXml!=null?instanceXml.length():0;
					return countCharsUsage(usedCharsInInstance, maxCharsInstanceCanHave);
				}
			}
			
		}
		return 0;
	}


}
